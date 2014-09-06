package com.lm.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.lm.util.Constants;

public class RemoveFlexWriter {
	public RemoveFlexWriter(int name)  throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader("data/Trans/IABC/"+name));
        BufferedWriter bw = new BufferedWriter(new FileWriter("data/Trans/IABC_new/"+name));
        Random r=new Random(1);//1代表种子数
        
		// 系统一些主要参数
		String line;			
		line = reader.readLine();
		bw.write(line);
		
		String[] seq = line.split("\\s++");
		int machNum = Integer.parseInt(seq[0]);
		int jobNum = Integer.parseInt(seq[1]);
		int cellNum = Integer.parseInt(seq[2]);
		int familyNum = Integer.parseInt(seq[3]);
		int transportorSize = Integer.parseInt(seq[4]);
		reader.readLine();// 空格行
		bw.write("\n\n");
		
		//机器分布矩阵
		line = reader.readLine();
		bw.write(line);
		reader.readLine();// 空格行
		bw.write("\n\n");
		
		// 单元间转移关系矩阵
		for (int i = 1; i <= cellNum; i++) {
				line = reader.readLine();
				bw.write(line+"\n");
		}
		reader.readLine();// 空格行
		bw.write("\n");
		
		// 单元间转移时间矩阵
		for (int i = 1; i <= cellNum; i++) {
			line = reader.readLine();
			bw.write(line+"\n");
		}
		reader.readLine();// 空格行
		bw.write("\n");
		
		//setupTime——跟工件族相关
		for (int i = 0; i < familyNum; i++) {
			line = reader.readLine();
			bw.write(line+"\n");
		}
		reader.readLine();// 空格行
		bw.write("\n");
		
		// 工件属性
		for (int jobNo = 0; jobNo < jobNum; jobNo++) {
			// 工件的总括属性
			line = reader.readLine();
			bw.write(line+"\n");
			seq = line.split("\\s++");
			int operNum = Integer.parseInt(seq[0]); // 工序数目
			
			for (int operNo = 0; operNo < operNum; operNo++) {
				line = reader.readLine();
				seq = line.split("\\s+");
				int i;
				int[] OperPos = new int[seq.length];
				int count = 0;
				for (i = 0; i < seq.length; i++) {
					if(Integer.parseInt(seq[i])!=0) {
						OperPos[count++] = i;
					}
				}
				int Position = r.nextInt(count);
				for(int j = 0; j <seq.length; j++) {
					if(j!=OperPos[Position]) bw.write("0 ");
					else 	 bw.write(Integer.parseInt(seq[OperPos[Position]])+" ");
				}
				bw.write("\n");
			}
		}
		if (bw != null) {
			bw.flush();
			bw.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		for(int i=1; i<20;i++){
			RemoveFlexWriter transfer= new RemoveFlexWriter(i);
			System.out.println("success transfer"+i);
		}
	}
}
