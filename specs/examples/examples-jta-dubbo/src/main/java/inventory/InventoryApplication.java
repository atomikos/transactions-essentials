package inventory;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.atomikos.jdbc.AtomikosDataSourceBean;

@SpringBootApplication
@PropertySource("classpath:inventory.properties")
public class InventoryApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		System.setProperty("com.atomikos.icatch.rest_port_url", "http://localhost:9091/transactions/atomikos"); 
		new InventoryApplication()
		.configure(new SpringApplicationBuilder(InventoryApplication.class))
		.run(args);

	}

	@Bean(initMethod = "init", destroyMethod = "close")
	public AtomikosDataSourceBean dataSource() {
		AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
		dataSource.setUniqueResourceName("inventory");
		dataSource.setLocalTransactionMode(true);
		dataSource.setXaDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
		Properties xaProperties = new Properties();
		xaProperties.setProperty("user", "sa");
		xaProperties.setProperty("url", "jdbc:h2:mem:inventory");
		dataSource.setXaProperties(xaProperties);
		return dataSource;
	}

	
}
