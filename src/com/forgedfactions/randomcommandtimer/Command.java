package com.forgedfactions.randomcommandtimer;

import java.util.ArrayList;
import java.util.List;


class Command {
    private final String name;
    private final int min, max;
    private int id, cycles;
    private boolean running = false;
    private List<String> commands = new ArrayList<>();

    Command(String name, int min, int max, List<String> commands) { //command constructor
        this.name = name;
        this.min = min;
        this.max = max;
        this.commands = commands;
    }

    void setId(int id) {
        this.id = id;
    } //sets id of command after starting task

    void setRunning(boolean running) {
        this.running = running;
    } //sets running true/false

    void setCycles(int cycles){
        this.cycles = cycles;
    }

    String getName() {
        return name;
    }

    int getMin() {
        return min;
    }

    int getMax() {
        return max;
    }

    boolean getRunning() {
        return running;
    }

    int getId() {
        return id;
    }

    int getCycles(){
        return cycles;
    }

    List<String> getCommands() {
        return commands;
    }

}