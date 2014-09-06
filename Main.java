package com.lm;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.lm.algorithms.SimpleScheduler;
import com.lm.algorithms.ga.HSGA;
import com.lm.algorithms.measure.IMeasurance;
import com.lm.algorithms.measure.Makespan;
import com.lm.algorithms.measure.TotalWeightedTardiness;
import com.lm.algorithms.rule.machine.IMachineRule;
import com.lm.algorithms.rule.machine.MachineEDD;
import com.lm.algorithms.rule.machine.MachineGP1;
import com.lm.algorithms.rule.machine.MachinePT_TIS;
import com.lm.algorithms.rule.machine.MachineSPT;
import com.lm.algorithms.rule.machine.MachineSRPT;
import com.lm.algorithms.rule.transportor.ITransportorRule;
import com.lm.algorithms.rule.transportor.TransEDD;
import com.lm.algorithms.rule.transportor.TransFIFO;
import com.lm.algorithms.rule.transportor.TransGP1;
import com.lm.algorithms.rule.transportor.TransOperAndTrans;
import com.lm.algorithms.rule.transportor.TransOpersAndFIFO;
import com.lm.data.CMSReader;
import com.lm.data.MetaCMSReader;
import com.lm.domain.Cell;
import com.lm.domain.CellSet;
import com.lm.domain.JobSet;
import com.lm.domain.Machine;
import com.lm.domain.MachineSet;
import com.lm.statistic.MySummaryStat;
import com.lm.statistic.RuleFrequencyStatistic;
import com.lm.util.Constants;

public class Main {
	
//	private static Logger log = LoggerHelper.getLogger("Main");
	private static MetaCMSReader dr;
//	private static CMSReader dr;
	static IMeasurance makespan = new Makespan();
	static IMeasurance TWT 		= new TotalWeightedTardiness();
	
	/**
	 * @Description 主函数入口
	 * @param args
	 * @throws IOException
	 * @throws CloneNotSupportedException
	 */
	public static void main(String[] args) throws IOException,
			CloneNotSupportedException {
//		RulePriors();
//		bestRule_twt();
	    HSGA(TWT);
//		FullFactorExperiment(TWT);
	}

	/**
	 * 生成各种基于启发式信息的优先级序列
	 * @param bia
	 * @throws IOException
	 */
	static void RulePriors() throws IOException {
		 for (int caseNo = 0; caseNo < Constants.TOTAL_PROBLEMS; caseNo++) {
			Constants.RULESET_DIR = "RulePrioirs/Case1/" + (caseNo + 1);
			
			dr = new MetaCMSReader("data/Trans/IABC/" + (caseNo + 1));
			MachineSet machineSet = dr.getMachineSet();
	        JobSet jobSet= dr.getJobSet();
	        CellSet cellSet=dr.getCellSet();

	//		/**
			for(Constants.MachineRuleIndex = 0; Constants.MachineRuleIndex< Constants.mRules.length; Constants.MachineRuleIndex++){
				for(Constants.TransRuleIndex = 0; Constants.TransRuleIndex< Constants.TRules.length; Constants.TransRuleIndex++){
					SimpleScheduler simpleScheduler = new SimpleScheduler(machineSet, jobSet ,cellSet);
					//set machine rule
					for(Machine m:machineSet){
//						m.setRule(Constants.mRules[Constants.MachineRuleIndex]);
						m.setRule(new MachineGP1());
					}
					//set machine rule
					for(Cell c:cellSet){
						c.setRule(Constants.TRules[Constants.TransRuleIndex]);
						c.setRule(new TransGP1());
					}
					simpleScheduler.schedule();
				}
			}
//			System.out.println("程序完成！TWT结果为:"+TWT.toString());	//simpleScheduler.getTotalWeightedTardiness()
	//		**/
		 }
	}
	/**
	 * 模拟GA进化-使用默认参数
	 * @param measure
	 * @param machineSet
	 * @param jobSet
	 * @param stageSet
	 * @param stat
	 * @param caseNo
	 * @throws CloneNotSupportedException 
	 */
	static void simulationGA(IMeasurance measure,MySummaryStat stat,MachineSet machineSet, JobSet jobSet,CellSet cellSet) throws CloneNotSupportedException {
		simulationGA(measure,stat, machineSet, jobSet, cellSet, 0.6, 0.18, 100, 48);
    }

	/**
	 * @Description 模拟GA进化-带参数的 
	 * @param measure
	 * @param stat
	 * @param machineSet
	 * @param jobSet
	 * @param cellSet
	 * @param pc	crossProb
	 * @param pm	mutationProb
	 * @param gm	maxGeneration
	 * @param ps	populationSize
	 * @throws CloneNotSupportedException
	 */
	static void simulationGA(IMeasurance measure,MySummaryStat stat, MachineSet machineSet, JobSet jobSet,CellSet cellSet,
			   					double pc,double pm, int gm, int ps) throws CloneNotSupportedException {
	        for (int ins = 0; ins < Constants.INSTANCES_PER_PROBLEM; ins++) {
	        	 
		        SimpleScheduler scheduler = new SimpleScheduler(machineSet, jobSet,cellSet);
		        HSGA ga = new HSGA(machineSet, jobSet, cellSet, scheduler, measure,
		                pc, pm, gm, ps);
		        ga.setStat(new RuleFrequencyStatistic());
            	
		        double meanperformance = 0, totalperformance = 0, totalTime = 0, meanTime = 0;
            	for (int i = 0; i < Constants.REPLICATIONS_PER_INSTANCE; i++) {
	        		long start = System.currentTimeMillis();
	        		ga.schedule();
//	       	        ga.printScheduleResult();
	        		totalTime += (System.currentTimeMillis() - start);
	        		totalperformance += ga.getFunctionValue();
	        	}//end for replications

	        	meanperformance = totalperformance
/ Constants.REPLICATIONS_PER_INSTANCE;
	        	meanTime = totalTime / Constants.REPLICATIONS_PER_INSTANCE;
	        	stat.value(meanperformance);
	        	stat.addTime(meanTime);
	        	stat.setParameter(ga.parameter());
//	        	System.out
//	                .println(ins + "\t" + ga.parameter() + "\t"
//	                        + measure.toString() + "\t" + meanperformance + "\t"
//	                        + meanTime);
	        }//end for instances
	   }
	
	/**
	 * 
	 * @param measure GP+GA实验
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	static void HSGA(IMeasurance measure) throws IOException, CloneNotSupportedException {
		    StringBuilder resultFileName = new StringBuilder(80);
	        resultFileName.append("result/BestRule/").append(
	                new Throwable().getStackTrace()[0].getMethodName());
	        System.out.println(resultFileName.toString());
	        
	        printTitle(resultFileName.toString());
	        for (int caseNo = 0; caseNo < Constants.TOTAL_PROBLEMS; caseNo++) {
	        	 System.out.println("case"+(caseNo+1)+":");
	        	Constants.RULESET_DIR = "RulePrioirs/IABC/" + (caseNo + 1);
	            dr = new MetaCMSReader("data/Trans/IABC/" + (caseNo + 1));
		       	MachineSet machineSet = dr.getMachineSet();
		        JobSet jobSet= dr.getJobSet();
		        CellSet cellSet=dr.getCellSet();
		        
	           MySummaryStat stat=new MySummaryStat(Constants.PROBLEM_NAMES[caseNo]);
	           simulationGA(measure,stat,machineSet,jobSet,cellSet);
	           printResult(resultFileName.toString(), stat);
	        }
	}
	 
	/**
	 * 最优规则组合_衡量twt的
	 * @throws IOException
	 */
	static void bestRule_twt() throws IOException {

	        StringBuilder resultFileName = new StringBuilder(80);
	        resultFileName.append("result/BestRule/").append(
	                new Throwable().getStackTrace()[0].getMethodName());
	        System.out.println(resultFileName.toString());


	    	/** 机器选工件调度规则 */
	        IMachineRule[] mRule = { new MachinePT_TIS(), new MachineSPT(),
	                				 new MachineSRPT(),new MachineEDD() };
	        /**Transportor调度规则*/
	        ITransportorRule[] TRules = {new TransOperAndTrans(),new TransFIFO(),
	        							 new TransEDD(),new TransOpersAndFIFO()};
	     

	        printTitle(resultFileName.toString());
	        for (int i = 0; i < 4; i++) {

	            for (int caseNo = 0; caseNo < Constants.TOTAL_PROBLEMS; caseNo++) {
	            	MySummaryStat stat=new MySummaryStat(Constants.PROBLEM_NAMES[caseNo]);

//	            	dr = new CMSReader("data/Stage1/" + (caseNo + 1));//for test
	            	

	            	MetaCMSReader dr = new MetaCMSReader("data/Trans/Case1/" + (caseNo + 1));
	            	MachineSet testmachineSet = dr.getMachineSet();
	        		JobSet testjobSet = dr.getJobSet();
	        		CellSet testcellSet =dr.getCellSet();

	                double totalperformance = 0, totalTime = 0, meanTime = 0;
	                for (int in = 0; in < Constants.INSTANCES_PER_PROBLEM; in++) {
	                    for (Machine machine : testmachineSet) {
	                        machine.setRule(mRule[i]);
	                    }
	                    for (Cell c : testcellSet) {
	                        c.setRule(TRules[i]);
	                    }
	                    SimpleScheduler scheduler = new SimpleScheduler(testmachineSet, testjobSet ,testcellSet);
	                    
	                    long start = System.currentTimeMillis();	                    
	                    scheduler.schedule();
	                    totalTime += (System.currentTimeMillis() - start);
	                    
	                    double CurPerf=TWT.getMeasurance(scheduler);//getTotalWeightedTardiness
//	                    double CurPerf=scheduler.getMakespan();
	                    totalperformance += CurPerf;
			            
	                    System.out.println(caseNo+"_in"+"\t" + CurPerf + "\t");
	            
	                    stat.value(CurPerf);
	                }
		            meanTime = totalTime / Constants.INSTANCES_PER_PROBLEM;
		            stat.addTime(meanTime);
		            stat.setParameter("bestRule 无参数");

	            printResult(resultFileName.toString(), stat);
	            }//end for every case
	        }//end for each rule's component
	    }
	  
	/**
	 * 全因子(ANOVA)实验
	 * @param measure
	 * @throws IOException
	 * @throws CloneNotSupportedException 
	 */
	static void FullFactorExperiment(IMeasurance measure) throws IOException, CloneNotSupportedException {

	        StringBuilder resultFileName = new StringBuilder(80);
	        resultFileName.append("result/ANOVA/")
	                .append(new Throwable().getStackTrace()[0].getMethodName())
	                .append("+").append(measure.toString());
	        System.out.println(resultFileName.toString());

	        BufferedWriter br = new BufferedWriter(new FileWriter(
	                resultFileName.toString()));

	        /*
	         * 全因子组合
	         */
//	        int[] ps = { 6, 12, 24, 48 };
//	        double[] pc = { 0.05, 0.3, 0.6, 0.9 };
//	        double[] pm = { 0, 0.02, 0.1, 0.18 };
//	        int[] gm = { 25, 50, 100 };
	        int[] ps = { 12 };
	        double[] pc = { 0.9};
	        double[] pm = { 0.3};
	        int[] gm = { 100 };
	        printTitle(resultFileName.toString());
	        for (int iPs = 0; iPs < ps.length; iPs++) {
	            for (int iPc = 0; iPc < pc.length; iPc++) {
	                for (int iPm = 0; iPm < pm.length; iPm++) {
	                    for (int iGm = 0; iGm < gm.length; iGm++) {
	                        for (int caseNo = 0; caseNo < Constants.TOTAL_PROBLEMS; caseNo++) {
	                        	MySummaryStat stat=new MySummaryStat(Constants.PROBLEM_NAMES[caseNo]);
	                            dr = new MetaCMSReader("data/Trans/Case1/" + (caseNo + 1));
	                            MachineSet machineSet = dr.getMachineSet();
	        	        		JobSet jobSet = dr.getJobSet();
	        	        		CellSet cellSet =dr.getCellSet();
	                            for (int ins = 0; ins < Constants.INSTANCES_PER_PROBLEM; ins++) {
	                            	simulationGA(measure,stat, machineSet, jobSet,
	                                		cellSet, pc[iPc], pm[iPm], gm[iGm],
	                                        ps[iPs]);
	                            }
	                            printDOEResult(br, stat);
	                        }
	                    }
	                }
	            }
	        }

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
