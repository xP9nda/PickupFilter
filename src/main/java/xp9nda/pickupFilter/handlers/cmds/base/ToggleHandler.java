package xp9nda.pickupFilter.handlers.cmds.base;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.UUID;

public class ToggleHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();


    public ToggleHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to toggle the users item filter active state
    @CommandMethod("pickupfilter|pf|itemfilter|if toggle")
    @CommandPermission("pickupfilter.toggle")
    private void onToggleCommand(Player commandSender) {
        // get the player's UUID
        String playerUUIDString = commandSender.getUniqueId().toString();
        UUID playerUUID = commandSender.getUniqueId();

        PickupUser userData = plugin.getDataHolder().getPlayerData(playerUUID);

        // get the player's current filter state
        boolean currentFilterState = userData.isPickupEnabled();

        // toggle the player's filter state
        userData.setPickupEnabled(!currentFilterState);

        // send a message to the player
        commandSender.sendMessage(miniMessage.deserialize(
                plugin.getConfigHandler().getItemFilterToggledMessage(),
                Placeholder.unparsed("new_enabled_state", userData.isPickupEnabled() ? "enabled" : "disabled"),
                Placeholder.unparsed("new_enabled_state_bool", userData.isPickupEnabled() ? "true" : "false"),
                Placeholder.unparsed("old_enabled_state", currentFilterState ? "enabled" : "disabled"),
                Placeholder.unparsed("old_enabled_state_bool", currentFilterState ? "true" : "false")
        ));
    }
}
