import java.util.Arrays;

public class CumulativeThetaLambdaTree {
	
	private static int MINUSINFINITY = 0x80000000;
	
	Task[] tasks;
	private int capacity;
	
    private int env_of_nodes[];
    private int e_of_nodes[];
    private int lambda_env_of_nodes[];
    private int lambda_e_of_nodes[];

    private Integer responsible_e[];
    private Integer responsible_env[];
    
    private int n;
    private int firstIndexOnTheLowestLevel;
    private int lastIndexOnTheLowestLevel;
    private int task_index_to_node_index[];
    
    /*
     * The tree is initialized with Theta = T and Lambda = \empty, meaning that that no task is initially in Lambda.
     * Therefore, each leaf is initialized with the attributes of the corresponding task.
     */
    public CumulativeThetaLambdaTree(Task[] tasks, int C) {
        this.tasks = tasks;
        this.n = tasks.length;
        this.capacity = C;
        
        this.env_of_nodes = new int[2 * n - 1];
        this.e_of_nodes = new int[2 * n - 1];
        this.lambda_env_of_nodes = new int[2 * n - 1];
        this.lambda_e_of_nodes = new int[2 * n - 1];
        
        this.responsible_e = new Integer[2 * n - 1];
        this.responsible_env = new Integer[2 * n - 1];
               
        this.task_index_to_node_index = new int [n];    
        this.firstIndexOnTheLowestLevel = nextPowerOfTwoMinusOne(n);       
        this.lastIndexOnTheLowestLevel = 2 * (n - 1);
       
        Integer[] task_indices = new Integer[tasks.length];
        for (int q = 0; q < n; q++) {
           task_indices[q] = new Integer(q);
        }
        Arrays.sort(task_indices, new Task.ComparatorByEst(tasks));      
        for (int q = 0; q < n; q++) {
            task_index_to_node_index[task_indices[q]]  = q;          
            
            int b = getNodeIndexWithLeafIndex(q);

            int j = task_indices[q];
            env_of_nodes[b] = tasks[j].envelop(C);
            e_of_nodes[b] = tasks[j].energy();
            
            lambda_env_of_nodes[b] = MINUSINFINITY;
            lambda_e_of_nodes[b] = MINUSINFINITY;

            responsible_e[b] = null;
            responsible_env[b] = null;
        }
      
        //The inner-nodes are updated
        for(int i = n-2; i>=0; i--)
        {  	
        	updateNode(i);
        }
    }
    
    public void moveFromThetaToLambda(int index) {
    	int i = task_index_to_node_index[index];
        int b = getNodeIndexWithLeafIndex(i);     

        env_of_nodes[b] = MINUSINFINITY;
        e_of_nodes[b] = 0;
        
        lambda_env_of_nodes[b] = tasks[index].envelop(capacity);
        lambda_e_of_nodes[b] = tasks[index].energy();

        responsible_e[b] = index;
        responsible_env[b] = index;
        
        updateInnerNodes(b);
    }
    
    public void removeFromLambda(int index) {
    	int i = task_index_to_node_index[index];	
    	int b = getNodeIndexWithLeafIndex(i);        

    	lambda_env_of_nodes[b] = MINUSINFINITY;
    	lambda_e_of_nodes[b] = MINUSINFINITY;
        
    	responsible_e[b] = null;
    	responsible_env[b] = null;
        
        updateInnerNodes(b);
    }

    public int envOfTree() {
        return env_of_nodes[0];
    }
    
    public int lambdaEnvOfTree() {
    	return lambda_env_of_nodes[0];
    }
    
    public Integer getEnvResponsibleTask()
    {
    	return responsible_env[0];
    }
    
    public boolean lambdaEmpty(){
    	return responsible_env[0] == null;
    }

    
    
//PRIVATE FUNCTIONS -------------------------------------------------------------
    
    private void updateInnerNodes(int leafIndex)
    {
    	int w = (leafIndex - 1) / 2;
    	int t = 0;
    	
    	if(leafIndex == 0)
    			return;
    	
        do {	
        	updateNode(w);
        	
            w =  (int) Math.floor((w - 1)/ 2);
            if (w == 0) 
                t++;
            if (t > 1) 
                break;
            
        } while (w >= 0);
    }
    
    private void updateNode(int i)
    {
    	int left, right;
    	left = 2 * i + 1;
    	right = 2 * i + 2;
    	
    	env_of_nodes[i] = Math.max(env_of_nodes[right], plus(env_of_nodes[left], e_of_nodes[right]));
    	e_of_nodes[i] = plus(e_of_nodes[left], e_of_nodes[right]);
        
        //For gray nodes
    	lambda_env_of_nodes[i] = Math.max(Math.max(	lambda_env_of_nodes[right], 
    												plus(lambda_env_of_nodes[left], e_of_nodes[right])), 
    												plus(env_of_nodes[left] ,lambda_e_of_nodes[right]));
    	
    	lambda_e_of_nodes[i] = Math.max(	plus(lambda_e_of_nodes[left], e_of_nodes[right]), 
    										plus(e_of_nodes[left], lambda_e_of_nodes[right]));
        
        
        if(lambda_env_of_nodes[i] == lambda_env_of_nodes[right])
        	responsible_env[i] = responsible_env[right];
        else if (lambda_env_of_nodes[i] == plus(env_of_nodes[left], lambda_e_of_nodes[right]))
        	responsible_env[i] = responsible_e[right];
        else
        	responsible_env[i] = responsible_env[left];
        
        if(lambda_e_of_nodes[i] == plus(e_of_nodes[left], lambda_e_of_nodes[right]))
        	responsible_e[i] = responsible_e[right];
        else
        	responsible_e[i] = responsible_e[left];   
    }
    
    private int plus(int a, int b)
    {
    	if(a == MINUSINFINITY || b == MINUSINFINITY)
    		return MINUSINFINITY;
    	else 
    		return a + b;
    }
     
    private int getNodeIndexWithLeafIndex(int index)
    {
    	int b;
    	if (index <= (lastIndexOnTheLowestLevel - firstIndexOnTheLowestLevel))   
            b = firstIndexOnTheLowestLevel + index;
        else 
            b = lastIndexOnTheLowestLevel / 2 + index - ((lastIndexOnTheLowestLevel - firstIndexOnTheLowestLevel) + 1);  
    	
    	return b;
    }
    
    // Returns 2^ceil(lg(n)) - 1
    private static int nextPowerOfTwoMinusOne(int n) {
        // If n is a power of two
        if ((n & (n - 1)) == 0)
            return n - 1;
        int shift = 1;
        int result = n;
        do {
            n = result;
            result = n | (n >> shift);
            shift += shift;
        }
        while (n != result);
        return result;
    }

}