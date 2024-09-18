package org.oreo.drillPlugin.items

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

object ItemManager {
    private var plugin: JavaPlugin? = null
    var drill: ItemStack? = null
    var digger: ItemStack? = null
    var chopper: ItemStack? = null

    private var drillModel: Int? = null
    private var diggerModel: Int? = null
    private var chopperModel: Int? = null

    /**
     * Item initialisation
     */
    fun init(pluginInstance: JavaPlugin?) {
        plugin = pluginInstance
        drillModel = plugin?.config?.getInt("drill-main-model")
        diggerModel = plugin?.config?.getInt("digger-main-model")
        chopperModel = plugin?.config?.getInt("chopper-main-model")
        createItems()
    }

    /**
     * Creates the item
     */
    private fun createItems() {
        drill = createDrillItem()
        digger = createDiggerItem()
        chopper = createChopperItem()
    }

    /**
     * @return the item
     * Makes the drill item , gives it the enchantment glow description and lore
     */
    fun createDrillItem(): ItemStack {
        val item = ItemStack(Material.NETHERITE_PICKAXE, 1)
        val meta = item.itemMeta

        if (meta != null) {
            meta.setDisplayName("§eDrill")

            val lore: MutableList<String> = ArrayList()
            lore.add("§7Make 3x3 holes with ease")
            lore.add("§4WARNING: this item does not drop ores")
            lore.add("§5\"oreoDrillinator 300\"")
            meta.lore = lore

            meta.addEnchant(Enchantment.LUCK, 1, true)
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS) //to add the enchant glint but not have it be visible

            meta.setCustomModelData(drillModel)

            item.setItemMeta(meta)
        }
        return item
    }

    /**
     * @return the item
     * Makes the drill item , gives it the enchantment glow description and lore
     */
    fun createDiggerItem(): ItemStack {
        val item = ItemStack(Material.NETHERITE_SHOVEL, 1)
        val meta = item.itemMeta

        if (meta != null) {
            meta.setDisplayName("§eDigger")

            val lore: MutableList<String> = ArrayList()
            lore.add("§7Clear out dirt like its nothing")
            lore.add("§5\"oreoDigatron 800\"")
            meta.lore = lore

            meta.addEnchant(Enchantment.LUCK, 1, true)
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS) //to add the enchant glint but not have it be visible

            meta.setCustomModelData(diggerModel)

            item.setItemMeta(meta)
        }
        return item
    }

    /**
     * @return the item
     * Makes the drill item , gives it the enchantment glow description and lore
     */
    fun createChopperItem(): ItemStack {
        val item = ItemStack(Material.NETHERITE_AXE, 1)
        val meta = item.itemMeta

        if (meta != null) {
            meta.setDisplayName("§eChopperr")

            val lore: MutableList<String> = ArrayList()
            lore.add("§7Chop wood in a 3x3 radius")
            lore.add("§5\"oreoChoppinator 2000\"")
            meta.lore = lore

            meta.addEnchant(Enchantment.LUCK, 1, true)
            meta.addEnchant(Enchantment.SILK_TOUCH, 1, true)
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS) //to add the enchant glint but not have it be visible

            meta.setCustomModelData(chopperModel)

            item.setItemMeta(meta)
        }
        return item
    }

    public fun isDrill(item:ItemStack) : Boolean{

        val drill = ItemManager.drill ?: return false

        val itemMetaInHand = item.itemMeta ?: return false
        val drillMeta = drill.itemMeta ?: return false

        // Check display name
        if (itemMetaInHand.displayName != drillMeta.displayName) {
            return false
        }

        // Check lore
        if (itemMetaInHand.lore != drillMeta.lore) {
            return false
        }

        // Check enchantments
        if (itemMetaInHand.enchants != drillMeta.enchants) {
            return false
        }

        return true
    }

    public fun isDigger(item:ItemStack) : Boolean{

        val digger = ItemManager.digger ?: return false

        val itemMetaInHand = item.itemMeta ?: return false
        val diggerMeta = digger.itemMeta ?: return false

        // Check display name
        if (itemMetaInHand.displayName != diggerMeta.displayName) {
            return false
        }

        // Check lore
        if (itemMetaInHand.lore != diggerMeta.lore) {
            return false
        }

        // Check enchantments
        if (itemMetaInHand.enchants != diggerMeta.enchants) {
            return false
        }

        return true
    }

    public fun isChopper(item:ItemStack) : Boolean{

        val chopper = ItemManager.chopper ?: return false

        val itemMetaInHand = item.itemMeta ?: return false
        val chopperMeta = chopper.itemMeta ?: return false

        // Check display name
        if (itemMetaInHand.displayName != chopperMeta.displayName) {
            return false
        }

        // Check lore
        if (itemMetaInHand.lore != chopperMeta.lore) {
            return false
        }

        // Check enchantments
        if (itemMetaInHand.enchants != chopperMeta.enchants) {
            return false
        }

        return true
    }
}
