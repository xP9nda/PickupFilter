package xp9nda.pickupFilter.handlers.suggestions;

import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupProfile;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ProfileSuggestionsProvider {

    private final PickupFilter plugin;

    public ProfileSuggestionsProvider(PickupFilter plugin) {
        this.plugin = plugin;
    }

    // method to fetch the profile suggestions given a command context
    @Suggestions("profile_names")
    public List<String> suggestions(@NonNull CommandContext<CommandSender> context, String input) {
        if (!(context.getSender() instanceof Player player)) {
            return List.of(); // Return empty list if sender is not a player
        }

        HashMap<UUID, PickupProfile> profiles = plugin.getDataHolder().getPlayerData(player.getUniqueId()).getPickupProfiles();

        if (profiles == null) {
            return List.of(); // Return empty list if player has no profiles
        }

        return profiles
                .values()
                .stream()
                .map(PickupProfile::getProfileName)
                .filter(name -> name.toLowerCase().startsWith(input.toLowerCase())) // Filter for autocomplete
                .toList();
    }

}
