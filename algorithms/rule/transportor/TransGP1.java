package com.lm.algorithms.rule.transportor;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;
import com.lm.domain.Operation;
import com.lm.algorithms.rule.GPRuleBase;
/**
 * GP引入的节点
 */
public class TransGP1 extends GPRuleBase implements ITransportorRule {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public double calPrio(Operation e,int CurCellID,int NextCellID){

   	List<Machine> a=e.getProcessMachineList();
   	int MachineIndex=0;
   	while(a.get(MachineIndex).getCellID()!=NextCellID){
   		MachineIndex++;
   	}
   	double PT=e.getProcessingTime(a.get(MachineIndex));
    double DD=e.getDueDate();
    double RT=e.getRelDate();
    return 
    		Div( 
    				Add(
    						Mul (
    							 Sub (
    									Div (Div (RT, RT), PT)
    								,
    									Sub( RT ,DD)
    								)
    			                ,			Mul (
    												Mul( PT ,DD)
    											,	
    												Sub( 0.7222472440182017, 0.7222472440182017)
    											)
    							)
    				        
    					,
    						Div( 
    							Add( 
    								 Add( DD ,PT)
    								,
    								 Sub( PT ,RT)
    								)
    							, 
    							Sub(
    								 Sub (PT ,DD) 
    								,
    								 Sub (0.7222472440182017 ,PT)
    								)
    						)
    					)
    				 
    			    ,	 
    				Sub (
    					 Mul( 
    						 Sub(
    							 Div(
    								 Add( DD, PT) 
    								,
    								 Add( DD ,0.7222472440182017)
    								) 
    							,
    							 Div (DD ,DD)
    							)
    						, Add(
    							  Sub( PT ,PT)
    							  ,
    							  Mul( RT ,RT)
    							 )
    						)
    					, 
    					 Mul(
    			     		 Mul(
    						      Sub(DD, PT)
    							, 
    							  Div (PT ,PT)
    							) 
    						,
    						 Sub(
    						      Add (RT ,PT)
    							,
    							  Sub (PT ,PT)
    							)
    					    )
    					)
    				)
;
  }
}
