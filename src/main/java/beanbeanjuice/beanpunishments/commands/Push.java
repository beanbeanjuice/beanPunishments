package beanbeanjuice.beanpunishments.commands;

import beanbeanjuice.beanpunishments.BeanPunishments;
import beanbeanjuice.beanpunishments.utilities.GeneralHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Push implements CommandExecutor {

    private BeanPunishments plugin;

    public Push(BeanPunishments plugin) {
        this.plugin = plugin;
        plugin.getCommand("push").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(GeneralHelper.getConsolePrefix() + "Only players may execute this command.");
            return false;
        }

        Player punisher = (Player) sender;

        if (punisher.hasPermission("beanpunishments.push")) {
            if (args.length != 2) {
                punisher.sendMessage(GeneralHelper.getPrefix() + GeneralHelper.translateColors(plugin.getConfig().getString("push-incorrect-syntax")));
                return false;
            } else if (Bukkit.getPlayer(args[0]) == null) {
                punisher.sendMessage(GeneralHelper.getPrefix() + GeneralHelper.translateColors(plugin.getConfig().getString("player-not-found").replace("{player}", args[0])));
                return false;
            } else if (isBlacklisted(args[0])) {
                punisher.sendMessage(GeneralHelper.getPrefix() + GeneralHelper.translateColors(plugin.getConfig().getString("push-not-allowed").replace("{player}", args[0])));
                return false;
            } else if (Math.abs(Integer.parseInt(args[1])) > plugin.getConfig().getInt("push-maximum")) {
                punisher.sendMessage(GeneralHelper.getPrefix() + GeneralHelper.translateColors(plugin.getConfig().getString("push-above-maximum").replace("{max}", Integer.toString(plugin.getConfig().getInt("push-maximum")))));
                return false;
            }
            Player punishee = Bukkit.getPlayer(args[0]);
            pushPlayer(punisher, punishee, Integer.parseInt(args[1]));
            punisher.sendMessage(GeneralHelper.getPrefix() + GeneralHelper.translateColors(plugin.getConfig().getString("successful-push").replace("{player}", punishee.getName())));
            return true;
        } else {
            punisher.sendMessage(GeneralHelper.getPrefix() + GeneralHelper.translateColors(plugin.getConfig().getString("no-permission")));
            return false;
        }
    }

    void pushPlayer(Player punisher, Player punishee, int strength) {
        Vector direction = punisher.getLocation().getDirection();
        punishee.setVelocity(direction.multiply(strength));
    }

    boolean isBlacklisted(String punishee) {
        return plugin.getConfig().getStringList("push-blacklisted-players").contains(punishee);
    }
}