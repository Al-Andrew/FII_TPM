package tpm.lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.random.RandomGenerator;
import java.util.concurrent.ThreadLocalRandom;

public class Experiment {
    
    private static final int ACCESES_PER_THREAD = 10_000;
    private IList<Integer> list;
    private final int adders;
    private int[] adders_accesses;
    private final int removers;
    private int[] removers_accesses;
    private String name;

    public Experiment(IList<Integer> listImpl, String exp_name, int adders, int removers) {
        this.list = listImpl;
        this.name = exp_name;
        this.adders = adders;
        this.removers = removers;
    }

    public void run() {
        List<Thread> threads = new ArrayList<>();
        long start = System.currentTimeMillis();
        for(int i = 0 ; i < adders; ++i) {
            Thread adder = new Adder(i);
            adder.start();
            threads.add(adder);
        }
        for(int i = 0; i < removers; ++i) {
            Thread remover = new Remover(i);
            remover.start();
            threads.add(remover);
        }
        for( Thread th : threads ) {
            try {
                th.join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        long time =  end - start;

        System.out.printf("Experiment %s finished running in %d miliseconds.\n    Adders: %d\n    Removers: %d\n", name, time, adders, removers);
    }

    class Adder extends Thread {
        int id;

        public Adder(int id) {
            this.id = id;
        }


        public void run() {
            for(int i = 0; i < ACCESES_PER_THREAD; ++i) {
                int num = ThreadLocalRandom.current().nextInt(1, 101);
                list.add(num);
            }
        }
    }

    class Remover extends Thread {
        int id;

        public Remover(int id) {
            this.id = id;
        }
        
        public void run() {
            for(int i = 0; i < ACCESES_PER_THREAD; ++i) {
                int num = ThreadLocalRandom.current().nextInt(1, 101);
                list.remove(num);
            }
        }
    }

}
