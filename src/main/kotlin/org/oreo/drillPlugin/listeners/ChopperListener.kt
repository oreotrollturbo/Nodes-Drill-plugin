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


class ChopperListener : Listener {

    @EventHandler
    fun onPlayerUseChopper(e: BlockBreakEvent){
        val player = e.player
        val itemInHand = player.itemInHand

        if (e.isCancelled || !ItemManager.isChopper(itemInHand)){
            return
        }

        if (GetNodesInfo.isWarOn()){
            e.isCancelled = true
            player.sendMessage("§4You cannot use choppers during war time")
            return
        }

        val block = e.block
        val town = Nodes.getResident(player)?.town

        if (town == null){
            e.isCancelled = true
            player.sendMessage("§4You need to be part of a town to use choppers")
            return
        } else if (wrongTerritory(town,block)){
            e.isCancelled = true
            player.sendMessage("§4You cannot chop outside of your town")
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
            if (block1.type != Material.AIR && !wrongTerritory(town,block1) && isBreakableByAxe(block1)){

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

    private fun isBreakableByAxe(block: Block) : Boolean{
        val material = block.type

        return when (material) {
            // Metal blocks
            Material.OAK_TRAPDOOR,
            Material.OAK_DOOR,
            Material.BARREL,
            Material.CARTOGRAPHY_TABLE,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.CRAFTING_TABLE,
            Material.FLETCHING_TABLE,
            Material.LECTERN,
            Material.LOOM,
            Material.SMITHING_TABLE,
            Material.BAMBOO_MOSAIC,
            Material.BAMBOO_PLANKS,
            Material.CAMPFIRE,
            Material.OAK_FENCE_GATE,
            Material.JUKEBOX,
            Material.OAK_LOG,
            Material.OAK_PLANKS,
            Material.OAK_SLAB,
            Material.OAK_STAIRS,
            Material.BOOKSHELF,
            Material.CHISELED_BOOKSHELF,
            Material.JACK_O_LANTERN,
            Material.MELON,
            Material.PUMPKIN,
            Material.OAK_SIGN,
            Material.OAK_HANGING_SIGN,
            Material.NOTE_BLOCK,
            Material.MANGROVE_ROOTS,
            Material.OAK_PRESSURE_PLATE,
            Material.BEEHIVE,
            Material.LADDER,
            Material.BEE_NEST,
            Material.COMPOSTER,
            Material.BAMBOO,
            Material.RED_BED,
            Material.COCOA,
            Material.DAYLIGHT_DETECTOR,
            Material.VINE,

            Material.SPRUCE_LOG,
            Material.BIRCH_LOG,
            Material.JUNGLE_LOG,
            Material.ACACIA_LOG,
            Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG,
            Material.CHERRY_LOG,
            Material.CRIMSON_STEM,
            Material.WARPED_STEM,
// Stripped Logs
            Material.STRIPPED_OAK_LOG,
            Material.STRIPPED_SPRUCE_LOG,
            Material.STRIPPED_BIRCH_LOG,
            Material.STRIPPED_JUNGLE_LOG,
            Material.STRIPPED_ACACIA_LOG,
            Material.STRIPPED_DARK_OAK_LOG,
            Material.STRIPPED_MANGROVE_LOG,
            Material.STRIPPED_CHERRY_LOG,
            Material.STRIPPED_CRIMSON_STEM,
            Material.STRIPPED_WARPED_STEM,

                // Planks
            Material.SPRUCE_PLANKS,
            Material.BIRCH_PLANKS,
            Material.JUNGLE_PLANKS,
            Material.ACACIA_PLANKS,
            Material.DARK_OAK_PLANKS,
            Material.MANGROVE_PLANKS,
            Material.CHERRY_PLANKS,
            Material.CRIMSON_PLANKS,
            Material.WARPED_PLANKS,

            Material.SPRUCE_DOOR,
            Material.BIRCH_DOOR,
            Material.JUNGLE_DOOR,
            Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR,
            Material.MANGROVE_DOOR,
            Material.CHERRY_DOOR,
            Material.BAMBOO_DOOR,
            Material.CRIMSON_DOOR,
            Material.WARPED_DOOR,

            Material.SPRUCE_TRAPDOOR,
            Material.BIRCH_TRAPDOOR,
            Material.JUNGLE_TRAPDOOR,
            Material.ACACIA_TRAPDOOR,
            Material.DARK_OAK_TRAPDOOR,
            Material.MANGROVE_TRAPDOOR,
            Material.CHERRY_TRAPDOOR,
            Material.BAMBOO_TRAPDOOR,
            Material.CRIMSON_TRAPDOOR,
            Material.WARPED_TRAPDOOR,

            Material.OAK_FENCE,
            Material.SPRUCE_FENCE,
            Material.BIRCH_FENCE,
            Material.JUNGLE_FENCE,
            Material.ACACIA_FENCE,
            Material.DARK_OAK_FENCE,
            Material.MANGROVE_FENCE,
            Material.CHERRY_FENCE,
            Material.BAMBOO_FENCE,
            Material.CRIMSON_FENCE,
            Material.WARPED_FENCE,
            Material.NETHER_BRICK_FENCE,

            Material.SPRUCE_FENCE_GATE,
            Material.BIRCH_FENCE_GATE,
            Material.JUNGLE_FENCE_GATE,
            Material.ACACIA_FENCE_GATE,
            Material.DARK_OAK_FENCE_GATE,
            Material.MANGROVE_FENCE_GATE,
            Material.CHERRY_FENCE_GATE,
            Material.BAMBOO_FENCE_GATE,
            Material.CRIMSON_FENCE_GATE,
            Material.WARPED_FENCE_GATE,

            Material.SPRUCE_SIGN,
            Material.BIRCH_SIGN,
            Material.JUNGLE_SIGN,
            Material.ACACIA_SIGN,
            Material.DARK_OAK_SIGN,
            Material.MANGROVE_SIGN,
            Material.CHERRY_SIGN,
            Material.BAMBOO_SIGN,
            Material.CRIMSON_SIGN,
            Material.WARPED_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.MANGROVE_WALL_SIGN,
            Material.CHERRY_WALL_SIGN,
            Material.BAMBOO_WALL_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.WARPED_WALL_SIGN,

            Material.SPRUCE_SLAB,
            Material.BIRCH_SLAB,
            Material.JUNGLE_SLAB,
            Material.ACACIA_SLAB,
            Material.DARK_OAK_SLAB,
            Material.MANGROVE_SLAB,
            Material.CHERRY_SLAB,
            Material.BAMBOO_SLAB,
            Material.CRIMSON_SLAB,
            Material.WARPED_SLAB,
            Material.BAMBOO_MOSAIC_SLAB,
            Material.PETRIFIED_OAK_SLAB,

            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.BIRCH_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.ACACIA_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.MANGROVE_BUTTON,
            Material.CHERRY_BUTTON,
            Material.BAMBOO_BUTTON,
            Material.CRIMSON_BUTTON,
            Material.WARPED_BUTTON,
            Material.STONE_BUTTON,
            Material.POLISHED_BLACKSTONE_BUTTON,

            Material.TORCH,
            Material.REDSTONE_TORCH,
            Material.SOUL_TORCH,

            Material.BROWN_MUSHROOM_BLOCK,
            Material.RED_MUSHROOM_BLOCK,
            Material.MUSHROOM_STEM,

            Material.WHITE_BANNER,
            Material.ORANGE_BANNER,
            Material.MAGENTA_BANNER,
            Material.LIGHT_BLUE_BANNER,
            Material.YELLOW_BANNER,
            Material.LIME_BANNER,
            Material.PINK_BANNER,
            Material.GRAY_BANNER,
            Material.LIGHT_GRAY_BANNER,
            Material.CYAN_BANNER,
            Material.PURPLE_BANNER,
            Material.BLUE_BANNER,
            Material.BROWN_BANNER,
            Material.GREEN_BANNER,
            Material.RED_BANNER,
            Material.BLACK_BANNER,

            Material.WHITE_WALL_BANNER,
            Material.ORANGE_WALL_BANNER,
            Material.MAGENTA_WALL_BANNER,
            Material.LIGHT_BLUE_WALL_BANNER,
            Material.YELLOW_WALL_BANNER,
            Material.LIME_WALL_BANNER,
            Material.PINK_WALL_BANNER,
            Material.GRAY_WALL_BANNER,
            Material.LIGHT_GRAY_WALL_BANNER,
            Material.CYAN_WALL_BANNER,
            Material.PURPLE_WALL_BANNER,
            Material.BLUE_WALL_BANNER,
            Material.BROWN_WALL_BANNER,
            Material.GREEN_WALL_BANNER,
            Material.RED_WALL_BANNER,
            Material.BLACK_WALL_BANNER,

            Material.OAK_WOOD,
            Material.SPRUCE_WOOD,
            Material.BIRCH_WOOD,
            Material.JUNGLE_WOOD,
            Material.ACACIA_WOOD,
            Material.DARK_OAK_WOOD,
            Material.MANGROVE_WOOD,
            Material.CHERRY_WOOD,
            Material.CRIMSON_HYPHAE,
            Material.WARPED_HYPHAE,

            Material.SPRUCE_PRESSURE_PLATE,
            Material.BIRCH_PRESSURE_PLATE,
            Material.JUNGLE_PRESSURE_PLATE,
            Material.ACACIA_PRESSURE_PLATE,
            Material.DARK_OAK_PRESSURE_PLATE,
            Material.MANGROVE_PRESSURE_PLATE,
            Material.CHERRY_PRESSURE_PLATE,
            Material.BAMBOO_PRESSURE_PLATE,
            Material.CRIMSON_PRESSURE_PLATE,
            Material.WARPED_PRESSURE_PLATE,
            Material.STONE_PRESSURE_PLATE,
            Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.LIGHT_WEIGHTED_PRESSURE_PLATE,

            Material.SPRUCE_STAIRS,
            Material.BIRCH_STAIRS,
            Material.JUNGLE_STAIRS,
            Material.ACACIA_STAIRS,
            Material.DARK_OAK_STAIRS,
            Material.MANGROVE_STAIRS,
            Material.CHERRY_STAIRS,
            Material.BAMBOO_STAIRS,
            Material.CRIMSON_STAIRS,
            Material.WARPED_STAIRS,
            Material.BAMBOO_MOSAIC_STAIRS,

            Material.STRIPPED_OAK_WOOD,        // Stripped Oak Wood
            Material.STRIPPED_SPRUCE_WOOD,     // Stripped Spruce Wood
            Material.STRIPPED_BIRCH_WOOD,      // Stripped Birch Wood
            Material.STRIPPED_JUNGLE_WOOD,     // Stripped Jungle Wood
            Material.STRIPPED_ACACIA_WOOD,     // Stripped Acacia Wood
            Material.STRIPPED_DARK_OAK_WOOD,   // Stripped Dark Oak Wood
            Material.STRIPPED_MANGROVE_WOOD,   // Stripped Mangrove Wood
            Material.STRIPPED_CHERRY_WOOD,     // Stripped Cherry Wood
            Material.STRIPPED_CRIMSON_HYPHAE,  // Stripped Crimson Hyphae
            Material.STRIPPED_WARPED_HYPHAE    // Stripped Warped Hyphae


            -> true

            else -> false
        }
    }
}