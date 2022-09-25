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

# 기능 요구사항 (@Configuration 설정)
>현재 JdbcTemplate 에서 데이터베이스의 Connection 을 생성하는 부분이 static 으로 구현되어 있다. 또한 데이터베이스 설정 정보 또한 하드 코딩으로 관리하고 있어서 특정 데이터베이스에 종속되는 구조로 구현되어 있다.
> 데이터베이스에 종속되지 않도록 구현하고 Connection Pooling 을 지원하기 위해 Connection 대신 javax.sql.DataSource 인터페이스에 의존관계를 가지도록 지원한다.

>이 문제를 해결하기 위해 개발자가 직접 빈을 생성해 관리할 수 있는 별도의 설정 파일을 만든다.
>예를 들어, 설정 파일에 빈 인스턴스를 생성하는 메소드를 구현해 놓고 애노테이션으로 설정한다. DI 프레임워크는 이 설정 파일을 읽어서 BeanFactory 에 빈으로 저장할 수 있다면 BeanScanner 를 통해 등록한 빈과 같은 저장소에서 관리할 수 있다.

- 자바 클래스가 설정 파일이라는 표시는 @Configuration 으로 한다. 각 메소드에서 생성하는 인스턴스가 BeanFactory 에 빈으로 등록하라는 설정은 @Bean 애노테이션으로 한다.
- BeanScanner 에서 사용할 기본 패키지에 대한 설정을 하드코딩했는데 설정 파일에서 @ComponentScan 으로 설정할 수 있도록 지원한다.
- 위와 같이 @Configuration 설정 파일을 통해 등록한 빈과 BeanScanner 를 통해 등록한 빈 간에도 DI가 가능해야 한다.

# 기능 목록
- BeanDefinition 인터페이스
  - 빈에 대한 클래스 정보와 메서드 정보를 관리한다.
  - ClassBeanDefinition 구현체
    - 빈에 대한 클래스 정보를 필드로 가진다. 클래스 타입의 반환하는 메서드를 제공한다.
  - MethodBeanDefinition 구현체
    - 빈에 대한 클래스 정보와 메서드 정보를 필드로 가진다. 클래스 타입과 메서드, 그리고 메서드 리턴 타입을 반환하는 메서드를 제공한다.
- BeanDefinitions 객체
  - BeanDefinition 을 감싼 일급 컬렉션
  - 자동 & 수동으로 인해 등록될 모든 Bean 의 Definition 을 갖는다.
  - 자동 스캔으로 인해 등록된 BeanDefinition 을 가져올 수 있다.
  - 수동 스캔으로 인해 등록된 BeanDefinition 을 가져올 수 있다.
- BeanDefinitionRegistry 객체
  - BeanDefinitions 타입의 필드 2개를 관리한다.
    - methodBean 과 classBean 을 구분하여 저장하고 관리한다.
- ClassPathBeanDefinitionScanner 객체
  - 자동 스캔 : 특정 package 하위에 @Controller, @Service, @Repository, @Component, @Configuration 애노테이션이 붙은 클래스를 reflections 를 이용하여 scan 하여 BeanDefinitionRegistry 에 저장한다.
- AnnotatedBeanDefinitionReader 객체
  - 수동 스캔 : 특정 Configuration 설정 클래스에 대한 빈 정보를 저장한다.
- BeanFactory 객체
  - ClassPathBeanDefinitionScanner 과 AnnotatedBeanDefinitionReader 를 통해 저장된 BeanDefinitionRegistry 와, 인스턴스화 된 beans(빈 저장소) 를 관리한다.
  - BeanFactory 초기 생성 시, 인스턴스화 되기 전 scanner 와 reader 에 의해 저장된 BeanDefinitions 정보(후보 빈)들을 입력받는다.
  - BeanFactory 초기화 시, 후보 빈 들을 인스턴스 화 시킨다.
    - 빈 저장소에 후보 빈의 인스턴스가 존재할 경우, 바로 그 인스턴스를 반환한다.
    - 우선 수동 스캔으로 등록된 BeanDefinition 을 먼저 빈 인스턴스화 시킨다.
      - 만약 해당 설정 파일의 메서드의 파라미터가 존재하면, 해당 파라미터를 다시 인스턴스화 시키기 위해 재귀로 구현한다.
      - 파라미터가 존재하지 않을 경우, 해당 메서드를 invoke 한다. -> 최초로 빈을 인스턴스화 시킴 
      - 나머지 빈 정보들은 재귀 스택 프레임을 벗어나며 의존관계를 주입한다.
    - 이후 자동 스캔으로 등록된 BeanDefinition 을 생성자에 붙은 @Inject 애노테이션을 찾아 주입시킨다.
      - @Inject 생성자가 있으면 해당 생성자의 파라미터들을 이용하여 해당 빈을 인스턴스 화 시킨다. (인터페이스가 아닌 후보 빈 구현 클래스 타입이 존재해야 한다.)
      - @Inject 생성자가 없으면 기본 생성자로 해당 빈을 인스턴스 화 시킨다.
      - @Controller, @Service, @Repository 를 순서대로 빈 인스턴스화 시키면 필드끼리 의존관계가 존재할 수 있는 상황에서 인스턴스가 주입되지 않는 현상 발생
        - 재귀를 통해 의존관계가 없는 빈부터 인스턴스화 시키도록 하고 스택 프레임을 벗어나며 나머지 의존관계를 주입한다.
- ApplicationContext 객체
  - 설정 정보 클래스 타입들과 beanFactory 를 관리한다.
  - 최소 생성 시 설정 정보에 대한 클래스를 먼저 저장한다.
  - AnnotatedBeanDefinitionReader 와 ClassPathBeanDefinitionScanner 를 통해 등록된 BeanDefinitions 를 이용하여 BeanFactory 초기화 작업을 진행한다.
    - 설정 정보에 셋팅된 basePackages 를 기준으로 자동 스캔이 진행된다.
    - 설정 정보에 basPackages 가 셋팅 되어있지 않다면, 해당 설정 정보의 패키지 위치부터 스캔하도록 한다.
  - 특정 애노테이션을 가지는 클래스들에 대한 타입과 그 인스턴스를 반환할 수 있다.
  - DispatcherServlet 이 초기화 될 때 함꼐 가장 먼저 초기화 된다. 
- MyConfiguration 객체
  - 수동 스캔대상이 되는 설정 정보 클래스이다.
  - @Bean 애노테이션이 붙은 메서드의 리턴 타입이 빈으로 등록된다.
- HandlerConverter 객체
  - 요청에 대한 Handler 를 찾아주는 역할을 담당한다.
  - AnnotationHandlerMapping 생성 시 주입된 ApplicationContext 를 통해 Controller 애노테이션이 붙은 빈 정보를 넘겨받아서 Handler 를 찾는다.
