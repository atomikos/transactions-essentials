package inventory;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Stock {

	@Id
	public String itemId;

	public int amount;

	public Float unitPrice;
	
	public Stock() {
		// TODO Auto-generated constructor stub
	}
	
	

	public Stock(String itemId, int amount) {
		super();
		this.itemId = itemId;
		this.amount = amount;
	}



	public String getItemId() {
		return itemId;
	}



	public void setItemId(String itemId) {
		this.itemId = itemId;
	}



	public int getAmount() {
		return amount;
	}



	public void setAmount(int amount) {
		this.amount = amount;
	}



	public Float getUnitPrice() {
		return unitPrice;
	}



	public void setUnitPrice(Float unitPrice) {
		this.unitPrice = unitPrice;
	}




}
