package tpm.lab12;

public class App 
{
    public static void main( String[] args )
    {
        new Experiment("LockfreeQueue", new UnboundedLockfreeQueue<Integer>(), 2, 2).run_experiment();
        new Experiment("LockfreeQueue", new UnboundedLockfreeQueue<Integer>(), 4, 4).run_experiment();
        new Experiment("LockQueue", new UnboundedLockQueue<Integer>(), 2, 2).run_experiment();
        new Experiment("LockQueue", new UnboundedLockQueue<Integer>(), 4, 4).run_experiment();
    }
}
