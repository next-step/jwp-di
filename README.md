# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## 🚀 1단계 - DI 구현
1. [X] 요구사항 1 - @Service, @Repository 애노테이션을 가지고있는 클래스의 인스턴스를 생성한다. 
2. [X] 요구사항 2 - 생성된 인스턴스의 의존관계는 @Inject 애노테이션을 사용하여 주입한다.
3. [X] 요구사항 3 - 기존 ControllerScanner를 BeanFactory를 사용하도록 수정.

## 🚀 2단계 - DI 구현
1. [X] 요구사항 1 - @Service, @Repository 애노테이션을 가지고있는 클래스의 인스턴스를 생성한다.
2. [X] 요구사항 2 - 생성된 인스턴스의 의존관계는 @Inject 애노테이션을 사용하여 주입한다.
3. [X] 요구사항 3 - 기존 ControllerScanner를 BeanFactory를 사용하도록 수정.
4. [X] 요구사항 4 - Step1 피드백 반영 ( e.printStackTrace() -> Logger.error 로 변경)

## 🚀 3단계 - @Configuration 설정
1. [X] 요구사항 1 - @Bean어노테이션을 사용하여 BeanFactory에 빈으로 등록한다.
2. [X] 요구사항 2 - @ComponentScan으로 스캔할 패키지를 지원한다.
3. [X] 요구사항 3 - @Configuration설정파일을 통해 등록한 빈과 BeanScanner를 통해 등록한 빈 간에도 DI를 지원한다.
