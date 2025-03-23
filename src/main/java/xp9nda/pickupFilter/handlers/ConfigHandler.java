package xp9nda.pickupFilter.handlers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import xp9nda.pickupFilter.PickupFilter;

import java.util.HashSet;
import java.util.List;

public class ConfigHandler {

    private final PickupFilter plugin;

    // region Config values

    private String reloadMessage;

    private String dataDeleteSuccessMessage;
    private String dataDeleteFailMessage;
    private String dataDeletePlayerDoesNotExistMessage;
    private String dataDeletePlayerHasNoDataMessage;
    private String dataDeletePlayerOnlineMessage;

    private HashSet<Character> allowedProfileNameCharactersSet;

    private int profileNameLengthMin;
    private int profileNameLengthMax;
    private String newProfileInvalidNameMessage;
    private String newProfileNameExistsMessage;
    private String newProfileSuccessMessage;
    private String generalProfileNameInvalidMessage;
    private String profileStateChangedMessage;
    private String profileDeletedSuccessMessage;
    private String profileDeleteConfirmationMessage;
    private String noLongerHaveActiveProfileMessage;
    private String filterModeSetMessage;
    private String noActiveProfileMessage;
    private String profileInvalidMaterialMessage;
    private String profileNoHeldItemMessage;
    private String profileItemAlreadyInFilterMessage;
    private String profileItemAddedToFilterMessage;
    private String profileItemRemovedFromFilterMessage;
    private String profileItemNotInFilterMessage;

    private String itemFilterToggledMessage;
    private String profileUseMessage;
    private String profileRenamedSuccessMessage;

    private boolean editMenuCloseButtonEnabled;
    private Integer editMenuCloseButtonCustomModelData;
    private Material editMenuCloseButtonMaterial;
    private String editMenuCloseButtonName;
    private List<String> editMenuCloseButtonLore;

    private boolean previousPageButtonHideOnFirstPage;
    private Integer previousPageButtonCustomModelData;
    private Material previousPageButtonMaterial;
    private String previousPageButtonName;
    private List<String> previousPageButtonLore;

    private boolean nextPageButtonHideOnLastPage;
    private Integer nextPageButtonCustomModelData;
    private Material nextPageButtonMaterial;
    private String nextPageButtonName;
    private List<String> nextPageButtonLore;

    private boolean editMenuFilterModeButtonEnabled;
    private boolean editMenuFilterModeButtonCustomModelData;
    private Material editMenuFilterModeButtonMaterial;
    private String editMenuFilterModeButtonName;
    private List<String> editMenuFilterModeButtonLore;

    private boolean editMenuFilterEnabledButtonEnabled;
    private Integer editMenuFilterEnabledButtonCustomModelData;
    private Material editMenuFilterEnabledButtonMaterial;
    private String editMenuFilterEnabledButtonName;
    private List<String> editMenuFilterEnabledButtonLore;

    private List<String> editMenuRemoveFromProfileItemLore;

    private String editMenuName;

    private int maxAllowedProfiles;
    private String maxProfilesReachedMessage;

    private Material editMenuBottomRowFillerMaterial;
    private Integer editMenuBottomRowFillerCustomModelData;


    private String profileMenuTitle;
    private Material profileMenuItemMaterial;
    private String profileMenuItemTitle;
    private String profileMenuItemTitleActiveProfile;
    private List<String> profileMenuItemLore;
    private boolean shouldActiveProfileShowEnchanted;

    private Material profileMenuBottomRowMaterial;
    private Integer profileMenuBottomRowCustomModelData;

    private boolean profileMenuCloseButtonEnabled;
    private String profileMenuCloseButtonTitle;
    private List<String> profileMenuCloseButtonLore;
    private Material profileMenuCloseButtonMaterial;
    private Integer profileMenuCloseButtonCustomModelData;

    private boolean profileMenuPreviousPageButtonHideOnFirstPage;
    private String profileMenuPreviousPageButtonTitle;
    private List<String> profileMenuPreviousPageButtonLore;
    private Material profileMenuPreviousPageButtonMaterial;
    private Integer profileMenuPreviousPageButtonCustomModelData;

    private boolean profileMenuNextPageButtonHideOnLastPage;
    private String profileMenuNextPageButtonTitle;
    private List<String> profileMenuNextPageButtonLore;
    private Material profileMenuNextPageButtonMaterial;
    private Integer profileMenuNextPageButtonCustomModelData;

    // endregion

    // constructor
    public ConfigHandler(PickupFilter plugin) {
        this.plugin = plugin;

        reloadConfig();
    }

    public void reloadConfig() {
        // reload the config file & get the config
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        // set the config values
        setReloadMessage(config.getString("messages.reloadConfig"));
        setDataDeleteSuccessMessage(config.getString("messages.dataDeleteSuccess"));
        setDataDeleteFailMessage(config.getString("messages.dataDeleteFail"));
        setDataDeletePlayerDoesNotExistMessage(config.getString("messages.dataDeletePlayerDoesNotExist"));
        setDataDeletePlayerHasNoDataMessage(config.getString("messages.dataDeletePlayerHasNoData"));
        setDataDeletePlayerOnlineMessage(config.getString("messages.dataDeletePlayerIsOnline"));

        setProfileNameLengthMin(config.getInt("settings.profileNameLengthMin"));
        setProfileNameLengthMax(config.getInt("settings.profileNameLengthMax"));
        setAllowedProfileNameCharacters(config.getString("settings.allowedProfileNameCharacters"));
        setNewProfileInvalidNameMessage(config.getString("messages.newProfileInvalidName"));
        setNewProfileNameExistsMessage(config.getString("messages.newProfileNameExists"));
        setNewProfileSuccessMessage(config.getString("messages.newProfileCreatedSuccess"));
        setGeneralProfileNameInvalidMessage(config.getString("messages.generalProfileNameInvalid"));
        setProfileStateChangedMessage(config.getString("messages.profileStateChanged"));
        setProfileDeletedSuccessMessage(config.getString("messages.profileDeletedSuccess"));
        setItemFilterToggledMessage(config.getString("messages.itemFilterToggled"));
        setProfileUseMessage(config.getString("messages.profileUse"));
        setProfileRenamedSuccessMessage(config.getString("messages.profileRenamedSuccess"));
        setProfileDeleteConfirmationMessage(config.getString("messages.profileDeleteConfirmation"));
        setNoLongerHaveActiveProfileMessage(config.getString("messages.noLongerHaveActiveProfile"));
        setFilterModeSetMessage(config.getString("messages.filterModeSet"));
        setNoActiveProfileMessage(config.getString("messages.noActiveProfile"));
        setProfileInvalidMaterialMessage(config.getString("messages.profileInvalidMaterial"));
        setProfileNoHeldItemMessage(config.getString("messages.profileNoHeldItem"));
        setProfileItemAlreadyInFilterMessage(config.getString("messages.profileItemAlreadyInFilter"));
        setProfileItemAddedToFilterMessage(config.getString("messages.profileItemAddedToFilter"));
        setProfileItemRemovedFromFilterMessage(config.getString("messages.profileItemRemovedFromFilter"));
        setProfileItemNotInFilterMessage(config.getString("messages.profileItemNotInFilter"));

        setEditMenuName(config.getString("editMenu.title"));
        setEditMenuCloseButtonEnabled(config.getBoolean("editMenu.closeButton.enabled"));
        setEditMenuCloseButtonName(config.getString("editMenu.closeButton.title"));
        setEditMenuCloseButtonLore(config.getStringList("editMenu.closeButton.lore"));

        setPreviousPageButtonHideOnFirstPage(config.getBoolean("editMenu.previousPageButton.hideOnFirstPage"));
        setPreviousPageButtonName(config.getString("editMenu.previousPageButton.title"));
        setPreviousPageButtonLore(config.getStringList("editMenu.previousPageButton.lore"));

        setNextPageButtonHideOnLastPage(config.getBoolean("editMenu.nextPageButton.hideOnLastPage"));
        setNextPageButtonName(config.getString("editMenu.nextPageButton.title"));
        setNextPageButtonLore(config.getStringList("editMenu.nextPageButton.lore"));

        setEditMenuFilterModeButtonEnabled(config.getBoolean("editMenu.filterModeButton.enabled"));
        setEditMenuFilterModeButtonName(config.getString("editMenu.filterModeButton.title"));
        setEditMenuFilterModeButtonLore(config.getStringList("editMenu.filterModeButton.lore"));

        setEditMenuRemoveFromProfileItemLore(config.getStringList("editMenu.removeFromProfileItemLore"));

        setEditMenuCloseButtonCustomModelData(config.getInt("editMenu.closeButton.customModelData"));
        setPreviousPageButtonCustomModelData(config.getInt("editMenu.previousPageButton.customModelData"));
        setNextPageButtonCustomModelData(config.getInt("editMenu.nextPageButton.customModelData"));
        setEditMenuFilterModeButtonCustomModelData(config.getBoolean("editMenu.filterModeButton.customModelData"));

        setEditMenuCloseButtonMaterial(Material.valueOf(config.getString("editMenu.closeButton.material")));
        setPreviousPageButtonMaterial(Material.valueOf(config.getString("editMenu.previousPageButton.material")));
        setNextPageButtonMaterial(Material.valueOf(config.getString("editMenu.nextPageButton.material")));
        setEditMenuFilterModeButtonMaterial(Material.valueOf(config.getString("editMenu.filterModeButton.material")));

        setMaxAllowedProfiles(config.getInt("settings.maxAllowedProfiles"));
        setMaxProfilesReachedMessage(config.getString("messages.maxProfilesReached"));

        setEditMenuBottomRowFillerMaterial(Material.valueOf(config.getString("editMenu.bottomRow.material")));
        setEditMenuBottomRowFillerCustomModelData(config.getInt("editMenu.bottomRow.customModelData"));

        setEditMenuFilterEnabledButtonEnabled(config.getBoolean("editMenu.filterEnabledButton.enabled"));
        setEditMenuFilterEnabledButtonCustomModelData(config.getInt("editMenu.filterEnabledButton.customModelData"));
        setEditMenuFilterEnabledButtonMaterial(Material.valueOf(config.getString("editMenu.filterEnabledButton.material")));
        setEditMenuFilterEnabledButtonName(config.getString("editMenu.filterEnabledButton.title"));
        setEditMenuFilterEnabledButtonLore(config.getStringList("editMenu.filterEnabledButton.lore"));

        setProfileMenuTitle(config.getString("profileMenu.title"));
        setProfileMenuItemMaterial(Material.valueOf(config.getString("profileMenu.editProfileItemMaterial")));
        setProfileMenuItemTitle(config.getString("profileMenu.editProfileItemTitle"));
        setProfileMenuItemLore(config.getStringList("profileMenu.editProfileItemLore"));

        setProfileMenuBottomRowMaterial(Material.valueOf(config.getString("profileMenu.bottomRow.material")));
        setProfileMenuBottomRowCustomModelData(config.getInt("profileMenu.bottomRow.customModelData"));

        setProfileMenuCloseButtonEnabled(config.getBoolean("profileMenu.closeButton.enabled"));
        setProfileMenuCloseButtonTitle(config.getString("profileMenu.closeButton.title"));
        setProfileMenuCloseButtonLore(config.getStringList("profileMenu.closeButton.lore"));
        setProfileMenuCloseButtonMaterial(Material.valueOf(config.getString("profileMenu.closeButton.material")));
        setProfileMenuCloseButtonCustomModelData(config.getInt("profileMenu.closeButton.customModelData"));

        setProfileMenuPreviousPageButtonHideOnFirstPage(config.getBoolean("profileMenu.previousPageButton.hideOnFirstPage"));
        setProfileMenuPreviousPageButtonTitle(config.getString("profileMenu.previousPageButton.title"));
        setProfileMenuPreviousPageButtonLore(config.getStringList("profileMenu.previousPageButton.lore"));
        setProfileMenuPreviousPageButtonMaterial(Material.valueOf(config.getString("profileMenu.previousPageButton.material")));
        setProfileMenuPreviousPageButtonCustomModelData(config.getInt("profileMenu.previousPageButton.customModelData"));

        setProfileMenuNextPageButtonHideOnLastPage(config.getBoolean("profileMenu.nextPageButton.hideOnLastPage"));
        setProfileMenuNextPageButtonTitle(config.getString("profileMenu.nextPageButton.title"));
        setProfileMenuNextPageButtonLore(config.getStringList("profileMenu.nextPageButton.lore"));
        setProfileMenuNextPageButtonMaterial(Material.valueOf(config.getString("profileMenu.nextPageButton.material")));
        setProfileMenuNextPageButtonCustomModelData(config.getInt("profileMenu.nextPageButton.customModelData"));

        setShouldActiveProfileShowEnchanted(config.getBoolean("profileMenu.shouldActiveProfileShowEnchanted"));
        setProfileMenuItemTitleActiveProfile(config.getString("profileMenu.editProfileItemTitleActive"));
    }


    public String getReloadMessage() {
        return reloadMessage;
    }

    public void setReloadMessage(String reloadMessage) {
        this.reloadMessage = reloadMessage;
    }

    public String getDataDeleteSuccessMessage() {
        return dataDeleteSuccessMessage;
    }

    public void setDataDeleteSuccessMessage(String dataDeleteSuccessMessage) {
        this.dataDeleteSuccessMessage = dataDeleteSuccessMessage;
    }

    public String getDataDeleteFailMessage() {
        return dataDeleteFailMessage;
    }

    public void setDataDeleteFailMessage(String dataDeleteFailMessage) {
        this.dataDeleteFailMessage = dataDeleteFailMessage;
    }

    public String getDataDeletePlayerDoesNotExistMessage() {
        return dataDeletePlayerDoesNotExistMessage;
    }

    public void setDataDeletePlayerDoesNotExistMessage(String dataDeletePlayerDoesNotExistMessage) {
        this.dataDeletePlayerDoesNotExistMessage = dataDeletePlayerDoesNotExistMessage;
    }

    public String getDataDeletePlayerHasNoDataMessage() {
        return dataDeletePlayerHasNoDataMessage;
    }

    public void setDataDeletePlayerHasNoDataMessage(String dataDeletePlayerHasNoDataMessage) {
        this.dataDeletePlayerHasNoDataMessage = dataDeletePlayerHasNoDataMessage;
    }

    public int getProfileNameLengthMin() {
        return profileNameLengthMin;
    }

    public void setProfileNameLengthMin(int profileNameLengthMin) {
        this.profileNameLengthMin = profileNameLengthMin;
    }

    public int getProfileNameLengthMax() {
        return profileNameLengthMax;
    }

    public void setProfileNameLengthMax(int profileNameLengthMax) {
        this.profileNameLengthMax = profileNameLengthMax;
    }

    public String getNewProfileInvalidNameMessage() {
        return newProfileInvalidNameMessage;
    }

    public void setNewProfileInvalidNameMessage(String newProfileInvalidNameMessage) {
        this.newProfileInvalidNameMessage = newProfileInvalidNameMessage;
    }

    public String getNewProfileNameExistsMessage() {
        return newProfileNameExistsMessage;
    }

    public void setNewProfileNameExistsMessage(String newProfileNameExistsMessage) {
        this.newProfileNameExistsMessage = newProfileNameExistsMessage;
    }

    public String getNewProfileSuccessMessage() {
        return newProfileSuccessMessage;
    }

    public void setNewProfileSuccessMessage(String newProfileSuccessMessage) {
        this.newProfileSuccessMessage = newProfileSuccessMessage;
    }

    public String getGeneralProfileNameInvalidMessage() {
        return generalProfileNameInvalidMessage;
    }

    public void setGeneralProfileNameInvalidMessage(String generalProfileNameInvalidMessage) {
        this.generalProfileNameInvalidMessage = generalProfileNameInvalidMessage;
    }

    public String getProfileStateChangedMessage() {
        return profileStateChangedMessage;
    }

    public void setProfileStateChangedMessage(String profileStateChangedMessage) {
        this.profileStateChangedMessage = profileStateChangedMessage;
    }

    public String getProfileDeletedSuccessMessage() {
        return profileDeletedSuccessMessage;
    }

    public void setProfileDeletedSuccessMessage(String profileDeletedSuccessMessage) {
        this.profileDeletedSuccessMessage = profileDeletedSuccessMessage;
    }

    public String getItemFilterToggledMessage() {
        return itemFilterToggledMessage;
    }

    public void setItemFilterToggledMessage(String itemFilterToggledMessage) {
        this.itemFilterToggledMessage = itemFilterToggledMessage;
    }

    public String getProfileUseMessage() {
        return profileUseMessage;
    }

    public void setProfileUseMessage(String profileUseMessage) {
        this.profileUseMessage = profileUseMessage;
    }

    public String getProfileRenamedSuccessMessage() {
        return profileRenamedSuccessMessage;
    }

    public void setProfileRenamedSuccessMessage(String profileRenamedSuccessMessage) {
        this.profileRenamedSuccessMessage = profileRenamedSuccessMessage;
    }

    public String getProfileDeleteConfirmationMessage() {
        return profileDeleteConfirmationMessage;
    }

    public void setProfileDeleteConfirmationMessage(String profileDeleteConfirmationMessage) {
        this.profileDeleteConfirmationMessage = profileDeleteConfirmationMessage;
    }

    public String getFilterModeSetMessage() {
        return filterModeSetMessage;
    }

    public void setFilterModeSetMessage(String filterModeSetMessage) {
        this.filterModeSetMessage = filterModeSetMessage;
    }

    public String getNoLongerHaveActiveProfileMessage() {
        return noLongerHaveActiveProfileMessage;
    }

    public void setNoLongerHaveActiveProfileMessage(String noLongerHaveActiveProfileMessage) {
        this.noLongerHaveActiveProfileMessage = noLongerHaveActiveProfileMessage;
    }

    public String getNoActiveProfileMessage() {
        return noActiveProfileMessage;
    }

    public void setNoActiveProfileMessage(String noActiveProfileMessage) {
        this.noActiveProfileMessage = noActiveProfileMessage;
    }

    public void setAllowedProfileNameCharacters(String allowedProfileNameCharacters) {
        // create a set of the allowed characters
        allowedProfileNameCharactersSet = new HashSet<>();
        for (char c : allowedProfileNameCharacters.toCharArray()) {
            allowedProfileNameCharactersSet.add(c);
        }
    }

    public HashSet<Character> getAllowedProfileNameCharactersSet() {
        return allowedProfileNameCharactersSet;
    }

    public String getProfileInvalidMaterialMessage() {
        return profileInvalidMaterialMessage;
    }

    public void setProfileInvalidMaterialMessage(String profileInvalidMaterialMessage) {
        this.profileInvalidMaterialMessage = profileInvalidMaterialMessage;
    }

    public String getProfileNoHeldItemMessage() {
        return profileNoHeldItemMessage;
    }

    public void setProfileNoHeldItemMessage(String profileNoHeldItemMessage) {
        this.profileNoHeldItemMessage = profileNoHeldItemMessage;
    }

    public String getProfileItemAlreadyInFilterMessage() {
        return profileItemAlreadyInFilterMessage;
    }

    public void setProfileItemAlreadyInFilterMessage(String profileItemAlreadyInFilterMessage) {
        this.profileItemAlreadyInFilterMessage = profileItemAlreadyInFilterMessage;
    }

    public String getProfileItemAddedToFilterMessage() {
        return profileItemAddedToFilterMessage;
    }

    public void setProfileItemAddedToFilterMessage(String profileItemAddedToFilterMessage) {
        this.profileItemAddedToFilterMessage = profileItemAddedToFilterMessage;
    }

    public String getProfileItemRemovedFromFilterMessage() {
        return profileItemRemovedFromFilterMessage;
    }

    public void setProfileItemRemovedFromFilterMessage(String profileItemRemovedFromFilterMessage) {
        this.profileItemRemovedFromFilterMessage = profileItemRemovedFromFilterMessage;
    }

    public String getProfileItemNotInFilterMessage() {
        return profileItemNotInFilterMessage;
    }

    public void setProfileItemNotInFilterMessage(String profileItemNotInFilterMessage) {
        this.profileItemNotInFilterMessage = profileItemNotInFilterMessage;
    }

    public String getEditMenuName() {
        return editMenuName;
    }

    public void setEditMenuName(String editMenuName) {
        this.editMenuName = editMenuName;
    }

    public boolean isEditMenuCloseButtonEnabled() {
        return editMenuCloseButtonEnabled;
    }

    public void setEditMenuCloseButtonEnabled(boolean editMenuCloseButtonEnabled) {
        this.editMenuCloseButtonEnabled = editMenuCloseButtonEnabled;
    }

    public String getEditMenuCloseButtonName() {
        return editMenuCloseButtonName;
    }

    public void setEditMenuCloseButtonName(String editMenuCloseButtonName) {
        this.editMenuCloseButtonName = editMenuCloseButtonName;
    }

    public List<String> getEditMenuCloseButtonLore() {
        return editMenuCloseButtonLore;
    }

    public void setEditMenuCloseButtonLore(List<String> editMenuCloseButtonLore) {
        this.editMenuCloseButtonLore = editMenuCloseButtonLore;
    }

    public boolean isPreviousPageButtonHideOnFirstPage() {
        return previousPageButtonHideOnFirstPage;
    }

    public void setPreviousPageButtonHideOnFirstPage(boolean previousPageButtonHideOnFirstPage) {
        this.previousPageButtonHideOnFirstPage = previousPageButtonHideOnFirstPage;
    }

    public String getPreviousPageButtonName() {
        return previousPageButtonName;
    }

    public void setPreviousPageButtonName(String previousPageButtonName) {
        this.previousPageButtonName = previousPageButtonName;
    }

    public List<String> getPreviousPageButtonLore() {
        return previousPageButtonLore;
    }

    public void setPreviousPageButtonLore(List<String> previousPageButtonLore) {
        this.previousPageButtonLore = previousPageButtonLore;
    }

    public boolean isNextPageButtonHideOnLastPage() {
        return nextPageButtonHideOnLastPage;
    }

    public void setNextPageButtonHideOnLastPage(boolean nextPageButtonHideOnLastPage) {
        this.nextPageButtonHideOnLastPage = nextPageButtonHideOnLastPage;
    }

    public String getNextPageButtonName() {
        return nextPageButtonName;
    }

    public void setNextPageButtonName(String nextPageButtonName) {
        this.nextPageButtonName = nextPageButtonName;
    }

    public List<String> getNextPageButtonLore() {
        return nextPageButtonLore;
    }

    public void setNextPageButtonLore(List<String> nextPageButtonLore) {
        this.nextPageButtonLore = nextPageButtonLore;
    }

    public boolean isEditMenuFilterModeButtonEnabled() {
        return editMenuFilterModeButtonEnabled;
    }

    public void setEditMenuFilterModeButtonEnabled(boolean editMenuFilterModeButtonEnabled) {
        this.editMenuFilterModeButtonEnabled = editMenuFilterModeButtonEnabled;
    }

    public String getEditMenuFilterModeButtonName() {
        return editMenuFilterModeButtonName;
    }

    public void setEditMenuFilterModeButtonName(String editMenuFilterModeButtonName) {
        this.editMenuFilterModeButtonName = editMenuFilterModeButtonName;
    }

    public List<String> getEditMenuFilterModeButtonLore() {
        return editMenuFilterModeButtonLore;
    }

    public void setEditMenuFilterModeButtonLore(List<String> editMenuFilterModeButtonLore) {
        this.editMenuFilterModeButtonLore = editMenuFilterModeButtonLore;
    }

    public List<String> getEditMenuRemoveFromProfileItemLore() {
        return editMenuRemoveFromProfileItemLore;
    }

    public void setEditMenuRemoveFromProfileItemLore(List<String> editMenuRemoveFromProfileItemLore) {
        this.editMenuRemoveFromProfileItemLore = editMenuRemoveFromProfileItemLore;
    }

    public Integer getEditMenuCloseButtonCustomModelData() {
        return editMenuCloseButtonCustomModelData;
    }

    public void setEditMenuCloseButtonCustomModelData(Integer editMenuCloseButtonCustomModelData) {
        this.editMenuCloseButtonCustomModelData = editMenuCloseButtonCustomModelData;
    }

    public Integer getPreviousPageButtonCustomModelData() {
        return previousPageButtonCustomModelData;
    }

    public void setPreviousPageButtonCustomModelData(Integer previousPageButtonCustomModelData) {
        this.previousPageButtonCustomModelData = previousPageButtonCustomModelData;
    }

    public Integer getNextPageButtonCustomModelData() {
        return nextPageButtonCustomModelData;
    }

    public void setNextPageButtonCustomModelData(Integer nextPageButtonCustomModelData) {
        this.nextPageButtonCustomModelData = nextPageButtonCustomModelData;
    }

    public boolean isEditMenuFilterModeButtonCustomModelData() {
        return editMenuFilterModeButtonCustomModelData;
    }

    public void setEditMenuFilterModeButtonCustomModelData(boolean editMenuFilterModeButtonCustomModelData) {
        this.editMenuFilterModeButtonCustomModelData = editMenuFilterModeButtonCustomModelData;
    }

    public Material getEditMenuCloseButtonMaterial() {
        return editMenuCloseButtonMaterial;
    }

    public void setEditMenuCloseButtonMaterial(Material editMenuCloseButtonMaterial) {
        this.editMenuCloseButtonMaterial = editMenuCloseButtonMaterial;
    }

    public Material getPreviousPageButtonMaterial() {
        return previousPageButtonMaterial;
    }

    public void setPreviousPageButtonMaterial(Material previousPageButtonMaterial) {
        this.previousPageButtonMaterial = previousPageButtonMaterial;
    }

    public Material getNextPageButtonMaterial() {
        return nextPageButtonMaterial;
    }

    public void setNextPageButtonMaterial(Material nextPageButtonMaterial) {
        this.nextPageButtonMaterial = nextPageButtonMaterial;
    }

    public Material getEditMenuFilterModeButtonMaterial() {
        return editMenuFilterModeButtonMaterial;
    }

    public void setEditMenuFilterModeButtonMaterial(Material editMenuFilterModeButtonMaterial) {
        this.editMenuFilterModeButtonMaterial = editMenuFilterModeButtonMaterial;
    }

    public int getMaxAllowedProfiles() {
        return maxAllowedProfiles;
    }

    public void setMaxAllowedProfiles(int maxAllowedProfiles) {
        this.maxAllowedProfiles = maxAllowedProfiles;
    }

    public String getMaxProfilesReachedMessage() {
        return maxProfilesReachedMessage;
    }

    public void setMaxProfilesReachedMessage(String maxProfilesReachedMessage) {
        this.maxProfilesReachedMessage = maxProfilesReachedMessage;
    }

    public Material getEditMenuBottomRowFillerMaterial() {
        return editMenuBottomRowFillerMaterial;
    }

    public void setEditMenuBottomRowFillerMaterial(Material editMenuBottomRowFillerMaterial) {
        this.editMenuBottomRowFillerMaterial = editMenuBottomRowFillerMaterial;
    }

    public Integer getEditMenuBottomRowFillerCustomModelData() {
        return editMenuBottomRowFillerCustomModelData;
    }

    public void setEditMenuBottomRowFillerCustomModelData(Integer editMenuBottomRowFillerCustomModelData) {
        this.editMenuBottomRowFillerCustomModelData = editMenuBottomRowFillerCustomModelData;
    }

    public boolean isEditMenuFilterEnabledButtonEnabled() {
        return editMenuFilterEnabledButtonEnabled;
    }

    public void setEditMenuFilterEnabledButtonEnabled(boolean editMenuFilterEnabledButtonEnabled) {
        this.editMenuFilterEnabledButtonEnabled = editMenuFilterEnabledButtonEnabled;
    }


    public Material getEditMenuFilterEnabledButtonMaterial() {
        return editMenuFilterEnabledButtonMaterial;
    }

    public void setEditMenuFilterEnabledButtonMaterial(Material editMenuFilterEnabledButtonMaterial) {
        this.editMenuFilterEnabledButtonMaterial = editMenuFilterEnabledButtonMaterial;
    }

    public String getEditMenuFilterEnabledButtonName() {
        return editMenuFilterEnabledButtonName;
    }

    public void setEditMenuFilterEnabledButtonName(String editMenuFilterEnabledButtonName) {
        this.editMenuFilterEnabledButtonName = editMenuFilterEnabledButtonName;
    }

    public List<String> getEditMenuFilterEnabledButtonLore() {
        return editMenuFilterEnabledButtonLore;
    }

    public void setEditMenuFilterEnabledButtonLore(List<String> editMenuFilterEnabledButtonLore) {
        this.editMenuFilterEnabledButtonLore = editMenuFilterEnabledButtonLore;
    }

    public Integer getEditMenuFilterEnabledButtonCustomModelData() {
        return editMenuFilterEnabledButtonCustomModelData;
    }

    public void setEditMenuFilterEnabledButtonCustomModelData(Integer editMenuFilterEnabledButtonCustomModelData) {
        this.editMenuFilterEnabledButtonCustomModelData = editMenuFilterEnabledButtonCustomModelData;
    }

    public String getProfileMenuTitle() {
        return profileMenuTitle;
    }

    public void setProfileMenuTitle(String profileMenuTitle) {
        this.profileMenuTitle = profileMenuTitle;
    }

    public String getProfileMenuItemTitle() {
        return profileMenuItemTitle;
    }

    public void setProfileMenuItemTitle(String profileMenuItemTitle) {
        this.profileMenuItemTitle = profileMenuItemTitle;
    }

    public List<String> getProfileMenuItemLore() {
        return profileMenuItemLore;
    }

    public void setProfileMenuItemLore(List<String> profileMenuItemLore) {
        this.profileMenuItemLore = profileMenuItemLore;
    }

    public Material getProfileMenuBottomRowMaterial() {
        return profileMenuBottomRowMaterial;
    }

    public void setProfileMenuBottomRowMaterial(Material profileMenuBottomRowMaterial) {
        this.profileMenuBottomRowMaterial = profileMenuBottomRowMaterial;
    }

    public Integer getProfileMenuBottomRowCustomModelData() {
        return profileMenuBottomRowCustomModelData;
    }

    public void setProfileMenuBottomRowCustomModelData(Integer profileMenuBottomRowCustomModelData) {
        this.profileMenuBottomRowCustomModelData = profileMenuBottomRowCustomModelData;
    }

    public boolean isProfileMenuCloseButtonEnabled() {
        return profileMenuCloseButtonEnabled;
    }

    public void setProfileMenuCloseButtonEnabled(boolean profileMenuCloseButtonEnabled) {
        this.profileMenuCloseButtonEnabled = profileMenuCloseButtonEnabled;
    }

    public String getProfileMenuCloseButtonTitle() {
        return profileMenuCloseButtonTitle;
    }

    public void setProfileMenuCloseButtonTitle(String profileMenuCloseButtonTitle) {
        this.profileMenuCloseButtonTitle = profileMenuCloseButtonTitle;
    }

    public List<String> getProfileMenuCloseButtonLore() {
        return profileMenuCloseButtonLore;
    }

    public void setProfileMenuCloseButtonLore(List<String> profileMenuCloseButtonLore) {
        this.profileMenuCloseButtonLore = profileMenuCloseButtonLore;
    }

    public Material getProfileMenuCloseButtonMaterial() {
        return profileMenuCloseButtonMaterial;
    }

    public void setProfileMenuCloseButtonMaterial(Material profileMenuCloseButtonMaterial) {
        this.profileMenuCloseButtonMaterial = profileMenuCloseButtonMaterial;
    }

    public Integer getProfileMenuCloseButtonCustomModelData() {
        return profileMenuCloseButtonCustomModelData;
    }

    public void setProfileMenuCloseButtonCustomModelData(Integer profileMenuCloseButtonCustomModelData) {
        this.profileMenuCloseButtonCustomModelData = profileMenuCloseButtonCustomModelData;
    }

    public boolean isProfileMenuPreviousPageButtonHideOnFirstPage() {
        return profileMenuPreviousPageButtonHideOnFirstPage;
    }

    public void setProfileMenuPreviousPageButtonHideOnFirstPage(boolean profileMenuPreviousPageButtonHideOnFirstPage) {
        this.profileMenuPreviousPageButtonHideOnFirstPage = profileMenuPreviousPageButtonHideOnFirstPage;
    }

    public String getProfileMenuPreviousPageButtonTitle() {
        return profileMenuPreviousPageButtonTitle;
    }

    public void setProfileMenuPreviousPageButtonTitle(String profileMenuPreviousPageButtonTitle) {
        this.profileMenuPreviousPageButtonTitle = profileMenuPreviousPageButtonTitle;
    }

    public List<String> getProfileMenuPreviousPageButtonLore() {
        return profileMenuPreviousPageButtonLore;
    }

    public void setProfileMenuPreviousPageButtonLore(List<String> profileMenuPreviousPageButtonLore) {
        this.profileMenuPreviousPageButtonLore = profileMenuPreviousPageButtonLore;
    }

    public Material getProfileMenuPreviousPageButtonMaterial() {
        return profileMenuPreviousPageButtonMaterial;
    }

    public void setProfileMenuPreviousPageButtonMaterial(Material profileMenuPreviousPageButtonMaterial) {
        this.profileMenuPreviousPageButtonMaterial = profileMenuPreviousPageButtonMaterial;
    }

    public Integer getProfileMenuPreviousPageButtonCustomModelData() {
        return profileMenuPreviousPageButtonCustomModelData;
    }

    public void setProfileMenuPreviousPageButtonCustomModelData(Integer profileMenuPreviousPageButtonCustomModelData) {
        this.profileMenuPreviousPageButtonCustomModelData = profileMenuPreviousPageButtonCustomModelData;
    }

    public boolean isProfileMenuNextPageButtonHideOnLastPage() {
        return profileMenuNextPageButtonHideOnLastPage;
    }

    public void setProfileMenuNextPageButtonHideOnLastPage(boolean profileMenuNextPageButtonHideOnLastPage) {
        this.profileMenuNextPageButtonHideOnLastPage = profileMenuNextPageButtonHideOnLastPage;
    }

    public String getProfileMenuNextPageButtonTitle() {
        return profileMenuNextPageButtonTitle;
    }

    public void setProfileMenuNextPageButtonTitle(String profileMenuNextPageButtonTitle) {
        this.profileMenuNextPageButtonTitle = profileMenuNextPageButtonTitle;
    }

    public List<String> getProfileMenuNextPageButtonLore() {
        return profileMenuNextPageButtonLore;
    }

    public void setProfileMenuNextPageButtonLore(List<String> profileMenuNextPageButtonLore) {
        this.profileMenuNextPageButtonLore = profileMenuNextPageButtonLore;
    }

    public Material getProfileMenuNextPageButtonMaterial() {
        return profileMenuNextPageButtonMaterial;
    }

    public void setProfileMenuNextPageButtonMaterial(Material profileMenuNextPageButtonMaterial) {
        this.profileMenuNextPageButtonMaterial = profileMenuNextPageButtonMaterial;
    }

    public Integer getProfileMenuNextPageButtonCustomModelData() {
        return profileMenuNextPageButtonCustomModelData;
    }

    public void setProfileMenuNextPageButtonCustomModelData(Integer profileMenuNextPageButtonCustomModelData) {
        this.profileMenuNextPageButtonCustomModelData = profileMenuNextPageButtonCustomModelData;
    }

    public Material getProfileMenuItemMaterial() {
        return profileMenuItemMaterial;
    }

    public void setProfileMenuItemMaterial(Material profileMenuItemMaterial) {
        this.profileMenuItemMaterial = profileMenuItemMaterial;
    }

    public String getDataDeletePlayerOnlineMessage() {
        return dataDeletePlayerOnlineMessage;
    }

    public void setDataDeletePlayerOnlineMessage(String dataDeletePlayerOnlineMessage) {
        this.dataDeletePlayerOnlineMessage = dataDeletePlayerOnlineMessage;
    }

    public boolean isShouldActiveProfileShowEnchanted() {
        return shouldActiveProfileShowEnchanted;
    }

    public void setShouldActiveProfileShowEnchanted(boolean shouldActiveProfileShowEnchanted) {
        this.shouldActiveProfileShowEnchanted = shouldActiveProfileShowEnchanted;
    }

    public String getProfileMenuItemTitleActiveProfile() {
        return profileMenuItemTitleActiveProfile;
    }

    public void setProfileMenuItemTitleActiveProfile(String profileMenuItemTitleActiveProfile) {
        this.profileMenuItemTitleActiveProfile = profileMenuItemTitleActiveProfile;
    }
}
