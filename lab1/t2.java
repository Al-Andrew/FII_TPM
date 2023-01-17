// Type your code here, or load an example.

/*

2. Pastrati prima varianta a programului. Modificati-l pentru a proteja accesul la contor in doua noi variante: folosind un ReentrantLock si folosind un semafor binar. Contorul trebuie sa fie 0 la finalul executiei pentru cazul n = m.
Rulati toate cele trei variante de implementare masurand timpul de rulare in milisecunde, folosind seturi egale m = n = 1, 2 si 4 thread-uri si centralizati rezultatele intr-un tabel comparativ. (1 punct)

*/
import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
 

class Program {
    private static int ITERATIONS = 100000;
    public static class WorkerInc extends Thread {

        public void run() {
            int local;
            for(int i = 0 ; i < ITERATIONS; ++i) {
                c_lock.lock();
                local = counter;
                ++local;
                counter = local;
                c_lock.unlock();
            }
        }
    }

    public static class WorkerDec extends Thread {

        public void run() {
            int local;
            for(int i = 0 ; i < ITERATIONS; ++i) {
                c_lock.lock();
                local = counter;
                --local;
                counter = local;
                c_lock.unlock();
            }
        }
    }
    
    public static ReentrantLock c_lock = new ReentrantLock();
    public static int counter = 0;
    public static void main(String args[]) throws InterruptedException {
        int m = 2;
        int n = 3;

        ArrayList<Thread> threads = new ArrayList<Thread>();
        long startTime = System.nanoTime();
        for(int i = 0; i < m ; ++i) {
            Thread t = new WorkerInc();
            t.start();
            threads.add(t);
        }
        for(int i = 0; i < n ; ++i) {
            Thread t = new WorkerDec();
            t.start();
            threads.add(t);
        }
        
        for(int i = 0 ; i < n + m ; ++i) {
            threads.get(i).join();
        }
        long endTime = System.nanoTime();
        System.out.println(counter);
        System.out.println("Time nano:\n");
        System.out.println(endTime - startTime);
    }
}