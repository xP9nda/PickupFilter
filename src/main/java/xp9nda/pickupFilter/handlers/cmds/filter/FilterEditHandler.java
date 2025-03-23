package xp9nda.pickupFilter.handlers.cmds.filter;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import fr.minuskube.inv.SmartInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;
import xp9nda.pickupFilter.handlers.menus.ItemFilterEditMenu;

import java.util.UUID;

public class FilterEditHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public FilterEditHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to register a new profile
    @CommandMethod("pickupfilter|pf|itemfilter|if edit")
    @CommandPermission("pickupfilter.edit")
    private void onFilterEditCommand(Player commandSender) {
        // check that the user has an active profile
        PickupUser userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());

        // if the player has no user data, which realistically should never happen, but just in case, attempt to create a new user data object for them
        if (userData == null) {
            plugin.getDataHolder().addPlayerData(commandSender.getUniqueId(), new PickupUser());
            userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());
        }

        // get the active profile
        UUID activeProfileUUID = userData.getActiveProfileUUID();

        if (activeProfileUUID == null) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNoActiveProfileMessage()
            ));
            return;
        }

        PickupProfile activeProfile = userData.getPickupProfile(activeProfileUUID);

        // if the active profile is null, the user has no active profile, tell them and return
        if (activeProfile == null) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNoActiveProfileMessage()
            ));
            return;
        }

        // construct the menu name
        Component menuName = miniMessage.deserialize(
                plugin.getConfigHandler().getEditMenuName(),
                Placeholder.unparsed("profile_name", activeProfile.getProfileName())
        );

        String menuNameString = PlainTextComponentSerializer.plainText().serialize(menuName);

        // open the menu
        ItemFilterEditMenu itemFilterEditMenu = new ItemFilterEditMenu(plugin, commandSender.getUniqueId(), menuNameString, activeProfileUUID);
        SmartInventory inventory = itemFilterEditMenu.buildMenu();

        inventory.open(commandSender);
        plugin.getItemFilterEditListener().addActiveEditMenu(commandSender.getUniqueId(), inventory);
    }
}