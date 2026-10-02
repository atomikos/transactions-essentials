package client;


import org.apache.dubbo.config.annotation.Reference;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import com.atomikos.icatch.jta.UserTransactionManager;

import inventory.InventoryService;
import payment.Payment;
import payment.PaymentService;


@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
@PropertySource("classpath:client.properties")
public class DubboClient {

    //private final Logger logger = LoggerFactory.getLogger(getClass());

    @Reference(version = "1.0.0", url = "dubbo://127.0.0.1:6789")
    private PaymentService paymentService;

    
    @Reference(version = "1.0.0", url = "dubbo://127.0.0.1:12345")
    private InventoryService inventoryService;

    
    public static void main(String[] args) {
        SpringApplication.run(DubboClient.class).close();
    }

    @Bean
    public ApplicationRunner runner() {
    	
        return args -> {
    		UserTransactionManager utm = new UserTransactionManager();
    		utm.init();
    		try {			
    			utm.begin();
    			Payment payment = new Payment("card10", 10);
    			paymentService.pay(payment);
    			inventoryService.update("20", 2);
    			utm.commit();
    		} catch (Exception e) {
    			e.printStackTrace();
    			if (utm != null) {
    				utm.rollback();
    			}
    		}
    		utm.close();        	
        	
        };
    }
	
}
