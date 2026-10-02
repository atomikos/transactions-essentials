ARCHITECTURE
------------
                               2. create person
(XXX)IntegrationTest -> Spring ----------------> PersonServiceImpl
                        |                        |
                        | 1. begin tx            | 2.1 save
                        | 3. commit tx           |
                        |                        -> PersonRepository(Hibernate)
                        |                            |
                        |                            | 2.1.1 get connection
                        |                            | 2.1.2 SQL
                        |                            | 2.1.3 close connection
                        |             3.1 commit     |
                         ––> Atomikos ----------->    --> AtomikosDataSourceBean 
                                       
