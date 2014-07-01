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

public class RulePrioirsReader {
	public MachineSet machineSet;
	public JobSet jobSet;
	public CellSet cellSet;
	public ArrayList<Integer>[] machines;
	

	
	/**
	 * @Description constructor
	 * @param filename
	 * @exception:
	 */
	public RulePrioirsReader(int msize,int csize,String filename) {
		File file = new File(filename);
		BufferedReader reader = null;
		try {
			reader 		   			  = new BufferedReader(new FileReader(file));

			// 初始化单元信息&&机器对象
			Constants.MachineToParts  = new int[msize+1][];
			Constants.CellToNextCells = new int[csize+1][];
			Constants.CellToParts	  = new ArrayList[csize+1][csize+1];
			for(int i = 1; i < csize+1; i++){
				for(int j = 1; j < csize+1; j++){
					Constants.CellToParts[i][j] = new ArrayList<Integer>();
				}
			}
			
			
			String line;
			String[] seq = null;
			
			/**读取机器信息**/
			for (int i = 1; i < msize+1; i++) {
				Constants.MachineToParts[i] = new int[machines[i-1].size()+1];
				line = reader.readLine();
				int m = line.indexOf(":");
				String t = line.substring(m+1);
				seq  = t.split(",");
				for(int j = 0; j < machines[i-1].size()+1;j++ ){
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
							Constants.CellToParts[i][j].add(k, Integer.parseInt(seq[k]));
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
						Constants.CellToNextCells[i][j] = Integer.parseInt(seq[j]);
					}
				}
			}
			

		
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}

	
	public static void main(String[] args) {
		



    }
}
