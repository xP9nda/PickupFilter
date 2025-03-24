package xp9nda.pickupFilter.handlers;

import fr.minuskube.inv.SmartInventory;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupUser;
import xp9nda.pickupFilter.handlers.menus.ItemFilterProfilesMenu;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProfileCreationButtonChatHandler implements Listener {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    private final Set<UUID> usersWhoAreCreatingAProfile = new HashSet<>();

    public ProfileCreationButtonChatHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }


    public void addPlayerToProfileCreation(UUID playerUUID) {
        usersWhoAreCreatingAProfile.add(playerUUID);
    }

    public void removePlayerFromProfileCreation(UUID playerUUID) {
        usersWhoAreCreatingAProfile.remove(playerUUID);
    }

    public boolean isPlayerCreatingProfile(UUID playerUUID) {
        return usersWhoAreCreatingAProfile.contains(playerUUID);
    }

    // listen for chat events
    @EventHandler
    public void onChat(AsyncChatEvent event) {
        // check if the player is creating a profile
        Player player = event.getPlayer();
        if (!isPlayerCreatingProfile(player.getUniqueId())) {
            return;
        }

        // cancel the chat event as it shouldn't be sent out to players
        event.setCancelled(true);

        // get the message the player sent
        Component chattedMessage = event.message();
        String profileName = PlainTextComponentSerializer.plainText().serialize(chattedMessage);

        // check that the profile name is within the length limits
        if (profileName.length() < plugin.getConfigHandler().getProfileNameLengthMin() || profileName.length() > plugin.getConfigHandler().getProfileNameLengthMax()) {
            // send an error message to the player
            player.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNewProfileInvalidNameMessage()
            ));
            openProfileMenu(player);
            return;
        }

        // run through each character in the profile name, and if it is not in the allowed characters, do not allow creation
        HashSet<Character> allowedChars = plugin.getConfigHandler().getAllowedProfileNameCharactersSet();
        for (char c : profileName.toCharArray()) {
            if (!allowedChars.contains(c)) {
                // send an error message to the player
                player.sendMessage(miniMessage.deserialize(
                        plugin.getConfigHandler().getNewProfileInvalidNameMessage()
                ));
                openProfileMenu(player);
                return;
            }
        }

        // check that the user does not already have a profile with the same name
        PickupUser userData = plugin.getDataHolder().getPlayerData(player.getUniqueId());

        // check if the user is allowed to create a new profile
        int maxAllowedProfilesForUser = plugin.getProfileCreateHandler().getMaxAllowedProfilesForUser(player);
        int numberOfProfiles = userData.getPickupProfiles().size();

        if (numberOfProfiles >= maxAllowedProfilesForUser) {
            // send an error message to the player
            player.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getMaxProfilesReachedMessage(),
                    Formatter.number("max_allowed_profiles", maxAllowedProfilesForUser)
            ));
            openProfileMenu(player);
            return;
        }

        // if a profile with the same name already exists
        boolean doesAProfileExistWithName = userData.userHasProfileWithName(profileName);
        if (doesAProfileExistWithName) {
            // send an error message to the player
            player.sendMessage(miniMessage.deserialize(
                    plugin.getConfigHandler().getNewProfileNameExistsMessage(),
                    Placeholder.unparsed("profile_name", profileName)
            ));
            openProfileMenu(player);
            return;
        }

        // create the new profile with the unique valid name
        userData.registerNewPickupProfile(profileName);

        // send a success message to the player
        player.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getNewProfileSuccessMessage(),
                Placeholder.unparsed("profile_name", profileName)
        ));

        openProfileMenu(player);
    }

    private void openProfileMenu(Player player) {
        // re-open the profiles menu
        new BukkitRunnable() {
            @Override
            public void run() {
                removePlayerFromProfileCreation(player.getUniqueId());

                // construct the menu name
                Component menuName = miniMessage.deserialize(
                        plugin.getConfigHandler().getProfileMenuTitle()
                );

                String menuNameString = PlainTextComponentSerializer.plainText().serialize(menuName);

                // open the menu
                ItemFilterProfilesMenu itemFilterProfilesMenu = new ItemFilterProfilesMenu(plugin, player.getUniqueId(), menuNameString);
                SmartInventory inventory = itemFilterProfilesMenu.buildMenu();

                inventory.open(player);
            }
        }.runTask(plugin);
    }

}
