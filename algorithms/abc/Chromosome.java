package com.lm.algorithms.abc;

import java.util.ArrayList;

import com.lm.util.Constants;

/**
 * @Description abc方法的Chromosom类

 * @author:lm

 * @time:2014-4-13 上午09:37:04

 */
public class Chromosome implements Comparable<Chromosome>, Cloneable { 
/***************************属性域***********************************************************************/
	/** the MachineSegment **/
	public int[][] MachineSegment;
	/** the VehicleSegment **/
	public int[][] VehicleSegment;
	/**	the sequence of parts for buffer of each cell**/
	public ArrayList<Integer>[][] IntercellPartSequences;
    /** size of MachineSegment**/
    private int MachineSize;
    /** size of VehicleSegment**/
    private int VehicleSize;
    /** fitness of Chromosome*/
    private double fitness;
    /** function values of Chromosome*/
//    private double function;
    private double functions[];
	/** probability of Chromosome that being selected*/
    private double prob;
    /** the lifespan of the chromosome**/
    private int    age;
    /** the function values of the last generation**/
    private double LastBest;
	/***************************方法域***********************************************************************/
    /** 
     * @Description constructed function
     * @param size
     * @exception:
     */
    public Chromosome(int msize,int vsize) {
        super();
        this.MachineSize	= msize;
        this.VehicleSize 	= vsize;
        MachineSegment 		= new int[msize+1][];
        VehicleSegment		= new int[vsize+1][];
        LastBest			= -1;
    
        IntercellPartSequences 	= new ArrayList[vsize+1][vsize+1];
        for(int i = 1; i < vsize+1; i++){
			for(int j = 1; j < vsize+1; j++){
				IntercellPartSequences[i][j] = new ArrayList<Integer>();
			}
		}
        
//        function			= -1*Double.MAX_VALUE;
        functions			= new double[3];		// 上上一代、上一代、当前值
        functions[0]		= -1*Double.MAX_VALUE;
        functions[1]		= -1*Double.MAX_VALUE;
        functions[2]		= -1*Double.MAX_VALUE;
    }
    
    public double getLastBest() {
		return LastBest;
	}

	public void setLastBest(double lastBest) {
		LastBest = lastBest;
	}

	/**
     * @Description get function values of Chromosomes
     * @return
     */
    public double getFunction() {
		return functions[2];
	}

	/**
	 * @Description update function values of Chromosomes for current
	 * Also store the function before
	 * @param function
	 */
	public void setFunction(double function) {
		//更新上上一代
		functions[0] = functions[1];
		//更新上一代
		functions[1] = functions[2];
		//更新本次
		functions[2] = function;
	}
    /* (non-Javadoc):clone methods to copy a get function values of Chromosomes
     * @see java.lang.Object#clone()
     * override
     */
    public Chromosome clone() throws CloneNotSupportedException {
        Chromosome cloned = new Chromosome(MachineSize,VehicleSize);
        //cloned.setGene(Arrays.copyOf(cloned.getGene(), size));
        cloned.setMachineSegment(this.getMachineSegment());
        cloned.setVehicleSegment(this.getVehicleSegment());
        cloned.setPartSequence(this.getPartSequence());
        cloned.setFunction(this.getFunction()) ;
        return cloned;
    }

    /**
     * @Description Indicates whether the gene is "equal to" this one.
     * @param o
     * @return
     */
//    @Override
    public boolean equals(Chromosome o){
   	 for (int i = 1; i < o.MachineSegment.length; i++) {
   		 if(MachineSegment[i]!=null){
   			 for(int j = 0;j < o.MachineSegment[i].length; j++) {
   				 if(MachineSegment[i][j]!=o.getMachinegene(i,j)) return false;
   			 }
   		 }
   	 }
   	 for(int i = 1; i < o.VehicleSegment.length;i++) {
   		 if(VehicleSegment[i]!=null){
   			 for(int j = 0;j < o.VehicleSegment[i].length;j++){
   				 if(VehicleSegment[i][j]!=o.getVehicleSegment(i, j)) return false;
   			 }
   		 }
   	 }
   	 for(int i = 1; i< o.IntercellPartSequences.length;i++){
   		 if(IntercellPartSequences[i]!=null){
   			 for(int j = 0;j <o.IntercellPartSequences[i].length;j++){
   				 if(IntercellPartSequences[i][j]!=null){
   					 for(int k = 0;k < o.IntercellPartSequences[i][j].size();k++){
   						 if(IntercellPartSequences[i][j].get(k)!=o.IntercellPartSequences[i][j].get(k)) return false;
   					 }
   				 }
   			 }
   		 }
   	 }
   	 return true;
   }
    /**
     * @Description get gene of particular position on Machine Segment
     * @param index
     * @return
     */
    public int getMachinegene(int GeneIndex, int PriorIndex) {
        if (GeneIndex < 0 || GeneIndex >= MachineSize+1) throw new IllegalArgumentException("out of gene arrays boundary");
        return MachineSegment[GeneIndex][PriorIndex];
    }
    /**
     * @Description get gene of particular position on Vehicle Segment
     * @param index
     * @return
     */
    public int getVehicleSegment(int GeneIndex, int PriorIndex) {
        if (GeneIndex < 0 || GeneIndex >= VehicleSize+1) throw new IllegalArgumentException("out of gene arrays boundary");
        return VehicleSegment[GeneIndex][PriorIndex];
    }
    public ArrayList<Integer> getPartSequence(int SourceIndex, int TargetIndex, ArrayList<Integer> partPrior) {
    	IntercellPartSequences[SourceIndex][TargetIndex]= new ArrayList<Integer>(partPrior); 
    	return IntercellPartSequences[SourceIndex][TargetIndex];
    }

	/**
	 * @Description set gene values of particular position on vehicle segment
	 * @param index
	 * @param PriorArray
	 */
	public void setVehicleSegment(int index, int[] PriorArray) {
		 if (index < 0 || index >= VehicleSize+1) throw new IllegalArgumentException("out of gene arrays boundary");
	     VehicleSegment[index] = PriorArray.clone();
	}
    
    /**
     * @Description set gene values of particular position on machine segment
     * @param index
     * @param PriorArray
     */
    public void setMachineSegment(int index, int[] PriorArray) {
        if (index < 0 || index >= MachineSize+1) throw new IllegalArgumentException("out of gene arrays boundary");
        MachineSegment[index] = PriorArray.clone();
    }

    public void setPartSequence(int SourceIndex, int tarindex,int[] partPrior) {      
    	    IntercellPartSequences[SourceIndex][tarindex] = new ArrayList<Integer>(partPrior.length);
            for(int i = 0;i<partPrior.length;i++){
            	int temp =0;
            	temp = partPrior[i];
            	IntercellPartSequences[SourceIndex][tarindex].add(temp);	

            }
//    	IntercellPartSequences[SourceIndex][TargetIndex]= new String(partPrior2);    	
    }
    /**
     * @Description get size of Chromosomes
     * @return
     */
    public int MachinesegmentSize() {
        return MachineSize;
    }  

    /**
     * @Description get the Prior array size of the gene
     * @return
     */
    public int getGeneSize(int GeneIndex) {
        return MachineSegment[GeneIndex].length;
    }
    
    /**
     * @Description get Chromosomes
     * @return
     */
    public int[][] getMachineSegment() {
        return MachineSegment;
    }
    public int[][] getVehicleSegment() {
        return VehicleSegment;
    }
    public ArrayList<Integer>[][] getPartSequence() {
        return IntercellPartSequences;
    }

    /**
     * @Description set setMachineSegment
     * @param gene
     */
 
    public void setMachineSegment(int[][] gene) {
        this.MachineSegment = gene.clone();
    }
    public void setVehicleSegment(int[][] gene) {
        this.VehicleSegment = gene.clone();
    }
    public void setPartSequence(ArrayList<Integer>[][] gene) {
//    	String[][] gene =new String[][];
    	for(int i = 0; i < gene.length;i++){
    		if(gene[i].length!=0){
    			for(int j =0; j < gene[i].length;j++){
    				if(gene[i][j]!=null){
    					IntercellPartSequences[i][j]= new ArrayList<Integer>();
    					for(int k=0;k<gene[i][j].size();k++){
    						this.IntercellPartSequences[i][j].add(gene[i][j].get(k));
    					}
    				}
    			}
    		}
    	}
//    	for( int i=0;i<gene.length;i++){
//    		
//    		if(gene[i].length!=0){
//    			for(int j=0;j<gene[i].length;j++){
//        				if(gene[i][j]!=null){
//        				IntercellPartSequences[i][j]= new String("");
//    					for(int k=0;k<gene[i][j].length();k++){	
//    						
//    						this.IntercellPartSequences[i][j]+=gene[i][j].charAt(k);
//    					}
//    				
//    				}
//    			}
//    		}	
//    	
//    	}
//        this.IntercellPartSequences =gene.clone() ;
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
	    return new Double(functions[2]).compareTo(new Double(o.getFunction()));
	}

	/*  override the toString for Chromosome
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    StringBuffer sb = new StringBuffer(80);
//	    for (int i = 0; i < MachineSize.length; i++) {
//	        sb.append(MachineSize[i]);
//	        sb.append(",");
//	    }
	    return sb.toString();
	}

	public int getMachineSize() {
		return MachineSize;
	}

	public int getVehicleSize() {
		return VehicleSize;
	}
	
	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getRate() {
		// TODO Auto-generated method stub
		if(functions[0]== -1*Double.MAX_VALUE){
			return 1;
		}
		else return functions[0]/functions[1];
	}
}
