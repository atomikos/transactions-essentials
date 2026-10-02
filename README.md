<p align="center"><font size="+2"><strong>Atomikos TransactionsEssentials: light-weight distributed transactions
</strong></font></p>


<p align="center"><img src=".github/Atomikos_Logo_Background.png" width="360" height="270" alt="Atomikos Logo"></p>

<p align="center"><em>Community development "mirror" of <a href="https://www.atomikos.com/Main/TransactionsEssentials">atomikos.com/Main/TransactionsEssentials</a>.</em></p>

<p align="center"><font size="+1"><em><strong>New contributors welcome - help us shape transactions for the cloud!</strong></em></font></p>

<p align="center"><em>Please <a href="https://github.com/atomikos/transactions-essentials/stargazers">star this project</a> to help us grow. Thanks in advance!</em></p>

<p align="center"><em>New to distributed transactions? Start with our free <a href="https://www.atomikos.com/Blog/DistributedTransactionsTutorial">distributed transactions tutorial</a>.</em></p>




## Highlights

* **Distributed transaction management for Java** - so your Java application gets **instant reliability** without design efforts in your code.
* **Microservices support** - so you can have one global transaction spanning several services.
* **JEE compatible** - so it integrates effortlessly in your **Spring** or **Tomcat** configuration.
* **Javax and JakartaEE compatible** - so you can use the latest greatest ecosystem if you want to.
* **Light-weight**  - so your **microservices** can use it, too.
* **Embeddable in your code** - so you can **test everything in the IDE** and avoid late integration issues at deployment time.
* **OSGi support** - so you can use OSGi containers also.
* **Connection pooling for JDBC and JMS** - so you get maximum **performance**. Pooling can also be disabled per datasource if needed (e.g., for testing, debugging, serverless environments, or third party pooling delegation).
* **Built-in support for Hibernate and JPA** - so you can use your favorite persistence framework.
* **Automatic crash / restart recovery** - so your incomplete **distributed transactions are cleaned up** and your data returns to a consistent state.
* **Cloud-native design** - so your applications are ready for **deployment to your cloud**. 
* **Commercial support available** - if you're serious about transactions in your business. 

## NEW: JakartaEE support

Relevant jars are transformed into jakarta equivalents so we support both Javax and Jakarta.

## NOTE: Dual Build Dependencies (javax + jakarta)

Since 6.0 we publish both javax and jakarta variants. To keep this working, some core
dependencies are marked as `provided` and we post-process certain jars with the transformer
to produce the jakarta classifier. As a result, some modules cannot express a transitive
dependency on others (for example, `transactions-jdbc` cannot depend transitively on
`transactions-jta`) because the runtime dependency must match the chosen classifier.

If you build or run outside an application server, make sure you add the correct variant
of these APIs explicitly.

### BOMs (Recommended)

Use the product BOMs to align versions:

* **javax BOM**: `com.atomikos:transactions-essentials`
* **jakarta BOM**: `com.atomikos:transactions-essentials-jakarta`

Example usage:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.atomikos</groupId>
      <artifactId>transactions-essentials</artifactId>
      <version>${atomikos.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

Use the jakarta BOM instead if your runtime is jakarta-based:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>com.atomikos</groupId>
      <artifactId>transactions-essentials-jakarta</artifactId>
      <version>${atomikos.version}</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>
```

## NEW: Microservice Support

With microservices, the typical monolith ACID transaction becomes split into 2 or more local transactions, one in each microservice. This can quickly lead to inconsistencies - for instance if there are network timeouts or other failures: 

<p align="center"><img src=".github/inconsistency.jpg" alt="Inconsistent Microservices"></p>

Some concrete examples of possible inconsistencies:

 * For network timeouts, microservice 1 does not see the difference between a failure/rollback of microservice 2 and a commit at microservice 2. The global system state (and consistency) is undefined.
 * For retries (when microservice 1 calls microservice 2 a number of times until it does succeed) we depend on microservice 2 offering a correct implementation of an idempotent consumer. This is hard and error-prone.
 * Also for retries, microservice 1 has to remember that it needs to retry at microservice 2. This adds technical complexity to an otherwise simple scenario.
 * For failures when invoking microservice 2 there is no easy alternative but to retry (due to the above). This means you can't have microservice 1 forget about microservice 2 and try an equivalent "competing" implementation. Why? Because it just might be that the invocation of microservice 2 did actually commit.

We allow 1 global transaction, even across separate microservices and you don't have to code anything for that. In the example shown, a failed call means rollback at microservice 2, and if you want then you can safely try an alternative microservice instead (even within the same global transaction). 

This enables safer retries (if you do want to retry - that is your choice) plus the notion of a global commit.

For more information on how to fine-tune microservice transactions: get our free <a href="https://www.atomikos.com/Blog/MicroserviceTransactionPatternsBook">ebook on microservice transaction patterns</a>.

## Using TransactionsEssentials

### Getting Started

See [Getting Started](https://www.atomikos.com/Documentation/GettingStarted) for general documentation.

### Documentation

See documentation at [www.atomikos.com/Documentation](https://www.atomikos.com/Documentation/)

### Code Samples

Runnable example applications live in [`specs/examples/`](specs/examples): JTA,
Spring, Hibernate/JPA, PostgreSQL, JMS, and REST/gRPC — each a self-contained
Maven project you can build and run.

### Releases

Check [Maven Central](https://search.maven.org)


## When to Use Atomikos

Atomikos is a good fit when you need **distributed transaction coordination** across multiple resources. Use it when:

- You need **atomicity across multiple transactional resources** (e.g., DB + JMS)
- You need **JTA/XA** semantics in a Java stack
- You must **coordinate commit/rollback** across multiple databases or brokers
- You need **crash-safe recovery** of in-flight distributed transactions
- You are tired of implementing Saga logic and want **automatic distributed transactions** in a microservice architecture
- You want to drop your application server but keep **distributed transactions**
- You want **exactly-once messaging** without duplicates, retries, or lost messages
- You want to eliminate brittle **eventual consistency** and **idempotent consumer** patterns
- Your transactions involve valuable content that has a high cost if delivered twice or if lost (payments, insurance claims, expensive back-end commands)

If you only use a single database and don’t need cross-resource atomicity, a local transaction manager is usually sufficient.


## Going to production?

This edition (TransactionsEssentials) is free and open source. When you take
distributed transactions to production — especially on Kubernetes or the cloud —
you may want shared-database recovery that survives replaced nodes (LogCloud),
high availability, and commercial support with an SLA and indemnification. That's
[Atomikos ExtremeTransactions](https://www.atomikos.com/golive) — de-risk your
go-live with a free trial.


## Improvements in the commercial edition

A lot of improvements have been made in our commercial edition
(ExtremeTransactions) — see the [release notes](https://www.atomikos.com/Main/ReleaseNotes).


## Governance & Participating

### Joining

See our [Community Page](https://www.atomikos.com/Main/AtomikosCommunity) for how to join us.

### Building From Source

Pull latest from repo `git pull origin master` and try `mvn clean install -Popensource`.

### AI Agent Sessions

AI Agent sessions (like Codex or Claude Code) must read `USING_AGENTS.md` before making changes.

### Contributing

The fastest way to contribute is by starring this project. Thank you :-)

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

See our [license policy page](https://www.atomikos.com/Main/WhichLicenseApplies).

## Feedback Wanted!

Do you think something's missing on this page? Please open an issue to let us know!

_Copyright (c) 2000-2026, Atomikos - all rights reserved. Visit [www.atomikos.com](https://www.atomikos.com/) for more..._
