# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

---

# 🚀 1단계 - DI 구현

### 요구사항
> 각 클래스에 대한 인스턴스 생성 및 의존관계 설정을 애노테이션으로 자동화한다.  
> 역할에 맞는 `Controller, Service, Repository` 애너테이션을 설정한다.  
> 인스턴스 간의 의존 관계는 `@Inject` 애노테이션을 사용한다.  

### 추가 요구사항
> ControllerScan을 추가된 애너테이션을 지원하도록 개선한다.  
> 역할이 변경되었으니 클래스 이름도 BeanScanner로 변경한다.  
> AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링 한다.  

### 기능 목록
- [x] BeanFactoryTest 의 di() 테스트가 성공하면 DI 프레임워크 구현 완료.
  - BeanFactoryUtils 클래스 활용
  - 인스턴스 생성은 스프링의 BeanUtils의 instantiateClass() 메서드 사용가능
- [x] BeanFactoryUtils 테스트 코드 작성
  - [x] getInjectedConstructor 
  - [x] findConcreteClass 
- [x] BeanFactory 의 initialize 구현
  - [x] 생성자로 주입 받은 클래스의 인스턴스를 생성한다.
  - [x] 생성된 인스턴스는 타입을 키로 하는 Map 에 저장한다.
  - [x] 생성에 필요한 빈이 Map 에 없으면 해당 빈도 인스턴스를 생성한다.
  - [x] 생성에 필요한 빈이 Map 에 있으면 인스턴스 생성에 활용한다.
- [x] 특정 애너테이션이 적용된 클래스들을 찾는다.
  - [x] 여러개의 애너테이션을 가변인자로 전달할 수 있다. 
- [x] ControllerScan 에서 Service, Repository 애너테이션을 지원하도록 개선
  - [x] 스캔 대상 애노테이션 추가 
- [x] 클래스 이름 및 패키지 변경 core.mvc.tobe.ControllerScan -> core.di.factory.BeanScanner
- [x] BeanFactory와 BeanScanner를 활용하여 리팩토링

# 🚀 2단계 - DI 구현(힌트)

### 1단계 피드백
- [x] BeanFactory: private 메서드를 호출 순서로 정렬
  > 클린코드   
  > **코드는 신문 기사를 읽듯이, 위에서 아래로 읽어 내려가며 이해할 수 있어야 한다.**   
  > 기능은 위에서부터 아래 순서로 읽어내릴 수 있게 서술되어야 하며,   
  > 비슷한 기능은 서로 가까운 행에 배치하면 읽는 사람이 이해하기 쉽다.   
- [ ] BeanFactory: 불필요한 static 키워드 제거
  - IDE 자동 완성으로 인한 static 추가..
- [ ] BeanFactoryUtils#findImplementedConcreteClass
  - contains 로직까지 모두 메서드로 분리하여 가독성을 높일 수 있을 것 같다.
- [ ] BeanFactoryUtils: 파라미터명 변경 (beans -> bean) 
  - 의미있는 이름을 사용하자!
- [ ] BeanScanner: 변수명 변경 (controllers -> controllerTypes)
  - 일관성 이름을 사용해야 코드를 읽을 때 혼란을 줄일 수 있다
- [ ] BeanFactoryUtils#getInjectedConstructor `@Inject` 애너테이션이 2개 이상 감지되면 예외 발생
  - API 사용자가 주석을 읽지 않고 사용하는 경우를 대비해서 예외를 던진다면 개발 중에 빠르게 인지할 수 있을 것 같다.
- 
