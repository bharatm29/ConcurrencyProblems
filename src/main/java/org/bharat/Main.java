package org.bharat;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        compileClass();

        ClassLoader parentClassLoader = ReloadClassLoader.class.getClassLoader();
        ReloadClassLoader classLoader = new ReloadClassLoader(parentClassLoader);
        var clazz = classLoader.loadClass("org.bharat.DiningPhilosopher");
        var obj = (UselessInterface) clazz.getConstructor().newInstance();

        dinningPhilosopher(obj);

        System.out.println("BEFORE COMPILING");

        Scanner scanner = new Scanner(System.in);

        if (scanner.next() != null) {
            compileClass();

            //create new class loader so classes can be reloaded.
            classLoader = new ReloadClassLoader(parentClassLoader);
            clazz = classLoader.loadClass("org.bharat.DiningPhilosopher");

            obj = (UselessInterface) clazz.getConstructor().newInstance();
            dinningPhilosopher(obj);
            System.out.println("AFTER COMPILING");
        }
    }

    private static void compileClass() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        try (StandardJavaFileManager mgr = compiler.getStandardFileManager(null, null, null)) {
            File file = new File("/home/bharat/ProgramFiles/learning_java/ConcurrenyProblems/src/main/java/org/bharat/DiningPhilosopher.java");

            Iterable<? extends JavaFileObject> sources = mgr.getJavaFileObjectsFromFiles(List.of(file));
            CompilationTask task = compiler.getTask(null, mgr, null, null, null, sources);
            task.call();
        }
    }

    private static void readerWriter() throws InterruptedException {
        final ReaderWriter obj = new ReaderWriter();

        final int readerCnt = 10;
        final int writerCnt = 2;

        final CountDownLatch latch = new CountDownLatch(readerCnt + writerCnt);

        final List<Thread> readers = Stream.generate(() -> new Thread(() -> {
            obj.read();
            latch.countDown();
        })).limit(10).toList();

        final List<Thread> writers = Stream.generate(() -> new Thread(() -> {
            obj.write();
            latch.countDown();
        })).limit(2).toList();

        readers.stream().limit(readerCnt).forEach(Thread::start);
        writers.stream().limit(writerCnt).forEach(Thread::start);

        latch.await();
    }

    private static void dinningPhilosopher(final UselessInterface philosophers) throws InterruptedException {
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