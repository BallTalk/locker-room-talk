package com.locker.auth.application;

import com.locker.user.domain.Provider;
import com.locker.user.domain.Team;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = delegate.loadUser(req);

        String registrationId = req.getClientRegistration().getRegistrationId();
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        String userIdAttr = getUserIdAttributeName(provider);
        String oauthId    = oauth2User.getAttribute(userIdAttr);
        String nickname;
        String profileImageUrl = null;
        Team team = Team.NOT_SET;

        if (provider == Provider.KAKAO) {
            @SuppressWarnings("unchecked")
            Map<String,Object> kakaoAccount =
                    (Map<String,Object>) oauth2User.getAttributes().get("kakao_account");
            @SuppressWarnings("unchecked")
            Map<String,Object> profile =
                    (Map<String,Object>) kakaoAccount.get("profile");
            nickname = (String) profile.getOrDefault("nickname", "unknown");
            profileImageUrl = (String) profile.get("profile_image_url");
        }

        else { // GOOGLE
            String email = oauth2User.getAttribute("email");
            nickname = (email != null) ? email.split("@")[0] : "unknown";
        }

        Optional<User> opt = userRepository.findByProviderAndProviderId(provider, oauthId);
        if (opt.isEmpty()) {
            User newUser = User.createOAuthUser(provider, oauthId, nickname, team, profileImageUrl);
            userRepository.save(newUser);
        }

        Set<GrantedAuthority> auths = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new DefaultOAuth2User(auths, oauth2User.getAttributes(), userIdAttr);
    }

    private String getUserIdAttributeName(Provider provider) {
        return switch (provider) {
            case LOCAL -> null;
            case GOOGLE -> "sub";
            case KAKAO  -> "id";
        };
    }


}
