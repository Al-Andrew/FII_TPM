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


class ShadyLock implements Lock {
    private volatile int turn;
    private volatile boolean used = false;
 
    public void lock() {
       int me = ThreadId.get();
       do {
          do {
               turn = me;
          } while (used);
          used = true;
       } while (turn != me); 
    }
    
    public void unlock () {
       used = false;
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
        new Experiment("ShadyLock", new ShadyLock(), 4).run_experiment();
    }
}
