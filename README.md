# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

@Controller, @Service, @Repository 클래스의 수집
@Inject 를 이용해서 주입
BeanFactoryTest를 이용하여 테스트
BeanFactoryUtils 클래스를 사용
BeanUtils instantiateClass() 사용가능

추가 요구사항
mvc의 ControllerScanner를 di 패키지로 이동해서 @Service, @Repository를 찾도록 리팩토링. BeanScanner
AnnotationHandlerMapping 이 BeanFactory와 BeanScanner를 사용하도록
