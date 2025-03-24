package xp9nda.pickupFilter.handlers.cmds.profile;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.UUID;

public class ProfileDeleteHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ProfileDeleteHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to delete a profile
    @CommandMethod("pickupfilter|pf|itemfilter|if profile delete <profile> [confirmation]")
    @CommandPermission("pickupfilter.profile.delete")
    private void onProfileDeleteCommand(
            Player commandSender,
            @Argument(value = "profile", suggestions = "profile_names") String profileName,
            @Argument(value = "confirmation") String confirmation
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

        // get the player's current used profile
        UUID currentProfileUUID = userData.getActiveProfileUUID();

        // if the player has an active profile
        if (currentProfileUUID != null) {
            // get the UUID of the profile to delete
            UUID profileUUIDToRemove = userData.getPickupProfile(profileName).getProfileUUID();

            if (profileUUIDToRemove == null) {
                // send an error message to the player
                commandSender.sendMessage(miniMessage.deserialize(
                        plugin.getConfigHandler().getGeneralProfileNameInvalidMessage(),
                        Placeholder.unparsed("profile_name", profileName)
                ));
                return;
            }

            // if the player is trying to delete the profile they are currently using
            if (currentProfileUUID.equals(profileUUIDToRemove)) {
                // if the player did not provide the confirmation argument
                if (confirmation == null || !confirmation.equalsIgnoreCase("confirm")) {
                    // send an error message to the player
                    commandSender.sendMessage(miniMessage.deserialize(
                            plugin.getConfigHandler().getProfileDeleteConfirmationMessage(),
                            Placeholder.unparsed("profile_name", profileName)
                    ));
                    return;
                }

                // if the player did provide a valid confirmation argument
                // set the player's active profile to null
                userData.setActiveProfileUUID(null);

                userData.removePickupProfile(profileUUIDToRemove);

                // stop tracking the profile
                plugin.getDataHolder().clearPlayerActiveProfileItems(commandSender.getUniqueId());

                // tell the user they no longer have an active profile
                commandSender.sendMessage(miniMessage.deserialize(
                        plugin.getConfigHandler().getNoLongerHaveActiveProfileMessage()
                ));
             }
        } else {
            // if the player is not trying to delete the profile they are currently using then proceed ignoring the confirmation argument
            PickupProfile profile = userData.getPickupProfile(profileName);
            UUID profileUUID = profile.getProfileUUID();

            userData.removePickupProfile(profileUUID);
        }

        // send a success message to the player
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getProfileDeletedSuccessMessage(),
                Placeholder.unparsed("profile_name", profileName)
        ));
    }

}
