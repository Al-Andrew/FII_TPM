package tpm.lists;

public interface IList <T> {
    
    boolean add(T item);
    boolean remove(T item);
    boolean contains(T item);
    
}
