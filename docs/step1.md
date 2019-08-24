# 1단계 - DI 구현
## 실습 환경 구축 및 코드 리뷰
* 미션 시작 버튼을 클릭해 리뷰어 매칭과 브랜치를 생성한다.
* DI 프레임워크 구현 실습을 위한 저장소 저장소 브랜치에 자신의 github 아이디에 해당하는 브랜치가 있는지 확인한다. 없으면 미션 시작 버튼을 눌러 미션을 시작한다.
* 온라인 코드리뷰 요청 1단계 문서의 1단계부터 5단계까지 참고해 실습 환경을 구축한다.
* next.WebServerLauncher를 실행한 후 브라우저에서 http://localhost:8080으로 접근한다.
* 브라우저에 질문/답변 게시판이 뜨면 정상적으로 세팅된 것이다.

## 기본 요구사항
> 새로 만든 MVC 프레임워크는 자바 리플렉션을 활용해 @Controller 애노테이션이 설정되어 있는 클래스를 찾아 인스턴스를 생성하고, URL 매핑 작업을 자동화했다. 
> 
> 같은 방법으로 각 클래스에 대한 인스턴스 생성 및 의존관계 설정을 애노테이션으로 자동화한다.
>
> 먼저 애노테이션은 각 클래스 역할에 맞도록 컨트롤러는 이미 추가되어 있는 @Controller, 서비스는 @Service, DAO는 @Repository 애노테이션을 설정한다. 
>
> 이 3개의 설정으로 생성된 각 인스턴스 간의 의존관계는 @Inject 애노테이션을 사용한다.
  
### DI 프레임워크를 활용한 DI 예제
```java
@Controller
public class QnaController extends AbstractNewController {
    private MyQnaService qnaService;

    @Inject
    public QnaController(MyQnaService qnaService) {
        this.qnaService = qnaService;
    }
    
    @RequestMapping("/questions")
    public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return jspView("/qna/list.jsp");
    }
}
```

```java
@Service
public class MyQnaService {
    private UserRepository userRepository;
    private QuestionRepository questionRepository;

    @Inject
    public MyQnaService(UserRepository userRepository, QuestionRepository questionRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
    }
    
    [...]
}
```

```java
@Repository
public class JdbcQuestionRepository implements QuestionRepository {
}

@Repository
public class JdbcUserRepository implements UserRepository {
}
```

### DI 프레임워크 테스트 및 팁
* 효과적인 실습을 위해 애노테이션(core.annotation 패키지), DI가 설정되어 있는 예제 코드(core.di.factory.example), 요구사항을 만족해야 하는 테스트 코드(core.di.factory.BeanFactoryTest)를 제공하고 있다.

* BeanFactoryTest의 di() 테스트가 성공하면 생성자를 활용하는 DI 프레임워크 구현을 완료한 것이다. 또한 구현 중 필요한 기능을 도와주기 위해 core.di.factory.BeanFactoryUtils 클래스를 제공하고 있다.

* 자바 클래스에 대한 인스턴스 생성은 자바 리플렉션 API를 직접 이용할 수도 있지만 이를 추상화한 Spring 프레임워크에서 제공하는org.springframework.beans.BeanUtils의 instantiateClass() 메소드를 사용해도 된다.

## 추가 요구사항
> 지금까지의 과정을 통해 DI 프레임워크를 완료했다면 다음 단계는 앞에서 구현한 MVC 프레임워크와의 통합이 필요하다. 
>
> 여기서 구현한 DI 프레임워크를 활용할 경우 앞에서 @Controller이 설정되어 있는 클래스를 찾는 ControllerScanner를 DI 프레임워크가 있는 패키지로 이동해 @Controller, @Service, @Repository에 대한 지원이 가능하도록 개선한다.
>
> 클래스 이름도 @Controller 애노테이션만 찾던 역할에서 @Service, @Repository 애노테이션까지 확대 되었으니 BeanScanner로 이름을 리팩토링한다.
>
> MVC 프레임워크의 AnnotationHandlerMapping이 BeanFactory와 BeanScanner를 활용해 동작하도록 리팩토링한다.