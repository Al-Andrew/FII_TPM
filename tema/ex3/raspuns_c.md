# Raspuns 3.c
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

Singura situație prin care un thread poate face alegrea `red` este dacă este mai rapid ca toate celelalte si ajunge la `if(last == me)` înainte ca vreoun alt thread sa ajungă la `last = me`. Acest thread va fi setat `getWhite = true` deci de acum in colo toate celelate thread-uri vor alege `white`. 

Dacă mai multe thread-uri trec peste `if(getWhite)` înainte ca vreunul să ajungă la `getWhite = true` maxim unul va alege `red` (cel care a trecut ultimul de `last = me`, asta daca până la acel moment nu a trecut vre-un alt thread pe acolo) iar celelalte vor alege `black`.

'white' este alegerea facută de majoritatea thread-urilor, dar ca această alegere să poată fi facută cel puțin un thread trebuie să fi trecut peste `if(getWhite)` și să fi setat `getWhite = true`. Deci vor fi cel mult `n-1` alegeri `white`.

Deci alegerile pot fi:

1 `red`, m `black` and k `white` unde m $\in$ [0, m], m < n, k = n - m - 1

sau

0 `red`, m `black` and k `white` unde m $\in$ [1, m], m < n, k = n - m