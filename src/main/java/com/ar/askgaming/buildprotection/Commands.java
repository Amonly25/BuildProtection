package com.ar.askgaming.buildprotection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class Commands implements TabExecutor {
    
    private Main plugin;
    public Commands(Main main) {
        plugin = main;
    }
        @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        List<String> result = new ArrayList<String>();

        if (args.length == 1) {
            result = new ArrayList<>(Arrays.asList(""));
        }

        return result;
    }
        @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                
        if (sender instanceof Player){
        } else {return true;}

        Player p = (Player) sender;

        if (args.length == 0) {
            sender.sendMessage("Use /protection <subcommand>");
            return true;
        }

        if (args.length == 1) {
            switch (args[0]) {
                case "select":
                    if (!plugin.playersInEditMode.containsKey(p)){
                        new Selection(p,plugin);
                        p.sendMessage("Estas en modo edicion para protecction.");
                        
                    } else {
                        p.sendMessage("Has salido del modo edicion.");
                        plugin.playersInEditMode.get(p).cancel();
                        plugin.playersInEditMode.remove(p);
                        
                    }
                break;
                case "create":
                    if (plugin.playersInEditMode.containsKey(p)){
                        plugin.playersInEditMode.get(p).create();
                    }    
                break;
            
                default:
                    break;
            }
        }
        if (args.length == 2) {
            switch (args[0]) {
                case "select":
                   if (plugin.playersInEditMode.containsKey(p)){
                        try {
                            int i = Integer.parseInt(args[1]);
                            plugin.playersInEditMode.get(p).setByRadius(i, p);
                            p.sendMessage("Has establecido el radio en " + i);
                            
                        } catch (Exception e) {
                            p.sendMessage("Numero invalido.");
                        }
                   }
                    break;
            
                default:
                    break;
            }
        }

        return false;
    }
}
