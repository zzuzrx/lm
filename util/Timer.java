package com.lm.util;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public final class Timer {
	/** 当前时间**/
    static int time = 0;

 //------------------------------之前版本------------------
    
    static Set<Integer> triggers = new TreeSet<Integer>();
    public static void addTrigger(int t) {
        if (t > 0) triggers.add(t);
    }
    
    public static void resetTimer() {//重置时钟
        time = 0;
        triggers.clear();
    }

    public static void stepTimer() {//时钟步进
        if (triggers.size() > 0) {
            int nextTime = Collections.min(triggers);
            time = nextTime;
            triggers.remove(new Integer(nextTime));
        }
        else {
            time++;
        }
    }
    
 //------------------------------------------------------------
 
  //------------------------------尝试优化的版本-----------------
    /**
	static Set<Integer> Machinetriggers = new TreeSet<Integer>();
    static Set<Integer> Celltriggers = new TreeSet<Integer>();
    
    //  反映当前timetrigger的状态的两个bool变量
    //  true 表示当前用check
    //  false 表示当前不用check
    //
    static boolean MachineSituation;
	static boolean CellSituation;
	
    public static void changeMachineSituationToTrue() {
    	MachineSituation=true;
	}
    public static void changeCellSituationToTrue() {
    	CellSituation=true;
	}
    public static boolean isMachineSituation() {
		return MachineSituation;
	}
    public static boolean isCellSituation() {
		return CellSituation;
	}
    public static void addMachineTrigger(int t) {
        if (t > 0) Machinetriggers.add(t);
    }
    public static void addCellTrigger(int t) {
        if (t > 0) Celltriggers.add(t);
    }
    public static void resetTimer() {//重置时钟
        time = 0;
        MachineSituation=true;
        CellSituation=true;
        Machinetriggers.clear();
        Celltriggers.clear();
    }

    public static void stepTimer() {//时钟步进
    	if(Celltriggers.size()==0){
    		if(Machinetriggers.size()==0){//两个trigger都为0
    			time++;
    			MachineSituation=false;
    			CellSituation=false;
    		}
    		else{//machinetrigger不为空
    		int nextTime = Collections.min(Machinetriggers);
            time = nextTime;
            Machinetriggers.remove(new Integer(nextTime));
            MachineSituation=true;
            CellSituation=false;
    		}
    	}
    	else if(Machinetriggers.size()==0){//Celltriggers不为空
    		int nextTime = Collections.min(Celltriggers);
            time = nextTime;
            Celltriggers.remove(new Integer(nextTime));
            MachineSituation=false;
            CellSituation=true;
    	}
    	else{//二者均不为空
        	int a=Collections.min(Machinetriggers);
        	int b=Collections.min(Celltriggers);
        	if(a>b){
        		Celltriggers.remove(b);
        		time=b;
        	}
        	else if(a<b){
        		Machinetriggers.remove(a);
        		time=a;
        	}
        	else if(a==b){
        		Celltriggers.remove(b);
        		Machinetriggers.remove(a);
        		time=a;
        	}
        	 MachineSituation=true;
             CellSituation=true;
        }
    }
    **/
  //------------------------------------------------------------
    /** 获取当前时间 */
    public static int currentTime() {
        return time;
    }
    
    /** 启动计时器 */
    public static void startTimer() {
        time = 0;
    }
    
    /**
     * 时钟步进
     * 
     * @param step
     *            时间步进步长
     */
    public static void stepTimer(int step) {
        time += step;
    }

	public static void printTrigger() {
		// TODO Auto-generated method stub
		/**transform the Set To Array **/
		Integer[] TimeResult=(Integer[])triggers.toArray(new Integer[triggers.size()]);
		StringBuilder  infoBuilder = new StringBuilder();
		for (int i = 0; i < TimeResult.length; i++) {
			infoBuilder.append(TimeResult[i]);
			infoBuilder.append(",");
		}
		System.out.println(infoBuilder.toString());
	}
}
