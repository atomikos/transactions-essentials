WARNING
=======

This example does not guarantee the right startup ordering for the Atomikos beans. Only ExtremeTransactions (https://www.atomikos.com/Main/ExtremeTransactions) can do this.

ARCHITECTURE
------------

        2. receive                    3. onMessage
Queue <--------- MessageDrivenContainer ---------> MessageDrivenBank
      <---------      |                             |
      4. commit |     |                             | 3.1 withdraw
        (delete)|     |  1. begin tx                |
                |     |  4. commit tx               --> Bank
                |     |                                 |
                |     |                                 | 3.1.1 SQL
                |     |                                 |
                |     -->                               |
                --------  Atomikos  ---------------->   --> Database
                                        4. commit           
