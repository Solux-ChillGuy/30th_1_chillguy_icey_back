# 🧊 ICEY - 백엔드 레포지토리

> 어색함을 녹이고, 말이 트이도록  
> 자연스러운 아이스브레이킹과 팀 커뮤니케이션의 모든 것, **ICEY**

---


## 팀원 소개

<table>
  <thead>
    <tr>
      <th>사진</th>
      <th>이름</th>
      <th>파트</th>
      <th>담당 기능</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/0395ac5d-a409-4936-b891-0f93f8cd88b6"
             width="100" height="100" style="object-fit: cover; border-radius: 50%;">
      </td>
      <td align="center"><b>홍서현</b></td>
      <td align="center">팀장 / 백엔드</td>
      <td>
        • 카카오·구글 소셜 로그인<br/>
        • 밸런스게임 (Gemini)<br/>
        • 스몰토크 (Gemini)<br/>
        • SSE 기반 실시간 알림 구현<br/>
        • 배포 및 CICD 파이프라인 구축
      </td>
    </tr>
    <tr>
      <td align="center">
        <img src= "https://github.com/user-attachments/assets/1720c576-2798-4576-a4cc-764d91bb45a1"
             width="100" height="100" style="object-fit: cover; border-radius: 50%;">
      </td>
      <td align="center"><b>권윤재</b></td>
      <td align="center">백엔드</td>
      <td>
        • 유저별 개인 명함 관리<br/>
        • 유저별 팀 명함 관리<br/>
        • 팀 내 메모 및 메모 반응하기 구현
      </td>
    </tr>
    <tr>
      <td align="center">
        <img src="https://github.com/user-attachments/assets/916745ea-d366-40af-b487-46012834e63a"
             width="100" height="100" style="object-fit: cover; border-radius: 50%;">
      </td>
      <td align="center"><b>홍상희</b></td>
      <td align="center">백엔드</td>
      <td>
        • 팀 관리(생성/만료/초대 및 가입)<br/>
        • 팀 내 약속잡기 (when2meet)<br/>
        • 쪽지 기능
      </td>
    </tr>
  </tbody>
</table>


---


## ⚙️ 기술 스택

### 🧑‍💻 언어 & 프레임워크  
![Java](https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=java&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)  

### 🔐 보안  
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white)  ![JWT](https://img.shields.io/badge/JWT-JSON%20Web%20Token-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

### 🗄️ 데이터베이스
![JPA](https://img.shields.io/badge/JPA-Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)  
![MySQL](https://img.shields.io/badge/MySQL-5.7+-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

### ☁️ 인프라 & 자동화 툴  
![GCP](https://img.shields.io/badge/Google%20Cloud-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white)  ![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)  ![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)  ![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white)



---


## ✨ 주요 기능

| 기능 구분 |
|-----------|
|소셜 로그인 | 
| SmallTalk | 
| 팀 생성 및 초대  | 
| 밸런스 게임 |
| 명함 | 
| 메모 | 
| 약속 잡기| 
| 쪽지 |
| SSE 기반 실시간 알림 | 



---


## 🌐 서비스 아키텍처

<img width="4983" height="2160" alt="서비스아키텍처" src="https://github.com/user-attachments/assets/ef638edf-761b-4c33-b70a-821a1db6a2bb" />



## 📁 프로젝트 디렉터리 구조
```
📦 
├─ .github
│  ├─ ISSUE_TEMPLATE
│  │  └─ 이슈-생성-템플릿.md
│  └─ pull_request_template.md
├─ .gitignore
├─ README.md
├─ cloudbuild.yaml
└─ icey
   ├─ Dockerfile
   ├─ build.gradle
   ├─ gradle
   │  └─ wrapper
   │     ├─ gradle-wrapper.jar
   │     └─ gradle-wrapper.properties
   ├─ gradlew
   ├─ gradlew.bat
   ├─ settings.gradle
   └─ src
      ├─ main
      │  ├─ java
      │  │  └─ com
      │  │     └─ project
      │  │        └─ icey
      │  │           ├─ IceyApplication.java
      │  │           ├─ app
      │  │           │  ├─ controller
      │  │           │  │  ├─ BalanceGameController.java
      │  │           │  │  ├─ CardController.java
      │  │           │  │  ├─ LetterController.java
      │  │           │  │  ├─ LoginController.java
      │  │           │  │  ├─ MemoController.java
      │  │           │  │  ├─ NotificationController.java
      │  │           │  │  ├─ ScheduleController.java
      │  │           │  │  ├─ SmallTalkController.java
      │  │           │  │  └─ TeamController.java
      │  │           │  ├─ domain
      │  │           │  │  ├─ AccessoryType.java
      │  │           │  │  ├─ BalanceGame.java
      │  │           │  │  ├─ BalanceGameVote.java
      │  │           │  │  ├─ CandidateDate.java
      │  │           │  │  ├─ Card.java
      │  │           │  │  ├─ Letter.java
      │  │           │  │  ├─ Memo.java
      │  │           │  │  ├─ MemoReaction.java
      │  │           │  │  ├─ MemoReactionId.java
      │  │           │  │  ├─ NotificationEntity.java
      │  │           │  │  ├─ NotificationType.java
      │  │           │  │  ├─ Provider.java
      │  │           │  │  ├─ QuestionType.java
      │  │           │  │  ├─ RoleType.java
      │  │           │  │  ├─ Schedule.java
      │  │           │  │  ├─ ScheduleTimeSlot.java
      │  │           │  │  ├─ ScheduleVote.java
      │  │           │  │  ├─ SmallTalk.java
      │  │           │  │  ├─ SmallTalkList.java
      │  │           │  │  ├─ Team.java
      │  │           │  │  ├─ User.java
      │  │           │  │  ├─ UserRole.java
      │  │           │  │  └─ UserTeamManager.java
      │  │           │  ├─ dto
      │  │           │  │  ├─ AnswerRequest.java
      │  │           │  │  ├─ BalanceGameCreateRequest.java
      │  │           │  │  ├─ BalanceGameDto.java
      │  │           │  │  ├─ BalanceGameResultDto.java
      │  │           │  │  ├─ BalanceGameVoteRequest.java
      │  │           │  │  ├─ CardRequest.java
      │  │           │  │  ├─ CardResponse.java
      │  │           │  │  ├─ ConfirmScheduleRequest.java
      │  │           │  │  ├─ CreateTeamRequest.java
      │  │           │  │  ├─ CustomUserDetails.java
      │  │           │  │  ├─ ErrorResponse.java
      │  │           │  │  ├─ GeminiRequset.java
      │  │           │  │  ├─ GeminiResponse.java
      │  │           │  │  ├─ GoogleTokenResponse.java
      │  │           │  │  ├─ InvitationResponse.java
      │  │           │  │  ├─ InvitationTeamInfoResponse.java
      │  │           │  │  ├─ KakaoLoginRequest.java
      │  │           │  │  ├─ LetterDetailResponse.java
      │  │           │  │  ├─ LetterSendRequest.java
      │  │           │  │  ├─ LetterSummaryResponse.java
      │  │           │  │  ├─ LikeUser.java
      │  │           │  │  ├─ LoginRequestDto.java
      │  │           │  │  ├─ MajorityTimeResponse.java
      │  │           │  │  ├─ MemoRequest.java
      │  │           │  │  ├─ MemoResponse.java
      │  │           │  │  ├─ Notification.java
      │  │           │  │  ├─ ScheduleCreateRequest.java
      │  │           │  │  ├─ ScheduleVoteCombinedResponse.java
      │  │           │  │  ├─ ScheduleVoteRequest.java
      │  │           │  │  ├─ ScheduleVoteResponse.java
      │  │           │  │  ├─ ScheduleVoteSummaryResponse.java
      │  │           │  │  ├─ SimpleTeamInfo.java
      │  │           │  │  ├─ SmallTalkAnswerListRequest.java
      │  │           │  │  ├─ SmallTalkCreateRequest.java
      │  │           │  │  ├─ SmallTalkDto.java
      │  │           │  │  ├─ SmallTalkEditRequest.java
      │  │           │  │  ├─ SmallTalkListDto.java
      │  │           │  │  ├─ SmallTalkListSaveRequest.java
      │  │           │  │  ├─ SmallTalkResponse.java
      │  │           │  │  ├─ SwapResponse.java
      │  │           │  │  ├─ TeamDetailResponse.java
      │  │           │  │  ├─ TeamMember.java
      │  │           │  │  ├─ TeamResponse.java
      │  │           │  │  ├─ TitleUpdateRequest.java
      │  │           │  │  ├─ UserInfoResponse.java
      │  │           │  │  ├─ UserTeamJoinResponse.java
      │  │           │  │  └─ WriteInfoResponse.java
      │  │           │  ├─ repository
      │  │           │  │  ├─ BalanceGameRepository.java
      │  │           │  │  ├─ BalanceGameVoteRepository.java
      │  │           │  │  ├─ CandidateDateRepository.java
      │  │           │  │  ├─ CardRepository.java
      │  │           │  │  ├─ EmitterRepository.java
      │  │           │  │  ├─ LetterRepository.java
      │  │           │  │  ├─ MemoReactionRepository.java
      │  │           │  │  ├─ MemoRepository.java
      │  │           │  │  ├─ NotificationRepository.java
      │  │           │  │  ├─ ScheduleRepository.java
      │  │           │  │  ├─ ScheduleTimeSlotRepository.java
      │  │           │  │  ├─ ScheduleVoteRepository.java
      │  │           │  │  ├─ SmallTalkListRepository.java
      │  │           │  │  ├─ SmallTalkRepository.java
      │  │           │  │  ├─ TeamRepository.java
      │  │           │  │  ├─ UserRepository.java
      │  │           │  │  └─ UserTeamRepository.java
      │  │           │  └─ service
      │  │           │     ├─ BalanceGameService.java
      │  │           │     ├─ CardService.java
      │  │           │     ├─ GeminiClientService.java
      │  │           │     ├─ LetterService.java
      │  │           │     ├─ LoginService.java
      │  │           │     ├─ MemoService.java
      │  │           │     ├─ NotificationService.java
      │  │           │     ├─ ScheduleService.java
      │  │           │     ├─ SmallTalkGeneratorService.java
      │  │           │     ├─ SmallTalkService.java
      │  │           │     ├─ SseEmitterService.java
      │  │           │     ├─ TeamCleanupService.java
      │  │           │     └─ TeamService.java
      │  │           ├─ global
      │  │           │  ├─ config
      │  │           │  │  ├─ RestTemplateConfig.java
      │  │           │  │  ├─ SecurityConfig.java
      │  │           │  │  ├─ SwaggerConfig.java
      │  │           │  │  └─ WebConfig.java
      │  │           │  ├─ dto
      │  │           │  │  └─ ApiResponseTemplete.java
      │  │           │  ├─ exception
      │  │           │  │  ├─ AlreadyJoinedException.java
      │  │           │  │  ├─ ErrorCode.java
      │  │           │  │  ├─ InvalidTokenException.java
      │  │           │  │  ├─ SuccessCode.java
      │  │           │  │  └─ model
      │  │           │  │     ├─ CoreApiException.java
      │  │           │  │     ├─ CustomException.java
      │  │           │  │     ├─ GlobalExceptionHandler.java
      │  │           │  │     └─ ResourceNotFoundException.java
      │  │           │  └─ security
      │  │           │     └─ TokenService.java
      │  │           └─ oauth2
      │  │              ├─ CustomOAuth2SuccessHandler.java
      │  │              ├─ CustomOAuth2UserService.java
      │  │              ├─ KakaoUserInfo.java
      │  │              └─ OAuth2UserInfo.java
      │  └─ resources
      │     ├─ application-jwt.yml
      │     ├─ application.properties
      │     ├─ application.yml
      │     └─ static
      │        ├─ login.html
      │        └─ sse-test.html
      └─ test
         └─ java
            └─ com
               └─ project
                  └─ icey
                     └─ IceyApplicationTests.java
```
©generated by [Project Tree Generator](https://woochanleee.github.io/project-tree-generator)
