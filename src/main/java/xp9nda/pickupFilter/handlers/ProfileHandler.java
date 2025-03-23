package xp9nda.pickupFilter.handlers;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.FilterMode;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

public class ProfileHandler implements Listener {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ProfileHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }


    // command to see info about a profile
    @CommandMethod("pickupfilter|pf|itemfilter|if profile <profileName>")
    @CommandPermission("pickupfilter.profile.info")
    private void onInfoProfileCommand(
            Player commandSender,
            @Argument(value = "profileName") String profileName
    )
    {
        // check that the user provided a profile name
        if (profileName == null) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getGeneralProfileNameInvalidMessage(),
                    Placeholder.unparsed("profile_name", "")
            ));
            return;
        }

        // check that the user has a profile with the provided name
        PickupUser userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());

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

        // TODO: load the profile as a menu that the player can interact with to modify filters/settings
        commandSender.sendMessage("temp msg, profile: " + profileName + ", state: " + profile.getFilterMode());
    }



}
