package com.lm.algorithms.rule.machine;

import java.util.List;

import com.lm.domain.Entity;
import com.lm.domain.Machine;
import com.lm.util.Timer;

/**
 * Slack = due date - current time - remaining processing time smallest first
 */
public class MachineSL implements IMachineRule {

//    @Override
    public double calPrio(Entity e) {
        // Slack = due date - current time - remaining processing time smallest first
        return e.getDueDate() - Timer.currentTime() - e.getRemainingTime();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

}
