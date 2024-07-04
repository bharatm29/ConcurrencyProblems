package org.bharat;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.stream.Stream;

public class DiningPhilosopher implements UselessInterface {
    private final Random random;
    private final List<Semaphore> mutexes;
    private final int N = 5;

    public DiningPhilosopher() {
        this.random = new Random();

        this.mutexes = Stream.generate(() -> new Semaphore(1))
                .limit(N)
                .toList();
    }

    public void behave(final int i) {
        try {
            mutexes.get(left_fork(i)).acquire();
            mutexes.get(right_fork(i)).acquire();

            System.out.printf("Philosopher-%d acquired both forks: [%d, %d]\n", i, left_fork(i), right_fork(i));

            { // critical section
                final int rand = random.nextInt(1000);

                System.out.printf("Philosopher-%d do be eating shit for %d\n", i, rand);

                Thread.sleep(rand);
                System.out.printf("Philosopher-%d finished eating\n", i);
            }

            mutexes.get(left_fork(i)).release();
            mutexes.get(right_fork(i)).release();
            System.out.printf("Philosopher-%d finished everything\n", i);
        } catch (InterruptedException e) {
            throw new RuntimeException("Couldn't acquire mutex at: " + i);
        }
    }

    private int left_fork(final int i) {
        return (i - 1 + N) % N;
    }

    private int right_fork(final int i) {
        return (i) % N;
    }
}