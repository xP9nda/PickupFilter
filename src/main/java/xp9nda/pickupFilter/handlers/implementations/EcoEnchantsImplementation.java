package xp9nda.pickupFilter.handlers.implementations;

import com.willfp.eco.core.events.DropQueuePushEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xp9nda.pickupFilter.PickupFilter;

import java.util.Collection;

public class EcoEnchantsImplementation implements Listener {

    private final PickupFilter plugin;

    public EcoEnchantsImplementation(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // listen to telekinesis ecoenchants events
    @EventHandler(ignoreCancelled = true)
    public final void handleDropQueue(@NotNull DropQueuePushEvent event) {
        // if the event is telekinetic, then check if the items should be picked up
        if (event.isTelekinetic()) {
            Player player = event.getPlayer();
            Collection<? extends ItemStack> items = event.getItems();

            for (ItemStack item : items) {
                boolean isPickupAllowed = plugin.getPickupHandler().shouldItemBePickedUp(player, item);

                // if the item should not be picked up, cancel the event and drop the item on the ground
                if (!isPickupAllowed) {
                    event.setCancelled(true);
                    plugin.getPickupHandler().handleDroppingItem(event.getLocation(), item);
                }
            }
        }
    }

}
