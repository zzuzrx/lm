package com.lm.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

//import com.lm.domain.Cell;                   //用于产生规则时
//import com.lm.domain.CellSet;
//import com.lm.domain.Job;
//import com.lm.domain.JobSet;
//import com.lm.domain.Machine;
//import com.lm.domain.MachineSet;
//import com.lm.domain.Operation;
import com.lm.Metadomain.Cell;			//用于元启发调度
import com.lm.Metadomain.CellSet;
import com.lm.Metadomain.Job;
import com.lm.Metadomain.JobSet;
import com.lm.Metadomain.Machine;
import com.lm.Metadomain.MachineSet;
import com.lm.Metadomain.Operation;
import com.lm.util.Constants;
import com.lm.util.ListHelper;

public class MetaCMSReader {
	private MachineSet machineSet;
	private JobSet jobSet;
	private CellSet cellSet;
	private ArrayList<Integer>[] machines;
	
	/**
	 * @Description constructor
	 * @param filename
	 * @exception:
	 */
	public MetaCMSReader(String filename) {
		File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader 		   			  = new BufferedReader(new FileReader(file));
			machineSet     			  = new MachineSet();
			jobSet 		   			  = new JobSet();
			cellSet 	   			  = new CellSet();
			
			
			
			// 系统一些主要参数
			String line;			
			line = reader.readLine();
			String[] seq = line.split("\\s++");
			int machNum = Integer.parseInt(seq[0]);
			int jobNum = Integer.parseInt(seq[1]);
			int cellNum = Integer.parseInt(seq[2]);
			int familyNum = Integer.parseInt(seq[3]);
			int transportorSize = Integer.parseInt(seq[4]);
			reader.readLine();// 空格行
			/** 输出test--success **/
			/**
			 * System.out.println(machNum
			 * +" "+jobNum+" "+cellNum+" "+familyNum+" "+transportorSize);
			 **/

			// 初始化单元信息&&机器对象
			Constants.MachineToParts  = new int[machNum+1][];
			Constants.CellToNextCells = new int[cellNum+1][];
			Constants.CellToParts	  = new ArrayList[cellNum+1][cellNum+1];
			for(int i = 1; i < cellNum+1; i++){
				for(int j = 1; j < cellNum+1; j++){
					Constants.CellToParts[i][j] = new ArrayList<Integer>();
				}
			}
			machines	   			  = new ArrayList[machNum+1];
			for(int i = 0; i <=machNum; i++){
				machines[i] = new ArrayList<Integer>();
			}
			Constants.CellForm = new HashMap<Integer, Integer>();
			line = reader.readLine();
			seq = line.split("\\s++");
			for (int machNo = 0; machNo < machNum; machNo++) {
				Machine m = new Machine(machNo + 1, "M" + (machNo + 1), 1);
				m.setCellID(Integer.parseInt(seq[machNo]));// 现在是机器set
				
				Constants.CellForm.put(machNo + 1, Integer.parseInt(seq[machNo]));
				machineSet.add(m);
			}
			reader.readLine();// 空格行
			/** 输出test--success **/
			/**
			 * for (int machNo = 0; machNo < machNum; machNo++) {
			 * System.out.println(machineSet.get(machNo).getNumInCell()); }
			 **/

			// 单元间转移时间矩阵
			Constants.transferTime = new int[cellNum + 1][cellNum + 1];
			for (int i = 1; i <= cellNum; i++) {
				line = reader.readLine();
				seq = line.split("\\s+");
				for (int j = 1; j <= cellNum; j++) {
					Constants.transferTime[i][j] = Integer.parseInt(seq[j - 1]);
				}
			}
			reader.readLine();// 空格行
			/** 输出test--success **/
			/**
			 * for (int i = 0; i < cellNum; i++) { for (int j = 0; j < cellNum;
			 * j++) { System.out.println(Constants.transferTime[i][j]); }
			 * System.out.println('\n'); }
			 **/

			// 单元间的转移关系
			for (int i = 0; i < cellNum; i++) {
				line = reader.readLine();
				seq = line.split("\\s+");
				Cell c=new Cell(i + 1, cellNum, transportorSize);
				c.setNextCell(seq);
				cellSet.addCell(c);
				
				AddToCellMessage(i,c);
			}
			reader.readLine();// 空格行

			// setupTime——跟工件族相关
			Constants.setupTime = new int[familyNum][machNum + 1];
			for (int i = 0; i < familyNum; i++) {
				line = reader.readLine();
				seq = line.split("\\s+");
				for (int j = 1; j <= machNum; j++) {
					Constants.setupTime[i][j] = Integer.parseInt(seq[j - 1]);
				}
			}
			reader.readLine();// 空格行
			/** 输出test--success **/
			/**
			 * for (int i = 0; i < cellNum; i++) { for (int j = 0; j < cellNum;
			 * j++) { System.out.println(Constants.setupTime[i][j]); } }
			 **/

			// 工件属性
			for (int jobNo = 0; jobNo < jobNum; jobNo++) {
				// 工件的总括属性
				line = reader.readLine();
				seq = line.split("\\s++");
				int operNum = Integer.parseInt(seq[0]); // 工序数目
				int family = Integer.parseInt(seq[1]); // 工件族号
				int DueDate = Integer.parseInt(seq[2]);// 工件的DUEDATE
				double weight = Double.parseDouble(seq[3]);// 工件权重
				Job job = new Job(jobNo + 1, "J" + (jobNo + 1));
				jobSet.addJob(job);
				job.setFamily(family);
				job.setDuedate(DueDate);
				// job.setUnitTransferTime(unitTransferTime);//单元转移时间，还有必要放在工件中么
				job.setWeight(weight); // 工件的权重
				/** 输出test--succeed **/
				/**
				 * for (int machNo = 0; machNo < machNum; machNo++) {
				 * System.out.println(job.getName()+" "+job.getFamily()+" "+job.getDuedate()
				 * 					  +" "+job.getWeight()); }
				 **/
				
				Vector<Integer> PreCells =new Vector<Integer>();
				Vector<Integer> CurCells =new Vector<Integer>();
				// 工序属性
				List<Operation> operations = new ArrayList<Operation>();
				for (int operNo = 0; operNo < operNum; operNo++) {
					line = reader.readLine();
					seq = line.split("\\s+");
					int machineIndex = 0;
					Map<Machine, Integer> procTimeMap = new HashMap<Machine, Integer>();
					
					for (int i = 0; i < seq.length; i++) {
						int processTime = Integer.parseInt(seq[i]);
						if (processTime == 0) {// 不能在该机器上加工
							machineIndex++;
							continue;
						}
						
						AddToMachineMessage(jobNo+1,machineSet.get(machineIndex).getId());
						CurCells.add(Constants.CellForm.get(machineIndex+1));
						if(operNo!=0){
							AddpartsRoutesMessage(jobNo+1,machineIndex+1,PreCells);
						}
						
						procTimeMap.put(machineSet.get(machineIndex),processTime);
						machineIndex++;
					}
					//对operation进行相关记录
					Operation operation = new Operation(operNo, (jobNo + 1) + "-" + (operNo + 1));
					operation.setJob(job);
					operation.setProcTimes(procTimeMap);
					Operation prev = ListHelper.getLast(operations);
					if (prev != null) {
						operation.setPrev(prev);
						prev.setNext(operation);
					}
					operations.add(operation);
					//对所在单元内容进行相关记录
					PreCells.clear();
					for(int cur:CurCells){
						PreCells.add(cur);
					}
					CurCells.clear();
					
				}// End for operations
				job.setOperations(operations);
				/** 输出test **/
				/**
				 * System.out.println(job.getName()); 
				 * for(int j=0;j<operations.size();j++) { Operation
				 * test=job.getOperation(j); List<Machine>
				 * Mtests=test.getProcessMachineList(); for(int
				 * i=0;i<Mtests.size();i++){
				 * System.out.println(j+"工序： "+test.getName
				 * ()+" "+Mtests.get(i).getName
				 * ()+" "+test.getProcessingTime(Mtests.get(i))); } }
				 **/
			}// End for jobs
		} catch (IOException e) {
			e.printStackTrace();
		}
		TransferMachineMessageArray();
	}

	/**
	 * @Description 添加每条路线上加工信息的信息
	 * @param jobNo
	 * @param machineNo
	 * @param preCells 
	 */
	private void AddpartsRoutesMessage(int jobNo, int machineNo, Vector<Integer> preCells) {
		int cur = Constants.CellForm.get(machineNo);
		
		for(int pre:preCells){
			if(cur!=pre){
				int i=0;
				for(;i <Constants.CellToParts[pre][cur].size();i++){
					if(Constants.CellToParts[pre][cur].get(i)==jobNo){ 
						break;
					}
				}
				if(i == Constants.CellToParts[pre][cur].size()){
					Constants.CellToParts[pre][cur].add(jobNo);
				}
			}
		}
	}

	/**
	 * @Description 添加机器可加工工件的信息
	 * @param jobNo
	 * @param MachineNo
	 */

		private void AddToMachineMessage(int jobNo, int MachineNo) {
		//transfer the machineid to the index in array
		int indexNo = MachineNo - 1; 
		//check if exists
		for(int i=0; i<machines[indexNo].size();i++){
			if(machines[indexNo].get(i) == jobNo) return;
		}
		//if not,add it
		machines[indexNo].add(jobNo);
	}


	/**
	 * @Description 添加单元可转移到的信息
	 * @param i
	 * @param c
	 */
	private void AddToCellMessage(int i,Cell c) {
		Constants.CellToNextCells[i+1] = new int[c.NextCell.size()+1];
		for(int j = 0 ; j < c.NextCell.size()+1; j++){
			if(j==0){
				Constants.CellToNextCells[i+1][j] =0;
			}
			else{
				Constants.CellToNextCells[i+1][j] = c.NextCell.get(j-1);
			}
		}
	}

	
	/**
	 * @Description Transfer the Vector message to Constants.MachineToParts
	 */
	public void TransferMachineMessageArray() {
		for(int MID = 1; MID < machines.length; MID++){
			Constants.MachineToParts[MID] = new int[machines[MID-1].size()+1];
			for(int curChar = 0; curChar < machines[MID-1].size()+1; curChar++){
//				Constants.MachineToParts[MID][curChar] = machines.get(MID-1).charAt(curChar)-'0';
				if(curChar==0){
					Constants.MachineToParts[MID][curChar] =0;
				}
				else{
					Constants.MachineToParts[MID][curChar] = machines[MID-1].get(curChar-1);
				}
			}
		}
	
	}
	
	/**
	 * @Description get machine set
	 * @return
	 */
	public MachineSet getMachineSet() {
		return machineSet;
	}

	/**
	 * @Description get job set
	 * @return
	 */
	public JobSet getJobSet() {
		return jobSet;
	}

	/**
	 * @Description get cell set
	 * @return
	 */
	public CellSet getCellSet() {
		return cellSet;
	}

	/**
	 * @Description print machine's information
	 */
	private void printMachineInfo() {
		for (Machine m : machineSet) {
			System.out.println("[machine]" + m.getName());
			System.out.println("id:" + m.getId() + " cell:" + m.getCellID()
					+ " capacity:" + m.getCapacity());
		}
	}

	/**
	 * @Description print job's information
	 */
	private void printJobInfo() {
		for (Job job : jobSet) {
			System.out.println("[job]" + job.getName());
			System.out.println("id:" + job.getId() + " weight:"
					+ job.getWeight() + " duedate:" + job.getDuedate()
					+ " releasetime:" + job.getRelDate() + " size:"
					+ job.getSize() + " family:" + job.getFamily() + " amount:"
					+ job.getAmount());
			for (Operation operation : job) {
				System.out.println("[operation]" + operation.getName());
				for (Map.Entry<Machine, Integer> entry : operation
						.getProcTimes().entrySet()) {
					System.out.print(entry.getValue() + "@" + entry.getKey()
							+ " ");
				}
				System.out.println();
			}// End for operation
		}// End for job
	}

//	public static void main(String[] args) {
//		MetaCMSReader dataSource = new MetaCMSReader("TransTest.txt");
//		System.out.println("begin//--------------------------------------");
//		// dataSource.printMachineInfo();
//		// dataSource.printJobInfo();
//		System.out.println("success!//--------------------------------------");
//	}

}
