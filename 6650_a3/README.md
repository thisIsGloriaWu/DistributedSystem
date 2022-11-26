# Building Scalable Distributed System - Adding Persistence

## Database Design
In class `ConsumerMain` in package `consumer`, instead of recording users' data by hashmap, the consumer will write data into **Redis** database.

- utilizes `lettuce` among Redis Java clients to guarantee **thread safety**.
- uses `redis hashes` to write users' data into database. In this case, `skierId` is the key and `resortId` and `liftId` are its values.
- provides extensibility: just add more field-value pairs if is required to store more information.


## Deployment Topologies
![deployment](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a3/test%20results/deployment.PNG)

- Client on local machine: the client that sends 200k requests to servlets.
- `6650_servlet_0` on AWS Linux EC2 instance: the servlet to receive POST requests from client. **Only deploys one servlet due to budget**.
- `RMQ` on Ubuntu instance: the remote queue using `RabbitMQ` to store messages. Messages are posted from servlets and consumed by consumers.
- `consumer` on AWS Linux EC2 instance: consumers that consume messages and write data into Redis database.
- `Redis` on AWS Linux EC2 instance: Redis database that gets data from consumers.


## Results

The best performance with **one servlet** and **four consumers**:
![performance](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a3/test%20results/client.PNG)

- Uses only one servlet due to budget, which is the temporary **bottleneck** of this system. Could highly increase throughput if adding more servlets with load balancers.
- Uses four consumers to retrieve messages from remote queue. As shown in the screenshot below, there's hardly a message in the queue since it is immediately consumed.

![RMQ](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a3/test%20results/RMQ.PNG)



## Trade-offs: Redis vs. MongoDB

### Recommend options:

== Redis ==

=== Architecture Diagram ===

Redis stores data in a key-value format. It is an in-memory and single-threaded datastore.

![Redis](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a3/redis-architecture.png)

=== Pros ===

- Speed: Redis is faster than MongoDB since it's an in-memory database.
- Availability: Redis has Master Slave architecture. If a Master fails then Redis Sentinels promote a Slave to be the new Master, making the entire solution highly available.
- Tenacity: Redis provides flexibility for storing various data types in the main memory.

=== Cons ===

- Consistency: Redis does not guarantee strong consistency since it may lose data in case of a failure.
- Cost: Redis, an in-memory database, uses more RAM than MongoDB for non-trivial data sets. RAM is a lot more expensive than disk storage and therefore not cost-effective for large data sets.
- Backups: Redis Cluster does not offer cross-shard backups. Each shard must be backed up individually.

=== Tradeoffs ===

Redis trades consistency off for higher availability.

### Alternatives

== MongoDB ==

=== Architecture Diagram ===

a document-oriented database:

![MongoDB](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a3/mongodb-architecture.png)

=== Pros ===

- Scalability: MongoDB has a horizontal scale-out architecture. Horizontal scaling is achieved with sharding which allows to distribute data across multiple nodes.
- Consistency: MongoDB offers strong data safety and consistency.
- Backups: MongoDB Atlas has consistent backups with point-in time recovery.

=== Cons ===

- Performance: slower than Redis
- Functions: MongoDB does not support joins.
- Limitations: MongoDB has the limitations of document sizes and document nesting.

=== Tradeoffs ===

MongoDB trades availability off for higher consistency.

References:

[redis-vs-mongodb](https://hevodata.com/learn/redis-vs-mongodb/)

[mongodb-vs-redis](https://www.integrate.io/blog/mongodb-vs-redis/)

[mongodb-vs-redis](https://www.mongodb.com/compare/mongodb-vs-redis)

[redis-availability](https://stackoverflow.com/questions/59511275/redis-availability-and-cap-theorem)

[mongodb-pros-and-cons](https://www.thinkautomation.com/our-two-cents/understanding-the-key-mongodb-pros-and-cons/)


