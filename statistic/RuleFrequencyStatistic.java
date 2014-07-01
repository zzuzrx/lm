package com.lm.statistic;

import com.lm.algorithms.ga.Chromosome;
import com.lm.util.Constants;

public class RuleFrequencyStatistic {

	int[] seqCounter;
	//int[] assCounter;
    int[] TranCounter;
	
    public RuleFrequencyStatistic() {
		seqCounter = new int[Constants.mRules.length];
		//assCounter = new int[Constants.jRules.length];
		TranCounter =new int[Constants.TRules.length];
	}

	public int getSeqSum() {
		int sum = 0;
		for (int i = 0; i < seqCounter.length; i++) {
			sum += seqCounter[i];
		}
		return sum;
	}
    
	/**
	public int getAssSum() {
		int sum = 0;
		for (int i = 0; i < assCounter.length; i++) {
			sum += assCounter[i];
		}
		return sum;
	}
    **/
	public int getTransSum() {
		int sum = 0;
		for (int i = 0; i < TranCounter.length; i++) {
			sum += TranCounter[i];
		}
		return sum;
	}

	public void statSeqChromosome(Chromosome chro) {
		for (int i = 0; i < chro.size(); i++) {
			seqCounter[chro.get(i)]++;
		}
	}

	/**
	public void statAssChromosome(StrategyChromosome chro) {
		for (int i = 0; i < chro.size(); i++) {
			assCounter[chro.get(i)]++;
		}
	}
	**/
	public void statTransChromosome(Chromosome chro) {
		for (int i = 0; i < chro.size(); i++) {
			TranCounter[chro.get(i)]++;
		}
	}


	public void clear() {
		seqCounter = new int[Constants.mRules.length];
		//assCounter = new int[Constants.jRules.length];
		TranCounter =new int[Constants.TRules.length];
	}

	public void reportStat() {
		reportSeqStat();
		//reportAssStat();
		reportTransStat();
	}

	void reportSeqStat() {
		System.out.println("---The frequency of sequencing rules assigment---");
		for (int i = 0; i < seqCounter.length; i++) {
			System.out.print(Constants.mRules[i].toString() + "\t");
		}
		System.out.println();
		for (int i = 0; i < seqCounter.length; i++) {
			System.out.print(seqCounter[i] + "\t");
		}
		System.out.println("\nTotal assigments is " + getSeqSum());
	}
    /**
	void reportAssStat() {
		System.out.println("---The frequency of assigment rules assigment---");
		for (int i = 0; i < assCounter.length; i++) {
			System.out.print(Constants.jRules[i].toString() + "\t");
		}
		System.out.println();
		for (int i = 0; i < assCounter.length; i++) {
			System.out.print(assCounter[i] + "\t");
		}
		System.out.println("\nTotal assigments is " + getAssSum());
	}
    **/
	void reportTransStat() {
		System.out.println("---The frequency of assigment rules assigment---");
		
		for (int i = 0; i < TranCounter.length; i++) {
			System.out.print(Constants.TRules[i].toString() + "\t");
		}
		System.out.println();
		for (int i = 0; i < TranCounter.length; i++) {
			System.out.print(TranCounter[i] + "\t");
		}
		System.out.println("\nTotal transport is " + getTransSum());
	}

	public static void main(String[] args) {
		// 染色体初始化
		Chromosome seqChro = new Chromosome(6);
		Chromosome assChro = new Chromosome(5);

		seqChro.set(0, 0);
		seqChro.set(1, 1);
		seqChro.set(2, 2);
		seqChro.set(3, 0);
		seqChro.set(4, 2);
		seqChro.set(5, 0);

		assChro.set(0, 1);
		assChro.set(1, 1);
		assChro.set(2, 2);
		assChro.set(3, 2);
		assChro.set(4, 3);


		RuleFrequencyStatistic stat = new RuleFrequencyStatistic();
		stat.statSeqChromosome(seqChro);
		//stat.statAssChromosome(assChro);

		stat.reportSeqStat();
		stat.reportTransStat();
		System.out.println("----------------");

		stat.reportStat();
	}
}
