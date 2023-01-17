package tpm.lab12;

import java.util.Random;

public class Experiment {
    final int LIMIT_VALUE = 100_000;
    volatile int enqs_total = 0;
    volatile int deqs_total = 0;
    IUnboundedQueue<Integer> bq;
    String expName;
    final int enqers;
    final int dequers;
    int[] eqs;
    int[] deqs;

    public Experiment(String name, IUnboundedQueue<Integer> bq, int enqers, int deqers) {
        this.expName = name;
        this.bq = bq;
        this.enqers = enqers;
        this.dequers = deqers;
        this.eqs = new int[enqers];
        this.deqs = new int[deqers];
    }

    static final Random rng = new Random();
    static private Integer getRandom() {
        return rng.nextInt(1,100);
    }

    final class Enqer extends Thread {
        final int id;
        public Enqer(int id) {
            this.id = id;
            eqs[id] = 0;
        }

        public void run() {
            for(int i = 0; i < LIMIT_VALUE; ++i) {
                bq.enq(getRandom());
                enqs_total++;
                eqs[id]++;
            }
        }
    }
    
    final class Deqer extends Thread {
        final int id;
        public Deqer(int id) {
            this.id = id;
            deqs[id] = 0;
        }

        public void run() {
            for(int i = 0; i < LIMIT_VALUE - 10;) {
                try {
                    Integer _unused = bq.deq();
                    ++i;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                }
                deqs_total++;
                deqs[id]++;
            }
        }
    }

    public long run_experiment() {
        long start = System.currentTimeMillis();
        int tc = enqers + dequers;
        Thread[] threads = new Thread[tc];
        for(int i = 0; i < enqers; ++i) {
            threads[i] = new Enqer(i);
            threads[i].start();
        }
        for(int i = enqers; i < tc; ++i) {
            threads[i] = new Deqer(i - enqers);
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

        System.out.printf("============== Experiment: %s finished | Ran with %d enqerss and %d deqers =================\n", expName, enqers, dequers);
        System.out.printf("Execution finished in %d miliseconds\n", diff);
        
        System.out.printf("Total enqs: %d\n", enqs_total);
        for(int i = 0 ; i < enqers ; ++i) {
            System.out.printf("Enqer %d operated %d times\n", i, eqs[i]);
        }

        System.out.printf("Total deqs: %d\n", deqs_total);
        for(int i = 0; i < dequers; ++i) {
            System.out.printf("Deqer %d operated %d times\n", i, deqs[i]);
        }

        return diff;
    }
}