package com.lm.statistic;

import java.util.Random;

import com.lm.util.Constants;
import com.lm.util.RandomHelper;

/**
 * This class is implemented to store the original value of observation
 * */
public class MySummaryStat extends SummaryStat {

    private double[] originalValue;
    private int _index;

    private SummaryStat runTime;
    private String parameter;
    public static String TABLE_TAG;
    static {
        StringBuilder sb = new StringBuilder(100);
        sb.append("Test problems\t");
        for (int i = 0; i < Constants.INSTANCES_PER_PROBLEM; i++) {
            sb.append("Ins").append(i+1).append("\t");
        }
        sb.append("mean_time\tmax\tmin\tmean\tvar");
        TABLE_TAG = sb.toString();
    }
    
    public MySummaryStat() {
        super();
        runTime = new SummaryStat();
        originalValue = new double[Constants.INSTANCES_PER_PROBLEM];
        _index = 0;
    }

    public MySummaryStat(String name) {
        super(name);
        runTime = new SummaryStat();
        originalValue = new double[Constants.INSTANCES_PER_PROBLEM];
        _index = 0;
    }

    @Override
    public void value(double v) {
        super.value(v);
        originalValue[_index++] = v;
    }

    public void addTime(double t) {
        runTime.value(t);
    }

    /** 获取平均运行时间 */
    public double time() {
        return runTime.mean();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(80);
        sb.append(getName()).append("\t");
        for (double value : originalValue) {
            sb.append(value).append("\t");
        }
        sb.append(time()).append("\t").append(max()).append("\t").append(min())
                .append("\t").append(mean()).append("\t").append(variance())
                .append("\t");
        return sb.toString();
    }

    public String DOEString() {
        StringBuilder sb = new StringBuilder(200);
        for (double value : originalValue) {
            sb.append(getName()).append("\t").append(parameter).append("\t")
                    .append(value).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        MySummaryStat stat = new MySummaryStat(Constants.PROBLEM_NAMES[0]);
        Random rand = new Random(-19203);
        for (int i = 0; i < 10; i++) {
            stat.value(RandomHelper.discreteUniform(0, 100, rand));
        }

        // output
        System.out.println(MySummaryStat.TABLE_TAG);
        System.out.println(stat.toString());
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
