package com.lm.algorithms.rule.machine;

import com.lm.domain.Entity;
import com.lm.algorithms.rule.GPRuleBase;
/**
 * GP引入的节点
 */
public class MachineGP8 extends GPRuleBase implements IMachineRule {

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public double calPrio(Entity e) {
    double W= e.getWeight();
    double PT=e.getProcessingTime();
    double AT=e.getArrivalTime();
    double DD=e.getDueDate();
    double RPT=e.getRemainingTime();
    double RT=e.getRelDateTime();
    double OP=e.getOpNumber();
    return 
    		Add(
    				 Add(
    					 Sub(
    						 Sub(
    							 Div(PT,0.5141037958491357)
    							,
    							 Sub(OP, RT)
    							)		
    			   			,
    						 Add(
    							 Mul(AT,AT)
    							,
    							 Add(DD,DD)
    							)
    			    		)
    					,
    					 Add(
    						 Add(
    							 Sub(RPT,PT)
    							,
    							 Add(PT,AT)
    							) 
    			   			,
    						 Div(
    							 Mul(W, W)
    							,
    							 Sub(PT,DD)
    							)
    						)
    			    	) 
    			    ,
    			  	 Sub(
    					 Div(
    						 Sub(
    							 Div(RPT, PT)
    							,
    							 Add(DD,RT)
    							) 
    			    		,
    						 Add(
    							 Mul(RT,PT)
    							,
    							 Add(0.5141037958491357,AT)
    							)
    						) 
    			    	,
    					 Div(
    			    		 Add(
    							 Sub(RPT,RT)
    							,
    							 Mul(0.5141037958491357,AT)
    							) 
    			    		,
    						 Sub(
    							 Add(
    							     OP
    								,
    								 Sub(
    									 Div(OP,OP)
    									,
    									 Div(W,PT)
    									)
    								)
    							,
    							 Div(0.5141037958491357,AT)
    							)
    						)
    			        )
    				)
    		;
    }
}
