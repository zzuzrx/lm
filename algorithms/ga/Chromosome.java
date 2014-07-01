package com.lm.algorithms.ga;

import java.util.Arrays;
/**
 * @Description:Chromosome的gene所表示的类

 * @author:lm

 * @time:2013-11-6 下午03:53:33

 */
public class Chromosome implements Comparable<Chromosome>, Cloneable {
/***************************属性域***********************************************************************/
	/** arrays that denotes the Chromosome **/
	private int[] gene;
    /** size of Chromosome**/
    private int size;
    /** fitness of Chromosome*/
    private double fitness;
    /** function values of Chromosome*/
    private double function;
	/** probability of Chromosome that being selected*/
    private double prob;
/***************************方法域***********************************************************************/
    /**
     * @Description constructed function
     * @param size
     * @exception:
     */
    public Chromosome(int size) {
        super();
        this.size = size;
        this.fitness = 0.0;
        gene = new int[size];
    }
    
    /**
     * @Description get function values of Chromosomes
     * @return
     */
    public double getFunction() {
		return function;
	}

	/**
	 * @Description set function values of Chromosomes
	 * @param function
	 */
	public void setFunction(double function) {
		this.function = function;
	}

    /* (non-Javadoc):clone methods to copy a get function values of Chromosomes
     * @see java.lang.Object#clone()
     * override
     */
    public Chromosome clone() throws CloneNotSupportedException {
        Chromosome cloned = (Chromosome) super.clone();
        cloned.setGene(Arrays.copyOf(cloned.getGene(), size));
        return cloned;
    }

    /**
     * @Description Indicates whether the gene is "equal to" this one.
     * @param o
     * @return
     */
//    @Override
    public boolean equals(Chromosome o){
    	 for (int i = 0; i < gene.length; i++) {
    		 if(gene[i]!=o.get(i)) return false;
    	 }
    	 return true;
    }
    /**
     * @Description get gene of particular position on Chromosomes
     * @param index
     * @return
     */
    public int get(int index) {
        if (index < 0 || index >= size) throw new IllegalArgumentException("out of gene arrays boundary");
        return gene[index];
    }

    /**
     * @Description set gene values of particular position on Chromosomes
     * @param index
     * @param value
     */
    public void set(int index, int value) {
        if (index < 0 || index >= size) throw new IllegalArgumentException("out of gene arrays boundary");
        gene[index] = value;
    }

    /**
     * @Description get size of Chromosomes
     * @return
     */
    public int size() {
        return size;
    }  

    /**
     * @Description get Chromosomes
     * @return
     */
    public int[] getGene() {
        return gene;
    }

    /**
     * @Description set Chromosomes
     * @param gene
     */
    public void setGene(int[] gene) {
        this.gene = gene;
    }

    /**
     * @Description get fitness
     * @return
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * @Description set fitness
     * @param fitness
     * @return
     */
    public Chromosome setFitness(double fitness) {
        this.fitness = fitness;
        return this;
    }

    /**
     * @Description get probablity of Chromosome that being selected
     * @return
     */
    public double getProb() {
        return prob;
    }

    /**
     * @Description set probablity of Chromosome that being selected
     * @param prob
     */
    public void setProb(double prob) {
        this.prob = prob;
    }

	
	/* override the compareTo for Chromosome
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Chromosome o) {
	    if (o == this) return 0;
	    return new Double(function).compareTo(new Double(o.getFunction()));
	}

	/*  override the toString for Chromosome
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer(80);
	    for (int i = 0; i < gene.length; i++) {
	        sb.append(gene[i]);
	        sb.append(",");
	    }
	    return sb.toString();
	}
}
