package org.oreo.drillPlugin.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.oreo.drillPlugin.items.ItemManager

class GetDrill : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        // Check for permissions
        if (!sender.isOp) {
            sender.sendMessage("§cYou don't have permission to use this command.")
            return false
        }

        // If no argument is provided, and the sender is a Player, give the item to the sender
        if (args.isEmpty()) {
            if (sender !is Player) {
                sender.sendMessage("§cYou need to be a player to use this command without specifying a target.")
                return false
            }
            val player = sender as Player
            ItemManager.drill?.let { player.inventory.addItem(it) }
            player.sendMessage("§aGave you a drill successfully.")
        } else {
            val targetPlayerName = args[0]
            val targetPlayer = Bukkit.getPlayer(targetPlayerName)

            if (targetPlayer == null) {
                sender.sendMessage("§cPlayer '$targetPlayerName' not found or is not online.")
                return false
            }

            // Give the item to the target player
            ItemManager.drill?.let { targetPlayer.inventory.addItem(it) }
            sender.sendMessage("§aSuccessfully gave a drill to $targetPlayerName.")
        }

        return true
    }
}