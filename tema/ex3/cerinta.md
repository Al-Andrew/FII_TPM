# 3.

    a. (5 puncte) O echipa de programatori a dezvoltat algoritmul de lock prezentat in pseudocodul urmator. ThreadId se considera a fi o clasa ce furnizeaza un id unic pozitiv fiecarui thread.
    Grupele de la seria A: Intr-o executie concurenta a n > 1 thread-uri, este acest algoritm starvation-free? Argumentati.
    Grupele de la seriile B si E: Intr-o executie concurenta a n > 1 thread-uri, este acest algoritm deadlock-free? Argumentati.

```java
class ShadyLock {
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
}
```
    In argumentatie se pot include si eventuale trace-uri demonstrative pentru executia unor thread-uri sau optional o implementare exemplificativa daca este cazul.
---
    b. (5 puncte) O alta echipa de programatori a dezvoltat algoritmul de lock prezentat in pseudocodul urmator ce incapsuleaza un alt lock oarecare. Se considera ca lock-ul incapsulat asigura corect excluderea mutuala si este starvation-free. De asemenea lock-ul incapsulat permite un apel unlock fara exceptie si fara efect chiar daca nu a existat un apel lock. ThreadId se considera a fi o clasa ce furnizeaza un id unic pozitiv fiecarui thread.
    Grupele de la seria A: Intr-o executie concurenta a n > 1 thread-uri, asigura acest algoritm excluderea mutuala? Argumentati.
    Grupele de la seriile B si E: Intr-o executie concurenta a n > 1 thread-uri, asigura acest algoritm o garantie de fairness (niciun thread nu poate accesa o sectiune critica protejata de lock mai des ca altele)? Argumentati.

```java
class VeryShadyLock {
   private Lock lock;
   private int x, y = 0;

   public void lock() {
      int me = ThreadId.get();
      x = me;
      while (y != 0) {};
      y = me;
      if (x != me) {
         lock.lock();
      }
   }

   public void unlock() {
      y = 0;
      lock.unlock();
   }
}
```

    In argumentatie se pot include si eventuale trace-uri demonstrative pentru executia unor thread-uri sau optional o implementare exemplificativa daca este cazul.

---
    c. (5 puncte) O a treia echipa de programatori s-a plictisit de programat lacate, si a implementat clasa din pseudocodul urmator. Consideram un context de executie concurenta a n > 1 thread-uri, in care fiecare thread apeleaza metoda choose. ThreadId se considera a fi o clasa ce furnizeaza un id unic pozitiv fiecarui thread.
    Grupele de la seria A: Demonstrati ca maxim un thread poate obtine valoarea "red" si maxim n-1 thread-uri pot obtine valoarea "black".
    Grupele de la seriile B si E: Demonstrati ca maxim un thread poate obtine valoarea "red" si maxim n-1 thread-uri pot obtine valoarea "white".

```java
class ShadyChoice {
   private boolean getWhite = false;
   private int last = 0;

   public string choose() {
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
```

    In argumentatie se pot include si eventuale trace-uri demonstrative pentru executia unor thread-uri sau optional o implementare exemplificativa daca este cazul.

