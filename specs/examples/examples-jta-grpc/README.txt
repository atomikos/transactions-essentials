ARCHITECTURE
============
                      
  1. begin  4. commit                    2. pay                               
 client.EmbeddedTransactionManagerClient ----> payment.PaymentServer
                                         4.2 2pc                                               
      |  3. update                                                                             
      -----------> inventory.InventoryServer 
         4.1 2pc                                         
                                       
              

RUNNING THE EXAMPLES
====================

1a. run mvn install to create the demo jar file

   (then cd into target/ to have examples-jta-rest-jaxrs.jar in reach)

2. start the inventory service:

   java -cp examples-jta-grpc.jar inventory.InventoryServer

3. start the payment service:

   java -cp examples-jta-grpc.jar payment.PaymentServer

4. run the client with distributed transactions across payment and inventory:

   java -cp examples-jta-grpc.jar client.Client
	
	
DISCLAIMER
==========

The DBMS used for the demo is in-memory only, you would need a full XA DBMS server for production use.

