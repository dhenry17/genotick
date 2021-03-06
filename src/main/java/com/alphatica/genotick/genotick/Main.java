package com.alphatica.genotick.genotick;

import com.alphatica.genotick.data.DataFactory;
import com.alphatica.genotick.data.DataLoader;
import com.alphatica.genotick.data.DataSaver;
import com.alphatica.genotick.data.DataSet;
import com.alphatica.genotick.data.FileSystemDataLoader;
import com.alphatica.genotick.data.FileSystemDataSaver;
import com.alphatica.genotick.data.MainAppData;
import com.alphatica.genotick.data.YahooFixer;
import com.alphatica.genotick.reversal.Reversal;
import com.alphatica.genotick.ui.Parameters;
import com.alphatica.genotick.ui.UserInput;
import com.alphatica.genotick.ui.UserInputOutputFactory;
import com.alphatica.genotick.ui.UserOutput;
import com.alphatica.genotick.utility.TimeCounter;
import com.alphatica.genotick.utility.ParallelTasks;

import java.io.IOException;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class Main {
    public static final String DEFAULT_DATA_DIR = "data";
    private static final String VERSION_STRING = "Genotick version 0.10.7 (copyleft 2017)";
    private ErrorCode error = ErrorCode.NO_ERROR;
    private boolean canContinue = true;
    private UserInput input;
    private UserOutput output;
    private MainInterface.Session session;

    public static void main(String[] args) throws IOException, IllegalAccessException {
        Main main = new Main();
        main.init(args, null);
    }

    public ErrorCode init(String[] args, MainInterface.Session session) throws IOException, IllegalAccessException {
        ParallelTasks.prepareDefaultThreadPool();
        TimeCounter totalRunTime = new TimeCounter("Total Run Time", false);
        this.session = session;
        Parameters parameters = new Parameters(args);
        if (canContinue) {
            initHelp(parameters);
        }
        if (canContinue) {
            initVersionRequest(parameters);
        }
        if (canContinue) {
            initUserIO(parameters);
        }
        if (canContinue) {
            initDrawData(parameters);
        }
        if (canContinue) {
            initShowPopulation(parameters);
        }
        if (canContinue) {
            initShowRobot(parameters);
        }
        if (canContinue) {
            initMerge(parameters);
        }
        if (canContinue) {
            initReverse(parameters);
        }
        if (canContinue) {
            initYahoo(parameters);
        }
        if (canContinue) {
            initSimulation(parameters);
        }
        printError(error, totalRunTime.stop(TimeUnit.SECONDS));
        return error;
    }
    
    private void setError(ErrorCode error) {
        this.error = error;
        this.canContinue = false;
    }

    private void printError(final ErrorCode error, long elapsedSeconds) {
        System.out.println(format("Program finished with error code %s(%d) in %d seconds", error.toString(), error.getValue(), elapsedSeconds));
    }

    private void initHelp(Parameters parameters) {
        if(parameters.getValue("help") != null
                || parameters.getValue("--help") != null
                || parameters.getValue("-h") != null) {
            System.out.print("Displaying version: ");
            System.out.println("    java -jar genotick.jar showVersion");
            System.out.print("Reversing data: ");
            System.out.println("    java -jar genotick.jar reverse=MY_DATA_DIR");
            System.out.print("Inputs from a file: ");
            System.out.println("    java -jar genotick.jar input=file:MY_CONFIG_FILE iterations=X (optional)");
            System.out.print("Output to a file: ");
            System.out.println("    java -jar genotick.jar output=csv");
            System.out.print("Custom output directory for generated files (log, charts, population): ");
            System.out.println("    java -jar genotick.jar outdir=MY_RESULT_DIR");
            System.out.print("Show population: ");
            System.out.println("    java -jar genotick.jar showPopulation=MY_POPULATION_DIR");
            System.out.print("Show robot info: ");
            System.out.println("    java -jar genotick.jar showRobot=MY_POPULATION_DIR\\ROBOT_ID.prg");
            System.out.print("Merge robots: ");
            System.out.println("    java -jar genotick.jar mergeRobots=MY_TARGET_POPULATION_DIR candidateRobots=MY_SOURCE_POPULATIONS_DIR");
            System.out.print("Draw price curves for asset data ");
            System.out.println("    java -jar genotick.jar drawData=MY_DATA_DIR begin=TIMEPOINT end=TIMEPOINT");
            System.out.println("contact:        lukasz.wojtow@gmail.com");
            System.out.println("more info:      genotick.com");

            setError(ErrorCode.NO_ERROR);
        }
    }
    
    private void initVersionRequest(Parameters parameters) {
        if(parameters.getValue("showVersion") != null) {
            System.out.println(Main.VERSION_STRING);
            setError(ErrorCode.NO_ERROR);
        }
    }

    private void initUserIO(Parameters parameters) throws IOException {
        output = UserInputOutputFactory.createUserOutput(parameters);
        if (output == null) {
            setError(ErrorCode.NO_OUTPUT);
            return;
        }
        input = UserInputOutputFactory.createUserInput(parameters, output, session);
        if (input == null) {
            setError(ErrorCode.NO_INPUT);
            return;
        }
    }

    private void initDrawData(Parameters parameters) {
        String dataDirectory = parameters.getValue("drawData");
        if (dataDirectory != null) {
            String beginString = parameters.getValue("begin");
            String endString = parameters.getValue("end");
            DataPrinter.DrawData(output, dataDirectory, beginString, endString);
            setError(ErrorCode.NO_ERROR);
        }
    }
    
    private void initShowRobot(Parameters parameters) {
        String path = parameters.getValue("showRobot");
        if(path != null) {
            try {
                RobotPrinter.printRobot(path);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
            setError(ErrorCode.NO_ERROR);
        }
    }

    private void initShowPopulation(Parameters parameters) {
        String path = parameters.getValue("showPopulation");
        if(path != null) {
            try {
                PopulationPrinter.printPopulation(path);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                System.err.println(e.getMessage());
            }
            setError(ErrorCode.NO_ERROR);
        }
    }

    private void initMerge(Parameters parameters) {
        String destination = parameters.getValue("mergeRobots");
        if(destination != null) {
            String source = parameters.getValue("candidateRobots");
            if(source != null) {
                ErrorCode errorCode = ErrorCode.NO_OUTPUT;
                try {
                    errorCode = Merge.mergePopulations(destination, source);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    output.errorMessage(e.getMessage());
                }
                setError(errorCode);
            } else {
                output.errorMessage("mergeRobots command found but candidateRobots argument is missing.");
                setError(ErrorCode.MISSING_ARGUMENT);
                return;
            }
        }
    }

    private void initYahoo(Parameters parameters) {
        String path = parameters.getValue("fixYahoo");
        if(path != null) {
            YahooFixer yahooFixer = new YahooFixer(path, output);
            yahooFixer.fixFiles();
            setError(ErrorCode.NO_ERROR);
        }
    }

    private void initReverse(Parameters parameters) {
        String dataDirectory = parameters.getValue("reverse");
        if(dataDirectory != null) {
            DataLoader loader = new FileSystemDataLoader(output);
            DataSaver saver = new FileSystemDataSaver(output);
            MainAppData data = loader.loadAll(dataDirectory);
            for (DataSet loadedSet : data.getDataSets()) {
                Reversal reversal = new Reversal(loadedSet);
                if (!reversal.isReversed()) {
                    if (!data.containsDataSet(reversal.getReversedName())) {
                        DataSet reversedSet = reversal.getReversedDataSet();
                        saver.save(reversedSet);
                    }
                }
            }
            setError(ErrorCode.NO_ERROR);
        }
    }

    private void initSimulation(Parameters parameters) throws IllegalAccessException {
        int iterations = 1;
        String iterationsString = parameters.getAndRemoveValue("iterations");
        if (iterationsString != null && !iterationsString.isEmpty()) {
            iterations = Math.max(1, Integer.parseInt(iterationsString));
        }
        if(!parameters.allConsumed()) {
            output.errorMessage("Not all arguments processed: " + parameters.getUnconsumed());
            setError(ErrorCode.UNKNOWN_ARGUMENT);
            return;
        }
        MainSettings settings = input.getSettings();
        MainAppData data = input.getData(settings.dataDirectory);
        generateMissingData(settings, data);
        int simulationIteration = 0;
        while (iterations-- > 0) {
            Simulation simulation = new Simulation(output);
            MainInterface.SessionResult sessionResult = (session != null) ? session.result : null;
            simulation.start(settings, data, sessionResult, ++simulationIteration);
        }
        setError(ErrorCode.NO_ERROR);
    }
    
    private void generateMissingData(MainSettings settings, MainAppData data) {
        if (settings.requireSymmetricalRobots) {
            Collection<DataSet> loadedSets = data.getDataSets();
            DataSet[] loadedSetsCopy = loadedSets.toArray(new DataSet[data.getDataSets().size()]);
            for (DataSet loadedSet : loadedSetsCopy) {
                Reversal reversal = new Reversal(loadedSet);
                if (reversal.addReversedDataSetTo(data)) {
                    if (!settings.dataDirectory.isEmpty()) {
                        DataSaver saver = DataFactory.getDefaultSaver(output);
                        saver.save(reversal.getReversedDataSet());
                    }
                }
            }
        }
    }
}
