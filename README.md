# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

### 구현 요구 사항
* Step 1
  * BeanFactoryTest 의 di()테스트를 성공 시킨다.
    * @Controller, @Service, @Repository 애노테이션을 통한 빈 자동 주입 기능을 생성한다.
    * ControllerScanner 패키지 이동 후 BeanScanner Rename 및 @Service, @Repository 애노테이션 까지 지원
* Step2
  * Step 1 구조로 진행 함
* Step3
  * 설정 파일이라는 표시를 @Configuration으로, BeanFactory에 등록하라는 설정은 @Bean 애노테이션으로 함.
  * BeanScanner 사용할 기본 패키지에 대한 설정을 설정 파일에서 @ComponentScan으로 설정.
  * @Configuration <-> BeanScanner를 통한 빈 사이에도 DI 가능하여야 함.
