package tpm.lab11;

public interface BQ<T> {
    public void enq(T x);
    public T deq();
    public int size();
}
