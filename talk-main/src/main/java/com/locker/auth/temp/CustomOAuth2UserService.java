package com.locker.auth.temp;

import com.locker.user.domain.Provider;
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

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate;
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req)
            throws OAuth2AuthenticationException {

        // 1) 구글/카카오 API 로부터 프로필 정보 가져오기
        OAuth2User oauth2User = delegate.loadUser(req);


        String registrationId = req.getClientRegistration().getRegistrationId();
        Provider provider = Provider.valueOf(registrationId.toUpperCase());
        String userIdAttr = getUserIdAttributeName(provider);
        String oauthId    = oauth2User.getAttribute(userIdAttr);
        String email      = oauth2User.getAttribute("email");
        String nickname   = email != null ? email.split("@")[0] : "unknown";

        Optional<User> opt = userRepository.findByProviderAndProviderId(provider, oauthId);
        if (opt.isEmpty()) {
            User newUser = User.createOAuthUser(provider, oauthId, nickname, "DEFAULT_TEAM");
            userRepository.save(newUser);
        }

        Set<GrantedAuthority> auths =
                Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new DefaultOAuth2User(
                auths,
                oauth2User.getAttributes(),
                userIdAttr
        );
    }

    private String getUserIdAttributeName(Provider provider) {
        return switch (provider) {
            case LOCAL -> null;
            case GOOGLE -> "sub";
            case KAKAO  -> "id";
        };
    }


}
