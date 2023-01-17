package tpm.lab11;



final class Experiment {
    final int LIMIT_VALUE = 100_000;
    volatile int enqs_total = 0;
    volatile int deqs_total = 0;
    BQ<Integer> bq;
    String expName;
    final int enqers;
    final int dequers;
    int[] eqs;
    int[] deqs;

    public Experiment(String name, BQ<Integer> bq, int enqers, int deqers) {
        this.expName = name;
        this.bq = bq;
        this.enqers = enqers;
        this.dequers = deqers;
        this.eqs = new int[enqers];
        this.deqs = new int[deqers];
    }


    final class Enqer extends Thread {
        final int id;
        public Enqer(int id) {
            this.id = id;
            eqs[id] = 0;
        }

        public void run() {
            for(int i = 0; i < LIMIT_VALUE; ++i) {
                bq.enq(enqs_total);
                if(expName == "BoundedQueueSpin2")
                    System.out.printf("E%d | Enq-ing %d | val: %d\n", id, i, enqs_total);
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
            for(int i = 0; i < LIMIT_VALUE - 10; ++i) {
                int t = bq.deq();
                if(expName == "BoundedQueueSpin2")
                    System.out.printf("D%d | Deq-ing %d | val: %d\n", id, i, t);
                deqs_total++;
                deqs[id]++;
            }
        }
    }

    public long run_experiment() {
        long start = System.currentTimeMillis();
        int tc = enqers + dequers;
        Thread[] threads = new Thread[tc];
        System.out.println("Starting enqers");
        for(int i = 0; i < enqers; ++i) {
            threads[i] = new Enqer(i);
            threads[i].start();
        }
        System.out.println("Starting deqers");
        for(int i = enqers; i < tc; ++i) {
            threads[i] = new Deqer(i - enqers);
            threads[i].start();
        }
        System.out.println("Waiting for joins");
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
        System.out.printf("Items in q : %d\n", bq.size());


        return diff;
    }
}

public class App 
{
    public static void main( String[] args )
    {
        long te1 = new Experiment("BoundedQueueBlock", new BoundedQueueBlock<Integer>(1000), 2, 2).run_experiment();
        // long te2 = new Experiment("BoundedQueueSpin", new BoundedQueueSpin<Integer>(1000), 2, 2).run_experiment();
    }
}
