ARCHITECTURE
============
                      
                      2. pay                                 
 client.TaasClient --------------> payment.PaymentServer
 (or taas-client.js)
                                         ^                                  
   |  |  3. update                       |                                   
   |  -------->inventory.InventoryServer |
   |                    ^                | 4.2 2pc                    
   |           4.1 2pc  |                |                                   
   |                    |                |                                     
   ---->  client.RestTransactionServiceInstance 
   1. begin                                      
   4. commit
   

RUNNING THE EXAMPLES
====================

1. run mvn install to create the demo jar file

   (then cd into target/ to have examples-jta-rest-jaxrs.jar in reach)

2. start the inventory service:

   java -cp examples-jta-rest-jaxrs.jar -Dcom.atomikos.icatch.tm_unique_name=inventory inventory.InventoryServer

3. start the payment service:

   java -cp examples-jta-rest-jaxrs.jar -Dcom.atomikos.icatch.tm_unique_name=payment payment.PaymentServer
   
4. then start the RestTransactionService - needed to start and terminate distributed REST transactions from universal clients:

   java -cp examples-jta-rest-jaxrs.jar -Dcom.atomikos.icatch.tm_unique_name=coordinator client.RestTransactionServiceInstance
   
5. run the Java client with distributed transactions across payment and inventory:

   java -cp examples-jta-rest-jaxrs.jar client.TaasClient
   

ALTERNATIVELY
=============

6. cd into src/main/js

7. run the Node.js client with distributed transactions across payment and inventory:

	node taas-client.js
	
