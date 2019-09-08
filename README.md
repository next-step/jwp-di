# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)


# step3
- [x] @Configuration 설정파일 생성
    - [x] beanFactory에서 초기화 하기 (매개변수 : Configuration.class)
    - [x] beanScanner에 @ComponentScan 경로 전달하기
    - [x] dispatcherServlet에서 호출하도록 설정
    - [x] 초기화
- [x] di적용 (JdbcTemplate)
- [x] 테스트케이스 및 기타 수정