package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.timepoint.TimePoint;
import com.alphatica.genotick.ui.UserOutput;

import java.lang.reflect.Field;

public class MainSettings {

    public TimePoint startTimePoint = new TimePoint(0);
    public TimePoint endTimePoint = new TimePoint(Long.MAX_VALUE);
    public String populationDAO = "";
    public boolean performTraining = true;
    public String dataSettings = Main.DEFAULT_DATA_DIR;
    public int populationDesiredSize = 1_000;
    public int processorInstructionLimit = 256;
    public double maximumDeathByAge = 0.01;
    public double maximumDeathByWeight = 0.1;
    public double probabilityOfDeathByAge = 0.5;
    public double probabilityOfDeathByWeight = 0.5;
    public double inheritedChildWeight = 0;
    public int dataMaximumOffset = 256;
    public int protectRobotsUntilOutcomes = 100;
    public double newInstructionProbability = 0.01;
    public double instructionMutationProbability = 0.01;
    public double skipInstructionProbability = 0.01;
    public long minimumOutcomesToAllowBreeding = 50;
    public long minimumOutcomesBetweenBreeding = 50;
    public boolean killNonPredictingRobots = true;
    public double randomRobotsAtEachUpdate = 0.02;
    public double protectBestRobots = 0.02;
    public boolean requireSymmetricalRobots = true;
    public double resultThreshold = 1;
    public int ignoreColumns = 0;

    private MainSettings() {
        /* Empty */
    }
    public static MainSettings getSettings() {
        return new MainSettings();
    }

    public String getString(UserOutput output) {
        StringBuilder sb = new StringBuilder();
        Field [] fields = this.getClass().getDeclaredFields();
        for(Field field: fields) {

            try {
                sb.append(field.getName()).append(" ").append(field.get(this)).append("\n");
            } catch (IllegalAccessException e) {
                output.errorMessage("Unable to print field " + field.getName());
            }
        }
        return sb.toString();
    }

    @SuppressWarnings("WeakerAccess")
    public void validateTimePoints(MainAppData data) {
        TimePoint first = data.getFirstTimePoint();
        TimePoint last = data.getLastTimePoint();
        if(startTimePoint.compareTo(first) < 0) {
            startTimePoint = first;
        }
        if(endTimePoint.compareTo(last) > 0) {
            endTimePoint = last;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void validate() {
        ensure(startTimePoint.compareTo(endTimePoint) <= 0,
                "End Time Point must be higher or equal Start Time Point");
        ensure(populationDesiredSize > 0, greaterThanZeroString("Population desired size"));
        ensure(dataMaximumOffset > 0, greaterThanZeroString("Data Maximum Offset"));
        ensure(processorInstructionLimit > 0, greaterThanZeroString("Processor Instruction Limit"));
        ensure(checkZeroToOne(maximumDeathByAge), zeroToOneString("Maximum Death by Age"));
        ensure(checkZeroToOne(maximumDeathByWeight), zeroToOneString("Maximum Death by Weight"));
        ensure(checkZeroToOne(probabilityOfDeathByAge), zeroToOneString("Probability Death by Age"));
        ensure(checkZeroToOne(inheritedChildWeight), zeroToOneString("Inherited Child's Weight"));
        ensure(protectRobotsUntilOutcomes >= 0, atLeastZeroString("Protect Robots until Outcomes"));
        ensure(checkZeroToOne(newInstructionProbability), zeroToOneString("New Instruction Probability"));
        ensure(checkZeroToOne(instructionMutationProbability), zeroToOneString("Instruction Mutation Probability"));
        ensure(checkZeroToOne(skipInstructionProbability), zeroToOneString("Skip Instruction Probability"));
        ensure(minimumOutcomesToAllowBreeding >= 0, atLeastZeroString("Minimum outcomes to allow breeding"));
        ensure(minimumOutcomesBetweenBreeding >= 0, atLeastZeroString("Minimum outcomes between breeding"));
        ensure(randomRobotsAtEachUpdate >=0, zeroToOneString("Random Robots at Each Update"));
        ensure(protectBestRobots >= 0, zeroToOneString("Protect Best Robots"));
        ensure(resultThreshold >= 1,atLeastOneString("Result threshold"));
        ensure(ignoreColumns >= 0, atLeastZeroString("Ignore columns"));

    }
    private String atLeastZeroString(String s) {
        return s + " must be at least 0";
    }
    private String zeroToOneString(String s) {
        return s + " must be between 0.0 and 1.0";
    }
    private String greaterThanZeroString(String s) {
        return s + " must be greater than 0";
    }
    private String atLeastOneString(String s) {return s + " must be greater than 1";}
    private boolean checkZeroToOne(double value) {
        return value >= 0 && value <= 1;
    }

    private void ensure(boolean condition, String message) {
        if(!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
