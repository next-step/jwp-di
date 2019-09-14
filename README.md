# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)


# step3
- [x] @Configuration 설정파일 생성
    - [x] beanFactory에서 초기화 하기 (매개변수 : Configuration.class)
    - [x] beanScanner에 @ComponentScan 경로 전달하기
    - [x] dispatcherServlet에서 호출하도록 설정
    - [x] 초기화
- [x] di적용 (JdbcTemplate)
- [x] 테스트케이스 및 기타 수정

# step4
- [x] ConfigurationBeanScanner , test 만들기
- [x] 설정 파일을 통해 등록한 Bean과 ClasspathBeanScanner를 통해 추가한 Bean 통합
- [x] ApplicationContext 생성
- [ ] BeanFactory에서 method 중복기능 정리


---------------------------------------------------------------------------------

@Bean 생성방법
- @Bean이 등록된 method들을 for문으로 돌면서 빈 생성
- 클래스(리턴타입)가 생성됐는지 beans 확인 >> `중복`
- 파라미터가 있는지 확인
- 없으면 method.invoke 해서 생성
- 있으면 for문 돌면서 `instantiateBean` 재귀호출
    - 생성된 빈들 인자로 전달해서 method.invoke

ClassPath기반 생성
- 등록한 클래스들 for문 돌면서 빈 생성 >> instantiateClass
- 클래스가 생성됐는지 beans 확인
- 생성자가 있는지 확인
- 생성자가 없으면 클래스.newInstance() 생성
- 있으면 생성자 통한 bean 생성
    - 생성자에서 파라미터들을 받아
    - for문을 돌면서 실제 클래스타입을 구하고, `instantiateClass` 재귀호출
    - 그걸 기반으로 객체생성