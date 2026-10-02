package payment;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Payment implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	public String cardno;
	public Integer amount;
	
	public Payment() {
	}

	
	public Payment(String cardno, Integer amount) {
		super();
		this.cardno = cardno;
		this.amount = amount;
	}


	public String getCardno() {
		return cardno;
	}

	public void setCardno(String cardno) {
		this.cardno = cardno;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}



}