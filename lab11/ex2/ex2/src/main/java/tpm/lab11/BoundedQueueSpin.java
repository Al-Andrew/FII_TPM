package tpm.lab11;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueueSpin<T> implements BQ<T>{
    ReentrantLock enqLock, deqLock;
    AtomicInteger size;
    Node head, tail;
    int capacity;
    Condition notFullCondition, notEmptyCondition;

    public BoundedQueueSpin(int capacity) {
        this.capacity = capacity;
        this.head = new Node(null);
        this.tail = head;
        this.size = new AtomicInteger(0);
        this.enqLock = new ReentrantLock();
        this.notFullCondition = enqLock.newCondition();
        this.deqLock = new ReentrantLock();
        this.notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T x) {
        boolean mustWakeDequeuers = false;

        enqLock.lock();
        try {
            System.out.printf("Spin enq while: %d\n",  size.get());
            while (size.get() == capacity) {
            }; // spinning
            System.out.println("Spin enq af while");
            Node e = new Node(x);
            tail.next = e;
            tail = tail.next;
            size.getAndIncrement();
        } finally {
            enqLock.unlock();
        }
    }

    public T deq() {
        boolean mustWakeEnqueuers = false;
        T v = null;

        deqLock.lock();
        try {
            System.out.println("Spin denq while");
            while (head.next == null) {
            }; // spinning
            System.out.println("Spin denq af while");
            v = head.next.value;
            head = head.next;
            size.getAndDecrement();
        } finally {
            deqLock.unlock();
        }
        return v;
    }

    protected class Node {

        public T value;
        public Node next;

        public Node(T x) {
            value = x;
            next = null;
        }
    }

    @Override
    public int size() {
        return this.size.get();
    }
}
