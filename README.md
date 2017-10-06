# Problem
I have some resources that I compute, at runtime. Generating each of them is time-consuming (CPU intensive), and I want to have a few of them generated beforehand because eventually, I will have to hand these to users of the application.

For the purposes of this example, I think it's safe to assume that I'm generating 2048 bit RSA public/private key pairs. This is representative of something that takes a significant amount of time to compute.

Say I want to generate 1000 of these keys before I start serving requests. We can think that I will have a warm-up time available so we can generate enough of these keys.

The idea is to minimize the warm up time by using multiple threads to generate these keys, but I would like to be able to control the number of threads assigned to the computation task.

For example, I will deploy the application on a server that has 48 cores. I would like to be able to use say, 80% of the available cores (or at least to start as many threads) if my current queue has less than 50% of its capacity. But after I have 50% of my cache filled up, then I would like to decrease to just using a number of threads equal to 50% of available cores.

And eventually, when the queue is filled 90%, I would like to dial down the generation to just a number of threads equal to 10% of the available cores.

The goal is also to eventually if the queue starts to get depleted, I will again ramp up the number of worker threads to try to catch up with the demand.

# Planned solution
The queue will be a [BlockingQueue\<String>](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html), that will hold 