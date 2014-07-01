package com.lm.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lm.util.Constants;

class Operation {
	  int id;    // 全局工序号
	  int jobId; // Job id
	  int pos;   // Position in job
	  int IsTransport;//判断这道工序是否是跨单元运输

};

class Mode {
	  int opId; // Operation global id
	  int mch;  // Machine id 或者 批次id(即单元id)
	  int pt;   // Processing time (输入数据含有处理时间和生产准备时间)
};

class Transfer {
  int transid;	 //跨单元转移id
  int opId;		 // Operation global id
};

public class CplexDataWriter {
    public CplexDataWriter(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        
        int nbJobs;//工件数
        int nbMchs;//机器数
        int nbBMchs;//单元数
        int familychs;//工件族数目
        int nbBatches=5;//小车批次
        int TransNum;//总的跨单元次数
        
        int capacity;//小车容量
        
        //处理基本数据
        String line = br.readLine();
        String[] seq = line.split("\\s+");
        nbMchs= Integer.parseInt(seq[0]);
        nbJobs= Integer.parseInt(seq[1]);
        nbBMchs=Integer.parseInt(seq[2]);
        familychs=Integer.parseInt(seq[3]);
        capacity=Integer.parseInt(seq[4]);
        
        //释放时间
        int relDate []=new int[nbJobs];
        for (int i = 0; i < nbBMchs; i++) 
        	relDate[i]=0;
        
        //单元分布批次
        int CellLayOut[][]=new int[nbBMchs+1][nbBMchs+1];
//      CellLayOut[1][2]=1;
//      CellLayOut[2][1]=2;
        
//        CellLayOut[1][2]=1;
//        CellLayOut[1][3]=2;

//	      CellLayOut[1][2]=1;
//	      CellLayOut[1][3]=2;
//        CellLayOut[2][3]=3;
//        CellLayOut[2][1]=4;
//        CellLayOut[3][1]=5;
//        CellLayOut[3][2]=6;
        
        int Num=1;
        for (int i = 1; i <= nbBMchs; i++) 
        	for (int j = 1; j <= nbBMchs; j++) 
        		CellLayOut[i][j]=Num++;
        //权重数组
        double[] weights=new double[nbJobs+1];
        //释放时间数组
        int[] Duedates=new int[nbJobs+1];
        //运载量数组
        int ovenCapacity[]=new int[nbBMchs];
      
    	for (int i = 0; i < nbBMchs; i++) 
    		ovenCapacity[i]=capacity;
        br.readLine();
        
        //机器分布情况
        int MachineLayout[]=new int[nbMchs+1];
        line = br.readLine();
        seq = line.split("\\s+");
      	for (int i = 1; i <= seq.length; i++){
      		MachineLayout[i]=Integer.parseInt(seq[i-1]);;
      	}
        br.readLine();
        
        //转移时间
        int TransTime[][]=new int[nbBMchs+1][nbBMchs+1];
       for(int c1=1;c1<=nbBMchs;c1++){
    	   line = br.readLine();
           seq = line.split("\\s+");
           for (int  c2= 1; c2 <= seq.length; c2++){
        	   TransTime[c1][c2]=Integer.parseInt(seq[c2-1]);
           }
       }
       br.readLine();
        
        //存在转移的关系--cplex暂时用不到
       for(int c1=0;c1<nbBMchs;c1++){
    	   line = br.readLine();
       }
       br.readLine();
        
        
        //setupTime--cplex暂时用不到
       for(int c1=0;c1<nbBMchs;c1++){
    	   line = br.readLine();
       }
       br.readLine();
        
        //整体工序内容
       Operation Ops[]=new Operation[200];
       Mode Modes[]=new Mode[200];
       Transfer Trans[]=new Transfer[50];
        int globalID=0;
	    int TransferNum=0;
		for (int jobNo = 1; jobNo <= nbJobs; jobNo++) {
			line = br.readLine();
		    seq = line.split("\\s+");
		    
		    int opNum= Integer.parseInt(seq[0]);;//工序数(包括跨单元数)
		    int familyid= Integer.parseInt(seq[1]);;//工件族号
		    Duedates[jobNo]= Integer.parseInt(seq[2]);;//交货期
		    weights[jobNo]= Double.parseDouble(seq[3]);;//工件的交货期
		    
		    int BeforeCell=0;
		    int jobID=0;
		    do{
		    	line = br.readLine();
			    seq = line.split("\\s+");
		    	//找到当前能加工的机器
		    	int maNo=1;
		    	for(;maNo<=nbMchs;maNo++){
		    		if(Integer.parseInt(seq[maNo-1])!=0){
		    			break;
		    		}
		    	}
		    	if(jobID==0 || BeforeCell==MachineLayout[maNo]){
			    	++globalID;
			    	++jobID;
			    	Ops[globalID]=new Operation();
			    	Ops[globalID].id=globalID;
			    	Ops[globalID].jobId=jobNo;
			    	Ops[globalID].pos=jobID;
			    	Ops[globalID].IsTransport=0;
			    	
			    	Modes[globalID]=new Mode();
			    	Modes[globalID].opId=globalID;
			    	Modes[globalID].mch=maNo;
			    	Modes[globalID].pt=Integer.parseInt(seq[maNo-1]);
			    	
			    	BeforeCell=MachineLayout[maNo];
		    	}else{
		    		//跨单元调度的oper
			    	++globalID;
			    	++jobID;
			    	Ops[globalID]=new Operation();
			    	Ops[globalID].id=globalID;
			    	Ops[globalID].jobId=jobNo;
			    	Ops[globalID].pos=jobID;
			    	Ops[globalID].IsTransport=1;
			    	
			    	Modes[globalID]=new Mode();
			    	Modes[globalID].opId=globalID;
			    	Modes[globalID].mch=CellLayOut[BeforeCell][MachineLayout[maNo]];
			    	Modes[globalID].pt=TransTime[BeforeCell][MachineLayout[maNo]];
			    	
			    	TransferNum++;
			    	Trans[TransferNum]=new Transfer();
			    	Trans[TransferNum].transid=TransferNum;
			    	Trans[TransferNum].opId=globalID;
			    	//跨单元后一道oper
			    	++globalID;
			    	++jobID;
			    	Ops[globalID]=new Operation();
			    	Ops[globalID].id=globalID;
			    	Ops[globalID].jobId=jobNo;
			    	Ops[globalID].pos=jobID;
			    	Ops[globalID].IsTransport=0;
			    	
			    	Modes[globalID]=new Mode();
			    	Modes[globalID].opId=globalID;
			    	Modes[globalID].mch=maNo;
			    	Modes[globalID].pt=Integer.parseInt(seq[maNo-1]);
			    	
			    	BeforeCell=MachineLayout[maNo];
		    	}
		    	}while(jobID<opNum);
		}
		
		//写输出
        BufferedWriter bw = new BufferedWriter(new FileWriter(
        		"data/Trans/cplex/output/j20m12c5"
        ));
    	try {
	        bw.write("nbJobs= "+nbJobs+";\n");
	        bw.write("nbMchs= "+nbMchs+";\n");
	        bw.write("nbBMchs= "+nbBMchs+";\n");
	        bw.write("nbBatches= "+nbBatches+";\n");
	        bw.write("TransNum= "+TransferNum+";\n");
	        bw.newLine();
	        bw.write("ovenCapacity = [\n");
	        for (int i = 0; i < nbBMchs; i++){
	         	 if(i!=0) bw.write(",");
	        	 bw.write(ovenCapacity[i]+"");
	        }
	        bw.write("];\n\n");
	        
	        bw.write("weight = [\n");
	        for (int i = 1; i <=nbJobs ; i++){
	         	 if(i!=1) bw.write(",");
	        	 bw.write(weights[i]+"");
	        }
	        bw.write("];\n\n");
	        
	        bw.write("relDate = [\n");
	        for (int i = 0; i <nbJobs ; i++){
	         	 if(i!=0) bw.write(",");
	        	 bw.write(relDate[i]+"");
	        }
	        bw.write("];\n\n");
	        
	        bw.write("dueDate = [\n");
	        for (int i = 1; i <=nbJobs ; i++){
	         	 if(i!=1) bw.write(",");
	        	 bw.write(Duedates[i]+"");
	        }
	        bw.write("];\n\n");
	        
	        bw.write("Ops = {\n");
	        for (int i = 1; i <=globalID ; i++){
	        	bw.write("<"+Ops[i].id+","+Ops[i].jobId+","+Ops[i].pos+","+Ops[i].IsTransport+">");
	            
	        	if(i!= globalID) bw.write(",");
	        	if(Ops[i].IsTransport==1) bw.write("//跨单元工序");
	        	bw.newLine();
	        }
	        bw.write("};\n");
	        
	        bw.write("Modes = {\n");
	        for (int i = 1; i <=globalID ; i++){
	        	bw.write("<"+Modes[i].opId+","+Modes[i].mch+","+Modes[i].pt+">");
	            if(i!= globalID) bw.write(",");
	            bw.newLine();
	        }
	        bw.write("};\n");
	        
	        bw.write("Trans  = {\n");
	        for (int i = 1; i <=TransferNum ; i++){
	        	bw.write("<"+Trans[i].transid+","+Trans[i].opId+">");
	            if(i!= globalID) bw.write(",");
	            bw.newLine();
	        }
	        bw.write("};\n");
    	
    	} catch (IOException e) {
			e.printStackTrace();
		}finally {
		//Close the BufferedWriter
		try {
			if (bw != null) {
				bw.flush();
				bw.close();
			}
		}catch (IOException ex) {
			ex.printStackTrace();
		}//end try
		}//end finally
    }
  
    public static void main(String[] args) throws IOException {
    	System.out.println("begin//--------------------------------------");
    	CplexDataWriter dataWriter = new CplexDataWriter("data/Trans/cplex/j20m12c5");
		System.out.println("success!//--------------------------------------");   
    }
}
