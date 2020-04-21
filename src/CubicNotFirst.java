import java.util.Arrays;
import java.util.Comparator;

public class CubicNotFirst {
    Integer C;
    Task[] tasks;
    private Profile tl;
    private Integer[] minECT;
    private Integer[] tasks_indices_lct;
    private Integer[] tasks_indices_est;
    private Integer[] tasks_indices_ect;

    Integer makespan;
    private int[] estPrime;

    public CubicNotFirst(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        this.tl = new Profile();
        estPrime = new int[tasks.length];
        minECT = new Integer[tasks.length];
        Arrays.fill(minECT, -1);

        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST
        tasks_indices_ect = sortWithJavaLibrary(tasks, new Task.ComparatorByEct(tasks)); //Increasing ECT

        makespan = Integer.MAX_VALUE;

        InitializeTimeLine();
    }


    public int[] Filter()
    {
        for (int i = 0; i < tasks.length; i++)
            estPrime[i] = tasks[i].earliestStartingTime();
        InitializeIncrements(tasks_indices_lct.length - 1);
        makespan = ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime());
        if(makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return estPrime;
        for(int i = 0; i < tasks.length; i++){
            for(int j = 0; j < tasks.length; j++){
                if(i != j && tasks[tasks_indices_lct[i]].earliestStartingTime() < tasks[tasks_indices_lct[j]].latestCompletionTime()){
                    newInitializeIncrements(j, i);
                    if(ScheduleTasks(tasks[tasks_indices_lct[j]].latestCompletionTime()) > tasks[tasks_indices_lct[j]].latestCompletionTime()){
                        estPrime[tasks_indices_lct[i]] = Math.max(estPrime[tasks_indices_lct[i]], minECT[tasks_indices_lct[i]]);
                        break;
                    }
                }

            }
        }
        return estPrime;
    }








    private void InitializeTimeLine()
    {
        int n = tasks.length;
        tl.Add(new Timepoint(tasks[tasks_indices_est[0]].earliestStartingTime(), C));
        Timepoint t = tl.first;

        int p,i,j,k;
        p = i = j = k = 0;

        int maxLCT = Integer.MIN_VALUE;

        while(i < n || j < n || k < n)
        {
            if(i<n && (j == n || tasks[tasks_indices_est[i]].earliestStartingTime() <= tasks[tasks_indices_ect[j]].earliestCompletionTime()) &&
                    (k == n || tasks[tasks_indices_est[i]].earliestStartingTime() <= tasks[tasks_indices_lct[k]].latestCompletionTime()))
            {
                if(tasks[tasks_indices_est[i]].earliestStartingTime() > t.time)
                {
                    t.InsertAfter(new Timepoint(tasks[tasks_indices_est[i]].earliestStartingTime(), C));
                    t = t.next;
                }
                tasks[tasks_indices_est[i]].est_to_timepoint = t;
                p += tasks[tasks_indices_est[i]].processingTime();
                maxLCT = Math.max(maxLCT, tasks[tasks_indices_est[i]].latestCompletionTime());

                tasks[tasks_indices_est[i]].inLambda = false;

                i++;
            }
            else if(j < n && (k==n || tasks[tasks_indices_ect[j]].earliestCompletionTime() <= tasks[tasks_indices_lct[k]].latestCompletionTime()))
            {
                if(tasks[tasks_indices_ect[j]].earliestCompletionTime() > t.time)
                {
                    t.InsertAfter(new Timepoint(tasks[tasks_indices_ect[j]].earliestCompletionTime(), C));
                    t = t.next;
                }
                tasks[tasks_indices_ect[j]].ect_to_timepoint = t;
                j++;
            }
            else
            {
                if(tasks[tasks_indices_lct[k]].latestCompletionTime() > t.time)
                {
                    t.InsertAfter(new Timepoint(tasks[tasks_indices_lct[k]].latestCompletionTime(), C));
                    t = t.next;
                }
                tasks[tasks_indices_lct[k]].lct_to_timepoint = t;
                k++;
            }

        }
        t.InsertAfter(new Timepoint(maxLCT + p, 0));
    }
    private void PrintTimepoint() {
        // TODO Auto-generated method stub
        //String info = " ";
        String espace = " ";
        Timepoint t = tl.first;
        while(t != null)
        {
            String info =  "Timepoint: (t = " + t.time + ", capacity= " + t.capacity + ") ";
            espace += info;
            t = t.next;
        }
        System.out.println(espace);
    }



    private void InitializeIncrements(int maxIndex)
    {
        Timepoint t = tl.first;
        while(t != null)
        {
            t.increment = 0;
            t.incrementMax = 0;
            t.hMaxTotal = 0;
            t.hreal = 0;
            t.minimumOverflow = 0;
            t.overflow = 0;
            t.capacity = C;

            t = t.next;
        }
        for(int i = 0; i <= maxIndex; i++)
        {
            t = tasks[tasks_indices_lct[i]].est_to_timepoint;
            t.increment += tasks[tasks_indices_lct[i]].height();
            t.incrementMax += tasks[tasks_indices_lct[i]].height();

            t = tasks[tasks_indices_lct[i]].ect_to_timepoint;
            t.increment -= tasks[tasks_indices_lct[i]].height();

            t = tasks[tasks_indices_lct[i]].lct_to_timepoint;
            t.incrementMax -= tasks[tasks_indices_lct[i]].height();
        }
    }

    private void newInitializeIncrements(int maxIndex, int a)
    {
        Timepoint t = tl.first;
        while(t != null)
        {
            t.increment = 0;
            t.incrementMax = 0;
            t.hMaxTotal = 0;
            t.hreal = 0;
            t.minimumOverflow = 0;
            t.overflow = 0;
            t.capacity = C;

            t = t.next;
        }
        int minEct = Integer.MAX_VALUE;
        for(int i = 0; i <= maxIndex; i++)
        {
            if(i != a && tasks[tasks_indices_lct[i]].earliestCompletionTime() > tasks[tasks_indices_lct[a]].earliestStartingTime()){
                minEct = Math.min(minEct, tasks[tasks_indices_lct[i]].earliestCompletionTime());
                t = tasks[tasks_indices_lct[i]].est_to_timepoint;
                t.increment += tasks[tasks_indices_lct[i]].height();
                t.incrementMax += tasks[tasks_indices_lct[i]].height();

                t = tasks[tasks_indices_lct[i]].ect_to_timepoint;
                t.increment -= tasks[tasks_indices_lct[i]].height();

                t = tasks[tasks_indices_lct[i]].lct_to_timepoint;
                t.incrementMax -= tasks[tasks_indices_lct[i]].height();
            }
        }
        t = tl.first;
        t.increment += tasks[tasks_indices_lct[a]].height();
        t.incrementMax += tasks[tasks_indices_lct[a]].height();
        if(tasks[tasks_indices_lct[a]].earliestCompletionTime() < tasks[tasks_indices_lct[maxIndex]].latestCompletionTime()){
            t = tasks[tasks_indices_lct[a]].ect_to_timepoint;
            t.increment -= tasks[tasks_indices_lct[a]].height();
            t.incrementMax -= tasks[tasks_indices_lct[a]].height();
        }else{
            t = tasks[tasks_indices_lct[maxIndex]].lct_to_timepoint;
            t.increment -= tasks[tasks_indices_lct[a]].height();
            t.incrementMax -= tasks[tasks_indices_lct[a]].height();
        }
        minECT[tasks_indices_lct[a]] = minEct;
    }




    private int ScheduleTasks(int maxLCT)
    {
        int hreq, hmaxInc, ov, ect;
        ect = Integer.MIN_VALUE;
        ov = hreq = hmaxInc = 0;
        Timepoint t = tl.first;

        while(t.time < maxLCT)
        {
            int l = t.next.time - t.time;

            hmaxInc += t.incrementMax;
            t.hMaxTotal = hmaxInc;
            int hmax = Math.min(hmaxInc, C);
            hreq += t.increment;

            int hcons = Math.min(hreq + ov, hmax);

            if(ov > 0 && ov < (hcons - hreq) * l)
            {
                l = Math.max(1, ov / (hcons-hreq));
                t.InsertAfter(new Timepoint(t.time + l, t.capacity));
            }
            ov += (hreq - hcons) * l;


            t.capacity = C - hcons;

            if(t.capacity < C)
                ect = t.next.time;

            t = t.next;
        }
        if(ov > 0)
            return Integer.MAX_VALUE;

        return ect;
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

    private String PrintTasksDomain()
    {
        int diff = -1 * tl.first.time;
        String text ="C = " + C + "\n Tasks = [";
        for(int i=0; i<tasks.length; i++)
        {
            text += "\n [" + i + "] : {est = " + (tasks[i].earliestStartingTime() + diff) +
                    " , lct = " + (tasks[i].latestCompletionTime() + diff) +
                    " , p = " + tasks[i].processingTime() +
                    " , h = " + tasks[i].height() +
                    "} , ";
        }
        text += "] \n";
        return text;
    }

}
