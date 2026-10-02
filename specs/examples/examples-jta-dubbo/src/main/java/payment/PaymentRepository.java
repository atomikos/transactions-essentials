package payment;

import org.springframework.data.jpa.repository.JpaRepository;

import payment.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}
