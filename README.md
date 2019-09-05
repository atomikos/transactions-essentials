<p align="center"><font size="+2"><strong>Atomikos TransactionsEssentials: light-weight distributed transactions
</strong></font></p>


<p align="center"><img src=".github/Atomikos_Logo_Background.png" width="360" height="270" alt="Atomikos Logo"></p>

<p align="center"><em>Community development "mirror" of <a href="https://www.atomikos.com/Main/TransactionsEssentials">atomikos.com/Main/TransactionsEssentials</a>.</em></p>

<p align="center"><font size="+1"><em><strong>New contributors welcome - help us shape transactions for the cloud!</strong></em></font></p>

## Highlights

* **Distributed transaction management for Java** - so your Java application gets **instant reliability** without design efforts in your code.
* **Microservices support** - so you can have one global transaction spanning several services.
* **JEE compatible** - so it integrates effortlessly in your **Spring** or **Tomcat** configuration.
* **Light-weight**  - so your **microservices** can use it, too.
* **Embeddable in your code** - so you can **test everything in the IDE** and avoid late integration issues at deployment time.
* **OSGi support** - so you can use OSGi containers also.
* **Connection pooling for JDBC and JMS** - so you get maximum **performance**.
* **Built-in support for Hibernate and JPA** - so you can use your favorite persistence framework.
* **Automatic crash / restart recovery** - so your incomplete **distributed transactions are cleaned up** and your data returns to a consistent state.
* **Cloud-native design** - so your applications are ready for **deployment to your cloud**. 
* **Commercial support available** - if you're serious about transactions in your business. 

## NEW: Microservice Support

With microservices, the typical monolith ACID transaction becomes split into 2 or more local transactions, one in each microservice. This can quickly lead to inconsistencies - for instance if there are network timeouts or other failures: 

<p align="center"><img src=".github/inconsistency.jpg" alt="Inconsistent Microservices"></p>

We enable 1 global transaction, even across separate microservices. Working samples are included in our <a href="https://www.atomikos.com/Main/TransactionsEssentials">official download</a>.

For more information on how to fine-tune microservice transactions: check out our <a href="https://atomikos.teachable.com/p/microservice-transaction-patterns">online course</a>.

## Using TransactionsEssentials

### Getting Started

See [Getting Started](http://www.atomikos.com/Documentation/GettingStarted) for general documentation.

### Documentation

See documentation at [www.atomikos.com/Documentation](http://www.atomikos.com/Documentation/)

### Code Samples

Register and download from [www.atomikos.com](http://www.atomikos.com/)

### Releases

Register and download from [www.atomikos.com](http://www.atomikos.com/)

Or check [Maven Central](http://search.maven.org) (no samples available there)


## Governance & Participating

### Joining

See our [Community Page](http://www.atomikos.com/Main/AtomikosCommunity) for how to join us.

### Building From Source

Pull latest from repo `git pull origin master` and try `mvn clean install -Popensource`.

### Contributing

See our [contributor guidelines](CONTRIBUTING.MD) for inspiration and guidance.

### Code of conduct

See our [code of conduct](CODE-OF-CONDUCT.MD) for details and how to report violations.

### About This Repository

This GitHub project is merged with  - and updated regularly from - our internal development repository to work towards our next open source release (note: stable maintenance releases and our commercial power features are managed outside GitHub). 

IMPORTANT: we (Atomikos) don't develop on GitHub ourselves (yet) so you won't see a lot of our commits here - only refreshes when we merge + push to GitHub. That is because at least initially, the sole purpose of this project is to allow interested GitHub community members to fork and contribute useful features to what we have.

Activities here are probably higher when:

   - we prepare a new community release (milestone), and 
   - the 3 months of stabilization period after that (during which we publish lots of bug fixes)
   - (check the milestones page with due dates to get an impression...)
   
After that, we are busy upgrading our customers and on-boarding new customers so you will see less activity here. That's because most customer work is done in our private repositories.

## Special Thanks / Featured Contributors

You know how it goes, one always forgets to mention someone - but the following fine people have all delivered memorable technical contributions to this project in one way or another.

So: **thank you!** (and apologies to the superstars not mentioned here):

 - [@pascalleclercq](https://github.com/pascalleclercq) 
 - [@tkrah](https://github.com/tkrah) 
 - [@dpocock](https://github.com/dpocock) 
 - [@lorban](https://github.com/lorban) 
 - [@dandiep](https://github.com/dandiep) 
 - [@gregw](https://github.com/gregw) 
 - [@janbartel](https://github.com/janbartel)
 - [@Fungrim](https://github.com/Fungrim)
 

## License

See our [license policy page](http://www.atomikos.com/Main/WhichLicenseApplies).

## Feedback Wanted!

Do you think something's missing on this page? Please open an issue to let us know!

_Copyright (c) 2000-2019, Atomikos - all rights reserved. Visit [www.atomikos.com](http://www.atomikos.com/) for more..._
