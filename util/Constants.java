package com.lm.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.lm.algorithms.rule.machine.IMachineRule;
import com.lm.algorithms.rule.machine.MachineAT_RPT;
import com.lm.algorithms.rule.machine.MachineCR;
import com.lm.algorithms.rule.machine.MachineEDD;
import com.lm.algorithms.rule.machine.MachineFIFO;
import com.lm.algorithms.rule.machine.MachinePT_TIS;
import com.lm.algorithms.rule.machine.MachineSL;
import com.lm.algorithms.rule.machine.MachineSPT;
import com.lm.algorithms.rule.machine.MachineSRPT;
import com.lm.algorithms.rule.machine.MachineTIS;
import com.lm.algorithms.rule.machine.MachineWEDD;
import com.lm.algorithms.rule.machine.MachineWSPT;
import com.lm.algorithms.rule.machine.MachineGP1;
import com.lm.algorithms.rule.machine.MachineGP2;
import com.lm.algorithms.rule.machine.MachineGP3;
import com.lm.algorithms.rule.transportor.ITransportorRule;
import com.lm.algorithms.rule.transportor.TransEDD;
import com.lm.algorithms.rule.transportor.TransFIFO;
import com.lm.algorithms.rule.transportor.TransOperAndTrans;
import com.lm.algorithms.rule.transportor.TransOpersAndFIFO;
import com.lm.algorithms.rule.transportor.TransGP1;
import com.lm.algorithms.rule.transportor.TransGP2;

public class Constants {
    public static final int TOTAL_CASE = 1;//为了测试修改成1.原来是18
    public static final double DUE_FACTOR_DEFAULT = 1;//2;
//    public static final String CMS_SOURCE = "testInstances/cms-Trans/";
    public static final String CMS_SOURCE = "data/Trans/Case1/";

	/** 机器选工件调度规则 */
//    public static final IMachineRule[] mRules = { new MachineSPT(),
//            new MachineEDD(), new MachinePT_TIS(), new MachineTIS(),
//            new MachinePT_TIS(), new MachineAT_RPT(), new MachineSL(),
//            new MachineCR(), new MachineSRPT(), new MachineWSPT(),
//            new MachineWEDD()};
    public static final IMachineRule[] mRules = { 
//    	new MachineWSPT(),
    	new MachineEDD(),
//    	new MachineGP1(),
    	new MachinePT_TIS(),
    	new MachineAT_RPT(),new MachineSL(), new MachineSRPT(),
//    	new MachineGP2(),
//    	new MachineWEDD(), 
    	new MachineCR(),
//    	new MachineGP3(),new MachineGP4(),
//    	new MachineGP5(),new MachineGP6(),new MachineGP7(),
//    	new MachineGP8(),
    };
    public static String RULESET_DIR = "RulePrioirs/Case1/";
    public static int MachineRuleIndex;
    /**Transportor调度规则*/
//      public static final ITransportorRule[] TRules = {
//    	  new TransOperAndTrans(),new TransFIFO(),new TransEDD(),new TransOpersAndFIFO()
//      };
    public static final ITransportorRule[] TRules = {
    	new TransOperAndTrans(),new TransOpersAndFIFO(),
    	new TransFIFO(),new TransEDD(),
//    	new TransGP1(),new TransGP2(),new TransGP3(),
//    	new TransGP4(),
//    	new TransGP5(),new TransGP6()
    };
    public static int TransRuleIndex;
 
    public static int[][] setupTime; 		// 生产准备时间，以工件族为索引
    public static int[][] transferTime;		// 转移时间矩阵
    
    public static int[][] MachineToParts;	// 机器可加工的工件集合
//    public static int[][] MachineToPartsFunc;//机器可加工的工件Func
    public static int[][] CellToNextCells;	// 单元可运输到的单元集合
//    public static int[][] CellToNextCellsFunc;//机器可加工的工件Func
    public static ArrayList<Integer>[][] CellToParts;	// 对应路线上可以运输的工件集合
//    public static int[][][] CellToPartsFunc;//对应路线上可以运输的工件Func
    
    /**机器--单元的哈希表关系**/
    public static Map<Integer, Integer> CellForm;	
    
//    public static final String[] PROBLEM_NAMES = {
//    	"1","2","3","4","5",
//    	"6","7","8","9","10"
//    	
////    	"j5m6c3__3", "j15m8c3__3","j20m11c3__3",
////    	"j40m13c5__5","j50m15c5__5","j60m16c5__5",
////        "j70m20c7__7","j80m21c7__7","j90m21c7__7",
////        "j100m25c9__9", "j200m50c15__15"
//    };
    public static final String[] PROBLEM_NAMES = {
    	"j5m6c3__3", "j15m8c3__3","j20m11c3__3","j30m11c4_4",
    	"j40m13c5__5","j45m13c5_5","j50m15c5__5","j60m16c5__5","j65m18c6_6",
        "j70m20c7__7","j80m21c7__7","j90m21c7__7","j95m23c7_7",
        "j100m25c9__9", "j110m27c9__9", "j120m30c9__9",
        "j130m32c10__10", "j140m34c10__10", "j150m36c10__10",
        "j160m40c12__12"
    };

    public static final int TOTAL_PROBLEMS = 20;
    public static final int INSTANCES_PER_PROBLEM = 1;
    public static final int REPLICATIONS_PER_INSTANCE = 1;
}
