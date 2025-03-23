package xp9nda.pickupFilter.handlers.menus;

import fr.minuskube.inv.SmartInventory;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.HashMap;
import java.util.UUID;

public class ItemFilterEditListener implements Listener {

    private final PickupFilter plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final HashMap<UUID, SmartInventory> activeEditMenus = new HashMap<>();

    public void addActiveEditMenu(UUID menuUUID, SmartInventory menu) {
        activeEditMenus.put(menuUUID, menu);
    }

    public void removeActiveEditMenu(UUID menuUUID) {
        activeEditMenus.remove(menuUUID);
    }

    public ItemFilterEditListener(PickupFilter plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();

        // if the menu that was closed was an edit menu, remove it from the active menus
        if (activeEditMenus.containsKey(player.getUniqueId())) {
            removeActiveEditMenu(player.getUniqueId());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) {
            return;
        }

        // check that the user has the edit menu open
        if (!activeEditMenus.containsKey(event.getWhoClicked().getUniqueId())) {
            return;
        }

        ItemStack itemToAddToFilter = null;

        // if this was a regular left click
        if (event.getClick().isLeftClick() && !event.getClick().isShiftClick()) {
            // if the user is clicking within their own inventory, do nothing
            // as we only want to add items that are being clicked into the edit menu
            if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
                return;
            }

            itemToAddToFilter = event.getCursor();
            if (itemToAddToFilter.getType() == Material.AIR) {
                return;
            }

            // if the item is not a valid item, do nothing
            if (itemToAddToFilter.isEmpty()) {
                return;
            }
        }

        // if this was a shift click
        if (event.getClick().isShiftClick() && event.getClick().isLeftClick()) {
            // if the user is not clicking within their own inventory, do nothing
            // as we only want to add items that are in the inventory and being shift clicked into the edit menu
            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                return;
            }

            itemToAddToFilter = event.getCurrentItem();

            if (itemToAddToFilter == null) {
                return;
            }

            if (itemToAddToFilter.getType() == Material.AIR) {
                return;
            }

            // if the item is not a valid item, do nothing
            if (itemToAddToFilter.isEmpty()) {
                return;
            }
        }

        // if the item for some reason is null, safety check
        if (itemToAddToFilter == null) {
            return;
        }

        PickupUser userData = plugin.getDataHolder().getPlayerData(event.getWhoClicked().getUniqueId());
        UUID playerUUID = event.getWhoClicked().getUniqueId();

        SmartInventory inventory = activeEditMenus.get(playerUUID);
        ItemFilterEditMenu.ItemFilterEditProvider provider = (ItemFilterEditMenu.ItemFilterEditProvider) inventory.getProvider();

        if (provider == null) {
            return;
        }

        // get the profile UUID of the menu that is open
        UUID openMenuProfileUUID = provider.thisMenuProfileUUID;


        // add the item to the player's active profile
        PickupProfile profileOfOpenMenu = userData.getPickupProfile(openMenuProfileUUID);

        // if the active profile is null, then the player has no active profile, so tell them and return
        // realistically though this should never happen as the user won't be able to open the menu with no active profile,
        // but who knows if things might break
        if (profileOfOpenMenu == null) {
            return;
        }

        ItemStack itemStackQuantity1 = itemToAddToFilter.clone();
        itemStackQuantity1.setAmount(1);

        // add the item to the active profile
        // ensure the item is not already in the player's filter
        String serializedItem = plugin.getDataHolder().getSerializedStringFromItemStack(itemStackQuantity1);

        // if the item has not been serialized, then serialize it using the datautils
        if (serializedItem == null) {
            serializedItem = plugin.getDataUtils().itemStackToString(itemStackQuantity1);
        }

        event.setCancelled(true);

        if (profileOfOpenMenu.getPickupFilter().contains(serializedItem)) {
            event.getWhoClicked().sendMessage(miniMessage.deserialize(plugin.getConfigHandler().getProfileItemAlreadyInFilterMessage()));
            return;
        }

        // if the item has been serialized, then update the number of profiles tracking it
        // only if the player is using the profile that the menu is open for
        if (userData.getActiveProfileUUID() != null) {
            if (userData.getActiveProfileUUID().equals(openMenuProfileUUID)) {
                plugin.getDataHolder().addSerializedStringAndItemStack(serializedItem, itemStackQuantity1);
                plugin.getDataHolder().addPlayerActiveProfileItem(playerUUID, itemStackQuantity1);
            }
        }

        // the item is not in the profile's filter list, so add it
        profileOfOpenMenu.addFilterString(serializedItem);

        // tell the user
        event.getWhoClicked().sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getProfileItemAddedToFilterMessage(),
                Placeholder.component("item_name", itemToAddToFilter.displayName()),
                Placeholder.unparsed("profile_name", profileOfOpenMenu.getProfileName())
        ));

        // refresh the menu
        provider.refreshItems((Player) event.getWhoClicked(), provider.inventoryContents);
    }
}
