package tpm.lab4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


class TASlock implements Lock{
    AtomicBoolean state = new AtomicBoolean(false);

    public void lock() {
        while (state.getAndSet(true)) {}
    }

    public void unlock() {
        state.set(false);
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


class TTASlock implements Lock {

    AtomicBoolean state = new AtomicBoolean(false);

    public void lock() {
        while (true) {
            while (state.get()) {}
            if (!state.getAndSet(true))
                return;
        }
    }

    public void unlock() {
        state.set(false);
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



class CCASlock implements Lock {

    AtomicInteger state = new AtomicInteger(1);

    public void lock() {
        while (true) {
            while (state.get() == 0) {}
            if (state.getAndSet(0) == 1)
                return;
        }
    }

    public void unlock() {
        state.set(1);
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


final class Experiment {
    public int THREAD_COUNT;
    final public int LIMIT_VALUE = 300_000;
    int contor = 0;
    public Lock lock;
    int[] increments;
    final String expName;

    Experiment(String name, Lock lock, int threadCount) {
        this.expName = name;
        this.lock = lock;
        THREAD_COUNT = threadCount;
        increments = new int[threadCount];
    }


    final class Worker extends Thread {
        final int id;
        public Worker(int id) {
            this.id = id;
            increments[id] = 0;
        }

        public void run() {
            while(true) {
                lock.lock();
                if(contor < LIMIT_VALUE) {
                    contor++;
                    increments[id]++;
                } else {
                    lock.unlock();
                    break;
                }
                lock.unlock();
            }
        }
    }

    public void run_experiment() {
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[THREAD_COUNT];
        for(int i = 0; i < THREAD_COUNT ; ++i) {
            threads[i] = new Worker(i);
            threads[i].start();
        }
        for(Thread thread : threads) {
            try {
                thread.join();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        long end = System.currentTimeMillis();
        long diff = end - start;

        System.out.printf("============== Experiment: %s finished | Ran with %d threads =================\n", expName, THREAD_COUNT);
        System.out.printf("Execution finished in %d miliseconds with a contor value of %d\n", diff, contor);
        for(int i = 0 ; i < THREAD_COUNT ; ++i) {
            System.out.printf("Therad %d added to counter %d times\n", i, increments[i]);
        }
    }
}

public final class App {

    public static void main(String[] args) {
        new Experiment("TASLock", new TASlock(), 4).run_experiment();
        new Experiment("TASLock", new TASlock(), 8).run_experiment();
        new Experiment("TTASLock", new TTASlock(), 4).run_experiment();
        new Experiment("TTASLock", new TTASlock(), 8).run_experiment();
        new Experiment("CCASLock", new CCASlock(), 4).run_experiment();
        new Experiment("CCASLock", new CCASlock(), 8).run_experiment();
    }
}
