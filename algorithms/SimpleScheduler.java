package com.lm.algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lm.domain.Cell;
import com.lm.domain.CellSet;
import com.lm.domain.Entity;
import com.lm.domain.Job;
import com.lm.domain.JobSet;
import com.lm.domain.Machine;
import com.lm.domain.MachineSet;
import com.lm.domain.Operation;
import com.lm.util.Constants;
import com.lm.util.MyLinkedList;
import com.lm.util.Timer;
import com.lm.util.Utils;

import ec.EvolutionState;
import ec.Individual;
import ec.app.myApp.DoubleData;
import ec.app.myApp.EvolveBatchingRule;
import ec.app.myApp.EvolveSequencingRule;
import ec.gp.ADFStack;

/**
 * @Description schedule process only for inter-cell problems that concerned about vehicles
 
 * extends from AbstractScheduler
 
 * @author:lm

 * @time:2013-11-6 下午05:48:26

 */
public class SimpleScheduler extends AbstractScheduler {
/***************************属性域***********************************************************************/
    
	/** 未到达但是已经确定的工件的缓存列表，每新到达一个，则从这删除**/
    private List<Job> unArriJobs;
    public static double[][] MachineToPartsFunc;//机器可加工的工件Func
//    public static double[][] CellToNextCellsFunc;//机器可加工的工件Func
    public static double[][][] CellToPartsFunc;//对应路线上可以运输的工件Func
    
    /** GPInfo class that contains some useful attributes for GP evalution process**/
    public static class GPInfo {
        public static EvolutionState state;
        public static Individual ind;
        public static int subpopulation;
        public static ADFStack stack;
        public static int threadnum;
        public static DoubleData input;
        public static EvolveSequencingRule seqProblem;
        public static EvolveBatchingRule batProblem;
//      public static EvolveAssigmentRule assProblem;
    }
/***************************方法域***********************************************************************/
    
    /**
     * @Description construction of SimpleScheduler
     * @param machineSet
     * @param jobSet
     * @param cellSet
     */
    public SimpleScheduler(MachineSet machineSet, JobSet jobSet,CellSet cellSet) {
        super(machineSet, jobSet,cellSet);
        unArriJobs = new ArrayList<Job>();
        for (Job job : jobSet) {
            unArriJobs.add(job);
        }
        
        //初始化 分数记录的数组 刚开始把值记录为最大值
        MachineToPartsFunc = new double[Constants.MachineToParts.length][];
        for(int i = 1; i < MachineToPartsFunc.length; i++){
        	MachineToPartsFunc[i] = new double[Constants.MachineToParts[i].length];
        	for(int j = 0; j < MachineToPartsFunc[i].length; j++){
        		MachineToPartsFunc[i][j] = Double.MAX_VALUE;
        	}
        }
        CellToPartsFunc = new double[cellSet.size()+1][cellSet.size()+1][];
        for(int i = 1; i < CellToPartsFunc.length; i++){
        	for(int j = 1; j < CellToPartsFunc[i].length; j++){
        		CellToPartsFunc[i][j] = new double[Constants.CellToParts[i][j].size()];
        		for(int k = 0; k < CellToPartsFunc[i][j].length; k++){
        			CellToPartsFunc[i][j][k]= Double.MAX_VALUE;
        		}
        	}
        }
    }

    
    /**
     * @Description 把Arrival中的当前可决策的工件加入到对应的机器队列
     * @param job
     */
    private void assignOperationToMachine(Job job) {
    	//如果是第一道工序，现假设只能是在一个机器上加工，所以第一道工序可直接指定
    	if(job.getNextScheduleNo()==1){
       	    Operation nextScheduleOperation = job.getNextScheduleOperation();
    		Machine selectMachine= nextScheduleOperation.getProcessMachineList().get(0);//job.selectMachine(nextScheduleOperation);
    	    job.setNextMachineID(selectMachine.getId());            	 
    	    selectMachine.addOperationToBuffer(nextScheduleOperation);//这个应该在batch决策完成处添加
    	    nextScheduleOperation.setArrivalTime(Timer.currentTime());
    	}
    	else{
    	 Operation nextScheduleOperation = job.getNextScheduleOperation();
         // 工件仍有下一道工序
         if (nextScheduleOperation != null) {
            // TODO 加入对跨单元转移的考虑
            /**看是否需要跨出单元，是否不需要直接指派，不然放置到单元的BUFFER上面**/
        	Cell c=cellSet.get(job.getLastMachine().getCellID()-1);
        	int ChooseResult=c.CanGoWhichCell(nextScheduleOperation);
        	boolean ShouldChangeCell=false;
        	
        	//工件根据chooseResult的结果来决定，留还是不留在本单元内
        	switch(ChooseResult){
        	case 0:
        		ShouldChangeCell=false;
        		break;
        	case 1:
        		ShouldChangeCell=job.selectCell(nextScheduleOperation);
        		break;
        	case 2:
        		ShouldChangeCell=true;
        		break;
        	}
//        	Utils.echo("当前工序"+job.getCurrentOperation().getName()+"转移"+(ShouldChangeCell?"需要":"不需要")+"跨单元");
            if(ShouldChangeCell==false){//还在同一单元内加工，就直接可以得到
            		Machine selectedMachine = machineSet.get(c.getCurJobMachineID()-1);//获取能加工的机器
            		job.setNextMachineID(selectedMachine.getId());            	 
            		selectedMachine.addOperationToBuffer(nextScheduleOperation);//这个应该在batch决策完成处添加
            		nextScheduleOperation.setArrivalTime(Timer.currentTime());
            	   //job.getCurrentOperation().getProcessingTime(selectedMachine)//工件当前工序的加工时间
            		
            		/**统计Func分数，**/
            		
            }
            else{//如果是不同单元
            	c.addTransBatch(nextScheduleOperation);
            }   
         }//end if
    	}//end else
    }

    /**
     * @Description 把实体中的工件都加入到各机器队列
     * @param entity
     */
    private void assignOperationToMachine(Entity entity) {
        for (Operation operation : entity.getOperations()) {
            assignOperationToMachine(operation.getJob());
        }
    }

    /**
     * @Description 卸载当前处理的工件至下一个缓冲区
     * @param machine
     */
    private void unload(Machine machine) {
        Entity processingEntity = machine.getProcessingEntity();
        if (processingEntity != null) {
        	//log.info(machine.toString()+"卸载工件:"+processingEntity.getOperations().get(0).toString());
            processingEntity.scheduleOperations();
            assignOperationToMachine(processingEntity);
            machine.setProcessingEntity(null);
        }
    }
    
    /**
     * @Description 调度一道工序
     * @param machine
     */
    private void load(Machine machine) {
        if (!machine.isBufferEmpty()) {
        	/**调试信息--查看buffer情况
        	log.info(machine.toString()+"缓冲区情况");
        	for(int i=0;i<machine.getBuffer().size();i++){
    			  log.info(machine.getBuffer().get(i).toString());
    		}
        	**/
            Entity entity = machine.selectEntity(MachineToPartsFunc);
            machine.removeOperationFromBuffer(entity);// 从缓冲区删除该工序
         
            // 设置工序开始结束时间
            entity.setStartTime(Timer.currentTime());
            int setupTime =entity.GetSetupTime(machine.getId());
            int processingTime = entity.getProcessingTime();
            int endTime = Timer.currentTime() + setupTime+processingTime;
            entity.setEndTime(endTime);
            entity.setProcessingMachine(machine);
            
            // 设置机器信息
            machine.setProcessingEntity(entity);
            machine.setNextIdleTime(endTime);
            // 时间步进
            Timer.addTrigger(endTime);
        }
    }

    /**
     * @Description 扫描各单元，是否有小车处于可运输状态有的话组批运输————将工件加入unArriJobs,并修改cell的维护队列的信息
     */
    private void scanCell(){
    	for(Cell c : cellSet)//扫描所有单元
    	{
    		//如果有小车运输完毕了，修改小车状态
    		if(!c.getVehicle().getIdle()&&c.getVehicle().IsTimeEqual(Timer.currentTime())){
    			c.getVehicle().changeIdle();
    		}

    		if(c.getVehicle().getIdle() && c.getBufferSize()!=0)//当前有小车可以运输
    		{
    			//assign batch~~根据批中第一个工件去向，得到下一个要去的单元
    			List<Operation> result=c.SelectTransBatch(CellToPartsFunc);
    			
        		//set小车状态
    			c.getVehicle().changeIdle();
    			/**当前的单元ID**/
    			int current=c.getID();
    			/**去往的单元ID**/
    			int next=Constants.CellForm.get(result.get(0).GetNextMachineID());
    			/**小车到达目标地点的时间节点**/
    			int ArrivalTime=Timer.currentTime()+Constants.transferTime[current][next];
    			/**小车下一次回来的时间**/
    			int NextTime=ArrivalTime+Constants.transferTime[next][current];
    			c.getVehicle().SetBackTime(NextTime);
    			Timer.addTrigger(ArrivalTime);
    			Timer.addTrigger(NextTime);
    			
    			/**修改各队列状态**/
    			for (int i = 0; i < result.size(); i++) {
    				//设置工件的到达时间
    				result.get(i).setArrivalTime(ArrivalTime);
    				//根据确定的下一个单元，更新当前每个
        			//将各Job工序加入到各个机器加工的缓冲队列中
					machineSet.get(result.get(i).GetNextMachineID()-1).addOperationToBuffer(result.get(i));
				}//end for
    		}//end if
    	}
    	
    }
     
    /** 
     * @Description 扫描工件是否有作业新到达
     *  
     */
    private void scanJobs() {
        List<Job> arrivedJobs = new ArrayList<Job>();
        for (Job job : unArriJobs) {
            if (job.getRelDate() == Timer.currentTime()) { // 有新工件到达
                // 选择调度机器并把工序加入其缓冲区
                assignOperationToMachine(job);
                arrivedJobs.add(job);
            }
        }
        unArriJobs.removeAll(arrivedJobs);
    }

    /**
     * @Description 扫描机器是否有完工
     */
    private void scanMachine() {
        for (Machine machine : machineSet) {
            if (machine.getNextIdleTime() <= Timer.currentTime()) {// 机器空闲
                // 卸载当前处理的工件至下一个缓冲区
                unload(machine);
                // 缓冲区不空则调度一道工序
                load(machine);
            }
            else { // 机器工作--可先不管
                machine.addWorkingTime();
            }
        }
    }

    /* (non-Javadoc)  进行跨单元小车问题的调度过程
     * @see com.lm.algorithms.AbstractScheduler#schedule()
     */
    @Override
    public void schedule() {
//    	log.info("————————————————————————————————GA当前代试验开始————————————————————————————————");
        reset();        // 重置调度状态
        Timer.resetTimer();
        initialTrigger();

        
        while (!jobSet.isScheduleAll()
                || !machineSet.isIdleAll(Timer.currentTime())
        	   ) {
//        	/**
        	Utils.echo("当前时间"+Timer.currentTime());
        	
        	Utils.echo("当前Cell0小车状态"+cellSet.get(0).getVehicle().getIdle());
//        	if(Timer.currentTime()==453){
//        		Utils.echo("当前断点");
//        	}
//        	**/
            if (!unArriJobs.isEmpty()) {
                scanJobs();
            }
            scanMachine();
            scanCell();
            Timer.stepTimer();
//          log.info("当前时间"+Timer.currentTime());
        	Utils.echo("当前Cell0小车状态"+cellSet.get(0).getVehicle().getIdle());
            PrintInfo();

        }
//        PrintFuncResult();
//    	FormPriorSequences();
//        log.info("————————————————————————————————GA当前代试验结束————————————————————————————————");
    }

    /**
     * @param cellToParts 
     * @param cells 
     * @param machines 
     * @Description write the priors of rule to file 
     */
    private void WriteToFile(StringBuilder[] machines, MyLinkedList[] cells, StringBuilder[][] cellToParts) {
    	BufferedWriter bw;
    	try {
    		//if the dir is exists
    		File file = new File(Constants.RULESET_DIR);
    		if (!file.exists()) {
    			   file.mkdir();
    		}
    		//write the file
    		bw = new BufferedWriter(new FileWriter(
    				Constants.RULESET_DIR+ "/"+Constants.mRules[Constants.MachineRuleIndex].toString()
					+"+"+Constants.TRules[Constants.TransRuleIndex].toString()
					));
			
			for(int i = 1; i < MachineToPartsFunc.length; i++){
				bw.write("machine"+i+":"+machines[i].toString()+"\n");
	    	}
			bw.newLine();
	    	for(int i=1; i< cellToParts.length; i++){
	    		for(int j=1; j< cellToParts[i].length; j++){
	    			bw.write(i+"to "+j+":"+cellToParts[i][j].toString()+"\n");
	    		}
	    	}
	    	bw.newLine();
	    	for(int i=1; i< cellToParts.length; i++){
	    		bw.write("cell"+i+":"+cells[i].toString()+"\n");
	    	}
			
			//Close the BufferedWriter
			if (bw != null) {
				bw.flush();
				bw.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
     * @Description 根据Func结果来生成工件的优先级序列关系
     */
    private void FormPriorSequences() {
    	StringBuilder []machines;
    	MyLinkedList  []Cells;
    	StringBuilder [][]CellToParts;
    	
    	//机器的优先级序列
    	machines = new StringBuilder[Constants.MachineToParts.length];
    	for(int i = 1; i < MachineToPartsFunc.length; i++){
    		machines[i] = new StringBuilder("0,");
    		//按照Func大小，依次连接对应的工件
    		while(true){
    			//找出每一次最优的工件
    			int k = 0;
    			for(int j=1; j<MachineToPartsFunc[i].length; j++){
    				if(MachineToPartsFunc[i][j] < MachineToPartsFunc[i][k]){
    					k = j;
    				}
    			}
    			
    			if(MachineToPartsFunc[i][k] == Double.MAX_VALUE) break;
    			
    			machines[i].append(Constants.MachineToParts[i][k]+",");
    			MachineToPartsFunc[i][k] = Double.MAX_VALUE;
    		}
    	}
    	//单元间工件的优先级序列 && 单元优先级
    	CellToParts 	= new StringBuilder[cellSet.size()+1][cellSet.size()+1];
    	Cells 			= new MyLinkedList[cellSet.size()+1];
    	for(int i=1; i< CellToParts.length; i++){
    		Cells[i] = new MyLinkedList<Double>();	//设置优先级的数组
    		
    		for(int j=1; j< CellToParts[i].length; j++){
    			CellToParts[i][j] = new StringBuilder("");
    			if(Constants.CellToParts[i][j].size() != 0){ 	//存在该对应的工件buffer
    				double value = 0;							//统计每个buffer中所有的value总和
    				int    count = 0; 							//统计每个buffer中被决策的工件总和
    				boolean Flag[] = new boolean[Constants.CellToParts[i][j].size()];
    				
    				//按照Func大小，依次连接对应的工件
	    			while(true){
	        			//找出每一次最优的工件
	        			int k = 0;
	        			for(int t=1; t<CellToPartsFunc[i][j].length; t++){
	        				if(CellToPartsFunc[i][j][t] < CellToPartsFunc[i][j][k]){
	        					k = t;
	        				}
	        			}
	        			if(CellToPartsFunc[i][j][k] == Double.MAX_VALUE) break;
	        			
	        			count++;
	        			Flag[k]= true;
	        			value+=CellToPartsFunc[i][j][k];
	        			CellToParts[i][j].append(Constants.CellToParts[i][j].get(k)+",");
	        			CellToPartsFunc[i][j][k] = Double.MAX_VALUE;
	        		}
	    			
	    			/**

	    			 */
	    			LinkingRemainingParts(CellToParts,i,j,Flag);
	    			
	    			Cells[i].insert(j, value/(double)count);
    			}
    		}
    	}
    	
    	//打印出结果来观察
    	for(int i = 1; i < MachineToPartsFunc.length; i++){
    		System.out.println("machine"+i+":"+machines[i].toString());
    	}
    	for(int i=1; i< CellToParts.length; i++){
    		for(int j=1; j< CellToParts[i].length; j++){
    			System.out.println(i+"to "+j+":"+CellToParts[i][j].toString());
    		}
    	}
    	for(int i=1; i< CellToParts.length; i++){
    		System.out.println(Cells[i].toString());
    	}
    	
    	WriteToFile(machines,Cells,CellToParts);
	}


	/**
	 * @Description 把sources里面未被决策到的部分连接到target当中
	 * 对于Constants出现，但是 超启发选择的时候没出现的结果
	 * 即跟上游单元是同一个单元的情况
	 * 所以默认先把这些连接到CellToparts[i][j]的前面
	 * @param cellToParts
	 * @param  
	 * @param pre_cell 
	 * @param flags
	 */
	private void LinkingRemainingParts(StringBuilder[][] source,
			int upper, int down, boolean[] flags) {
		// TODO Auto-generated method stub
		
		StringBuilder remains = new StringBuilder("");
		for(int t=0; t<Constants.CellToParts[upper][down].size(); t++){
			if(flags[t] == false) remains.append(Constants.CellToParts[upper][down].get(t)+",");
		}
		remains.append(source[upper][down]);
		source[upper][down] = new StringBuilder(remains);
	}


	/**
     * @throws IOException 
	 * @Description 打印规则产生的Func结果
     */
    private void PrintFuncResult(){
    	
    	System.out.println("机器段————————————————————————————————");
    	for(int i = 1; i < MachineToPartsFunc.length; i++){
    		System.out.println("机器"+i+"上Func情况:");
    		StringBuilder sentens = new StringBuilder("");
    		for(int j = 1; j < MachineToPartsFunc[i].length; j++){
    			sentens.append(Constants.MachineToParts[i][j]+"————"+MachineToPartsFunc[i][j]+";   ");
    		}
    		System.out.println(sentens.toString());
    	}
//    	System.out.println("单元段————————————————————————————————");
    	for(int i = 1; i < CellToPartsFunc.length; i++){
    		System.out.println("单元"+i+"上Func情况:");
    		for(int j = 1; j < CellToPartsFunc[i].length; j++){
    			StringBuilder sentens = new StringBuilder("");
    			sentens.append(j+"——");
    			for(int k = 0; k < CellToPartsFunc[i][j].length; k++){
    				sentens.append(Constants.CellToParts[i][j].get(k)+":"+CellToPartsFunc[i][j][k]+";   ");
    			}
    			System.out.println(sentens.toString());
    		}
    	}
    	
	}


	/**
     * @Description  进行调度过程
     */
    private void PrintInfo() {
		// TODO Auto-generated method stub
    	  Utils.echo("机器加工信息:");
    	  for (Machine machine : machineSet) {
    		  Utils.echo(machine.getName()
    				  +"  当前加工工件:"+machine.GetProcessingEntityName()
    				  +"   BufferSize:"+machine.getBuffer().size()
    				  );
    		  for(int i=0;i<machine.getBuffer().size();i++){
    			  Utils.echo(machine.getBuffer().get(i).toString());
    		  }
    	  }
    	  Utils.echo("\n");
    	  Utils.echo("工件加工状态：");
    	  for (Job j : jobSet) {
    		  Utils.echo(j.getName()
    				  +"   下一道工序:"+j.getNextScheduleNo()
    				  +"   下一道加工机器:"+j.getNextMachineID()
    				  );
    	  }
    	  Utils.echo("\n");
    	  Utils.echo("单元上Buffer状态：");
    	  for (Cell c:cellSet){
    		  Utils.echo(c.getname()
    				  +"   小车状态:"+ (c.getVehicle().getIdle()?"空闲":"在运输") 
    				  +"   Buffer上工件总数"+c.getBufferSize());
    	  }
    	  Utils.echo("------------------------------------------------------");
	}

	/**
     * 初始化时间点Trigger
     */
    private void initialTrigger() {
        for (Job job : jobSet) {
            Timer.addTrigger(job.getRelDate());
        }
//        Timer.printTrigger();
    }

    /* (non-Javadoc) 重置
     * @see com.lm.algorithms.AbstractScheduler#reset()
     */
    @Override
    public void reset() {
        super.reset();
        unArriJobs.clear();
        for (Job job : jobSet) {
            unArriJobs.add(job);
        }
    }
}
