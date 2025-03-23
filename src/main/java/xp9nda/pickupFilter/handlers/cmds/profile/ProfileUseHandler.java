package xp9nda.pickupFilter.handlers.cmds.profile;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.UUID;

public class ProfileUseHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ProfileUseHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to use/activate a profile
    @CommandMethod("pickupfilter|pf|itemfilter|if profile use <profile>")
    @CommandPermission("pickupfilter.profile.use")
    private void onProfileUseCommand(
            Player commandSender,
            @Argument(value = "profile", suggestions = "profile_names") String profileName
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

        // get the profiles uuid
        UUID profileUUID = userData.getPickupProfile(profileName).getProfileUUID();

        // set the active profile for the player
        userData.setActiveProfileUUID(profileUUID);

        // trigger a refresh of the player's filters
        plugin.getDataHolder().clearPlayerActiveProfileItems(commandSender.getUniqueId());

        // run through each item in the newly active profile and add it to the player's active profile filtered items in dataholder
        for (String serializedItem : userData.getPickupProfile(profileUUID).getPickupFilter()) {
            ItemStack itemStackToFilter = plugin.getDataHolder().getItemStackFromSerializedString(serializedItem);

            // if the item has not been serialized, then serialize it using the datautils
            if (itemStackToFilter == null) {
                itemStackToFilter = plugin.getDataUtils().stringToItemStack(serializedItem);
            }

            plugin.getDataHolder().addPlayerActiveProfileItem(commandSender.getUniqueId(), itemStackToFilter);
            plugin.getDataHolder().addSerializedStringAndItemStack(serializedItem, itemStackToFilter);
        }

        // send a success message to the player
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getProfileUseMessage(),
                Placeholder.unparsed("profile_name", profileName)
        ));
    }

}
