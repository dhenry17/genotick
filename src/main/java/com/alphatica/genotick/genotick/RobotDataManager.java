package com.alphatica.genotick.genotick;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alphatica.genotick.data.DataSeries;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.FilterSettings;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.timepoint.TimePoint;

public class RobotDataManager {

    private final MainAppData data;
    private final int maxBars;
    private final List<RobotDataPair> robotDataList;

    RobotDataManager(MainAppData data, int maxBars) {
        this.data = data;
        this.maxBars = maxBars;
        this.robotDataList = Collections.synchronizedList(new ArrayList<>());
    }
    
    List<RobotDataPair> getUpdatedRobotDataList() {
        return robotDataList;
    }
    
    void initDataSetFilters(FilterSettings filterSettings, TimePoint timeBegin, TimePoint timeEnd) {
        data.getDataSets().parallelStream().forEach(dataSet -> {
            int barBegin = dataSet.getNearestBar(timeBegin);
            int barEnd = dataSet.getNearestBar(timeEnd);
            dataSet.setFilterSettings(filterSettings);
            dataSet.updateFilteredOhlcData(barBegin, barEnd);
        });
    }
    
    void update(TimePoint timePoint) {
        robotDataList.clear();
        data.getOriginalDataSets().parallelStream().forEach(dataSet -> {
            int bar = dataSet.getBar(timePoint);
            if (bar >= 0) {
                DataSet reversedDataSet = data.getReversedDataSet(dataSet.getName());
                RobotData originalData = createRobotData(dataSet, bar);
                RobotData reversedData = (reversedDataSet != null) ? createRobotData(reversedDataSet, bar) : null;
                robotDataList.add(new RobotDataPair(originalData, reversedData));
            }
        });
    }
    
    private RobotData createRobotData(DataSet dataSet, int bar) {
        boolean firstBarIsNewest = true;
        boolean useFiltered = true;
        dataSet.updateFilteredOhlcData(bar, bar + 1);
        DataSeries series = dataSet.createOhlcDataSection(bar, maxBars, firstBarIsNewest, useFiltered);
        return RobotData.create(dataSet.getName(), series);
    }
}
