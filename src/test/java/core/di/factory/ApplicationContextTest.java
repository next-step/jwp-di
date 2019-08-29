package core.di.factory;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import core.di.factory.example.MyConfiguration;
import next.AppConfiguration;

public class ApplicationContextTest {
    
   
	@DisplayName("MyConfiguration 기준 빈 생성")
    @Test
    public void di() throws Exception {
        ApplicationContext applicationContext = new ApplicationContext(MyConfiguration.class);
        assertThat(applicationContext.getBean(DataSource.class)).isNotNull();
    }
    
    @DisplayName("AppConfiguration 기준 빈 생성")
    @Test
    public void di2() throws Exception {
        ApplicationContext applicationContext = new ApplicationContext(AppConfiguration.class);
        assertThat(applicationContext.getBean(DataSource.class)).isNotNull();
    }


}
