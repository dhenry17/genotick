package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;
import com.alphatica.genotick.processor.Processor;

public class PercentileOfColumn extends VarVarInstruction {

    @SuppressWarnings("unused")
    private static final long serialVersionUID = -329518949586814597L;

    // This value is replaced before use by mutate()
    protected int percentile = 95;

    @SuppressWarnings("unused")
    public PercentileOfColumn() {
    }

    private PercentileOfColumn(PercentileOfColumn ins) {
        this.setVariable1Argument(ins.getVariable1Argument());
        this.setVariable2Argument(ins.getVariable2Argument());
        this.percentile = ins.percentile;
    }

    @Override
    public void executeOn(Processor processor) {
        processor.execute(this);
    }

    @Override
    public Instruction copy() {
        return new PercentileOfColumn(this);
    }
    
    @Override
    public void mutate(Mutator mutator) {
        super.mutate(mutator);
        percentile = (Math.abs(mutator.getNextInt()) % 100) + 1;
    }

    public int getPercentile() {
        return percentile;
    }
}