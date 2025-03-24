package xp9nda.pickupFilter.handlers.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.FilterMode;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemFilterEditMenu {

    private final PickupFilter plugin;
    private final UUID playerUUID;
    private final UUID profileUUID;
    private final String menuName;

    public SmartInventory INVENTORY;
    public ItemFilterEditProvider itemFilterEditProvider;

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ItemFilterEditMenu(PickupFilter plugin, UUID playerUUID, String menuName, UUID profileUUID) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.menuName = menuName;
        this.profileUUID = profileUUID;
    }

    public SmartInventory buildMenu() {
        itemFilterEditProvider = new ItemFilterEditProvider();

        INVENTORY = SmartInventory.builder()
                .id("itemFilterEditMenu")
                .provider(itemFilterEditProvider)
                .size(6, 9)
                .title(menuName)
                .manager(plugin.getInventoryManager())
                .build();

        return INVENTORY;
    }

    public class ItemFilterEditProvider implements InventoryProvider {

        public InventoryContents inventoryContents;
        public UUID thisMenuProfileUUID;

        @Override
        public void init(Player player, InventoryContents inventoryContents) {
            this.inventoryContents = inventoryContents;
            this.thisMenuProfileUUID = profileUUID;

            PickupUser userData = plugin.getDataHolder().getPlayerData(playerUUID);

            if (userData == null) {
                plugin.getDataHolder().addPlayerData(playerUUID, new PickupUser());
                userData = plugin.getDataHolder().getPlayerData(playerUUID);
            }

            // check the profile that is being edited
            if (profileUUID == null || !userData.userHasProfileWithUUID(profileUUID)) {
                player.sendMessage(miniMessage.deserialize(
                        plugin.getConfigHandler().getNoActiveProfileMessage()
                ));
                return;
            }

            // set up the items
            refreshItems(player, inventoryContents);

            PickupUser finalUserData = userData;
            PickupProfile pickupProfileOfThisMenu = finalUserData.getPickupProfile(profileUUID);

            // back button
            if (plugin.getConfigHandler().isEditMenuBackButtonEnabled()) {
                ItemStack backButton = new ItemStack(plugin.getConfigHandler().getEditMenuBackButtonMaterial(), 1);
                backButton.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(
                            plugin.getConfigHandler().getEditMenuBackButtonTitle()
                    ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getEditMenuBackButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg,
                                    Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getEditMenuBackButtonCustomModelData() != 0) {
                    backButton.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getEditMenuBackButtonCustomModelData());
                    });
                }

                inventoryContents.set(5, plugin.getConfigHandler().getEditMenuBackButtonColumnSlot(), ClickableItem.of(
                        backButton,
                        e -> {
                            // construct the menu name
                            Component menuName = miniMessage.deserialize(
                                    plugin.getConfigHandler().getProfileMenuTitle()
                            );

                            String menuNameString = PlainTextComponentSerializer.plainText().serialize(menuName);

                            // open the menu
                            ItemFilterProfilesMenu itemFilterProfilesMenu = new ItemFilterProfilesMenu(plugin, player.getUniqueId(), menuNameString);
                            SmartInventory inventory = itemFilterProfilesMenu.buildMenu();

                            player.closeInventory();
                            inventory.open(player);
                        }
                ));
            }

            // set up the buttons that will need to be updated
            // toggle filter mode button
            if (plugin.getConfigHandler().isEditMenuFilterModeButtonEnabled()) {
                ItemStack filterModeItem = new ItemStack(plugin.getConfigHandler().getEditMenuFilterModeButtonMaterial(), 1);
                filterModeItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(
                            plugin.getConfigHandler().getEditMenuFilterModeButtonName()
                    ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getEditMenuFilterModeButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg,
                                    Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName()),
                                    Placeholder.unparsed("filter_mode", pickupProfileOfThisMenu.getFilterMode().toString().toLowerCase()),
                                    Placeholder.unparsed("filter_mode_opposite", pickupProfileOfThisMenu.getFilterMode().getOpposite().toString().toLowerCase())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                inventoryContents.set(5, plugin.getConfigHandler().getEditMenuFilterModeButtonColumnSlot(), ClickableItem.of(
                        filterModeItem,
                        e -> {
                            FilterMode filterMode = pickupProfileOfThisMenu.getFilterMode();

                            switch (filterMode) {
                                case BLACKLIST:
                                    pickupProfileOfThisMenu.setFilterMode(FilterMode.WHITELIST);
                                    break;
                                case WHITELIST:
                                    pickupProfileOfThisMenu.setFilterMode(FilterMode.BLACKLIST);
                                    break;
                            }

                            // send the player a message that the filter mode has been toggled
                            player.sendMessage(miniMessage.deserialize(
                                    plugin.getConfigHandler().getProfileStateChangedMessage(),
                                    Placeholder.unparsed("profile_state", pickupProfileOfThisMenu.getFilterMode().toString().toLowerCase()),
                                    Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

                            // Refresh the inventory
                            init(player, inventoryContents);
                        }
                ));
            }
        }

        public void refreshItems(Player player, InventoryContents inventoryContents) {
            // safety check
            if (player == null || inventoryContents == null) {
                return;
            }

            PickupUser userData = plugin.getDataHolder().getPlayerData(playerUUID);

            PickupProfile pickupProfileOfThisMenu = userData.getPickupProfile(profileUUID);
            List<ItemStack> filteredItems = getItemStacks(pickupProfileOfThisMenu);

//            plugin.getSLF4JLogger().info("Filtered items: " + filteredItems.size());

            Pagination pagination = inventoryContents.pagination();
            int ITEMS_PER_PAGE = 45;
            pagination.setItemsPerPage(ITEMS_PER_PAGE);

            ClickableItem[] clickableItems = new ClickableItem[filteredItems.size()];
            for (int i = 0; i < filteredItems.size(); i++) {
                ItemStack itemStack = filteredItems.get(i).clone();
                final String[] originalSerializedString = {plugin.getDataHolder().getSerializedStringFromItemStack(itemStack)};

                ItemStack displayItemStack = itemStack.clone();
                displayItemStack.editMeta(meta -> {
                    meta.lore(
                            plugin.getConfigHandler().getEditMenuRemoveFromProfileItemLore().stream()
                                    .map(loreMsg -> miniMessage.deserialize(
                                            loreMsg,
                                            Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName())
                                    ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                                    .toList()
                    );
                });

                clickableItems[i] = ClickableItem.of(displayItemStack, e -> {
                    if (originalSerializedString[0] == null) {
                        // deserialize the item stack to a string
                        originalSerializedString[0] = plugin.getDataUtils().itemStackToString(itemStack);
                    }

                    pickupProfileOfThisMenu.removeStringInFilterList(originalSerializedString[0]);

                    // if this menu is the active menu for the player, then remove the item from the player's active profile
                    if (userData.getActiveProfileUUID() != null) {
                        if (userData.getActiveProfileUUID().equals(profileUUID)) {
                            plugin.getDataHolder().removePlayerActiveProfileItem(playerUUID, itemStack);
                            plugin.getDataHolder().removeSerializedStringToItemStack(originalSerializedString[0]);
                        }

                    }

                    player.sendMessage(miniMessage.deserialize(
                            plugin.getConfigHandler().getProfileItemRemovedFromFilterMessage(),
                            Placeholder.component("item_name", itemStack.displayName()),
                            Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName())
                    ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

                    refreshItems(player, inventoryContents);
                });
            }

            pagination.setItems(clickableItems);
            pagination.addToIterator(inventoryContents.newIterator(SlotIterator.Type.HORIZONTAL, 0, 0));

            setupBottomButtons(player, inventoryContents, pagination);
        }

        @Override
        public void update(Player player, InventoryContents inventoryContents) {
            // do nothing
        }

        private void setupBottomButtons(Player player, InventoryContents inventoryContents, Pagination pagination) {
            PickupUser user = plugin.getDataHolder().getPlayerData(player.getUniqueId());
            PickupProfile pickupProfileOfThisMenu = user.getPickupProfile(profileUUID);

            // Close Button
            if (plugin.getConfigHandler().isEditMenuCloseButtonEnabled()) {
                ItemStack closeButtonItem = new ItemStack(plugin.getConfigHandler().getEditMenuCloseButtonMaterial(), 1);

                closeButtonItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getEditMenuCloseButtonName()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getEditMenuCloseButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg,
                                    Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getEditMenuCloseButtonCustomModelData() != 0) {
                    closeButtonItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getEditMenuCloseButtonCustomModelData());
                    });
                }

                inventoryContents.set(5, plugin.getConfigHandler().getEditMenuCloseButtonColumnSlot(), ClickableItem.of(
                        closeButtonItem,
                        e -> player.closeInventory()
                ));
            }

            // Pagination Buttons

            // if we are on the first page and the previous page button should be hidden if so
            boolean hideFirstPageButton = pagination.isFirst() && plugin.getConfigHandler().isPreviousPageButtonHideOnFirstPage();

            if (!hideFirstPageButton) {
                ItemStack previousPageItem = new ItemStack(plugin.getConfigHandler().getPreviousPageButtonMaterial(), 1);
                previousPageItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getPreviousPageButtonName()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getPreviousPageButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg,
                                    Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getPreviousPageButtonCustomModelData() != 0) {
                    previousPageItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getPreviousPageButtonCustomModelData());
                    });
                }

                inventoryContents.set(5, plugin.getConfigHandler().getPreviousPageButtonColumnSlot(), ClickableItem.of(
                        previousPageItem,
                        e -> buildMenu().open(player, pagination.previous().getPage())
                ));
            }


            // if we are on the last page and the button should be hidden if so
            boolean hideLastPageButton = pagination.isLast() && plugin.getConfigHandler().isNextPageButtonHideOnLastPage();

            if (!hideLastPageButton) {
                ItemStack nextPageItem = new ItemStack(plugin.getConfigHandler().getNextPageButtonMaterial(), 1);
                nextPageItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getNextPageButtonName()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getNextPageButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg,
                                    Placeholder.unparsed("profile_name", pickupProfileOfThisMenu.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getNextPageButtonCustomModelData() != 0) {
                    nextPageItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getNextPageButtonCustomModelData());
                    });
                }

                inventoryContents.set(5, plugin.getConfigHandler().getNextPageButtonColumnSlot(), ClickableItem.of(
                        nextPageItem,
                        e -> buildMenu().open(player, pagination.next().getPage())
                ));
            }

            // fill the rest of the bottom row with filler items (black stained glass pane)
            Integer customModelDataForFillerItem = plugin.getConfigHandler().getEditMenuBottomRowFillerCustomModelData();
            ItemStack fillerItem = new ItemStack(plugin.getConfigHandler().getEditMenuBottomRowFillerMaterial(), 1);
            fillerItem.editMeta(meta -> {
                meta.displayName(Component.text(" "));
            });

            if (customModelDataForFillerItem != 0) {
                fillerItem.editMeta(meta -> {
                    meta.setCustomModelData(customModelDataForFillerItem);
                });
            }

            for (int i = 0; i < 9; i++) {
                if (inventoryContents.get(5, i).isPresent()) {
                    continue;
                }

                inventoryContents.set(5, i, ClickableItem.empty(fillerItem));
            }
        }
    }

    private @NotNull List<ItemStack> getItemStacks(PickupProfile profileToGetItemsFrom) {
        List<String> serializedStrings = profileToGetItemsFrom.getPickupFilter();

        List<ItemStack> filteredItems = new ArrayList<>();
        serializedStrings.forEach(serializedString -> {
            ItemStack existingItem = plugin.getDataHolder().getItemStackFromSerializedString(serializedString);
            if (existingItem == null) {
                existingItem = plugin.getDataUtils().stringToItemStack(serializedString);
            }
            filteredItems.add(existingItem);
        });
        return filteredItems;
    }

}