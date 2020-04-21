import java.util.Arrays;
import java.util.Comparator;

public class RevisitedEdgeFinder {

    Integer C;
    Task[] tasks;
    private Profile tl;

    private Integer[] tasks_indices_lct;
    private Integer[] tasks_indices_h;
    private Integer[] tasks_indices_est;
    private Integer[] tasks_indices_ect;
    Integer makespan;
    private int[] estPrime;

    public RevisitedEdgeFinder(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        this.tl = new Profile();
        estPrime = new int[tasks.length];

        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST
        tasks_indices_ect = sortWithJavaLibrary(tasks, new Task.ComparatorByEct(tasks)); //Increasing ECT
        tasks_indices_h = sortWithJavaLibrary(tasks, new Task.ComparatorByHeight(tasks)); //Increasing Height

        makespan = Integer.MAX_VALUE;

        InitializeTimeLine();
    }




    public boolean OverloadCheck()
    {
        InitializeIncrements(tasks_indices_lct.length - 1);
        if(ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime(), C) > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return false;

        for(int i=tasks_indices_lct.length - 1; i>0; i--)
        {
            while(i > 0 && tasks[tasks_indices_lct[i]].latestCompletionTime() == tasks[tasks_indices_lct[i-1]].latestCompletionTime())
            {
                i--;
            }

            if(i == 0)
                return true;

            InitializeIncrements(i-1);
            if(ScheduleTasks(tasks[tasks_indices_lct[i-1]].latestCompletionTime(), C) > tasks[tasks_indices_lct[i-1]].latestCompletionTime())
                return false;
        }

        return true;
    }



    public int[] RevisitedEdgeFinder_DetectionPruning()
    {
        for (int i = 0; i < tasks.length; i++)
            estPrime[i] = tasks[i].earliestStartingTime();
        InitializeIncrements(tasks_indices_lct.length - 1);
        makespan = ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime(), C);
        if(makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return estPrime;
        for(int i = tasks_indices_lct.length -1; i > 0; i--)
        {
            while(i > 0 && tasks[tasks_indices_lct[i]].latestCompletionTime() == tasks[tasks_indices_lct[i-1]].latestCompletionTime())
            {
                tasks[tasks_indices_lct[i]].inLambda = true;
                i--;
            }
            tasks[tasks_indices_lct[i]].inLambda = true;
            if(i == 0)
                return estPrime;
            int min = 0; int max = min + 1;
            while(max <= tasks.length)
            {
                if(max == tasks.length || tasks[tasks_indices_h[max]].height() > tasks[tasks_indices_h[min]].height()){
                    InitializeIncrements(i-1);
                    if(ScheduleTasks(tasks[tasks_indices_lct[i-1]].latestCompletionTime(), tasks[tasks_indices_h[min]].height()) > tasks[tasks_indices_lct[i-1]].latestCompletionTime())
                        return estPrime;
                    DetectePrecedences(i-1, min, max-1);
                    min = max;
                }
                max++;
            }
        }
        return estPrime;
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
            t.overlap = 0;
            t.slackOver = 0;
            t.slackUnder = 0;
            t.contact = null;

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

    private int ScheduleTasks(int maxLCT, int h)
    {
        int hreq, hmaxInc, ov, ect, overlap, slackUnder, slackOver;
        ect = Integer.MIN_VALUE;
        ov = hreq = hmaxInc = overlap = slackUnder = slackOver = 0;
        Timepoint t = tl.first;

        while(t.time < maxLCT)
        {
            int l = t.next.time - t.time;

            hmaxInc += t.incrementMax;
            t.hMaxTotal = hmaxInc;
            int hmax = Math.min(hmaxInc, C);
            hreq += t.increment;
            t.overlap = overlap;
            t.slackUnder = slackUnder;
            t.slackOver = slackOver;

            int hcons = Math.min(hreq + ov, hmax);

            if(ov > 0 && ov < (hcons - hreq) * l)
            {
                l = Math.max(1, ov / (hcons-hreq));
                t.InsertAfter(new Timepoint(t.time + l, t.capacity));
            }
            ov += (hreq - hcons) * l;
            if(hcons > C - h) {
                t.contact = t;
            }


            t.capacity = C - hcons;

            overlap += Math.max(hcons - (C - h), 0) * l;
            slackOver += Math.max(hmax - Math.max(C - h, hcons), 0) * l;
            slackUnder += Math.max(Math.min(C - h, hmax) - hcons, 0) * l;

            if(t.capacity < C)
                ect = t.next.time;

            t = t.next;
        }
        t.overlap = overlap;
        t.slackUnder = slackUnder;
        t.slackOver = slackOver;
        Timepoint best = null;
        while(t.previous != null){
            if(t.contact != null && best == null)
                best = t.contact;
            if(t.contact != null && t.previous.contact == null)
                t.previous.contact = t.contact;
            if(t.previous.contact != null && best != null){
                if(best.overlap - t.previous.overlap < best.slackUnder - t.previous.slackUnder)
                    t.previous.contact = best;
            }
            t = t.previous;
        }
        if(ov > 0)
            return Integer.MAX_VALUE;

        return ect;
    }


    private void DetectePrecedences(int j, int min, int max){
        for(int i = min; i <= max; i++){
            if(tasks[tasks_indices_h[i]].latestCompletionTime() > tasks[tasks_indices_lct[j]].latestCompletionTime() && tasks[tasks_indices_h[i]].inLambda &&
                    tasks[tasks_indices_h[i]].est_to_timepoint.contact != null &&
                    tasks[tasks_indices_h[i]].est_to_timepoint.contact.time < tasks[tasks_indices_h[i]].earliestCompletionTime()){
                if(tasks[tasks_indices_h[i]].earliestCompletionTime() < tasks[tasks_indices_lct[j]].latestCompletionTime()){
                    Timepoint t = tasks[tasks_indices_h[i]].est_to_timepoint.contact;
                    int overlap1 = t.overlap;
                    int slackUnder1 = t.slackUnder;
                    t = tasks[tasks_indices_h[i]].ect_to_timepoint;
                    int overlap2 = t.overlap;
                    int slackOver1 = t.slackOver;
                    t = tasks[tasks_indices_lct[j]].lct_to_timepoint;
                    int slackUnder2 = t.slackUnder;
                    int slackOver2 = t.slackOver;
                    if(overlap2 - overlap1 > slackUnder2 - slackUnder1 + slackOver2 - slackOver1){
                        int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1);
                        int estt = tasks[tasks_indices_h[i]].est_to_timepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                        if(estt > estPrime[tasks_indices_h[i]]) {
                            estPrime[tasks_indices_h[i]] = Math.max(estPrime[tasks_indices_h[i]], estt);
                            tasks[tasks_indices_h[i]].inLambda = false;
                        }
                    }
                }else{
                    Timepoint t = tasks[tasks_indices_h[i]].est_to_timepoint.contact;
                    int overlap1 = t.overlap;
                    int slackUnder1 = t.slackUnder;
                    t = tasks[tasks_indices_lct[j]].lct_to_timepoint;
                    int slackUnder2 = t.slackUnder;
                    int overlap2 = t.overlap;
                    if(overlap2 - overlap1 > slackUnder2 - slackUnder1){
                        int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1);
                        int estt = tasks[tasks_indices_h[i]].est_to_timepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                        if(estt > estPrime[tasks_indices_h[i]]) {
                            estPrime[tasks_indices_h[i]] = Math.max(estPrime[tasks_indices_h[i]], estt);
                            tasks[tasks_indices_h[i]].inLambda = false;
                        }
                    }
                }
            }
        }
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

    public void PrintTimepoint() {
        // TODO Auto-generated method stub
        //String info = " ";
        String espace = " ";
        Timepoint t = tl.first;
        while(t != null)
        {
            String info =  "Timepoint: (t = " + t.time + ", capacity= " + t.capacity +  ", hreq = "+ t.increment + ", hmax = " + t.incrementMax + ") ";
            espace += info;
            t = t.next;
        }
        System.out.println(espace);
    }


}
