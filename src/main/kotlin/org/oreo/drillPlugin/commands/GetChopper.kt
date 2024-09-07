package org.oreo.drillPlugin.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.oreo.drillPlugin.items.ItemManager

class GetChopper : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Only players can use this command")
            return true
        }

        val player: Player = sender
        if (player.isOp) {
            ItemManager.chopper?.let { player.inventory.addItem(it) }
            player.sendMessage("Gave you a chopper successfully")
        } else {
            player.sendMessage("§c You don't have permission to use this command")
        }
        return true
    }
}