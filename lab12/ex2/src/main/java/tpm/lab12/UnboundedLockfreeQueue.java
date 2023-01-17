package tpm.lab12;

import java.util.concurrent.atomic.AtomicReference;

public class UnboundedLockfreeQueue<T> implements IUnboundedQueue<T> {
    private AtomicReference<Node> head;
    private AtomicReference<Node> tail;

    public UnboundedLockfreeQueue() {
        Node dummy = new Node(null);
        head = new AtomicReference<Node>(dummy);
        tail = new AtomicReference<Node>(dummy);
    }

    protected class Node {
        public T value;
        public AtomicReference<Node> next;

        public Node(T value) {
            this.value = value;
            next = new AtomicReference<Node>(null);
        }
    }

    public void enq(T value) {
        Node newNode = new Node(value);
        while (true) {
            Node last = tail.get(); // se obtine legatura la next din tail
            Node next = last.next.get(); // pentru a adauga urmatorul nod

            if (last == tail.get()) { // se reverifica daca tail s-a modificat intre timp
                // si in caz ca da se reincearca

                if (next == null) { // se verifica daca legatura la next obtinuta din tail
                    // inca refera null pentru ca un alt thread e posibil
                    // sa fi pornit o alta adaugare

                    if (last.next.compareAndSet(next, newNode)) { // se incearca aici adaugarea la tail verificand
                        // in acelasi timp daca legatura la next link este inca
                        // null la momentul adaugarii

                        tail.compareAndSet(last, newNode); // aici se incearca sa avansam pozitia tail in coada de
                        return; // unde era catre nodul adaugat
                        // in cazul in care comparatia cu pozitia tail esueaza
                        // inseamna ca un alt thread a facut o modificare a tail
                        // (in partea else de mai jos)

                    }

                } else {
                    tail.compareAndSet(last, next); // in cazul cand next nu a fost null inseamna ca:
                    // 1. un alt thread a inceput adaugarea inainte de cel
                    // curent si nu a reusit inca sa schimbe pozitia tail
                    // asa incat va fi ajutat sa termine aceasta modificare
                    // incercand mutarea in pozitia next folosind CAS, dupa
                    // care adaugarea se va incerca la iteratia urmatoare
                    // 2. tail nu se mai afla unde era - un alt thread
                    // a inceput o adaugare pe care deja a incheiat-o si
                    // a mutat tail; in acest caz operatia CAS esueaza
                    // si se va reincerca adaugarea
                }
            }
        }
    }

    public T deq() throws Exception {
        while (true) {
            Node first = head.get();
            Node last = tail.get();
            Node next = first.next.get(); // se obtine next din head pentru a extrage valoarea
            if (first == head.get()) { // se asigura ca head nu a fost schimbat intre timp;
                // in caz ca da se incearca din nou in alta iteratie

                if (first == last) { // se verifica daca head si tail au aceeasi valoare,
                    // ceea ce inseamna ca structura de coada este vida
                    if (next == null) {
                        throw new Exception();
                    }
                    tail.compareAndSet(last, next); // daca next nu este null in acest caz (coada vida)
                    // inseamna ca dupa ce head a fost extras in first
                    // alt thread a facut de asemenea acest lucru si
                    // coada a fost schimbata, deci se va reincerca in
                    // alta iteratie, dar mai intai se ajuta setarea tail
                    // la pozitia corecta similar situatiei enq

                } else {
                    T value = next.value; // in cazul in care coada nu pare vida se pregateste
                    if (head.compareAndSet(first, next)) { // returul valorii dar inainte de a returna se incearca
                        return value; // un update la head pentru a elimina primul nod;
                        // daca head a fost schimbat de un alt thread si CAS
                    } // esueaza inseamna ca valoarea de retur nu e
                      // corecta si se incearca din nou (o noua iteratie)
                }
            }
        }
    }
}
