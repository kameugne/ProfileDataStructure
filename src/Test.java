import java.lang.reflect.Array;
import java.util.Arrays;

public class Test {
    public static void main(String[] args){
        /*int C = 3;
        Task[] task = new Task[5];
        task[0] = new Task(0, 0, 4, 2, 1);
        task[1] = new Task(1, 1, 4, 1, 3);
        task[2] = new Task(2, 2, 4, 1, 3);
        task[3] = new Task(2, 2, 4, 1, 1);
        task[4] = new Task(3, 1, 10, 3, 1);*/
        /*int C = 2;
        Task[] task = new Task[4];
        task[0] = new Task(0, 5, 14, 4, 1);
        task[1] = new Task(1, 5, 8, 3, 1);
        task[2] = new Task(2, 4, 13, 2, 1);
        task[3] = new Task(3, 5, 8, 1, 1);*/
        /*int C = 3;
        Task[] task = new Task[2];
        task[0] = new Task(0, 1, 5, 4, 2);
        task[1] = new Task(1, 2, 10, 2, 2);*/
        /*
        The adjustment made by the classic edge-finder can be great than the new adjustment
         */
        /*int C = 2;
        Task[] task = new Task[4];
        task[0] = new Task(0, 1, 7, 3, 1);
        task[1] = new Task(1, 1, 7, 5, 1);
        task[2] = new Task(2, 1, 9, 1, 1);
        task[3] = new Task(3, 1, 12, 6, 1);*/
        /*int C = 2;
        Task[] task = new Task[4];
        task[0] = new Task(0, 6, 12, 6, 1);
        task[1] = new Task(1, 10, 13, 3, 1);
        task[2] = new Task(2, 6, 14, 4, 1);
        task[3] = new Task(3, 8, 13, 2, 1);*/

        /*int C = 2;
        Task[] task = new Task[4];
        task[0] = new Task(0, 5, 14, 6, 1);
        task[1] = new Task(1, 6, 10, 2, 1);
        task[2] = new Task(2, 6, 11, 5, 1);
        task[3] = new Task(3, 5, 11, 2, 1);*/

        /*int C = 2;
        Task[] task = new Task[4];
        task[0] = new Task(0, 6, 8, 1, 1);
        task[1] = new Task(1, 5, 8, 3, 1);
        task[2] = new Task(2, 2, 9, 1, 1);
        task[3] = new Task(3, 5, 12, 3, 1);*/

        /*int C = 2;
        Task[] task = new Task[4];
        task[0] = new Task(0, 1, 5, 4, 1);
        task[1] = new Task(1, 5, 7, 2, 2);
        task[2] = new Task(2, 3, 20, 3, 1);
        task[3] = new Task(3, 10, 13, 3, 2);*/

        /*int C = 2;
        Task[] task = new Task[4];
        task[0] = new Task(0, 8, 14, 5, 1);
        task[1] = new Task(1, 1, 7, 6, 1);
        task[2] = new Task(2, 1, 7, 3, 1);
        task[3] = new Task(3, 3, 14, 6, 1);*/

        /*int C = 2;
        Task[] task = new Task[5];
        task[0] = new Task(0, 1, 9, 5, 1);
        task[1] = new Task(1, 5, 10, 2, 1);
        task[2] = new Task(2, 1, 10, 3, 1);
        task[3] = new Task(3, 1, 12, 6, 1);
        task[4] = new Task(4, 2, 9, 3, 1);*/

        int C = 2;
        Task[] task = new Task[5];
        task[0] = new Task(0, 7, 9, 1, 1);
        task[1] = new Task(1, 4, 13, 5, 1);
        task[2] = new Task(2, 4, 8, 1, 1);
        task[3] = new Task(3, 2, 5, 3, 1);
        task[4] = new Task(4, 7, 9, 2, 1);



        EdgeFinderVilim vef = new EdgeFinderVilim(task, C);
        int[] vest = vef.Filter();
        System.out.println("vest : " + Arrays.toString(vest));

        CubicEdgeFinder cef = new CubicEdgeFinder(task, C);
        int[] cest = cef.Filter();
        System.out.println("cest : " + Arrays.toString(cest));

        NewEdgeFinder  nef = new NewEdgeFinder(task, C);
        int[] nest = nef.Filter();
        System.out.println("nest : " + Arrays.toString(nest));

        EdgeFinder  ef = new EdgeFinder(task, C);
        int[] est = ef.Filter();
        System.out.println("est : " + Arrays.toString(est));

        TimeTableEdgeFinder  ttef = new TimeTableEdgeFinder(task, C);
        int[] ttest = ttef.TimeTableEdgeFinder_DetectionPruning();
        System.out.println("ttest : " + Arrays.toString(ttest));
    }
}
