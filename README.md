# Spring Day 2 exercises: starter project

One Maven project for all hands-on exercises of Day 2 (Spring Boot 3.3, Java 17+, Spring Data JPA, H2).
Every exercise has its own package, and every exercise has tests that describe the result you are aiming for.

## How to work

1. Import the project as a Maven project in your IDE (or run `mvn test` on the command line).
2. Open the test class of the exercise, run it, and read what fails: a failing test tells you what is still missing.
3. Make the change in the service class, run the tests again, and read the console.
4. The tests run in the order of the steps (`step1_`, `step2_`, ...).

### Reading the console

The console is part of the exercise, but it is busy: every call to a repository opens and commits a transaction of its own,
and the setup of each test does that several times. Two things help:

- Every test prints a line `---- setup done: the test starts here ----`. Start reading **below** that line.
- Look for the lines that start with `[persistence]`, `[transactions]` or `[messaging]`, or search for the name of the method that
  you are working on (for example `CustomerService.changeEmail`). At the end of every test a line starts with `after the test:`: it
  tells you what is in the tables (and, in the messaging exercise, what was sent), so you do not need a database console.

The lines that matter for the exercises are: the SQL (`Hibernate: ...`) and the transaction lines
(`Creating new transaction`, `Participating in existing transaction`, `Suspending current transaction`, `Initiating transaction commit`).

There is no `@Transactional` on any test class: every call to a service or repository runs in its own transaction,
exactly as in your own application.

## The exercises

| Slides | Package | Test class | What you change |
|---|---|---|---|
| Topic 1: Spring Data JPA & the persistence context | `persistence` | `PersistenceContextTest`, `PersistenceBonusTest` | `CustomerService` |
| Topic 2: Transactions in Spring Data JPA | `transactions` | `TransactionsTest`, `TransactionsBonusTest` | `OrderService`, `AuditService` |
| Topic 4: Asynchronous integration & transactions | `messaging` | `MessagingTest`, `MessagingBonusTest` | `OrderService`, `OrderMessagePublisher`, `OutboxPublisher`, `OrderListener` |

The exercises are numbered after the topics in the slides: 1, 2 and 4. Topic 3 (data consistency in practice) is a discussion and has no code.
The package `shop` holds the `Order` entity that the other two exercises share. Both `transactions` and `messaging` have an `OrderService`:
they are different classes.

In the tests, `annId` is "customer 1" (Ann) and `bobId` is "customer 2" (Bob). The real ids are not 1 and 2: the sequence keeps counting.

## What you should see when you start

Some tests are red on purpose: they describe a result that you have to create. Some are green on purpose: they show you a
behaviour that you have to observe and explain.

| Test | At the start | Turns green after |
|---|---|---|
| `PersistenceContextTest.step1_dirtyChecking_theNewNameIsStoredWithoutSave` | red | step 1 |
| `PersistenceContextTest.step2_detached_theChangeIsNotStored` | green | no code: run it, and explain what you see |
| `PersistenceContextTest.step3_duplicateEmail_whereDoesTheExceptionAppear` | red | step 3, first run. Do the second run (with `flush()`) anyway |
| `PersistenceBonusTest` | red | the bonus |
| `TransactionsTest.step1_observe_requiredJoinsTheTransaction` | green | no code: observe. It breaks in step 2 and is green again after it |
| `TransactionsTest.step2_checkedException_theTransactionMustRollBack` | red | step 2 |
| `TransactionsTest.step3_failingAudit_theOrderIsStillSaved` | red | step 3 |
| `TransactionsTest.step4_requiresNew_theAuditSurvivesTheRollback` | red | step 3: no code of its own, read the log |
| `TransactionsBonusTest` | red | the bonus |
| `MessagingTest.step1_2_aRollbackMustNotSendAMessage` | red | step 2 |
| `MessagingTest.step2_aCommitSendsTheMessage` | green | it must stay green |
| `MessagingTest.step3_brokerDown_theOrderExistsWithoutAMessage` | red | step 2: no code of its own, read the log |
| `MessagingTest.step4_aRollbackLeavesNothingInTheOutbox` | green | it must stay green |
| `MessagingTest.step4_orderAndOutboxMessageAreSavedTogether` | red | step 4 |
| `MessagingTest.step4_publisherKeepsTheMessageWhileTheBrokerIsDown` | red | step 4 |
| `MessagingBonusTest` | red | the bonus |

Work through the steps in order. A step can break an earlier test on the way (for example, a checked exception that does not
roll back): that is part of the lesson.

## Things to know

- The database is H2 in memory: it starts empty every time, and `spring.jpa.hibernate.ddl-auto` creates the tables.
- The broker in the messaging exercise is a fake (`FakeMessageBroker`): it records what leaves the application, and
  `setFailing(true)` simulates a broker that is down. No RabbitMQ is needed. In the slides you see `rabbit.convertAndSend` and
  `AmqpException`: in this project that is `broker.send` and `BrokerUnavailableException`. Spring AMQP itself is a separate topic.
- In a real application `OutboxPublisher.publishPending()` runs on a schedule (`@Scheduled`), and `OrderListener.handle()` is a
  message listener. Here the tests call them directly, so that nothing runs in the background while you debug.
- On a recent Java (for example 25) Maven and Mockito print warnings such as `A Java agent has been loaded dynamically` or about
  `sun.misc.Unsafe`. They are harmless.
