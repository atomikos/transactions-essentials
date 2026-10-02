package payment;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.atomikos.jdbc.AtomikosDataSourceBean;

@SpringBootApplication
@PropertySource("classpath:payment.properties")
public class PaymentApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		System.setProperty("com.atomikos.icatch.rest_port_url", "http://localhost:9092/transactions/atomikos");
		
		new PaymentApplication()
		.configure(new SpringApplicationBuilder(PaymentApplication.class))
		.run(args);
	}

	
	@Bean(initMethod="init", destroyMethod="close")
	public AtomikosDataSourceBean dataSource() {
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
		dataSource.setLocalTransactionMode(true);
		dataSource.setUniqueResourceName("payment");
		dataSource.setXaDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
		Properties xaProperties = new Properties();
		xaProperties.setProperty("user", "sa");
		xaProperties.setProperty("url", "jdbc:h2:mem:payment");
		dataSource.setXaProperties(xaProperties);
		return dataSource;
	}
	
	
}
