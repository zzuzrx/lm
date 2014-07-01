package com.lm.algorithms.rule.transportor;


import com.lm.algorithms.rule.IRule;
import com.lm.domain.Cell;
import com.lm.domain.Operation;

/**
 * @Description 小车选工件组批运输的调度规则

 * @author:lm

 * @time:2013-11-6 下午05:40:41

 */
public interface ITransportorRule extends IRule {
	 public double calPrio(Operation e,int CurCellID,int NextCellID);
}
