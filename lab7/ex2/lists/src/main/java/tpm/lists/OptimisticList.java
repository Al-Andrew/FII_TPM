package tpm.lists;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tpm.lists.Experiment.Adder;

public class OptimisticList<T> implements IList<T> {

    /**
     * First list Node
     */
    private Node head;
    /**
     * Last list Node
     */
    private Node tail;

    /**
     * Constructor
     */
    public OptimisticList() {
        // Add sentinels to start and end
        head = new Node(Integer.MIN_VALUE);
        tail = new Node(Integer.MAX_VALUE);
        head.next = this.tail;
    }

    @Override
    public boolean add(T item) {

        int key = item.hashCode();

        while(true) {
            Node pred =  head;
            Node current = pred.next;

            
            while (current.key <= key) {
                
                if (key == current.key)
                    return false;
                    
                    pred = current;
                    current = current.next;
                } 
            
                pred.lock(); current.lock();
                
            try {
                if(!validate(pred, current))
                break;
                
                Node added = new Node(item);
                pred.next = added;
                added.next = current;
                
            } finally {
                pred.unlock();
                current.unlock();
            }
        }
            
        return true;
    }
    
    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        
        while (true) {
        
            Node pred = head;
            Node current = pred.next;
            
            while (current.key <= key) {
            
                if (key == current.key)
                    break;
                
                pred = current;
                current = current.next;
            } pred.lock(); current.lock();

            try {
                if (validate(pred, current)) {
                    if (current.key == key) {   
                        pred.next = current.next;
                        return true;
                    } else {
                        return false;
                    }
                    
                }
            }finally {
                pred.unlock();
                current.unlock();
            }    
        } //end of while retrial (if validation fails)
        
    } // end of removal method

    @Override
    public boolean contains(T item) {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean validate(Node pred, Node current) {
        Node node = head;

        while (node.key <= pred.key) {
            if (node == pred)
                return pred.next == current;

            node = node.next;
        }

        return false;
    }

    /**
     * list Node
     */
    private class Node {
        /**
         * actual item
         */
        T item;
        /**
         * item's hash code
         */
        int key;
        /**
         * next Node in list
         */
        Node next;

        private Lock lock = new ReentrantLock();

        /**
         * Constructor for usual Node
         * 
         * @param item element in list
         */
        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }

        /**
         * Constructor for sentinel Node
         * 
         * @param key should be min or max int value
         */
        Node(int key) {
            this.item = null;
            this.key = key;
        }

        public void lock() {
            this.lock.lock();
        }

        public void unlock() {
            this.lock.unlock();
        }
    }
}
