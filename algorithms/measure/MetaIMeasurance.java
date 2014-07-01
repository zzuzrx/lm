package com.lm.algorithms.measure;

import com.lm.algorithms.AbstractScheduler;
import com.lm.algorithms.MetaHeuristicScheduler;

/**
 * @Description the interface of measure methods for problems

 * @author:lm

 * @time:2013-11-6 下午05:15:01

 */
public interface MetaIMeasurance {
    public double getMeasurance(MetaHeuristicScheduler scheduler);
}
