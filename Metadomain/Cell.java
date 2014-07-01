package com.lm.Metadomain;

import java.util.ArrayList;
import java.util.List;

import com.lm.domain.Vehicle;
import com.lm.util.Constants;

/**
 * @Description Class Cell for meta-heuristic

 * @author:lm

 * @time:2014-4-16 上午10:51:24

 */
public class Cell {
/***************************属性域***********************************************************************/
	/**单元ID**/
	public int id;
	/**单元名称 */
	private String name;
	/**小车对象*/
	private Vehicle transCar;

	/**
	 * JobBuffer[1] 就表示跨到NextCell[1]单元 的缓存区
	 */
	
	/**下游能到的单元集合 **/
	public  List<Integer> NextCell;
	/**去各单元的工件缓存区 **/
	private List<Buffer>  JobBuffer;
	/**当前工件在本单元的下一次加工机器的ID **/
	private int CurJobMachineID;
	/**小车决策运输的优先级序列 即 vehicleSegment for cur cell**/
	private	int[] PriorSequence;
	/**	the sequence of parts for buffer of each cell**/
	private ArrayList<Integer>[] IntercellPartSequences;

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
	
	public int[] getPriorSequence() {
		return PriorSequence;
	}

	public void setPriorSequence(int[] priorSequence) {
		PriorSequence = priorSequence;
	}
	
	public ArrayList<Integer>[] getIntercellPartSequences() {
		return IntercellPartSequences;
	}

	public void setIntercellPartSequences(ArrayList<Integer>[] intercellPartSequences) {
		IntercellPartSequences = intercellPartSequences;
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
	 * @Description 选择要运输的工件批次 && 运输路径
	 * @return 路径表示的字符串
	 * 示例  
	 * 1: 2-3,4-2; 2:3-1;
	 * (表示先把工序2-3，4-2运到1单元，再把工序3-1运到2单元)
	 */
	public StringBuffer SelectTransBatch() {

        StringBuffer SelectRoutes=new StringBuffer("");
        int    BatchSum = 0;
        
    	/** get the batch using PriorSequences**/
        for (int CurDesCellNo:PriorSequence) {
        	
        	SelectRoutes.append(CurDesCellNo).append(":");

        	Buffer CurBuffer = JobBuffer.get(GetBufferIndexInNextCellByCellNo(CurDesCellNo));
        	
        	if(CurBuffer.size() == 0){
        		SelectRoutes.append(";");
        		continue;
        	}
        	
        	for(int curop: IntercellPartSequences[CurDesCellNo]){
        		//Find the Operation in CurBuffer
        		int b =SelectRoutes.length();
        		SelectRoutes.append(FindOperationInBuffer(CurBuffer,curop));
        		if(SelectRoutes.length()>b+3){
        			BatchSum++;
        		}
        	}
        	SelectRoutes.append(";");
        	if(BatchSum >= transCar.getCapacity())	break;
        }
		return SelectRoutes;
	}

	/**
	 * @Description Find the current operation of part CurPartId in Buffer cur
	 * @param cur
	 * @param CurPartId
	 * @return
	 */
	private String FindOperationInBuffer(Buffer cur, int CurPartId) {
		for(Operation op:cur){
			if(op.getJob().getId() ==CurPartId){
				//delete from the jobBuffer
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
		for(Buffer CurrentBuffer : JobBuffer){
			CurrentBuffer.removeOperation(op);
		}
	}

	/**
	 * @Description get the correseponding buffer for destination cell in JobBuffer by CellNo
	 * @param CellNo
	 * @return
	 */
	private int GetBufferIndexInNextCellByCellNo(int CellNo) {
		if(NextCell.indexOf(CellNo) == -1) throw new IllegalArgumentException("Wrong CellNo for JobBuffer");
		return NextCell.indexOf(CellNo);
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

}
