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

import com.ar.askgaming.buildprotection.Protection.Area;
import com.ar.askgaming.buildprotection.Protection.Protection;
import com.ar.askgaming.buildprotection.Protection.ProtectionFlags;
import com.ar.askgaming.buildprotection.Protection.ProtectionsManager;
import com.ar.askgaming.buildprotection.Selection.Selection;
import com.ar.askgaming.buildprotection.Selection.SelectionManager;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Commands implements TabExecutor {
    
    private final BuildProtection plugin;
    private final ProtectionsManager manager;
    public Commands(BuildProtection main) {
        plugin = main;
        manager = plugin.getProtectionsManager();
        plugin.getServer().getPluginCommand("rtp").setExecutor(this);
    }

    private List<Player> confirmMessage = new ArrayList<>();

    private boolean confirm(Player p){
        if (confirmMessage.contains(p)){
            confirmMessage.remove(p);
            return true;
        } else {
            p.sendMessage(getLang("commands.confirm", p));
            confirmMessage.add(p);
            return false;
        }
    }

    private String getLang(String key,Player p){
        return plugin.getLangManager().get(key, p);
    }
    private void save(Protection prote){
        manager.save(prote);
    }
    private void save(Area area){
        Protection prote = area.getParentProtection();
        save(prote);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list = new ArrayList<>(List.of("select", "create", "list","set","info","show","add","remove","message","delete","subzone","rent","unrent"));
            if (sender.hasPermission("buildprotection.rtp")) {
                list.add("rtp");
            }
            if (sender.hasPermission("buildprotection.teleport")){
                list.add("tp");
            }
            return list;
        }
        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "set":
                    for (ProtectionFlags.FlagType flag : ProtectionFlags.FlagType.values()) {
                        list.add(flag.toString().toLowerCase());
                    }
                    return list;
                case "add":
                    for (Player p : Bukkit.getOnlinePlayers()){
                        list.add(p.getName());
                    }
                    return list;  
                case "remove":
                    for (Player p : Bukkit.getOnlinePlayers()){
                        list.add(p.getName());
                    }
                    return list; 
                case "expand":
                    list.add("up");
                    list.add("down");
                    list.add("north");
                    list.add("south");
                    list.add("east");
                    list.add("west");
                    return list;
                default:
                    break;
            }

        }
        return null;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                
        if (sender instanceof Player){
        } else {return true;}

        Player p = (Player) sender;

        if (command.getName().equalsIgnoreCase("rtp")) {
            randomTeleport(p, args);
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(getLang("help", p));
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
            case "rent":
                rent(p,args);
                break;
            case "unrent":
                unrent(p,args);
                break;
            case "rtp":
                randomTeleport(p,args);
                break;
            case "reload":
                reload(p);
                break;
            case "help":
            default:
                p.sendMessage(getLang("help", p));
                break;
        }
        return true;
    }
    //#region delete
    private void handleDeleteCommand(Player p, String[] args) {

        Protection prote = null;

        if (args.length == 2) {
            prote = manager.getProtectionByName(args[1], p.getWorld());
            if (prote == null) {
                p.sendMessage(getLang("prote.no_exist", p));
                return;
            }
        } else {
            prote = manager.getProtectionByLocation(p.getLocation());
            if (prote == null) {
                p.sendMessage(getLang("prote.no_there", p));
                return;
            }
        }
    
        if (manager.hasAdminPermission(prote, p)) {
            if (confirm(p)) {
                manager.deleteProtection(p,prote);
                p.sendMessage(getLang("prote.delete", p));
            }
        } else {
            p.sendMessage(getLang("commands.no_perm", p));
        }
    }
    //#region message
    private void handleMessageCommand(Player p, String[] args) {

        Area area = manager.getAreaByLocation(p.getLocation());

        if (area == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }

        if (!manager.hasAdminPermission(area, p)){
            p.sendMessage(getLang("commands.no_perm", p));
            return;
        }
    
        if (args.length < 3) {
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }
        if (args[2].length() > 32){
            p.sendMessage(getLang("prote.message_long", p));
            return;

        }
        StringBuilder message = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        switch (args[1].toLowerCase()) {
            case "enter":
            area.setEnterMessage(message.toString().trim());
            save(area);
                break;
            case "exit":
            area.setExitMessage(message.toString().trim());
            save(area);
                break;
            default:
                p.sendMessage(getLang("commands.invalid", p));
                return;
        }
        p.sendMessage(getLang("prote.message_set", p).replace("%message%", message.toString().trim()));

    }
   
    //#region setFlag
    private void handleSetFlagCommand(Player p, String[] args) {

        if (args.length != 3) {
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }

        Area area = manager.getAreaByLocation(p.getLocation());

        if (area == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        if (!manager.hasAdminPermission(area, p)){
            p.sendMessage(getLang("commands.no_perm", p));
            return;
        }

        boolean value = Boolean.parseBoolean(args[2]);

        if (args[1].equalsIgnoreCase("rentable")){
            area.setRentable(value);
            save(area);
            p.sendMessage(getLang("prote.flag_set", p).replace("%flag%", args[1]).replace("%value%", String.valueOf(value)));
            return;
        }
        if (!plugin.getProtectionFlags().isValid(args[1])) {
            p.sendMessage(getLang("flags.invalid", p));
            return;
        }
        if (!manager.canCreateMore(p)){
            p.sendMessage(getLang("prote.max", p));
            return;
        }

        if (plugin.getProtectionFlags().hasFlagPermission(args[1].toLowerCase(), p)){
            area.setFlag(ProtectionFlags.FlagType.valueOf(args[1].toUpperCase()), value);
            save(area);
            p.sendMessage(getLang("prote.flag_set", p).replace("%flag%", args[1]).replace("%value%", String.valueOf(value)));
            
        } else {
            p.sendMessage(getLang("flags.no_perm", p));
        } 
    }

    //#region create
    private void handleCreateCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }
        if (!isValid(p, args[1])){
            return;
        }
        if (manager.getProtectionsByWorld(p.getWorld()).containsKey(args[1])){
            p.sendMessage(getLang("prote.exists", p));
        } else {
            if (manager.getPlayersInEditMode().containsKey(p)){
                manager.getPlayersInEditMode().get(p).preCreateProtection(args[1]);
            } else {
                p.sendMessage(getLang("select.must", p));
            }
        }
    }
    //#region select
    private void handleSelectCommand(Player p, String[] args) {

        if (args.length == 1) {
            if (!manager.getPlayersInEditMode().containsKey(p)){
                new Selection(p,plugin);  
                p.sendMessage(getLang("select.enter", p));          
            } else {
                p.sendMessage(getLang("select.exit", p));
                manager.getPlayersInEditMode().remove(p);    
            }

        } else if (args.length == 2) {
            if (manager.getPlayersInEditMode().containsKey(p)){
                try {
                    int i = Integer.parseInt(args[1]);
                    manager.getPlayersInEditMode().get(p).setByRadius(i);
                    
                } catch (Exception e) {
                    p.sendMessage(getLang("commands.invalid", p));
                }
            }
        }
    }
    //#region tp
    private void handleTpCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }
        if (!p.hasPermission("buildprotection.teleport")) {
            p.sendMessage(getLang("commands.no_perm", p));
            return;
        }

        Protection prote = manager.getProtectionByName(args[1],p.getWorld());
        if (prote != null){
            Location l = prote.getCenter();
            if (l != null){
                p.teleport(l);
                //Message?
            }
        }else {
            p.sendMessage(getLang("prote.no_exist", p));
        }    
    }
    //#region show
    private void handleShowCommand(Player p, String[] args) {

        Protection prote = manager.getProtectionByLocation(p.getLocation());
        if (prote == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        if (!manager.hasAdminPermission(prote, p)){
            p.sendMessage(getLang("commands.no_perm", p));
            return; 

        }
            
        Area area = manager.getAreaByLocation(p.getLocation());

        p.sendMessage(getLang("select.show", p));

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
                
    }
    //#region list
    private void handleListCommand(Player p, String[] args) {
        if (args.length == 1) {
            // Listar todas las protecciones del jugador
            p.sendMessage(getLang("prote.list", p));
            manager.getProtectionsByOwner(p.getUniqueId()).forEach(prote -> {
                p.sendMessage(prote.getName());
            });
            return;
        }
        if (!p.hasPermission("buildprotection.listothers")) {
            p.sendMessage(getLang("commands.no_perm", p));
            return;
        }
        if (args[1].equalsIgnoreCase("all")){
            p.sendMessage("Listing protections of world " + p.getWorld().getName());
            manager.getProtectionsByWorld(p.getWorld()).forEach((name, prote) -> {
                p.sendMessage(name);
            });
        } else {
            //Obtener proteciones de un jugador
            p.sendMessage("Listing protections of player " + args[1]);
            @SuppressWarnings("deprecation")
            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
            manager.getProtectionsByOwner(player.getUniqueId()).forEach(prote -> {
                p.sendMessage(prote.getName());
            });
        }
    }
    //#region info
    private void handleInfoCommand(Player p, String[] args) {

        Area area = manager.getAreaByLocation(p.getLocation());
        
        if (area == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        Protection prote = area.getParentProtection();
        String areas = prote.getAreas().keySet().toString();

        String proteName;
        String ownerName;
        if (area.isRented()){
            proteName = area.getName();
            ownerName = area.getRentedOwnerName();
        } else {
            proteName = prote.getName();
            ownerName = prote.getOwnerName();
        }
        
        p.sendMessage(getLang("prote.info.name", p) + proteName);
        p.sendMessage(getLang("prote.info.owner", p) + ownerName);

        if (area.isRentable()){
            p.sendMessage(getLang("prote.info.rentable", p)+ area.getRentCost());
        } else p.sendMessage(getLang("prote.info.area", p) + area.getName() + ": " + areas);
        
        p.sendMessage(getLang("prote.info.players", p) + area.getPlayersNames());
        TextComponent flags = new TextComponent(getLang("prote.info.flags",p));
        TextComponent hoverOver = new TextComponent(getLang("prote.info.hover",p));
        String sortedFlags = plugin.getProtectionFlags().getSortedFlags(area).toString().replaceAll("false", ChatColor.RED+"false"+ChatColor.GRAY).replaceAll("true", ChatColor.GREEN+"true"+ChatColor.GRAY);
        HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(sortedFlags));
                            
        flags.setHoverEvent(he);
        flags.addExtra(hoverOver);

        p.spigot().sendMessage(flags);
                    
    }
    //#region add/remove
    private void handleAddRemovePlayerCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }
        Area area = manager.getAreaByLocation(p.getLocation());
        if (area == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        if (!manager.hasAdminPermission(area, p)){
            p.sendMessage(getLang("commands.no_perm", p));
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
                    p.sendMessage(getLang("prote.player_added", p).replace("%player%", args[1]));
                    return;
                }
                if (area.addPlayer(args[1])){
                    p.sendMessage(getLang("prote.player_added", p).replace("%player%", args[1]));
                    save(area);
                } else{
                    p.sendMessage(getLang("prote.player_no_exist", p).replace("%player%", args[1]));
                }
                break;
            case "remove":
                if (area.removePlayer(args[1])){
                    p.sendMessage(getLang("prote.player_removed", p).replace("%player%", args[1]));
                    save(area);
                } else {
                    p.sendMessage(getLang("prote.player_no_exist", p).replace("%player%", args[1]));        
                }
                break;
            default:
                p.sendMessage(getLang("commands.invalid", p));
                break;
        }
    }
    //#region Subzone
    private void subzone(Player p, String[] args) {
        if (args.length != 3){
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }
        Protection prote = manager.getProtectionByLocation(p.getLocation());
        if (prote == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        if (!manager.hasAdminPermission(prote, p)){
            p.sendMessage(getLang("commands.no_perm", p));
            return;
        }
        if (!isValid(p, args[2])){
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
                p.sendMessage(getLang("commands.invalid", p));
                break;
        }
    }
    private void createSubzone(Player p, Protection prote, String name) {

        if (prote.getAreas().containsKey(name)){
            p.sendMessage(getLang("prote.sub_exists", p));
            return;
        }
        manager.getPlayersInEditMode().get(p).preCreateArea(name);
    }
    
    private void deleteSubzone(Player p, Protection prote, String name) {

        for (Area area : prote.getAreas().values()) {
            if (area.getName().equalsIgnoreCase(name)){
                if (area.isMain()){
                    p.sendMessage(getLang("prote.subzone_main", p));
                    return;
                }
                if (confirm(p)){
                    p.sendMessage(getLang("prote.subzone_delete", p));
                    prote.getAreas().remove(area.getName());
                    area = null;
                    save(prote);
                }
                return;
            }
        }

        p.sendMessage(getLang("prote.no_exists", p));
    }
    //#region expand
    private List<Player> preExpand = new ArrayList<>();
    private void expand(Player p, String[] args){
        Selection sel;
        if (!manager.getPlayersInEditMode().containsKey(p)){
            sel = new Selection(p,plugin);  
            p.sendMessage(getLang("select.enter", p));          
        } else {
            sel = manager.getPlayersInEditMode().get(p);
        }   
        Area area = manager.getAreaByLocation(p.getLocation());
        
        if (area == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        if (area.isRentable()){
            p.sendMessage(getLang("rent.cant_expand", p));
            return;
        }
        if (!manager.hasAdminPermission(area.getParentProtection(), p)){
            p.sendMessage(getLang("commands.no_perm", p));
            return;
        }
        if (args.length == 2 && args[1].equalsIgnoreCase("confirm") && preExpand.contains(p)){
            //Expand selection confirm
            if (plugin.getSelectionManager().expandArea(area, sel)){
                p.sendMessage(getLang("prote.area_expand", p));
                preExpand.remove(p);
            }
            return;

        }

        if (args.length != 3){
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }

        SelectionManager.Direction direction;
        try {
            direction = SelectionManager.Direction.valueOf(args[1].toUpperCase());
        } catch (Exception e) {
            p.sendMessage(getLang("commands.invalid", p));
            return;
        }
        int i = 0;
        try {
            i = Integer.parseInt(args[2]);
        } catch (Exception e) {
            p.sendMessage(getLang("commands.invalid", p));
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
    //#region rent
    public void rent(Player p, String[] args){
        if (args.length != 1){
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }
        Area area = manager.getAreaByLocation(p.getLocation());
        if (area == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        if (!area.isRentable()){
            p.sendMessage(getLang("rent.not_rentable", p));
            return;
        }
        if (area.isRented()){
            p.sendMessage(getLang("rent.already_rented", p));
            return;
        }
        if (p.getUniqueId().equals(area.getParentProtection().getOwner())){
            return;
        }
        manager.rent(area, p);
    }
    //#region unrent
    public void unrent(Player p, String[] args){
        if (args.length != 1){
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }
        Area area = manager.getAreaByLocation(p.getLocation());
        if (area == null){
            p.sendMessage(getLang("prote.no_there", p));
            return;
        }
        if (!area.isRented()){
            p.sendMessage(getLang("rent.not_rentable", p));
            return;
        }
        if (manager.hasAdminPermission(area, p)){
            manager.unrent(area, p);
        } else {
            p.sendMessage(getLang("commands.no_perm", p));

        }
    }
    //#region rtp
    private void randomTeleport(Player p, String[] args) {

        if (args.length == 2){
            if (args[1].equalsIgnoreCase("return")){
                plugin.getRandomTeleport().sendToLastLocation(p);
                return;
            }
            p.sendMessage(getLang("commands.missing_arg", p));
            return;
        }

        if (!p.hasPermission("buildprotection.rtp")){
            p.sendMessage(getLang("commands.no_perm", p));
            return;
        }
        List<String> worlds = plugin.getConfig().getStringList("random_teleport.disabled_worlds");
        for (String world : worlds) {
            if (world.equalsIgnoreCase(p.getWorld().getName())){
                p.sendMessage(getLang("rtp.disabled", p));
                return;
            }
        }
        plugin.getRandomTeleport().sendToRandomLocation(p);
    }
    private boolean isValid(Player player, String name) {
        String invalid = getLang("misc.invalid_name", player);
        if (name.length() > 16) {
            player.sendMessage(invalid);
            return false;
        }
        if (name.length() < 3) {
            player.sendMessage(invalid);
            return false;
        }
        if (name.contains(" ")) {
            player.sendMessage(invalid);
            return false;
        }
        if (!name.matches("^[a-zA-Z0-9]+$")) {
            player.sendMessage(invalid);
            return false;
        }
    
        return true;
    }
    //#region reload
    private void reload(Player p) {
        if (!p.hasPermission("buildprotection.reload")) {
            p.sendMessage(getLang("commands.no_perm", p));
            return;

        }
        plugin.reloadConfig();
        plugin.getRentScheduler().load();
        plugin.getLangManager().clearCache();
        plugin.getRandomTeleport().load();
        plugin.getProtectionFlags().reload();
       // plugin.getLangManager().reload();
        p.sendMessage("Config and lang files reloaded.");
    }
}


