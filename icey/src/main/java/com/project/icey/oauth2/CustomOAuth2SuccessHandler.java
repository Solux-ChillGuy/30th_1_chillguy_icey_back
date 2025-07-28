



package com.project.icey.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.icey.app.domain.Provider;
import com.project.icey.app.domain.RoleType;
import com.project.icey.app.domain.User;
import com.project.icey.app.repository.UserRepository;
import com.project.icey.global.dto.ApiResponseTemplete;
import com.project.icey.global.exception.ErrorCode;
import com.project.icey.global.exception.SuccessCode;
import com.project.icey.global.security.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @Value("${app.oauth2.frontend-redirect-uri}")
    private String redirectUri;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) token.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String registrationId = token.getAuthorizedClientRegistrationId();
        Provider provider;
        if ("kakao".equalsIgnoreCase(registrationId)) {
            provider = Provider.KAKAO;
        } else {
            provider = Provider.GOOGLE;
        }


        if (email == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(response.getWriter(),
                    ApiResponseTemplete.error(ErrorCode.INVALID_REQUEST, "OAuth2 provider로부터 이메일 정보를 받지 못했습니다."));
            return;
        }

        // 사용자 저장 or 업데이트
        User user = userRepository.findByEmail(email)
                .map(u -> {
                    u.setUserName(name);
                    return userRepository.save(u);
                })
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email(email)
                                .userName(name)
                                .roleType(RoleType.USER)
                                .provider(provider)
                                .build()
                ));

        // 토큰 발급 및 저장
        String accessToken = tokenService.createAccessToken(user.getEmail());
        String refreshToken = tokenService.createRefreshToken();
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // ✅ 프론트 리다이렉트 URL 조립
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();
        response.sendRedirect(targetUrl);
    }
}
