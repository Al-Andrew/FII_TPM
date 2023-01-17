package tpm.lists;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        new Experiment(new CoarseList<Integer>(), "CoarseList", 2, 2).run();;
        new Experiment(new CoarseList<Integer>(), "CoarseList", 4, 4).run();;
        new Experiment(new OptimisticList<Integer>(), "CoarseList", 2, 2).run();;
        new Experiment(new OptimisticList<Integer>(), "CoarseList", 4, 4).run();;
        

    }
}
