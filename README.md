# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## STEP 1,2
- [X] @Controller, @Service, @Repository 를 찾아 인스턴스를 생성하고, @Inject 를 기반으로 의존관계를 주입한다.
  - [X] BeanScanner에서 빈이 될 대상 클래스를 스캔한다.
  - [X] BeanFactory에서 빈이 될 대상 클래스들을 재귀적으로 의존관계를 주입하여 빈을 생성한다.
  - [X] BeanFactory은 @Controller 애너테이트된 빈 목록을 제공한다.
