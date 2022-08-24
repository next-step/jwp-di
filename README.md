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
> ANnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링 한다.  

### 기능 목록
- [ ] BeanFactoryTest 의 di() 테스트가 성공하면 DI 프레임워크 구현 완료.
  - BeanFactoryUtils 클래스 활용
  - 인스턴스 생성은 스프링의 BeanUtils의 instantiateClass() 메서드 사용가능
- [ ] BeanFactoryUtils 테스트 코드 작성
  - [x] getInjectedConstructor 
  - [ ] findConcreteClass 
- [ ] BeanFactory 의 initialize 구현
  - [ ] 생성자로 주입 받은 클래스의 인스턴스를 생성한다.
  - [ ] 생성된 인스턴스는 타입을 키로 하는 Map 에 저장한다.
  - [ ] 생성에 필요한 빈이 Map 에 없으면 해당 빈도 인스턴스를 생성한다.
  - [ ] 생성에 필요한 빈이 Map 에 있으면 인스턴스 생성에 활용한다.
 

