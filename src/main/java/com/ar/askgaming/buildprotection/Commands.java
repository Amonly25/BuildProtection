package com.ar.askgaming.buildprotection;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.s;

import com.ar.askgaming.buildprotection.ProtectionFlags.FlagType;

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
        
        if (args.length == 1) {
            return List.of("select", "create", "list","set","tp","info","show","add","remove","message");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            List<String> flags = new ArrayList<>();
            for (ProtectionFlags.FlagType flag : ProtectionFlags.FlagType.values()) {
                flags.add(flag.toString().toLowerCase());
            }
            return flags;
        }
        return new ArrayList<>();
    }
        @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
                
        if (sender instanceof Player){
        } else {return true;}

        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage("Use /prote select para empezar a crear una proteccion.");
            p.sendMessage("O usa /prote help para ver los comandos disponibles.");
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
                handleHelpCommand(p, args);
                break;
            case "create":
                handleCreateCommand(p, args);
                break;
            default:
                p.sendMessage("Subcomando invalido. ");
                break;
        }
        return false;
    }

    private void handleHelpCommand(Player p, String[] args) {
            p.sendMessage("§eComandos de proteccion:");
            p.sendMessage("§7/protection select - Selecciona un area para crear una proteccion.");
            p.sendMessage("§7/protection create <nombre> - Crea una proteccion con el area seleccionada.");
            p.sendMessage("§7/protection list <jugador/all> - Muestra las protecciones de un jugador o todas.");
            p.sendMessage("§7/protection info - Muestra informacion de la proteccion en la que estas.");
            p.sendMessage("§7/protection show - Muestra la proteccion en la que estas.");
            p.sendMessage("§7/protection set <flag> <true/false> - Cambia el estado de una flag.");
            p.sendMessage("§7/protection tp <nombre> - Teletransporta a una proteccion.");
            p.sendMessage("§7/protection add <jugador> - Añade un jugador a la proteccion.");
            p.sendMessage("§7/protection remove <jugador> - Elimina un jugador de la proteccion.");
            p.sendMessage("§7/protection message <mensaje> - Cambia el mensaje de la proteccion.");

        }
   
    private void handleMessageCommand(Player p, String[] args) {

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());

        if (prote == null){
            p.sendMessage("No estas en una proteccion.");
            return;
        }

        if (!prote.isAdminProtection(p)){
            p.sendMessage("No tienes permiso para hacer eso.");
            return;
        }
    
        if (args.length < 2) {
            p.sendMessage("Faltan argumentos.");
            return;
        }
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        p.sendMessage("Cambiaste el mensaje a: " + message.toString().trim());
        prote.setMessage(message.toString().trim());
    }
   
    private void handleSetFlagCommand(Player p, String[] args) {

        if (args.length != 3) {
            p.sendMessage("Faltan argumentos.");
            return;
        }
        if (!plugin.getProtectionFlags().isValid(args[1])) {
            p.sendMessage("Flag invalida.");
            return;
        }

        Protection proteByLoc = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());

        if (proteByLoc == null){
            p.sendMessage("No estas en una proteccion.");
            return;
        }

        boolean value = Boolean.parseBoolean(args[2]);

        if (proteByLoc.isAdminProtection(p)){
            p.sendMessage("Flag " + args[1] + " seteado a " + value);
            proteByLoc.setFlag(FlagType.valueOf(args[1].toUpperCase()), value);
            
        } else {
            p.sendMessage("No tienes permiso para hacer eso.");
        }
    }

    private void handleCreateCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage("En necesario establecer un nombre.");
            return;
        }
        if (plugin.getProtectionsManager().getProtectionsByWorld(p.getWorld()).containsKey(args[1])){
            p.sendMessage("Ya existe una proteccion con ese nombre.");
        } else {
            plugin.getProtectionsManager().getPlayersInEditMode().get(p).create(args[1]);
        }
    }

    private void handleSelectCommand(Player p, String[] args) {
        if (args.length == 1) {
            if (!plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){
                new Selection(p,plugin);
                p.sendMessage("§eEstas en modo edicion para protecction.");
                p.sendMessage("§7Ahora puedes seleccionar dos puntos con un palo.");
                p.sendMessage("§7O usa /protection select <radio> para seleccionar un area alrededor de ti.");
                
            } else {
                p.sendMessage("Has salido del modo edicion.");
                plugin.getProtectionsManager().getPlayersInEditMode().remove(p);    
            }

        } else if (args.length == 2) {
            if (plugin.getProtectionsManager().getPlayersInEditMode().containsKey(p)){
                try {
                    int i = Integer.parseInt(args[1]);
                    plugin.getProtectionsManager().getPlayersInEditMode().get(p).setByRadius(i, p, p.getLocation());
                    
                } catch (Exception e) {
                    p.sendMessage("Numero invalido.");
                }
            }
        }
    }

    private void handleTpCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage("Falta un argumento.");
            return;
        }
        if (!p.hasPermission("buildprotection.teleport")) {
            p.sendMessage("No tienes permiso para teletransportarte.");
            return;
        }

        Protection prote = plugin.getProtectionsManager().getProtectionByName(args[1],p.getWorld());
        if (prote != null){
            Location l = prote.getCenter();
            if (l != null){
                p.teleport(l);
                p.sendMessage("Te has teletransportado a la proteccion " + args[1]);
            }
        }else {
            p.sendMessage("No existe una proteccion con ese nombre.");
        }    
    }

    private void handleShowCommand(Player p, String[] args) {

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (prote != null){
            if (prote.isAdminProtection(p)){
                plugin.getShowParticles().getShowProtections().add(prote);
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    plugin.getShowParticles().getShowProtections().remove(prote);
                }, 100L); // 100 ticks = 5 segundos (1 segundo = 20 ticks)
            } else {
                p.sendMessage("No tienes permiso para hacer eso.");
            }
        }else {
            p.sendMessage("No estas en una proteccion.");
        }    
    }

    private void handleListCommand(Player p, String[] args) {
        if (args.length == 1) {
            // Listar todas las protecciones del jugador
            p.sendMessage("Mostrando tus protecciones");
            return;
        }
        if (!p.hasPermission("buildprotection.listothers")) {
            p.sendMessage("No tienes permiso para hacer eso.");
            return;
        }
        if (args[1].equalsIgnoreCase("all")){
            plugin.getProtectionsManager().getProtectionsByWorld(p.getWorld()).forEach((name, prote) -> {
                p.sendMessage("Obteniendo todas las protecciones del mundo " + p.getWorld().getName());
                p.sendMessage(name);
            });
        } else {
            //Obtener proteciones de un jugador
            plugin.getProtectionsManager().getAllProtections().forEach((world,map) -> {
                map.forEach((name,prote) -> {
                    if (prote.getOwner().equalsIgnoreCase(args[1])){
                        p.sendMessage("Obteniendo protecciones de " + args[1]);
                        p.sendMessage(name + " en " + world);
                    }
                });
            });
        }
    }

    private void handleInfoCommand(Player p, String[] args) {

        Protection prote = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (prote != null){
            p.sendMessage("Nombre: " + prote.getName());
            p.sendMessage("Dueño: " + prote.getOwner());
            p.sendMessage("Con permisos: " + prote.getPlayers().toString());
            p.sendMessage("Mensaje: " + prote.getMessage());
            
            TextComponent flags = new TextComponent("Flags");
            TextComponent hoverOver = new TextComponent("(Hover over for more info)");
            HoverEvent he = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(ChatColor.GRAY+prote.getFlagsMap().toString().replaceAll("false", ChatColor.RED+"false"+ChatColor.GRAY).replaceAll("true", ChatColor.GREEN+"true"+ChatColor.GRAY)));
                             
            flags.setHoverEvent(he);
            flags.addExtra(hoverOver);

            p.spigot().sendMessage(flags);
        } else {
            p.sendMessage("No existe proteccion aqui.");
        }
    }

    private void handleAddRemovePlayerCommand(Player p, String[] args) {
        if (args.length != 2) {
            p.sendMessage("Faltan argumentos.");
            return;
        }
        Protection proteByLoc = plugin.getProtectionsManager().getProtectionByLocation(p.getLocation());
        if (proteByLoc == null){
            p.sendMessage("No estas en una proteccion.");
            return;
        }
        if (proteByLoc.isAdminProtection(p)){
            if (args[0].equalsIgnoreCase("add")){
                proteByLoc.addPlayer(args[1]);
                p.sendMessage("Has añadido a " + args[1] +" a la proteccion");
            } else if (args[0].equalsIgnoreCase("remove")){
                proteByLoc.removePlayer(args[1]);
                p.sendMessage("Jugador eliminado a "+ args[1] +" de la proteccion");
            } else {
                p.sendMessage("Argumento invalido. utiliza add o remove.");
            }
        } else {
            p.sendMessage("No tienes permiso para hacer eso.");
        }
    }
}


