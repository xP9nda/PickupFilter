package xp9nda.pickupFilter.handlers.cmds.profile;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.HashSet;
import java.util.UUID;

public class ProfileRenameHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ProfileRenameHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to use/activate a profile
    @CommandMethod("pickupfilter|pf|itemfilter|if profile rename <profile> <name>")
    @CommandPermission("pickupfilter.profile.rename")
    private void onProfileRenameCommand(
            Player commandSender,
            @Argument(value = "profile", suggestions = "profile_names") String profileName,
            @Argument(value = "name") String newName
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

        // check that the new profile name is within the length limits
        if (newName.length() < plugin.getConfigHandler().getProfileNameLengthMin() || newName.length() > plugin.getConfigHandler().getProfileNameLengthMax()) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNewProfileInvalidNameMessage()
            ));
            return;
        }

        // run through each character in the profile name, and if it is not in the allowed characters, do not allow creation
        HashSet<Character> allowedChars = plugin.getConfigHandler().getAllowedProfileNameCharactersSet();
        for (char c : newName.toCharArray()) {
            if (!allowedChars.contains(c)) {
                // send an error message to the player
                commandSender.sendMessage(miniMessage.deserialize(
                        plugin.getConfigHandler().getNewProfileInvalidNameMessage()
                ));
                return;
            }
        }

        // check that the user does not already have a profile with the same name
        if (userData.userHasProfileWithName(newName)) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNewProfileNameExistsMessage(),
                    Placeholder.unparsed("profile_name", newName)
            ));
            return;
        }

        // at this point the new profile name is valid
        PickupUser user = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());
        PickupProfile profile = user.getPickupProfile(profileName);

        // set the new name for the profile
        profile.setProfileName(newName);

        // send a success message to the player
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getProfileRenamedSuccessMessage(),
                Placeholder.unparsed("old_profile_name", profileName),
                Placeholder.unparsed("new_profile_name", newName)
        ));

    }

}
