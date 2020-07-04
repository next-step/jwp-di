# 정리노트

정리를 해봅시다.

## 3단계 - @Configuration

아.. 약 5시간 작업한 것이 구조적으로 심각하게 잘못되어 날리고 계획부터 다시 잡음..  

일단 고민 하나.. BeanDefinition에서  

- getBeanConstructor -> 생성
    - 이 구조의 문제점은 `@Bean`은 메소드가 invoke된 결과가 Bean이라는 것임..  
- getDependencies
    - 구조의 문제점은 아닌데 `@Bean`은 메소드 파라미터가 의존성이라는 것을 알아두어야 함
- 생성의 책임은 누가 가져야 하는가?  
    - BeanFactory가 해줘야겠지 뭐..  
        - 일관성 있게 하고 싶은데 어쩌지..

처음에 망했던 결정적인 이유
- BeanDefinitionHelper가 모든 것을 다해먹음
    - 정의 생성, 인스턴스화 등..
    
BeanFactory 주변에 대해 재설계를 해보자. 우선 현황 파악부터  
AS-IS
- ~~그냥 망했음~~
- 스캐너의 책임
    - 그냥 클래스 셔틀..  
- BeanFactory의 책임
    - Bean 생성 (필요한 경우)
    - 조회한 빈 반환
- BeanDefinitionUtil의 책임
    - 스캐너가 넘겨준 친구들을 BeanDefinition으로 변환
- scan -> defutil -> factory 순. 망.
  
TO-BE
- 스캐너의 책임
    - candidate class 모두 scan (기존과 동일)
    - BeanFactory에 BeanDefinition 밀어넣어주기 (추가)
        - 왜냐하면 ComponentScan도 고려해야하기 때문임..
    - ComponentScan 참고해서 스캔 (추가)
- BeanFactory의 책임
    - 조회한 Bean 반환
    - Bean 생성
        - 메소드 invoke 기반 (@Bean)
        - 생성자 기반 (기존)
            - 의존성이 있는 경우
            - 의존성이 없는 경우(No-Args)
- BeanDefinitionUtil의 책임
    - 이 친구의 책임이 스캐너로 이동
    
아직까지 고민인 부분
- BeanDefinition이 생성까지 책임지면 솔직히 나는 아주 편해짐 (~~대충하면 되니까~~)
    - 하지만 전혀 객체지향적이지 않음..  
    - 사실 이것도 문제인게 의존관계에 있는 것들을 주입해야함
        - 그래서 나는 BeanFacctory를 인자로 넘겨주다 망해버렸지
- 결론
    - Bean 생성에 대한 책임은 BeanFactory에게 있다.  

## 1단계 - DI 구현

- @Controller
- @Service
- @Repository

이 셋의 의존 관계는 `@Inject` 어노테이션을 추가해서 주입    

참고:  
- 어노테이션 패키지 (core.annotation)
- 예제 패키지 (core.di.factory.example)
- 테스트 통과 필요한 것 (BeanFactoryTest.java)
- 인스턴스화 (BeanUtils#instantiateClass)

추가 요구 사항:  

- ControllerScanner를 di 프레임워크 위치로 이동
- ControllerScanner를 다른 annotation도 지원하도록 변경
- rename: ControllerScanner to BeanScanner
- AnnotationHandlerMapping 의존성 변경
    - BeanFactory
    - BeanScanner
    
작업 순서, 할 일을 정리하자면:

1. BeanScanner
    - 책임: Bean을 찾아준다.  
2. BeanFactory
    - 책임: Bean을 가지고 있다. (Spring의 ApplicationContext의 BeanFactory랑 같은 포지션)

고민거리:

1. 복잡한 의존관계를 어떻게 탐색할 것인가?  
    - 그래프의 경우 순환(cycle)이 존재하니까 이 경우는 스프링에서도 동작 안함.  
    - 아 ㅇㅅㅇ. 그럼 트리구나. -> 재귀  
    - 근데 적어도 사이클이 존재한다면 이를 detection 할 방법은?  
        - ~~StackOverFlowError를 try-catch로 받는 무식한 짓을 한다~~
        - 각 bean을 탐색하고 의존관계를 그래프로 만들어 둔 상태에서 싸이클 검사를 하면 되긴 함.  
            - 하하 ㅇㅅㅇ 하지 말자.  
2. Lazy Initialization에 대한 처리는 어떻게 하시겠습니까?  

고민 빌드업:

- 재귀 탈출 조건: 더 이상의 의존성이 나오지 않을 때까지 탐색한다.  