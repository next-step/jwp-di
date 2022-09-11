# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## 1단계 - DI 구현

## 요구 사항

- 생성자를 활용하는 DI 프레임워크 구현.
- 각 클래스에 대한 `인스턴스 생성` 및 `의존관계 설정`을 어노테이션으로 자동화 한다.
  - 각 클래스의 역할에 맞도록 `@Controller`, `@Service`, `@Repository` 어노테이션을 설정
  - 어노테이션 설정된 각 인스턴스 간의 의존관계는 `@Inject` 어노테이션을 사용
    - `@Inject` 어노테이션이 설정된 생성자를 통해서 의존관계 주입
      - 빈 객체를 생성

- BeanFactoryTest의 `di()`를 통과하도록 DI 프레임워크를 구현
- 구현한 DI 프레임워크와 기존의 MVC 프레임워크를 통합.
  - `ControllerScanner` -> `BeanScanner`로 명칭 변경
  - `BeanScanner`는 @Controller 어노테이션만 찾는 역할에서, `@Controller, @Service, @Repository` 어노테이션을 모두 찾도록
    역할 확대
  - `AnnotationHandlerMapping`이 `BeanScanner`를 활용해 동작하도록 리팩토링.

## 2단계 - DI 구현 (힌트)

## 요구 사항

- 각 클래스의 역할에 맞도록 `@Controller`, `@Service`, `@Repository` 어노테이션을 설정
- 이 3개의 어노테이션으로 생성된 인스턴스간의 **의존관계는 `@Inject`를 사용**
- Bean 컨테이너를 실제 기능(사용자 관리 서비스, 질문/답변 게시판)에 적용
