import java.util.Arrays;
import java.util.Comparator;

public class TimeLineNotFirst {
    Integer C;
    Task[] tasks;

    private Integer[] tasks_indices_lct;
    private Integer[] tasks_indices_est;
    private Task[] tasksByLct;
    private Task[] tasksByEst;

    Integer makespan;
    private int[] estPrime;

    public TimeLineNotFirst(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        estPrime = new int[tasks.length];

        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST
        tasksByLct = new Task[tasks.length];
        tasksByEst = new Task[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            tasksByEst[i] = tasks[tasks_indices_est[i]];
            tasksByLct[i] = tasks[tasks_indices_lct[i]];
        }

        makespan = Integer.MAX_VALUE;
    }
    public int[] Filter(IThetaSet thetaSet) {
        for (int i = 0; i < tasks.length; i++)
            estPrime[i] = tasks[i].earliestStartingTime();
        thetaSet.setTasks(tasks, tasksByEst);
        thetaSet.initialize(C, 0);
        for (int k = 0; k < tasks.length; k++) {
        	thetaSet.add(tasks[k].id());
        }
        makespan = (int)Math.ceil((double)thetaSet.envThetaCi() / (double)C);

        for (int k = 0; k < tasks.length; k++) {
            Task iTask = tasksByLct[k];
            int i = iTask.id();
            int ci = iTask.height();
            int minEct = Integer.MAX_VALUE;
            thetaSet.initialize(C, ci);
            if (iTask.earliestCompletionTime() < iTask.latestCompletionTime()){
                for (int l = 0; l < tasks.length; l++) {
                    Task jTask = tasksByLct[l];
                    int j = jTask.id();
                    if (iTask.earliestStartingTime() < jTask.earliestCompletionTime() && j != i) {
                        thetaSet.add(j);
                        minEct = Math.min(minEct, jTask.earliestCompletionTime());
                        int Clctj = C * jTask.latestCompletionTime();
                        int ciMin = ci * Math.min(iTask.earliestCompletionTime(), jTask.latestCompletionTime());
                        if (thetaSet.envThetaCi() > Clctj - ciMin) {
                            estPrime[i] = Math.max(estPrime[i], minEct);
                            break;
                        }
                    }
                }
            }
        }

        return estPrime;
    }

    public int[] filterWithTimeline() {
        return Filter(new TimelineWrapper(tasks.length));
    }






    /* ------------ Utility Functions --------------*/
    private static Integer[] sortWithJavaLibrary(Task[] tasks, Comparator<Integer> comparator) {

        int n = tasks.length;
        Integer[] tasks_indices = new Integer[n];
        for (int q = 0; q < n; q++) {
            tasks_indices[q] = new Integer(q);
        }
        Arrays.sort(tasks_indices, comparator);
        return tasks_indices;
    }

}
