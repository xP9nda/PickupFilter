package xp9nda.pickupFilter.handlers.cmds.admin;

import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.SerializationJSON;
import xp9nda.pickupFilter.handlers.ConfigHandler;

import java.util.UUID;

public class AdminDeleteHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private ConfigHandler configHandler;

    public AdminDeleteHandler(PickupFilter plugin) {
        this.plugin = plugin;
        this.configHandler = plugin.getConfigHandler();
    }

    @CommandMethod("pickupfilter|pf|itemfilter|if admin delete <playerName>")
    @CommandPermission("pickupfilter.admin.delete")
    private void onDeleteCommand(
            CommandSender commandSender,
            @Argument(value = "playerName") String playerName
    ) {
        // attempt to get the player
        Player targetPlayer = plugin.getServer().getPlayer(playerName);

        // if target player was online, then we don't want to delete their data
        if (targetPlayer != null) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage(miniMessage.deserialize(
                        configHandler.getDataDeletePlayerOnlineMessage(),
                        Placeholder.unparsed("player_name", playerName)
                ));
            }

            if (commandSender instanceof ConsoleCommandSender) {
                plugin.getSLF4JLogger().info("Player " + playerName + " is online, so data could not be deleted.");
            }

            return;
        }

        // if the player could not be found, try to get them as an offline player
        targetPlayer = plugin.getServer().getOfflinePlayer(playerName).getPlayer();

        // if the player still could not be found, send an error message
        if (targetPlayer == null) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage(miniMessage.deserialize(
                        configHandler.getDataDeletePlayerDoesNotExistMessage(),
                        Placeholder.unparsed("player_name", playerName)
                ));
            }

            if (commandSender instanceof ConsoleCommandSender) {
                plugin.getSLF4JLogger().info("Player " + playerName + " does not exist.");
            }
            return;
        }

        // get the now existing player's UUID
        UUID targetPlayerUUID = targetPlayer.getUniqueId();

        // attempt to find the file for the player
        SerializationJSON dataSerialization = plugin.getSerializationJSON();

        boolean doesPlayerHaveData = dataSerialization.doesPlayerPickupDataExist(targetPlayerUUID);

        // if the player has no data, tell the command sender
        if (!doesPlayerHaveData) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage(miniMessage.deserialize(
                        configHandler.getDataDeletePlayerHasNoDataMessage(),
                        Placeholder.unparsed("player_name", playerName)
                ));
            }

            if (commandSender instanceof ConsoleCommandSender) {
                plugin.getSLF4JLogger().info("Player " + playerName + " has no data.");
            }

            return;
        }

        // if the player has valid data then delete it
        boolean deleteResult = dataSerialization.deletePlayerPickupData(targetPlayerUUID);

        // if the data was deleted successfully, send a success message
        if (deleteResult) {
            if (commandSender instanceof Player) {
                commandSender.sendMessage(miniMessage.deserialize(
                        configHandler.getDataDeleteSuccessMessage(),
                        Placeholder.unparsed("player_name", playerName),
                        Placeholder.unparsed("player_uuid", targetPlayerUUID.toString())
                ));
            }

            if (commandSender instanceof ConsoleCommandSender) {
                plugin.getSLF4JLogger().info("Successfully deleted player pickup data for player: " + playerName);
            }

        } else {
            // if the data was not deleted successfully, send a failure message
            if (commandSender instanceof Player) {
                commandSender.sendMessage(miniMessage.deserialize(
                        configHandler.getDataDeleteFailMessage(),
                        Placeholder.unparsed("player_name", playerName)
                ));
            }

            if (commandSender instanceof ConsoleCommandSender) {
                plugin.getSLF4JLogger().info("Failed to delete player pickup data for player: " + playerName);
            }
        }
    }
}
