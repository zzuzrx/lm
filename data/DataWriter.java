package com.lm.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.lm.util.Constants;

public class DataWriter {

    private List<MachineInfo> machines;
    private int stageNo;

    public DataWriter(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        machines = new ArrayList<MachineInfo>();
        String line = br.readLine();
        stageNo = 0;
        while (line != null) {
            if (line.startsWith("#")) {
                line = br.readLine();
                continue;
            }

            if (line.isEmpty()) {
                line = br.readLine();
                continue;
            }

            String[] seq = line.split("\\s+");
            for (int i = 0; i < seq.length; i += 2) {
                int capaticy = Integer.parseInt(seq[i]);
                machines.add(new MachineInfo(seq[i + 1], stageNo + 1, capaticy));
            }
            line = br.readLine();
            stageNo++;
        }
        br.close();
    }

    public void writeMachine(String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write("# machineName stage machineType capacity");
        bw.newLine();
        for (MachineInfo m : machines) {
            String type = "";
            if (m.capaticy > 1) type = "batching";
            else type = "machining";
            bw.write(m.getName() + " " + m.stage + " " + type + " "
                    + m.getCapaticy());
            bw.newLine();
        }
        bw.close();
    }

    public void writeMetaMachine() throws IOException {
        String filename = "data/machine";
        int[] stage = { 3, 5, 7, 9, 11, 13 };
        for (int i = 0; i < stage.length; i++) {
            Random rand = new Random(System.currentTimeMillis());
            for (int j = 0; j < 3; j++) {// 每个阶段产生3个sample
                BufferedWriter bw = new BufferedWriter(new FileWriter(filename
                        + (i * 3 + j + 1)));
                int machineIndex = 1;
                for (int stageCount = 0; stageCount < stage[i]; stageCount++) {
                    int machineNum = 2 + rand.nextInt(3);// [2,4]
                    if (stageCount == stage[i] / 2) {// 批处理机
                        for (int k = 0; k < machineNum; k++) {
                            int capacity = 2 + rand.nextInt(4);// [2,5]
                            bw.write(capacity + " M" + machineIndex + " ");
                            machineIndex++;
                        }
                    }
                    else {// 单处理机
                        for (int k = 0; k < machineNum; k++) {
                            bw.write("1 M" + machineIndex + " ");
                            machineIndex++;
                        }
                    }
                    bw.newLine();
                }
                bw.close();
            }
        }
    }

    public void writeJob(String filename, int jobNum) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write("#[j] jobName\n");
        bw.write("#[o] M1_setup M1_proc M2_setup M2_proc\n");
        bw.write("#[o] M3_setup M3_proc ...\n");
        bw.write("#[p] ArrivalTime DueDate weight\n");
        bw.newLine();

        int machineNum = machines.size();
        for (int i = 0; i < jobNum; i++) {
            bw.write("[j] job" + (i + 1));
            bw.newLine();
            Random rand = new Random(System.currentTimeMillis());
            int totalProcessTime = 0;
            for (int j = 0; j < stageNo; j++) {
                bw.write("[o]");
                int count = 0, total = 0;
                for (int k = 0; k < machineNum; k++) {
                    int setupTime, processTime;
                    if (machines.get(k).getStage() == (j + 1)) {// 机器属于该stage
                        if (machines.get(k).getCapaticy() > 1) {// 批处理机
                            setupTime = 10 + rand.nextInt(26);// [10,35]
                            processTime = rand.nextInt(101) + 100;// [100,200]
                        }
                        else {// 单处理机
                            setupTime = 5 + rand.nextInt(11);// [5,10]
                            processTime = 1 + rand.nextInt(30);// [1,30]
                        }
                        total += processTime;
                        count++;
                    }
                    else {
                        setupTime = processTime = 0;
                    }
                    bw.write(" " + setupTime + " " + processTime);
                }
                totalProcessTime += total / count;
                bw.newLine();
            }
            int arrivalTime = rand.nextInt(50);
            long duedate = Math.round((arrivalTime + totalProcessTime)
                    * Constants.DUE_FACTOR_DEFAULT);
            double weight = rand.nextDouble() * 0.5 + 0.5;
            java.text.NumberFormat formater = java.text.DecimalFormat
                    .getInstance();
            formater.setMaximumFractionDigits(2);
            formater.setMinimumFractionDigits(2);
            bw.write("[p] " + arrivalTime + " " + duedate + " "
                    + formater.format(weight) + "\n");
            bw.newLine();
        }
        bw.close();
    }

    public static void main(String[] args) throws IOException {
        // dataWriter.writeMetaMachine();
        int[] jobNum = new int[36];
        for (int i = 0; i < 36; i++) {
            jobNum[i] = 10 + i * 5;
        }
        for (int caseNo = 1; caseNo <= jobNum.length; caseNo++) {
            DataWriter dataWriter = new DataWriter("data/machine"
                    + ((caseNo + 1) / 2));
            dataWriter.writeMachine("data/case" + caseNo + "/machine.m");
            dataWriter.writeJob("data/case" + caseNo + "/job.j",
                    jobNum[caseNo - 1]);

        }
    }
}

class MachineInfo {
    public MachineInfo(String name, int stage, int capaticy) {
        super();
        this.name = name;
        this.stage = stage;
        this.capaticy = capaticy;
    }

    String name;
    int stage;
    int capaticy;

    public String getName() {
        return name;
    }

    public int getStage() {
        return stage;
    }

    public int getCapaticy() {
        return capaticy;
    }
}
