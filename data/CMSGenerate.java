package com.lm.data;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import com.lm.util.Constants;

public class CMSGenerate {
	public CMSGenerate(String filename) throws IOException {
		String Filename = Constants.CMS_SOURCE + filename;
		/**注意使用BufferedWriter一定要在finally中flush()并close().**/
		BufferedWriter writer = new BufferedWriter(new FileWriter(Filename));
		try {
			/**机器数  作业数 单元数 工件族数 小车容量**/
//			int FirstParams[]={6,5,3,3,3};//3个cell的情况
//			int FirstParams[]={8,15,3,3,3};
//			int FirstParams[]={11,20,3,3,3};
//			int FirstParams[]={11,30,4,4,4};
//			int FirstParams[]={13,40,5,5,5};//5个cell的情况
//			int FirstParams[]={13,45,5,5,5};
//			int FirstParams[]={15,50,5,5,5};
//			int FirstParams[]={16,60,5,5,5};
//			int FirstParams[]={18,65,6,6,6};
//			int FirstParams[]={20,70,7,7,7};//7个cell的情况
//			int FirstParams[]={21,80,7,7,7};
			int FirstParams[]={21,90,7,7,7};
//			int FirstParams[]={23,95,7,7,7};
//			int FirstParams[]={25,100,9,9,9};//9个cell的情况
//			int FirstParams[]={27,110,9,9,9};
//			int FirstParams[]={30,120,9,9,9};
//			int FirstParams[]={32,130,10,10,10};
//			int FirstParams[]={34,140,10,10,10};
//			int FirstParams[]={36,150,10,10,10};
//			int FirstParams[]={40,160,12,12,12};
//			int FirstParams[]={42,170,12,12,12};
//			int FirstParams[]={45,180,12,12,12};
//			int FirstParams[]={47,190,15,15,15};
//			int FirstParams[]={50,200,15,15,15};//15个cell的情况

			/**
			 * 机器所属单元--当前指定布局信息
			 * 为了保持下标和机器编号的一致，CellLayout[0]不使用
			 */
//			int CellLayout[]={0,1, 1, 2, 2, 3, 3 };  //m6
//			int CellLayout[]={0,1, 1, 1, 2, 2, 2, 3, 3 }; //m8
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 3, 3, 3, 3 }; //m11
//			int CellLayout[]={0,1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4 }; //m11 c4
//			int CellLayout[]={0,1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 5, 5 ,5 }; //m13
//			int CellLayout[]={0,1, 1, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5 ,5 }; 
//			int CellLayout[]={0,1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 5, 5 ,5, 5, 5};  //m15
//			int CellLayout[]={0,1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 5, 5 ,5, 5, 5};  //m16
//			int CellLayout[]={0,1, 1, 1, 2, 2, 2, 3, 3, 3, 4, 4, 4, 5 ,5, 5, 6, 6, 6};  //m18
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7};  //m20
			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7};   //m21
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 7, 7};   //m23
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 8, 9, 9, 9};  //m25
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 8, 9, 9, 9}; //27
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 7, 7, 8, 8,
//								9, 9, 9}; //m30
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 7, 7, 8, 8,
//								9, 9,10,10,10}; //m32
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7, 7,
//								8, 8, 9, 9, 9,10,10}; //m34
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 6, 6, 6, 7, 7,
//								7, 8, 8, 8, 9, 9, 9,10,10}; //m36
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6,
//								7, 7, 8, 8, 8, 9, 9, 9,10,11,11,12,12}; //m40
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6,
//								6, 6, 6, 7, 7, 7, 8, 8, 8, 9, 9, 9,10,11,12}; //m42
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6,
//								6, 6, 6, 7, 7, 7, 7, 8, 8, 9, 9,10,10,11,11,11,12,12}; //m45
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6,
//								6, 6, 7, 7, 7, 7, 8, 8, 8, 9, 9, 9,10,11,12,13,13,14,15,15}; //m47
//			int CellLayout[]={0,1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 4, 4, 5, 5, 5, 6, 6, 7, 8, 9, 9, 9,
//								10,10,10,10,11,11,11,11,12,12,12,12,13,13,13,13,14,14,14,14,15,15,15,15,15};
			/**单元间的转移关系--可以做到根据生成的数据来修改得到转移信息**/
//			int CELL_Size = 3;
//			int CELL_Size = 4;
//			int CELL_Size = 5;
//			int CELL_Size = 6;			
//			int CELL_Size = 7;
			int CELL_Size = 9;
//			int CELL_Size = 10;
//			int CELL_Size = 12;
//			int CELL_Size = 15;
			int TransMatrix[][]= new int [CELL_Size][CELL_Size];
			for(int i = 0; i < CELL_Size; i++) {
				for(int j = 0; j < CELL_Size; j++){
					if(i==j){
						TransMatrix[i][j] = 0;
					}else{
						TransMatrix[i][j] = 1;
					}
				}
			}

/***************************具体写文件内容***********************************************************************/
			writer.write(FirstParams[0]+" "+FirstParams[1]+" "+
						 FirstParams[2]+" "+FirstParams[3]+" "+
						 FirstParams[4]+"\n"
						);
			writer.newLine();// 写入空格行  
			
			for (int i = 1; i < CellLayout.length; i++) {
				writer.write(CellLayout[i]+" ");
			}
			writer.newLine();// 写入行尾结束符
			writer.newLine();// 写入空格行  
			
			int machNum = 	FirstParams[0];
			int jobNum  =  	FirstParams[1];
			int cellNum = 	FirstParams[2];
			int familyNum = FirstParams[3];//工件族的编号是从0开始的
			int transportorSize = FirstParams[4];
			Random r=new Random(1);//1代表种子数
			
			//单元间转移时间矩阵
			int TranTime[][]=new int[cellNum+1][cellNum+1];
			for (int i = 1; i <= cellNum; i++) {
				for (int j = 1; j <= cellNum; j++) {
					if(i==j){
						TranTime[i][j]=0;
						writer.write(0+" ");
					}
					else{
						TranTime[i][j]=r.nextInt(46)+6;//TransTime U[6,50]
						writer.write(TranTime[i][j]+" ");
					}
				}
				writer.newLine();// 写入行尾结束符
			}
			writer.newLine();// 写入空格行  


			for (int i = 1; i <= cellNum; i++) {
				for (int j = 1; j <= cellNum; j++) {
					//System.out.println(TransMatrix[i-1][j-1]);
					writer.write(TransMatrix[i-1][j-1]+" ");
				}
				writer.newLine();// 写入行尾结束符
			}
			writer.newLine();// 写入空格行  
			
			// setupTime——矩阵
			int SetupTime[][]=new int[familyNum][machNum+1];
			for (int i = 0; i < familyNum; i++) {
				for (int j = 1; j <= machNum; j++) {
					int CurTime=r.nextInt(5)+5;//setupTime U[5,10]
					SetupTime[i][j]=CurTime;
					writer.write(CurTime+" ");
				}
				writer.newLine();// 写入行尾结束符
			}
			writer.newLine();// 写入空格行  

			// 工件属性
			for (int jobNo = 0; jobNo < jobNum; jobNo++) {
				/** 工序数 	工件族	 duedate 	weight
				 *  其中duedate是通过df*avg_tt计算得出来的
				 *  df因子
				 * **/
				int operNum =    r.nextInt(14)+5; // 工序数目 U[5,19]
				int family  =    r.nextInt(familyNum); // 工件族号
				int DueDate ;// 工件的DUEDATE
				double weight = (r.nextInt(9)+1)/10.0;// 工件权重 U[0,1]
				
				int OperTime[][]=new int[operNum+1][machNum+1];//默认初始化为0
				int OperMachine[][]=new int[operNum+1][];//每道工件能够加工的机器数
				float aveg_tt=0;//统计duedate
				/**
				 * 初始化加工时间
				 * 根据OperMachine序列和加工时间 来计算DUTDATE
				 */
				for (int j = 1; j <= operNum; j++) {
					
					/**
					 * 当前工序可以加工的机器数目 U[1,(单元数目,5)的最小值]
					 * 这个地方应该根据小车模型有所改变，我们要保证这些机器都是在不同单元内的
					 */
					int AbleMachineNum = Math.abs(r.nextInt())%(Math.min(cellNum,5)) + 1;
					OperMachine[j]=new int[AbleMachineNum];
					int[] AbleCell=new int[AbleMachineNum];
					int sum=0;
					
					//先random单元号
					Set<Integer> set = new TreeSet<Integer>();
					while(set.size()<AbleMachineNum){//产生不重复的随机数
						int c=r.nextInt(cellNum)+1;
						set.add(c);//n 是随机出的单元数
					}
					int num = 0;
					for(Integer digit: set){
						AbleCell[num++] = digit;
					}
					
					for (int t = 0; t < AbleMachineNum; t++) {
						//再在指定单元里面，random机器号
						int sumOfMachines=0;
						int AbleMachine=0;
						while(CellLayout[++AbleMachine]!=AbleCell[t]);
						if(AbleCell[t]!=cellNum){
							while(CellLayout[AbleMachine+(++sumOfMachines)]==AbleCell[t]);
						}
						else{
							sumOfMachines=machNum-AbleMachine+1;
						}
						AbleMachine+=r.nextInt(sumOfMachines);
						 
						 int OperatTime=r.nextInt(30)+1;//加工时间 U[1,30]
						 OperMachine[j][t]=AbleMachine;
						 OperTime[j][AbleMachine]=OperatTime;
						 sum+=OperatTime+SetupTime[family][AbleMachine];
						 
					}//end 当前工序抉择
				    aveg_tt+=(sum*1.0)/AbleMachineNum;

				    if(j!=1){
				    	int TransSum=0;
				    	int Num=0;
				    	//比较前一道工序可以加工的机器和当前工序可以加工的机器
				    	for (int i = 0; i < OperMachine[j-1].length; i++) {
				    		for (int k = 0; k < OperMachine[j].length; k++) {
				    			/**
				    			System.out.println("机器"+OperMachine[j-1][i]+":单元"+CellLayout[OperMachine[j-1][i]]
				    			                   +"————"+"机器"+OperMachine[j][k]+":单元"+CellLayout[OperMachine[j][k]]);
				    			System.out.println("转移时间"+TranTime[ CellLayout[OperMachine[j-1][i]] ][ CellLayout[OperMachine[j][k]] ] );
								**/
								if(CellLayout[OperMachine[j-1][i]] != CellLayout[OperMachine[j][k]]){
									TransSum+=TranTime[ CellLayout[OperMachine[j-1][i]] ][ CellLayout[OperMachine[j][k]] ];
									Num++;
								}
							}
				    	}
				    	
				    /***
				     * 能否根据这个工序前后的关系，添加转移关系代码块
				     */
				    if(Num!=0) TransSum/=Num;
				    aveg_tt+=TransSum;
				    }//end if(j!=1)
				}//end for当前工件的所有工序
				DueDate=(int)(Constants.DUE_FACTOR_DEFAULT*aveg_tt+0);//0是表示arriveTime			
//--------------DueDate统计结束--------------------------

/***************************开始写工序内容***********************************************************************/
				writer.write(operNum+"    "+family+"    "+DueDate+"    "+weight+"\n");
				for(int i=1;i<=operNum;i++){
					for(int j=1;j<=machNum;j++){
//						System.out.println(OperTime[i][j]);
						writer.write(OperTime[i][j]+" ");
					}
					writer.newLine();// 写入行尾结束符
				}
			}//end for 工件属性
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
		//Close the BufferedWriter
		try {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}catch (IOException ex) {
			ex.printStackTrace();
		}//end try
		}//end finally
	}//end Construction

	public static void main(String[] args) throws IOException {
		System.out.println("begin//--------------------------------------");
		CMSGenerate dataSource = new CMSGenerate("j90m21c7");
		System.out.println("success!//--------------------------------------");
	}
}
