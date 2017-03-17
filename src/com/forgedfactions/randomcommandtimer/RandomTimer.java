package com.forgedfactions.randomcommandtimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class RandomTimer extends JavaPlugin {

    private static final List<Command> commandList = new ArrayList<>(); //holds command objects
    private final List<String> names = new ArrayList<>();


    public void onEnable() {
        this.saveDefaultConfig(); //creates config
        registerCommands(); //creates and adds command objects
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rct")) { //checking command
            Player p = (Player) sender;
            if (sender instanceof Player && args != null) {  //checking if player sends command && has an argument
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadConfig(); //reload config file from disk
                    registerCommands(); //recreate command objects
                } else if (names.contains(args[0])) { //checks if object name is in config - as per register commands method
                    int index = getIndex(args[0]); //saves index of command in commandList
                    if (Objects.equals(args[1], "start") && index != -1) { //checks if command is start as well as if its in the list
                        if (commandList.get(index).getRunning()) { //checks to see if command is already running
                            p.sendMessage(ChatColor.RED.toString() + "[RTC] '" + args[0] + "' is already running!");
                        } else {
                            final int[] rand = {(int) (Math.random() * commandList.get(index).getMax()) + commandList.get(index).getMin()}; //sets first random delay
                            commandList.get(index).setRunning(true); //sets running to true
                            commandList.get(index).setId(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() { //schedules repeating task
                                @Override
                                public void run() {
                                    if (commandList.get(index).getCycles() >= rand[0]) { //waits for delay
                                        for (int i = 0; i < commandList.get(index).getCommands().size(); i++) { //executes all commands in list
                                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandList.get(index).getCommands().get(i)); //dispatch commands
                                        }
                                        rand[0] = (int) (Math.random() * commandList.get(index).getMax()) + commandList.get(index).getMin(); //gets new random delay
                                        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[RCT] '" + args[0] + "' was successfully executed! '" + rand[0] + "' seconds until next execution.");
                                        commandList.get(index).setCycles(0);
                                    }
                                    commandList.get(index).setCycles(commandList.get(index).getCycles() + 1); //adds cycles + 1
                                }
                            }, 20, 20)); //runnable executes once per second / 20 ticks
                            p.sendMessage(ChatColor.GREEN.toString() + "[RCT] '" + args[0] + "' was successfully started!");
                        }
                    } else if (Objects.equals(args[1], "stop") && index != -1) { //stops command
                        if (commandList.get(getIndex(args[0])).getRunning()) { //checks to make sure its running
                            Bukkit.getScheduler().cancelTask(commandList.get(getIndex(args[0])).getId()); //stops task from id set earlier
                            commandList.get(getIndex(args[0])).setRunning(false); //sets running to false
                            p.sendMessage(ChatColor.GREEN.toString() + "[RCT] '" + args[0] + "' was successfully stopped!");
                        } else {  //various warnings to player below
                            p.sendMessage(ChatColor.RED.toString() + "[RCT] '" + args[0] + "' is not currently running. \nUse '/rct " + args[0] + " start' to start running the command.");
                        }
                    } else if (index == -1) {
                        p.sendMessage(ChatColor.RED.toString() + "[RCT] '" + args[0] + "' was not found in registered commands!");
                    } else {
                        p.sendMessage(ChatColor.RED.toString() + "[RCT] Please use 'start' or 'stop' to stop the command");
                    }
                } else {
                    p.sendMessage(ChatColor.RED.toString() + "[RCT] '" + args[0] + "' is not a valid command!");
                }
            }
            return true;
        }
        return false;
    }

    private void registerCommands() {
        commandList.clear(); //clears any previous commands
        Iterator var2 = this.getConfig().getConfigurationSection("schedule").getKeys(false).iterator(); //goes through commands
        while (var2.hasNext()) {
            final String key = (String) var2.next();
            names.add(key); //adds command to reference list
            int min = this.getConfig().getInt("schedule." + key + ".mintime");
            int max = this.getConfig().getInt("schedule." + key + "maxtime");
            List<String> comms = this.getConfig().getStringList("schedule." + key + ".commands");
            addCommand(new Command(key, min, max, comms)); //adds command to commandList
        }
    }

    private static int getIndex(String name) {
        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).getName().equalsIgnoreCase(name))
                return i; //finds index of command in the commandList
        }
        return -1;
    }


    private static void addCommand(Command com) {
        commandList.add(com); //adds command to list
    }


}
