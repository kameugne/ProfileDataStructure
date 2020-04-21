import java.util.Arrays;
import java.util.Comparator;
public class TimeTableEdgeFinder {

    Integer C;
    Task[] tasks;
    private TimeTableProfile tl;

    private Integer[] tasks_indices_lct;
    private Integer[] tasks_indices_lst;
    private Integer[] tasks_indices_h;
    private Integer[] tasks_indices_est;
    private Integer[] tasks_indices_ect;

    Integer makespan;
    private int[] estPrime;

    public TimeTableEdgeFinder(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        this.tl = new TimeTableProfile();
        estPrime = new int[tasks.length];

        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_lst = sortWithJavaLibrary(tasks, new Task.ComparatorByLst(tasks)); //Increasing LST
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST
        tasks_indices_ect = sortWithJavaLibrary(tasks, new Task.ComparatorByEct(tasks)); //Increasing ECT
        tasks_indices_h = sortWithJavaLibrary(tasks, new Task.ComparatorByHeight(tasks)); //Increasing Height

        makespan = Integer.MAX_VALUE;
        TimeTableInitializeTimeLine();

    }



    public boolean OverloadCheck()
    {
        TimeTableInitializeIncrements(tasks_indices_lct.length - 1);
        if(TimeTableScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime(), C) > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return false;

        for(int i=tasks_indices_lct.length - 1; i>0; i--)
        {
            while(i > 0 && tasks[tasks_indices_lct[i]].latestCompletionTime() == tasks[tasks_indices_lct[i-1]].latestCompletionTime())
            {
                i--;
            }

            if(i == 0)
                return true;

            TimeTableInitializeIncrements(i-1);
            if(TimeTableScheduleTasks(tasks[tasks_indices_lct[i-1]].latestCompletionTime(), C) > tasks[tasks_indices_lct[i-1]].latestCompletionTime())
                return false;
        }

        return true;
    }


    public int[] TimeTableEdgeFinder_DetectionPruning()
    {
        for (int i = 0; i < tasks.length; i++)
            estPrime[i] = tasks[i].earliestStartingTime();
        TimeTableInitializeIncrements(tasks_indices_lct.length - 1);
        makespan = TimeTableScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime(), C);
        if(makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return estPrime;
        for(int i = 0; i < tasks_indices_lct.length - 1; i++)
        {
            while(i + 1 < tasks.length && tasks[tasks_indices_lct[i]].latestCompletionTime() == tasks[tasks_indices_lct[i+1]].latestCompletionTime())
            {
                i++;
            }
            if(i == tasks.length-1)
                return estPrime;
            int min = 0; int max = min + 1;
            while(max <= tasks.length)
            {
                if(max == tasks.length || tasks[tasks_indices_h[max]].height() > tasks[tasks_indices_h[min]].height()){
                    int h = -1;
                    int k = min;
                    while(k < max && h == -1){
                        if(!tasks[tasks_indices_h[k]].inLambda) {
                            h = tasks[tasks_indices_h[k]].height();
                        }
                        k++;
                    }
                    if(h != -1){
                        TimeTableInitializeIncrements(i);
                        int ect = TimeTableScheduleTasks(tasks[tasks_indices_lct[i]].latestCompletionTime(), h);
                        if(ect > tasks[tasks_indices_lct[i]].latestCompletionTime())
                            return estPrime;
                        DetectePrecedences(i, min, max-1);
                    }
                    min = max;
                }
                max++;
            }
        }

        return estPrime;
    }





    private void DetectePrecedences(int j, int min, int max){
        for(int i = min; i <= max; i++){
            if(tasks[tasks_indices_h[i]].latestCompletionTime() > tasks[tasks_indices_lct[j]].latestCompletionTime() &&
                    tasks[tasks_indices_h[i]].est_to_tttimepoint.contact != null &&
                    tasks[tasks_indices_h[i]].est_to_tttimepoint.contact.time < tasks[tasks_indices_h[i]].earliestCompletionTime()){
                if(tasks[tasks_indices_h[i]].hasFixedPart()){
                    if(tasks[tasks_indices_h[i]].earliestCompletionTime() < tasks[tasks_indices_lct[j]].latestCompletionTime()
                            && tasks[tasks_indices_h[i]].latestStartingTime() < tasks[tasks_indices_lct[j]].latestCompletionTime()){
                        TimeTableTimePoint t = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact;
                        int overlap1 = t.overlap;
                        int slackUnder1 = t.slackUnder;
                        t = tasks[tasks_indices_h[i]].lst_to_tttimepoint;
                        int overlap2 = t.overlap;
                        int slackOver1 = t.slackOver;
                        t = tasks[tasks_indices_lct[j]].lct_to_tttimepoint;
                        int slackUnder2 = t.slackUnder;
                        int slackOver2 = t.slackOver;
                        if(overlap2 - overlap1 > slackUnder2 - slackUnder1 + slackOver2 - slackOver1 ){
                            int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1 + slackOver2 - slackOver1);
                            int estt = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                            if(estt > estPrime[tasks_indices_h[i]])
                                estPrime[tasks_indices_h[i]] = Math.max(estPrime[tasks_indices_h[i]], estt);
                        }
                    }
                    if(tasks[tasks_indices_h[i]].earliestCompletionTime() >= tasks[tasks_indices_lct[j]].latestCompletionTime()
                            && tasks[tasks_indices_h[i]].latestStartingTime() < tasks[tasks_indices_lct[j]].latestCompletionTime()){
                        TimeTableTimePoint t = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact;
                        int overlap1 = t.overlap;
                        int slackUnder1 = t.slackUnder;
                        t = tasks[tasks_indices_h[i]].lst_to_tttimepoint;
                        int overlap2 = t.overlap;
                        int slackOver1 = t.slackOver;
                        t = tasks[tasks_indices_lct[j]].lct_to_tttimepoint;
                        int slackUnder2 = t.slackUnder;
                        int slackOver2 = t.slackOver;
                        if(overlap2 - overlap1 > slackUnder2 - slackUnder1 + slackOver2 - slackOver1){
                            int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1 + slackOver2 - slackOver1);
                            int estt = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                            if(estt > estPrime[tasks_indices_h[i]])
                                estPrime[tasks_indices_h[i]] = Math.max(estPrime[tasks_indices_h[i]], estt);
                        }
                    }
                    if(tasks[tasks_indices_h[i]].latestStartingTime() >= tasks[tasks_indices_lct[j]].latestCompletionTime()){
                        TimeTableTimePoint t = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact;
                        int overlap1 = t.overlap;
                        int slackUnder1 = t.slackUnder;
                        t = tasks[tasks_indices_lct[j]].lct_to_tttimepoint;
                        int overlap2 = t.overlap;
                        int slackUnder2 = t.slackUnder;
                        if(overlap2 - overlap1 > slackUnder2 - slackUnder1){
                            int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1 );
                            int estt = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                            if(estt > estPrime[tasks_indices_h[i]])
                                estPrime[tasks_indices_h[i]] = Math.max(estPrime[tasks_indices_h[i]], estt);
                        }
                    }
                }else{
                    if(tasks[tasks_indices_h[i]].earliestCompletionTime() < tasks[tasks_indices_lct[j]].latestCompletionTime()){
                        TimeTableTimePoint t = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact;
                        int overlap1 = t.overlap;
                        int slackUnder1 = t.slackUnder;
                        t = tasks[tasks_indices_h[i]].ect_to_tttimepoint;
                        int overlap2 = t.overlap;
                        int slackOver1 = t.slackOver;
                        t = tasks[tasks_indices_lct[j]].lct_to_tttimepoint;
                        int slackUnder2 = t.slackUnder;
                        int slackOver2 = t.slackOver;
                        if(overlap2 - overlap1 > slackUnder2 - slackUnder1 + slackOver2 - slackOver1){
                            int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1 + slackOver2 - slackOver1);
                            int estt = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                            if(estt > estPrime[tasks_indices_h[i]])
                                estPrime[tasks_indices_h[i]] = Math.max(estPrime[tasks_indices_h[i]], estt);
                        }
                    }else{
                        TimeTableTimePoint t = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact;
                        int overlap1 = t.overlap;
                        int slackUnder1 = t.slackUnder;
                        t = tasks[tasks_indices_lct[j]].lct_to_tttimepoint;
                        int slackUnder2 = t.slackUnder;
                        int overlap2 = t.overlap;
                        if(overlap2 - overlap1 > slackUnder2 - slackUnder1){
                            int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1);
                            int estt = tasks[tasks_indices_h[i]].est_to_tttimepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                            if(estt > estPrime[tasks_indices_h[i]])
                                estPrime[tasks_indices_h[i]] = Math.max(estPrime[tasks_indices_h[i]], estt);
                        }
                    }
                }
            }
        }
    }



    private void TimeTableInitializeTimeLine()
    {
        int n = tasks.length;
        tl.Add(new TimeTableTimePoint(tasks[tasks_indices_est[0]].earliestStartingTime(), C));
        TimeTableTimePoint t = tl.first;

        int p,i,j,k,l;
        p = i = j = k = l = 0;

        int maxLCT = Integer.MIN_VALUE;

        while(i < n || j < n || k < n || l < n)
        {
            if(i<n && (j == n || tasks[tasks_indices_est[i]].earliestStartingTime() <= tasks[tasks_indices_lst[j]].latestStartingTime()) &&
                      (k == n || tasks[tasks_indices_est[i]].earliestStartingTime() <= tasks[tasks_indices_ect[k]].earliestCompletionTime()) &&
                      (l == n || tasks[tasks_indices_est[i]].earliestStartingTime() <= tasks[tasks_indices_lct[l]].latestCompletionTime()))
            {
                if(tasks[tasks_indices_est[i]].earliestStartingTime() > t.time)
                {
                    t.InsertAfter(new TimeTableTimePoint(tasks[tasks_indices_est[i]].earliestStartingTime(), C));
                    t = t.next;
                }
                tasks[tasks_indices_est[i]].est_to_tttimepoint = t;
                p += tasks[tasks_indices_est[i]].processingTime();
                maxLCT = Math.max(maxLCT, tasks[tasks_indices_est[i]].latestCompletionTime());

                i++;
            }
            else if (j < n && (k == n || tasks[tasks_indices_lst[j]].latestStartingTime() <= tasks[tasks_indices_ect[k]].earliestCompletionTime()) &&
                              (l == n || tasks[tasks_indices_lst[j]].latestStartingTime() <= tasks[tasks_indices_lct[l]].latestCompletionTime()))
            {
                if (tasks[tasks_indices_lst[j]].latestStartingTime() > t.time) {
                    t.InsertAfter(new TimeTableTimePoint(tasks[tasks_indices_lst[j]].latestStartingTime(), C));
                    t = t.next;
                }
                tasks[tasks_indices_lst[j]].lst_to_tttimepoint = t;
                j++;
            }
            else if (k < n && (l == n || tasks[tasks_indices_ect[k]].earliestCompletionTime() <= tasks[tasks_indices_lct[l]].latestCompletionTime()))
            {
                if (tasks[tasks_indices_ect[k]].earliestCompletionTime() > t.time) {
                    t.InsertAfter(new TimeTableTimePoint(tasks[tasks_indices_ect[k]].earliestCompletionTime(), C));
                    t = t.next;
                }
                tasks[tasks_indices_ect[k]].ect_to_tttimepoint = t;
                k++;
            }
            else
            {
                if (tasks[tasks_indices_lct[l]].latestCompletionTime() > t.time) {
                    t.InsertAfter(new TimeTableTimePoint(tasks[tasks_indices_lct[l]].latestCompletionTime(), C));
                    t = t.next;
                }
                tasks[tasks_indices_lct[l]].lct_to_tttimepoint = t;
                l++;
            }
        }
        t.InsertAfter(new TimeTableTimePoint(maxLCT + p, 0));
    }




    private void TimeTableInitializeIncrements(int maxIndex)
    {
        TimeTableTimePoint t = tl.first;
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

            t.overlap = 0;
            t.slackOver = 0;
            t.slackUnder = 0;
            t.contact = null;

            t = t.next;
        }
        for(int i = 0; i < tasks.length; i++)
        {
            if(i <= maxIndex) {
                t = tasks[tasks_indices_lct[i]].est_to_tttimepoint;
                t.increment += tasks[tasks_indices_lct[i]].height();
                t.incrementMax += tasks[tasks_indices_lct[i]].height();

                t = tasks[tasks_indices_lct[i]].ect_to_tttimepoint;
                t.increment -= tasks[tasks_indices_lct[i]].height();

                t = tasks[tasks_indices_lct[i]].lct_to_tttimepoint;
                t.incrementMax -= tasks[tasks_indices_lct[i]].height();
                //658271403 timothée
            }else{
                if(tasks[tasks_indices_lct[i]].hasFixedPart()
                && tasks[tasks_indices_lct[i]].latestStartingTime() < tasks[tasks_indices_lct[maxIndex]].latestCompletionTime()){
                    t = tasks[tasks_indices_lct[i]].lst_to_tttimepoint;
                    t.increment += tasks[tasks_indices_lct[i]].height();
                    t.incrementMax += tasks[tasks_indices_lct[i]].height();
                    if(tasks[tasks_indices_lct[i]].earliestCompletionTime() < tasks[tasks_indices_lct[maxIndex]].latestCompletionTime()){
                        t = tasks[tasks_indices_lct[i]].ect_to_tttimepoint;
                        t.increment -= tasks[tasks_indices_lct[i]].height();
                        t.incrementMax -= tasks[tasks_indices_lct[i]].height();

                    }else{
                        t = tasks[tasks_indices_lct[maxIndex]].lct_to_tttimepoint;
                        t.increment -= tasks[tasks_indices_lct[i]].height();
                        t.incrementMax -= tasks[tasks_indices_lct[i]].height();
                    }
                }
            }
        }
    }




    private int TimeTableScheduleTasks(int maxLCT, int h)
    {
        int hreq, hmaxInc, ov, ect;
        int overlap, slackUnder, slackOver;
        overlap = slackOver = slackUnder = 0;
        ect = Integer.MIN_VALUE;
        ov = hreq = hmaxInc = 0;
        TimeTableTimePoint t = tl.first;

        while(t.time < maxLCT)
        {
            int l = t.next.time - t.time;
            t.overlap = overlap;
            t.slackUnder = slackUnder;
            t.slackOver = slackOver;

            hmaxInc += t.incrementMax;
            t.hMaxTotal = hmaxInc;
            int hmax = Math.min(hmaxInc, C);
            hreq += t.increment;
            t.hreal = hreq;

            int hcons = Math.min(hreq + ov, hmax);
            t.cons = hcons;

            if(ov > 0 && ov < (hcons - hreq) * l)
            {
                l = Math.max(1, ov / (hcons-hreq));
                t.InsertAfter(new TimeTableTimePoint(t.time + l, t.capacity));
            }
            ov += (hreq - hcons) * l;
            if(hcons > C - h) {
                t.contact = t;
            }

            overlap += Math.max(hcons - (C - h), 0) * l;
            slackOver += Math.max(hmax - Math.max(C - h, hcons), 0) * l;
            slackUnder += Math.max(Math.min(C - h, hmax) - hcons, 0) * l;

            t.capacity = C - hcons;

            if(t.capacity < C)
                ect = t.next.time;

            t = t.next;
        }
        t.overlap = overlap;
        t.slackUnder = slackUnder;
        t.slackOver = slackOver;
        TimeTableTimePoint best = null;
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
