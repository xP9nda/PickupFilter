package xp9nda.pickupFilter.handlers.implementations;

import com.willfp.eco.core.events.DropQueuePushEvent;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xp9nda.pickupFilter.PickupFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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

            Collection<ItemStack> itemsToRemove = new ArrayList<>();
            for (ItemStack item : items) {
                boolean isPickupAllowed = plugin.getPickupHandler().shouldItemBePickedUp(player, item);

                // if the item should not be picked up, remove the item from the list of items to reward the player with
                if (!isPickupAllowed) {
                    itemsToRemove.add(item);

                    plugin.getPickupHandler().handleDroppingItem(event.getLocation(), item);
                }
            }

            // remove any items that have been queued for removal as they should not be allowed
            int xpToGive = event.getXp();
            boolean hasXpBeenGiven = false;

            if (!itemsToRemove.isEmpty()) {
                for (ItemStack itemToRemove : itemsToRemove) {
                    items.remove(itemToRemove);

                    // manually provide the player with the experience that should have been given
                    if ((xpToGive > 0) && !hasXpBeenGiven) {
                        player.giveExp(xpToGive, true);
                    }
                }

                // update the event items
                event.setItems(items);
            }

        }
    }

}
