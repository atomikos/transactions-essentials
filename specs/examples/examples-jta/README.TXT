ARCHITECTURE
------------

                           1. begin
(Non)XaJdbcIntegrationTest ----------> UserTransactionImp (Atomikos) 
             |             5. commit               |
             |                                     |
             | 2. get connection                   | 
             | 3. do SQL                           | 5.1 commit
             | 4. close connection                 | 
             |                                     |
             --> Atomikos(NonXA)DataSourceBean <---
