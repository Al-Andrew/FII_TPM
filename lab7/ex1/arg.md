
```java
public boolean remove(T item) {

Node pred, current;
int key = item.hashCode();

lock.lock();
try {
    pred = this.head;
    curr = pred.next;
    while (current.key < key) {
    pred = current;
    current = current.next;
    }
    if (key == current.key) {  // present
        pred.next = current.next; // <- pucnct linearizare la succes
        return true;
    } else {
        // <- punct de linearizare la esec
        return false;         // not present 
    }
} finally {               // always unlock
    lock.unlock();
}
}
```

la succes punctul de linearizare corespunde liniei de cod cand este 'sters' efectiv nodul din lista
la esec punctul corespunde `return` - ului deoarece atunci are loc effectul de "fail" 