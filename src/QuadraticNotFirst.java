import org.mockito.internal.util.reflection.LenientCopyTool;

import java.util.Arrays;
import java.util.Comparator;

public class QuadraticNotFirst {

    Integer C;
    Task[] tasks;
    private Profile tl;
    private Integer[] NotFirst;

    private Integer[] tasks_indices_lct;
    private Integer[] tasks_indices_est;
    private Integer[] tasks_indices_ect;

    Integer makespan;
    private int[] estPrime;
    private int[] exEnergy;

	public QuadraticNotFirst(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        this.tl = new Profile();
        estPrime = new int[tasks.length];
        NotFirst = new Integer[tasks.length];
        Arrays.fill(NotFirst, -1);

        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST
        tasks_indices_ect = sortWithJavaLibrary(tasks, new Task.ComparatorByEct(tasks)); //Increasing ECT

        makespan = Integer.MAX_VALUE;

        InitializeTimeLine();
    }

        /* ------------ Filtering algorihtms --------------*/


    public int[] Filter(){
        return NotFirst_DectectionPruning();
    }
    public boolean OverloadCheck()
    {
        InitializeIncrements(tasks_indices_lct.length - 1);
        if(ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime()) > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
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
            if(ScheduleTasks(tasks[tasks_indices_lct[i-1]].latestCompletionTime()) > tasks[tasks_indices_lct[i-1]].latestCompletionTime())
                return false;
        }

        return true;
    }

    private int[] computeExcludedEnergy(int cont, int i, int lct){
        Arrays.fill(exEnergy, 0);
        int max, min;
        max = lct;
        min = max - 1;
        while(min >= 0){
            if(min == 0 || tasks[tasks_indices_lct[min]].latestCompletionTime() < tasks[tasks_indices_lct[max]].latestCompletionTime()){
                int E = 0;
                for(int k = min+1; k <= max; k++){
                    E += 0;
                }

                max = min;
            }
            min--;
        }
        return exEnergy;
    }

    private int compExcludedEnergy(int cont, int j, int i){
        int E = 0;
        for(int k = 0; k < tasks.length; k++){
            if(tasks[tasks_indices_lct[k]].earliestCompletionTime() > tasks[tasks_indices_lct[i]].earliestStartingTime() &&
                    tasks[tasks_indices_lct[k]].latestCompletionTime() > tasks[tasks_indices_lct[j]].latestCompletionTime())
                E += (Math.min(tasks[tasks_indices_lct[j]].latestCompletionTime(), tasks[tasks_indices_lct[k]].earliestCompletionTime()) -
                        Math.max(cont, tasks[tasks_indices_lct[k]].earliestStartingTime()))*tasks[tasks_indices_lct[k]].height();
        }
        return E;
    }

    private int minEct(int i, int lct){
        int minEct = Integer.MAX_VALUE;
        for(int k = 0; k < tasks.length; k++){
            if(tasks[tasks_indices_lct[k]].earliestCompletionTime() > tasks[tasks_indices_lct[i]].earliestStartingTime() &&
                    tasks[tasks_indices_lct[k]].latestCompletionTime() <= tasks[tasks_indices_lct[lct]].latestCompletionTime())
                minEct = Math.min(minEct, tasks[tasks_indices_lct[k]].earliestCompletionTime());
        }
        return minEct;
    }



    private int[] NotFirst_DectectionPruning() {
        for (int i = 0; i < tasks.length; i++)
            estPrime[i] = tasks[i].earliestStartingTime();
        for(int i = 0; i < tasks.length; i++){
            if(tasks[tasks_indices_lct[i]].earliestCompletionTime() <  tasks[tasks_indices_lct[i]].latestCompletionTime()){
                int lct_index =  QuadInitializeIncrements(i, false);
                if(lct_index >= 0 && lct_index < tasks.length) {
                    int ect = QuadScheduleTasks(tasks[tasks_indices_lct[lct_index]].latestCompletionTime(), i);
                    if (tasks[tasks_indices_lct[i]].est_to_timepoint.contact != null && tasks[tasks_indices_lct[i]].est_to_timepoint.contact.time < tasks[tasks_indices_lct[i]].earliestCompletionTime()) {
                        int cont = tasks[tasks_indices_lct[i]].est_to_timepoint.contact.time;
                        int minEct = QuadInitializeIncrements(i, true);
                        int ectt = QuadScheduleTasks(tasks[tasks_indices_lct[lct_index]].latestCompletionTime(), i);
                        if (ectt > tasks[tasks_indices_lct[lct_index]].latestCompletionTime()) {
                            estPrime[tasks_indices_lct[i]] = Math.max(estPrime[tasks_indices_lct[i]], minEct);
                        }else{
                            for(int j = lct_index; j >= 0; j--){
                                if(tasks[tasks_indices_lct[j]].earliestCompletionTime() > tasks[tasks_indices_lct[i]].earliestStartingTime() &&
                                        tasks[tasks_indices_lct[j]].lct_to_timepoint.overflow > 0 && cont > tasks[tasks_indices_lct[j]].latestCompletionTime()){
                                   int overflow = tasks[tasks_indices_lct[j]].lct_to_timepoint.overflow;
                                   int exEnergy = compExcludedEnergy(cont, j, i);
                                   if(overflow > exEnergy)
                                       estPrime[tasks_indices_lct[i]] = Math.max(estPrime[tasks_indices_lct[i]], minEct);
                                }

                            }
                        }

                    }
                }
            }

        }
        InitializeIncrements(tasks_indices_lct.length - 1);
        makespan = ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime());
        if (makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return estPrime;

        return estPrime;
    }



    private int[] NotFirst_Adjustment() {
        for (int i = 0; i < tasks.length; i++)
            estPrime[i] = tasks[i].earliestStartingTime();
        for(int i = 0; i < tasks.length; i++){
            if(NotFirst[i] != -1){
                int minEct = Integer.MAX_VALUE;
                for(int u = 0; u < tasks.length; u++){
                    if(tasks[tasks_indices_lct[u]].id() != tasks[i].id() && tasks[tasks_indices_lct[u]].latestCompletionTime() <= tasks[tasks_indices_lct[NotFirst[i]]].latestCompletionTime() &&
                            tasks[tasks_indices_lct[u]].earliestCompletionTime() > tasks[i].earliestStartingTime()){
                        minEct = Math.min(minEct, tasks[tasks_indices_lct[u]].earliestCompletionTime());
                    }
                }
                estPrime[i] = Math.max(estPrime[i], minEct);
            }
        }
        return estPrime;
    }



    private int QuadInitializeIncrements(int i, boolean withTask)
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
        int lct_index = Integer.MIN_VALUE;
        int minEct = Integer.MAX_VALUE;
        for(int k = 0; k < tasks.length; k++)
        {
            if(k != i && tasks[tasks_indices_lct[k]].earliestCompletionTime() > tasks[tasks_indices_lct[i]].earliestStartingTime()){
                lct_index = Math.max(lct_index, k);
                minEct = Math.min(minEct, tasks[tasks_indices_lct[k]].earliestCompletionTime());
                t = tasks[tasks_indices_lct[k]].est_to_timepoint;
                t.increment += tasks[tasks_indices_lct[k]].height();
                t.incrementMax += tasks[tasks_indices_lct[k]].height();

                t = tasks[tasks_indices_lct[k]].ect_to_timepoint;
                t.increment -= tasks[tasks_indices_lct[k]].height();

                t = tasks[tasks_indices_lct[k]].lct_to_timepoint;
                t.incrementMax -= tasks[tasks_indices_lct[k]].height();
            }
        }
        if(withTask){
            t = tl.first;
            t.increment += tasks[tasks_indices_lct[i]].height();
            t.incrementMax += tasks[tasks_indices_lct[i]].height();

            t = tasks[tasks_indices_lct[i]].ect_to_timepoint;
            t.increment -= tasks[tasks_indices_lct[i]].height();
            t.incrementMax -= tasks[tasks_indices_lct[i]].height();
        }
        if(withTask)
            return minEct;
        else
            return lct_index;
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
                t.energy = 0;

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


    private void InitializeIncrements1()
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
            t.energy = 0;

            t = t.next;
        }
    }




        private int QuadScheduleTasks(int maxLCT, int i) {
            int hreq, hmaxInc, ov, ect, overlap, slackUnder, slackOver;
            ect = Integer.MIN_VALUE;
            ov = hreq = hmaxInc = overlap = slackUnder = slackOver = 0;
            Timepoint t = tl.first;
            int h = tasks[tasks_indices_lct[i]].height();

            while (t.time < maxLCT) {
                int l = t.next.time - t.time;

                hmaxInc += t.incrementMax;
                t.hMaxTotal = hmaxInc;
                int hmax = Math.min(hmaxInc, C);
                hreq += t.increment;
                t.overlap = overlap;
                t.slackUnder = slackUnder;
                t.slackOver = slackOver;

                int hcons = Math.min(hreq + ov, hmax);

                if (ov > 0 && ov < (hcons - hreq) * l) {
                    l = Math.max(1, ov / (hcons - hreq));
                    t.InsertAfter(new Timepoint(t.time + l, t.capacity));
                }
                ov += (hreq - hcons) * l;
                if (hcons > C - h) {
                    t.contact = t;
                }
                t.overflow = ov;


                t.capacity = C - hcons;

                overlap += Math.max(hcons - (C - h), 0) * l;
                slackOver += Math.max(hmax - Math.max(C - h, hcons), 0) * l;
                slackUnder += Math.max(Math.min(C - h, hmax) - hcons, 0) * l;

                if (t.capacity < C)
                    ect = t.next.time;

                t = t.next;
            }
            t.overflow = ov;
            t.overlap = overlap;
            t.slackUnder = slackUnder;
            t.slackOver = slackOver;
            Timepoint best = null;
            while (t.previous != null) {
                if (t.contact != null && best == null)
                    best = t.contact;
                if (t.contact != null && t.previous.contact == null)
                    t.previous.contact = t.contact;
                if (t.previous.contact != null && best != null) {
                    if (best.overlap - t.previous.overlap < best.slackUnder - t.previous.slackUnder)
                        t.previous.contact = best;
                }
                t = t.previous;
            }
            if (ov > 0)
                return Integer.MAX_VALUE;

            return ect;
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
            t.overflow = ov;
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
        t.overflow = ov;
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
        private void PrintTimepoint() {
            // TODO Auto-generated method stub
            //String info = " ";
            String espace = " ";
            Timepoint t = tl.first;
            while(t != null)
            {
                String info =  "Timepoint: (t = " + t.time + ", capacity = " + t.capacity +  " Energy = " + t.energy + " Overflow = " + t.overflow + ") ";
                espace += info;
                t = t.next;
            }
            System.out.println(espace);
        }

    }
