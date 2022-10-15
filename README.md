# DI 프레임워크 구현
## 진행 방법
* 프레임워크 구현에 대한 요구사항을 파악한다.
* 요구사항에 대한 구현을 완료한 후 자신의 github 아이디에 해당하는 브랜치에 Pull Request(이하 PR)를 통해 코드 리뷰 요청을 한다.
* 코드 리뷰 피드백에 대한 개선 작업을 하고 다시 PUSH한다.
* 모든 피드백을 완료하면 다음 단계를 도전하고 앞의 과정을 반복한다.

## 온라인 코드 리뷰 과정
* [텍스트와 이미지로 살펴보는 온라인 코드 리뷰 과정](https://github.com/next-step/nextstep-docs/tree/master/codereview)

## Step 1 - DI 구현 요구사항
- BeanFactoryTest 테스트 케이스 통과
- ControllerScanner 리팩터링

## 3단계 - @Configuration 설정

- 자바 클래스가 설정 파일이라는 표시는 '@Configuration'으로 한다. 각 메소드에서 생성하는 인스턴스가 'BeanFactory'에 빈으로 등록하라는 설정은 '@Bean' 애노테이션으로 한다.
- 'BeanScanner'에서 사용할 기본 패키지에 대한 설정을 하드코딩했는데 설정 파일에서 '@ComponentScan'으로 설정할 수 있도록 지원하면 좋겠다.
- '@Configuration' 설정 파일을 통해 등록한 빈과 'BeanScanner'를 통해 등록한 빈 간에도 DI가 가능해야 한다.
