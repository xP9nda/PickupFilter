package xp9nda.pickupFilter.handlers.implementations;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;

public class WorldGuardImplementation {

    private final PickupFilter plugin;
    public static StateFlag ITEMFILTER_PICKUP_FLAG;

    public WorldGuardImplementation(PickupFilter plugin) {
        this.plugin = plugin;
    }

    public void registerCustomFlags() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            // create a flag with the name "my-custom-flag", defaulting to true
            StateFlag flag = new StateFlag("pickup-filter-disabled", true);
            registry.register(flag);
            ITEMFILTER_PICKUP_FLAG = flag; // only set our field if there was no error
        } catch (FlagConflictException e) {
            // some other plugin registered a flag by the same name already.
            // you can use the existing flag, but this may cause conflicts - be sure to check type
            Flag<?> existing = registry.get("pickup-filter-disabled");
            if (existing instanceof StateFlag) {
                ITEMFILTER_PICKUP_FLAG = (StateFlag) existing;
            } else {
                // types don't match - this is bad news! some other plugin conflicts with you
                // hopefully this never actually happens
                throw new RuntimeException("Flag conflict with WorldGuard");
            }
        }
    }

    public boolean isPlayerInNoFilterRegion(Player player) {
        // first, check if worldguard exists
        WorldGuardPlugin worldGuardPlugin = plugin.getWorldGuardPlugin();
        if (worldGuardPlugin == null) {
            return false;
        }
        plugin.getSLF4JLogger().info("WorldGuard is enabled.");

        // if it exists, get the region container and check if the player is in a region with the flag
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = BukkitAdapter.adapt(player.getWorld());
        RegionManager regions = container.get(world);
        if (regions == null) {
            return false;
        }
        plugin.getSLF4JLogger().info("RegionManager is not null.");

        Location playerLocation = player.getLocation();
        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.asBlockVector(playerLocation));
        plugin.getSLF4JLogger().info("ApplicableRegionSet is not null. Set Size: " + set.size());
        plugin.getSLF4JLogger().info("Flag: " + ITEMFILTER_PICKUP_FLAG.getName() + ", State: " + set.testState(null, ITEMFILTER_PICKUP_FLAG));
        return set.testState(null, ITEMFILTER_PICKUP_FLAG);
    }

}
