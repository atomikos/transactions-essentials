package inventory;


import org.springframework.beans.factory.annotation.Autowired;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(version = "1.0.0")
@Transactional
public class InventoryServiceImpl implements InventoryService {

	@Autowired
	StockRepository repository;
	
	public void update(String itemId, int qty) throws Exception {
		System.out.println("itemId: "+itemId+" - "+qty);
		repository.save(new Stock(itemId, qty)); 
	}
}
