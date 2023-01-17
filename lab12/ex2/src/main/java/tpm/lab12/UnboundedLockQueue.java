package tpm.lab12;

import java.util.concurrent.locks.ReentrantLock;

public class UnboundedLockQueue<T> implements IUnboundedQueue<T> {
    ReentrantLock enqLock, deqLock;
    Node head, tail;

    public void enq(T value) {
        enqLock.lock();
        try {
            Node newNode = new Node(value);
            tail.next = newNode;
            tail = newNode;
        } finally {
            enqLock.unlock();
        }
    }

    public T deq() throws Exception {
        T result;
        deqLock.lock();
        try {
            if (head.next == null) {
                throw new Exception();
            }
            result = head.next.value;
            head = head.next;
        } finally {
            deqLock.unlock();
        }
        return result;
    }

    public UnboundedLockQueue() {
        head = new Node(null);
        tail = head;
        enqLock = new ReentrantLock();
        deqLock = new ReentrantLock();
    }

    protected class Node {
        public T value;
        public Node next;

        public Node(T value) {
            this.value = value;
            next = null;
        }
    }
}
