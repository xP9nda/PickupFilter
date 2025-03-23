package xp9nda.pickupFilter.handlers.cmds.base;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import fr.minuskube.inv.SmartInventory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupUser;
import xp9nda.pickupFilter.handlers.menus.ItemFilterProfilesMenu;

public class ProfileMenuHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ProfileMenuHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to register a new profile
    @CommandMethod("pickupfilter|pf|itemfilter|if")
    @CommandPermission("pickupfilter.profiles")
    private void onFilterEditCommand(Player commandSender) {
        // check that the user has an active profile
        PickupUser userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());

        // if the player has no user data, which realistically should never happen, but just in case, attempt to create a new user data object for them
        if (userData == null) {
            plugin.getDataHolder().addPlayerData(commandSender.getUniqueId(), new PickupUser());
            userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());
        }

        // construct the menu name
        Component menuName = miniMessage.deserialize(
                plugin.getConfigHandler().getProfileMenuTitle()
        );

        String menuNameString = PlainTextComponentSerializer.plainText().serialize(menuName);

        // open the menu
        ItemFilterProfilesMenu itemFilterProfilesMenu = new ItemFilterProfilesMenu(plugin, commandSender.getUniqueId(), menuNameString);
        SmartInventory inventory = itemFilterProfilesMenu.buildMenu();

        inventory.open(commandSender);
    }
}