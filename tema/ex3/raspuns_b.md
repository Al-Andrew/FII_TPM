```java
 1|  class VeryShadyLock implements Lock {
 2|      private Lock lock = new EncapsulatedLock();
 3|      private int x, y = 0;
 4|   
 5|      public void lock() {
 6|         int me = ThreadId.get();
 7|         x = me; 
 8|         while (y != 0) {}; 
 9|         y = me;
10|         if (x != me) {
11|            lock.lock();
12|         }
13|      }
14|   
15|      public void unlock() {
16|         y = 0;
17|         lock.unlock();
18|      }
19|  }
```

Fie trei thread-uri: A, B, C care partajeaza accesul la o resursă cu ajutorul `VeryShadyLock`.
Să zicem că A apelează primul `lock()`. Thread-urile B si C sunt blocate la linia 8.
Dupa ce A face `unlock()` B va ieși din loop și va prelua lacătul. A face imediat un apel `lock()` și este blocat împreuna cu C la linia 8.
Când B va face `unlock()` nimic nu-l oprește pe A să fie următorul care va prelua lacătul.

Am ajuns intr-o situatie in care A a facut preluat de 2 ori lacatul, B 1 data iar C de 0 ori => `lacătul nu este fair`.