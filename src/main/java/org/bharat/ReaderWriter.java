package org.bharat;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class ReaderWriter {
    private final Semaphore db;
    private final Semaphore reader;
    private int reader_cnt;
    private final Random random;

    ReaderWriter() {
        this.db = new Semaphore(1);
        this.reader = new Semaphore(1);
        this.random = new Random();

        this.reader_cnt = 0;
    }

    public void read() {
        final int rand = random.nextInt(1000);

        try {
            reader.acquire();

            if (++reader_cnt == 1) {
                db.acquire();
            }

            reader.release();

            System.out.printf("Reading from db for %d with %d readers\n", rand, reader_cnt);
            Thread.sleep(rand);

            if (--reader_cnt == 0) {
                db.release();
            }

            System.out.printf("Reading finished with %d readers\n", reader_cnt);
        } catch (InterruptedException _) {}
    }

    public void write() {
        final int rand = random.nextInt(1000);

        try {
            db.acquire();

            System.out.printf("Writing to db for %d\n", rand);

            Thread.sleep(rand);
            db.release();

            System.out.println("Writing finished");
        } catch (InterruptedException _) {}
    }
}
