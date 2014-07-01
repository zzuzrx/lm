package com.lm.algorithms.rule.transportor;


import com.lm.domain.Operation;
/** FIFO : RealDate*/
public class TransFIFO implements ITransportorRule {
    
	public double calPrio(Operation e,int CurCellID,int NextCellID){
    	return -e.getRelDate();
    }
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
