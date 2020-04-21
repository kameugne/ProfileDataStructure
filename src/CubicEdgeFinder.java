import java.util.Arrays;
import java.util.Comparator;

public class CubicEdgeFinder {
    Integer C;
    Task[] tasks;
    private Profile tp;
    private Timepoint tpinit;
    private Integer[] Preced;

    private Integer[] tasks_indices_lct;
    private Integer[] tasks_indices_h;
    private Integer[] tasks_indices_est;
    private Integer[] tasks_indices_ect;
    Integer makespan;
    private int[] estPrime;

    public CubicEdgeFinder(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        this.tp = new Profile();
        estPrime = new int[tasks.length];

        Preced = new Integer[tasks.length];
        Arrays.fill(Preced, -1);

        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST
        tasks_indices_ect = sortWithJavaLibrary(tasks, new Task.ComparatorByEct(tasks)); //Increasing ECT
        tasks_indices_h = sortWithJavaLibrary(tasks, new Task.ComparatorByHeight(tasks)); //Increasing Height

        makespan = Integer.MAX_VALUE;

        InitializeTimeLine();
    }

    /*
    The filtering algorithm performs the adjustment after each detection
     */

    public int[] Filter()
    {
        int[] results = new int[tasks.length];
        if(CubicDetection())
            results = EdgeFinder_Pruning();
        return results;
    }

    /*
    The OverloadChecker checks if each set of tasks LCut(T, j) = {k \in T \mid lct_k <= lct_j} can be scheduled in the available space
     */





    /*
     The detection phase computes the profile of the set of tasks LCut(T,j) for the different reference height h_i with i \in Lambda and then,
     for each tasks i \in Lambda, it is checked if LCut(T,j) precedes i and the information is quippe in the array Prec[i] = j.
     */


    private boolean CubicDetection(){
        CubicInitializeIncrements(tasks_indices_lct.length - 1, -1);
        makespan = CubicScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime());
        if(makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return false;
        for(int j=tasks_indices_lct.length - 1; j>0; j--) {
            while (j > 0 && tasks[tasks_indices_lct[j]].latestCompletionTime() == tasks[tasks_indices_lct[j - 1]].latestCompletionTime()) {
                tasks[tasks_indices_lct[j]].inLambda = true;
                j--;
            }
            tasks[tasks_indices_lct[j]].inLambda = true;

            if (j == 0)
                return true;
            CubicInitializeIncrements(j - 1, -1);
            if(CubicScheduleTasks(tasks[tasks_indices_lct[j-1]].latestCompletionTime()) > tasks[tasks_indices_lct[j - 1]].latestCompletionTime())
                return false;
            for(int i = j; i < tasks.length; i++){
                if(tasks[tasks_indices_lct[i]].inLambda) {
                    CubicInitializeIncrements(j - 1, i);
                    if (CubicScheduleTasks(tasks[tasks_indices_lct[j - 1]].latestCompletionTime()) > tasks[tasks_indices_lct[j - 1]].latestCompletionTime()) {
                        Preced[tasks_indices_lct[i]] = j - 1;
                        tasks[tasks_indices_lct[i]].inLambda = false;
                    }
                }
            }
        }
        return true;
    }








    private int[] EdgeFinder_Pruning()
    {
        for (int i = 0; i < tasks.length; i++){
            estPrime[i] = tasks[i].earliestStartingTime();
        }
        for(int i=0; i<tasks.length; i++)
        {
            if(Preced[i] != -1)
            {
                InitializeIncrements(Preced[i]);
                int maxOv = ComputeMinimumOverflow(C - tasks[i].height(), tasks[tasks_indices_lct[Preced[i]]].latestCompletionTime());
                if (maxOv > 0) {
                    int est = ComputeBound(tasks[i].height(), maxOv);
                    if (est > tasks[i].earliestStartingTime()) {
                        estPrime[i] = Math.max(estPrime[i], est);
                    }
                }
            }
        }
        return estPrime;
    }






    private void InitializeTimeLine()
    {
        int n = tasks.length;
        tp.Add(new Timepoint(tasks[tasks_indices_est[0]].earliestStartingTime(), C));
        Timepoint t = tp.first;

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




    private void InitializeIncrements(int maxIndex)
    {
        Timepoint t = tp.first;
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





    private int ComputeMinimumOverflow(int c, int lct)
    {
        int ov, h, hmax;
        ov = h = hmax = 0;

        tpinit = tp.first;
        while(tpinit.time < lct)
        {
            int l = tpinit.next.time - tpinit.time;
            tpinit.overflow = ov;
            h += tpinit.increment;
            hmax += tpinit.incrementMax;

            tpinit.hreal = h;
            tpinit.hMaxTotal = hmax;

            int cmax = Math.min(hmax,c);

            int f = cmax - h;

            if(ov < f*l)
            {
                ov = 0;
            }
            else
            {
                ov = Math.max(0, ov + (Math.max(0, h-cmax) - Math.max(0,  f)) * l);
            }
            tpinit = tpinit.next;
        }
        tpinit.overflow = ov;
        int maxOv = tpinit.overflow;
        int min = Integer.MAX_VALUE;

        while(tpinit != null)
        {
            min = Math.min(min, tpinit.overflow);
            tpinit.minimumOverflow = min;

            if(min == 0)
                break;
            tpinit = tpinit.previous;
        }
        return maxOv;
    }


    private int ComputeBound(int hi, int maxOv)
    {
        int est = Integer.MIN_VALUE;
        int hreal, hmax, hcons, ov, d, time;
        d = ov = 0;
        Timepoint t = tpinit;
        time = t.time;

        hreal = t.hreal;
        hmax = t.hMaxTotal;

        while(t.next != null)
        {
            int l = t.next.time - time;
            int c;
            c = Math.min(hmax, C);

            hcons = Math.min(hreal + ov, c);

            boolean next = true;
            if(ov > 0 && ov < (hcons - hreal) * l)
            {
                if(ov <= hcons-hreal)
                    l = 1;
                else
                    l = ov / (hcons-hreal);
                next = false;
            }

            int dreal;
            if(hcons <= (C - hi))
                dreal = 0;
            else
                dreal = Math.min(t.next.minimumOverflow - d,(hcons - (C - hi))*l);

            if(d + dreal >= maxOv)
            {
                est = Math.min(t.next.time, time + (int)Math.ceil((double)(maxOv - d) / (double)(hcons-(C-hi))));
                return est;
            }

            d+=dreal;
            ov += (hreal - hcons)*l;

            if(next)
            {
                t = t.next;
                time = t.time;
                hreal += t.increment;
                hmax += t.incrementMax;
            }
            else
                time += l;
        }
        return est;
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

    private String PrintPrecedences()
    {
        String text = "";

        text += "Precedences = [";
        for(int i=0; i<Preced.length; i++)
        {
            text += Preced[i] == -1 ? "-1 , " : tasks_indices_lct[Preced[i]] + " , ";
        }
        text += "] \n";

        return text;
    }






    /* ------------ Functions used in the Detection phase --------------*/

    /*
     * Implementation of the Algorihtm 1 : ScheduleTasks
     * as presented in Generalizing the Edge-Finder Rule for the Cumulative Constraint.
     * Lines 26-34 are omitted since a stripped down version of ScheduleTasks is instead use the in Adjustment algrorithm.
     * See ComputeMinimumOverflow function.
     */


    private void CubicInitializeIncrements(int maxIndex, int u)
    {
        Timepoint t = tp.first;
        while(t != null)
        {
            t.increment = 0;
            t.incrementMax = 0;
            t.hMaxTotal = 0;
            t.hreal = 0;
            t.minimumOverflow = 0;
            t.overflow = 0;
            t.capacity = C;

            t.cons = 0;

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
        if(u != -1){
            t = tasks[tasks_indices_lct[u]].est_to_timepoint;
            t.increment += tasks[tasks_indices_lct[u]].height();
            t.incrementMax += tasks[tasks_indices_lct[u]].height();

            if(tasks[tasks_indices_lct[u]].earliestCompletionTime() < tasks[tasks_indices_lct[maxIndex]].latestCompletionTime()){
                t = tasks[tasks_indices_lct[u]].ect_to_timepoint;
                t.increment -= tasks[tasks_indices_lct[u]].height();
                t.incrementMax -= tasks[tasks_indices_lct[u]].height();
            }else{
                t = tasks[tasks_indices_lct[maxIndex]].lct_to_timepoint;
                t.increment -= tasks[tasks_indices_lct[u]].height();
                t.incrementMax -= tasks[tasks_indices_lct[u]].height();
            }
        }
    }


    private int CubicScheduleTasks(int maxLCT)
    {
        int hreq, hmaxInc, ov, ect;
        ect = Integer.MIN_VALUE;
        ov = hreq = hmaxInc = 0;
        Timepoint t = tp.first;

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

}
