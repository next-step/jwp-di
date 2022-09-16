package core.di.scanner;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.di.factory.BeanFactory;
import core.di.factory.example.MyJdbcTemplate;

public class ConfigurationBeanScannerTest {
	@Test
	@DisplayName("Bean 어노테이션 Bean 등록 테스트")
	public void register() {
		BeanFactory beanFactory = new BeanFactory();
		ConfigurationBeanScanner scanner = new ConfigurationBeanScanner(beanFactory);
		scanner.scan("core.di.factory.example");

		beanFactory.initialize();

		assertThat(beanFactory.getBean(DataSource.class)).isNotNull();
		assertThat(beanFactory.getBean(MyJdbcTemplate.class)).isNotNull();
	}
}
