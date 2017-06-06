package com.forgedfactions.randomcommandtimer;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RandomTimer extends JavaPlugin {

    private static final List<Command> commandList = new ArrayList<>(); //holds command objects
    private final List<String> names = new ArrayList<>();
    String VERSION = "3.2.1";

    public void onEnable() {
        this.saveDefaultConfig(); //creates config
        registerCommands(); //creates and adds command objects
        Bukkit.getServer().getConsoleSender().sendMessage("~~~~~~~~~~~~~~~~[RCT]~~~~~~~~~~~~~~~~");
        Bukkit.getServer().getConsoleSender().sendMessage("RandomTimedCommands is now enabled!");
        Bukkit.getServer().getConsoleSender().sendMessage("Version " + VERSION);
        Bukkit.getServer().getConsoleSender().sendMessage("Developed by play.forgedfactions.com");
        Bukkit.getServer().getConsoleSender().sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public void onDisable() {
        saveConfig();
        terminateCommands();
        Bukkit.getServer().getConsoleSender().sendMessage("~~~~~~~~~~~~~~~~[RCT]~~~~~~~~~~~~~~~~");
        Bukkit.getServer().getConsoleSender().sendMessage("RandomTimedCommands is now disabled");
        Bukkit.getServer().getConsoleSender().sendMessage("Version " + VERSION);
        Bukkit.getServer().getConsoleSender().sendMessage("Developed by play.forgedfactions.com");
        Bukkit.getServer().getConsoleSender().sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rct")) { //checking command
            if (args.length > 0) {  //checking if player sends command && has an argument
                if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("rct.admin")) {
                        saveConfig();
                        reloadConfig(); //reload config file from disk
                        registerCommands(); //recreate command objects
                        Bukkit.getPluginManager().disablePlugin(this);
                        Bukkit.getPluginManager().enablePlugin(this);
                        sender.sendMessage(ChatColor.GREEN.toString() + "[RCT] RCT was successfully reloaded!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                    }
                } else if (args[0].equalsIgnoreCase("help")) {
                    if (sender.hasPermission("rct.admin")) {
                        sendHelp(sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                    }
                } else if (args[0].equalsIgnoreCase("list")) {
                    if (sender.hasPermission("rct.admin")) {
                        listCommands(sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                    }
                } else if (args[0].equalsIgnoreCase("stopall")) {
                    if (sender.hasPermission("rct.admin")) {
                        terminateCommands();
                        sender.sendMessage(ChatColor.GREEN.toString() + "[RCT] all commands were successfully terminated!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                    }
                } else if (names.contains(args[0])) { //checks if object name is in config - as per register commands method
                    if (getIndex(args[0]) != -1) {
                        int index = getIndex(args[0]); //saves index of command in commandList
                        if (args.length > 1) {
                            if (args[1].equalsIgnoreCase("start") && index != -1) { //checks if command is start as well as if its in the list
                                if (sender.hasPermission("rct.admin")) {
                                    startCommand(sender, args, index);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                                }
                            } else if (args[1].equalsIgnoreCase("stop") && index != -1) { //stops command
                                if (sender.hasPermission("rct.admin")) {
                                    stopCommand(sender, args, index);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                                }
                            } else if (args[1].equalsIgnoreCase("execute")) {
                                if (sender.hasPermission("rct.admin")) {
                                    executeCommand(sender, args, index);
                                } else {
                                    sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                                }
                            } else if (args[1].equalsIgnoreCase("time")) {
                                if (sender.hasPermission("rct.admin") || sender.hasPermission("rct.time")) {
                                    sender.sendMessage(commandList.get(index).getTime() + (commandList.get(index).getRand() - commandList.get(index).getCycles()) + " seconds.");
                                } else {
                                    sender.sendMessage(ChatColor.RED + "[RCT] No permission to execute this command!");
                                }
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED.toString() + "[RCT] '" + args[0] + "' is not a valid command!");
                            sender.sendMessage(ChatColor.RED.toString() + "[RCT] Please use 'start', 'stop', 'execute', or 'time'");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED.toString() + "[RCT] '" + args[0] + "' was not found in registered commands!");
                    }
                } else {
                    sendHelp(sender);
                }
            }
            return true;
        }
        return false;
    }

    private void startCommand(CommandSender sender, String[] args, int index) {
        if (commandList.get(index).getRunning()) { //checks to see if command is already running
            sender.sendMessage(ChatColor.RED.toString() + "[RTC] '" + args[0] + "' is already running!");
        } else {
            commandList.get(index).setRand(commandList.get(index).getMin() + (int) (Math.random() * ((commandList.get(index).getMax() - commandList.get(index).getMin()) + 1))); //sets first random delay
            commandList.get(index).setRunning(true); //sets running to true
            commandList.get(index).setId(Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() { //schedules repeating task
                @Override
                public void run() {
                    if (commandList.get(index).getCycles() >= commandList.get(index).getRand()) { //waits for delay
                        runCommands(args, index);
                        commandList.get(index).setRand(commandList.get(index).getMin() + (int) (Math.random() * ((commandList.get(index).getMax() - commandList.get(index).getMin()) + 1))); //gets new random delay
                        commandList.get(index).setCycles(0);
                    }
                    commandList.get(index).setCycles(commandList.get(index).getCycles() + 1); //adds cycles + 1
                }
            }, 20, 20)); //runnable executes once per second / 20 ticks
            sender.sendMessage(ChatColor.GREEN.toString() + "[RCT] '" + args[0] + "' was successfully started! '" + commandList.get(index).getRand() + "' seconds until next execution.");
        }
    }

    private void stopCommand(CommandSender sender, String[] args, int index) {
        if (commandList.get(index).getRunning()) { //checks to make sure its running
            Bukkit.getScheduler().cancelTask(commandList.get(index).getId()); //stops task from id set earlier
            commandList.get(index).setRunning(false); //sets running to false
            sender.sendMessage(ChatColor.GREEN.toString() + "[RCT] '" + args[0] + "' was successfully stopped!");
        } else {  //various warnings to player below
            sender.sendMessage(ChatColor.RED.toString() + "[RCT] '" + args[0] + "' is not currently running. \nUse '/rct " + args[0] + " start' to start running the command.");
        }
    }

    private void terminateCommands() {
        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).getRunning()) {
                Bukkit.getScheduler().cancelTask(commandList.get(i).getId());
                commandList.get(i).setRunning(false);
            }
        }
    }

    private void executeCommand(CommandSender sender, String[] args, int index) {
        commandList.get(index).setRand(commandList.get(index).getMin() + (int) (Math.random() * ((commandList.get(index).getMax() - commandList.get(index).getMin()) + 1))); //gets new random delay
        commandList.get(index).setCycles(0);
        runCommands(args, index);
        sender.sendMessage(ChatColor.GREEN + "[RCT] '" + args[0] + "' was successfully executed!");
    }

    private void runCommands(String[] args, int index) {
        for (int i = 0; i < commandList.get(index).getCommands().size(); i++) { //executes all commands in list
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandList.get(index).getCommands().get(i)); //dispatch commands
        }
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN.toString() + args[0] + "' was successfully executed! '" + commandList.get(index).getRand() + "' seconds until next execution.");
    }

    private void listCommands(CommandSender sender) {
        sender.sendMessage("~~~~~~~~~~~[RCT Commands]~~~~~~~~~~~~");
        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).getRunning()) {
                sender.sendMessage("* " + commandList.get(i).getName() + "[Running]");
            } else {
                sender.sendMessage("* " + commandList.get(i).getName() + "[Stopped]");
            }
        }
        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    private void registerCommands() {
        for (int i = 0; i < commandList.size(); i++) { //stops all running commands
            if (commandList.get(i).getRunning()) {
                Bukkit.getScheduler().cancelTask(commandList.get(i).getId());
            }
        }
        commandList.clear(); //clears any previous commands
        Iterator var2 = this.getConfig().getConfigurationSection("schedule").getKeys(false).iterator(); //goes through commands
        while (var2.hasNext()) {
            final String key = (String) var2.next();
            names.add(key); //adds command to reference list
            String time = this.getConfig().getString("schedule." + key + ".time");
            int min = this.getConfig().getInt("schedule." + key + ".mintime");
            int max = this.getConfig().getInt("schedule." + key + ".maxtime");
            List<String> comms = this.getConfig().getStringList("schedule." + key + ".commands");
            addCommand(new Command(key, time, min, max, comms)); //adds command to commandList
        }
    }

    private static int getIndex(String name) {
        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).getName().equalsIgnoreCase(name))
                return i; //finds index of command in the commandList
        }
        return -1;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("~~~~~~~~~~~~~~~[RCT Help]~~~~~~~~~~~~~~~~");
        sender.sendMessage("/rct reload");
        sender.sendMessage("- reload the plugin");
        sender.sendMessage("/rct list");
        sender.sendMessage("- lists all registered timed commands");
        sender.sendMessage("/rct stopall");
        sender.sendMessage("- stops all running timed commands");
        sender.sendMessage("/rct <commandname> start/stop/execute/time");
        sender.sendMessage("- start or stop a command");
        sender.sendMessage("- execute a command immediately");
        sender.sendMessage("- display time left until next execution");
        sender.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }


    private static void addCommand(Command com) {
        commandList.add(com); //adds command to list
    }
}
