package com.alphatica.genotick.instructions;

import com.alphatica.genotick.mutator.Mutator;

abstract class IntDoubleJumpInstruction extends IntDoubleInstruction implements JumpInstruction {
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 3733708799704002530L;

    private int address;

    IntDoubleJumpInstruction() {
        address = 0;
    }

    @Override
    public int getAddress() {
        return address;
    }

    void setAddress(int address) {
        this.address = address;
    }

    @Override
    public void mutate(Mutator mutator) {
        super.mutate(mutator);
        int minJump = mutator.getMinJumpSize();
        int jumpRange = mutator.getMaxJumpSize() - minJump;
        address = minJump + (mutator.getNextInt() % jumpRange);
    }
}
