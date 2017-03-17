package com.forgedfactions.randomcommandtimer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomTimer extends JavaPlugin {

    public static List<Command> commandList = new ArrayList<>();
    String name = "";
    int min = 0, max = 0;
    List<String> comms = new ArrayList<>();
    List<String> names = new ArrayList<>();


    public void onEnable() {
        this.saveDefaultConfig();
        registerCommands();
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("rct")) {
            Player p = (Player) sender;
            if (sender instanceof Player && args != null) {
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    registerCommands();
                } else if (names.contains(args[0])) {
                    int index = getIndex(args[0]);
                    if (args[1] == "start" && index != -1) {
                        final int[] rand = {(int) (Math.random() * commandList.get(index).getMax()) + commandList.get(index).getMin()};
                        commandList.get(index).setRunning(true);
                        commandList.get(index).id = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                            @Override
                            public void run() {
                                int secs = 0;
                                if (secs >= rand[0]) {
                                    for (int i = 0; i < commandList.get(index).getCommands().size(); i++) {
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandList.get(index).getCommands().get(i));
                                    }
                                    rand[0] = (int) (Math.random() * commandList.get(index).getMax()) + commandList.get(index).getMin();
                                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[RCT] '" + args[0] + "' was successfully executed! '" + rand + "' seconds until next execution.");
                                    secs = 0;
                                }
                                secs++;
                            }
                        }, 20, 20);
                        p.sendMessage(ChatColor.GREEN.toString() + "[RCT] '" + args[0] + "' was successfully started!");
                    } else if (args[1] == "stop" && index != -1) {
                        if (commandList.get(getIndex(args[0])).getRunning() == true) {
                            Bukkit.getScheduler().cancelTask(commandList.get(getIndex(args[0])).getId());
                            commandList.get(getIndex(args[0])).setRunning(false);
                            p.sendMessage(ChatColor.GREEN.toString() + "[RCT] '" + args[0] + "' was successfully stopped!");
                        } else {
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

    public void registerCommands() {
        commandList.clear();
        Iterator var2 = this.getConfig().getConfigurationSection("schedule").getKeys(false).iterator();
        while (var2.hasNext()) {
            final String key = (String) var2.next();
            name = key;
            names.add(key);
            if (isInt(this.getConfig().getInt("schedule." + key + ".mintime")) && isInt(this.getConfig().getInt("schedule." + key + "maxtime"))) {
                min = this.getConfig().getInt("schedule." + key + ".mintime");
                max = this.getConfig().getInt("schedule." + key + "maxtime");
                comms = this.getConfig().getStringList("schedule." + key + ".commands");
                addCommand(new Command(name, min, max, comms));
            } else {
                this.getServer().getConsoleSender().sendMessage(ChatColor.RED + "[RCT] Failed to set up command(s). Range is not a valid number.");
                break;
            }
        }
    }

    public static int getIndex(String name) {
        for (int i = 0; i < commandList.size(); i++) {
            if (commandList.get(i).getName().equalsIgnoreCase(name)) return i;
        }
        return -1;
    }


    public static void addCommand(Command com) {
        commandList.add(com);
    }


    private boolean isInt(int num) {
        try {
            Integer.parseInt(num + "");
            return true;
        } catch (Exception var3) {
            return false;
        }
    }
}
