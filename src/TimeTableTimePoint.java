public class TimeTableTimePoint {
    //Attributes used to modelize the linked list
    public TimeTableTimePoint next;
    public TimeTableTimePoint previous;

    //Attributes of a TimeTableTimepoint
    public int time;
    public int capacity;

    //Attributes used by the Edge-Finder filtering algorithm
    public int increment;
    public int incrementMax;
    public int hMaxTotal;
    public int hreal;
    public int overflow;
    public int cons;
    public int minimumOverflow;
    public int avail;
    public int slackUnder;
    public int slackOver;
    public int overlap;
    public int conflictingTime;
    public int dreal;
    public TimeTableTimePoint contact;
    public int l;

    public TimeTableTimePoint(int ptime, int pcapacity)
    {
        next = null;
        previous = null;
        time = ptime;
        capacity = pcapacity;
        increment = 0;
        incrementMax = 0;
        overflow = 0;
        cons = 0;
        minimumOverflow = 0;
        hMaxTotal = 0;
        hreal = 0;
        avail = 0;
        slackUnder = 0;
        slackOver = 0;
        overlap = 0;
        conflictingTime = -1;
        dreal = 0;
        contact = null;
    }

    public void InsertAfter(TimeTableTimePoint tl)
    {
        tl.previous = this;
        tl.next = this.next;
        if(next != null)
        {
            next.previous = tl;
        }
        next = tl;
    }

    @Override
    public String toString() {
        return "TimeTableTimepoint : (t = " + this.time + ", next = " + this.next.time + ", c = " + this.capacity + ")";
    }
}
