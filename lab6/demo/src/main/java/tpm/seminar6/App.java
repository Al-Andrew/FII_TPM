package tpm.seminar6;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * RWLock
 */
interface RWLock {
    public Lock readLock();
    public Lock writeLock();
}

class ReentrantReadWriteLock implements RWLock {
    Lock lock = new ReentrantLock();

    @Override
    public Lock readLock() {
        return lock;
    }

    @Override
    public Lock writeLock() {
        return lock;
    }
}

class ReworkedReadWriteLock implements RWLock {
    AtomicInteger readers = new AtomicInteger(0);
    AtomicInteger writers = new AtomicInteger(0);
    ReentrantLock lock = new ReentrantLock();
    WriteLock writerLock = new WriteLock();
    ReadLock readerLock = new ReadLock();

    @Override
    public Lock readLock() {
        return readerLock;
    }

    @Override
    public Lock writeLock() {
        return writerLock;
    }

    public class ReadLock implements Lock {

        @Override
        public void lock() {
            while( writers.get() != 0 );
            readers.incrementAndGet();
        }

        @Override
        public void unlock() {
            readers.decrementAndGet();
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // TODO Auto-generated method stub

        }

        @Override
        public Condition newCondition() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean tryLock() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            // TODO Auto-generated method stub
            return false;
        }


    }

    public class WriteLock implements Lock {

        @Override
        public void lock() {
            writers.incrementAndGet();
            while(readers.get() != 0);
            lock.lock();
        }

        @Override
        public void unlock() {
            lock.unlock();
            writers.decrementAndGet();
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // TODO Auto-generated method stub

        }

        @Override
        public Condition newCondition() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean tryLock() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            // TODO Auto-generated method stub
            return false;
        }


    }

}

class SimpleReadWriteLock implements RWLock {

    int readers;
    boolean writer;
    Lock helperLock;
    Lock readLock;
    Lock writeLock;
    Condition condition;

    public SimpleReadWriteLock() {
        writer = false;
        readers = 0;
        helperLock = new ReentrantLock();
        readLock = new ReadLock();
        writeLock = new WriteLock();
        condition = helperLock.newCondition();
    }

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }

    protected class ReadLock implements Lock {

        public void lock() {
            helperLock.lock();
            try {
                while (writer) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
                readers++;
            } finally {
                helperLock.unlock();
            }
        }

        public void unlock() {
            helperLock.lock();
            try {
                readers--;
                if (readers == 0)
                    condition.signalAll();
            } finally {
                helperLock.unlock();
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // TODO Auto-generated method stub

        }

        @Override
        public Condition newCondition() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean tryLock() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            // TODO Auto-generated method stub
            return false;
        }

    }

    protected class WriteLock implements Lock {

        public void lock() {
            helperLock.lock();
            try {
                while (readers > 0 || writer) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                    }
                }
                writer = true;
            } finally {
                helperLock.unlock();
            }
        }

        public void unlock() {
            helperLock.lock();
            try {
                writer = false;
                condition.signalAll();
            } finally {
                helperLock.unlock();
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // TODO Auto-generated method stub

        }

        @Override
        public Condition newCondition() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean tryLock() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            // TODO Auto-generated method stub
            return false;
        }

    }
}

final class Experiment {
    final private int READER_COUNT;
    final private int WRITER_COUNT;
    final static private int LOOP_VALUE = 100_000;
    int contor = 0;
    public RWLock lock;
    int[] write_accesses;
    int[] read_accesses;
    final String expName;

    Experiment(String name, RWLock lock, int writerCount, int readerCount) {
        this.expName = name;
        this.lock = lock;
        READER_COUNT = readerCount;
        WRITER_COUNT = writerCount;
        write_accesses = new int[writerCount];
        read_accesses = new int[readerCount];
    }

    final class Writer extends Thread {
        public int id;
        public Writer(int id) {
            this.id = id;
        }

        public void run() {
            while (true) {
                lock.writeLock().lock();
                if (write_accesses[id] < LOOP_VALUE) {
                    contor++;
                    write_accesses[id]++;
                } else {
                    lock.writeLock().unlock();
                    break;
                }
                lock.writeLock().unlock();
            }
        }
    }

    final class Reader extends Thread {
        public int id;

        public Reader(int id) {
            this.id = id;
        }

        public void run() {
            while(true) {
                lock.readLock().lock();
                read_accesses[id]++;
                if(contor == WRITER_COUNT * LOOP_VALUE) {
                    lock.readLock().unlock();
                    break;
                }
                lock.readLock().unlock();
            }
        }
    }

    public void run_experiment() {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[READER_COUNT + WRITER_COUNT];
        int threadc = 0;

        for (int i = 0; i < READER_COUNT; ++i) {
            threads[threadc] = new Reader(i);
            threads[threadc].start();
            threadc++;
        }

        for (int i = 0; i < WRITER_COUNT; ++i) {
            threads[threadc] = new Writer(i);
            threads[threadc].start();
            threadc++;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        long end = System.currentTimeMillis();
        long diff = end - start;

        System.out.printf("============== Experiment: %s finished | Ran with %d writers and %d readers =================\n", expName,
                WRITER_COUNT, READER_COUNT);
        System.out.printf("Execution finished in %d miliseconds with a contor value of %d\n", diff, contor);
        for (int j = 0; j < READER_COUNT; ++j) {
            System.out.printf("Reader %d had %d accesses\n", j, read_accesses[j]);
        }
        for (int j = 0; j < WRITER_COUNT; ++j) {
            System.out.printf("Writer %d had %d accesses\n", j, write_accesses[j]);
        }
    }
}

public final class App {

    public static void main(String[] args) {
        new Experiment("SimpleReadWriteLock", new SimpleReadWriteLock(), 4, 4).run_experiment();
        new Experiment("ReentrantReadWriteLock", new ReentrantReadWriteLock(), 4, 4).run_experiment();
        new Experiment("ReworkedReadWriteLock", new ReworkedReadWriteLock(), 4, 4).run_experiment();
    }
}
