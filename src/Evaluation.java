public class Evaluation {
    public static void main(String[] args)throws Exception
    {
        Task[] tasks = new Task[6];
        tasks[0] = new Task(0, 3, 8, 4, 3);
        tasks[1] = new Task(1, 0, 2, 2, 2);
        tasks[2] = new Task(2, 0, 3, 3, 3);
        tasks[3] = new Task(3, 1, 3, 2, 1);
        tasks[4] = new Task(4, 2, 5, 3, 2);
        tasks[5] = new Task(5, 3, 7, 4, 2);

        //EdgeFinder ef = new EdgeFinder(tasks, 6);
        //NewEdgeFinder nef = new NewEdgeFinder(tasks, 6);
        TimeTableEdgeFinder ttef = new TimeTableEdgeFinder(tasks, 6);
        //ttef.TimeTableInitializeTimeLine();

        //System.out.println("new est " + ef.Filter()[0] + " old est " + ef.Filter()[0]);
    }
}
