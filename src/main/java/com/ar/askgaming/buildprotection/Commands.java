package com.ar.askgaming.buildprotection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.ar.askgaming.buildprotection.Managers.DataHandler;
import com.ar.askgaming.buildprotection.Managers.ProtectionFlags;
import com.ar.askgaming.buildprotection.Managers.SelectionManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Commands implements TabExecutor {
    
    private final BuildProtection plugin;
    public Commands(BuildProtection main) {
        plugin = main;
        dataHandler = plugin.getDataHandler();
    }

    private List<Player> confirmMessage = new ArrayList<>();

    private boolean confirm(Player p){
        if (confirmMessage.contains(p)){
            confirmMessage.remove(p);
            return true;
        } else {
            p.sendMessage(plugin.getDataHandler().getLang("commands.confirm", p));
            confirmMessage.add(p);
            return false;
        }
    }

    private DataHandler dataHandler;
    private String getLang(String key,Player p){
        return dataHandler.getLang(key,p);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            return List.of("expand","select", "create", "list","set","tp","info","show","add","remove","message","delete","subzone","help");
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
                case "expand":
                    list.add("up");
                    list.add("down");
                    list.add("north");
                    list.add("south");
                    list.add("east");
                    list.add("west");
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
            case "area":
            case "subzone":
                subzone(p, args);
                break;
            case "expand":
                expand(p,args);
                break;
            default:
                p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
                break;
        }
        return false;
    }
    //#region delete
    private void handleDeleteCommand(Player p, String[] args) {

        Protection prote = null;

        if (args.length == 2) {
            prote = plugin.getProtectionsManager().getProtectionByName(args[1], p.getWorld());
            if (prote == null) {
                p.sendMessage(plugin.getDataHandler().getLang("prote.no_exist", p));
                return;
            }
        } else {
            prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
            if (prote == null) {
                p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
                return;
            }
        }
    
        if (prote.isAdminProtection(p)) {
            if (confirm(p)) {
                plugin.getProtectionsManager().deleteProtection(prote);
                p.sendMessage(plugin.getDataHandler().getLang("prote.delete", p));
            }
        } else {
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
        }
    }
    //#region message
    private void handleMessageCommand(Player p, String[] args) {

        Area area = plugin.getProtectionsManager().getAreaByLocation(p.getLocation());

        if (area == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }

        if (!area.getParentProtection().isAdminProtection(p)){
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            return;
        }
    
        if (args.length < 3) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        if (args[2].length() > 32){
            p.sendMessage(plugin.getDataHandler().getLang("prote.message_long", p));
            return;

        }
        StringBuilder message = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        switch (args[1].toLowerCase()) {
            case "enter":
            area.setEnterMessage(message.toString().trim());
                break;
            case "exit":
            area.setExitMessage(message.toString().trim());
                break;
            default:
                p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
                return;
        }
        p.sendMessage(plugin.getDataHandler().getLang("prote.message_set", p).replace("%message%", message.toString().trim()));

    }
   
    //#region setFlag
    private void handleSetFlagCommand(Player p, String[] args) {

        if (args.length != 3) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        if (!plugin.getProtectionFlags().isValid(args[1])) {
            p.sendMessage(plugin.getDataHandler().getLang("flags.invalid", p));
            return;
        }

        Area area = plugin.getProtectionsManager().getAreaByLocation(p.getLocation());

        if (area == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }

        boolean value = Boolean.parseBoolean(args[2]);

        if (area.getParentProtection().isAdminProtection(p)){
            if (plugin.getProtectionFlags().hasFlagPermission(args[1].toLowerCase(), p)){
                area.setFlag(ProtectionFlags.FlagType.valueOf(args[1].toUpperCase()), value);
                
                p.sendMessage(plugin.getDataHandler().getLang("prote.flag_set", p).replace("%flag%", args[1]).replace("%value%", String.valueOf(value)));
                
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("flags.no_perm", p));
            }            
        } else {
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
        }
    }

    //#region create
    private void handleCreateCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        if (plugin.getProtectionsManager().getProtectionsByWorld(p.getWorld()).containsKey(args[1])){
            p.sendMessage(plugin.getDataHandler().getLang("prote.exists", p));
        } else {
            if (plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){
                plugin.getProtectionsManager().getPlayersInEditMode().get(p).preCreateProtection(args[1]);
            } else {
                p.sendMessage(plugin.getDataHandler().getLang("select.must", p));
            }
        }
    }
    //#region select
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
    //#region tp
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
    //#region show
    private void handleShowCommand(Player p, String[] args) {

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());

        if (prote != null){
            if (prote.isAdminProtection(p)){

                Area area = plugin.getProtectionsManager().getAreaByLocation(p.getLocation());

                p.sendMessage(plugin.getDataHandler().getLang("select.show", p));

                plugin.getShowParticles().getShowProtections().add(prote.getMainArea());

                if (!area.isMain()){

                    plugin.getShowParticles().getShowProtections().add(area);

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        plugin.getShowParticles().getShowProtections().remove(area);
                    }, 100L); // 100 ticks = 5 segundos (1 segundo = 20 ticks)
                }

                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    plugin.getShowParticles().getShowProtections().remove(prote.getMainArea());

                }, 100L); // 100 ticks = 5 segundos (1 segundo = 20 ticks)

            } else {
                p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            }
        }else {
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
        }    
    }
    //#region list
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
    //#region info
    private void handleInfoCommand(Player p, String[] args) {

        Area area = plugin.getProtectionsManager().getAreaByLocation(p.getLocation());
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (prote != null){

            p.sendMessage(plugin.getDataHandler().getLang("prote.info.name", p) + prote.getName());
            p.sendMessage(plugin.getDataHandler().getLang("prote.info.owner", p) + prote.getOwnerName());
            String areas = prote.getAreas().keySet().toString();

            if (area != null){
                p.sendMessage(plugin.getDataHandler().getLang("prote.info.area", p) + area.getName() + ": " + areas);
                p.sendMessage(plugin.getDataHandler().getLang("prote.info.players", p) + area.getPlayersNames());
                TextComponent flags = new TextComponent(plugin.getDataHandler().getLang("prote.info.flags",p));
                TextComponent hoverOver = new TextComponent(plugin.getDataHandler().getLang("prote.info.hover",p));
                HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+area.getSortedFlags().toString().replaceAll("false", ChatColor.RED+"false"+ChatColor.GRAY).replaceAll("true", ChatColor.GREEN+"true"+ChatColor.GRAY)));
                                 
                flags.setHoverEvent(he);
                flags.addExtra(hoverOver);
    
                p.spigot().sendMessage(flags);
            }
            
        } else {
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
        }
    }
    //#region add/remove
    private void handleAddRemovePlayerCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        Area area = plugin.getProtectionsManager().getAreaByLocation(p.getLocation());
        if (area == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }
        if (!area.getParentProtection().isAdminProtection(p)){
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            return;
        }
        String playerName = args[1];
        if (playerName.equalsIgnoreCase(p.getName())){
            return;
        }
        @SuppressWarnings("deprecation")
        OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);
        switch (args[0].toLowerCase()) {
            case "add":
                if (area.getPlayers().contains(player.getUniqueId())){
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_added", p).replace("%player%", args[1]));
                    return;
                }
                if (area.addPlayer(args[1])){
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_added", p).replace("%player%", args[1]));
                } else{
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_no_exist", p).replace("%player%", args[1]));
                }
                break;
            case "remove":
                if (area.removePlayer(args[1])){
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_removed", p).replace("%player%", args[1]));
                } else {
                    p.sendMessage(plugin.getDataHandler().getLang("prote.player_no_exist", p).replace("%player%", args[1]));        
                }
                break;
            default:
                p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
                break;
        }
    }
    //#region Subzone
    private void subzone(Player p, String[] args) {
        if (args.length != 3){
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }
        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (prote == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }
        if (!prote.isAdminProtection(p)){
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            return;
        }
        switch (args[1].toLowerCase()) {
            case "create":
                createSubzone(p, prote, args[2]);
                break;
            case "delete":
                deleteSubzone(p, prote, args[2]);
                break;
            default:
                p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
                break;
        }
    }
    private void createSubzone(Player p, Protection prote, String name) {

        if (prote.getAreas().containsKey(name)){
            p.sendMessage(plugin.getDataHandler().getLang("prote.sub_exists", p));
            return;
        }
        plugin.getProtectionsManager().getPlayersInEditMode().get(p).preCreateArea(name);
    }
    
    private void deleteSubzone(Player p, Protection prote, String name) {

        for (Area area : prote.getAreas().values()) {
            if (area.getName().equalsIgnoreCase(name)){
                if (area.isMain()){
                    p.sendMessage(plugin.getDataHandler().getLang("prote.subzone_main", p));
                    return;
                }
                if (confirm(p)){
                    p.sendMessage(plugin.getDataHandler().getLang("prote.subzone_delete", p));
                    prote.getAreas().remove(area.getName());
                    area = null;
                    prote.save();
                }
                return;
            }
        }

        p.sendMessage(plugin.getDataHandler().getLang("prote.no_exists", p));
    }
    //#region expand
    private List<Player> preExpand = new ArrayList<>();
    private void expand(Player p, String[] args){
        Selection sel = plugin.getProtectionsManager().getPlayersInEditMode().getOrDefault(p, null);
        if (sel == null){
            p.sendMessage(plugin.getDataHandler().getLang("select.must", p));
            return;      
        }   
        Area area = plugin.getProtectionsManager().getAreaByLocation(p.getLocation());

        if (area == null){
            p.sendMessage(plugin.getDataHandler().getLang("prote.no_there", p));
            return;
        }
        if (!area.getParentProtection().isAdminProtection(p)){
            p.sendMessage(plugin.getDataHandler().getLang("commands.no_perm", p));
            return;
        }

        if (args[1].equalsIgnoreCase("confirm") && preExpand.contains(p)){

            //Expand selection confirm
            if (plugin.getSelectionManager().expandArea(area, sel)){
                p.sendMessage(plugin.getDataHandler().getLang("prote.area_expand", p));
                preExpand.remove(p);
            }
            return;
        }

        if (args.length != 3){
            p.sendMessage(plugin.getDataHandler().getLang("commands.missing_arg", p));
            return;
        }

        SelectionManager.Direction direction;
        try {
            direction = SelectionManager.Direction.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
            return;
        }
        int i = 0;
        try {
            i = Integer.parseInt(args[2]);
        } catch (Exception e) {
            p.sendMessage(plugin.getDataHandler().getLang("commands.invalid", p));
            return;
        }
        //Expand selection
        p.sendMessage("§eSelected " + i + " blocks to " + direction.toString());
        plugin.getSelectionManager().expandSelection(area, sel, direction, i);

        //Detect cost 
        double cost = plugin.getSelectionManager().getExpandCost(area, sel);
        p.sendMessage(getLang("prote.expand_cost", p).replace("%cost%", cost+""));
        if (plugin.getSelectionManager().hasExpandCost(area, sel)){
            p.sendMessage(getLang("prote.expand_confirm", p));
            preExpand.add(p);
        } else {
            p.sendMessage(getLang("prote.no_money", p));
        }
    }
}


