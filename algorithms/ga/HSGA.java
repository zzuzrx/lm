package com.lm.algorithms.ga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.lm.algorithms.AbstractScheduler;
import com.lm.algorithms.LocalSearch.LocalSearchMethods;
import com.lm.algorithms.measure.IMeasurance;
import com.lm.domain.Cell;
import com.lm.domain.CellSet;
import com.lm.domain.Job;
import com.lm.domain.JobSet;
import com.lm.domain.Machine;
import com.lm.domain.MachineSet;
import com.lm.domain.Operation;
import com.lm.statistic.RuleFrequencyStatistic;
import com.lm.util.Constants;
/**
 * @Description:GA类

 * @author:lm

 * @time:2013-11-6 下午03:53:33

 */

public class HSGA{
/*每一种fuction结果对应的染色体，最多允许出现在种群中的次数*/	
private static final int MAX_AMOUNT_OF_EACH_CHROMOS = 8;
private static final int MIN_AMOUNT_OF_EACH_CHROMOS = 4;
/***************************属性域***********************************************************************/
	/**针对具体问题的GA's evalution process**/
	protected AbstractScheduler evaluator;
	/**适应度评估方法**/
	protected IMeasurance measurance;
	/**选择的领域搜索方法**/
	protected LocalSearchMethods LSMethods;
	/**用于随机的种子**/
	protected Random rand = new Random(System.currentTimeMillis());
	/**最佳适应度函数value对应的适应度**/
	protected double bestFitness = 0d;
	/**最佳的适应度函数的value**/
	protected double bestFunction = 0d;
	
	//种群数据相关变量
	/**机器的染色体**/
	protected List<Chromosome> mPopulation;
	/**小车的染色体**/
	protected List<Chromosome> transPopulation;
	/**最佳机器染色体序列**/
	protected Chromosome bestmChromosome;
	/**最佳小车染色体序列**/
	protected Chromosome bestTransChromosome;
//	protected Chromosome bestjChromosome;

	//算法参数
	/**the population's size. dafault=48**/
	protected final int POPULATION_SIZE;
	/**the probablity of crossover. dafault=0.6**/
	protected double CROSSOVER_PROBABILITY;
	/**the probablity of mutation. dafault=0.18**/
	protected double MUTATION_PROBABILITY;
	/**the threshold of generation numbers. dafault=100**/
	protected final int MAX_GENERATION;
	/**the threshold of chromosome numbers that have no improvement. dafault=100**/
	protected final int MAX_NONIMPROVE;
//	/** (father + children)'s size/population size . dafault=2**/
//	protected final int MAX_POPULATION_RATE=2;
	/**the probablity of father population that being reserved to next population . default=0.2**/
	protected double RESERVEDPROBABLITY=0.3;
	
	
	//the input data for inter-cell problems
	/**the machine's set **/
	protected MachineSet mSet;
	/**the job's set **/
	protected JobSet jSet;
	/**the cell's set **/
	protected CellSet cellSet;
	
	/**ending flag of evaluation process**/
	protected boolean isImproved = false;

	/** 规则分配频率统计类
	 *  Frequency statistics of heuristic rules will be used
	*/
	private RuleFrequencyStatistic stat = new RuleFrequencyStatistic();
/***************************构造函数域***********************************************************************/
	/** 
	 * @Description:construction of HSGA:默认参数
	 * @param mSet
	 * @param jSet
	 * @param cellSet
	 * @param scheduler
	 * @param measurance
	 */
	public HSGA(MachineSet mSet, JobSet jSet, CellSet cellSet,
			AbstractScheduler scheduler, IMeasurance measurance) {
		this(mSet, jSet, cellSet, scheduler, measurance, 0.6, 0.18, 100, 48);
	}

	/**
	 * @Description:construction of HSGA :自定义GA参数
	 * @param mSet
	 * @param jSet
	 * @param cellSet
	 * @param scheduler
	 * @param measurance
	 * @param cross
	 * @param mutation
	 * @param maxGeneration
	 * @param populationSize
	 * HSGA
	 * @exception:
	 */
	public HSGA(MachineSet mSet, JobSet jSet, CellSet cellSet,
			AbstractScheduler scheduler, IMeasurance measurance, double cross,
			double mutation, int maxGeneration, int populationSize) {
		CROSSOVER_PROBABILITY = cross;
		MUTATION_PROBABILITY = mutation;
		this.mSet = mSet;
		this.jSet = jSet;
		this.cellSet = cellSet;
		this.measurance = measurance;
		MAX_GENERATION = maxGeneration;
		POPULATION_SIZE = populationSize;
		MAX_NONIMPROVE = MAX_GENERATION;
//		RESERVEDPROBABLITY=0.3;
		this.evaluator = scheduler;
		mPopulation = new ArrayList<Chromosome>();
		transPopulation = new ArrayList<Chromosome>();
	}
/*****************************************************************************************************/

/****************************方法域*********************************************************************/

	/**
	 * @Description 获取stat变量(各个规则的频率)
	 * @return stat变量
	 */
	public RuleFrequencyStatistic getStat() {
		return stat;
	}

	/**
	 * @Description 设置stat变量
	 * @param stat
	 */
	public void setStat(RuleFrequencyStatistic stat) {
		this.stat = stat;
	}

	/**
	 * @Description 获取 bestFunction变量的值
	 * @return value of bestFunction
	 */
	public double getFunctionValue() {
		return bestFunction;
	}

	/** MAIN PROCESS
	 * @throws CloneNotSupportedException 
	 * @Description framework for GA
	 * 
	 */
	public void schedule() throws CloneNotSupportedException {
		
//		initialPopulation();
		init_popula_withcheck();
		
		int nonImporveCount = 0;	// 未改善代数计数器
		double func_value,fitness;	//记录每个染色体对应的函数值和适应度
		for (int currentGen = 0; currentGen < MAX_GENERATION; currentGen++) {//迭代进化过程

			LocalSearch();
			
//			System.out.println("代 " + currentGen + "\t" + bestFunction+ "\t"+
//					Outputchromos(bestmChromosome)+"\t"+
//					Outputchromos(bestTransChromosome)
//			);
			
			for (int index = 0; index < POPULATION_SIZE; index++) {
				func_value = evaluation(mPopulation.get(index),transPopulation.get(index));
				fitness = 1.0 / (1 + func_value);
				
//				System.out.println("个体"+index+"的Function值："+func_value+"——————"
//						+"染色体片段"+mPopulation.get(index)+":"+transPopulation.get(index));

				mPopulation.get(index).setFitness(fitness).setFunction(func_value);
				transPopulation.get(index).setFitness(fitness).setFunction(func_value);				
			}

			updateBestChromosome();		//更新调度规则和函数值，并判断调度结果是否有效
			
			if(isImproved){
				nonImporveCount = 0;
				isImproved = false;
			}else{
				nonImporveCount++;
			}
			
			if (nonImporveCount > MAX_NONIMPROVE) {
				break;
			}
			
			//GA 进化操作
			selectOperator();
			crossOverOperator();
			mutationOperator();
			
//			VirusOperator();					//后期扩展来做，病毒变异
			Catastrophe(currentGen);			//choose to change the args or not?
		}
		//进化结束
		System.out.println(bestFunction+ "\t"+
				Outputchromos(bestmChromosome)+"\t"+
				Outputchromos(bestTransChromosome)
		);
	}
	
	private String Outputchromos(Chromosome Chromos) {
		if(Chromos == null)
			return "null";
		return Chromos.toString();
	}

	/**
	 * @Description Catastrophe Operator(灾变遗传操作)
	 */
	private void Catastrophe(int current_generation) {
		// TODO Auto-generated method stub
		
		//灾变操作主要有两种：1.对整个种群进行灭绝，随机产生一些新的个体 2.大幅提高遗传算法的变异率
		//这一次采取第2种方式
		int K = MAX_GENERATION/5;
		if(current_generation<K){
			this.MUTATION_PROBABILITY=0.3;
			this.CROSSOVER_PROBABILITY=0.6;
		}else if(current_generation<3*K){
			this.MUTATION_PROBABILITY=0.5;
			this.CROSSOVER_PROBABILITY=0.7;
		}else{
			this.MUTATION_PROBABILITY=0.7;
			this.CROSSOVER_PROBABILITY=0.9;
		}
	}

	/**
	 * @Description Virus Operator for GA
	 * including:infection && replacement 
	 */
	private void VirusOperator() {
		// TODO Auto-generated method stub
		VirusInfection();
		VirusReplacement();
	}

	/**
	 * @Description Virus replacement
	 */
	private void VirusReplacement() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @Description Virus infection
	 */
	private void VirusInfection() {
		// TODO Auto-generated method stub
	}

	/**
	 * @throws CloneNotSupportedException 
	 * @Description local search for population ： 采用Hill Climbing方法
	 */
	private void LocalSearch() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		for (int index = 0; index < POPULATION_SIZE; index++) {	//有必要对所有个体都进行localSearch么？
			//local search for mPop 
			HillClimbing(0,index);
			//local search for transPop
			HillClimbing(1,index);
		}
	}

	/**
	 * @Description Hill Climbing for local search
	 * @param change_popula_flag
	 * @param index
	 * @throws CloneNotSupportedException 
	 */
	private void HillClimbing(int change_popula_flag,int index) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		Chromosome m_chr = mPopulation.get(index).clone();
		Chromosome trans_chr = transPopulation.get(index).clone();

		if(change_popula_flag==0){	// for machine part
			double Current = evaluation(m_chr, trans_chr);
			double neighbor1,neighbor2;
			Chromosome chr1,chr2;
			int count=0;
			Chromosome Curchr = m_chr;
			while(true){
				if(count==Curchr.size()-1) break;
				
				chr2 = Neighbor2(Curchr,count);
				neighbor2=evaluation(chr2,trans_chr);
				chr1 = Neighbor(Curchr,count++);
				neighbor1=evaluation(chr1,trans_chr);

				if(neighbor1 > Current && neighbor2 > Current) break;
//				if(neighbor1 > Current) break;
				
				if(neighbor1 > neighbor2){
					Current = neighbor2;
					Curchr  = chr2;
				}
				else{
					Current = neighbor1;
					Curchr  = chr1;
				}
			}
			mPopulation.set(index, Curchr);
			
		}else if(change_popula_flag==1){	//for trans part
			double Current = evaluation(m_chr, trans_chr);
			double neighbor1,neighbor2;
			Chromosome chr1,chr2;
			int count=0;
			Chromosome Curchr = trans_chr;
			while(true){
				if(count==Curchr.size()-1) break;
				
				chr2 = Neighbor2(Curchr,count);
				neighbor2=evaluation(m_chr,chr2);
				chr1 = Neighbor(Curchr,count++);
				neighbor1=evaluation(m_chr,chr1);

				if(neighbor1 > Current && neighbor2 > Current) break;
//				if(neighbor1 > Current) break;
				
				if(neighbor1 > neighbor2){
					Current = neighbor2;
					Curchr  = chr2;
				}
				else{
					Current = neighbor1;
					Curchr  = chr1;
				}
			}
			transPopulation.set(index, Curchr);
		}
	}

	private Chromosome Neighbor2(Chromosome Chr, int i) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		if(i<0 || i>=Chr.size()-1)
			throw new IllegalArgumentException("index out of boundary");
		Chromosome chr_new = Chr.clone();
		int left = Chr.get(i);
		if(left ==0) 	chr_new.set(i, left+1);
		else 			chr_new.set(i, left-1);
		return chr_new;
	}

	/**
	 * @Description 染色体的领域结构 ————这里采取交换 左右两个基因的领域结构
	 * @param chromosome
	 * @param i
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	private Chromosome Neighbor(Chromosome Chr, int i) throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		if(i<0 || i>=Chr.size()-1)
			throw new IllegalArgumentException("index out of boundary");
		Chromosome chr_new = Chr.clone();
		int left = chr_new.get(i);
		chr_new.set(i, chr_new.get(i+1));
		chr_new.set(i+1, left);
		return chr_new;
	}

	/**
	 * @Description 将各参数转化为一串字符串
	 * @return 一个String变量
	 */
	public String parameter() {
		StringBuilder sb = new StringBuilder(30);
		sb.append(POPULATION_SIZE).append("\t").append(CROSSOVER_PROBABILITY)
				.append("\t").append(MUTATION_PROBABILITY).append("\t").append(
						MAX_GENERATION);
		return sb.toString();
	}

	/**
	 * @Description update the value of bestmChromosome&&bestTransChromosome
	 * @throws CloneNotSupportedException
	 */
	private void updateBestChromosome() throws CloneNotSupportedException {
		bestmChromosome = bestSoFar(mPopulation, bestmChromosome);
		bestTransChromosome = bestSoFar(transPopulation, bestTransChromosome);
	}

	/**
	 * @Description evalution process for GA
	 * @param trans_chromosome chromosome for trans part 
	 * @param m_chromosome chromosome for machine part
	 */
	protected double evaluation(Chromosome m_chromosome, Chromosome trans_chromosome) {
			updateMachineRules(m_chromosome);
			updateTransRules(trans_chromosome);
			
//    		long start = System.currentTimeMillis();
//    		System.out.println("初始Timer:"+Timer.currentTime());
			try {
				evaluator.schedule();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			long end   = System.currentTimeMillis();
//			System.out.println("本次时间:"+(end-start)+"ms");
			
			/**确定fitness的过程很重要，比例太接近不利于决定进化方向**/
			return measurance.getMeasurance(evaluator);
	}

	/**
	 * @Description Find the best chromosome
	 * @param population
	 * @param bestChromosome
	 * @return bestChromosome
	 * @throws CloneNotSupportedException
	 */
	private Chromosome bestSoFar(List<Chromosome> population, Chromosome bestChromosome)
			throws CloneNotSupportedException {
		Chromosome currentBest = Collections.min(population).clone();
		//bestFitness = currentBest.getFitness();
		//bestFunction = currentBest.getFunction();
		
		if (bestChromosome == null) {
			bestChromosome = currentBest;
			bestFitness = bestChromosome.getFitness();
			bestFunction = bestChromosome.getFunction();
		} else if (currentBest.compareTo(bestChromosome) < 0) {
				bestChromosome = currentBest;
				bestFitness = bestChromosome.getFitness();
				bestFunction = bestChromosome.getFunction();
				isImproved = true;
		}
		
		return bestChromosome;
	}

	/**
	 * @Description init the population
	 */
	private void initialPopulation() {
		for (int i = 0; i < POPULATION_SIZE; i++) {
		/**********************************机器段************************************************************/
			int mSetSize = mSet.size();
			Chromosome mchromosome = new Chromosome(mSetSize);
			/**For test**/
//			mchromosome.set(0,1);
//			mchromosome.set(1,10);
//			mchromosome.set(2,3);
//			mchromosome.set(3,5);
//			mchromosome.set(4,8);
//			mchromosome.set(5,7);
//			mchromosome.set(6,0);
//			mchromosome.set(7,4);
			/**random产生初始机器段种群 **/
			for (int index = 0; index < mSetSize; index++) {
				mchromosome.set(index, rand.nextInt(Constants.mRules.length));
			}
			AddToPopulation(mPopulation,mchromosome);
		/**********************************运输段************************************************************/
			int batchNum = cellSet.size();
			Chromosome bchromosome = new Chromosome(batchNum);
			/**For test**/
//			bchromosome.set(0,1);
//			bchromosome.set(1,4);
//			bchromosome.set(2,6);
			/**random产生初始运输段种群 ，因为运输段程度不多，可以允许规则重复**/
			for (int index = 0; index < batchNum; index++) {
				bchromosome.set(index, rand.nextInt(Constants.TRules.length));
			}
			/**因为运输段程度不多，可以允许规则重复**/
			transPopulation.add(bchromosome);
//			AddToPopulation(transPopulation,bchromosome);
		}
	}
	
	private void init_popula_withcheck() throws CloneNotSupportedException{
		int mSetSize = mSet.size();
		Chromosome mchromosome = new Chromosome(mSetSize);
		int batchNum = cellSet.size();
		Chromosome bchromosome = new Chromosome(batchNum);
		while(mPopulation.size()<POPULATION_SIZE) {	//运输段的染色体添加与否是和机器段一起确定的
				/**For test**/
//				mchromosome.set(0,1);
//				mchromosome.set(1,10);
//				mchromosome.set(2,3);
//				mchromosome.set(3,5);
//				mchromosome.set(4,8);
//				mchromosome.set(5,7);
//				mchromosome.set(6,0);
//				mchromosome.set(7,4);	
				for (int index = 0; index < mSetSize; index++) {
					mchromosome.set(index, rand.nextInt(Constants.mRules.length));
				}
				/**For test**/
//				bchromosome.set(0,1);
//				bchromosome.set(1,4);
//				bchromosome.set(2,6);
				for (int index = 0; index < batchNum; index++) {
					bchromosome.set(index, rand.nextInt(Constants.TRules.length));
				}

				updateMachineRules(mchromosome);
				updateTransRules(bchromosome);
				try {
					evaluator.schedule();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				double func_value = measurance.getMeasurance(evaluator);
				mchromosome.setFunction(func_value);
				bchromosome.setFunction(func_value);
				mchromosome.setFitness(1.0 / (1 + func_value));	
				bchromosome.setFitness(1.0 / (1 + func_value));	
				
				SelectionAdd(mPopulation,mchromosome.clone(),MIN_AMOUNT_OF_EACH_CHROMOS);
				SelectionAdd(transPopulation,bchromosome.clone(),MIN_AMOUNT_OF_EACH_CHROMOS);
		}
//		System.out.println("初始化结束");
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
		
		for(Chromosome be: Population){
			if(chromosome.equals(be)) return;
			
			else if(be.equals(Population.get(Population.size()-1))){
				Population.add(chromosome);
				return;
			}
		}
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 * @Description select process for GA
	 */
	private void selectOperator() throws CloneNotSupportedException {
		// 计算总适应度值
		double totalFitness = 0.0f;
		for (Chromosome chromosome : mPopulation) {
			totalFitness += chromosome.getFitness();
		}
		if (Double.compare(totalFitness, 0) == 0) {
			// 对于GA_Y，fitness=0表示找到最优可行解
			return;
		}
		updateChromosomeProbability(mPopulation, totalFitness);
		updateChromosomeProbability(transPopulation, totalFitness);
		select(mPopulation, totalFitness);
		select(transPopulation, totalFitness);
	}

	/**
	 * @Description select methods : roulette methods(效果不好，直接sort，选前几个出来，然后用病毒遗传)
 	 * @param population
	 * @param totalFitness
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	private void select(List<Chromosome> population, double totalFitness) throws CloneNotSupportedException {
		List<Chromosome> newPopulation = new ArrayList<Chromosome>();
		Random rand = new Random(System.currentTimeMillis());
		Collections.sort(population);
		
		newPopulation.add(population.get(0).clone());
		newPopulation.add(population.get(1).clone());
		
		int reserved_size = (int)(POPULATION_SIZE * RESERVEDPROBABLITY);
		int i=0;
		while (newPopulation.size() < reserved_size ) {
//			/**
			double prob = rand.nextDouble();
			double sum=0.0;
			for (int j = 0; j < population.size(); j++) {
				sum+=population.get(j).getProb();
				if (sum > prob) {
					try {
						newPopulation.add(population.get(j).clone());
//						AddToPopulation(newPopulation,population.get(j).clone());
//						SelectionAdd(newPopulation,population.get(j).clone(),MAX_AMOUNT_OF_EACH_CHROMOS);
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
					break;
				}
			}
//			**/
//			newPopulation.add(population.get(i++).clone());
		}

		population.clear();
		population.addAll(newPopulation);
	}

	/**
	 * @Description Add a new chromos to population using rule
	 * @param newPopulation
	 * @param clone
	 */
	private void SelectionAdd(List<Chromosome> Population, Chromosome chromos,int chromos_num_upper_bound) {
		// TODO Auto-generated method stub
		if (Population.size()==0){
			Population.add(chromos);
			return ;
		}
		
		Double func_value = new Double(chromos.getFunction());
		int count=0;
		for(Chromosome be: Population){
			if (func_value.equals(be.getFunction())){
				if(count==chromos_num_upper_bound) break;
				else count++;
			}
		}
		if(count<chromos_num_upper_bound) Population.add(chromos);
	}

	/**
	 * @Description calculate the Probability of Chromosome that being selected
	 * @param population
	 * @param totalFitness
	 */
	private void updateChromosomeProbability(List<Chromosome> population,
			double totalFitness) {
		// 机器规则
		double sum = 0.0f;
		if (Double.compare(totalFitness, 0) == 0)
			return;
		Collections.sort(population);
		for (Chromosome chromosome : population) {
			sum += chromosome.getFitness();
			chromosome.setProb(sum / totalFitness);
		}
	}
	
	/**
	 * @throws CloneNotSupportedException 
	 * @Description cross process for GA
	 */
	private void crossOverOperator() throws CloneNotSupportedException {
		//List是引用传递的
		cross(mPopulation);
		cross(transPopulation);
	}

	/**
	 * @Description corss methods : 改成平移变异
	 * @param population
	 * @throws CloneNotSupportedException 
	 */
	private void cross(List<Chromosome> population) throws CloneNotSupportedException {

//		/** for two point cross
		for (int count = 0; count < POPULATION_SIZE - POPULATION_SIZE*RESERVEDPROBABLITY; count += 2) {
			Chromosome one, two;

				one = population.get(rand.nextInt((int) (POPULATION_SIZE*RESERVEDPROBABLITY))).clone();
				two = population.get(rand.nextInt((int) (POPULATION_SIZE*RESERVEDPROBABLITY))).clone();
				if (rand.nextDouble() <= CROSSOVER_PROBABILITY) {// 满足概率要求则运行交叉算子
					
					int pos1 = rand.nextInt(one.size());
					int pos2 = rand.nextInt(one.size());
					swapChromosome(one, two, pos1, pos2);
				}
				population.add(one);
				population.add(two);
		}
//		**/
		/**  //平移变异
		int reserved_size =(int) (POPULATION_SIZE*RESERVEDPROBABLITY);
		int count = reserved_size;
		while(count < POPULATION_SIZE ){
				Chromosome one = population.get(rand.nextInt(reserved_size)).clone();
				Chromosome two = population.get(rand.nextInt(reserved_size)).clone();
				
				if (new Random(System.currentTimeMillis()).nextDouble() <= CROSSOVER_PROBABILITY) {// 满足概率要求则运行交叉算子
					Chromosome new_chromos=new Chromosome(one.size());	
//					Vector<Integer> Model=new Vector<Integer>(0);
					int model_count=0;
					while(model_count<one.size()){
						new_chromos.set(model_count,rand.nextInt(2)==0?one.get(model_count):two.get(model_count));
						model_count++;
					}	
				population.add(new_chromos);
				count++;
				}
		}
		 **/
	}

	/** 突变操作 **/
	/**
	 * @Description mutate process for GA : two point mutation
	 */
	private void mutationOperator() {
		rand = new Random(System.currentTimeMillis());
		for (Chromosome chromosome : mPopulation) {
			if (rand.nextDouble() <= MUTATION_PROBABILITY) {// 满足概率要求则运行变异算子
				chromosome.set(rand.nextInt(mSet.size()), rand
						.nextInt(Constants.mRules.length));
			}
		}
		for (Chromosome chromosome : transPopulation) {
			if (rand.nextDouble() <= MUTATION_PROBABILITY) {// 满足概率要求则运行变异算子
				chromosome.set(rand.nextInt(cellSet.size()), rand
						.nextInt(Constants.TRules.length));
			}
		}
	}

	private void swapChromosome(Chromosome left, Chromosome right, int pos1,int pos2) {
		if (pos2 < pos1) {
			int temp = pos1;
			pos1 = pos2;
			pos2 = temp;
		}
		for (int index = pos1; index < pos2; index++) {
			int temp = left.get(index);
			left.set(index, right.get(index));
			right.set(index, temp);
		}
	}

	/** 根据染色体更新机器规则 */
	/**
	 * @Description update rules for machine of mset
	 * @param chromosome
	 */
	protected void updateMachineRules(Chromosome chromosome) {
		int index = 0;
		String MachineGene="";
		for (Machine m : mSet) {
			MachineGene+=chromosome.get(index)+",";
			m.setRule(Constants.mRules[chromosome.get(index++)]);
		}
//		Utils.echo(MachineGene);
	}

	/** //根据染色体更新工件规则 
//	protected void updateJobRules(Chromosome chromosome) {
//		int index = 0;
//		String JobGene="";
//		for (Job job : jSet) {
//			JobGene+=Constants.jRules[chromosome.get(index)]+",";
//			job.setRule(Constants.jRules[chromosome.get(index++)]);
//		}
//		Utils.echo(JobGene);
//	}
	**/
	
	/**
	 * @Description update rules for vehicles of cellSet
	 * @param chromosome
	 */
	protected void updateTransRules(Chromosome chromosome) {
		int index = 0;
		String CellGene="";
		for (Cell m : cellSet) {
			CellGene+=chromosome.get(index)+",";
			m.setRule(Constants.TRules[chromosome.get(index++)]);
		}
	}
	
	/**
	 * @Description output best machine chromosome
	 * @return
	 */
	public String getBestmChromosome() {
		return "machine:" + bestmChromosome.toString();
	}

	/**
	 * @Description output best trans chromosome
	 * @return
	 */
	public String getBestbChromosome() {
		return "Trans:" + bestTransChromosome.toString();
	}
    
	/**
     * @Description output schedule results for test
     */
    public void printScheduleResult() {
        updateMachineRules(bestmChromosome);
        updateTransRules(bestTransChromosome);
        try {
			evaluator.schedule();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        for (Job job : jSet) {
            for (Operation operation : job) {
                System.out.println("Job " + job.getId() + " Operatioin "
                        + operation.getId() + " on machine "
                        + operation.getProcessingMachine() + " starting at "
                        + operation.getStartTime() + " processing "
                        + (operation.getEndTime() - operation.getStartTime())
                        + " ending at " + operation.getEndTime());
            }
        }
    }

    public void printPopulation(){
    	for(int i=0;i<POPULATION_SIZE;i++){
    		System.out.println(mPopulation.get(i)+"("+mPopulation.get(i).size()+"):"+ transPopulation.get(i)+" "+mPopulation.get(i).getFunction());
    	}
    }
}
