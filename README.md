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