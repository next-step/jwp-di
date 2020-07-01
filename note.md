# 정리노트

정리를 해봅시다.  

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