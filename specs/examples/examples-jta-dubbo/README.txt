RUNNING THE EXAMPLES
====================

1a. run mvn install to create the demo jar file

   (then cd into target/ to have examples-jta-dubbo.jar in reach)
   
2. start the inventory service:

   java -cp examples-jta-dubbo.jar inventory.InventoryApplication

3. start the payment service:

   java -cp examples-jta-dubbo.jar payment.PaymentApplication

4. run the client with distributed transactions across payment and inventory:

   java -cp examples-jta-dubbo.jar client.DubboClient
