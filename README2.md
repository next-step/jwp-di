Configuration 설정

1. @Configuration 어노테이션을 찾고 @Bean 어노테이션이 있을 경우 BeanFactory 에 등록한다.
2. Reflections 의 기본 패키지는 하드 코딩 -> @ComponentScan 을 통해 기본 패키지 설정 (없을 경우? 전체 패키지)
3. @Configuration Bean 과 BeanScanner Bean 들은 공유가 필요하다? (DI 할 수 있다.)

설정 파일 예제를 통해 테스트를 진행 해본다.
