import java.util.Arrays;
import java.util.Comparator;

public class DefaultEdgeFinder {

    Integer C;
    Task[] tasks;

    Integer[] tasks_indices_lct;
    Integer[] tasks_indices_est;

    Integer makespan;
    private int[] estPrime;

    public DefaultEdgeFinder(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        estPrime = new int[tasks.length];


        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST

        makespan = Integer.MAX_VALUE;
    }





    public int[] defautEdgefinder(){
        int[] Dupdate = new int[tasks.length];
        int[] SLupdate = new int[tasks.length];
        int[] E = new int[tasks.length];
        for (int i = 0; i < tasks.length; i++) {
            estPrime[i] = tasks[i].earliestStartingTime();
            Dupdate[i] = Integer.MIN_VALUE;
            SLupdate[i] = Integer.MIN_VALUE;
        }
        for(int j = 0; j < tasks.length; j++){
            int Energy = 0; int maxEnergy = 0; int estD = Integer.MIN_VALUE;
            for(int i = tasks.length-1; i >= 0; i--){
                if(tasks[tasks_indices_est[i]].latestCompletionTime() <= tasks[tasks_indices_lct[j]].latestCompletionTime()) {
                    Energy += tasks[tasks_indices_est[i]].energy();
                    Double Density = (double) Energy / (double) (tasks[tasks_indices_lct[j]].latestCompletionTime() - tasks[tasks_indices_est[i]].earliestStartingTime());
                    Double maxDensity = (double) maxEnergy / (double) (tasks[tasks_indices_lct[j]].latestCompletionTime() - estD);
                    if (Density > maxDensity) {
                        maxEnergy = Energy;
                        estD = tasks[tasks_indices_est[i]].earliestStartingTime();
                    }
                }else{
                    int rest = maxEnergy - (C - tasks[tasks_indices_est[i]].height()) * (tasks[tasks_indices_lct[j]].latestCompletionTime() - estD);
                    if (rest > 0) {
                        int est = estD + (int) Math.ceil((double)rest / (double)tasks[tasks_indices_est[i]].height());
                        Dupdate[tasks_indices_est[i]] = Math.max(Dupdate[tasks_indices_est[i]], est);
                    }
                }
                E[tasks_indices_est[i]] = Energy;
            }
            int minSL = Integer.MAX_VALUE; int estS = tasks[tasks_indices_lct[j]].latestCompletionTime();
            for(int i = 0; i < tasks.length; i++){
                if(C*(tasks[tasks_indices_lct[j]].latestCompletionTime() - tasks[tasks_indices_est[i]].earliestStartingTime()) - E[tasks_indices_est[i]] < minSL){
                    estS = tasks[tasks_indices_est[i]].earliestStartingTime();
                    minSL = C*(tasks[tasks_indices_lct[j]].latestCompletionTime() - estS) - E[tasks_indices_est[i]];
                }
                if(tasks[tasks_indices_est[i]].latestCompletionTime() > tasks[tasks_indices_lct[j]].latestCompletionTime()){
                    int rest = tasks[tasks_indices_est[i]].height()*(tasks[tasks_indices_lct[j]].latestCompletionTime() - estS) - minSL;
                    if(estS <= tasks[tasks_indices_lct[j]].latestCompletionTime() && rest > 0){
                        int est = estS + (int)Math.ceil((double)rest/(double)tasks[tasks_indices_est[i]].height());
                        SLupdate[tasks_indices_est[i]] = Math.max(est, SLupdate[tasks_indices_est[i]]);
                    }
                    if(tasks[tasks_indices_est[i]].earliestCompletionTime() >= tasks[tasks_indices_lct[j]].latestCompletionTime() || minSL - tasks[tasks_indices_est[i]].energy() < 0) {
                        int est = Math.max(Dupdate[tasks_indices_est[i]], SLupdate[tasks_indices_est[i]]);
                        estPrime[tasks_indices_est[i]] = Math.max(estPrime[tasks_indices_est[i]], est);
                    }
                }
            }
        }
        return  estPrime;
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
