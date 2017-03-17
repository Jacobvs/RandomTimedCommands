package com.forgedfactions.randomcommandtimer;

import java.util.ArrayList;
import java.util.List;


public class Command {
    public String name;
    public int min;
    public int max;
    public int id;
    boolean running = false;
    public List<String> commands = new ArrayList<>();

    public Command(String name, int min, int max, List<String> commands){
        this.name = name;
        this.min = min;
        this.max = max;
        this.commands = commands;
    }

    public void setId(int id){this.id = id;}
    public void setRunning(boolean running){this.running = running;}

    public String getName(){return name;}
    public int getMin(){return min;}
    public int getMax(){return max;}
    public boolean getRunning(){return running;}
    public int getId(){return id;}
    public List<String> getCommands(){return commands;}

}