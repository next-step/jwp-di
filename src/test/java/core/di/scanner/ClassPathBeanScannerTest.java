package core.di.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.di.factory.BeanFactory;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;

public class ClassPathBeanScannerTest {
	@Test
	@DisplayName("Bean 등록 테스트")
	public void register() {
		// given
		BeanFactory beanFactory = new BeanFactory();
		ClassPathBeanScanner scanner = new ClassPathBeanScanner(beanFactory);

		// when
		scanner.scan("core.di.factory.example");
		beanFactory.initialize();

		// then
		assertThat(beanFactory.getBean(QnaController.class)).isNotNull();

		QnaController qnaController = beanFactory.getBean(QnaController.class);
		assertThat(qnaController.getQnaService()).isNotNull();

		MyQnaService qnaService = qnaController.getQnaService();
		assertThat(qnaService.getUserRepository()).isNotNull();
		assertThat(qnaService.getQuestionRepository()).isNotNull();
	}
}
