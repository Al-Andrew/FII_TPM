package tmp.seminar2;


import java.util.concurrent.atomic.AtomicInteger;
/*
Se da pseudocodul de mai jos ca varianta pentru algoritmul lui Peterson de excludere mutuala
ce ofera o generalizare pentru n thread-uri. Ideea este de a trece fiecare thread printr-un filtru
de n-1 nivele pana la accesul la sectiunea critica.
 i poate fi considerat ca identificator al unui thread iar L ca numar al nivelului.
 Tabloul level asociat nivelelor retine nivelul curent pentru fiecare thread,
 iar tabloul victim retine identificatorul fiecarui ultim thread ce a avansat la respectivul nivel.

```java
lock() {
	for (int L = 1; L < n; L++) {
		level[i] = L;
		victim[L] = i;
		while (( exists k != i with level[k] >= L ) &&
			victim [L] == i ) {};
	}
}

unlock() {
	level[i] = 0;
}
```

Scrieti un program ce foloseste algoritmul lui Peterson generalizat de mai sus pentru a proteja un contor partajat.
Fiecare thread va incrementa acest contor.
In plus, fiecare thread va numara accesele proprii la contorul partajat intr-un tablou separat de dimensiune n
(fiecare thread va avea asociat un element din tablou). Thread-urile se vor opri cand contorul va atinge
valoarea limita 300000. Programul va afisa timpul total de executie, valoarea finala a contorului
si a numarului de accese per thread din tabloul mentionat mai sus.
Rulati programul cu cel putin 4 thread-uri.

Hints: In situatia in care observati durate foarte mari ale executiei, folositi o limita mai joasa
pentru contor. Pentru tablourile level si victim tipul recomandat pentru utilizare este
AtomicInteger (folosirea volatile va face doar referintele tablourilor volatile, nu si elementele acestora).
(2 puncte)

*/
import java.util.concurrent.atomic.AtomicIntegerArray;

interface CustomLock {
    void lock(int threadID);
    void unlock(int threadID);
}

final class TASLock implements CustomLock {
    AtomicInteger state = new AtomicInteger(0);

    @Override
    public void lock(int threadID) {
        while (state.getAndSet(1) == 1) {};
    }

    @Override
    public void unlock(int threadID) {
        state.set(0);
    }

}

final class PetersonLock implements CustomLock {
    AtomicIntegerArray level;
    AtomicIntegerArray victim;
    final int n; // the managed thread count

    public PetersonLock(int n) {
        this.n = n;
        this.level = new AtomicIntegerArray(n);
        this.victim = new AtomicIntegerArray(n);
    }

    public void lock(int thread) {
        for (int L = 1; L < n; L++) {
            level.set(thread, L);
            victim.set(L, thread);
            boolean exists = false;
            do {
                exists = false;
                for(int k = 0; k < n; ++k) {
                    if( k != thread  && level.get(k) >= L ) {
                        exists = true;
                        break;
                    }
                }
            }
            while (( exists ) && victim .get(L) == thread );
        }
    }

    public void unlock(int thread) {
        level.set(thread, 0);
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
        Experiment exp2 = new Experiment("TASLock", new TASLock(), tc);
        exp2.run_experiment();

    }
}
