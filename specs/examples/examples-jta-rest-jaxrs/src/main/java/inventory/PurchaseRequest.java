package inventory;

public class PurchaseRequest {
	public String cardno; 
	public int itemId , qty;
	public PurchaseRequest() {
	}
	public PurchaseRequest(String cardno, int itemId, int qty) {
		super();
		this.cardno = cardno;
		this.itemId = itemId;
		this.qty = qty;
	}
}
