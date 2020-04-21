import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by vincent on 2017-01-30.
 */
public class EvaluationPetitExample{
	
    public static void main(String[] args) throws Exception
    {
        boolean found = false;
        int C = 2;
        int n = 3;
        
        for (int i=0; i<900000000; i++)
        {
            Task[] tasks = TaskGenerator.generateCumulativeTaks(n, 0, 15, C);
            //EdgeFinder nef = new EdgeFinder(tasks, C);
            //CubicEdgeFinder cef = new CubicEdgeFinder(tasks, C);
            EdgeFinderVilim vef = new EdgeFinderVilim(tasks, C);
            EdgeFinderNew efn = new EdgeFinderNew(tasks, C);
            //NewEdgeFinder nef = new NewEdgeFinder(tasks, C);
            //TimeTableEdgeFinder ttef = new TimeTableEdgeFinder(tasks, C);
            //HorizontallyEdgeFinder hef = new HorizontallyEdgeFinder(tasks, C);
            //NewEdgeFinderKameugne nef = new NewEdgeFinderKameugne(tasks, C);

            //QuadraticNotFirst qnf = new QuadraticNotFirst(tasks, C);
            //TimeLineNotFirst tlnf = new TimeLineNotFirst(tasks, C);



            //int[] nest = nef.Filter();
            int[] vest = vef.Filter();
            int[] estn = efn.Filter();
            //int[] ttest = ttef.Filter();
            boolean validInstance = true;
            if(!vef.OverloadCheck())
            {
                validInstance = false;
            }
            else
            {
                for(int l=0; l < tasks.length; l++){
                    if(estn[l] > tasks[l].earliestStartingTime() || estn[l] + tasks[l].processingTime() > tasks[l].latestCompletionTime()){
                    	validInstance = false;
                        break;
                    }
                    /*if(nest[l] + tasks[l].processingTime() > tasks[l].latestCompletionTime()){
                    	validInstance = false;
                        break;
                    }*/
                }

                for(int t=0; t<=15; t++)
                {
                    int h = 0;
                    for(int l=0; l < tasks.length; l++){
                        if(t >= tasks[l].latestStartingTime() && t < tasks[l].earliestCompletionTime())
                            h+=tasks[l].height();
                    }
                    if(h > C)
                    {
                        validInstance = false;
                        break;
                    }
                    
                    for(int k = 0; k < tasks.length; k++){
                    	int TT  = 0;
                    	for(int l = 0; l < tasks.length; l++){
                    		if(k != l && t >= tasks[l].latestStartingTime() && t < tasks[l].earliestCompletionTime())
                    			TT += tasks[l].height();
                    	}
                    	if(t < tasks[k].earliestCompletionTime() && tasks[k].height() + TT > C)
                    	{
                    		validInstance = false;
                            break;
                    	}
                    }
                }
            }
            
            for(int j=0; j < vest.length; j++)
            {
                
                if(validInstance && efn.OverloadCheck() && vest[j] > estn[j] && vest[j] + tasks[j].processingTime() <= tasks[j].latestCompletionTime())
                {


                    for(int k=0; k<tasks.length; k++)
                    {
                        System.out.println("k = " + k + "  : " + tasks[k].toString());
                    }
                    //System.out.println("new edge finder : " + Arrays.toString(nest));
                    System.out.println("edge finder : " + Arrays.toString(estn));
                    System.out.println("vilim edge finder : " + Arrays.toString(vest));
                    System.out.println("C = " + C + "-----" + "i = " +j);

                    found = true;
                    break;
                }
            }
            if(found)
                break;
        }
    }

	

	/*private static void ScheduleConflictingTask(Task[] tasks, int C, int j) {
		// TODO Auto-generated method stub
		int ov = 0;  
		for(int t=0; t<=15; t++)
        {
			int hmax = 0; int hreq = 0;
			for(int k=0; k<tasks.length; k++)
            {
				if(k != j && tasks[k].earliestStartingTime() <= t && t < tasks[k].latestCompletionTime() && tasks[j].earliestStartingTime() < tasks[k].earliestCompletionTime())
					hmax += tasks[k].height();
				if(k != j && tasks[k].earliestStartingTime() <= t && t < tasks[k].earliestCompletionTime() && tasks[j].earliestStartingTime() < tasks[k].earliestCompletionTime())
					hreq += tasks[k].height();
            }
			if(t <= tasks[j].earliestCompletionTime())
			{
				hmax += tasks[j].height();
				hreq += tasks[j].height();
			}
			int Hmax = Math.min(hmax, C);
			int hcons = Math.min(hreq + ov, Hmax);
			ov = ov + hreq - hcons;
			for(int k=0; k<tasks.length; k++)
			{
				if(t == tasks[k].latestCompletionTime())
					System.out.println(tasks[k].toString() + ", overflow = " + ov + ", hreq = "+ hreq + ", hmax = "+ Hmax + ", hcons = "+ hcons);
				//if(t == tasks[k].earliestStartingTime())
					//System.out.println(tasks[k].toString() + ", est.overflow = " + ov + ", hreq = "+ hreq + ", hmax = "+ Hmax + ", hcons = "+ hcons);
			}
        }
	}*/

	
}
