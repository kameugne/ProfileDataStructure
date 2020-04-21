import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

public class EdgeFinderVilim {
	Integer C;
	Task[] tasks;
	Integer[] Prec;
	
	Integer[] tasks_indices_lct;
	
	Integer makespan;
	private int[] estPrime;
	
	public EdgeFinderVilim(Task[] tasks, int C)
	{
		this.C = C;
		this.tasks = tasks;
		estPrime = new int[tasks.length];
		Prec = new Integer[tasks.length];
		for(int i=0; i<tasks.length; i++)
		{
			Prec[i] = tasks[i].earliestCompletionTime();
		}
		
		tasks_indices_lct = sortWithJavaLibrary(tasks, new Task.ReverseComparatorByLct(tasks)); //Increasing LCT
	}
	
	public int[] Filter()
	{
		int[] results = new int[tasks.length];
		if(EdgeFinder_Detection())
		{
			results = EdgeFinder_Pruning();
		}
		
		return results;
	}
	
	public boolean OverloadCheck()
	{
		///////////////////////
		// Overload Check 
		CumulativeThetaLambdaTree t = new CumulativeThetaLambdaTree(tasks, C);
		
		for(int j = 0; j < tasks.length; j++)
		{
			if (t.envOfTree() > C * tasks[tasks_indices_lct[j]].latestCompletionTime()) {
				return false;
			}
			t.moveFromThetaToLambda(tasks_indices_lct[j]);
		}
		return true;
	}
	
	public boolean EdgeFinder_Detection()
	{
		///////////////////////
		// Detection 
		/*Prec = new Integer[tasks.length];
		for(int i=0; i<tasks.length; i++)
		{
			Prec[i] = tasks[i].earliestCompletionTime();
		}*/
		CumulativeThetaLambdaTree t = new CumulativeThetaLambdaTree(tasks, C);
		
		makespan = (int)Math.ceil((double)t.envOfTree() / (double)C);
		for(int j = 0; j < tasks.length; j++)
		{
			//Overload Check
			if (t.envOfTree() > C * tasks[tasks_indices_lct[j]].latestCompletionTime()) {
				return false;
			}
			
			while(!t.lambdaEmpty() && t.lambdaEnvOfTree() > C * tasks[tasks_indices_lct[j]].latestCompletionTime())
			{
				int i = t.getEnvResponsibleTask();
				Prec[i] = Math.max(Prec[i], tasks[tasks_indices_lct[j]].latestCompletionTime());
				t.removeFromLambda(i);
			}
			t.moveFromThetaToLambda(tasks_indices_lct[j]);
		}
		return true;
	}
	
	public int[] EdgeFinder_Pruning()
	{
		for (int i = 0; i < tasks.length; i++)
			estPrime[i] = tasks[i].earliestStartingTime();
	    // Propagation
		Vector<Integer> heights = new Vector<Integer>();
		Integer[] tasks_indices_height = sortWithJavaLibrary(tasks, new Task.ComparatorByHeight(tasks)); //Increasing LCT
		
		Integer[] tasks_to_height_index = new Integer[tasks.length];
		
		//We find all the distinct heights and we map the task to their corresponding height
		heights.add(tasks[tasks_indices_height[0]].height());
		tasks_to_height_index[tasks_indices_height[0]] = 0;
		
		int count = 0;
		for(int i=1; i<tasks_indices_height.length; i++)
		{
			if(tasks[tasks_indices_height[i]].height() > tasks[tasks_indices_height[i-1]].height())
			{
				heights.add(tasks[tasks_indices_height[i]].height());
				count++;
			}
			tasks_to_height_index[tasks_indices_height[i]] = count;
		}
		
		//We compute an update value for every height and LCut.
		//The update values are initialized to minus infinity.
		Integer[][] update = new Integer[heights.size()][tasks.length];
		for(int i =0; i<heights.size(); i++)
		{
			for(int j=0; j < tasks.length; j++)
			{
				update[i][j] = Integer.MIN_VALUE;
			}
		}
		
		ExtendedThetaTree et = new ExtendedThetaTree(tasks, C);
		//For every distinct heights...
		for(int i=0; i<heights.size(); i++)
		{
			et.init(heights.get(i));
			
			int u = Integer.MIN_VALUE;
			//We want incremental LCuts. Therefore, we iterate through them in non decreasing order of lct.
			//...and for every LCut
			for(int j=tasks_indices_lct.length-1; j>=0; j--)
			{
				int lctj = (C - heights.get(i))*tasks[tasks_indices_lct[j]].latestCompletionTime();
				int eml = plus(et.Env(tasks_indices_lct[j]), -lctj);
				int diff;
				if(eml == Integer.MIN_VALUE)
					diff = Integer.MIN_VALUE;
				else
					diff = (int)Math.ceil((double)eml / (double)heights.get(i));
				
				u = Math.max(u, diff);
				update[i][tasks_indices_lct[j]] = u;
			}
		}
		
		//We sort the precedences
		Integer[] precMap = sortWithJavaLibraryInNonIncreasingOrder(Prec); //Increasing LCT
		
		//We update the earliest starting times by iterating through the precedences
		int curJ = 0;
		for(int i=0; i<tasks.length; i++)
		{
			while(curJ < tasks.length && tasks[tasks_indices_lct[curJ]].latestCompletionTime() > Prec[precMap[i]])
				curJ ++;
			if(curJ >= tasks.length)
				break;
			
			int locJ = curJ;
			do
			{
				if(tasks[tasks_indices_lct[locJ]].latestCompletionTime() != tasks[precMap[i]].latestCompletionTime())
				{
					estPrime[precMap[i]] = Math.max(estPrime[precMap[i]], update[tasks_to_height_index[precMap[i]]][tasks_indices_lct[locJ]]);
					//tasks[precMap[i]].setEarliestStartingTimeWithCheck(
							//update[tasks_to_height_index[precMap[i]]][tasks_indices_lct[locJ]]);
				}
			}
			while(tasks[tasks_indices_lct[locJ]].latestCompletionTime() == Prec[precMap[i]] &&
								locJ++ < tasks.length - 1);
		}
		return estPrime;
	}
	
	
	/* ------------ Utility functions --------------*/
	private static int plus(int a, int b)
    {
    	if(a == Integer.MIN_VALUE || b == Integer.MIN_VALUE)
    		return Integer.MIN_VALUE;
    	else 
    		return a + b;
    }
	
	public static Integer[] sortWithJavaLibrary(Task[] tasks, Comparator<Integer> comparator) {

        int n = tasks.length;
        Integer[] tasks_indices = new Integer[n];
        for (int q = 0; q < n; q++) {
            tasks_indices[q] = new Integer(q);
        }  
        Arrays.sort(tasks_indices, comparator);
        return tasks_indices;
    }
	
	public static Integer[] sortWithJavaLibraryInNonIncreasingOrder(Integer[] array) {

        int n = array.length;
        Integer[] tasks_indices = new Integer[n];
        for (int q = 0; q < n; q++) {
            tasks_indices[q] = q;
        }  
        Arrays.sort(tasks_indices, new IntComparator(array));
        return tasks_indices;
    }
	
	public static class IntComparator implements Comparator<Integer> {
        private  Integer[] array;

        public IntComparator( Integer[] array) {

            this.array = array;
        }
        @Override
        public int compare(Integer a, Integer b) {
        	return array[b] - array[a];

        }
    }
}
