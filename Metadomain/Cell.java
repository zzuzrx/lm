package com.lm.Metadomain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.lm.Metadomain.Buffer;
import com.lm.Metadomain.Operation;
import com.lm.Metadomain.Vehicle;
import com.lm.Metadomain.Machine;
import com.lm.algorithms.abc.Chromosome;
import com.lm.util.Constants;
import com.lm.util.MapUtil;
import com.lm.util.Timer;

/**
 * @Description Class Cell for meta-heuristic

 * @author:lm

 * @time:2015-03-31 上午11:22:32

 */
public class Cell {
/***************************属性域***********************************************************************/
	/** 单元ID **/
	public int id;
	/** 单元名称 */
	private String name;
	/** 小车对象 */
	private Vehicle transCar;


	/** 下游能到的单元集合 **/
	public  List<Integer> NextCell;
	/** 去各单元的工件缓存区 **/
	private List<Buffer>  JobBuffer;  //JobBuffer[1] 就表示跨到NextCell[1]单元 的缓存区
	/** 当前工件在本单元的下一次加工机器的ID **/
	private int CurJobMachineID;
	/** 小车决策运输的优先级序列 即 vehicleSegment for cur cell **/
	private	int[] PriorSequence;
	/** 每个下游单元内工件的序列 即	the sequence of parts for buffer of each cell **/
	private ArrayList<Integer>[] IntercellPartSequences;
	/** 运输路径**/
	private StringBuffer Route;
	/** 路径对应的耗费 **/
	private double RouteCost;
	
/***************************方法域***************************************************************************************************/
	/**
	 * @Description 单元构造函数(construction of Cell)
	 * @param Id
	 * @param cellNum
	 * @param transSize
	 */
	public Cell(int Id, int cellNum, int transSize) {
		this.id = Id;
		this.name = "Cell" + Id;
		this.transCar = new Vehicle(transSize);
		this.NextCell = new ArrayList<Integer>();
	}

	/**
	 * @Description 获取源机器到目标机器的转移时间(transfer time from source cell to destination cell)
	 * @param srcNumInCell
	 *            源机器所属单元号(the cell index for source cell)
	 * @param destNumInCell
	 *            目标机器所属单元号(the cell index for destination cell)
	 * @return 转移时间(the tranfer time)
	 * */
	public int getTransferTime(int srcNumInCell, int destNumInCell) {
		if (Constants.transferTime == null) {
			throw new NullPointerException(
					"transferTime should be initialized first!");
		}
		if (srcNumInCell == destNumInCell) {
			return 0;
		}
		return Constants.transferTime[srcNumInCell][destNumInCell];
	}

	/**
	 * @Description 初始化JobBuffer长度
	 * @param sum 下游单元总数：表示要建的缓冲区长度
	 */
	public void initJobBuffer(int sum) {
		this.JobBuffer = new ArrayList<Buffer>(sum);
		for(int i = 0;i < sum; i++) {
			Buffer e = new Buffer();
			JobBuffer.add(e);
		}
	}

	/**
	 * @Description 设置下游单元集合
	 * @param seq
	 */
	public void setNextCell(String[] seq) {
		for (int j = 0; j < seq.length; j++) {
			if (Integer.parseInt(seq[j]) == 1) {
				NextCell.add(j + 1);
			}
		}
		initJobBuffer(NextCell.size());
	}

	/**
	 * 设置该单元与其余单元的关系
	 * @param cellNum
	 */
	public void setCellLink(int cellNum) {
		for (int i = 1; i <= cellNum; i++) {
			if (i == this.id) {
				continue;
			}
			NextCell.add(i);
		}
		initJobBuffer(NextCell.size());		
	}
	
	/**
	 * @Description 判断当前CELL是否完成组批--未完成
	 * @return
	 */
	public boolean isTransComplete() {
		return false;
	}

	/** 
	 * @Description 重置作业状态 
	 */
	public void reset() {
		//重置不能改变NextCell, 所以不能调用NextCell.clear(); 
		for(int i = 0;i < JobBuffer.size(); i++) {
			JobBuffer.get(i).clear();
		}
		//重置还要改变小车状态，不要忘记了
		if(transCar.getIdle() == false){
			transCar.changeIdle();
		}
	}
	
	/**
	 * @Description 获取小车
	 */
	public Vehicle getVehicle() {
		return this.transCar;
	}
	
	/**
	 * @Description 获取ID
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * @Description 获取单元的名字
	 */
	public String getname() {
		return this.name;
	}
	
	/**
	 * @Description 获取CurJobMachineID的值
	 */
	public int getCurJobMachineID() {
		return this.CurJobMachineID;
	}
	
	/**
	 * @Description 获取BUFFER总长度
	 */
	public int getBufferSize() {
		int sum = 0;
		for(int i = 0;i < this.JobBuffer.size(); i++) {
			sum += this.JobBuffer.get(i).size();
		}
		return sum;
	}
	
	/**
	 * @Description 获取对应队列上面的Buffer
	 */
	public Buffer getBuffer(int i) {
		return this.JobBuffer.get(i);
	}
	
	/**
	 * @Description 获取PriorSequence
	 */
	public int[] getPriorSequence() {
		return PriorSequence;
	}

	/**
	 * @Description 设置priorSequence
	 */
	public void setPriorSequence(int[] priorSequence) {
		PriorSequence = priorSequence;
	}
	
	/**
	 * @Description 获取IntercellPartSequences
	 */
	public ArrayList<Integer>[] getIntercellPartSequences() {
		return IntercellPartSequences;
	}
	
	/**
	 * @Description 设置IntercellPartSequences
	 */
	public void setIntercellPartSequences(ArrayList<Integer>[] intercellPartSequences) {
		IntercellPartSequences = intercellPartSequences;
	}
	
	/**
	 * @Description 将当前完成某道工序、并且准备跨单元的工件的工序加入到缓冲队列当中
	 * @param currentOperation
	 */
	public void addTransBatch(Operation currentOperation) {
		List<Machine> NextMachines=currentOperation.getProcessMachineList();
		for(int i = 0;i < NextMachines.size(); i++){
			int c = NextCell.indexOf(NextMachines.get(i).getCellID());	//得到当前buffer号
			JobBuffer.get(c).addOperation(currentOperation);
		}
	}

    /**
     * @Description 通过决策，判断下一道工序可以在哪些单元加工
     * 如果下一道工序本单元内不存在可以加工的机器，那肯定可以直接跨出；
     * 否则，存在这个本单元、外单元的冲突，通过规则来判断是否值得跨出
     * @param currentOperation
     * @return 
     * 0--下一道工序只能在本单元内加工
     * 1--下一道工序既能在本单元，又能在其他单元加工，则工件需要决策
     * 2--下一道工序只能在其他单元加工
     */
	public int CanGoWhichCell(Operation currentOperation) {
		List<Machine> NextMachines = currentOperation.getProcessMachineList();
		for(int i = 0;i < NextMachines.size(); i++) {
			int c = NextMachines.get(i).getCellID();	//得到当前单元号
			if(c == this.id) {
				this.CurJobMachineID = NextMachines.get(i).getId();
				if(NextMachines.size() == 1){
					return 0;
				}else {
					return 1;
				}
			}
		}
		return 2;
	}
	
	/**
	 * @Description 选择要运输的工件批次 && 运输路径
	 * @return 路径表示的字符串
	 * 示例  
	 * 1: 2-3,4-2; 2:3-1;
	 * (表示先把工序2-3，4-2运到1单元，再把工序3-1运到2单元)
	 */
	public StringBuffer SelectTransBatch() {
        StringBuffer SelectRoutes = new StringBuffer("");
        int    BatchSum = 0;
        // get the batch using PriorSequences
        for (int CurDesCellNo : PriorSequence) {
        	SelectRoutes.append(CurDesCellNo).append(":");
        	Buffer CurBuffer = JobBuffer.get(GetBufferIndexInNextCellByCellNo(CurDesCellNo));
        	
        	if(CurBuffer.size() == 0){
        		SelectRoutes.append(";");
        		continue;
        	}
        	
        	//Find the Operation in CurBuffer
        	for(int curop: IntercellPartSequences[CurDesCellNo]){
        	    int b = SelectRoutes.length();
        		SelectRoutes.append(FindOperationInBuffer(CurBuffer,curop));
        		if(SelectRoutes.length() > b + 3){   //< b + 3说明没有连接上新的operation
        			BatchSum++;
        		}
        	}
        	SelectRoutes.append(";");
        	if(BatchSum >= transCar.getCapacity())	break;
        }
		return SelectRoutes;
	}

	/**
	 * @Description 选择要运输的工件批次 && 运输路径(新的运输策略)
	 * @return 路径表示的字符串
	 * 示例  
	 * 1: 2-3,4-2; 2:3-1;
	 * (表示先把工序2-3，4-2运到1单元，再把工序3-1运到2单元)
	 */
	public StringBuffer SelectTransWithNewStrategy() {
		int preCell   = id;							//本单元号
        int RemainSum = transCar.getCapacity();		//批次中还可以加入的工件数量
        //形成端点集
        Vector<Integer> NextCellCandidate = new Vector<Integer>();
        for (int cellId = 0; cellId < JobBuffer.size(); cellId++) {
        	if(JobBuffer.get(cellId).getOperations().size()>0) {
        		NextCellCandidate.add(cellId);
        	}
        }
        
        Route = new StringBuffer("");
        RouteCost = -1;
        // Find proper transport route
        FindRoute(RemainSum, NextCellCandidate,new StringBuffer(""), 0, preCell, 0);
        //Delete the ops from the buffer
        DeleteOpsByRoutes(Route);
        
	    return Route;
	}
	
	/**
	 * @Description 选择要运输的工件批次 && 运输路径--加入策略的影响
	 * @param chromosome  影响到的染色体
	 * @return 路径表示的字符串
	 * 示例  
	 * 1: 2-3,4-2; 2:3-1;
	 * 策略对工件之间次序修改的影响，只影响比较的工件的相互次序 
	 * (表示先把工序2-3，4-2运到1单元，再把工序3-1运到2单元)
	 */
	@SuppressWarnings("unchecked")
	public StringBuffer SelectTransBatchWithStrategy(Chromosome chromosome){
        int    preCell=id;										//本单元号
        int RemainSum = transCar.getCapacity();					//批次中还可以加入的工件数量
        double AllTransferTime = 0;								//总的TransferTime
        double r1 = 5.2;										//cost的参数
        
        //形成端点集
        Vector<Integer> NextCellCandidate = new Vector<Integer>();
        for (int cellId = 0; cellId < JobBuffer.size(); cellId++) {
        	if(JobBuffer.get(cellId).getOperations().size()>0) {
        		NextCellCandidate.add(cellId);
        	}
        }

        //在端点集当中逐次构造路径
        StringBuffer Routes=new StringBuffer("");				//运输路线的结果
        if(NextCellCandidate.size() == 1) {
        	Vector<String> []EachCellBatch = new Vector[NextCellCandidate.size() + 1];	//存放每个批次里面的工序id
        	ReduceCost(0,NextCellCandidate.get(0),preCell,EachCellBatch,AllTransferTime,RemainSum,chromosome);
    	    //Adding into Routes
        	Routes.append(NextCell.get(NextCellCandidate.get(0))).append(":");
        	for(int i = 0; i< EachCellBatch[0].size(); i++) {
        		if(i != 0){
        			Routes.append(",");
        		}
        		Routes.append(EachCellBatch[0].get(i));
        	}
            Routes.append(";");
            
            //update remain sum and remove the jobs in the JobBuffer
        	DeleteOps(EachCellBatch[0]);
        	return Routes;
    }
        
        /** Method 1 -- 类似最小生成树方法      
        double[] CostsForCells = new double[JobBuffer.size()];	//每个单元的评分
    	Vector<Integer> RouteCellPriors = new Vector<Integer>();//本次运输单元的次序

        while(NextCellCandidate.size()!=0 && RemainSum >0){

        	double costMax = 0;
        	double costMin = Double.MAX_VALUE;
        	int EndResult  = 0;
        	Vector<String> []EachCellBatch =new Vector[NextCellCandidate.size()+1];	//存放每个批次里面的工序id

        	//find the best destination 
        	for(int cur = 0; cur < NextCellCandidate.size(); cur++) {

        		double cost = ReduceCost(cur,NextCellCandidate.get(cur),preCell,EachCellBatch,AllTransferTime,RemainSum,chromosome);

//        		if(costMax< cost ) {
//        			costMax   = cost;
//        			EndResult = cur;
//        		}

        		if(costMin > cost ) {
        			costMin   = cost;
        			EndResult = cur;
        		}
        	} 

        	//Adding into Routes
        	Routes.append(NextCell.get(NextCellCandidate.get(EndResult))).append(":");
        	for(int i=0; i< EachCellBatch[EndResult].size(); i++) {
        		if(i!=0){
        			Routes.append(",");
        		}
        		Routes.append(EachCellBatch[EndResult].get(i));
        	}
            Routes.append(";");

        	//update remain sum and remove the jobs in the JobBuffer
        	RemainSum-=EachCellBatch[EndResult].size();
        	DeleteOps(EachCellBatch[EndResult]);
        	RouteCellPriors.add(NextCell.get(NextCellCandidate.get(EndResult)));
        	NextCellCandidate.remove(EndResult);

        	//Adding transferTime to route
        	AllTransferTime+=Constants.transferTime[preCell][NextCell.get(EndResult)];
        	preCell = NextCell.get(EndResult);
        }

        //modify the priors for cells in chromos
        if(RouteCellPriors.size()>1){
        	ModifyCellPriors(chromosome,this.id,RouteCellPriors);
        }
        **/
    	
//    	/** Method 2 : 类似哈夫曼编码形式 
//      //效果不好，是不是跟 合并的时候，没有注意到批次数量有关系  
        boolean[] GetCandidate = new boolean[NextCellCandidate.size()];			    //标记哪些candidate已经被加入到单元中
        boolean[] RemovePoint  = new boolean[NextCellCandidate.size()];			    //标记哪些candidate已经被加入删掉了(并到其他单元中了)
        Vector<String> [] BestCellBatch =new Vector[NextCellCandidate.size()+1];	//存放每个批次里面的工序id
        Vector<String> [] EachCellBatch =new Vector[NextCellCandidate.size()+1];	//计算过程中的临时序列
		int[] TransferTime = new int[NextCellCandidate.size()];	 				    //每个子运输段的总运输时间
		StringBuffer[] RouteSegments = new StringBuffer[NextCellCandidate.size()];  //每个子段的构成
		for (int i =0 ; i < RouteSegments.length; i++) {
			RouteSegments[i] = new StringBuffer("");
		}
		int[] EndOfCell = new int[NextCellCandidate.size()];					    //每个子运输段的结尾标记，初始都是自身(因为长度为1)
		for (int i = 0; i < EndOfCell.length; i++) {
			EndOfCell[i] = i;
		}
		
		while( NotAllFlag(GetCandidate) && RemainSum >0){
        	double costMax = -Double.MAX_VALUE;
        	double costMin = Double.MAX_VALUE;
        	int CurStart   = -1;
        	int CurEnd     = -1;
    		//calculate cost_dec[i][j]
        	for(int i = 0; i < NextCellCandidate.size(); i++) 
        		for(int j = 0; j < NextCellCandidate.size(); j++) 
        			if(!RemovePoint[j] && !RemovePoint[i] && i!=j) {
        				double Cost_i;
        				double Cost_j;
        				double Cost_ij;
        				int    Amount_i;
        				
        				// cal the Cost 0i0
        				if(!GetCandidate[i]) {
        					Cost_i = ReduceCost(i,NextCellCandidate.get(i),preCell,EachCellBatch,
        								        0,RemainSum,chromosome);
        					Amount_i = EachCellBatch[i].size();
        				}else {
        					Cost_i = ReCalculCost(i,preCell,RouteSegments[i],0);
        					Amount_i = 0;
        				}
        				
        				//cal the remain part
        				if(!GetCandidate[j]) {
        					//cal 0j0
        					Cost_j  = ReduceCost(j,NextCellCandidate.get(j),preCell,EachCellBatch,
        								 Constants.transferTime[preCell][NextCell.get(NextCellCandidate.get(i))]+TransferTime[i]
        								+Constants.transferTime[preCell][NextCell.get(NextCellCandidate.get(EndOfCell[i]))],
        								RemainSum-Amount_i,chromosome);
        					//cal ij 
        					Cost_ij = ReduceCost(j,NextCellCandidate.get(j),NextCellCandidate.get(EndOfCell[i]),EachCellBatch,
		    							 Constants.transferTime[preCell][NextCell.get(NextCellCandidate.get(i))]+TransferTime[i],
								         RemainSum-Amount_i,chromosome);
        				}else {
        					//cal 0j0
        					Cost_j  = ReCalculCost(j,preCell,RouteSegments[j],
        								 Constants.transferTime[preCell][NextCell.get(NextCellCandidate.get(i))]+TransferTime[i]
        								+Constants.transferTime[preCell][NextCell.get(NextCellCandidate.get(EndOfCell[i]))]
        								);
            				//cal ij 
        					Cost_ij = ReCalculCost(j,NextCellCandidate.get(EndOfCell[i]),RouteSegments[j],
        								Constants.transferTime[preCell][NextCell.get(NextCellCandidate.get(i))]+TransferTime[i]
    					              	);        	
						}
    					
        				// r1*cost_i + cost_j - costij 等价于 (costj - Cost_ij) + (r1-1)*costi
        				double cost = (r1-1)*Cost_i + Cost_j - Cost_ij; 
        			
        				/** 算最大值max的作为输出
	        			if(costMax < cost ) {
	            			costMax  = cost;
	            			CurStart = i;
	            			CurEnd   = j;
	            			
	            			BestCellBatch[CurStart]=new Vector<String>();
	            			for(int index = 0; index < EachCellBatch[i].size(); index++) {
	            				BestCellBatch[CurStart].add(EachCellBatch[i].get(index));
	            			}
	            			BestCellBatch[CurEnd]=new Vector<String>();
	            			for(int index = 0; index < EachCellBatch[j].size(); index++) {
	            				BestCellBatch[CurEnd].add(EachCellBatch[j].get(index));
	            			}
	            		}
        				*/
        				
        				/** 算最大值min的作为输出 **/
	        			if(costMin > cost ) {
	            			costMin   = cost;
	            			CurStart = i;
	            			CurEnd   = j;
	            			BestCellBatch[CurStart]=new Vector<String>();
	            			for(int index = 0; index < EachCellBatch[i].size(); index++) {
	            				BestCellBatch[CurStart].add(EachCellBatch[i].get(index));
	            			}
	            			BestCellBatch[CurEnd]=new Vector<String>();
	            			for(int index = 0; index < EachCellBatch[j].size(); index++) {
	            				BestCellBatch[CurEnd].add(EachCellBatch[j].get(index));
	            			}
	            		}
	        			
        			}// end finding the current good joint
        	
        	//Adding into CellRoutes
        	if(!GetCandidate[CurStart]) { //forming first time for CurStart cell
        		RouteSegments[CurStart].append(NextCell.get(NextCellCandidate.get(CurStart))).append(":");
	        	for(int i = 0; i < BestCellBatch[CurStart].size(); i++) {
	        		if(i != 0) {
	        			RouteSegments[CurStart].append(",");
	        		}
	        		RouteSegments[CurStart].append(BestCellBatch[CurStart].get(i));
	        	}
	        	RouteSegments[CurStart].append(";");
	        	GetCandidate[CurStart] = true;
        	}
            if(!GetCandidate[CurEnd]) { //forming first time for CurEnd cell
        		RouteSegments[CurEnd].append(NextCell.get(NextCellCandidate.get(CurEnd))).append(":");
	        	for(int i = 0; i < BestCellBatch[CurEnd].size(); i++) {
	        		if(i != 0){
	        			RouteSegments[CurEnd].append(",");
	        		}
	        		RouteSegments[CurEnd].append(BestCellBatch[CurEnd].get(i));
	        	}
	        	RouteSegments[CurEnd].append(";");
	        	GetCandidate[CurEnd] = true;
        	} 
//          System.out.println(RouteSegments[CurStart]+"连接上"+RouteSegments[CurEnd]);
            RouteSegments[CurStart].append(RouteSegments[CurEnd]);
            Routes = RouteSegments[CurStart];
            
            //调整CurStart 和 CurEnd的单元顺序 —— 在编码序列上
            AdjustPos(chromosome,this.id,CurStart,CurEnd);
            //update remain sum and remove the jobs in the JobBuffer
        	RemainSum -= BestCellBatch[CurStart].size();
        	RemainSum -= EachCellBatch[CurEnd].size();
            //Adding transferTime to route
        	TransferTime[CurStart]+=Constants.transferTime[NextCell.get(NextCellCandidate.get(CurStart))][NextCell.get(NextCellCandidate.get(CurEnd))];
        	EndOfCell[CurStart] = EndOfCell[CurEnd];    
        	//remove the CurEnd
        	RemovePoint[CurEnd]=true;
         }
		 //Delete the ops from the buffer
		 DeleteOpsByRoutes(Routes);
//    	 */
        return Routes;
	}

	/**
	 * Find a trans route(CurRoute) with cost in capacity(remianSum) of cellSet(remainCell) 
	 * @param remainSum
	 * @param remainCell
	 * @param CurRoute
	 * @param cost
	 * @param preCell
	 * @param curTransTime
	 */
	private void FindRoute( int remainSum, Vector<Integer> remainCell, StringBuffer CurRoute, double cost, int preCell, int curTransTime) {
		// return的两个条件判断  （1） remainSum ==0  （2） remainCell 为空的时候
		if( remainSum ==0 || remainCell.size()==0) {
			//这么看来，是不是要加一个全局变量，就是总的整个的cost值情况，来选择哪个路线最优
			if(cost > RouteCost) { 
				Route = CurRoute;
				RouteCost = cost;
			}
			return;
		}
		
		// 从这中间选每个单元的批次，进行合并，看效果 （也就相当于cost在其作用）
		for (int cellindex = 0; cellindex < remainCell.size(); cellindex++ ) {
			int curCellIdInbuffer = remainCell.get(cellindex);
			int curCell = NextCell.get(remainCell.get(cellindex));
			List<Operation> Ops = JobBuffer.get(curCellIdInbuffer).getOperations();

			//给Ops排序
			Map<Integer, Double> Ops_Cost = new HashMap<Integer,Double>();	//单元内存在多个ops的cost
			for(int opIndex = 0; opIndex < Ops.size(); opIndex++) {
				double Opcost = CalCost(curTransTime,Ops.get(opIndex),preCell,curCell);	//计算当前工序的cost的值
				Ops_Cost.put(opIndex, Opcost);
			}
			//sort according the cost 
			Ops_Cost = MapUtil.sortByValue(Ops_Cost);
			
			//select min(remainSum,Ops_Cost.size()) amount of parts to form the batch && calculate the AllCost 			
			int min = Math.min(remainSum, Ops.size());
			for (int i = 1; i<= min; i++) {
				//concat the batch and form the new route
		    	StringBuffer NewRoute = new StringBuffer(CurRoute).append(curCell+":");
		    	double CostCount = 0;
		    	int count = 0;
				for(Integer ops_id : Ops_Cost.keySet()){
					if(count != 0) {
						NewRoute.append(",");
					}
					NewRoute.append(Ops.get(ops_id).toString());
					CostCount += Ops_Cost.get(ops_id);
					count++;
					if(count == i) {
						break;
					}
				}
				NewRoute.append(";");
				//get new remain cell set
		    	Vector<Integer> NewRemainCell = new Vector<Integer>(remainCell);
		    	NewRemainCell.remove(new Integer(curCellIdInbuffer));
		    	//给出i个个体的批次的String,计算一下cost
		    	FindRoute(remainSum - i, NewRemainCell, new StringBuffer(NewRoute), 
		    			  cost+CostCount, curCell, curTransTime+Constants.transferTime[preCell][curCell]
                         );
		    }
		}
		return;
	}
	
	/**
	 * Adjust the position of curStart && curEnd in the chromosome of cell's transportation
	 * @param chromosome 
	 * @param curStart
	 * @param curEnd
	 * @param curEnd2 
	 */
	private void AdjustPos(Chromosome chromosome, int curCellId, int StartPos, int EndPos){
		// 修改cell上面的 Pos
		int startCell = PriorSequence[StartPos];
		int endCell   = PriorSequence[EndPos];
		//修改染色体上面的 Pos
		chromosome.setVehicleGene(curCellId, StartPos, endCell);
		chromosome.setVehicleGene(curCellId, EndPos, startCell);
	}

	/**
	 * find the pos of cellId in the PriorSequence
	 * @param curStart
	 * @return
	 */
	private int FindPriorPlace(int cellId){
		for(int i=0; i < PriorSequence.length; i++) {
			if(PriorSequence[i]==cellId) {
				return i;
			}
		}
		System.out.println("do not find the cellId in the priorSequnce in cell"+this.id);
		return -1;
	}

	private void DeleteOpsByRoutes(StringBuffer routes) {
		String[] seq  = routes.toString().split(";");
		for(String cur:seq){
			int m = cur.indexOf(":");
			if(cur.substring(m).length()<3) continue;
			//工件号
			int l = cur.indexOf(":");
			String t = cur.substring(0, l);
			int NextCellID = Integer.parseInt(t);
   			int NextBufferID = findInBuffer(NextCellID);
			//从信息中分解出所有的加工工序
   			int i = cur.indexOf(":");
   			String[] Ops = cur.substring(i+1).split(",");       					//.split 起到分割每个工序信息的作用 
   			if(Ops.length != 0){
				//find the Operation according the name
   				for(String op : Ops){
   					Operation CurOp =null;
   					for(int buffer_ops_id = 0; buffer_ops_id < JobBuffer.get(NextBufferID).size(); buffer_ops_id++){
   						//remove from the buffer
   						if(op.equals(JobBuffer.get(NextBufferID).get(buffer_ops_id).toString())) {
   							CurOp = JobBuffer.get(NextBufferID).get(buffer_ops_id);
   							JobBuffer.get(NextBufferID).removeOperation(CurOp);		
   							break;
   						}
   					}
   				}
   			}
		} //end for each cell
	}

	private int findInBuffer(int nextCellID) {
		for (int i = 0; i < NextCell.size(); i++) {
			if(NextCell.get(i) == nextCellID){
				return i;
			}
		}
		return -1;
	}

	/**
	 * judge whether the cells in candidate are all flaged
	 * @param getCandidate
	 * @return
	 */
	private boolean NotAllFlag(boolean[] getCandidate) {
		for (boolean b : getCandidate) {
			if (!b) {
				return true;
			}
		}
		return false;
	}


	/**
	 * modify the priors for cells in chromos
	 * @param chromosome
	 * @param cellId
	 * @param routeCellPriors
	 */
	private void ModifyCellPriors(Chromosome chromosome, int cellId, Vector<Integer> routeCellPriors) {
		int PriorIndex = 0;
		//find the cellId in the vector,modify to the cell in the right position
		for(int i = 0; i < chromosome.getVehicleSeq(cellId).length; i++) {
			for(int j =0; j < routeCellPriors.size(); j++) {
				if(chromosome.getVehicleSegment(cellId,i)==routeCellPriors.get(j))	{
					chromosome.setVehicleGene(cellId,i,routeCellPriors.get(PriorIndex));
					PriorIndex++;
					break;
				}
			}
		}
	}

	/**
	 * delete chosen parts（0~ops_id） from the jobBuffer of Cell cell_id
	 * and set the next machine for the part
	 * @param batch_ops_name
	 * @param cell_id
	 * @param ops_id
	 */
	private void DeleteOps(Vector<String> batch_ops_name) {
	   for(int ops_id = 0; ops_id < batch_ops_name.size(); ops_id++){
		    String cur = batch_ops_name.get(ops_id);
		   	for(int cell_id = 0 ; cell_id< JobBuffer.size(); cell_id++) { 		//遍历每个buffer
				//remove from the buffer 
		   		for(int buffer_ops_id = 0; buffer_ops_id < JobBuffer.get(cell_id).size(); buffer_ops_id++){
					 if(cur.equals(JobBuffer.get(cell_id).get(buffer_ops_id).toString())) {
						 Operation Buffer_ops = JobBuffer.get(cell_id).get(buffer_ops_id);
						 JobBuffer.get(cell_id).removeOperation(Buffer_ops);
						 break;
					 }
				 } //end for ops
			} //end for buffers
		} //end for batchNum
	}
	

	/**
	 * recalculate the sum of cost for the joint cells
	 * @return
	 */
	private double ReCalculCost(int indexInCandidate, int preCell, 
				StringBuffer routeSegments, double allTransferTime) {
		//eachCellBatch[indexInCandidate] 已经存在了，直接重新计算即可
		double Amount = 0;
		//decode the routeSegments
		String route = routeSegments.toString();
		String[] seq  = route.split(";");
		int CurID = preCell;
		int NextCellID;
		double ArrivalTime = 0;
		
		for(String cur : seq){
			int m = cur.indexOf(":");
			if(cur.substring(m).length()<3) continue;
           
			//工件号
			int l = cur.indexOf(":");
			String t = cur.substring(0, l);
			NextCellID = findInBuffer(Integer.parseInt(t));
			ArrivalTime += Constants.transferTime[CurID][NextCellID];		//小车到达目标地点的时间节点

   			//从信息中分解出所有的加工工序
   			int i = cur.indexOf(":");
   			String[] Ops = cur.substring(i+1).split(",");       //.split 起到分割每个工序信息的作用 
   			if(Ops.length != 0){
   				for(String op : Ops){
   					Operation CurOp = null;
   					//find the Operation according the name
   					for(int buffer_ops_id = 0; buffer_ops_id < JobBuffer.get(NextCellID).size(); buffer_ops_id++){
   						if(op.equals(JobBuffer.get(NextCellID).get(buffer_ops_id).toString())) {
						 CurOp = JobBuffer.get(NextCellID).get(buffer_ops_id);
						 break;
   						}
   					}
   					Amount+= CalCost(allTransferTime,CurOp,preCell,NextCell.get(NextCellID));	//计算当前工序的cost的值
   				}
   				CurID = NextCellID;	//修改下一次的CurID信息
   			}
		} //end for each cell
		return Amount;
	}
		
	/**
	 * 计算routes后加cur节点的cost耗费
	 * 其实不同的起点，起作用的也就是TranferTime加上以后，跟 下游机器什么时候解放的时间比较上 
	 * 并且在其中根据cost计算的结果，修改序列的结果
	 * @param allTransferTime
	 * @param remainSum 
	 * @param chromosome 
	 * @param cur
	 * @param cur2 
	 * @return
	 */
	private double ReduceCost(int indexInCandidate, int nextCell, int preCell, Vector<String>[] eachCellBatch, double allTransferTime, int remainSum, Chromosome chromosome) {
		Map<Integer, Double> Ops_Cost = new HashMap<Integer,Double>();	//单元内存在多个ops的cost
		eachCellBatch[indexInCandidate] = new Vector<String>();			//初始化批次
		Buffer CurBatch = JobBuffer.get(nextCell);
		
		for( int ops = 0; ops < CurBatch.getOperations().size(); ops++) {
			Operation CurOps = CurBatch.get(ops);
			double cost = CalCost(allTransferTime,CurOps,preCell,NextCell.get(nextCell));	//计算当前工序的cost的值
			Ops_Cost.put(ops, cost);
			//计算完所有operation的，看它们的顺序是否符合sequence的顺序。
			if(ops != 0) {
				JudgeConsistence(CurOps.getJob().getId(),cost,
						 		 CurBatch.get(ops-1).getJob().getId(),Ops_Cost.get(ops-1),
						 		 NextCell.get(nextCell),chromosome); 
			}
		} 
		//sort according the cost 
		Ops_Cost = MapUtil.sortByValue(Ops_Cost);
		//select min(remainSum,Ops_Cost.size()) amount of parts to form the batch && calculate the AllCost 
		double CostCount  = 0;		
		for(Integer ops_id : Ops_Cost.keySet()){
			eachCellBatch[indexInCandidate].add(CurBatch.get(ops_id).toString());
			CostCount+=Ops_Cost.get(ops_id);
			if(eachCellBatch[indexInCandidate].size() >= remainSum) break;
		}
		return CostCount;
	}

	/**
	 * Judge whether to change the position of the two job in the priors
	 * @param curJobId
	 * @param curCost
	 * @param preJobId
	 * @param preCost
	 * @param cellBufferIndex
	 * @param chromosome 
	 */
	private void JudgeConsistence(int curJobId, double curCost, int preJobId, double preCost, int cellBufferIndex, Chromosome chromosome) {
		//find the position
		int curPos,prePos;
		for(curPos = 0; curPos < IntercellPartSequences[cellBufferIndex].size(); curPos++) {
			if(IntercellPartSequences[cellBufferIndex].get(curPos) == curJobId) {
				break;
			}
		}
		for(prePos = 0; prePos < IntercellPartSequences[cellBufferIndex].size(); prePos++) {
			if(IntercellPartSequences[cellBufferIndex].get(prePos) == preJobId) {
				break;
			}
		}
		// need to change the position
		if( (curCost > preCost && curPos < prePos) || (curCost < preCost && curPos > prePos)) {
//			System.out.println(curPos+":"+curJobId+"和"+prePos+":"+preJobId+"互换位置");
			// 修改cell上面的Pos
			IntercellPartSequences[cellBufferIndex].set(prePos, curJobId);
			IntercellPartSequences[cellBufferIndex].set(curPos, preJobId);
			//修改染色体上面的Pos
			chromosome.setPartSequenceinPos(this.id, cellBufferIndex, prePos,curJobId);
			chromosome.setPartSequenceinPos(this.id, cellBufferIndex, curPos,preJobId);
		}
	}

	/**
	 * 计算当前的operation的cost
	 * @param end 
	 * @param allTransferTime 
	 * @param curOperation
	 * @param curCell
	 * @param nextCell2
	 * @return
	 */
	private double CalCost(double allTransferTime, Operation e, int begin, int end) {
    	int MachineIndex=0;
    	while(e.getProcessMachineList().get(MachineIndex).getCellID()!=end){
    		MachineIndex++;
    	}
		Machine m = e.getProcessMachineList().get(MachineIndex);
		double result = Math.max(m.getNextIdleTime(),Timer.currentTime()+allTransferTime+Constants.transferTime[begin][end]) - e.getArrivalTime();
		return result;
	}

	/**
	 * @Description Find the current operation of part CurPartId in Buffer cur
	 * @param cur
	 * @param CurPartId
	 * @return
	 */
	private String FindOperationInBuffer(Buffer cur, int CurPartId) {
		for(Operation op : cur) {
			//delete from the jobBuffer
			if(op.getJob().getId() == CurPartId) {
				DeleteOperationFromBuffer(op);
				return op.toString()+",";
			}
		}
		//else, this part is not in buffer,then return nothing
		return "";
	}

	/**
	 * @Description delete the op from the job buffer
	 * @param op
	 */
	private void DeleteOperationFromBuffer(Operation op) {
		for(Buffer CurrentBuffer : JobBuffer) {
			CurrentBuffer.removeOperation(op);
		}
	}

	/**
	 * @Description get the correseponding buffer for destination cell in JobBuffer by CellNo
	 * @param CellNo
	 * @return
	 */
	private int GetBufferIndexInNextCellByCellNo(int CellNo) {
		if(NextCell.indexOf(CellNo) == -1) {
			throw new IllegalArgumentException("Wrong CellNo for JobBuffer");
		}
		return NextCell.indexOf(CellNo);
	}

	/**
	 * @Description 输出目标单元信息--用于测试
	 */
	public void printNextCell() {
		System.out.println(NextCell.get(0)+" "+NextCell.get(1)+" "+NextCell.get(2));
	}

	/**
	 * @Description 输出目标单元集合的大小
	 * @return
	 */
	public int getNextCellSize() {
		return NextCell.size();
	}
}
