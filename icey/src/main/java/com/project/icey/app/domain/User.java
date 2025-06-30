package com.project.icey.app.domain;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Long id;

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String email;

    @Setter
    private String userName;

    @Setter
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Enumerated(EnumType.STRING) // ENUM 사용
    @Column(nullable = false)
    private Provider provider;  // "Google", "Kakao"

    // 유저 권한 정보 반환
    public void authorizeUser() { this.roleType = RoleType.USER; }
   // 리프레쉬 토큰 반환
    public void updateRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }


    @Builder
    public User(Long id,
                String email,
                String userName,
                String refreshToken,
                RoleType roleType,
                Provider provider) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.refreshToken = refreshToken;
        this.roleType = roleType;
        this.provider = provider;
    }
}
