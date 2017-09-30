package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.Column;
import com.alphatica.genotick.data.DataSetName;
import com.alphatica.genotick.processor.NotEnoughDataException;

import java.util.ArrayList;
import java.util.List;

public class RobotData {
    private final List<double[]> ohlcColumnsOfData;
    private final DataSetName name;
    private final double lastPriceChange;

    public static RobotData createData(List<double[]> priceData, DataSetName name) {
        return new RobotData(priceData, name);
    }

    public static RobotData createEmptyData(DataSetName name) {
        List<double[]> list = new ArrayList<>();
        list.add(new double[0]);
        return createData(list, name);
    }

    private RobotData(List<double[]> ohlcColumnsOfData, DataSetName name) {
        this.ohlcColumnsOfData = ohlcColumnsOfData;
        this.name = name;
        this.lastPriceChange = calculateLastPriceChange();
    }

    public DataSetName getName() {
        return name;
    }

    public double getPriceData(int column, int offset) {
        if (offset >= ohlcColumnsOfData.get(column).length)
            throw new NotEnoughDataException();
        else
            return ohlcColumnsOfData.get(column)[offset];
    }
    
    public int getColumnCount() {
        return ohlcColumnsOfData.size();
    }

    private int getAssetDataLength(int column) {
        return ohlcColumnsOfData.get(column).length;
    }

    public boolean isEmpty() {
        return getAssetDataLength(Column.OHLCV.OPEN) == 0;
    }
    
    double getLastPriceOpen() {
        return ohlcColumnsOfData.get(Column.OHLCV.OPEN)[0];
    }

    public double getLastPriceChange() {
        return lastPriceChange;
    }

    private double calculateLastPriceChange() {
        if (getAssetDataLength(Column.OHLCV.OPEN) < 2) {
            return 0.0;
        }
        final double currentOpen = ohlcColumnsOfData.get(Column.OHLCV.OPEN)[0];
        final double previousOpen = ohlcColumnsOfData.get(Column.OHLCV.OPEN)[1];
        return currentOpen - previousOpen;
    }
}
