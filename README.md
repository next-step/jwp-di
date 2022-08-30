# DI 프레임워크 구현

## 요구사항
- [x] BeanFactoryTest 통과 구현
  - BeanFactoryTest의 di() 테스트가 성공하면 생성자를 활용하는 DI 프레임워크 구현을 완료한 것이다. 또한 구현 중 필요한 기능을 도와주기 위해 core.di.factory.BeanFactoryUtils 클래스를 제공하고 있다.
  - 자바 클래스에 대한 인스턴스 생성은 자바 리플렉션 API를 직접 이용할 수도 있지만 이를 추상화한 Spring 프레임워크에서 제공하는org.springframework.beans.BeanUtils의 instantiateClass() 메소드를 사용해도 된다.
- [x] ControllerScanner를 DI 프레임워크가 있는 패키지로 이동해 @Controller, @Service, @Repository에 대한 지원이 가능하도록 개선
- [x] 클래스 이름도 @Controller 애노테이션만 찾던 역할에서 @Service, @Repository 애노테이션까지 확대 되었으니 BeanScanner로 이름을 리팩토링
- [x] MVC 프레임워크의 AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링

## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)
