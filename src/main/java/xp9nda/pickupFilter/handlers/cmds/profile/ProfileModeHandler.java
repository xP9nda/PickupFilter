package xp9nda.pickupFilter.handlers.cmds.profile;

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

public class ProfileModeHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ProfileModeHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to register a new profile
    @CommandMethod("pickupfilter|pf|itemfilter|if profile mode <profile> <mode>")
    @CommandPermission("pickupfilter.profile.mode")
    private void onFilterProfileCommand(
            Player commandSender,
            @Argument(value = "profile", suggestions = "profile_names") String profileName,
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

        boolean doesAProfileExistWithName = userData.userHasProfileWithName(profileName);
        if (!doesAProfileExistWithName) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getGeneralProfileNameInvalidMessage(),
                    Placeholder.unparsed("profile_name", profileName)
            ));
            return;
        }

        // get the profile with the provided name
        PickupProfile profile = userData.getPickupProfile(profileName);

        // set the new mode
        profile.setFilterMode(newMode);

        // tell the user that the mode has been updated
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getProfileStateChangedMessage(),
                Placeholder.unparsed("profile_name", profileName),
                Placeholder.unparsed("profile_state", newMode.toString().toLowerCase())
        ));
    }

}
