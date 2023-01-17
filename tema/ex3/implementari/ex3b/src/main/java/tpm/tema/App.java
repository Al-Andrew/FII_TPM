package tpm.tema;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;



final class ThreadId {

    private static final AtomicInteger uniqueId = new AtomicInteger(0);

    private static final ThreadLocal<Integer> uniqueNum = 
        new ThreadLocal () {
            @Override protected Integer initialValue() {	//implementarea metodei initialValue()
                return uniqueId.getAndIncrement();		//pentru a obtine valoarea dorita intr-un anume thread         
    }
    };

    public static int get() {
        return uniqueNum.get();
    }
} 

// A simple CCAS lock that statisifies the requirements to be encapsulated in the VeryShadyLock
class EncapsulatedLock implements Lock {
    AtomicInteger state = new AtomicInteger(1);

    public void lock() {
        while (true) {
            while (state.get() == 0) {}
            if (state.getAndSet(0) == 1) {
                //System.out.println(" [TID: " + ThreadId.get() + "] | Locking.");
                return;
            }
        }
    }

    public void unlock() {
        //System.out.println(" [TID: " + ThreadId.get() + "] | Unlocking.");
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
    public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }
}


class VeryShadyLock implements Lock {
    private Lock lock = new EncapsulatedLock();
    private int x, y = 0;
 
    public void lock() {
       int me = ThreadId.get();
       x = me; 
       while (y != 0) {}; 
       y = me;
       if (x != me) {
          lock.lock();
       }
    }
 
    public void unlock() {
       y = 0;
       lock.unlock();
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
    public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException {
        // TODO Auto-generated method stub
        return false;
    }
 }


final class Experiment {
    public int THREAD_COUNT;
    final public int LIMIT_VALUE = 100_000;
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
            while(contor < LIMIT_VALUE) {
                lock.lock();                
                //System.out.printf("[Worker: %d] [Tid: %d] | Locked\n", id, ThreadId.get());
                contor++;
                increments[id]++;
                //System.out.printf("[Worker: %d] [Tid: %d] | Unlocking\n", id, ThreadId.get());
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
        new Experiment("VeryShadyLock", new VeryShadyLock(), 2).run_experiment();
    }
}
