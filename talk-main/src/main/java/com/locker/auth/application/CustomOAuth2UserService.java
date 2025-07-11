package com.locker.auth.application;

import com.locker.user.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate;
    private final UserRepository             userRepository;
    private final PasswordEncoder            passwordEncoder;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest req)
            throws OAuth2AuthenticationException {

        OAuth2User oauth2User = delegate.loadUser(req);

        String registrationId = req.getClientRegistration().getRegistrationId();
        Provider provider      = Provider.valueOf(registrationId.toUpperCase());

        // get the attribute name (configured in application.yml as user-name-attribute)
        String userIdAttr = req.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        String oauthId = Objects.requireNonNull(oauth2User.getAttribute(userIdAttr)).toString();

        String nickname;
        String profileImageUrl = null;
        Team   team            = Team.NOT_SET;

        if (provider == Provider.KAKAO) {
            @SuppressWarnings("unchecked")
            Map<String,Object> kakaoAccount =
                    (Map<String,Object>) oauth2User.getAttributes().get("kakao_account");
            @SuppressWarnings("unchecked")
            Map<String,Object> profile =
                    (Map<String,Object>) kakaoAccount.get("profile");
            nickname        = (String) profile.getOrDefault("nickname", "unknown");
            profileImageUrl = (String) profile.get("profile_image_url");
        } else { // GOOGLE
            String email = oauth2User.getAttribute("email");
            nickname = (email != null) ? email.split("@")[0] : "unknown";
        }

        final String profileImageUrlToUse =
                (profileImageUrl != null && !profileImageUrl.isBlank())
                        ? profileImageUrl
                        : "default_profile_image_url";

        // find or create the User
        userRepository
                .findByProviderAndProviderId(provider, oauthId)
                .orElseGet(() -> {
                    String prefix    = provider.name().toLowerCase();
                    int    maxSuffix = 20 - prefix.length();
                    String suffix    = UUID.randomUUID()
                            .toString().replace("-", "")
                            .substring(0, Math.min(maxSuffix, 12));

                    String loginId        = prefix + suffix;
                    String hashedPassword = passwordEncoder.encode(suffix);

                    User newUser = User.createOAuthUser(
                            provider,
                            oauthId,
                            loginId,
                            hashedPassword,
                            nickname,
                            team,
                            profileImageUrlToUse
                    );

                    userRepository.save(newUser);
                    return newUser;
                });

        Set<GrantedAuthority> auths = Set.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new DefaultOAuth2User(auths, oauth2User.getAttributes(), userIdAttr);
    }
}
