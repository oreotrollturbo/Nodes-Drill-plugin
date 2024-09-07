package org.oreo.drillPlugin.listeners

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.oreo.drillPlugin.items.ItemManager
import org.oreo.drillPlugin.java.GetNodesInfo
import phonon.nodes.Nodes
import phonon.nodes.objects.Town
import java.util.function.Consumer


class DiggerListener : Listener {

    @EventHandler
    fun onPlayerUseDigger(e: BlockBreakEvent){
        val player = e.player
        val itemInHand = player.itemInHand

        if (e.isCancelled || !ItemManager.isDigger(itemInHand)){
            return
        }

        if (GetNodesInfo.isWarOn()){
            e.isCancelled = true
            player.sendMessage("§4You cannot use diggers during war time")
            return
        }

        val block = e.block
        val town = Nodes.getResident(player)?.town

        if (town == null){
            e.isCancelled = true
            player.sendMessage("§4You need to be part of a town to use diggers")
            return
        } else if (wrongTerritory(town,block)){
            e.isCancelled = true
            player.sendMessage("§4You cannot dig outside of your town")
            return
        }

        //This entire chunk of code is ripped and translated into straight from
        // https://github.com/TheRealRomyy/Hammer/blob/master/src/fr/rome/hammerforsiri/listeners/BlockBreakListener.java
        //and translated into kotlin by InteliJ
        // Credit to TheRealRomyy for the amazing code , would have never done such a good job myself

        val location = block.location

        val locs = ArrayList<Location>()

        val x = location.x
        val y = location.y
        val z = location.z

        when (getDirection(player, block)) {
            0 -> {
                locs.add(Location(location.getWorld(), x, y + 1, z))
                locs.add(Location(location.getWorld(), x, y - 1, z))
                locs.add(Location(location.getWorld(), x, y, z + 1))
                locs.add(Location(location.getWorld(), x, y, z - 1))
                locs.add(Location(location.getWorld(), x, y + 1, z + 1))
                locs.add(Location(location.getWorld(), x, y + 1, z - 1))
                locs.add(Location(location.getWorld(), x, y - 1, z + 1))
                locs.add(Location(location.getWorld(), x, y - 1, z - 1))
            }

            1 -> {
                locs.add(Location(location.getWorld(), x, y + 1, z))
                locs.add(Location(location.getWorld(), x, y - 1, z))
                locs.add(Location(location.getWorld(), x + 1, y, z))
                locs.add(Location(location.getWorld(), x - 1, y, z))
                locs.add(Location(location.getWorld(), x + 1, y + 1, z))
                locs.add(Location(location.getWorld(), x - 1, y + 1, z))
                locs.add(Location(location.getWorld(), x + 1, y - 1, z))
                locs.add(Location(location.getWorld(), x - 1, y - 1, z))
            }

            2 -> {
                locs.add(Location(location.getWorld(), x, y, z + 1))
                locs.add(Location(location.getWorld(), x, y, z - 1))
                locs.add(Location(location.getWorld(), x + 1, y, z))
                locs.add(Location(location.getWorld(), x - 1, y, z))
                locs.add(Location(location.getWorld(), x + 1, y, z + 1))
                locs.add(Location(location.getWorld(), x - 1, y, z + 1))
                locs.add(Location(location.getWorld(), x - 1, y, z - 1))
                locs.add(Location(location.getWorld(), x + 1, y, z - 1))
            }

            else -> {}
        }

        locs.forEach(Consumer { location1: Location ->
            val block1 = location1.block
            if (block1.type != Material.AIR && !wrongTerritory(town,block1) && isBreakableByShovel(block1)){

                block1.breakNaturally(itemInHand)
            }
        })
    }

    private fun getDirection(player: Player, block: Block): Int {
        if (player.location.y > block.location.y) return 2 // Down

        var rot = ((player.location.yaw - 90) % 360).toDouble()
        if (rot < 0) rot += 360.0

        return if (0 <= rot && rot < 67.5) {
            0 // North
        } else if (67.5 <= rot && rot < 157.5) {
            1 // East
        } else if (157.5 <= rot && rot < 247.5) {
            0 // South
        } else if (247.5 <= rot && rot < 337.5) {
            1 // West
        } else if (337.5 <= rot && rot < 360.0) {
            0 // North
        } else {
            3
        }
    }

    private fun wrongTerritory(town: Town, block: Block) : Boolean{

        val territoryID = Nodes.getTerritoryFromBlock(block.x,block.z)?.id

        return !town.territories.contains(territoryID)
    }

    private fun isBreakableByShovel(block: Block) : Boolean{
        val material = block.type

        return when (material) {
            // Metal blocks
            Material.CLAY,
            Material.COARSE_DIRT,
            Material.DIRT,
            Material.DIRT_PATH,
            Material.FARMLAND,
            Material.GRASS_BLOCK,
            Material.GRAVEL,
            Material.MUD,
            Material.MUDDY_MANGROVE_ROOTS,
            Material.MYCELIUM,
            Material.PODZOL,
            Material.RED_SAND,
            Material.ROOTED_DIRT,
            Material.SAND,
            Material.SNOW,
            Material.SNOW_BLOCK,
            Material.SOUL_SAND,
            Material.SOUL_SOIL,
            Material.SUSPICIOUS_GRAVEL,
            Material.SUSPICIOUS_SAND,

                // New concrete powders
            Material.WHITE_CONCRETE_POWDER,
            Material.ORANGE_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.YELLOW_CONCRETE_POWDER,
            Material.LIME_CONCRETE_POWDER,
            Material.PINK_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.CYAN_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER,
            Material.BROWN_CONCRETE_POWDER,
            Material.GREEN_CONCRETE_POWDER,
            Material.RED_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER, -> true

            else -> false
        }
    }
}