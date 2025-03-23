package xp9nda.pickupFilter.handlers.cmds.admin;

import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;

public class AdminConfigReloadHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public AdminConfigReloadHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // command to reload the config
    @CommandMethod("pickupfilter|pf|itemfilter|if admin reload")
    @CommandPermission("pickupfilter.admin.reload")
    private void onReloadConfigCommand(Player commandSender) {
        // reload the config
        plugin.getConfigHandler().reloadConfig();

        // send a message to the player who reloaded the config
        Component messageToSend = miniMessage.deserialize(
                plugin.getConfigHandler().getReloadMessage()
        );
        commandSender.sendMessage(messageToSend);
    }

}
