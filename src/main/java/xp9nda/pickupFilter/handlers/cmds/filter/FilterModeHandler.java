package xp9nda.pickupFilter.handlers.cmds.filter;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.FilterMode;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.UUID;

public class FilterModeHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public FilterModeHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to register a new profile
    @CommandMethod("pickupfilter|pf|itemfilter|if mode <mode>")
    @CommandPermission("pickupfilter.mode")
    private void onFilterModeCommand(
            Player commandSender,
            @Argument(value = "mode") FilterMode newMode
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

        // update the filter mode of the active profile
        activeProfile.setFilterMode(newMode);

        // send a success message to the player
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getFilterModeSetMessage(),
                Placeholder.unparsed("filter_mode", newMode.toString().toLowerCase()),
                Placeholder.unparsed("profile_name", activeProfile.getProfileName())
        ));
    }

}
