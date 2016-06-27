package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataSetName;

public class DataSetResult {
    private final DataSetName name;
    private double weightUp;
    private double weightOut;
    private int countOut;
    private double weightDown;
    private int countDown;
    private int countUp;

    public DataSetResult(DataSetName name) {
        this.name = name;
    }

    public void addResult(RobotResult robotResult) {
        Double weight = robotResult.getWeight();
        if(weight.isNaN())
            return;
        processWeight(robotResult);
    }

    public DataSetName getName() {
        return name;
    }

    private void processWeight(RobotResult robotResult) {
        if(robotResult.getWeight() > 0) {
            switch (robotResult.getPrediction()) {
                case UP: recordUp(robotResult.getWeight()); break;
                case DOWN: recordDown(robotResult.getWeight()); break;
                case OUT: recordOut(robotResult.getWeight());
            }
        }
        if(robotResult.getWeight() < 0) {
            switch (robotResult.getPrediction()) {
                case UP: recordDown(-robotResult.getWeight()); break;
                case DOWN: recordUp(-robotResult.getWeight()); break;
                case OUT: recordOut(robotResult.getWeight());
            }
        }
    }

    private void recordOut(double weight) {
        weightOut += weight;
        countOut++;
    }

    private void recordDown(double weight) {
        weightDown += weight;
        countDown++;
    }

    private void recordUp(double weight) {
        weightUp += weight;
        countUp++;
    }

    Prediction getCumulativePrediction(double threshold) {
        double direction = weightUp - weightDown;
        double directionAfterThreshold = applyThreshold(direction,threshold);
        if(direction * directionAfterThreshold < 0) {
            return Prediction.OUT;
        }
        return Prediction.getPrediction(directionAfterThreshold);
    }

    private double applyThreshold(double direction, double threshold) {
        if(threshold == 1) {
            return direction;
        }
        double localWeightUp = weightUp;
        double localWeightDown = weightDown;
        if(direction > 0) {
            localWeightUp /= threshold;
        }
        if(direction < 0) {
            localWeightDown /= threshold;
        }
        return localWeightUp - localWeightDown;
    }

}
