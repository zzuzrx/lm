package com.lm.algorithms.abc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.lm.algorithms.MetaHeuristicScheduler;
import com.lm.algorithms.abc.Chromosome;
import com.lm.algorithms.measure.Makespan;
import com.lm.algorithms.measure.MetaIMeasurance;
import com.lm.Metadomain.CellSet;
import com.lm.Metadomain.JobSet;
import com.lm.Metadomain.MachineSet;
import com.lm.Metadomain.Operation;
import com.lm.statistic.RuleFrequencyStatistic;
import com.lm.util.Constants;
import com.lm.util.HeapMaxPriorityQueue;
import com.lm.util.MapUtil;
import com.lm.util.MaxPriorityQueue;

import java.lang.StringBuffer;
/*
 * @Description:DABC
 * 
 */

public class DABC5 {


/*******************************************属性域*******************************************************/
	/**针对具体问题的调度过程**/
	protected MetaHeuristicScheduler evaluator;
	/**适应度评估方法**/
	protected MetaIMeasurance measurance ;
	/**用于随机的种子**/
	protected Random rand = new Random(System.currentTimeMillis());
	/**最佳的适应度函数的value**/
	protected double bestFunction = 0d;
	/**最差的适应度函数的value**/
	protected double worstFunction = 0d;
	
	//种群相关数据
	/**每一代的种群**/
	protected List<Chromosome> Population;
	/**记忆池**/
	protected HeapMaxPriorityQueue<Chromosome> Memory;
	/**CurHeap**/
	protected HeapMaxPriorityQueue<Chromosome> CurHeap;
    /**最佳染色体序列**/
	protected Chromosome bestChromosome;
	/**最差染色体序列**/
	protected Chromosome worstChromosome;
	//算法参数
	protected int POPULATION_SIZE=48;                       //修改量POPULATION_SIZE
	/**the maxmum of iteration. default=100**/
	protected final int MaxCycle=500;                              //修改量MaxCycle
	/**factor for x(best) - x(i)**/
	protected final double MutateFactor1 = 0.5;
	/**factor for x(r1) - x(2^)**/
	protected final double MutateFactor2 = 0.5;
	
	protected final double threshold = 1.03; 
	//the input data for inter-cell problems
	/**the machine's set **/
	protected MachineSet mSet;
	/**the job's set **/
	protected JobSet jSet;
	/**the cell's set **/
	protected CellSet cellSet;
    
	
		
/***************************构造函数域***********************************************************************/
	
	/** 
	 * @Description:construction of DABC:默认参数
	 * @param mSet
	 * @param jSet
	 * @param cellSet
	 * @param scheduler
	 * @param measurance
	 */
	public DABC5(MachineSet mSet, JobSet jSet, CellSet cellSet,
			MetaHeuristicScheduler scheduler, MetaIMeasurance measurance) {
		this(mSet, jSet, cellSet, scheduler, measurance,500, 48);
	}

	/**
	 * @Description:construction of DABC:自定义参数
	 * @param mSet
	 * @param jSet
	 * @param cellSet
	 * @param scheduler
	 * @param measurance
     * @param populationSize
	 * DABC
	 * @exception:
	 */
	public DABC5(MachineSet mSet, JobSet jSet, CellSet cellSet,
			MetaHeuristicScheduler scheduler, MetaIMeasurance measurance, int Maxcycle,
			int populationSize) {
		this.mSet = mSet;
		this.jSet = jSet;
		this.cellSet = cellSet;
		this.measurance = measurance;
		this.POPULATION_SIZE = populationSize;
		this.evaluator = scheduler;
		this.Population = new ArrayList<Chromosome>();
		this.CurHeap = new HeapMaxPriorityQueue<Chromosome>(populationSize/5);
		this.Memory  = new HeapMaxPriorityQueue<Chromosome>(populationSize);
	}

/*****************************************************************************************************/

/****************************方法域*********************************************************************/
	/**目标函数&适应度函数：makespan或者totalweightedtardiness*/

	/**获取bestfunctionvalue**/
	public  double getBestFunctionValue() {
	    return bestFunction; 
	}
	
	/**获取worstfunctionvalue**/
    public double getWorstFunctionValue(){
		return worstFunction;
	}


	/** MAIN PROCESS
	 * @throws CloneNotSupportedException 
	 * @Description framework for DABC
	 * 
	 */
//	public void schedule() throws CloneNotSupportedException {
//		
//		int iter=0;
//		init_population();
//		updateBestChromosome();                       //保存种群中最好的那个调度解
//		System.out.println("本次循环开始");
//		for (iter=0;iter<MaxCycle;iter++){        //迭代数
//			System.out.println("第"+iter+"代：");
//			EmployedBees();
//		    OnlookerBees();
//		    updateBestChromosome();
//		    updateMemory();	
//		    ScoutBees();
//			if(iter==499){
//			    System.out.println("该种群中最优秀的调度解：");
//				System.out.println("最优解的函数值:"+bestFunction);
//			}
//			//System.out.println("代:"+iter+"的解："+bestFunction);
//		}
//	}
public void schedule(int caseIndex) throws CloneNotSupportedException {
		
		int iter=0;
		int counter[]=new int[POPULATION_SIZE];           //EmployedBee里的计数器
		double result1[] =new double[POPULATION_SIZE];           //EmployedBee里上上代解函数值
		double result2[] =new double[POPULATION_SIZE];           //EmployedBee里上代解函数值
		
		for(int i=0;i<POPULATION_SIZE;i++){
			counter[i]=0;
		}
		
		init_population(caseIndex);
		updateBestChromosome();                       //保存种群中最好的那个调度解
		for (iter=0;iter<MaxCycle;iter++){        //迭代数
//			System.out.println("第"+iter+"代：");
			
			//emplyed bee's searching
			if(iter == 0){ // first step: localsearch randomly
				for(int i=0;i<POPULATION_SIZE;i++){                //用来存储EmployedBee里需要用到的上上代的函数值；
					result1[i]=Population.get(i).getFunction();
				    result2[i]=Population.get(i).getFunction();
				}
			
				LocalSearch1();	
			
//				LocalSearch2();
			
			}else{
				
				for(int i=0;i<POPULATION_SIZE;i++){         //计数器对种群整个+1
					
					counter[i]++;
				}
				EmployedBees(counter,result1,result2);
			}
		    //onlooker bee's searching
//			OnlookerBees();
		    updateBestChromosome();
//		    updateMemory();	
		    //scout bee's searching
//		    ScoutBees();
		    for(int i=0;i<POPULATION_SIZE;i++){					//实现上上代的函数值和上代的函数值的记录
		    	result1[i]=result2[i];
		    	result2[i]=Population.get(i).getFunction();
		    }
		    
		    
		    if(iter==499){
			    System.out.println("该种群中最优秀的调度解：");
				System.out.println("最优解的函数值:"+bestFunction);
			}
			System.out.println(bestFunction);
		}
	}

	
	/**
	 * @Description 根据这一代的迭代结果CurHeap，更新全局的memory
	 */
	private void updateMemory() {
		// TODO Auto-generated method stub
		for(Chromosome cur:CurHeap){
			Memory.insert(cur);
		}
	}

	/**
	 * @Description update the value of bestmChromosome&&bestTransChromosome&&bestInterCellSequence
	 * @throws CloneNotSupportedException
	 */
	private void updateBestChromosome() throws CloneNotSupportedException {
		bestChromosome = bestSoFar(Population, bestChromosome);
	}
		
	/**
	 * @Description evalution process for GA
	 * @param trans_chromosome chromosome for trans part 
	 * @param m_chromosome chromosome for machine part
	 */
	protected double evaluation(Chromosome chromosome) {
	     	int mSetSize = mSet.size();
		    int vSetSize = cellSet.size();	
//		    System.out.println("Msize"+ mSetSize);
		    for(int i=0;i<mSetSize;i++){
//		    	System.out.println(chromosome.MachineSegment[i+1].toString());
		        int[] temp = new int[chromosome.MachineSegment[i+1].length-1];
		        for(int j=0;j<chromosome.MachineSegment[i+1].length-1;j++){
		        	temp[j]=chromosome.MachineSegment[i+1][j+1];
		        }
		    	mSet.get(i).setPriorSequence(temp);
		    }
		    for(int i=0;i<vSetSize;i++){
		    	int[] temp = new int[chromosome.VehicleSegment[i+1].length-1];
//		    	String[] Temp = new String[chromosome.IntercellPartSequences[i+1].length-1];
		    	for(int j=0;j<chromosome.VehicleSegment[i+1].length-1;j++){
		    		temp[j]=chromosome.VehicleSegment[i+1][j+1];
		    	}
		    	cellSet.get(i).setPriorSequence(temp);
//		    	for(int j=0;j<chromosome.IntercellPartSequences[i+1].length-1;j++){
//		    		Temp[j]=chromosome.IntercellPartSequences[i+1][j+1];
//		    	}
//		    	cellSet.get(i).setIntercellPartSequences(Temp);
		    	cellSet.get(i).setIntercellPartSequences(chromosome.IntercellPartSequences[i+1]);
		    }
//    		long start = System.currentTimeMillis();
//    		System.out.println("初始Timer:"+Timer.currentTime());
			evaluator.schedule();
//			long end   = System.currentTimeMillis();
//			System.out.println("本次时间:"+(end-start)+"ms");
	
			
			return measurance.getMeasurance(evaluator);
	}

	
	/**
	 * @Description Find the best chromosome in population
	 * @param population
	 * @param bestChromosome
	 * @return bestChromosome
	 * @throws CloneNotSupportedException
	 */
	private Chromosome bestSoFar(List<Chromosome> population, Chromosome bestChromosome)
			throws CloneNotSupportedException {
		Chromosome temp ;
		
		Chromosome currentBest = Population.get(0);
		double currentBestFunc = Population.get(0).getFunction();
		
//		Chromosome currentBest = Collections.min(population).clone();
		for(int i=1;i<POPULATION_SIZE;i++) {
			temp=Population.get(i);
			if(temp.getFunction() <= currentBestFunc){
				currentBest 	= temp;
				currentBestFunc = temp.getFunction();
			}
			/***添加到 每一代的Heap池中*/
			InsertInHeap(temp);
		}
		
		if (bestChromosome == null) {
			bestChromosome = currentBest;
			bestFunction  = evaluation(bestChromosome);
		} else if (currentBest.getFunction() <= bestChromosome.getFunction()) {
				bestChromosome = currentBest;
				bestFunction  = evaluation(bestChromosome);
		}
//		System.out.println("该种群中最优秀的调度解：");
//		System.out.println("最优解的函数值:"+bestFunction);
		return bestChromosome;
	}
	
	
	/**
	 * @Description insert the candidate into the Heap
	 * @param temp
	 * @throws CloneNotSupportedException 
	 */
	private void InsertInHeap(Chromosome temp) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		for(Chromosome cur: CurHeap){
			if(cur.equals(temp)){
				return;
			}
		}	
		CurHeap.insert(temp.clone());	
	}

	/**
	 * @Description Find the worst chromosome
	 * @param population
	 * @param worstChromosome
	 * @return worstChromosome
	 * @throws CloneNotSupportedException
	 */
	private Chromosome worstSoFar(List<Chromosome> population, Chromosome worstChromosome)
			throws CloneNotSupportedException {
//		Chromosome currentWorst = Collections.max(population).clone();
		Chromosome chromosome1 ;
		Chromosome chromosome2 ;
		Chromosome currentWorst = new Chromosome(mSet.size(), cellSet.size()) ;
		for(int i=0;i<POPULATION_SIZE-1;i++) {
			chromosome1=Population.get(i);
			chromosome2=Population.get(i+1);
			if(i==0){
				if(chromosome1.getFunction() >= chromosome2.getFunction()){
					currentWorst=chromosome1;
				}
				else {
					currentWorst=chromosome2;
//				Population.set(i+1,currentBest);
				}
			}
			else if(currentWorst.getFunction() <= chromosome2.getFunction()){
				currentWorst=chromosome2;
//				Population.set(i,currentBest);
				}
		}
		if (worstChromosome == null) {
			worstChromosome = currentWorst;
			worstFunction  = worstChromosome.getFunction();
		} else if (currentWorst.getFunction() >= worstChromosome.getFunction()) {
			    worstChromosome = currentWorst;
				worstFunction  = worstChromosome.getFunction();
		}
		
//		System.out.println("该种群中最差的调度解：");
////		System.out.println("MachineSegment:"+Arrays.toString(worstChromosome.MachineSegment));
////		System.out.println("VehicleSegment:"+Arrays.toString(worstChromosome.VehicleSegment));
////		System.out.println("InterCellSequence:"+Arrays.toString(worstChromosome.IntercellPartSequences));
//		System.out.println("最差解的函数值:"+worstFunction);
//		worstFunction=0;
		
		return worstChromosome;
	}
	
	
	/**初始化：
		* 初始解的GP产生接口，初始解通过rules产生，初始解通过random产生
	    * 应该是单独建立3个部分，可以切换，从而形成3组初始解产生机制
		* ①GP产生；②rules产生；③random
		* */
	private void init_population(int caseIndex) throws CloneNotSupportedException {                                   
        int i;		
        int mSetSize = mSet.size();
		int vSetSize = cellSet.size();	              
		Population = new ArrayList<Chromosome>();
		Chromosome chromosome = new Chromosome(mSetSize,vSetSize);
		
			
		for(i=0;i<POPULATION_SIZE;i++) {
			if(i<24){
				RulePrioirsReader(mSetSize,vSetSize,"solutions/Case1/" +caseIndex+"/"+(i + 1));
//				RulePrioirsReader(mSetSize,vSetSize,"solutions/Case1/1/" + (i + 1));  //
//				RulePrioirsReader(mSetSize,vSetSize,"solutions/Case1/19/" + (i + 1));
				
				for (int index = 1; index <mSetSize+1; index++) {                      
					chromosome.setMachineSegment(index, Constants.MachineToParts[index]);
				}
			                                                                      
				for (int SourceIndex = 1; SourceIndex <vSetSize+1; SourceIndex++) {
					int[] VehicleCellSquence = Constants.CellToNextCells[SourceIndex];
//					if(VehicleCellSquence.length !=0){
					chromosome.setVehicleSegment(SourceIndex,VehicleCellSquence);
//					}
					for (int TargetIndex = 0; TargetIndex <VehicleCellSquence.length; TargetIndex++){
						int TargetCell = VehicleCellSquence[TargetIndex];

																		//Arraylist<Integer>转换为数组
//			      	  StringBuffer strBuffer = new StringBuffer();
						if(Constants.CellToParts[SourceIndex][TargetCell]!=null){
							if(Constants.CellToParts[SourceIndex][TargetCell].size()!=0){
								int[] temp = new int [Constants.CellToParts[SourceIndex][TargetCell].size()]; 
								int k=0;
								for(int o :Constants.CellToParts[SourceIndex][TargetCell] ){
//						    	    	strBuffer.append(o);
//						   		     	temp = new int[]{Integer.parseInt(strBuffer.toString())};
										temp[k]=o;
										k++;
//						 		       	strBuffer.delete(0, strBuffer.length());
								}
								chromosome.setPartSequence(SourceIndex,TargetCell,temp);  
							}		
						}			       			    			         
					}
				}
			}
			else{
				
				for (int index = 0; index <mSetSize+1; index++) {                      
	                 chromosome.setMachineSegment(index, RandomPriors(Constants.MachineToParts[index]));
				}
				                                                                      
				for (int SourceIndex = 0; SourceIndex <vSetSize+1; SourceIndex++) {
					int[] VehicleCellSquence = RandomPriors(Constants.CellToNextCells[SourceIndex]);
//					if(VehicleCellSquence.length !=0){
					chromosome.setVehicleSegment(SourceIndex,VehicleCellSquence);
//					}
					for (int TargetIndex = 0; TargetIndex <VehicleCellSquence.length; TargetIndex++){
						int TargetCell = VehicleCellSquence[TargetIndex];

																		//Arraylist<Integer>转换为数组
//				        StringBuffer strBuffer = new StringBuffer();
				        if(Constants.CellToParts[SourceIndex][TargetCell]!=null){
				        	if(Constants.CellToParts[SourceIndex][TargetCell].size()!=0){
				        		 int[] temp = new int [Constants.CellToParts[SourceIndex][TargetCell].size()]; 
				        		 int k=0;
				        		 for(int o :Constants.CellToParts[SourceIndex][TargetCell] ){
//							        	strBuffer.append(o);
//							        	temp = new int[]{Integer.parseInt(strBuffer.toString())};
	                                    temp[k]=o;
	                                    k++;
//							        	strBuffer.delete(0, strBuffer.length());
							     }
				        		 chromosome.setPartSequence(SourceIndex,TargetCell,RandomPriors(temp));  
				        	}		
				        }			       			    			         
					}
				}
				
			}
				
			double func_value = evaluation(chromosome);
//		double func_value = 100.00;
		chromosome.setFunction(func_value);
		
		AddToPopulation(Population,chromosome.clone());		
//		System.out.println("该种群中调度解：");

//		System.out.println("第"+(i+1)+"个调度解的函数值:"+func_value);
			


			}	
		}
	
	
	private void RulePrioirsReader(int msize, int csize, String filename) {
		// TODO Auto-generated method stub
		File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader 		   			  = new BufferedReader(new FileReader(file));

			// 初始化单元信息&&机器对象
			

			
			
//			Constants.MachineToParts  = new int[msize+1][];
//			Constants.CellToNextCells = new int[csize+1][];
//			Constants.CellToParts	  = new ArrayList[csize+1][csize+1];
//			for(int i = 1; i < csize+1; i++){
//				for(int j = 1; j < csize+1; j++){
//					Constants.CellToParts[i][j] = new ArrayList<Integer>();
//				}
//			}
//			
			
			String line;
			String[] seq = null;
			
			/**读取机器信息**/
			for (int i = 1; i < msize+1; i++) {
				Constants.MachineToParts[i] = new int[Constants.MachineToParts[i].length];
				line = reader.readLine();
				int m = line.indexOf(":");
				String t = line.substring(m+1);
				seq  = t.split(",");
				for(int j = 0; j < Constants.MachineToParts[i].length;j++ ){
					if(j==0){
						Constants.MachineToParts[i][j] =0;
					}
					else{
						Constants.MachineToParts[i][j] = Integer.parseInt(seq[j]);
					}
				}
				
				
				
			}
			reader.readLine();// 空格行
			
			
			/**读取InterCellSequence信息**/
			for (int i = 1; i < csize+1; i++) {

				for(int j = 1; j < csize+1;j++ ){
					line = reader.readLine();
					if(i!=j){
						int m = line.indexOf(":");
						String t = line.substring(m+1);
						seq  = t.split(",");
						for(int k =0;k < Constants.CellToParts[i][j].size();k++){
							Constants.CellToParts[i][j].set(k, Integer.parseInt(seq[k]));
						}
					}
				}
			}
			reader.readLine();// 空格行
			
			/**读取单元to单元信息**/
			for (int i = 1; i < csize+1; i++) {
				Constants.CellToNextCells[i] = new int[Constants.CellToNextCells[i].length];
				line = reader.readLine();
				int m = line.indexOf(":");
				String t = line.substring(m+1);
				seq  = t.split(",");
				for(int j = 0 ; j < Constants.CellToNextCells[i].length; j++){
					if(j==0){
						Constants.CellToNextCells[i][j] =0;
					}
					else{
						Constants.CellToNextCells[i][j] = Integer.parseInt(seq[j-1]);
					}
				}
			}
			

		
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	void EmployedBees(int[] m,double[] r1,double[] r2) throws CloneNotSupportedException
	{
	    int i;

	    Chromosome chromosome =new Chromosome(mSet.size(), cellSet.size());
	    Chromosome neighbor1 = new Chromosome(mSet.size(), cellSet.size()) ;
	    Chromosome neighbor2 = new Chromosome(mSet.size(), cellSet.size()) ;
 
	    for(i=0;i<POPULATION_SIZE;i++) {
	       
	    	if((r1[i]/r2[i])>threshold){	
	    		Random rand = new Random();		
	    		int k = rand.nextInt(2);         //组别3的搜索
	    		if(k==0){
                    for(int p=0;p<2;p++){                                     //2次swap元操作
	    				chromosome=Population.get(i);
	    				chromosome.setFunction(evaluation(chromosome));

                    	neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
                    	neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
                    	neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
                    	neighbor1.setFunction(evaluation(neighbor1));

                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
                    		chromosome=neighbor1;
                    	}
                    	Population.set(i,chromosome.clone());
                    }
                    
                    chromosome=Population.get(i);							//1次insert元操作
        	    	chromosome.setFunction(evaluation(chromosome));

        	    	neighbor2.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor2.MachineSegment);
        			neighbor2.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor2.VehicleSegment);
        			neighbor2.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor2.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
        			neighbor2.setFunction(evaluation(neighbor2));

        		    
        		    if(neighbor2.getFunction()<=chromosome.getFunction()){
        			    chromosome=neighbor2;
        		    }
        		    Population.set(i,chromosome.clone());
                    
                    
   	    			m[i]=0;                                           //有改进，所以计数器归0
	    		}else{
	    			for(int p=0;p<2;p++){                                     //2次insert元操作
	    				chromosome=Population.get(i);
	    				chromosome.setFunction(evaluation(chromosome));

	    				neighbor1.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
	        			neighbor1.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
	        			neighbor1.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	        			neighbor1.setFunction(evaluation(neighbor1));

                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
                    		chromosome=neighbor1;
                    	}
                    	Population.set(i,chromosome.clone());
                    }
                    
                    chromosome=Population.get(i);							//1次swap元操作
        	    	chromosome.setFunction(evaluation(chromosome));

        	    	neighbor2.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor2.MachineSegment);
                	neighbor2.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor2.VehicleSegment);
                	neighbor2.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor2.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
                	neighbor2.setFunction(evaluation(neighbor2));

        		    
        		    if(neighbor2.getFunction()<=chromosome.getFunction()){
        			    chromosome=neighbor2;
        		    }
        		    Population.set(i,chromosome.clone());
	    			
	    			m[i]=0;                                                  
	    		}
	    	}
	    	
	    	if((r1[i]/r2[i])>1&&(r1[i]/r2[i])<threshold){
	    		Random rand = new Random();           	                     //组别2的搜索           
	    		int k = rand.nextInt(2);
	    		if(k==0){
	    			 for(int p=0;p<2;p++){                                     //2次swap元操作
		    				chromosome=Population.get(i);
		    				chromosome.setFunction(evaluation(chromosome));

	                    	neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
	                    	neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
	                    	neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	                    	neighbor1.setFunction(evaluation(neighbor1));

	                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
	                    		chromosome=neighbor1;
	                    	}
	                    	Population.set(i,chromosome.clone());
	                    }
	    			
	    			 m[i]=0;
	    		}else{
	    			for(int p=0;p<2;p++){                                     //2次insert元操作
	    				chromosome=Population.get(i);
	    				chromosome.setFunction(evaluation(chromosome));

	    				neighbor1.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
	        			neighbor1.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
	        			neighbor1.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	        			neighbor1.setFunction(evaluation(neighbor1));

                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
                    		chromosome=neighbor1;
                    	}
                    	Population.set(i,chromosome.clone());
                    }
	    			
	    			m[i]=0;
	    		}
	    	}
	    	
	    	if(r1[i]/r2[i]==1){
	    		if(m[i]>10&&m[i]<=5){              //组别2的搜索
	    			Random rand = new Random();
	    			int k = rand.nextInt(2);
	    			if(k==0){
	    				for(int p=0;p<2;p++){                                     //2次swap元操作
		    				chromosome=Population.get(i);
		    				chromosome.setFunction(evaluation(chromosome));

	                    	neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
	                    	neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
	                    	neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	                    	neighbor1.setFunction(evaluation(neighbor1));

	                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
	                    		chromosome=neighbor1;
	                    	}
	                    	Population.set(i,chromosome.clone());
	                    }
	    				m[i]++;
	    					
	    			}else{
	    				for(int p=0;p<2;p++){                                     //2次insert元操作
		    				chromosome=Population.get(i);
		    				chromosome.setFunction(evaluation(chromosome));

		    				neighbor1.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
		        			neighbor1.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
		        			neighbor1.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
		        			neighbor1.setFunction(evaluation(neighbor1));

	                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
	                    		chromosome=neighbor1;
	                    	}
	                    	Population.set(i,chromosome.clone());
	                    }
	    				m[i]++;
	    					
	    			}
	    		}
	    		if(m[i]>50&&m[i]<=100){        //组别3的搜索
	    			Random rand = new Random();
	    			int k = rand.nextInt(2);
	    			if(k==0){
	    				for(int p=0;p<2;p++){                                     //2次swap元操作
		    				chromosome=Population.get(i);
		    				chromosome.setFunction(evaluation(chromosome));

	                    	neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
	                    	neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
	                    	neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	                    	neighbor1.setFunction(evaluation(neighbor1));

	                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
	                    		chromosome=neighbor1;
	                    	}
	                    	Population.set(i,chromosome.clone());
	                    }
	                    
	                    chromosome=Population.get(i);							//1次insert元操作
	        	    	chromosome.setFunction(evaluation(chromosome));

	        	    	neighbor2.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor2.MachineSegment);
	        			neighbor2.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor2.VehicleSegment);
	        			neighbor2.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor2.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	        			neighbor2.setFunction(evaluation(neighbor2));

	        		    
	        		    if(neighbor2.getFunction()<=chromosome.getFunction()){
	        			    chromosome=neighbor2;
	        		    }
	        		    Population.set(i,chromosome.clone());
	    				m[i]++;
	    					
	    			}else{
	    				for(int p=0;p<2;p++){                                     //2次insert元操作
		    				chromosome=Population.get(i);
		    				chromosome.setFunction(evaluation(chromosome));

		    				neighbor1.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
		        			neighbor1.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
		        			neighbor1.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
		        			neighbor1.setFunction(evaluation(neighbor1));

	                    	if(neighbor1.getFunction()<=chromosome.getFunction()){
	                    		chromosome=neighbor1;
	                    	}
	                    	Population.set(i,chromosome.clone());
	                    }
	                    
	                    chromosome=Population.get(i);							//1次swap元操作
	        	    	chromosome.setFunction(evaluation(chromosome));

	        	    	neighbor2.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor2.MachineSegment);
	                	neighbor2.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor2.VehicleSegment);
	                	neighbor2.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor2.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	                	neighbor2.setFunction(evaluation(neighbor2));

	        		    
	        		    if(neighbor2.getFunction()<=chromosome.getFunction()){
	        			    chromosome=neighbor2;
	        		    }
	        		    Population.set(i,chromosome.clone());
	    				m[i]++;
	    			}
	    		}
	    		if(m[i]>100){			   //组别1的搜索
	    			Random rand = new Random();
	    			int k = rand.nextInt(2);
	    			if(k==0){
	    				 chromosome=Population.get(i);							//1次swap元操作
		        	     chromosome.setFunction(evaluation(chromosome));

		        	     neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
		                 neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
		                 neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
		                 neighbor1.setFunction(evaluation(neighbor1));

		        		    
		        		 if(neighbor1.getFunction()<=chromosome.getFunction()){
		        			  chromosome=neighbor1;
		        		 }
		        		 Population.set(i,chromosome.clone());
	    				 m[i]++;
	    					
	    			}else{
	    				chromosome=Population.get(i);							//1次insert元操作
	        	    	chromosome.setFunction(evaluation(chromosome));

	        	    	neighbor2.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor2.MachineSegment);
	        			neighbor2.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor2.VehicleSegment);
	        			neighbor2.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor2.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	        			neighbor2.setFunction(evaluation(neighbor2));

	        		    
	        		    if(neighbor2.getFunction()<=chromosome.getFunction()){
	        			    chromosome=neighbor2;
	        		    }
	        		    Population.set(i,chromosome.clone());
	    				m[i]++;
	    			}
	    	  	}

	    		
	    		Random rand = new Random();			//组别1的搜索
	    		int k = rand.nextInt(2);
	    	 	if(k==0){
	    	 		chromosome=Population.get(i);							//1次swap元操作
	        	    chromosome.setFunction(evaluation(chromosome));

	        	    neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
	                neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
	                neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
	                neighbor1.setFunction(evaluation(neighbor1));

	        		    
	        		if(neighbor1.getFunction()<=chromosome.getFunction()){
	        			chromosome=neighbor1;
	        		}
	        		Population.set(i,chromosome.clone());
	    			m[i]++;
	    			
	    		}
	    		else{
	    			chromosome=Population.get(i);							//1次insert元操作
        	    	chromosome.setFunction(evaluation(chromosome));

        	    	neighbor2.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor2.MachineSegment);
        			neighbor2.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor2.VehicleSegment);
        			neighbor2.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor2.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
        			neighbor2.setFunction(evaluation(neighbor2));

        		    
        		    if(neighbor2.getFunction()<=chromosome.getFunction()){
        			    chromosome=neighbor2;
        		    }
        		    Population.set(i,chromosome.clone());
    				
	    			m[i]++;
	    		}


	    	}
	    	
	    	
	    }
	      
		    
		    
	        /*end of employed bee phase*/
    }

	void OnlookerBees() throws CloneNotSupportedException
	{
	  /**onlooker Bee Phase
      
	   * 对从employed bee phase中得出的解采用localsearch2()，得到neighbour2
	   * 比较对应neighbour和neighbour2，取优秀的赋给neighbour2
	   */
      
		
		
		if(Memory.size()!=0){
			for(int i = 0;i<Population.size();i++){
				Population.set(i, Mutation2(Population.get(i),i));
			}
		}
		
	   /*end of onlooker bee phase   */
	}

	
	/**
	 * @Description Scoutbees' generation
	 * @throws CloneNotSupportedException
	 */
	void ScoutBees() throws CloneNotSupportedException
	{
		/**
		 *scout bee phase
		 *
		 *选出onlooker中赋完值的neighbour2中最差的设为scout
		 *对scout舍弃，利用random重新生成一个解加入neighbour2中
		 *将neighbour2[][]赋值给foodnumber[][]
		 */

		//在所有neighbor2的population中找出最差的解，记为worstneighbor2
		worstChromosome=worstSoFar(Population,worstChromosome);         
		int m=0;
		for(int i=0;i<POPULATION_SIZE;i++){
			if(worstChromosome.getFunction()==Population.get(i).getFunction()){
				m=i;
				break;
			}
		}
		
		/*****Mutationg generate*****/
		Population.set(m, Mutation(worstChromosome,m));
		/*****Mutationg End*****/
		
		/*****Randomly generate**
		int mSetSize = mSet.size();
		int vSetSize = cellSet.size();

		for(int i=0;i<POPULATION_SIZE;i++) {
			for (int index = 0; index <mSetSize+1; index++) {                      
				worstChromosome.setMachineSegment(index, RandomPriors(Constants.MachineToParts[index]).clone());
			}
			                                                                      
			for (int SourceIndex = 0; SourceIndex <vSetSize+1; SourceIndex++) {
				int[] VehicleCellSquence = RandomPriors(Constants.CellToNextCells[SourceIndex]);
//				if(VehicleCellSquence.length !=0){
				worstChromosome.setVehicleSegment(SourceIndex,VehicleCellSquence);
//				}
				
				for (int TargetIndex = 0; TargetIndex <VehicleCellSquence.length; TargetIndex++){
					int TargetCell = VehicleCellSquence[TargetIndex];
					if(Constants.CellToParts[SourceIndex][TargetCell]!=null){//在这里对从source到target的所对应序列是否为空进行判断
						if(Constants.CellToParts[SourceIndex][TargetCell].size()!=0){
							int[] temp =new int[Constants.CellToParts[SourceIndex][TargetCell].size()];                                                  //Arraylist<Integer>转换为数组
//					        StringBuffer strBuffer = new StringBuffer();
					        int k=0;
			        		for(int o :Constants.CellToParts[SourceIndex][TargetCell] ){
//						        	strBuffer.append(o);
//						        	temp = new int[]{Integer.parseInt(strBuffer.toString())};
                                  temp[k]=o;
                                  k++;
//						        	strBuffer.delete(0, strBuffer.length());
						    }
							worstChromosome.setPartSequence(SourceIndex,TargetCell,RandomPriors(temp));
						}
						else{
							Constants.CellToParts[SourceIndex][TargetCell]=null;
						}
					}
					     //System.out.println(SourceIndex+TargetCell);
				}
			}
		}
		worstChromosome.setFunction(evaluation(worstChromosome));
		for(int i=1;i<worstChromosome.IntercellPartSequences.length;i++){
			worstChromosome.IntercellPartSequences[i][0]=null;
		}
		Population.set(m, worstChromosome);
		**Randomly End******/
		
//		System.out.println("替换worstChromosome而新加入的调度解:");
//		System.out.println(worstChromosome.getFunction());

	}

	/**
	 * @Description Mutation Process For The Whole Population
	 * @param origin
	 * @return
	 */
    private Chromosome Mutation(Chromosome origin, int index) {
    	int msize = origin.getMachineSize();
    	int vsize = origin.getVehicleSize();
    	
    	Chromosome New 	  = new Chromosome(msize,vsize);
    	Chromosome X_best = GetFromPool(); 
    	
    	int X1_index;
    	while(true){
    		X1_index = (int)(Math.random () *POPULATION_SIZE);
    		if(X1_index!=index) break;
    	}
		Chromosome X_1    = Population.get(X1_index);
    	
		Chromosome X_2    = GetFromPool();		//这里应该还要加上B集合
		// 针对机器段
		int[] tmp = new int[0];
		New.setMachineSegment(0,tmp);
    	for(int i = 1; i <= msize; i++){
    		New.setMachineSegment(
    				i, 
    				MutateOperate(
    						origin.getMachineSegment()[i],
    						X_best.getMachineSegment()[i],
    						X_1.getMachineSegment()[i],
    						X_2.getMachineSegment()[i]
    				)
    		);		
    	}
    	//针对小车段
    	New.setVehicleSegment(0, tmp);
    	for(int i = 1; i <= vsize; i++){
    		New.setVehicleSegment(
    				i, 
    				MutateOperate(
    						origin.getVehicleSegment()[i],
    						X_best.getVehicleSegment()[i],
    						X_1.getVehicleSegment()[i],
    						X_2.getVehicleSegment()[i]
    				)
    		);		
    	}
    	//针对单元间的工件
    	for(int i = 1; i <= vsize; i++){
    		for(int j = 1; j <= vsize; j++){
    			if(i!=j){
    				New.setPartSequence(i, j, 
    								MutateOperate(
    								ConvertToIntArray(origin.getPartSequence()[i][j]),
    								ConvertToIntArray(X_best.getPartSequence()[i][j]),
    								ConvertToIntArray(X_1.getPartSequence()[i][j]),
    								ConvertToIntArray(X_2.getPartSequence()[i][j])
    								)
    				);
    			}
    		}
    	}    	
    	
    	//对其进行变化，改进 并return
		return New;
	}
    
  /*Mutation2用于onlooker里，加入了新解旧解函数值的比较*/  
    private Chromosome Mutation2(Chromosome origin, int index) throws CloneNotSupportedException{
    	int msize = origin.getMachineSize();
    	int vsize = origin.getVehicleSize();
    	
    	Chromosome New 	  = new Chromosome(msize,vsize);
    	Chromosome X_best = GetFromPool(); 
    	
    	int X1_index;
    	while(true){
    		X1_index = (int)(Math.random () *POPULATION_SIZE);
    		if(X1_index!=index) break;
    	}
		Chromosome X_1    = Population.get(X1_index);
    	
		Chromosome X_2    = GetFromPool();		//这里应该还要加上B集合
		// 针对机器段
		int[] tmp = new int[0];
		New.setMachineSegment(0,tmp);
    	for(int i = 1; i <= msize; i++){
    		New.setMachineSegment(
    				i, 
    				MutateOperate(
    						origin.getMachineSegment()[i],
    						X_best.getMachineSegment()[i],
    						X_1.getMachineSegment()[i],
    						X_2.getMachineSegment()[i]
    				)
    		);		
    	}
    	//针对小车段
    	New.setVehicleSegment(0, tmp);
    	for(int i = 1; i <= vsize; i++){
    		New.setVehicleSegment(
    				i, 
    				MutateOperate(
    						origin.getVehicleSegment()[i],
    						X_best.getVehicleSegment()[i],
    						X_1.getVehicleSegment()[i],
    						X_2.getVehicleSegment()[i]
    				)
    		);		
    	}
    	//针对单元间的工件
    	for(int i = 1; i <= vsize; i++){
    		for(int j = 1; j <= vsize; j++){
    			if(i!=j){
    				New.setPartSequence(i, j, 
    								MutateOperate(
    								ConvertToIntArray(origin.getPartSequence()[i][j]),
    								ConvertToIntArray(X_best.getPartSequence()[i][j]),
    								ConvertToIntArray(X_1.getPartSequence()[i][j]),
    								ConvertToIntArray(X_2.getPartSequence()[i][j])
    								)
    				);
    			}
    		}
    	}  
    	New.setFunction(evaluation(New));
    	 if(New.getFunction()<=origin.getFunction()){
    		 origin=New;
		    }
//		    Population.set(index,origin.clone());
    	//对其进行变化，改进 并return
         return origin;
	}

    /**
     * @Description get an random one from the Pool
     * @return
     */
    private Chromosome GetFromPool() {
    	int index = (int) (Math.random()*Memory.size());
    	return Memory.getIndex(index);
	}
    
	/**
     * @Description convert ArrayList<Integer> to int[]
     * @param origin
     * @return
     */
    private int[] ConvertToIntArray(ArrayList<Integer> origin) {
		int []result = new int[origin.size()+1];
		result[0] = -1;
		for(int i = 1; i < result.length; i++){
			result[i] = origin.get(i-1);
		}
    	return result;
	}

	/**
     * @param MemoryBest 
	 * @param X_2 
	 * @param js 
	 * @Description 具体的变异操作
     * 通过将优先级转化成可以比较的数值 -- 初定每个数值之间差值为1
     * @param x1
     * @param x2
     * @return
     */
    private int[] MutateOperate(int [] X, int[] XBest, int[] X_1, int[] X_2){
    	Map<Integer, Double> Result        = new HashMap<Integer, Double>();

    	//优先级数组转化成可以比较数值大小的数组
    	Map<Integer, Integer> Priors_X     = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> Priors_Xbest = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> Priors_X1    = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> Priors_X2    = new HashMap<Integer, Integer>();
    	
    	int count = 1;
    	for(int i = X.length - 1; i >=1; i--){	//第0位是0，无用数据，不用存储
    		Priors_X.put(X[i], count);
    		Priors_Xbest.put(XBest[i], count);
    		Priors_X1.put(X_1[i], count);
    		Priors_X2.put(X_2[i], count);
    		count++;
    	}
    	
    	/** The operators begin**/
    	if(XBest.length != 0){	//have the memory infomations
	    	for(int i = 1; i < X.length; i++){
	    		Result.put(X[i],
	    			Priors_X.get(X[i])+
	    			MutateFactor1* ( Priors_Xbest.get(X[i]) - 	Priors_X.get(X[i])) +
	    			MutateFactor2* ( Priors_X1.get(X[i]) 	- 	Priors_X2.get(X[i]))
	    		);
	    	}
    	}
    	else {					// did not have the memory history
	    	for(int i = 1; i < X.length; i++){
	    		Result.put(X[i],
	    			Priors_X.get(X[i])+
	    			MutateFactor2* ( Priors_X1.get(X[i]) 	- 	Priors_X2.get(X[i]))
	    		);
	    	}
    	}
    	/** The operators end**/
    	
    	/**Make It Feasible
    	 * dispatch the job according the values
    	 * **/
    	//sort
    	Map<Integer, Integer> Sort = MapUtil.sortByValue(Result);
    	
    	if(X[0]==0){	//if head with 0
        	int [] New = new int[Result.size()+1];
	    	New[0] = 0;
	    	for(int i = 0 ;i < Result.size(); i++){
	    		New[i+1] = Sort.get(i);
	    	}
	    	return New;
	    }else{			//if head without 0,means it is Sequence
	    	int [] New = new int[Result.size()];
	    	for(int i = 0 ;i < Result.size(); i++){
	    		New[i] = Sort.get(i);
	    	}
	    	return New;
	    }

    }
		/**
         * @throws CloneNotSupportedException 
         * @Description localsearch1 for ： 采用
         */
    private void LocalSearch1() throws CloneNotSupportedException {

	    int i;
//	    int a=6;
//	    int b=3;
	    Chromosome chromosome =new Chromosome(mSet.size(), cellSet.size());
	    Chromosome neighbor1 = new Chromosome(mSet.size(), cellSet.size()) ;
//	    Chromosome neighbor1 = new Chromosome(a, b) ;        //a是mSet.size(),  b是cellSet.size();
 
	    for(i=0;i<POPULATION_SIZE;i++) {
	       
	    	chromosome=Population.get(i);
	    	chromosome.setFunction(evaluation(chromosome));
//	    	func_value = 40;
	    	neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
			neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
			neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
		
			neighbor1.setFunction(evaluation(neighbor1));
//	    	neighbor_func = 50;
		    if(neighbor1.getFunction()<=chromosome.getFunction()){
			    chromosome=neighbor1;
		    }
		    Population.set(i,chromosome.clone());
//		    System.out.println("初始种群中第"+(i+1)+"个调度解经过ls1后的函数值："+chromosome.getFunction());
	    }
    }

    /**
     * @throws CloneNotSupportedException 
     * @Description localsearch1 for ： 采用
     */
	private void LocalSearch2() throws CloneNotSupportedException {
	
	    int i;

	    Chromosome chromosome ;
	    Chromosome neighbor2 = new Chromosome(mSet.size(), cellSet.size()) ;
	
	    for(i=0;i<POPULATION_SIZE;i++) {
		       
	    	chromosome=Population.get(i);
	    	chromosome.setFunction(evaluation(chromosome));
//	    	func_value=30;
	    	neighbor2.MachineSegment = insert(chromosome.clone().getMachineSegment(),neighbor2.MachineSegment);
			neighbor2.VehicleSegment = insert(chromosome.clone().getVehicleSegment(),neighbor2.VehicleSegment);
			neighbor2.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor2.IntercellPartSequences);                   //swap的error是由于intersequence的类型引起的
			neighbor2.setFunction(evaluation(neighbor2));
//			neighbor_func=50;
		    
		    if(neighbor2.getFunction()<=chromosome.getFunction()){
			    chromosome=neighbor2;
		    }
		    Population.set(i,chromosome.clone());
//		    System.out.println("初始种群中第"+(i+1)+"个调度解经过ls2后的函数值："+chromosome.getFunction());
	    }
    }


	private ArrayList<Integer>[][] swap (ArrayList<Integer>[][] Sequences,ArrayList<Integer>[][] Sequences2){

		Sequences2=new ArrayList[Sequences.length][];
		for(int i =0;i<Sequences.length;i++){
			Sequences2[i]=new ArrayList[Sequences[i].length];
    		for(int j=0;j<Sequences[i].length;j++){
    			
    			if(Sequences[i][j]!=null){
        			int[] temp2 =new int[Sequences[i][j].size()];
    				Sequences2[i][j]=new ArrayList(Sequences[i][j].size());
    				if(Sequences[i][j].size()!=0){
    					if(Sequences[i][j].size()!=2){
    			
    						int[] temp =new int[Sequences[i][j].size()];                                                  //Arraylist<Integer>转换为数组

					        int k=0;
			        		for(int o :Sequences[i][j]){

                                  temp[k]=o;
                                  k++;
//						        	
						    }
    						temp2 =temp;                                                        //对数组进行swap操作
    						if(temp2.length!=0){
//    	    			if(segment[index]!=null){
    							if(temp2.length!=2){
    								int[] randoms = getRandomIndex (temp2.length);
    	    					
    								int a = temp2[randoms[0]];
    								temp2[randoms[0]] = temp2[randoms[1]];
    								temp2[randoms[1]] = a;
    	    					
    							}
    						}
    						for(int p = 0;p<temp2.length;p++){                                 //将数组转换回Arraylist<Integer>[][]
    							int b =0;
    							b = temp2[p];
    							Sequences2[i][j].add(b);	
    						}
    					}
    				}
    			}
    		}
		}
		return Sequences2;
    	
	}
	
    private  int[][] swap ( int[][] segment ,int[][] segment2)
    {

    	for(int i =1;i<segment.length;i++){
    	    segment2[i] =new int[segment[i].length];
    		for(int j=0;j<segment[i].length;j++){
    			segment2[i][j] =segment[i][j];
    		}
    	}
    	for (int index = 1; index < segment2.length; index++) {
    		if(segment2[index].length!=0){
//    		if(segment[index]!=null){
    				if(segment2[index].length!=2){
    					int[] randoms = getRandomIndex (segment2[index].length);
    		
    					int temp = segment2[index][randoms[0]];
    					segment2[index][randoms[0]] = segment2[index][randoms[1]];
    					segment2[index][randoms[1]] = temp;
    		
    				}
    		}		
    	}
    		
    	return segment2;
    }
    
	
    private ArrayList<Integer>[][] insert ( ArrayList<Integer>[][] Sequences ,ArrayList<Integer>[][] Sequences2)
    {

		Sequences2=new ArrayList[Sequences.length][];
		for(int i =0;i<Sequences.length;i++){
			Sequences2[i]=new ArrayList[Sequences[i].length];
    		for(int j=0;j<Sequences[i].length;j++){
    			if(Sequences[i][j]!=null){
    				Sequences2[i][j]=new ArrayList(Sequences[i][j].size());
    				if(Sequences[i][j].size()!=0){
    					if(Sequences[i][j].size()!=2){
    						
      						int[] temp2 =new int[Sequences[i][j].size()];
      						int[] temp =new int[Sequences[i][j].size()];                                                  //Arraylist<Integer>转换为数组

					        int k=0;
			        		for(int o :Sequences[i][j]){

                                  temp[k]=o;
                                  k++;
//						        	
						    }
    						temp2 =temp;                         
    						if(temp2.length!=0){
//        			if(chromosome[index]!=null){
    							if(temp2.length!=2){
    								int[] randoms = getRandomIndex (temp2.length);         //获取chromosome的优先级序列的长度
    								if (randoms[0] < randoms[1]){
            	
            		
    									int a=temp2[randoms[0]];
    									for ( int p = randoms[0]; p < randoms[1]; p++ ){
                	
    										temp2[p] = temp2[p + 1];
    									}
    									temp2[randoms[1]] = a;
            		
            	
    								}	
    								else
    								{
            	
     									int b=temp2[randoms[1]];
    									for ( int p = randoms[1]; p< randoms[0];p++ )
    									{
    										temp2[p] = temp2[p +1];
    									}
    									temp2[randoms[0]] = b;
            	   
     								}
    							}
    						}
    						for(int m = 0;m<temp2.length;m++){                                 //将数组转换回Arraylist<Integer>[][]
    							int c =0;
    							c = temp2[m];
    							Sequences2[i][j].add(c);	
    						}
    					}
    				}
    			}
    		}
		}
        
        return Sequences2;
    }
	
  
	
	
	private int[][] insert ( int[][] chromosome ,int[][] chromosome2)
    {
		for(int i =1;i<chromosome.length;i++){
    		chromosome2[i] =new int[chromosome[i].length];
    		for(int j=0;j<chromosome[i].length;j++){
    			chromosome2[i][j] =chromosome[i][j];
    		}
    	}
    
    for ( int index = 1; index < chromosome2.length; index++ )
    {
    	   
    	if(chromosome2[index].length!=0){
//    	if(chromosome[index]!=null){
    		if(chromosome2[index].length!=2){
    			int[] randoms = getRandomIndex (chromosome2[index].length);         //获取chromosome的优先级序列的长度
    			if (randoms[0] < randoms[1]){
        	
        		
        			int temp=chromosome2[index][randoms[0]];
        			for ( int i = randoms[0]; i < randoms[1]; i++ ){
            	
       					chromosome2[index][i] = chromosome2[index][i + 1];
       				}
       				chromosome2[index][randoms[1]] = temp;
        		
        	
    			}	
    			else
    			{
        	
        	    
        	
        	    	int temp=chromosome2[index][randoms[1]];
        	    	for ( int i = randoms[1]; i < randoms[0]; i++ )
        	    	{
        	    		chromosome2[index][i] = chromosome2[index][i +1];
        	    	}
        	    	chromosome2[index][randoms[0]] = temp;
        	   
        	           
    			}
    		}
    	}
    }
        
        return chromosome2;
    }
    
    private  int[] getRandomIndex ( int k )
    {
        int[] randoms = new int[2];
        int a = (int) ( Math.random () * (k-1) )+1;
        int b = a;
        while (b == a)
        {
            b = (int) ( Math.random () *( k-1) )+1;
        }
        randoms[0] = a;
        randoms[1] = b;
        //System.out.println ("indexs :" + Arrays.toString (randoms));
        return randoms;
    }



	
	/**
	 * @Description randomly rearrange the order of the string source
	 * @param string
	 */
//	private String RandomPriors(String source) {
//		String results="";
////		String source2 =new String("");
////		for(int i=0; i<source.length(); i++){
////    		source2+=source.charAt(i);
////    	}
//		char[] cur =  source.toCharArray();
//
//		if(source.length()!=0){
//		for (int i = 1; i < cur.length; i++) {
//			//int pos=(int)(rand.nextDouble()*(source.length-i+1)+i)-1;  
//            int pos =  (int) (Math.random () * source.length());
//            char temp=cur[i];  
//            cur[i]=cur[pos];  
//            cur[pos]=temp;  
//		}
//		for(char t:cur){
//			results+=t;
//		}
//		}
//		return results;
//	}


	/**
	 * @Description randomly rearrange the order of the array source
	 * @param source the source array
	 * @return
	 */
	private int[] RandomPriors(int[] source) { //check source鏄�浼犻�锛岃繕鏄璞′紶閫�
		
		if(source!=null){
			if(source.length!=0){
				for (int i = 1; i <source.length; i++) {
					//int pos=(int)(rand.nextDouble()*(source.length-i+1)+i)-1;  
					int pos =  (int) (Math.random () * (source.length-1))+1;
					int temp=source[i];  
					source[i]=source[pos];  
					source[pos]=temp;
				}
			}
		}
		else{
			source= new int [0];
		}
		return source;
	}

	/**
	 * @Description Add the new chromosome to current population
	 * @param Population 
	 * @param chromosome
	 */
	private void AddToPopulation(List<Chromosome> Population, Chromosome chromosome) {
		// TODO Auto-generated method stub
		if (Population.size()==0){
			Population.add(chromosome);
			return ;
		}
		
//		for(Chromosome be: Population){
//			if(chromosome.equals(be)) return;
//			
//			else if(be.equals(Population.get(Population.size()-1))){
//				Population.add(chromosome);
//				return;
//			}
//		}
		for(Chromosome be: Population){
			if(be.equals(Population.get(Population.size()-1))){
				Population.add(chromosome);
				return;
			}
		}
		
		
	}

//	public static void main ( String[] args ) throws CloneNotSupportedException{
//		 DABC test = new DABC ();
//		 MachineSet mSet = new MachineSet();
//		 CellSet cellSet = new CellSet();
//		 
//		
//		 
//		 test.schedule();
//		 System.out.println("population："+test.Population);
//		   
//	}

}
