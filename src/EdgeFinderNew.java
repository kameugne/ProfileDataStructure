import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
public class EdgeFinderNew {

    Integer C;
    Task[] tasks;
    private Profile tl;
    //private Timepoint tpinit;
    //private Integer[] Prec;
    // private Integer[] est;
    // private Integer[] type;
    private Line h,lct;
    Timepoint ht;
    private Integer[] tasks_indices_lct;
    private Integer[] tasks_indices_h;
    private Integer[] tasks_indices_est;
    private Integer[] tasks_indices_ect;
    Integer makespan;
    int lctn,hn,htn;

    private int[]

            estPrime; // garder les valeurs d'ajustement

    public EdgeFinderNew(Task[] tasks, int C)
    {
        this.C = C;
        this.tasks = tasks;
        this.tl = new Profile();
        estPrime = new int[tasks.length];

       /* Prec = new Integer[tasks.length];
        Arrays.fill(Prec, -1);
        est = new Integer[tasks.length];
        Arrays.fill(est, -1);
        type = new Integer[tasks.length];
        Arrays.fill(type, -1);*/

        tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ComparatorByLct(tasks)); //Increasing LCT
        tasks_indices_est = sortWithJavaLibrary(tasks, new Task.ComparatorByEst(tasks)); //Increasing EST
        tasks_indices_ect = sortWithJavaLibrary(tasks, new Task.ComparatorByEct(tasks)); //Increasing ECT
        tasks_indices_h = sortWithJavaLibrary(tasks, new Task.ComparatorByHeight(tasks)); //Increasing Height
        this.h =new Line(0);
        this.lct=new Line(0);
        this.ht = new Timepoint(0, C);
        lctn=hn=htn=0;
        makespan = Integer.MAX_VALUE;
        InitializeTimeLine();
    }

    /*
    The filtering algorithm performs the adjustment after each detection
     */

    public int[] Filter()
    {
        int[] results = new int[tasks.length];
        results = Detection_Adjustment();
        return results;
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
/*

    private int[] Detection_Adjustment()
    {
        Line Height=h.next;
        Line d=lct;
        for (int i = 0; i < tasks.length; i++)
        {
            estPrime[i] = tasks[i].earliestStartingTime();
        }
        InitializeIncrements(tasks_indices_lct.length - 1);
        makespan = ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime(), C);
        if(makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return estPrime;

        if(d!=null )
        {
            int i;
            while(d!=null )
            {
                i=d.time;
               // if(d.next!=null && (d.next.time-i)>1) i=d.next.time-1;
                int min=0;
                int max;
                if (Height != null)
                {
                    while(Height!=null) {
                        if (Height.time < tasks_indices_h.length-1 && Height.next == null) max = tasks_indices_h.length;
                        else max = Height.time;
                        int h = -1;
                        int k = min;
                        while (k < max && h == -1) {
                            if (tasks[tasks_indices_h[k]].latestCompletionTime() > tasks[tasks_indices_lct[i]].latestCompletionTime())
                                h = tasks[tasks_indices_h[k]].height();
                            k++;
                        }
                        if (h != -1) {
                            InitializeIncrements(i);
                            if (ScheduleTasks(tasks[tasks_indices_lct[i]].latestCompletionTime(), h) > tasks[tasks_indices_lct[i]].latestCompletionTime())
                                return estPrime;
                            DetectePrecedences(i, min, max - 1);
                        }
                        min = max;
                        Height = Height.next;
                    }
                }else{
                    max=tasks_indices_h.length;
                    int h = -1;
                    int k = min;
                    while (k < max && h == -1) {
                        if (tasks[tasks_indices_h[k]].latestCompletionTime() > tasks[tasks_indices_lct[i]].latestCompletionTime())
                            h = tasks[tasks_indices_h[k]].height();
                        k++;
                    }
                    if (h != -1) {
                        InitializeIncrements(i);
                        if (ScheduleTasks(tasks[tasks_indices_lct[i]].latestCompletionTime(), h) > tasks[tasks_indices_lct[i]].latestCompletionTime())
                            return estPrime;
                        DetectePrecedences(i, min, max - 1);
                    }
                }
                d=d.next;
            }
        }else return estPrime;
        return estPrime;
    }
 */



/*
private int[] Detection_Adjustment() {

    PrintLine();
    PrintLine2();
    PrintTimepoint();
    estPrime[0]= hn;
    estPrime[1]=htn;
    estPrime[2]=lctn;
    for (int i = 3; i < tasks.length; i++)
    {
        estPrime[i] = -1;
    }
     return estPrime ;//"( longueur de ht= "+ htn + "    Longueur de lct= " + lctn +"   Longueur de h= " + hn;

}
*/

    public int[] Detection_Adjustment()
    {
        //System.out.println(tasks.length);
        //PrintLine();
        //PrintLine2();
        //PrintTimepoint();

        Line d=lct;
        Line Height=h.next;
        for (int i = 0; i < tasks.length; i++)
        {
            estPrime[i] = tasks[i].earliestStartingTime();
        }
        InitializeIncrements(tasks_indices_lct.length - 1);
        makespan = ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime(), C);
        if(makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return estPrime;

        if(d!=null )
        {
            int i;
            while(d!=null )
            {
                i=d.time;
                //if(d.next!=null && d.next.time-d.time >1) i=d.next.time-1;
                //else if(d.next==null && d.time<tasks_indices_lct.length-1) i=tasks_indices_lct.length-1;
                int min=0;
                int max;
                if (Height != null )
                {
                    while(Height!=null)
                    {
                        if (Height.time < tasks_indices_h.length-1 && Height.next == null) max = tasks_indices_h.length;
                        else max = Height.time;
                        int h = -1;
                        int k = min;
                        InitializeIncrements(i);
                        while (k < max)
                        {
                            if (tasks[tasks_indices_h[k]].latestCompletionTime() > tasks[tasks_indices_lct[i]].latestCompletionTime())
                                h = tasks[tasks_indices_h[k]].height();
                            k++;
                        }
                        if (h != -1) {

                            if (ScheduleTasks(tasks[tasks_indices_lct[i]].latestCompletionTime(), h) > tasks[tasks_indices_lct[i]].latestCompletionTime())
                                return estPrime;
                            DetectePrecedences(i, min, max - 1);
                        }
                        min = max;
                        Height = Height.next;
                    }
                }else {
                    max=tasks_indices_h.length;
                    int h = -1;
                    int k = min;
                    while (k < max && h == -1) {
                        if (tasks[tasks_indices_h[k]].latestCompletionTime() > tasks[tasks_indices_lct[i]].latestCompletionTime())
                            h = tasks[tasks_indices_h[k]].height();
                        k++;
                    }
                    if (h != -1) {
                        InitializeIncrements(i);
                        if (ScheduleTasks(tasks[tasks_indices_lct[i]].latestCompletionTime(), h) > tasks[tasks_indices_lct[i]].latestCompletionTime())
                            return estPrime;
                        DetectePrecedences(i, min, max - 1);
                    }
                }
                d=d.next;
            }
        }else return estPrime;
        return estPrime;
    }

    private int[] Detection_AdjustmentR()
    {
        for (int i = 0; i < tasks.length; i++)
        {
            estPrime[i] = tasks[i].earliestStartingTime();
        }
        InitializeIncrements(tasks_indices_lct.length - 1);
        makespan = ScheduleTasks(tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime(), C);
        if(makespan > tasks[tasks_indices_lct[tasks_indices_lct.length - 1]].latestCompletionTime())
            return estPrime;
        for(int i = 0; i < tasks_indices_lct.length -1; i++)
        {
            while(i+1 < tasks.length && tasks[tasks_indices_lct[i]].latestCompletionTime() == tasks[tasks_indices_lct[i+1]].latestCompletionTime())
            {
                i++;
            }
            if(i == tasks.length-1)  return estPrime;

            int min = 0; int max = min + 1;
            while(max <= tasks.length)
            {
                if(max == tasks.length || tasks[tasks_indices_h[max]].height() > tasks[tasks_indices_h[min]].height()){
                    int h = -1;
                    int k = min;
                    while(k < max && h == -1)
                    {
                        if(tasks[tasks_indices_h[k]].latestCompletionTime()>tasks[tasks_indices_lct[i]].latestCompletionTime())
                            h = tasks[tasks_indices_h[k]].height();
                        k++;
                    }
                    if(h != -1){
                        InitializeIncrements(i);
                        if(ScheduleTasks(tasks[tasks_indices_lct[i]].latestCompletionTime(), h) > tasks[tasks_indices_lct[i]].latestCompletionTime())
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
            if(hcons > C - h)
            {
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
        while(t.previous != null)
        {
            if(t.contact != null && best == null)
                best = t.contact;
            if(t.contact != null && t.previous.contact == null)
                t.previous.contact = t.contact;
            if(t.previous.contact != null && best != null)
            {
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
            if(tasks[tasks_indices_h[i]].latestCompletionTime()>tasks[tasks_indices_lct[j]].latestCompletionTime() && tasks[tasks_indices_h[i]].est_to_timepoint.contact != null &&
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
                    if(overlap2 - overlap1 > slackUnder2 - slackUnder1 + slackOver2 - slackOver1)
                    {
                        int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1);
                        int estt = tasks[tasks_indices_h[i]].est_to_timepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                        if (estt>estPrime[tasks_indices_h[i]]) estPrime[tasks_indices_h[i]] =  estt;
                    }
                }else{
                    Timepoint t = tasks[tasks_indices_h[i]].est_to_timepoint.contact;
                    int overlap1 = t.overlap;
                    int slackUnder1 = t.slackUnder;
                    t = tasks[tasks_indices_lct[j]].lct_to_timepoint;
                    int slackUnder2 = t.slackUnder;
                    int overlap2 = t.overlap;
                    if(overlap2 - overlap1 > slackUnder2 - slackUnder1)
                    {
                        //tasks[tasks_indices_h[i]].inLambda = true;
                        int rest = overlap2 - overlap1 -(slackUnder2 - slackUnder1);
                        int estt = tasks[tasks_indices_h[i]].est_to_timepoint.contact.time + (int)Math.ceil((double)(rest) / (double)(tasks[tasks_indices_h[i]].height()));
                        if (estt>estPrime[tasks_indices_h[i]]) estPrime[tasks_indices_h[i]] =  estt;
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
        //Timepoint htt = ht;
        Line l=h;
        Line P=lct;

        for (int i=1; i<tasks_indices_h.length; i++) {
            if (tasks[tasks_indices_h[i]].height() > tasks[tasks_indices_h[l.time]].height()) {
                l.InsertAfter(new Line(i));
                l = l.next;
                hn++;
            }
        }
        for (int i=1; i<tasks_indices_lct.length; i++) {
            if (tasks[tasks_indices_lct[i]].latestCompletionTime() > tasks[tasks_indices_lct[P.time]].latestCompletionTime()) {
                P.InsertAfter(new Line(i));
                P = P.next;
                lctn++;
            }
        }
      /*  for (int i=1; i<tasks_indices_h.length; i++)
        {
            if (tasks[tasks_indices_h[i]].height() > htt.time) {
                htt.InsertAfter(new Timepoint(tasks_indices_lct[i],C));
                htt= htt.next;
                htn++;
            }
        }*/

        int p,i,j,k;
        p = i = j = k = 0;

        int maxLCT = Integer.MIN_VALUE;

        while (i < n || j < n || k < n)
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

                //tasks[tasks_indices_est[i]].inLambda = false;

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
        System.out.println(" listes des timepoints ht \n");
        while(t != null)
        {
            String info =  "Timepoint: (t = " + t.time + ", capacity= " + t.capacity +  ", hreq = "+ t.increment + ", hmax = " + t.incrementMax + ") ";
            //String info = "Timepoint: (t = " + t.time +")";
            espace += info;
            t = t.next;
        }
        System.out.println(espace);
    }

    public void PrintLine() {
        // TODO Auto-generated method stub
        //String info = " ";
        String espace = " ";

        Line t = h;
        System.out.println("liste des hauteurs");
        while (t != null) {
            String info = "Line:" + " (t = " + t.time + ") ";
            espace += info;
            t = t.next;
        }
        System.out.println(espace + " hauteurs \n");
    }
    public void PrintLine2() {
        // TODO Auto-generated method stub
        //String info = " ";
        String espace = " ";
        Line lt = lct;
        System.out.println("liste des lct");
        while(lt != null)
        {
            String info =  "Line:"+" (t = " + lt.time +  ") ";
            espace += info;
            lt = lt.next;
        }
        System.out.println(espace +" liste des lct \n");

    }
}
