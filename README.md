## DI - DI 프레임워크 구현
<hr />

## 1단계 - DI 구현
### 기본 요구 사항
- 각 클래스에 대한 인스턴스 생성 및 의존관계 설정을 애노테이션으로 자동화
- 애노테이션은 각 클래스 역할에 맞도록 컨트롤러는 이미 추가되어 있는 @Controller, 서비스는 @Service, DAO는 @Repository 애노테이션을 설정
- 이 3개의 설정으로 생성된 각 인스턴스 간의 의존관계는 @Inject 애노테이션을 사용
- DI 가 설정 되어 있는 예제코드(core.di.factory.example), 요구 사항을 만족해야하는 테스트 코드(BeanFactoryTest) 제공 
- BeanFactoryTest의 di() 테스트가 성공하면 생성자를 활용하는 DI 프레임워크 구현을 완료한 것

### 추가 요구 사항
- 지금까지의 과정을 통해 DI 프레임워크를 완료했다면 다음 단계는 앞에서 구현한 MVC 프레임워크와의 통합이 필요
- 여기서 구현한 DI 프레임워크를 활용할 경우 앞에서 @Controller이 설정되어 있는 클래스를 찾는 ControllerScanner를 DI 
프레임워크가 있는 패키지로 이동해 @Controller, @Service, @Repository에 대한 지원이 가능하도록 개선
- 클래스 이름도 @Controller 애노테이션만 찾던 역할에서 @Service, @Repository 애노테이션까지 확대 되었으니 BeanScanner로 이름을 리팩토링
- MVC 프레임워크의 AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링

<hr />


## 2단계 - DI 구현(힌트)
### 기본 요구 사항
- 각 클래스에 대한 인스턴스 생성 및 의존관계 설정을 애노테이션으로 자동화
- 애노테이션은 각 클래스 역할에 맞도록 컨트롤러는 이미 추가되어 있는 @Controller, 서비스는 @Service, DAO는 @Repository 애노테이션을 설정
- 이 3개의 설정으로 생성된 각 인스턴스 간의 의존관계는 @Inject 애노테이션을 사용
- DI 가 설정 되어 있는 예제코드(core.di.factory.example), 요구 사항을 만족해야하는 테스트 코드(BeanFactoryTest) 제공
- BeanFactoryTest의 di() 테스트가 성공하면 생성자를 활용하는 DI 프레임워크 구현을 완료한 것

### 기본 요구 사항 - 힌트
- 재귀함수 사용 
- @Inject 애노테이션이 설정되어 있는 생성자를 통해 빈을 생성 
  - 이 생성자의 인자로 전달할 빈도 다른 빈과 의존관계 존재 
  - 이와 같이 꼬리에 꼬리를 물고 빈 간의 의존관계가 발생 
- 다른 빈과 의존관계를 가지지 않는 빈을 찾아 인스턴스를 생성할 때까지 재귀를 실행하는 방식으로 구현
- 재귀를 통해 새로 생성한 빈은 BeanFactory의Map<Class, Object>에 추가해 관리
- 인스턴스를 생성하기 전에 먼저 Class에 해당하는 빈이 Map<Class<?>, Object>에 존재하는지 여부를 판단, 존재하지 않을 경우 생성하는 방식으로 구현

### 추가 요구 사항
- 지금까지의 과정을 통해 DI 프레임워크를 완료했다면 다음 단계는 앞에서 구현한 MVC 프레임워크와의 통합이 필요
- 여기서 구현한 DI 프레임워크를 활용할 경우 앞에서 @Controller이 설정되어 있는 클래스를 찾는 ControllerScanner를 DI
  프레임워크가 있는 패키지로 이동해 @Controller, @Service, @Repository에 대한 지원이 가능하도록 개선
- 클래스 이름도 @Controller 애노테이션만 찾던 역할에서 @Service, @Repository 애노테이션까지 확대 되었으니 BeanScanner로 이름을 리팩토링
- MVC 프레임워크의 AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링

### 추가 요구 사항 - 힌트
- BeanScanner는 @Controller, @Service, @Repository이 설정되어 있는 모든 클래스를 찾아 Set 에 저장

<hr />

## 3단계 - @Configuration 설정
### 요구 사항
- 데이터베이스 설정 정보 하드 코딩으로 관리 되고 있는 부분 종속되지 않도록 구현
- Connection Pooling 을 지원하기 위해 Connection 대신 javax.sql.DataSource 인터페이스에 의존관계를 가지도록 지원
- 개발자가 직접 빈을 생성해 관리할 수 있는 별도의 설정 파일 생성
- @Configuration, @Bean 애노테이션 활용
- 설정 파일에서 @ComponentScan 애노테이션으로 설정할 수 있도록 지원
- @Configuration 설정 파일을 통해 등록한 빈과 BeanScanner를 통해 등록한 빈 간에도 DI가 가능하도록 지원

<hr />