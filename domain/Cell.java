package com.lm.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import com.lm.algorithms.rule.transportor.*;
import com.lm.domain.Vehicle;
import com.lm.util.Constants;
import com.lm.util.MapUtil;

/**
 * @Description 逻辑单元存储的类

 * @author:lm

 * @time:2013-11-6 下午09:54:49

 */
/**
 * @Description TODO

 * @author:lm

 * @time:2013-11-6 下午10:00:02

 */
public class Cell {
/***************************属性域***********************************************************************/
	/**单元ID**/
	private int id;
	/**单元名称 */
	private String name;
	/**小车对象*/
	private Vehicle transCar;
	/**当前采用的规则*/
	private ITransportorRule TransRule;
	
	/**
	 * JobBuffer[1] 就表示跨到NextCell[1]单元 的缓存区
	 */
	
	/**下游能到的单元集合 **/
	public  List<Integer> NextCell;
	/**去各单元的工件缓存区 **/
	private List<Buffer>  JobBuffer;
	/**当前工件在本单元的下一次加工机器的ID **/
	private int CurJobMachineID;

/***************************方法域***********************************************************************/
	/**
	 * @Description construction of Cell
	 * 
	 * @param Id
	 * @param cellNum
	 * @param transSize
	 */
	public Cell(int Id, int cellNum, int transSize) {
		this.id = Id;
		this.name = "Cell" + Id;
		this.transCar = new Vehicle(transSize);
		this.NextCell = new ArrayList<Integer>();
		/**Cell初始化的时候需不需要修改**/
		this.TransRule= new TransOperAndTrans();
	}

	/**
	 * @Description 获取源机器到目标机器的转移时间
	 * @param srcNumInCell
	 *            源机器所属单元号
	 * @param destNumInCell
	 *            目标机器所属单元号
	 * @return 转移时间
	 * */
	public int getTransferTime(int srcNumInCell, int destNumInCell) {
		if (Constants.transferTime == null) {
			throw new NullPointerException(
					"transferTime should be initialized first!");
		}
		if (srcNumInCell == destNumInCell)
			return 0;
		return Constants.transferTime[srcNumInCell][destNumInCell];
	}

	/**
	 * @Description 初始化JobBuffer长度
	 * 
	 * @param sum--下游单元总数：表示要建的缓冲区长度
	 */
	public void initJobBuffer(int sum) {
		this.JobBuffer = new ArrayList<Buffer>(sum);
		for(int i=0;i<sum;i++){
			Buffer e=new Buffer();
			JobBuffer.add(e);
		}
	}

	/**
	 * @Description 设置下游单元集合
	 * 
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
	 * @Description 判断当前CELL是否完成组批--未完成
	 * 
	 * @return
	 */
	public boolean isTransComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	/** @Description 重置作业状态 */
	public void reset() {
		//NextCell.clear(); 重置不能改变NextCell
		for(int i=0;i<JobBuffer.size();i++){
			JobBuffer.get(i).operationClear();
		}
		/**重置还要改变小车状态，不要忘记了 **/
		if(transCar.getIdle()==false)  transCar.changeIdle();
	}
	
	/**
	 * @Description 获取小车
	 */
	public Vehicle getVehicle(){
		return this.transCar;
	}
	
	/**
	 * @Description 获取ID
	 */
	public int getID(){
		return this.id;
	}
	
	/**
	 * @Description 获取NAME
	 */
	public String getname(){
		return this.name;
	}
	
	/**
	 * @Description 获取CurJobMachineID的值
	 */
	public int getCurJobMachineID(){
		return this.CurJobMachineID;
	}
	
	/**
	 * @Description 获取BUFFER总长度
	 */
	public int getBufferSize(){
		int sum=0;
		for(int i=0;i<this.JobBuffer.size();i++){
			sum+=this.JobBuffer.get(i).size();
		}
		return sum;
	}
	
	/**
	 * @Description 获取对应队列上面的Buffer
	 */
	public Buffer getBuffer(int i){
		return this.JobBuffer.get(i);
	}
	
	/**
	 * @Description 将当前完成某道工序、并且准备跨单元的工件的工序加入到缓冲队列当中
	 * @param currentOperation
	 */
	public void addTransBatch(Operation currentOperation) {
		// TODO Auto-generated method stub
		List<Machine> NextMachines=currentOperation.getProcessMachineList();
		for(int i=0;i<NextMachines.size();i++){
			int c=NextCell.indexOf(NextMachines.get(i).getCellID());//得到当前buffer号
			//currentOperation.GetNextMachineID()=
			JobBuffer.get(c).addOperation(currentOperation);
		}
	}

    /**
     * @Description 通过决策，判断下一道工序可以在哪些单元加工
     * @param currentOperation
     * @return 
     * 0--下一道工序只能在本单元内加工
     * 1--下一道工序既能在本单元，又能在其他单元加工，则工件需要决策
     * 2--下一道工序只能在其他单元加工
     */
	public int CanGoWhichCell(Operation currentOperation) {
		// TODO Auto-generated method stub
		/**
		如果下一道工序本单元内不存在可以加工的机器，那肯定可以直接跨出；
		否则，存在这个本单元、外单元的冲突，通过规则来判断是否值得跨出
		**/
		List<Machine> NextMachines=currentOperation.getProcessMachineList();
		for(int i=0;i<NextMachines.size();i++){
			int c=NextMachines.get(i).getCellID();//得到当前单元号
			if(c==this.id){
				this.CurJobMachineID=NextMachines.get(i).getId();
				if(NextMachines.size()==1) return 0;
				else return 1;
			}
		}
		return 2;
	}
	
	/**
	 * @param cellToPartsFunc 
	 * @param cellToNextCellsFunc 
	 * @Description 选择要运输的一批工件
	 * @return
	 */
	public List<Operation> SelectTransBatch(double[][][] cellToPartsFunc) {
	    
        Buffer SelectBuffer=null;	//本次要挑选的批次
        double BestBenchmark=0;		//最好的benchmark值记录

        int NextCellCount=0;//下游单元的下标
        int curCell=id;//本单元号
        
        int nextCell;//下游单元号
		int BestNextCell=0;//本次运输选中的下一个单元号
        
        for (Buffer CurBatch : JobBuffer) {//对每个Buffer中的工件都要用CalPrio，进行一个排序
            double[] Scores=new double[CurBatch.size()];
            int OperaCount=0;
        	nextCell=NextCell.get(NextCellCount++); //根据当前Buffer得到下一个去往的单元号
        	
        	for(Operation CurOperation:CurBatch.getOperations()){//选出合适的Operation
	            double score = TransRule.calPrio(CurOperation,curCell,nextCell);//为适应GP，calPrio的值改成越大越好了，即取反
	            if(score< -10000000) score=-10000000;
	            else if(score > 10000000) score=10000000;
	            
        		if(CurBatch.get(OperaCount).getScore()<score){ 
        			CurBatch.get(OperaCount).setScore(score);
        		}
        		Scores[OperaCount++]=0-score;
        		
        		 for(int i = 0; i < Constants.CellToParts[curCell][nextCell].size(); i++){	//找到对应工件位置，更新Func
              		if(Constants.CellToParts[curCell][nextCell].get(i) == CurOperation.getJob().getId()){
              			if(cellToPartsFunc[curCell][nextCell][i] == Double.MAX_VALUE) {	//第一次赋值
              			   cellToPartsFunc[curCell][nextCell][i] = -score;
              			}else{	//多次赋值，取平均
              				cellToPartsFunc[curCell][nextCell][i] = ( -score + cellToPartsFunc[curCell][nextCell][i])/2;
              			}
              		}
        		 }// end of 更新值
            }
        	
        	Arrays.sort(Scores);	//对scores进行排序,注意arrays.sort是 从小到大排序,所以上面取反来存

        	/**找到这次组成批的几个工序的score的和,并找到最合适的批**/
        	int i=0;
            double BestSum=Double.MAX_VALUE;
        	double sum=0;
        	double CurBenchmark = 0;
            boolean Flag=false;
            
        	while(i<transCar.getCapacity() && i< OperaCount){
        		Flag=true;
        		CurBenchmark=0-Scores[i++];
        		sum-=CurBenchmark;
        	}
        	if(Flag!=false && sum<BestSum){
        		BestSum=sum;
        		BestBenchmark=CurBenchmark;
//        		 Utils.echo("有选择哦~~");
        		SelectBuffer=CurBatch;
        		BestNextCell=nextCell;
        	}
       }
        
       /**根据选出的SelectBuffer，找到 capacity个operation，组成小车要运输的批次 **/
        List<Operation> resultBatch=new ArrayList<Operation>();
		for (int i = 0; i < SelectBuffer.size(); i++) {
			if(SelectBuffer.get(i).getScore()>=BestBenchmark){
				resultBatch.add(SelectBuffer.get(i));
				if(resultBatch.size()==transCar.getCapacity()) break;
			}
		}
        /**将resultBatch从JobBuffer中删去**/
		for (Operation operation : resultBatch) {
			for(Buffer CurrentBuffer : JobBuffer){
				CurrentBuffer.removeOperation(operation);
			}
		}
        /**记住将选择出来的下一个单元id，更新到Vehicle上面**/
        for (int i = 0; i < resultBatch.size(); i++) {
        	//根据下一个单元，确定一个工件下一个要加工的机器
        	List<Machine>  MachineList=resultBatch.get(i).getProcessMachineList();
        	for (int  j = 0; j < MachineList.size(); j++) {
        		if(Constants.CellForm.get( MachineList.get(j).getId() )==BestNextCell){//找到对应单元上加工的机器
        			resultBatch.get(i).SetNextMachineID(MachineList.get(j).getId());
        			break;
        		}
			}
	    }
		return resultBatch;
	}

	/**
	 * @param cellToPartsFunc for multiple destination cells
	 * @return 路径表示的字符串
	 * 示例  
	 * 1: 2-3,4-2; 2:3-1;
	 * (表示先把工序2-3，4-2运到1单元，再把工序3-1运到2单元)
	 */
	public StringBuffer SelectTransBatchForMultiple(double[][][] cellToPartsFunc) {

		int curCell=id;			//本单元号
        int Cell_ID=0;			//下游单元的下标
        int nextCell;			//下游单元号
        double[] ScoresForCells = new double[JobBuffer.size()];					//每个单元的评分
        Map<Integer, Double> Map_Cells_Func = new HashMap<Integer, Double>();	//每个单元cell对应它评分的map
        Map<Integer, Double> []Map_Ops_Func = new HashMap[JobBuffer.size()];	//单元内，operation对应它function值的map
//        Map<Double, Integer> Map_Cells_Func = new TreeMap<Double, Integer>();	
//        Map<Double, Integer> []Map_Ops_Func = new TreeMap[JobBuffer.size()];	
        
        for (Buffer CurBatch : JobBuffer) {										//对每个Buffer中的工件都CalPrio，进行排序
 
        	nextCell=NextCell.get(Cell_ID); 									//得到下一个单元的编号
        	Map_Ops_Func[Cell_ID]=new HashMap<Integer,Double>();				//TreeMap默认对key升序排序,所以是按Func有序的
        	ScoresForCells[Cell_ID]=0;
        	
        	int OperaCount=0;													//count of operations
        	for(Operation CurOperation:CurBatch.getOperations()){				//选出合适的Operation
	            double score = TransRule.calPrio(CurOperation,curCell,nextCell);//为适应GP，calPrio的值改成越大越好了，即取反
	            
	            if(score< -10000000) score=-10000000;
	            else if(score > 10000000) score=10000000;
        		if(CurBatch.get(OperaCount).getScore()<score){ 
        			CurBatch.get(OperaCount).setScore(score);
        		}
        		
        		ScoresForCells[Cell_ID]+=0-score;
        		Map_Ops_Func[Cell_ID].put(OperaCount++,0-score);
        		
        		for(int i = 0; i < Constants.CellToParts[curCell][nextCell].size(); i++){	//找到对应工件位置，更新Func
              		if(Constants.CellToParts[curCell][nextCell].get(i) == CurOperation.getJob().getId()){
              			if(cellToPartsFunc[curCell][nextCell][i] == Double.MAX_VALUE) {	//第一次赋值
              			   cellToPartsFunc[curCell][nextCell][i] = -score;
              			}else{	//多次赋值，取平均
              				cellToPartsFunc[curCell][nextCell][i] = ( -score + cellToPartsFunc[curCell][nextCell][i])/2;
              			}
              			 break;
              		}
        		}// end of 更新值
        	}//end for Operation
        	Map_Ops_Func[Cell_ID] = MapUtil.sortByValue(Map_Ops_Func[Cell_ID]);
        	
        	if(OperaCount!=0){
        		ScoresForCells[Cell_ID]/=OperaCount;
                Map_Cells_Func.put(Cell_ID,ScoresForCells[Cell_ID]);
        	}
        	
        	Cell_ID++;
        }//end of JobBuffer
        Map_Cells_Func = MapUtil.sortByValue(Map_Cells_Func);
        
        StringBuffer Routes=new StringBuffer("");				//运输路线的结果
        int    BatchSum = 0;									//已经加入的工件数目
        Vector<String> Batch_cell_name = new Vector<String>();	//批次中工序名字
        
        //按顺序选择批次当中的工件，再挑单元内工件）
        for(int cell_id = 0 ; cell_id< JobBuffer.size(); cell_id++) {        		//先挑单元
            
        	if(Map_Cells_Func.containsKey(cell_id)){								//找到有工件buffer的下游单元
	        	 
        		Routes.append(NextCell.get(cell_id)).append(":");

        		Boolean First_flag =true;
        		
	        	for(int ops_id = 0; ops_id <  Map_Ops_Func[cell_id].size(); ops_id++) {  	//在单元内，再挑工件
	        		  
	        		 String Cur_ops = JobBuffer.get(cell_id).get(ops_id).toString();		//当前工序名称
	        		 
	        		 if( CheckNotExist( Batch_cell_name , Cur_ops ) )  {  			
		        		 
	        			 Batch_cell_name.add(Cur_ops);
	        			 
	        			 if(First_flag){
	        				 First_flag=false;
	        			 }else{
	        				 Routes.append(",");
	        			 }
		        		 Routes.append( Cur_ops);
		        		 
		        		 BatchSum++;
		            	 if(BatchSum >= transCar.getCapacity())	{							//批次已满
		            		 
		            		 DeleteOps(Batch_cell_name);
		            		 
		            		 Routes.append(";");
		            		 return Routes;
		            	 }
	        		 }
	        	}//end for Ops
        		
	        	Routes.append(";");
        	}
        }// end for cell
        
        DeleteOps(Batch_cell_name);
        return Routes;
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
				 for(int buffer_ops_id = 0; buffer_ops_id < JobBuffer.get(cell_id).size(); buffer_ops_id++){
					 if(cur.equals(JobBuffer.get(cell_id).get(buffer_ops_id).toString())) {
						 
						 Operation Buffer_ops = JobBuffer.get(cell_id).get(buffer_ops_id);
						 
						 JobBuffer.get(cell_id).removeOperation(				//remove from the buffer
								 Buffer_ops);
						 break;
					 }
				 }//end for ops
			}//end for buffers
		}
	}

	/**
	 * 通过比对工序的名称，确保它是否已经被选在了批次里面
	 * @param batch_ops_name
	 * @param string
	 * @return
	 */
	private boolean CheckNotExist(Vector<String> batch_ops_name, String string) {
		
		for(int i = 0; i < batch_ops_name.size(); i++) {
			if ( batch_ops_name.get(i).equals(string) ) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @Description 输出目标单元信息--用于测试
	 */
	public void printNextCell() {
		// TODO Auto-generated method stub
		System.out.println(NextCell.get(0)+" "+NextCell.get(1)+" "+NextCell.get(2));
	}

	/**
	 * @Description 输出目标单元集合的大小
	 * @return
	 */
	public int getNextCellSize() {
		// TODO Auto-generated method stub
		return NextCell.size();
	}

	/**
	 * @Description 设置小车运输的rules
	 * @param iTransportorRule
	 */
	public void setRule(ITransportorRule iTransportorRule) {
		// TODO Auto-generated method stub
		TransRule=iTransportorRule;
	}


}
