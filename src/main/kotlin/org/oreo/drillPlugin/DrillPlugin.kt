package org.oreo.drillPlugin

import org.bukkit.plugin.java.JavaPlugin
import org.oreo.drillPlugin.commands.GetChopper
import org.oreo.drillPlugin.commands.GetDigger
import org.oreo.drillPlugin.commands.GetDrill
import org.oreo.drillPlugin.items.ItemManager
import org.oreo.drillPlugin.listeners.ChopperListener
import org.oreo.drillPlugin.listeners.DiggerListener
import org.oreo.drillPlugin.listeners.DrillListener

class DrillPlugin : JavaPlugin() {

    override fun onEnable() {

        ItemManager.init(this)

        server.pluginManager.registerEvents(DrillListener(), this)
        server.pluginManager.registerEvents(DiggerListener(), this)
        server.pluginManager.registerEvents(ChopperListener(), this)

        getCommand("drill")!!.setExecutor(GetDrill())
        getCommand("digger")!!.setExecutor(GetDigger())
        getCommand("chopper")!!.setExecutor(GetChopper())

        saveDefaultConfig()
    }

}
