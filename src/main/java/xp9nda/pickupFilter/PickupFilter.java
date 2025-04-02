package xp9nda.pickupFilter;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.gson.Gson;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import fr.minuskube.inv.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import xp9nda.pickupFilter.data.DataHolder;
import xp9nda.pickupFilter.data.SerializationJSON;
import xp9nda.pickupFilter.handlers.*;
import xp9nda.pickupFilter.handlers.ConfigHandler;
import xp9nda.pickupFilter.handlers.cmds.admin.AdminConfigReloadHandler;
import xp9nda.pickupFilter.handlers.cmds.admin.AdminDeleteHandler;
import xp9nda.pickupFilter.handlers.cmds.base.HelpHandler;
import xp9nda.pickupFilter.handlers.cmds.base.InfoHandler;
import xp9nda.pickupFilter.handlers.cmds.base.ProfileMenuHandler;
import xp9nda.pickupFilter.handlers.cmds.base.ToggleHandler;
import xp9nda.pickupFilter.handlers.cmds.filter.FilterAddHandler;
import xp9nda.pickupFilter.handlers.cmds.filter.FilterEditHandler;
import xp9nda.pickupFilter.handlers.cmds.filter.FilterModeHandler;
import xp9nda.pickupFilter.handlers.cmds.filter.FilterRemoveHandler;
import xp9nda.pickupFilter.handlers.cmds.profile.*;
import xp9nda.pickupFilter.handlers.implementations.EcoEnchantsImplementation;
import xp9nda.pickupFilter.handlers.implementations.Metrics;
import xp9nda.pickupFilter.handlers.implementations.WorldGuardImplementation;
import xp9nda.pickupFilter.handlers.menus.ItemFilterEditListener;
import xp9nda.pickupFilter.handlers.menus.ItemFilterEditMenu;
import xp9nda.pickupFilter.handlers.suggestions.ProfileSuggestionsProvider;
import xp9nda.pickupFilter.utils.DataUtils;

public final class PickupFilter extends JavaPlugin {

    private DataHolder dataHolder;

    private ConfigHandler configHandler;

    private InfoHandler infoHandler;
//    private HelpHandler helpHandler;

    private AdminDeleteHandler adminDeleteHandler;
    private PlayerJoinHandler playerJoinHandler;

    private DataUtils dataUtils;
    private SerializationJSON serializationJSON;

    private InventoryManager inventoryManager;

    private Gson gson;

    private ItemFilterEditListener itemFilterEditListener;
    private PickupHandler pickupHandler;
    private EcoEnchantsImplementation ecoEnchantsImplementation;
    private WorldGuardImplementation worldGuardImplementation;

    private ProfileCreationButtonChatHandler profileCreationButtonChatHandler;
    private ProfileCreateHandler profileCreateHandler;

    private WorldGuardPlugin worldGuardPlugin;

    @Override
    public void onEnable() {
        // save the default config
        saveDefaultConfig();

        // bstats setup
        int pluginId = 25304;
        Metrics metrics = new Metrics(this, pluginId);

        // setup
        PluginManager pluginManager = this.getServer().getPluginManager();

        inventoryManager = new InventoryManager(this);
        inventoryManager.init();

        gson = new Gson();
        dataHolder = new DataHolder(this);
        dataUtils = new DataUtils(this);
        serializationJSON = new SerializationJSON(this);

        ProfileSuggestionsProvider profileSuggestionProvider = new ProfileSuggestionsProvider(this);

        configHandler = new ConfigHandler(this);

        AdminConfigReloadHandler adminConfigReloadHandler = new AdminConfigReloadHandler(this);

        infoHandler = new InfoHandler(this);
//        helpHandler = new HelpHandler(this);
        ToggleHandler toggleHandler = new ToggleHandler(this);

        adminDeleteHandler = new AdminDeleteHandler(this);

        FilterAddHandler filterAddHandler = new FilterAddHandler(this);
        FilterRemoveHandler filterRemoveHandler = new FilterRemoveHandler(this);
        FilterEditHandler filterEditHandler = new FilterEditHandler(this);
        FilterModeHandler filterModeHandler = new FilterModeHandler(this);

        profileCreateHandler = new ProfileCreateHandler(this);
        ProfileDeleteHandler profileDeleteHandler = new ProfileDeleteHandler(this);
        ProfileModeHandler profileModeHandler = new ProfileModeHandler(this);
        ProfileRenameHandler profileRenameHandler = new ProfileRenameHandler(this);
        ProfileUseHandler profileUseHandler = new ProfileUseHandler(this);

        playerJoinHandler = new PlayerJoinHandler(this);
        pluginManager.registerEvents(playerJoinHandler, this);

        profileCreationButtonChatHandler = new ProfileCreationButtonChatHandler(this);
        pluginManager.registerEvents(profileCreationButtonChatHandler, this);

        pickupHandler = new PickupHandler(this);
        pluginManager.registerEvents(pickupHandler, this);

        itemFilterEditListener = new ItemFilterEditListener(this);
        pluginManager.registerEvents(itemFilterEditListener, this);

        ProfileMenuHandler profileMenuHandler = new ProfileMenuHandler(this);

        // soft dependencies
        Plugin ecoEnchants = pluginManager.getPlugin("EcoEnchants");

        if (ecoEnchants != null && ecoEnchants.isEnabled()) {
            ecoEnchantsImplementation = new EcoEnchantsImplementation(this);
            pluginManager.registerEvents(ecoEnchantsImplementation, this);
        }

        worldGuardPlugin = (WorldGuardPlugin) pluginManager.getPlugin("WorldGuard");

        // register commands
        try {
            PaperCommandManager<CommandSender> commandManager = PaperCommandManager.createNative(
                    this, CommandExecutionCoordinator.simpleCoordinator()
            );

            AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(
                    commandManager, CommandSender.class, params -> SimpleCommandMeta.empty()
            );

            // Register the suggestion provider
            commandManager.parserRegistry().registerSuggestionProvider("profile_names", profileSuggestionProvider::suggestions);

            // Register command handlers
            annotationParser.parse(adminConfigReloadHandler);
            annotationParser.parse(adminDeleteHandler);

            annotationParser.parse(infoHandler);
//            annotationParser.parse(helpHandler);
            annotationParser.parse(toggleHandler);
            annotationParser.parse(profileMenuHandler);

            annotationParser.parse(filterAddHandler);
            annotationParser.parse(filterRemoveHandler);
            annotationParser.parse(filterEditHandler);
            annotationParser.parse(filterModeHandler);

            annotationParser.parse(profileCreateHandler);
            annotationParser.parse(profileDeleteHandler);
            annotationParser.parse(profileModeHandler);
            annotationParser.parse(profileRenameHandler);
            annotationParser.parse(profileUseHandler);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoad() {
        // don't need to check if worldguard is loaded as it is not a soft dependency,
        // so is guaranteed to be loaded if the plugin is enabled
        worldGuardImplementation = new WorldGuardImplementation(this);
        worldGuardImplementation.registerCustomFlags();
        getSLF4JLogger().info("WorldGuard flags registered");
    }

    @Override
    public void onDisable() {
        // save all of the loaded player data before the plugin is disabled
        dataHolder.saveAllData();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public InfoHandler getInfoHandler() {
        return infoHandler;
    }

    public DataUtils getDataUtils() {
        return dataUtils;
    }

    public Gson getGson() {
        return gson;
    }

    public SerializationJSON getSerializationJSON() {
        return serializationJSON;
    }

    public DataHolder getDataHolder() {
        return dataHolder;
    }

//    public HelpHandler getHelpHandler() {
//        return helpHandler;
//    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public EcoEnchantsImplementation getEcoEnchantsImplementation() {
        return ecoEnchantsImplementation;
    }

    public PickupHandler getPickupHandler() {
        return pickupHandler;
    }

    public ItemFilterEditListener getItemFilterEditListener() {
        return itemFilterEditListener;
    }

    public ProfileCreationButtonChatHandler getProfileCreationButtonChatHandler() {
        return profileCreationButtonChatHandler;
    }

    public ProfileCreateHandler getProfileCreateHandler() {
        return profileCreateHandler;
    }

    public WorldGuardImplementation getWorldGuardImplementation() {
        return worldGuardImplementation;
    }

    public WorldGuardPlugin getWorldGuardPlugin() {
        return worldGuardPlugin;
    }
}
