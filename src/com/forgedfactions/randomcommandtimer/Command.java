package com.forgedfactions.randomcommandtimer;

import java.util.ArrayList;
import java.util.List;


class Command {
    private final String name, time;
    private final int min, max;
    private int id, cycles, rand;
    private boolean running = false;
    private List<String> commands = new ArrayList<>();

    Command(String name, String time, int min, int max, List<String> commands) { //command constructor
        this.name = name;
        this.time = time;
        this.min = min;
        this.max = max;
        this.commands = commands;
    }

    //setter methods
    void setId(int id) {
        this.id = id;
    } //sets id of command after starting task

    void setRunning(boolean running) {
        this.running = running;
    }

    void setCycles(int cycles) {
        this.cycles = cycles;
    }

    void setRand(int rand) {
        this.rand = rand;
    }

    //getter methods
    String getName() {
        return name;
    }

    String getTime() { return time;}

    int getRand() {
        return rand;
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

    int getId() { return id; }

    int getCycles() {
        return cycles;
    }

    List<String> getCommands() {
        return commands;
    }

}