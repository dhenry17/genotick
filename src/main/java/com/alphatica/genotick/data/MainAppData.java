package com.alphatica.genotick.data;

import com.alphatica.genotick.genotick.RobotData;
import com.alphatica.genotick.timepoint.TimePoint;

import java.sql.Time;
import java.util.*;

import static java.util.Collections.binarySearch;

public class MainAppData {
    private final Map<DataSetName, DataSet> sets;

    private final List<TimePoint> timePoints;

    public MainAppData() {
        sets = new HashMap<>();
        timePoints = new ArrayList<>();
    }

    public void addDataSet(DataSet set) {
        sets.put(set.getName(), set);
        updateTimePoints(set.getTimePoints());
    }

    private void updateTimePoints(List<TimePoint> newTimePoints) {
        Set<TimePoint> set = new HashSet<>(this.timePoints);
        set.addAll(newTimePoints);
        timePoints.clear();
        timePoints.addAll(set);
        timePoints.sort(TimePoint::compareTo);
    }

    public List<RobotData> prepareRobotDataList(final TimePoint timePoint) {
        List<RobotData> list = Collections.synchronizedList(new ArrayList<>());
        sets.entrySet().parallelStream().forEach((Map.Entry<DataSetName, DataSet> entry) -> {
            RobotData robotData = entry.getValue().getRobotData(timePoint);
            if (!robotData.isEmpty())
                list.add(robotData);

        });
        return list;
    }

    public double getActualChange(DataSetName name, TimePoint timePoint) {
        return sets.get(name).calculateFutureChange(timePoint);
    }

    public TimePoint getFirstTimePoint() {
        if (sets.isEmpty())
            return null;
        TimePoint firstTimePoint = null;
        for (DataSet set : sets.values()) {
            TimePoint first = set.getFirstTimePoint();
            if (firstTimePoint == null) {
                firstTimePoint = first;
            } else if (first.compareTo(firstTimePoint) < 0) {
                firstTimePoint = first;
            }
        }
        return firstTimePoint;
    }

    public TimePoint getLastTimePoint() {
        if (sets.isEmpty())
            return null;
        TimePoint lastTimePoint = null;
        for (DataSet set : sets.values()) {
            TimePoint last = set.getLastTimePoint();
            if (lastTimePoint == null) {
                lastTimePoint = last;
            } else if (last.compareTo(lastTimePoint) > 0) {
                lastTimePoint = last;
            }
        }
        return lastTimePoint;
    }

    public Collection<DataSet> listSets() {
        return sets.values();
    }


    boolean isEmpty() {
        return sets.isEmpty();
    }

    public TimePoint getNextTimePint(TimePoint now) {
        int index = binarySearch(timePoints, now);
        if(index < 0 || index > timePoints.size() - 2) {
            return null;
        } else  {
            return timePoints.get(index+1);
        }
    }

}
