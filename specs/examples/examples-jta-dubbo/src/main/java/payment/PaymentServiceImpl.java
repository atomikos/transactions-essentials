package payment;

import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(version = "1.0.0")
public class PaymentServiceImpl implements PaymentService {
	@Autowired
	private PaymentRepository paymentRepository;

	@Override
	public Payment pay(Payment payment) throws Exception {
		return paymentRepository.save(payment);
	}
	
	
}
