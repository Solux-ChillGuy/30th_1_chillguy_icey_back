package com.project.icey.oauth2;


import com.project.icey.app.domain.Provider;
import com.project.icey.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(request);
        String registrationId = request.getClientRegistration().getRegistrationId().toLowerCase();
        Map<String, Object> attributes = user.getAttributes();

        log.info("OAuth2User attributes: {}", attributes);

        String email = null;
        String name = null;
        Provider provider = null;

        if ("kakao".equals(registrationId)) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            if (kakaoAccount == null)
                throw new OAuth2AuthenticationException(new OAuth2Error("invalid_response"), "kakao_account is null");
            email = (String) kakaoAccount.get("email");
            name = (String) kakaoAccount.get("name");
            provider = Provider.KAKAO;
        } else if ("google".equals(registrationId)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            provider = Provider.GOOGLE;
        } else {
            throw new OAuth2AuthenticationException(new OAuth2Error("invalid_provider"), "Unsupported provider: " + registrationId);
        }

        if (email == null)
            throw new OAuth2AuthenticationException(new OAuth2Error("email_not_found"), "Email not found from OAuth2 provider");

        // *** 여기서 이메일은 같은데 provider가 다르면 에러 발생 ***
        final Provider finalProvider = provider;

        userRepository.findByEmail(email).ifPresent(existingUser -> {
            if (existingUser.getProvider() != finalProvider) {
                throw new OAuth2AuthenticationException(new OAuth2Error("account_conflict"),
                        "이미 다른 소셜 계정(" + existingUser.getProvider() + ")으로 가입된 이메일입니다.");
            }
        });


        // Kakao, Google 모두 공통 enrich
        Map<String, Object> enrichedAttributes = new HashMap<>(attributes);
        enrichedAttributes.put("email", email);
        enrichedAttributes.put("name", name);
        enrichedAttributes.put("provider", provider);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                enrichedAttributes,
                "email"
        );
    }
}
