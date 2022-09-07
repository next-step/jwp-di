# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

# 기능 요구사항 (DI 구현)
>이전 미션에서 새로 만든 MVC 프레임워크는 자바 리플렉션을 활용해서 @Controller 애노테이션이 설정되어 있는 클래스를 찾아 인스턴스를 생성하고, URL 매핑 작업을 자동화했다.
같은 방법으로 DI 구현을 위해, 각 클래스에 대한 인스턴스 생성 및 의존관계 설정을 애노테이션으로 자동화한다.<br>
먼저 애노테이션은 각 클래스 역할에 맞도록 컨트롤러는 이미 추가되어 있는 @Controller, 서비스는 @Service, DAO 는 @Repository 애노테이션을 설정한다.<br>
이 3 개의 설정으로 생성된 각 인스턴스 간의 의존관계는 @Inject 애노테이션을 사용한다.

> DI 프레임워크 구현을 완료했다면, 이제는 앞에서 구현한 MVC 프레임워크와 통합이 필요하다. DI 프레임워크를 활용하기 위해 @Controller 설정이 되어있는 클래스를 찾는 ControllerScanner 를 DI 프레임워크가 있는 패키지로 이동해서 @Controller 뿐만 아니라 @Service, @Repository 에 대한 지원이 가능하도록 개성한다.
> 클래스 이름도 @Controller 애노테이션만 찾던 역할에서 @Service, @Repository 애노테이션까지 확대 되었으니 BeanScanner 로 이름을 리팩토링 한다.

> MVC 프레임워크의 AnnotationHandlerMapping 이 BeanFactory 와 BeanScanner 를 활용하여 동작하도록 리팩토링 한다.
