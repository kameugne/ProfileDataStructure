public class Line {
    //Attributes used to modelize the linked list
    public Line next;
    public Line previous;

    //Attributes of a Line
    public int time;
    public Line(int ptime)
    {
        next = null;
        previous = null;
        time = ptime;
    }

    public void InsertAfter(Line tp)
    {
        tp.previous = this;
        tp.next = this.next;
        if(next != null)
        {
            next.previous = tp;
        }
        next = tp;
    }

    @Override
    public String toString() {
        return "Line : (t = " + this.time + ", next = " + this.next.time  + ")";
    }
}
