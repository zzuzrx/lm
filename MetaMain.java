package com.lm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.lm.algorithms.AbstractScheduler;
import com.lm.algorithms.MetaHeuristicScheduler;
import com.lm.algorithms.SimpleScheduler;
import com.lm.algorithms.abc.DABC;
import com.lm.algorithms.abc.DABC2;
import com.lm.algorithms.abc.DABC3;
import com.lm.algorithms.abc.DABC4;
import com.lm.algorithms.ga.HSGA;
import com.lm.algorithms.measure.IMeasurance;
import com.lm.algorithms.measure.Makespan;
import com.lm.algorithms.measure.MetaIMeasurance;
import com.lm.algorithms.measure.TotalWeightedTardiness;
import com.lm.algorithms.rule.machine.IMachineRule;
import com.lm.algorithms.rule.machine.MachineEDD;
import com.lm.algorithms.rule.machine.MachinePT_TIS;
import com.lm.algorithms.rule.machine.MachineSPT;
import com.lm.algorithms.rule.machine.MachineSRPT;
import com.lm.algorithms.rule.transportor.ITransportorRule;
import com.lm.algorithms.rule.transportor.TransEDD;
import com.lm.algorithms.rule.transportor.TransFIFO;
import com.lm.algorithms.rule.transportor.TransOperAndTrans;
import com.lm.algorithms.rule.transportor.TransOpersAndFIFO;
import com.lm.data.CMSReader;
import com.lm.data.MetaCMSReader;
import com.lm.Metadomain.Cell;
import com.lm.Metadomain.CellSet;
import com.lm.Metadomain.JobSet;
import com.lm.Metadomain.Machine;
import com.lm.Metadomain.MachineSet;
import com.lm.statistic.MySummaryStat;
import com.lm.statistic.RuleFrequencyStatistic;
import com.lm.util.Constants;

public class MetaMain {
	
//	private static Logger log = LoggerHelper.getLogger("Main");
	private static MetaCMSReader dr;
	static MetaIMeasurance makespan = new Makespan();
	static MetaIMeasurance TWT = new TotalWeightedTardiness();
	
	/**
	 * @Description 主函数入口
	 * @param args
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	public static void main(String[] args) throws IOException,
			CloneNotSupportedException {
		FullFactorExperiment(TWT);
//		ProcessDABC(TWT);
//		RunDABC(TWT);
		RunMutation(TWT);			//using DABC4
	}

	/**
	 * @Description run the process for 100 generations
	 * @param measure
	 * @throws IOException 
	 * @throws CloneNotSupportedException 
	 */
	private static void ProcessDABC(MetaIMeasurance measure) throws IOException, CloneNotSupportedException {
		// TODO Auto-generated method stub
		StringBuilder resultFileName = new StringBuilder(80);
		resultFileName.append("result/Process/")
			.append(new Throwable().getStackTrace()[0].getMethodName())
			.append("+").append(measure.toString());
		System.out.println(resultFileName.toString());

		for(int i = 0; i< Constants.TOTAL_PROBLEMS; i++){
			BufferedWriter Process = new BufferedWriter(new FileWriter(
					"ProCessing"+(i+1) ));
			MySummaryStat stat=new MySummaryStat(Constants.PROBLEM_NAMES[i]);    //Trans文件读取
			dr = new MetaCMSReader("data/Trans/IABC/" + (i + 1));                         
			MachineSet machineSet = dr.getMachineSet();
			JobSet jobSet = dr.getJobSet();
			CellSet cellSet =dr.getCellSet();
			simulationabc(measure,stat,machineSet,jobSet,cellSet);
		}
	}

	/**
	 * 测试使用的简单例子
	 * @param bia
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	static void RunDABC(MetaIMeasurance measure) throws IOException, CloneNotSupportedException {
		StringBuilder resultFileName = new StringBuilder(80);
		resultFileName.append("result/ABC/").append(
	               new Throwable().getStackTrace()[0].getMethodName());	        
		System.out.println(resultFileName.toString());
		
		 printTitle(resultFileName.toString());
		 for (int caseNo = 0; caseNo < Constants.TOTAL_PROBLEMS; caseNo++) {
	        	 System.out.println("case"+(caseNo+1)+":");
	            dr = new MetaCMSReader("data/Trans/IABC/" + (caseNo + 1));
		       	MachineSet machineSet = dr.getMachineSet();
		        JobSet jobSet= dr.getJobSet();
		        CellSet cellSet=dr.getCellSet();
		        
	           MySummaryStat stat=new MySummaryStat(Constants.PROBLEM_NAMES[caseNo]);
	           for (int ins = 0; ins < Constants.INSTANCES_PER_PROBLEM; ins++) {
	        	   MetaHeuristicScheduler scheduler = new MetaHeuristicScheduler(machineSet, jobSet,cellSet);
	        	   DABC abc = new DABC(machineSet, jobSet, cellSet, scheduler, measure);
	        	   long start = System.currentTimeMillis();
	        	   
	        	   abc.schedule();
	        	   
	        	   stat.addTime(System.currentTimeMillis() - start);
	        	   stat.value(abc.getBestFunctionValue()); 
	  		 	}
	 		 printResult(resultFileName.toString(), stat);
		 }
	}
	
	/**
	 * 测试mutation方法
	 * @param bia
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	static void RunMutation(MetaIMeasurance measure) throws IOException, CloneNotSupportedException {
		StringBuilder resultFileName = new StringBuilder(80);
		resultFileName.append("result/ABC/").append(
	               new Throwable().getStackTrace()[0].getMethodName());	        
		System.out.println(resultFileName.toString());
		
		 printTitle(resultFileName.toString());
		 for (int caseNo = 1; caseNo < Constants.TOTAL_PROBLEMS; caseNo++) {
	        	 System.out.println("case"+(caseNo+1)+":");
	            dr = new MetaCMSReader("data/Trans/IABC/" + (caseNo + 1));
		       	MachineSet machineSet = dr.getMachineSet();
		        JobSet jobSet= dr.getJobSet();
		        CellSet cellSet=dr.getCellSet();
		        
	           MySummaryStat stat=new MySummaryStat(Constants.PROBLEM_NAMES[caseNo]);
	           for (int ins = 0; ins < Constants.INSTANCES_PER_PROBLEM; ins++) {
	        	   MetaHeuristicScheduler scheduler = new MetaHeuristicScheduler(machineSet, jobSet,cellSet);
	        	   DABC4 abc = new DABC4(machineSet, jobSet, cellSet, scheduler, measure);
	        	   long start = System.currentTimeMillis();
	        	   
	        	   abc.schedule(caseNo+1);
	        	   
	        	   stat.addTime(System.currentTimeMillis() - start);
	        	   stat.value(abc.getBestFunctionValue()); 
	  		 	}
	 		 printResult(resultFileName.toString(), stat);
		 }
	}
	/**
	 * 
	 * @param measure
	 * @param stat
	 * @param machineSet
	 * @param jobSet
	 * @param cellSet
	 * @throws CloneNotSupportedException 
	 */
	 static void simulationabc(MetaIMeasurance measure, MySummaryStat stat,
		MachineSet machineSet, JobSet jobSet, CellSet cellSet) throws CloneNotSupportedException {
	
		MetaHeuristicScheduler scheduler = new MetaHeuristicScheduler(machineSet, jobSet,cellSet);
		DABC3 abc = new DABC3(machineSet, jobSet, cellSet, scheduler, measure);
		abc.schedule();
	}
	 
	 
	/**
	 * 全因子(ANOVA)实验
	 * @param measure
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	static void FullFactorExperiment(MetaIMeasurance measure) throws IOException, CloneNotSupportedException {

	        StringBuilder resultFileName = new StringBuilder(80);
	        resultFileName.append("result/ANOVA/")
	                .append(new Throwable().getStackTrace()[0].getMethodName())
	                .append("+").append(measure.toString());
	        System.out.println(resultFileName.toString());

	        BufferedWriter br = new BufferedWriter(new FileWriter(
	                resultFileName.toString()));

//	        /*
//	         * 全因子组合
//	         */
//	        int[] ps = { 6, 12, 24, 48 };
//	        int[] gm = { 25, 50, 100, 500 };
////	        int[] ps = { 12 };
////	        double[] pc = { 0.9};
////	        double[] pm = { 0.3};
////	        int[] gm = { 100 };
//	        printTitle(resultFileName.toString());
//	        for (int iPs = 0; iPs < ps.length; iPs++) {
//	                    for (int iGm = 0; iGm < gm.length; iGm++) {
//	                        for (int caseNo = 0; caseNo < Constants.TOTAL_PROBLEMS; caseNo++) {
//	                        	MySummaryStat stat=new MySummaryStat(Constants.PROBLEM_NAMES[caseNo]);
//	                            dr = new MetaCMSReader("data/Trans/Case1/" + (caseNo + 1));
//	                            MachineSet machineSet = dr.getMachineSet();
//	        	        		JobSet jobSet = dr.getJobSet();
//	        	        		CellSet cellSet =dr.getCellSet();
//	                            for (int ins = 0; ins < Constants.INSTANCES_PER_PROBLEM; ins++) {
//	                            	 simulationabc(measure,stat,machineSet,jobSet,cellSet);
//	                            }
//	                            printDOEResult(br, stat);
//	                        }
//	                    }
//	                }
//
//	    }
//	
	/*
     * GP、Rules、random性能对比实验
     */
    int[] ps = { 48 };        //种群数量
    int[] gm = {  500 };     //迭代次数
//    int[] ps = { 12 };
//    double[] pc = { 0.9};
//    double[] pm = { 0.3};
//    int[] gm = { 100 };

}
	/**
	 * 输出数据头到文件中
	 * @param fileName：文件名
	 * @param stats：当前统计的数据
	 */
	private static void printTitle(String fileName) {
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(fileName));
			br.write(MySummaryStat.TABLE_TAG);
			br.newLine();
			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出结果到文件中
	 * @param fileName：文件名
	 * @param stats：当前统计的数据
	 */
	private static void printResult(String fileName,MySummaryStat stat) {
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(fileName,true));
			br.write(stat.toString());
			br.newLine();

			br.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出全因子结果到文件缓存中
	 * @param br
	 * @param stat
	 */
	private static void printDOEResult(BufferedWriter br, MySummaryStat stat) {
		        try {
		            br.write(stat.DOEString());
		            br.newLine();
		            br.flush();
		        }
		        catch (IOException e) {
		            e.printStackTrace();
		        }
	  }
}
