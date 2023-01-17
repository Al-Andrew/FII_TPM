package tpm.tema;


import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


interface CustomLock {
    void lock(int threadID);
    void unlock(int threadID);
}

final class PetersonLock implements CustomLock {
    volatile int[] level;
    volatile int[] victim;
    final int n; // the managed thread count


    public PetersonLock(int n) {
        this.n = n;
        this.level = new int[n];
        this.victim = new int[n];
    }

    public void lock(int thread) {
        for (int L = 1; L < n; L++) {
            level[thread] = L;
            victim[L] = thread;
            boolean exists = false;
            do {
                exists = false;
                for(int k = 0; k < n; ++k) {
                    if( k != thread  && level[k] >= L ) {
                        exists = true;
                        break;
                    }
                }
            }
            while (( exists ) && victim[L] == thread );
        }
    }

    public void unlock(int thread) {
        level[thread] = 0;
    }
}

final class FairPetersonLock implements CustomLock {
    volatile int[] level;
    volatile int[] victim;
    final int n; // the managed thread count
    Lock roundsLock = new ReentrantLock();
    volatile boolean[] rounds;


    public FairPetersonLock(int n) {
        this.n = n;
        this.level = new int[n];
        this.victim = new int[n];
        this.rounds = new boolean[n];
        for(int i = 0 ; i < n; ++i) {
            this.rounds[i] = false;
        }  
    }

    public void lock(int thread) {
        while(rounds[thread]){};
        for (int L = 1; L < n; L++) {
            level[thread] = L;
            victim[L] = thread;
            boolean exists = false;
            do {
                exists = false;
                for(int k = 0; k < n; ++k) {
                    if( k != thread  && level[k] >= L ) {
                        exists = true;
                        break;
                    }
                }
            }
            while (( exists ) && victim[L] == thread );
        }
    }

    public void unlock(int thread) {
        level[thread] = 0;

        rounds[thread] = true;
        roundsLock.lock();
        boolean should_clear = true;
        for(int i = 0 ; i < n; ++i) {
            if( rounds[i] == false)
                should_clear = false;
        }
        if(should_clear) {
            for(int i = 0 ; i < n; ++i) {
                this.rounds[i] = false;
            }    
        }
        roundsLock.unlock();
    }
}



final class Experiment {
    public int THREAD_COUNT;
    final public int LIMIT_VALUE = 300_000;
    int contor = 0;
    public CustomLock lock;
    int[] increments;
    final String expName;

    Experiment(String name, CustomLock lock, int threadCount) {
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
                lock.lock(id);
                if(contor < LIMIT_VALUE) {
                    contor++;
                    increments[id]++;
                } else {
                    lock.unlock(id);
                    break;
                }
                lock.unlock(id);
                //System.out.printf("Thead %d finished incremeting counter for the %dth time.\n", id, increments[id]);
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

        System.out.printf("============== Experiment: %s finished =================\n", expName);
        System.out.printf("Execution finished in %d miliseconds with a contor value of %d\n", diff, contor);
        for(int i = 0 ; i < THREAD_COUNT ; ++i) {
            System.out.printf("Therad %d added to counter %d times\n", i, increments[i]);
        }
    }

}


public final class App {
    public static void main(String[] args) {
        int tc = 4;
        Experiment exp1 = new Experiment("PetersonLock", new PetersonLock(tc), tc);
        exp1.run_experiment();
        Experiment exp2 = new Experiment("", new FairPetersonLock(tc), tc);
        exp2.run_experiment();

    }
}
