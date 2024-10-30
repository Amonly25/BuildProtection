package com.ar.askgaming.buildprotection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Managers.ProtectionFlags;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Commands implements TabExecutor {
    
    private final Main plugin;
    public Commands(Main main) {
        plugin = main;
    }
        @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            return List.of("select", "create", "list","set","tp","info","show","add","remove","message","delete");
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "set":
                    for (ProtectionFlags.FlagType flag : ProtectionFlags.FlagType.values()) {
                        list.add(flag.toString().toLowerCase());
                    }
                    //Limit to permissions
                    break;
                case "add":
                    for (Player p : Bukkit.getOnlinePlayers()){
                        list.add(p.getName());
                    }
                    break;    
                case "remove":
                    for (Player p : Bukkit.getOnlinePlayers()){
                        list.add(p.getName());
                    }
                    break; 
                default:
                    break;
            }
        }
        return list;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                
        if (sender instanceof Player){
        } else {return true;}

        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage(plugin.getDataHandler().getLang("help", p));
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "select":
                handleSelectCommand(p, args);
                break;
            case "info":
                handleInfoCommand(p, args);
                break;
            case "list":
                handleListCommand(p, args);
                break;
            case "show":
                handleShowCommand(p, args);
                break;
            case "set":
                handleSetFlagCommand(p, args);
                break;
            case "tp":
                handleTpCommand(p, args);
                break;
            case "add":
                handleAddRemovePlayerCommand(p, args);
                break;
            case "remove":
                handleAddRemovePlayerCommand(p, args);
                break;
            case "message":
                handleMessageCommand(p, args);
                break;
            case "help":
                p.sendMessage(plugin.getDataHandler().getLang("help", p));
                break;
            case "create":
                handleCreateCommand(p, args);
                break;
            case "delete":
                 handleDeleteCommand(p, args);
                break;
            default:
                p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
                break;
        }
        return false;
    }

    private void handleDeleteCommand(Player p, String[] args) {
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
            if (prote != null){
                if (prote.isAdminProtection(p)){
                    plugin.getProtectionsManager().deleteProtection(prote);
                    p.sendMessage(plugin.getDataHandler().getLang("prote.delete", p));
                } else {
                    p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
                }
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));

            }
        }
   
    private void handleMessageCommand(Player p, String[] args) {

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());

        if (prote == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }

        if (!prote.isAdminProtection(p)){
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            return;
        }
    
        if (args.length < 2) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        if (args[1].length() > 32){
            p.sendMessage(plugin.getDataHandler().getLang("prote.message_long", p));
            return;

        }
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        p.sendMessage(plugin.getDataHandler().getLang("prote.message_set", p).replace("%message%", message.toString().trim()));
        prote.setMessage(message.toString().trim());
    }
   
    private void handleSetFlagCommand(Player p, String[] args) {

        if (args.length != 3) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        if (!plugin.getProtectionFlags().isValid(args[1])) {
            p.sendMessage(plugin.getDataHandler().getLang("flags.invalid", p));
            return;
        }

        Protection proteByLoc = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());

        if (proteByLoc == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }

        boolean value = Boolean.parseBoolean(args[2]);

        if (proteByLoc.isAdminProtection(p)){
            if (plugin.getProtectionFlags().hasFlagPermission(args[1].toLowerCase(), p)){
                proteByLoc.setFlag(ProtectionFlags.FlagType.valueOf(args[1].toUpperCase()), value);
                
                p.sendMessage(plugin.getDataHandler().getLang("prote.flag_set", p).replace("%flag%", args[1]).replace("%value%", String.valueOf(value)));
                
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("flags.no_perm", p));
            }            
        } else {
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
        }
    }

    private void handleCreateCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        if (plugin.getProtectionsManager().getProtectionsByWorld(p.getWorld()).containsKey(args[1])){
            p.sendMessage(plugin.getDataHandler().getLang("prote.exists", p));
        } else {
            if (plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).create(args[1]);
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("select.must", p));
            }
        }
    }

    private void handleSelectCommand(Player p, String[] args) {

        if (args.length == 1) {
            if (!plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){
                new Selection(p,plugin);  
                p.sendMessage(plugin.getDataHandler().getLang("select.enter", p));          
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("select.exit", p));
                plugin.getProtectionsManager().getPlayersInEditMode().remove(p);    
            }

        } else if (args.length == 2) {
            if (plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){
                try {
                    int i = Integer.parseInt(args[1]);
                    plugin.getProtectionsManager().getPlayersInEditMode().get(p).setByRadius(i);
                    
                } catch (Exception e) {
                    p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
                }
            }
        }
    }

    private void handleTpCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        if (!p.hasPermission("buildprotection.teleport")) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            return;
        }

        Protection prote = plugin.getProtectionsManager().getProtectionByName(args[1],p.getWorld());
        if (prote != null){
            Location l = prote.getCenter();
            if (l != null){
                p.teleport(l);
                //Message?
            }
        }else {
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_exist", p));
        }    
    }

    private void handleShowCommand(Player p, String[] args) {

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (prote != null){
            if (prote.isAdminProtection(p)){
                p.sendMessage(plugin.getDataHandler().getLang("select.show", p));
                plugin.getShowParticles().getShowProtections().add(prote);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    plugin.getShowParticles().getShowProtections().remove(prote);
                }, 100L); // 100 ticks = 5 segundos (1 segundo = 20 ticks)
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            }
        }else {
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
        }    
    }

    private void handleListCommand(Player p, String[] args) {
        if (args.length == 1) {
            // Listar todas las protecciones del jugador
            p.sendMessage(plugin.getDataHandler().getLang("prote.list", p));
            plugin.getProtectionsManager().getProtectionsByWorld(p.getWorld()).forEach((name, prote) -> {
                if (prote.getOwner().equals(p.getUniqueId())){
                    p.sendMessage(name);
                }
            });
            return;
        }
        if (!p.hasPermission("buildprotection.listothers")) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            return;
        }
        if (args[1].equalsIgnoreCase("all")){
            p.sendMessage("Listing protections of world " + p.getWorld().getName());
            plugin.getProtectionsManager().getProtectionsByWorld(p.getWorld()).forEach((name, prote) -> {
                p.sendMessage(name);
            });
        } else {
            //Obtener proteciones de un jugador
            p.sendMessage("Listing protections of player " + args[1]);
            plugin.getProtectionsManager().getAllProtections().forEach((world,map) -> {
                map.forEach((name,prote) -> {
                    if (prote.getOwner().equals(Bukkit.getPlayer(args[1]).getUniqueId())){
                        p.sendMessage(name + " at world: " + world);
                    }
                });
            });
        }
    }

    private void handleInfoCommand(Player p, String[] args) {

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (prote != null){

            p.sendMessage(plugin.getDataHandler().getLang("prote.info.name", p) + prote.getName());
            p.sendMessage(plugin.getDataHandler().getLang("prote.info.owner", p) + prote.getOwnerName());
            p.sendMessage(plugin.getDataHandler().getLang("prote.info.players", p) + prote.getPlayers().toString());
            p.sendMessage(plugin.getDataHandler().getLang("prote.info.message", p) + prote.getMessage());
            
            TextComponent flags = new TextComponent(plugin.getDataHandler().getLang("prote.info.flags",p));
            TextComponent hoverOver = new TextComponent(plugin.getDataHandler().getLang("prote.info.hover",p));
            HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+prote.getSortedFlags().toString().replaceAll("false", ChatColor.RED+"false"+ChatColor.GRAY).replaceAll("true", ChatColor.GREEN+"true"+ChatColor.GRAY)));
                             
            flags.setHoverEvent(he);
            flags.addExtra(hoverOver);

            p.spigot().sendMessage(flags);
        } else {
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
        }
    }

    private void handleAddRemovePlayerCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        Protection proteByLoc = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (proteByLoc == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }
        if (proteByLoc.isAdminProtection(p)){
            if (args[0].equalsIgnoreCase("add")){
                if (proteByLoc.addPlayer(args[1])){
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_added", p).replace("%player%", args[1]));
                } else{
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_no_exist", p).replace("%player%", args[1]));
                }
            } else if (args[0].equalsIgnoreCase("remove")){
                if (proteByLoc.removePlayer(args[1])){
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_removed", p).replace("%player%", args[1]));
                } else {
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_no_exist", p).replace("%player%", args[1]));        
                }
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
            }
        } else {
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
        }
    }
}


