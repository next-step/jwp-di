# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## 빈 등록 과정
- JdbcUserRepository 등록 1
- MyQnaService 등록 3
  - UserRepository
    - JdbcUserRepository 등록 1
  - QuestionRepository
    - JdbcQuestionRepository 등록 2
- QuestionRepository 1


## 추가 요구사항 
- 지금까지의 과정을 통해 DI 프레임워크를 완료했다면 다음 단계는 앞에서 구현한 MVC 프레임워크와의 통합이 필요하다. 
- 여기서 구현한 DI 프레임워크를 활용할 경우 앞에서 @Controller이 설정되어 있는 클래스를 찾는 ControllerScanner를
- DI 프레임워크가 있는 패키지로 이동해 @Controller, @Service, @Repository에 대한 지원이 가능하도록 개선한다.
- 클래스 이름도 @Controller 애노테이션만 찾던 역할에서 @Service, @Repository 애노테이션까지 확대 되었으니 
- BeanScanner로 이름을 리팩토링한다.
- MVC 프레임워크의 AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링한다. 
