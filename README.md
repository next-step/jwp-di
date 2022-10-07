# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)


## 1단계 - DI 구현
1. BeanFactoryTest의 di() 테스트를 성공시키기 위해 BeanFactory 클래스를 구현한다.
2. BeanFactory의 initialize() 메서드를 구현하여, beans Map을 초기화한다. (BeanFactoryUtils, BeanUtils 를 사용한다.)
3. MVC 프레임워크의 AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링한다.

## 2단계 - DI 구현 (힌트)
1. 재귀함수를 통해 @Inject 애노테이션이 설정되어있는 생성자를 통해 빈을 생성한다.
2. Class, Constructor에 대한 빈 인스턴스를 생성하는 메소드를 구현한다.
3. Bean 인스턴스 생성 로직을 구현한다.
4. 생성자를 활용한 인스턴스 생성을 구현한다.
5. MVC 프레임워크의 AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링한다.

## 3단계 - @Configuration 설정
1. 데이터 베이스 Connection 생성, 설정 정보를 하드 코딩으로 관리하는데, 데이터베이스에 종속되지 않고 javax.sql.DataSource 인터페이스에 의존관계를 갖도록 지원
2. 자바 설정파일 표시는 @Configuration으로 표시하고, @Bean 애노테이션으로 BeanFactory에 등록한다.
3. 설정 파일에서 @ComponentScan으로 BeanScanner에서 사용할 기본 패키지 설정
4. @Configuration 설정 파일을 통해 등록한 빈과 BeanScanner를 통해 등록한 빈 간에도 DI가 가능해야 한다.

## 4단계 - @Configuration 설정 (힌트)
1. ConfigurationBeanScanner와 ClasspathBeanScanner의 통합을 담당하는 새로운 클래스를 ApplicationContext라는 이름으로 추가한다.
2. AnnotationHandlerMapping에서 ApplicationContext을 사용해 초기화가 가능하도록 통합한다.
3. 싱글턴으로 인스턴스 생성하던 기존 방식을 @Inject 애노테이션을 통해 주입 받을 수 있도록 수정한다.
4. 기존 테스트가 동작할 수 있도록 테스트 코드를 수정한다.