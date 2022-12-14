# Building Scalable Distributed Systems
## Client Design

[GitHub Repo](https://github.com/thisIsGloriaWu/DistributedSystem)

### Part 1:

In part 1, I implemented `MultiThreadClient` with three major classes: `Event`, `DataGenerator` and `Producer`.

![Part1 Major Classes](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a1_jiayuewu/src/main/java/client/part1/part1.png)

In `MultiThreadClient`, the process of generating and sending requests follows the **“Producer/Consumer” model**. The producer is in charge of generating events and serving these events to a queue, where multiple threads, that are also consumers, take events from.

---
Class `Event`:

- has five variables. They accord with the five parameters used in method `writeNewLiftRideWithHttpInfo()` from `SkiersApi`. 
- These variables are generated by `DataGenerator` using `ThreadLocalRandom.current().nextInt()`.
---
Class `Producer`: 

- takes `size` and `queue` as its two variables. 
- `size` is specifically 200K in assignment 1. `Producer` uses it to create same value's requests. This is designed as **the solution to precisely calculate the numbers of requests to send**. 
- `queue` is implemented as a `LinkedBlockingQueue` 
  - to deal with **race condition problems** caused by multiple threads.
  - a trade-off in using `LinkedBlockingQueue` instead of other data structures like `CopyOnWriteArrayList`. In order to consume **as little memory as possible**, I use `BlockingQueue` to avoid keeping making copies of the underlying array. Besides, `LinkedBlockingQueue` has higher throughput than array-based queues([LinkedBlockingQueue](https://docs.oracle.com/en/java/javase/16/docs/api/java.base/java/util/concurrent/LinkedBlockingQueue.html)).
- In `run()` method, producer generates an event then adds it to the queue. 
- In `serve()` method, producer polls an event from queue and return it.
---
Class `MultiThreadClient`:

- `main()` method
  - start producer and consumers, sends requests to the AWS server and prints out results.
  - Consumers(multiple threads) are implemented by `ThreadPoolExecutor`.
  - When any of the 32 threads finishes its tasks, the executor extends its pool size to 256 threads and let each thread send 100 requests.
  - set `Producer` with **MAX_PRIORITY** since it serves on a single dedicated thread. Otherwise, consumers may wait too long for taking an event, which decreases the throughput.

- `sendRequest()` as a helper method to send requests, get responses and handle errors.


### Part 2:
The majority content of `MultiThreadClient2` is the same as Part 1. The different thing is that I have implemented another `LatencySaver` class to record response time and write into CSV files.

![Part 2 MultiThreadClient2 and LatencySaver](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a1_jiayuewu/src/main/java/client/part2/part2.png)

It applies to the **"Producer/Consumer" Model** as well. During the process of getting response time and writing into CSV files, threads in `MultiThreadClient2` play the role of producers, and the `LatencySaver` is the single consumer.

Class `LatencySaver`:

- has three variables: 
  - `size`: values 200K in this assignment. Used to calculate total number of responses to be written.
  - `latencyQueue`: a `LinkedBlockingQueue` to receive responses from multiple threads.
  - `responseTime`: a `List` to collect response time of each request.

- `receive()` method: convert requests' information into `String[]` and push it into the queue/list.

- set with MIN_PRIORITY in `MultiThreadClient2`. Since writing into CSV file is **a "promised" task**, lower the priority of writing could avoid throughput decrease when multiple threads wait to write responses.


### Comparison
- According to the Little's Law, the actual and theoretical results of a single thread's sending 10K requests is similar.
![Part 3 Little's Law test results](https://github.com/thisIsGloriaWu/DistributedSystem/blob/main/6650_a1_jiayuewu/src/main/java/client/test.PNG)

- By default, a **Spring/SpringBoot** server is deployed on the EC2 instance for testing.

- Both client configurations are optimized for **highest throughput**, not **latency**.

### Summary:
- implement client with a **"Producer/Consumer" Model**.

- use `ThreadPoolExecutor` to create multiple threads.

- use variable `size` to calculate requests' number, avoid using `AtomicInteger` to slow down the process.

- use `LinkedBlockingQueue` to deal with race condition problems.

- set different **thread priorities** to increase throughput.

- batch 100 requests at a time to make the most of each thread.

- deploy a **Spring/SpringBoot** server.
