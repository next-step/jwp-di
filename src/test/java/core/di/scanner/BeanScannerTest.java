package core.di.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.di.ApplicationContext;
import core.di.factory.BeanFactory;
import core.di.factory.example.MyJdbcTemplate;
import core.di.factory.example.MyQnaService;
import core.di.factory.example.QnaController;

public class BeanScannerTest {
	@Test
	@DisplayName("ComponentScan 설정을 통한 Bean 등록 테스트")
	public void register() {
		// given & when
		ApplicationContext beanScanner = new ApplicationContext("core.di.factory.example");
		beanScanner.scan();
		beanScanner.beanInitialize();
		BeanFactory beanFactory = beanScanner.getBeanFactory();

		// then
		assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
		assertThat(beanFactory.getBean(MyJdbcTemplate.class)).isNotNull();
		assertThat(beanFactory.getBean(QnaController.class)).isNotNull();
		QnaController qnaController = beanFactory.getBean(QnaController.class);
		assertThat(qnaController.getQnaService()).isNotNull();
		MyQnaService qnaService = qnaController.getQnaService();
		assertThat(qnaService.getUserRepository()).isNotNull();
		assertThat(qnaService.getQuestionRepository()).isNotNull();
	}
}
