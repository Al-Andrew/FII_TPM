package tpm.lab12;

public interface IUnboundedQueue<T> {
    public void enq(T value);
    public T deq() throws Exception;
}
