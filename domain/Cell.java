package com.lm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import com.lm.algorithms.rule.transportor.*;
import com.lm.domain.Vehicle;
import com.lm.util.Constants;

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
		// TODO Auto-generated method stub
	    
		// higher priority is preferred
        int NextCellCount=0;//下游单元的下标
        int curCell=id;//本单元号
        
        int nextCell;//下游单元号
		int BestNextCell=0;//本次运输选中的下一个单元号

        Buffer SelectBuffer=null;
        double BestSum=Double.MAX_VALUE;
        double BestBenchmark=0;
        
        //当前cell信息
//      Utils.echo("当前小车是单元"+name);
        for (Buffer CurBatch : JobBuffer) {//对每个Buffer中的工件都要用CalPrio，进行一个排序
//        	Utils.echo("Buffer内容如下");
//        	for(int i=0;i<CurBatch.getOperations().size();i++){
//        		  Utils.echo(CurBatch.getOperations().get(i).toString());
//        	}
        	
        	/**根据当前Buffer得到下一个去往的单元号**/
        	nextCell=NextCell.get(NextCellCount++);
            double[] Scores=new double[CurBatch.size()];
            int OperaCount=0;
            
        	for(Operation CurOperation:CurBatch.getOperations()){//选出合适的Operation
        		//为了适应GP，calPrio的值改成越大越好了，即取了个反
	            double score = TransRule.calPrio(CurOperation,curCell,nextCell);
         
	            
	            if(score< -10000000) score=-10000000;
	            else if(score > 10000000) score=10000000;
	            
        		if(CurBatch.get(OperaCount).getScore()<score){ 
        			CurBatch.get(OperaCount).setScore(score);
        		}
        		Scores[OperaCount++]=0-score;
	            /**找到对应工件在Constants.cellToPartsFunc中的位置，然后更新Func的值**/
        		 for(int i = 0; i < Constants.CellToParts[curCell][nextCell].size(); i++){
              		if(Constants.CellToParts[curCell][nextCell].get(i) == CurOperation.getJob().getId()){
              			//是否更新过值，若没有，附上新值
              			if(cellToPartsFunc[curCell][nextCell][i] == Double.MAX_VALUE) {
              			   cellToPartsFunc[curCell][nextCell][i] = -score;
              			}
              			//若更新过，与新值的结果取平均
              			else{
              				cellToPartsFunc[curCell][nextCell][i] = ( -score + cellToPartsFunc[curCell][nextCell][i])/2;
              			}
              		}
        		 }
//        		System.out.println("score结果是"+score);
            }
        	/**对scores进行排序,注意arrays.sort是 从小到大排序,所以上面取反来存**/
        	Arrays.sort(Scores);
        	/**找到这次组成批的几个工序的score的和,并找到最合适的批**/
        	int i=0;
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
//        Utils.echo("最后选出的Buffer内容如下");
//    	for(int i=0;i<SelectBuffer.getOperations().size();i++){
//    		  Utils.echo(SelectBuffer.getOperations().get(i).toString());
//    	}
        
       /**根据选出的SelectBuffer，找到 capacity个operation，组成小车要运输的批次 **/
        List<Operation> resultBatch=new ArrayList<Operation>();;
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
