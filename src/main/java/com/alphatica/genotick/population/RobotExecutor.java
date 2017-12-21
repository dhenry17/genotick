package com.alphatica.genotick.population;

import com.alphatica.genotick.genotick.Prediction;
import com.alphatica.genotick.genotick.RobotData;

public interface RobotExecutor {

    Prediction executeRobot(RobotData robotData, Robot robot);
}
