# DI 프레임워크 구현

## 진행 방법

* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정

* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## 1단계 - DI 구현

- 컨트롤러는 `@Controller`, 서비스는 `@Service`, DAO는 `@Repository` 애노테이션을 설정
- 의존관계는 `@Inject` 애노테이션 설정
- `BeanFactoryTest` 의 `di()` 테스트 완성
    - 필요한 기능을 위한 `core.di.factory.BeanFactoryUtils` 클래스를 제공
    - 인스턴스 생성은 `org.springframework.beans.BeanUtils`의 `instantiateClass()` 메소드 사용 가능
- `ControllerScanner` 을 `BeanScanner`으로 변경
    - `AnnotationHandlerMapping` 이 `BeanScanner` 으로 동작하도록 변경


## 2단계 - DI 구현(힌트)

### 1단계 힌트

- 재귀함수를 사용해 구현할 수 있다
  - 다른 빈과 의존관계를 가지지 않는 빈을 찾아 인스턴스를 생성할 때까지 재귀를 실행하는 방식으로 구현
- 재귀를 통해 새로 생성한 빈은 `BeanFactory`의 `Map<Class<?>, Object>`에 추가해 관리

### 2단계 힌트

- `Class`에 대한 빈 인스턴스를 생성하는 메소드
- `Constructor`에 대한 빈 인스턴스를 생성하는 메소드
- `@Inject` 애노테이션이 설정되어 있는 생성자가 존재하면 인스턴스 생성
  - 없으면 기본 생성자로 인스턴스 생성
- `Map<Class<?>, Object>`에 이미 존재하면 해당 빈 활용
  - 존재하지 않을 경우 `instantiateClass()` 으로 생성
- `ControllerScanner`를 `@Controller`, `@Service`, `@Repository` 도 지원하도록 `BeanScanner` 로 리팩토링
  - `@Controller`가 설정되어 있는 빈 목록을 Map<Class<?>, Object>으로 제공


## 3단계 - @Configuration 설정

- 설정 파일 표시는 `@Configuration`, `BeanFactory` 에 빈으로 등록하는 설정은 `@Bean`
- `BeanScanner` 에서 기본 패키지 설정을 설정 파일의 `@ComponentScan` 으로 설정
- `@Configuration` 설정 파일을 통해 등록한 빈과 `BeanScanner` 를 통해 등록한 빈 간에 DI 가능
