package com.lm.algorithms;

import java.util.ArrayList;
import java.util.List;

import com.lm.Metadomain.Cell;
import com.lm.Metadomain.CellSet;
import com.lm.Metadomain.Entity;
import com.lm.Metadomain.Job;
import com.lm.Metadomain.JobSet;
import com.lm.Metadomain.Machine;
import com.lm.Metadomain.MachineSet;
import com.lm.Metadomain.Operation;
import com.lm.algorithms.abc.Chromosome;
import com.lm.algorithms.abc.DABC;
import com.lm.util.Constants;
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
public class MetaHeuristicScheduler {
/***************************属性域***********************************************************************/
	/**job's set **/
	public JobSet jobSet;
	/**machine's set **/
	protected MachineSet machineSet;
	/**cell's set **/
    protected CellSet cellSet;
	/** 未到达但是已经确定的工件的缓存列表，每新到达一个，则从这删除**/
    private List<Job> unArriJobs;

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
    }
/***************************方法域***********************************************************************/
    
    /**
     * @Description construction of MetaHeuristicScheduler
     * @param machineSet
     * @param jobSet
     * @param cellSet
     */
    public  MetaHeuristicScheduler( MachineSet machineSet, JobSet jobSet,CellSet cellSet) {
    	this.jobSet = jobSet;
		this.machineSet = machineSet;
		this.cellSet = cellSet;
		Timer.startTimer();
        
        unArriJobs = new ArrayList<Job>();
        for (Job job : jobSet) {
            unArriJobs.add(job);
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
            
          	
            Entity entity = machine.selectEntity();
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
     * @Description 扫描各单元，是否有小车处于可运输状态
     * 				有的话组批运输————将工件加入unArriJobs,并修改cell的维护队列的信息
     */
    private void scanCell(){
    	for(Cell c : cellSet)
    	{
    		//如果有小车运输完毕了，修改小车状态
    		if(!c.getVehicle().getIdle()&&c.getVehicle().IsTimeEqual(Timer.currentTime())){
    			c.getVehicle().changeIdle();
    		}

    		if(c.getVehicle().getIdle() && c.getBufferSize()!=0)//当前有小车可以运输
    		{
    			c.getVehicle().changeIdle();        		//set小车状态
    			
    			int CurID=c.getID();						//当前的单元ID
    			int NextID;									//去往的单元ID
//    			int NextTime;								//小车下一次回来的时间
    			int ArrivalTime=Timer.currentTime();		//小车到达每个单元的时间
    			
    			//get routes for vehicles
    			String result = c.SelectTransBatch().toString();
    			String[] seq  = result.split(";");
    			for(String cur:seq){
    				int m = cur.indexOf(":");
    				if(cur.substring(m).length()<3) continue;
                                                     //工件号
    				int l = cur.indexOf(":");
    				String t = cur.substring(0, l);
    				NextID = Integer.parseInt(t);
//    				NextID = cur.charAt(0)-'0';
        			ArrivalTime+=Constants.transferTime[CurID][NextID];	//小车到达目标地点的时间节点
           			Timer.addTrigger(ArrivalTime);

           			//从信息中分解出所有的加工工序
           			int i = cur.indexOf(":");
           			String[] Ops = cur.substring(i+1).split(",");       //.split 起到分割每个工序信息的作用 
           			if(Ops.length!=0){
           				for(String Op:Ops){
           					//设置当前op要加工工件的信息
           					SetMessageForOpInBatch(Op,NextID,ArrivalTime);
           				}
           			
           				CurID = NextID;	//修改下一次的CurID信息
           			}
    			}
    			//设置小车回到单元的时间
    			
    				ArrivalTime+=Constants.transferTime[CurID][c.getID()];
    				c.getVehicle().SetBackTime(ArrivalTime);
    				Timer.addTrigger(ArrivalTime);
    		}//end if
    	}
    	
    }
     
    /**
     * @Description 为当前Operation设置 NextMachineId,ArrivalTime
     * @param op
     */
    private void SetMessageForOpInBatch(String op,int NextCellId, int ArrivalTime){
                     //工件号
		int t = op.indexOf("-");
		String y = op.substring(0, t);
//		int jobId = (int)op.charAt(0)-48; 
		int jobId = Integer.parseInt(y);                                                       //工件号
		int opId  = Integer.parseInt(op.substring(t+1));       //该工件的工序号
    	
		//根据下一个单元，确定一个工件下一个要加工的机器
		Operation CurOp =  jobSet.get(jobId-1).getOperation(opId-1);
//		Operation CurOp =  jobSet.get(jobId).getOperation(opId);
		List<Machine>  MachineList=CurOp.getProcessMachineList();
		
		for (int  j = 0; j < MachineList.size(); j++) {
			if(Constants.CellForm.get( MachineList.get(j).getId() )==NextCellId){
				CurOp.SetNextMachineID(MachineList.get(j).getId()); //找到对应单元上加工的机器
				CurOp.setArrivalTime(ArrivalTime);					//设置工件的到达时间
				machineSet.get(CurOp.GetNextMachineID()-1)
								.addOperationToBuffer(CurOp);		//将各Job工序加入到各个机器加工的缓冲队列中
				break;
			}
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
//            else { // 机器工作--可先不管
//                machine.addWorkingTime();
//            }
        }
    }

    /* (non-Javadoc)  进行跨单元小车问题的调度过程
     * @see com.lm.algorithms.AbstractScheduler#schedule()
     */
    public void schedule() {
//    	log.info("————————————————————————————————当前代试验开始————————————————————————————————");
        reset();        // 重置调度状态
        Timer.resetTimer();
        initialTrigger();
//        initialPrior();


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
//        log.info("————————————————————————————————当前代试验结束————————————————————————————————");
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
    public void reset() {
    	jobSet.reset();
		machineSet.reset();
		cellSet.reset();
        unArriJobs.clear();
        for (Job job : jobSet) {
            unArriJobs.add(job);
        }
    }


//    public void initialPrior(){
//    	int[][] MachinePrior = chromosome.MachineSegment.clone(); 
//    	machine.PriorSequence();
//    }
}