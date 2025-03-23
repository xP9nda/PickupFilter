package xp9nda.pickupFilter.handlers;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.DataHolder;
import xp9nda.pickupFilter.data.data.FilterMode;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.UUID;

public class PickupHandler implements Listener {

    private final PickupFilter plugin;
    private DataHolder dataHolder;

    public PickupHandler(PickupFilter plugin) {
        this.plugin = plugin;
        this.dataHolder = plugin.getDataHolder();

        // check if EcoEnchants is enabled
        if (Bukkit.getPluginManager().isPluginEnabled("EcoEnchants")) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
        }
    }

    public boolean shouldItemBePickedUp(Player player, ItemStack item) {
        // get the user data
        PickupUser userData = plugin.getDataHolder().getPlayerData(player.getUniqueId());

        // if the user's itemfilter is disabled, then allow pickup
        if (!userData.isPickupEnabled()) {
            return true;
        }

        // get the active profile
        PickupProfile activeProfile = userData.getPickupProfile(userData.getActiveProfileUUID());

        // if the user has no active profile, allow pickup
        if (activeProfile == null) {
            return true;
        }

        ItemStack itemStackQuantity1 = item.clone();
        itemStackQuantity1.setAmount(1);

        // get the active profile items and check if the item is in the profile
        UUID playerUUID = player.getUniqueId();
        boolean isItemInProfile = plugin.getDataHolder().isPlayerActiveProfileItem(playerUUID, itemStackQuantity1);

        // if the filter is on whitelist mode, then only allow pickup if the item is in the filter
        if (activeProfile.getFilterMode() == FilterMode.WHITELIST) {
            return isItemInProfile;
        }

        // if the filter is on blacklist mode, then only allow pickup if the item is not in the filter
        return !isItemInProfile;
    }

    public void handleDroppingItem(Location location, ItemStack itemStack) {
        location.getWorld().dropItemNaturally(location, itemStack);
    }

    // listen to item pickup events
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        // check if the entity is a player
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        // get the player
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();

        boolean isPickupAllowed = shouldItemBePickedUp(player, item);

        // if the item should not be picked up, cancel the event
        event.setCancelled(!isPickupAllowed);
    }
}
