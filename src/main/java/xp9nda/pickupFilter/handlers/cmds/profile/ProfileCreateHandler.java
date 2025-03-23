package xp9nda.pickupFilter.handlers.cmds.profile;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.HashSet;

public class ProfileCreateHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ProfileCreateHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to register a new profile
    @CommandMethod("pickupfilter|pf|itemfilter|if profile create <name>")
    @CommandPermission("pickupfilter.profile.create")
    private void onNewProfileCommand(
            Player commandSender,
            @Argument(value = "name") String profileName
    )
    {
        // check that the user provided a profile name
        if (profileName == null) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNewProfileInvalidNameMessage()
            ));
            return;
        }

        // check that the profile name is within the length limits
        if (profileName.length() < plugin.getConfigHandler().getProfileNameLengthMin() || profileName.length() > plugin.getConfigHandler().getProfileNameLengthMax()) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNewProfileInvalidNameMessage()
            ));
            return;
        }

        // run through each character in the profile name, and if it is not in the allowed characters, do not allow creation
        HashSet<Character> allowedChars = plugin.getConfigHandler().getAllowedProfileNameCharactersSet();
        for (char c : profileName.toCharArray()) {
            if (!allowedChars.contains(c)) {
                // send an error message to the player
                commandSender.sendMessage(miniMessage.deserialize(
                        plugin.getConfigHandler().getNewProfileInvalidNameMessage()
                ));
                return;
            }
        }

        // check that the user does not already have a profile with the same name
        PickupUser userData = plugin.getDataHolder().getPlayerData(commandSender.getUniqueId());

        // check if the user is allowed to create a new profile
        int maxAllowedProfilesForUser = getMaxAllowedProfilesForUser(commandSender);
        int numberOfProfiles = userData.getPickupProfiles().size();

//        plugin.getSLF4JLogger().info("maxAllowedProfilesForUser: " + maxAllowedProfilesForUser + " | users numberOfProfiles: " + numberOfProfiles);

        if (numberOfProfiles >= maxAllowedProfilesForUser) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getMaxProfilesReachedMessage(),
                    Formatter.number("max_allowed_profiles", maxAllowedProfilesForUser)
            ));
            return;
        }

        // if a profile with the same name already exists
        boolean doesAProfileExistWithName = userData.userHasProfileWithName(profileName);
        if (doesAProfileExistWithName) {
            // send an error message to the player
            commandSender.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNewProfileNameExistsMessage(),
                    Placeholder.unparsed("profile_name", profileName)
            ));
            return;
        }

        // create the new profile with the unique valid name
        userData.registerNewPickupProfile(profileName);

        // send a success message to the player
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getNewProfileSuccessMessage(),
                Placeholder.unparsed("profile_name", profileName)
        ));
    }

    private int getMaxAllowedProfilesForUser(Player player) {
        int maxAllowedProfiles = plugin.getConfigHandler().getMaxAllowedProfiles();

        int maxAllowedProfilesForUser = maxAllowedProfiles;

        for (int i = 0; i <= maxAllowedProfiles; i++) {
            if (player.hasPermission("pickupfilter.profile.create.max." + i)) {
                maxAllowedProfilesForUser = i;
            }
        }

        return maxAllowedProfilesForUser;
    }

}
