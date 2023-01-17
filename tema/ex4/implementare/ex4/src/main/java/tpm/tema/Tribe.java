package tpm.tema;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Tribe 
{
    final class Cook extends Thread {
        public void run() {

            refillLock.lock();
            try {

                while(tribesmenFed < tribesmenCount) {
                    
                    if(cauldronContains == 0) {
                        cauldronContains = cauldronCapacity; // Umplem ceaunul
                        System.out.println("Cook | Sending cauldronRefilled.");
                        cauldronRefilled.signalAll();
                    } else {                    
                        while(cauldronContains >= 1 && tribesmenFed < tribesmenCount) {
                            System.out.println("Cook | Awaiting for cauldronNeedsRefill");
                            cauldronNedsRefill.awaitNanos(500);
                        }
                    }
                }
            } catch(InterruptedException e) {
                e.printStackTrace();
            } finally {
                refillLock.unlock();
            }
        }
    }

    final class Tribesman extends Thread {
        public void run() {
            cauldronLock.lock();
            System.out.printf(" %d/%d | Aquired lock \n", tribesmenFed, tribesmenCount);
            try {
                
                while(true) 
                    if(cauldronContains >= 1) {
                        cauldronContains -= 1; //Mancam
                        tribesmenFed += 1; //Dupa ce am mancat ne trecem pe tally
                        System.out.printf(" %d/%d | Ate\n", tribesmenFed, tribesmenCount);
                        break;
                    }
                    else { // Daca nu este de mancare dam comanda bucatarului
                        refillLock.lock();
                        System.out.printf(" %d/%d | Sending cauldronNeedsRefill\n", tribesmenFed, tribesmenCount);
                        cauldronNedsRefill.signal();
                        while (cauldronContains < 1 ) {
                            System.out.printf(" %d/%d | Waiting for cauldronRefilled\n", tribesmenFed, tribesmenCount);
                            cauldronRefilled.await(); // Asteptam ca bucataurl sa umple ceaunul
                        }
                        refillLock.unlock();
                    }
                
            } catch(InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.printf(" %d/%d | Ulocking \n", tribesmenFed, tribesmenCount);
                cauldronLock.unlock();
            }
        }
    }


    final int cauldronCapacity;
    int cauldronContains;
    final int tribesmenCount;
    int tribesmenFed;
    
    Lock cauldronLock = new ReentrantLock();

    Lock refillLock = new ReentrantLock();
    Condition cauldronNedsRefill = refillLock.newCondition();
    Condition cauldronRefilled = refillLock.newCondition();

    Tribe(int cauldronSize, int tribesmenCount) {
        this.cauldronCapacity = cauldronSize;
        this.cauldronContains = cauldronSize; // Am presupus ca incepem cu ceaunul pllin :^)
        this.tribesmenCount = tribesmenCount;
    }

    void simulate_feeding() {
        List<Thread> threads = new ArrayList<>();
        
        long start = System.currentTimeMillis();
        Thread cook = new Cook();
        cook.start();
        threads.add(cook);


        for(int i = 0; i < tribesmenCount; ++i) {
            Thread trimesman = new Tribesman();
            trimesman.start();
            threads.add(trimesman);
        }
        for(int i = 0; i < tribesmenCount; ++i) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        long time = end - start;

        System.out.printf("Feeding simulation finished in %dms.\n", time);
        System.out.printf("    Tribesmen: %d\n", tribesmenCount);
        System.out.printf("    CauldronCapacity: %d\n", cauldronCapacity);
        System.out.printf("    CauldronContains: %d\n", cauldronContains);
    }


    public static void main( String[] args )
    {
        Tribe tr = new Tribe(10, 55);
        tr.simulate_feeding();
    }
}
