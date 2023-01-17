// Type your code here, or load an example.

/*

1. Scrieti un program care porneste n si respectiv m thread-uri de doua tipuri diferite 
conform descrierii ce urmeaza. Aceste thread-uri acceseaza un contor partajat
(initializat cu 0) intr-o bucla (100000 de iteratii). 
In fiecare iteratie thread-urile din primul tip citesc contorul intr-o variabila locala
clasei thread-ului, o incrementeaza, si stocheaza valoarea rezultata inapoi in 
contorul partajat. Thread-urile din al doilea tip realizeaza acelasi tip de
 operatii dar in loc de incrementarea contorului il decrementeaza. 
 Cand toate thread-urile finalizeaza operatiile, programul va afisa valoarea
  contorului si durata executiei (incercati o rulare pentru n = m). (2 puncte)



*/
import java.util.concurrent.*;
import java.util.ArrayList;

class Program {
    private static int ITERATIONS = 100000;
    public static class WorkerInc extends Thread {

        public void run() {
            int local;
            for(int i = 0 ; i < ITERATIONS; ++i) {
                local = counter;
                ++local;
                counter = local;
            }
        }
    }

    public static class WorkerDec extends Thread {

        public void run() {
            int local;
            for(int i = 0 ; i < ITERATIONS; ++i) {
                local = counter;
                --local;
                counter = local;
            }
        }
    }

    public static int counter = 0;
    public static void main(String args[]) throws InterruptedException {
        int m = 2;
        int n = 2;

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