package tmp.tema;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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


final class ShadyChoice {
    private boolean getWhite = false;
    private int last = 0;

    public String choose() {
        int me = ThreadId.get();
        last = me;
        if (getWhite)
            return "white";
        getWhite = true;
        if (last == me)
            return "red";
        else
            return "black";
    }
}

final class Experiment {
    final int threadCount;
    final String expName;
    ShadyChoice choice = new ShadyChoice();

    Lock mapLock = new ReentrantLock();
    Map<String, Integer> choicesCount = new HashMap<>();

    Experiment(String name, int threadCount) {
        this.expName = name;
        this.threadCount = threadCount;
    }

    final class Worker extends Thread {

        public void run() {

            String myChoice = choice.choose();
            
            mapLock.lock();
            if(choicesCount.containsKey(myChoice)) {
                Integer currentCount = choicesCount.get(myChoice);
                choicesCount.put(myChoice, currentCount+1);
            } else {
                choicesCount.put(myChoice, 1);
            }
            mapLock.unlock();
        }
        
    }

    public void run_experiment() {
    
        long start = System.currentTimeMillis();
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Worker();
            threads[i].start();
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

        System.out.println("Experiment " + expName + " finished in " + diff + "ms.");
        choicesCount.forEach((key, value) -> {
            System.out.println("    " + key + " was chosen " + value + " times.");
        });;
    }
}

public final class App {

    public static void main(String[] args) {
        new Experiment("Shady Choice", 1000).run_experiment();
    }
}
