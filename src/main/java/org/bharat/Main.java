package org.bharat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        final ReaderWriter obj = new ReaderWriter();

        final int readerCnt = 10;
        final int writerCnt = 2;

        final CountDownLatch latch = new CountDownLatch(readerCnt + writerCnt);

        final List<Thread> readers = Stream.generate(() -> new Thread(() -> {
            obj.read();
            latch.countDown();
        }, "Reader Thread")).limit(5).toList();

        final List<Thread> writers = Stream.generate(() -> new Thread(() -> {
            obj.write();
            latch.countDown();
        }, "Reader Thread")).limit(2).toList();

        readers.stream().limit(readerCnt).forEach(Thread::start);
        writers.stream().limit(writerCnt).forEach(Thread::start);

        latch.await();
    }

    private static void dinningPhilosopher() throws InterruptedException {
        final DiningPhilosopher philosophers = new DiningPhilosopher();

        final int cnt = 5;
        CountDownLatch latch = new CountDownLatch(cnt);

        for (int i = 0; i < cnt; i++) {
            final int id = i;

            new Thread(() -> {
                philosophers.behave(id);
                latch.countDown();
            }, "Thread-" + i).start();
        }

        latch.await();
    }
}