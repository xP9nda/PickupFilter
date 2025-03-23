package xp9nda.pickupFilter.handlers.cmds.filter;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.FilterMode;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.UUID;

public class FilterAddHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public FilterAddHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to register a new profile
    @CommandMethod("pickupfilter|pf|itemfilter|if add [item]")
    @CommandPermission("pickupfilter.add")
    private void onFilterAddCommand(
            Player commandSender,
            @Argument(value = "item") Material materialToAdd
    )
    {
        // check that the user has an active profile
        PickupUser userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());

        // if the player has no user data, which realistically should never happen, but just in case, attempt to create a new user data object for them
        if (userData == null) {
            plugin.getDataHolder().addPlayerData(commandSender.getUniqueId(), new PickupUser());
            userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());
        }

        // get the active profile
        UUID activeProfileUUID = userData.getActiveProfileUUID();
        PickupProfile activeProfile = userData.getPickupProfile(activeProfileUUID);

        // if the active profile is null, the user has no active profile, tell them and return
        if (activeProfile == null) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNoActiveProfileMessage()
            ));
            return;
        }

        // if the player provided a material in the command, then favour it over what is being held
        ItemStack itemStackToFilter;
        if (!(materialToAdd == null)) {
            Material material = Material.getMaterial(materialToAdd.toString());

            // If the provided material was invalid, send a message and return
            if (material == null || material.isEmpty()) {
                commandSender.sendMessage(miniMessage.deserialize(plugin.getConfigHandler().getProfileInvalidMaterialMessage()));
                return;
            }

            // check that the material is a valid item
            if (!material.isItem()) {
                commandSender.sendMessage(miniMessage.deserialize(plugin.getConfigHandler().getProfileInvalidMaterialMessage()));
                return;
            }

            itemStackToFilter = new ItemStack(material);
        } else {
            // if the player did not provide a material, then use what they are holding, but clone it so that the player's item is not changed
            itemStackToFilter = commandSender.getInventory().getItemInMainHand().clone();

            // if the player isn't holding anything, then tell them
            if (itemStackToFilter.getType() == Material.AIR) {
                commandSender.sendMessage(miniMessage.deserialize(plugin.getConfigHandler().getProfileNoHeldItemMessage()));
                return;
            }

            // check that the item is actually an item
            if (!itemStackToFilter.getType().isItem()) {
                commandSender.sendMessage(miniMessage.deserialize(plugin.getConfigHandler().getProfileInvalidMaterialMessage()));
                return;
            }
        }

        // set the item quantity to be 1, as we don't really care about the quantity of the item, just the type
        itemStackToFilter.setAmount(1);

        // check if this item is already in the list of active filter player items
        UUID playerUUID = commandSender.getUniqueId();
        if (plugin.getDataHolder().isPlayerActiveProfileItem(playerUUID, itemStackToFilter)) {
            commandSender.sendMessage(miniMessage.deserialize(plugin.getConfigHandler().getProfileItemAlreadyInFilterMessage()));
            return;
        }

        // ensure the item is not already in the player's filter
        String serializedItem = plugin.getDataHolder().getSerializedStringFromItemStack(itemStackToFilter);

        // if the item has not been serialized, then serialize it using the datautils
        if (serializedItem == null) {
            serializedItem = plugin.getDataUtils().itemStackToString(itemStackToFilter);
        }

        if (activeProfile.getPickupFilter().contains(serializedItem)) {
            commandSender.sendMessage(miniMessage.deserialize(plugin.getConfigHandler().getProfileItemAlreadyInFilterMessage()));
            return;
        }

        // if the item has been serialized, then update the number of profiles tracking it
        plugin.getDataHolder().addSerializedStringAndItemStack(serializedItem, itemStackToFilter);

        // the item is not in the profile's filter list, so add it
        activeProfile.addFilterString(serializedItem);

        // add the item to the player tracked filter itemstacks
        plugin.getDataHolder().addPlayerActiveProfileItem(playerUUID, itemStackToFilter);

        // send a success message to the player
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getProfileItemAddedToFilterMessage(),
                Placeholder.component("item_name", itemStackToFilter.displayName()),
                Placeholder.unparsed("profile_name", activeProfile.getProfileName())
        ));
    }

}
