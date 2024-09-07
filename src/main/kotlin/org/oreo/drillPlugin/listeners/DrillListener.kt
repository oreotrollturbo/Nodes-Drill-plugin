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


class DrillListener : Listener {

    @EventHandler
    fun onPlayerUseDrill(e: BlockBreakEvent){
        val player = e.player
        val itemInHand = player.itemInHand

        if (e.isCancelled || !ItemManager.isDrill(itemInHand)){
            return
        }

        if (GetNodesInfo.isWarOn()){
            e.isCancelled = true
            player.sendMessage("§4You cannot use drills during war time")
            return
        }

        val block = e.block
        val town = Nodes.getResident(player)?.town

        if (town == null){
            e.isCancelled = true
            player.sendMessage("§4You need to be part of a town to use drills")
            return
        } else if (wrongTerritory(town,block)){
            e.isCancelled = true
            player.sendMessage("§4You cannot drill outside of your town")
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
            if (block1.type != Material.AIR && block1.type != Material.BEDROCK
                && !wrongTerritory(town,block1) && isBreakableByPickaxe(block1)){

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

    private fun isBreakableByPickaxe(block: Block) : Boolean{
        val material = block.type

        return when (material) {
            // Metal blocks
            Material.ANVIL, Material.BELL, Material.REDSTONE_BLOCK, Material.BREWING_STAND, Material.CAULDRON,
            Material.CHAIN, Material.HOPPER, Material.IRON_BARS, Material.IRON_DOOR, Material.IRON_TRAPDOOR,
            Material.LANTERN, Material.SOUL_LANTERN, Material.LIGHT_WEIGHTED_PRESSURE_PLATE, Material.HEAVY_WEIGHTED_PRESSURE_PLATE,
            Material.COPPER_BLOCK, Material.IRON_BLOCK, Material.LAPIS_BLOCK, Material.CUT_COPPER, Material.CUT_COPPER_SLAB,
            Material.CUT_COPPER_STAIRS, Material.LIGHTNING_ROD, Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK,
            Material.GOLD_BLOCK, Material.NETHERITE_BLOCK, Material.PISTON, Material.STICKY_PISTON,
            Material.CONDUIT, Material.SHULKER_BOX,

                // Rock I blocks
            Material.AMETHYST_CLUSTER, Material.ANDESITE, Material.BASALT, Material.POLISHED_BASALT,
            Material.SMOOTH_BASALT, Material.BLACKSTONE, Material.BLAST_FURNACE, Material.AMETHYST_BLOCK, Material.COAL_BLOCK,
            Material.QUARTZ_BLOCK, Material.BONE_BLOCK, Material.BRICKS, Material.BUDDING_AMETHYST, Material.COAL_ORE,
            Material.COBBLED_DEEPSLATE, Material.COBBLESTONE, Material.DARK_PRISMARINE, Material.DEEPSLATE,
            Material.DEEPSLATE_COAL_ORE, Material.DIORITE, Material.DISPENSER, Material.DRIPSTONE_BLOCK, Material.DROPPER,
            Material.ENCHANTING_TABLE, Material.END_STONE, Material.ENDER_CHEST, Material.FURNACE, Material.GILDED_BLACKSTONE,
            Material.GRANITE, Material.GRINDSTONE, Material.LODESTONE, Material.MOSSY_COBBLESTONE, Material.MOSSY_STONE_BRICKS,
            Material.MUD_BRICKS, Material.NETHER_BRICKS, Material.NETHER_BRICK_FENCE, Material.NETHER_GOLD_ORE,
            Material.NETHER_QUARTZ_ORE, Material.NETHERRACK, Material.OBSERVER, Material.PACKED_MUD, Material.PRISMARINE,
            Material.PRISMARINE_BRICKS, Material.POINTED_DRIPSTONE, Material.POLISHED_ANDESITE, Material.POLISHED_BLACKSTONE,
            Material.POLISHED_BLACKSTONE_BRICKS, Material.POLISHED_BLACKSTONE_BUTTON, Material.POLISHED_BLACKSTONE_PRESSURE_PLATE,
            Material.POLISHED_DIORITE, Material.POLISHED_GRANITE, Material.RED_SANDSTONE, Material.SANDSTONE, Material.SMOKER,
            Material.STONECUTTER, Material.STONE, Material.STONE_BRICKS, Material.STONE_BUTTON, Material.STONE_PRESSURE_PLATE,
            Material.TERRACOTTA,

                // Additional blocks including wall variants
            Material.POLISHED_DEEPSLATE, Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_TILES, Material.END_STONE_BRICKS,
            Material.RED_NETHER_BRICKS,

                // Wall variants
            Material.COBBLESTONE_WALL, Material.MOSSY_COBBLESTONE_WALL, Material.STONE_BRICK_WALL, Material.MOSSY_STONE_BRICK_WALL,
            Material.PRISMARINE_WALL, Material.BRICK_WALL, Material.DEEPSLATE_BRICK_WALL, Material.DEEPSLATE_TILE_WALL,
            Material.RED_NETHER_BRICK_WALL, Material.POLISHED_BLACKSTONE_BRICK_WALL, Material.SANDSTONE_WALL,
            Material.RED_SANDSTONE_WALL,

                // Terracotta variants
            Material.WHITE_TERRACOTTA, Material.ORANGE_TERRACOTTA, Material.MAGENTA_TERRACOTTA,
            Material.LIGHT_BLUE_TERRACOTTA, Material.YELLOW_TERRACOTTA, Material.LIME_TERRACOTTA, Material.PINK_TERRACOTTA,
            Material.GRAY_TERRACOTTA, Material.LIGHT_GRAY_TERRACOTTA, Material.CYAN_TERRACOTTA, Material.PURPLE_TERRACOTTA,
            Material.BLUE_TERRACOTTA, Material.BROWN_TERRACOTTA, Material.GREEN_TERRACOTTA, Material.RED_TERRACOTTA,
            Material.BLACK_TERRACOTTA,

                // Glazed Terracotta variants
            Material.WHITE_GLAZED_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA,
            Material.LIGHT_BLUE_GLAZED_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA,
            Material.PINK_GLAZED_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA,
            Material.CYAN_GLAZED_TERRACOTTA, Material.PURPLE_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA,
            Material.BROWN_GLAZED_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA,
            Material.BLACK_GLAZED_TERRACOTTA,

                // Concrete variants
            Material.WHITE_CONCRETE, Material.ORANGE_CONCRETE, Material.MAGENTA_CONCRETE, Material.LIGHT_BLUE_CONCRETE,
            Material.YELLOW_CONCRETE, Material.LIME_CONCRETE, Material.PINK_CONCRETE, Material.GRAY_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE, Material.CYAN_CONCRETE, Material.PURPLE_CONCRETE, Material.BLUE_CONCRETE,
            Material.BROWN_CONCRETE, Material.GREEN_CONCRETE, Material.RED_CONCRETE, Material.BLACK_CONCRETE  -> true
            else -> false
        }
    }
}