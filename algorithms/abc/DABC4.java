package com.lm.algorithms.abc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.lm.Metadomain.CellSet;
import com.lm.Metadomain.JobSet;
import com.lm.Metadomain.MachineSet;
import com.lm.algorithms.MetaHeuristicScheduler;
import com.lm.algorithms.measure.MetaIMeasurance;
import com.lm.util.Constants;
import com.lm.util.HeapMaxPriorityQueue;
import com.lm.util.MapUtil;

public class DABC4 {


/*******************************************灞炴�鍩�******************************************************/
	/**閽堝鍏蜂綋闂鐨勮皟搴﹁繃绋�*/
	protected MetaHeuristicScheduler evaluator;
	/**閫傚簲搴﹁瘎浼版柟娉�*/
	protected MetaIMeasurance measurance ;
	/**鐢ㄤ簬闅忔満鐨勭瀛�*/
	protected Random rand = new Random(System.currentTimeMillis());
	/**鏈�匠鐨勯�搴斿害鍑芥暟鐨剉alue**/
	protected double bestFunction = 0d;
	/**鏈�樊鐨勯�搴斿害鍑芥暟鐨剉alue**/
	protected double worstFunction = 0d;
	
	//绉嶇兢鐩稿叧鏁版嵁
	/**姣忎竴浠ｇ殑绉嶇兢**/
	protected List<Chromosome> Population;
	/**璁板繂姹�*/
	protected HeapMaxPriorityQueue<Chromosome> Memory;
	/**CurHeap**/
	protected HeapMaxPriorityQueue<Chromosome> CurHeap;
	/**Archive B**/
	protected HeapMaxPriorityQueue<Chromosome> ArchiveB; 
    /**鏈�匠鏌撹壊浣撳簭鍒�*/
	protected Chromosome bestChromosome;
	/**鏈�樊鏌撹壊浣撳簭鍒�*/
	protected Chromosome worstChromosome;
	//绠楁硶鍙傛暟
	protected int POPULATION_SIZE=48;                       //淇敼閲廝OPULATION_SIZE
	/**the maxmum of iteration. default=100**/
	protected final int MaxCycle=100;                              //淇敼閲廙axCycle
	/**factor for x(best) - x(i)**/
	protected final double MutateFactor1 = 0.5;
	/**factor for x(r1) - x(2^)**/
	protected final double MutateFactor2 = 0.5;
	
	protected final double LeadingFactor = 0.5;
	
	//the input data for inter-cell problems
	/**the machine's set **/
	protected MachineSet mSet;
	/**the job's set **/
	protected JobSet jSet;
	/**the cell's set **/
	protected CellSet cellSet;
    
	
		
/***************************鏋勯�鍑芥暟鍩�**********************************************************************/
	
	/** 
	 * @Description:construction of DABC:榛樿鍙傛暟
	 * @param mSet
	 * @param jSet
	 * @param cellSet
	 * @param scheduler
	 * @param measurance
	 */
	public DABC4(MachineSet mSet, JobSet jSet, CellSet cellSet,
			MetaHeuristicScheduler scheduler, MetaIMeasurance measurance) {
		this(mSet, jSet, cellSet, scheduler, measurance,500, 48);
	}

	/**
	 * @Description:construction of DABC:鑷畾涔夊弬鏁�
	 * @param mSet
	 * @param jSet
	 * @param cellSet
	 * @param scheduler
	 * @param measurance
     * @param populationSize
	 * DABC
	 * @exception:
	 */
	public DABC4(MachineSet mSet, JobSet jSet, CellSet cellSet,
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
		this.ArchiveB   = new HeapMaxPriorityQueue<Chromosome>(populationSize/5);
		this.Memory  = new HeapMaxPriorityQueue<Chromosome>(populationSize);
	}

/*****************************************************************************************************/

/****************************鏂规硶鍩�********************************************************************/
	/**鐩爣鍑芥暟&閫傚簲搴﹀嚱鏁帮細makespan鎴栬�totalweightedtardiness*/

	/**鑾峰彇bestfunctionvalue**/
	public  double getBestFunctionValue() {
	    return bestFunction; 
	}
	
	/**鑾峰彇worstfunctionvalue**/
    public double getWorstFunctionValue(){
		return worstFunction;
	}


	/** MAIN PROCESS
	 * @throws CloneNotSupportedException 
	 * @Description framework for DABC
	 * 
	 */
	public void schedule(int caseIndex) throws CloneNotSupportedException {
		
		int iter=0;
		init_population(caseIndex);
		updateBestChromosome();                       //淇濆瓨绉嶇兢涓渶濂界殑閭ｄ釜璋冨害瑙�
//		System.out.println("鏈寰幆寮�");
		for (iter=0;iter<MaxCycle;iter++){        //杩唬鏁�
//			System.out.println("绗�+iter+"浠ｏ細");
			
			//emplyed bee's searching
			if(iter == 0){ // first step: localsearch randomly
				LocalSearch1();
//				LocalSearch2();
			}else{
				EmployedBees();
			}
		    //onlooker bee's searching
			OnlookerBees();
		    updateBestChromosome();
		    updateMemory();	
		    //scout bee's searching
		    ScoutBees();
			
		    if(iter==499){
			    System.out.println("璇ョ缇や腑鏈�紭绉�殑璋冨害瑙ｏ細");
				System.out.println("鏈�紭瑙ｇ殑鍑芥暟鍊�"+bestFunction);
			}
			System.out.println(bestFunction);
		}
	}

	
	/**
	 * @Description 鏍规嵁杩欎竴浠ｇ殑杩唬缁撴灉CurHeap锛屾洿鏂板叏灞�殑memory
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
//    		System.out.println("鍒濆Timer:"+Timer.currentTime());
			evaluator.schedule();
//			long end   = System.currentTimeMillis();
//			System.out.println("鏈鏃堕棿:"+(end-start)+"ms");
	
			
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
			/***娣诲姞鍒�姣忎竴浠ｇ殑Heap姹犱腑*/
			InsertInHeap(temp);
		}
		
		if (bestChromosome == null) {
			bestChromosome = currentBest;
			bestFunction  = evaluation(bestChromosome);
		} else if (currentBest.getFunction() <= bestChromosome.getFunction()) {
				bestChromosome = currentBest;
				bestFunction  = evaluation(bestChromosome);
		}
//		System.out.println("璇ョ缇や腑鏈�紭绉�殑璋冨害瑙ｏ細");
//		System.out.println("鏈�紭瑙ｇ殑鍑芥暟鍊�"+bestFunction);
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
	 * @Description insert the candidate into the Collect
	 * @param temp
	 * @param curHeap2 
	 * @throws CloneNotSupportedException 
	 */
	private void InsertInCollect(Chromosome temp, HeapMaxPriorityQueue<Chromosome> set) throws CloneNotSupportedException {
		for(Chromosome cur: set){
			if(cur.equals(temp)){
				return;
			}
		}	
		set.insert(temp.clone());	
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
		
//		System.out.println("璇ョ缇や腑鏈�樊鐨勮皟搴﹁В锛�);
////		System.out.println("MachineSegment:"+Arrays.toString(worstChromosome.MachineSegment));
////		System.out.println("VehicleSegment:"+Arrays.toString(worstChromosome.VehicleSegment));
////		System.out.println("InterCellSequence:"+Arrays.toString(worstChromosome.IntercellPartSequences));
//		System.out.println("鏈�樊瑙ｇ殑鍑芥暟鍊�"+worstFunction);
//		worstFunction=0;
		
		return worstChromosome;
	}
	
	
	/**鍒濆鍖栵細
		* 鍒濆瑙ｇ殑GP浜х敓鎺ュ彛锛屽垵濮嬭В閫氳繃rules浜х敓锛屽垵濮嬭В閫氳繃random浜х敓
	    * 搴旇鏄崟鐙缓绔�涓儴鍒嗭紝鍙互鍒囨崲锛屼粠鑰屽舰鎴�缁勫垵濮嬭В浜х敓鏈哄埗
		* 鈶燝P浜х敓锛涒憽rules浜х敓锛涒憿random
		* */
	private void init_population(int caseIndex) throws CloneNotSupportedException {                                   //鍗曚釜瑙ｇ殑鍒濆
        int i;		
        int mSetSize = mSet.size();
		int vSetSize = cellSet.size();	              
		Population = new ArrayList<Chromosome>();
		Chromosome chromosome = new Chromosome(mSetSize,vSetSize);
		
			
		for(i=0;i<POPULATION_SIZE;i++) {
			if(i<24){
				RulePrioirsReader(mSetSize,vSetSize,"solutions/Case1/" +caseIndex+"/"+(i + 1));
//				RulePrioirsReader(mSetSize,vSetSize,"solutions/Case1/1/" + (i + 1));  //RulePrioirs鍒濆瑙ｈ鍙�
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

			                                                   //Arraylist<Integer>杞崲涓烘暟缁�
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

				                                                   //Arraylist<Integer>杞崲涓烘暟缁�
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
//			double func_value = 100.00;
			chromosome.setFunction(func_value);
					
			AddToPopulation(Population,chromosome.clone());		
//			System.out.println("璇ョ缇や腑璋冨害瑙ｏ細");
//
//			System.out.println("绗�+(i+1)+"涓皟搴﹁В鐨勫嚱鏁板�:"+func_value);
//			


			}	
		}
	
	
	private void RulePrioirsReader(int msize, int csize, String filename) {
		// TODO Auto-generated method stub
		File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader 		   			  = new BufferedReader(new FileReader(file));

			// 鍒濆鍖栧崟鍏冧俊鎭�&鏈哄櫒瀵硅薄
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
			
			/**璇诲彇鏈哄櫒淇℃伅**/
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
			reader.readLine();// 绌烘牸琛�
			
			
			/**璇诲彇InterCellSequence淇℃伅**/
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
			reader.readLine();// 绌烘牸琛�
			
			/**璇诲彇鍗曞厓to鍗曞厓淇℃伅**/
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

	/**
	 * @Description Employed Bee Phase 
     * 瀵规瘡涓В鐢╨ocalsearch1寰楀埌neighbour锛宯eighbour瑕佷笌涔嬪墠淇濊瘉涓嶅悓
     * 鐢ㄨ瘎浼板嚱鏁版瘮杈僺ource鍜宯eighbour鐨勪紭鍔ｏ紝鍙栦紭绉��璧嬬粰neighbour
	 * @throws CloneNotSupportedException
	 */
	void EmployedBees() throws CloneNotSupportedException
	{
		    LocalSearch1();
		    LocalSearch2();  //鍔犱笉鍔犺繖涓紝瑙嗘儏鍐佃�瀹�
//		for(int i = 0;i<Population.size();i++){
//		    AdaptiveLocalSearch(Population.get(i));
//		}
    }
	
	/**
	 * @Description onlooker Bee Phase
	 * 瀵逛粠employed bee phase涓緱鍑虹殑瑙ｉ噰鐢╨ocalsearch2()锛屽緱鍒皀eighbour2
	 * 姣旇緝瀵瑰簲neighbour鍜宯eighbour2锛屽彇浼樼鐨勮祴缁檔eighbour2
	 * @throws CloneNotSupportedException
	 */
	void OnlookerBees() throws CloneNotSupportedException
	{
//        LocalSearch2();		//old version
		if(Memory.size()!=0){
			for(int i = 0;i<Population.size();i++){
				Chromosome origin = Population.get(i);
				Chromosome New = Mutation(origin,i);
		    	New.setFunction(evaluation(New));
		    	if(New.getFunction()<=origin.getFunction()){
					Population.set(i,New);
		    	}
			}
		}else{
			LocalSearch2();
		}
	}

	
	/**
	 * @Description Scoutbees' generation
	 * 閫夊嚭onlooker涓祴瀹屽�鐨刵eighbour2涓渶宸殑璁句负scout
	 * 瀵箂cout鑸嶅純锛屽埄鐢╮andom閲嶆柊鐢熸垚涓�釜瑙ｅ姞鍏eighbour2涓�
     * 灏唍eighbour2[][]璧嬪�缁檉oodnumber[][]
	 * @throws CloneNotSupportedException
	 */
	void ScoutBees() throws CloneNotSupportedException
	{
		//鍦ㄦ墍鏈塶eighbor2鐨刾opulation涓壘鍑烘渶宸殑瑙ｏ紝璁颁负worstneighbor2
		worstChromosome=worstSoFar(Population,worstChromosome);
		InsertInCollect(worstChromosome,ArchiveB);
		
		int m=0;
		for(int i=0;i<POPULATION_SIZE;i++){
			if(worstChromosome.getFunction()==Population.get(i).getFunction()){
				m=i;
				break;
			}
		}
		if(Memory.size()!=0){
			/*****Mutationg generate*****/
			Population.set(m, Mutation(worstChromosome,m));
		}else{
			/*****Randomly generate**/    
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
						if(Constants.CellToParts[SourceIndex][TargetCell]!=null){//鍦ㄨ繖閲屽浠巗ource鍒皌arget鐨勬墍瀵瑰簲搴忓垪鏄惁涓虹┖杩涜鍒ゆ柇
							if(Constants.CellToParts[SourceIndex][TargetCell].size()!=0){
								int[] temp =new int[Constants.CellToParts[SourceIndex][TargetCell].size()];                                                  //Arraylist<Integer>杞崲涓烘暟缁�
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
		}
	}

	/**
	 * @Description Mutation Process For The Whole Population
	 * @param origin
	 * @return
	 */
    private Chromosome Mutation(Chromosome origin, int index) {
    	Chromosome X_best = GetFromPool(); 
		Chromosome X_1    = Population.get( GetAnotherNumber(index));
//		Chromosome X_2    = GetFromPool();			// 鏃燘闆嗗悎鐨�
		Chromosome X_2    = GetFromPoolandB();		// 鏈塀闆嗗悎鐨�
		
		Double AgingFactor = AgingCalu(X_best);
		Double LeadingPowerFactor = LeadingPowerCalu(origin,X_best);		
//		System.out.println("("+AgingFactor+","+LeadingPowerFactor+")");

		int msize = origin.getMachineSize();
    	int vsize = origin.getVehicleSize();
		int[] tmp = new int[0];
    	Chromosome New 	  = new Chromosome(msize,vsize);
    	// 针对机器段
		New.setMachineSegment(0,tmp);
    	for(int i = 1; i <= msize; i++){
    		New.setMachineSegment(
    				i, 
    				MutateOperate(
    						origin.getMachineSegment()[i],
    						X_best.getMachineSegment()[i],
    						X_1.getMachineSegment()[i],
    						X_2.getMachineSegment()[i],
    						AgingFactor,
    						LeadingPowerFactor
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
    						X_2.getVehicleSegment()[i],
    						AgingFactor,
    						LeadingPowerFactor
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
    								ConvertToIntArray(X_2.getPartSequence()[i][j]),
    	    						AgingFactor,
    	    						LeadingPowerFactor
    						)
    				);
    			}
    		}
    	}  
    	
    	//return the changed one
		return New;
	}

    /**
     * @param chromosome 
     * @Description self adaptive method for local search for employed bee
     * by adjusting the search depth
     * to balance the explore and exploit
     */
//    private void AdaptiveLocalSearch(Chromosome cur) {
//		
//    	//
//    	if()
//	}

    /**
     * @Description get a different number compared to index
     * @param index
     * @return
     */
    private int GetAnotherNumber(int index) {
    	int result;
    	while(true){
    		result = (int)(Math.random () *POPULATION_SIZE);
    		if(result!=index) break;
    	}		
    	return result;
	}

	/**
     * @Description Power Evaluate Mechanism
	 *   鎺у埗鍙傛暟鑼冨洿鍦�1 - 0.417  (鍚庢湡鍙皟鏁�
	 *   涓斾竴鑸潵璇达紝浼氳惤鍦�(鍙�杩囧疄楠屾潵楠岃瘉涓�
     * @param origin
     * @param xBest
     * @return
     */
    private Double LeadingPowerCalu(Chromosome origin, Chromosome xBest) {
//    	System.out.println(origin.getFunction()+":"+xBest.getFunction());    	
		double GAP = (origin.getFunction() - xBest.getFunction())/origin.getFunction();
//		System.out.println("GAP"+ GAP);
//		System.out.println(Math.exp(GAP));
		return LeadingFactor*Math.exp(GAP);
//		return LeadingFactor*Math.sqrt(GAP);
	}

	/**
     * @Description Aging deteriorate Mechanism
     *   涓�埇鏉ヨ aging骞撮檺浼氫互100浣滀负鏍囧昂
	 *   鎺�鍒跺弬鏁拌寖鍥村湪 0.1 - 1
     * @param xBest : global best choromos
     * @return
     */
    private Double AgingCalu(Chromosome xBest) {
    	if(xBest.getAge() == 0)
    		return 1.0;
    	else
    		return Math.exp( -1.0*
						(Math.log(xBest.getAge())/Math.log(100))
		 	   );
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
     * @Description get an random one from the Set of Pool and ArchiveB 
     * @return
     */
    private Chromosome GetFromPoolandB() {
    	int index = (int) (Math.random()* (Memory.size()+ArchiveB.size()) );
    	if(index <Memory.size()){
    		return Memory.getIndex(index);
    	}
    	else{
    		return ArchiveB.getIndex(index-Memory.size());
    	}
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
	 * @param leadingPowerFactor 
	 * @param agingFactor 
	 * @param js 
	 * @Description 鍏蜂綋鐨勫彉寮傛搷浣�
     * 閫氳繃灏嗕紭鍏堢骇杞寲鎴愬彲浠ユ瘮杈冪殑鏁板� -- 鍒濆畾姣忎釜鏁板�涔嬮棿宸�涓�
     * @param x1
     * @param x2
     * @return
     */
    private int[] MutateOperate(int [] X, int[] XBest, int[] X_1, int[] X_2, Double agingFactor, Double leadingPowerFactor){
    	Map<Integer, Double> Result        = new HashMap<Integer, Double>();

    	//优先级数组转化成可以比较数值大小的数组
    	Map<Integer, Integer> Priors_X     = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> Priors_Xbest = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> Priors_X1    = new HashMap<Integer, Integer>();
    	Map<Integer, Integer> Priors_X2    = new HashMap<Integer, Integer>();
    	
    	int count = 1;
    	double Factor = agingFactor * leadingPowerFactor;
//    	double Factor = 0.5;
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
//	    			 Factor* ( Priors_Xbest.get(X[i]) - 	Priors_X.get(X[i])) 
//	    			+Factor* ( Priors_X1.get(X[i]) 	- 	Priors_X2.get(X[i]))
	    			MutateFactor2* ( Priors_Xbest.get(X[i]) - 	Priors_X.get(X[i])) 
	    			+MutateFactor2* ( Priors_X1.get(X[i]) 	- 	Priors_X2.get(X[i]))
	    		);
	    	}
    	}
    	else {					// did not have the memory history
	    	for(int i = 1; i < X.length; i++){
	    		Result.put(X[i],
	    			Priors_X.get(X[i])*1.0
	    			+MutateFactor2* ( Priors_X1.get(X[i]) 	- 	Priors_X2.get(X[i]))
	    		);
	    	}
    	}
    	/** The operators end**/
    	
    	/**Make It Feasible
    	 * dispatch the job according the values
    	 * **/
    	Map<Integer, Integer> Sort = MapUtil.sortByValue(Result);      	//sort
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
         * @Description localsearch1 for 锛�閲囩敤
         */
    private void LocalSearch1() throws CloneNotSupportedException {

	    int i;
//	    int a=6;
//	    int b=3;
	    Chromosome chromosome =new Chromosome(mSet.size(), cellSet.size());
	    Chromosome neighbor1 = new Chromosome(mSet.size(), cellSet.size()) ;
//	    Chromosome neighbor1 = new Chromosome(a, b) ;        //a鏄痬Set.size(),  b鏄痗ellSet.size();
 
	    for(i=0;i<POPULATION_SIZE;i++) {
	       
	    	chromosome=Population.get(i);
	    	chromosome.setFunction(evaluation(chromosome));
//	    	func_value = 40;
	    	neighbor1.MachineSegment = swap(chromosome.clone().getMachineSegment(),neighbor1.MachineSegment);
			neighbor1.VehicleSegment =  swap(chromosome.clone().getVehicleSegment(),neighbor1.VehicleSegment);
			neighbor1.IntercellPartSequences = swap(chromosome.clone().getPartSequence(),neighbor1.IntercellPartSequences);                   //swap鐨別rror鏄敱浜巌ntersequence鐨勭被鍨嬪紩璧风殑
		
			neighbor1.setFunction(evaluation(neighbor1));
//	    	neighbor_func = 50;
		    if(neighbor1.getFunction()<=chromosome.getFunction()){
			    chromosome=neighbor1;
		    }
		    Population.set(i,chromosome.clone());
//		    System.out.println("鍒濆绉嶇兢涓"+(i+1)+"涓皟搴﹁В缁忚繃ls1鍚庣殑鍑芥暟鍊硷細"+chromosome.getFunction());
	    }
    }

    /**
     * @throws CloneNotSupportedException 
     * @Description localsearch1 for 锛�閲囩敤
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
			neighbor2.IntercellPartSequences = insert(chromosome.clone().getPartSequence(), neighbor2.IntercellPartSequences);                   //swap鐨別rror鏄敱浜巌ntersequence鐨勭被鍨嬪紩璧风殑
			neighbor2.setFunction(evaluation(neighbor2));
//			neighbor_func=50;
		    
		    if(neighbor2.getFunction()<=chromosome.getFunction()){
			    chromosome=neighbor2;
		    }
		    Population.set(i,chromosome.clone());
//		    System.out.println("鍒濆绉嶇兢涓"+(i+1)+"涓皟搴﹁В缁忚繃ls2鍚庣殑鍑芥暟鍊硷細"+chromosome.getFunction());
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
    			
    						int[] temp =new int[Sequences[i][j].size()];                                                  //Arraylist<Integer>杞崲涓烘暟缁�

					        int k=0;
			        		for(int o :Sequences[i][j]){

                                  temp[k]=o;
                                  k++;
//						        	
						    }
    						temp2 =temp;                                                        //瀵规暟缁勮繘琛宻wap鎿嶄綔
    						if(temp2.length!=0){
//    	    			if(segment[index]!=null){
    							if(temp2.length!=2){
    								int[] randoms = getRandomIndex (temp2.length);
    	    					
    								int a = temp2[randoms[0]];
    								temp2[randoms[0]] = temp2[randoms[1]];
    								temp2[randoms[1]] = a;
    	    					
    							}
    						}
    						for(int p = 0;p<temp2.length;p++){                                 //灏嗘暟缁勮浆鎹㈠洖Arraylist<Integer>[][]
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
      						int[] temp =new int[Sequences[i][j].size()];                                                  //Arraylist<Integer>杞崲涓烘暟缁�

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
    								int[] randoms = getRandomIndex (temp2.length);         //鑾峰彇chromosome鐨勪紭鍏堢骇搴忓垪鐨勯暱搴�
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
    						for(int m = 0;m<temp2.length;m++){                                 //灏嗘暟缁勮浆鎹㈠洖Arraylist<Integer>[][]
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
    			int[] randoms = getRandomIndex (chromosome2[index].length);         //鑾峰彇chromosome鐨勪紭鍏堢骇搴忓垪鐨勯暱搴�
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
	private int[] RandomPriors(int[] source) { //check source閺勵垰锟芥导鐘伙拷閿涘矁绻曢弰顖氼嚠鐠炩�绱堕柅锟�
		
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
//		 System.out.println("population锛�+test.Population);
//		   
//	}

}




  

