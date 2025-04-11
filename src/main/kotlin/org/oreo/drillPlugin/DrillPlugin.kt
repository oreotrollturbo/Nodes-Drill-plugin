package org.oreo.drillPlugin

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.oreo.drillPlugin.commands.GetChopper
import org.oreo.drillPlugin.commands.GetDigger
import org.oreo.drillPlugin.commands.GetDrill
import org.oreo.drillPlugin.items.ItemManager
import org.oreo.drillPlugin.listeners.ChopperListener
import org.oreo.drillPlugin.listeners.DiggerListener
import org.oreo.drillPlugin.listeners.DrillListener
import phonon.nodes.Nodes

class DrillPlugin : JavaPlugin() {

    override fun onEnable() {

        nodesInstance = Bukkit.getServicesManager().load(Nodes::class.java)
        if (nodesInstance == null) {
            // Handle error: service not registered
            logger.severe("Nodes not detected!")
        }

        ItemManager.init(this)

        server.pluginManager.registerEvents(DrillListener(), this)
        server.pluginManager.registerEvents(DiggerListener(), this)
        server.pluginManager.registerEvents(ChopperListener(), this)

        getCommand("drill")!!.setExecutor(GetDrill())
        getCommand("digger")!!.setExecutor(GetDigger())
        getCommand("chopper")!!.setExecutor(GetChopper())

        saveDefaultConfig()
    }

    companion object{
        var nodesInstance : Nodes? = null
    }

}
