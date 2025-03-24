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
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xp9nda.pickupFilter.PickupFilter;
import xp9nda.pickupFilter.data.data.PickupProfile;
import xp9nda.pickupFilter.data.data.PickupUser;

import java.util.UUID;

public class ItemFilterProfilesMenu {

    private final PickupFilter plugin;
    private final UUID playerUUID;
    private final String menuName;

    public SmartInventory INVENTORY;
    public ItemFilterProfileProvider itemFilterEditProvider;

    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public ItemFilterProfilesMenu(PickupFilter plugin, UUID playerUUID, String menuName) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.menuName = menuName;
    }

    public SmartInventory buildMenu() {

        itemFilterEditProvider = new ItemFilterProfileProvider();

        INVENTORY = SmartInventory.builder()
                .id("itemFilterProfileMenu")
                .provider(itemFilterEditProvider)
                .size(2, 9)
                .title(menuName)
                .manager(plugin.getInventoryManager())
                .build();

        return INVENTORY;
    }

    public class ItemFilterProfileProvider implements InventoryProvider {

        public InventoryContents inventoryContents;

        @Override
        public void init(Player player, InventoryContents inventoryContents) {
            this.inventoryContents = inventoryContents;

            // set up the items
            refreshItems(player, inventoryContents);
        }

        public void refreshItems(Player player, InventoryContents inventoryContents) {
            // safety check
            if (player == null || inventoryContents == null) {
                return;
            }

            Pagination pagination = inventoryContents.pagination();
            int ITEMS_PER_PAGE = 9;
            pagination.setItemsPerPage(ITEMS_PER_PAGE);

            PickupUser user = plugin.getDataHolder().getPlayerData(player.getUniqueId());

            int numberOfProfiles = user.getPickupProfiles().size();
            ClickableItem[] clickableItems = new ClickableItem[numberOfProfiles];

            UUID activeProfileUUID = user.getActiveProfileUUID();

            int i = 0;
            for (PickupProfile profile : user.getPickupProfiles().values()) {
                ItemStack profileItem = new ItemStack(plugin.getConfigHandler().getProfileMenuItemMaterial(), 1);
                profileItem.editMeta(meta -> {
                    meta.lore(
                            plugin.getConfigHandler().getProfileMenuItemLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg,
                                    Placeholder.unparsed("profile_name", profile.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                // if the profile is the active one
                if (activeProfileUUID != null) {
                    if (profile.getProfileUUID().equals(activeProfileUUID)) {
                        // update the item name to show that it is the active profile
                        profileItem.editMeta(meta -> {
                            meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getProfileMenuItemTitleActiveProfile(),
                                    Placeholder.unparsed("profile_name", profile.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                        });

                        // set the enchantment glint
                        if (plugin.getConfigHandler().isShouldActiveProfileShowEnchanted()) {
                            profileItem.editMeta(meta -> {
                                meta.setEnchantmentGlintOverride(true);
                            });
                        } else {
                            profileItem.editMeta(meta -> {
                                meta.setEnchantmentGlintOverride(false);
                            });
                        }
                    } else {
                        // update the item name to show that it is not the active profile
                        profileItem.editMeta(meta -> {
                            meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getProfileMenuItemTitle(),
                                    Placeholder.unparsed("profile_name", profile.getProfileName())
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                        });
                    }
                } else {
                    // update the item name to show that it is not the active profile
                    profileItem.editMeta(meta -> {
                        meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getProfileMenuItemTitle(),
                                Placeholder.unparsed("profile_name", profile.getProfileName())
                        ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    });
                }

                clickableItems[i] = ClickableItem.of(
                        profileItem,
                        e -> {

                            if (e.getClick() == ClickType.DROP) {
                                // if the player is trying to delete the profile they are currently using
                                if (activeProfileUUID != null) {
                                    if (activeProfileUUID.equals(profile.getProfileUUID())) {
                                        // set the player's active profile to null
                                        user.setActiveProfileUUID(null);

                                        // stop tracking the profile
                                        plugin.getDataHolder().clearPlayerActiveProfileItems(player.getUniqueId());

                                        // tell the user they no longer have an active profile
                                        player.sendMessage(miniMessage.deserialize(
                                                plugin.getConfigHandler().getNoLongerHaveActiveProfileMessage()
                                        ));
                                    }
                                }

                                // send a success message to the player
                                player.sendMessage(miniMessage.deserialize(
                                        plugin.getConfigHandler().getProfileDeletedSuccessMessage(),
                                        Placeholder.unparsed("profile_name", profile.getProfileName())
                                ));

                                user.removePickupProfile(profile.getProfileUUID());

                                // refresh the inventory
                                refreshItems(player, inventoryContents);
                            }


                            // if it is a left click, open the edit menu
                            if (e.isLeftClick()) {
                                // construct the menu name
                                Component menuName = miniMessage.deserialize(
                                        plugin.getConfigHandler().getEditMenuName(),
                                        Placeholder.unparsed("profile_name", profile.getProfileName())
                                );

                                String menuNameString = PlainTextComponentSerializer.plainText().serialize(menuName);

                                // open the menu
                                ItemFilterEditMenu itemFilterEditMenu = new ItemFilterEditMenu(plugin, player.getUniqueId(), menuNameString, profile.getProfileUUID());
                                SmartInventory inventory = itemFilterEditMenu.buildMenu();
                                player.closeInventory();

                                plugin.getItemFilterEditListener().addActiveEditMenu(player.getUniqueId(), inventory);
                                inventory.open(player);

                            // if it is a right click, set the profile as the active one
                            } else if (e.isRightClick()) {
                                user.setActiveProfileUUID(profile.getProfileUUID());

                                // trigger a refresh of the player's filters
                                plugin.getDataHolder().clearPlayerActiveProfileItems(player.getUniqueId());

                                // run through each item in the newly active profile and add it to the player's active profile filtered items in dataholder
                                for (String serializedItem : user.getPickupProfile(profile.getProfileUUID()).getPickupFilter()) {
                                    ItemStack itemStackToFilter = plugin.getDataHolder().getItemStackFromSerializedString(serializedItem);

                                    // if the item has not been serialized, then serialize it using the datautils
                                    if (itemStackToFilter == null) {
                                        itemStackToFilter = plugin.getDataUtils().stringToItemStack(serializedItem);
                                    }

                                    plugin.getDataHolder().addPlayerActiveProfileItem(player.getUniqueId(), itemStackToFilter);
                                    plugin.getDataHolder().addSerializedStringAndItemStack(serializedItem, itemStackToFilter);
                                }

                                player.sendMessage(miniMessage.deserialize(
                                        plugin.getConfigHandler().getProfileUseMessage(),
                                        Placeholder.unparsed("profile_name", profile.getProfileName())
                                ));

                                // refresh the menu
                                refreshItems(player, inventoryContents);
                            }

                        }
                );

                i++;
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

            // Close Button
            if (plugin.getConfigHandler().isProfileMenuCloseButtonEnabled()) {
                ItemStack closeButtonItem = new ItemStack(plugin.getConfigHandler().getProfileMenuCloseButtonMaterial(), 1);

                closeButtonItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getProfileMenuCloseButtonTitle()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getProfileMenuCloseButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getEditMenuCloseButtonCustomModelData() != 0) {
                    closeButtonItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getEditMenuCloseButtonCustomModelData());
                    });
                }

                inventoryContents.set(1, plugin.getConfigHandler().getProfileMenuCloseButtonColumnSlot(), ClickableItem.of(
                        closeButtonItem,
                        e -> player.closeInventory()
                ));
            }

            // Pagination Buttons

            // if we are on the first page and the previous page button should be hidden if so
            boolean hideFirstPageButton = pagination.isFirst() && plugin.getConfigHandler().isProfileMenuPreviousPageButtonHideOnFirstPage();

            if (!hideFirstPageButton) {
                ItemStack previousPageItem = new ItemStack(plugin.getConfigHandler().getProfileMenuPreviousPageButtonMaterial(), 1);
                previousPageItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getProfileMenuPreviousPageButtonTitle()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getProfileMenuPreviousPageButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getProfileMenuPreviousPageButtonCustomModelData() != 0) {
                    previousPageItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getProfileMenuPreviousPageButtonCustomModelData());
                    });
                }

                inventoryContents.set(1, plugin.getConfigHandler().getProfileMenuPreviousPageButtonColumnSlot(), ClickableItem.of(
                        previousPageItem,
                        e -> buildMenu().open(player, pagination.previous().getPage())
                ));
            }


            // if we are on the last page and the button should be hidden if so
            boolean hideLastPageButton = pagination.isLast() && plugin.getConfigHandler().isProfileMenuNextPageButtonHideOnLastPage();

            if (!hideLastPageButton) {
                ItemStack nextPageItem = new ItemStack(plugin.getConfigHandler().getProfileMenuNextPageButtonMaterial(), 1);
                nextPageItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(plugin.getConfigHandler().getProfileMenuNextPageButtonTitle()).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getProfileMenuNextPageButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getProfileMenuNextPageButtonCustomModelData() != 0) {
                    nextPageItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getProfileMenuNextPageButtonCustomModelData());
                    });
                }

                inventoryContents.set(1, plugin.getConfigHandler().getProfileMenuNextPageButtonColumnSlot(), ClickableItem.of(
                        nextPageItem,
                        e -> buildMenu().open(player, pagination.next().getPage())
                ));
            }

            // toggle filter enabled/disabled button
            if (plugin.getConfigHandler().isEditMenuFilterEnabledButtonEnabled()) {
                ItemStack filterEnabledItem = new ItemStack(plugin.getConfigHandler().getEditMenuFilterEnabledButtonMaterial(), 1);

                filterEnabledItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(
                            plugin.getConfigHandler().getEditMenuFilterEnabledButtonName()
                    ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getEditMenuFilterEnabledButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg,
                                    Placeholder.unparsed("filter_enabled_oo", user.isPickupEnabled() ? "on" : "off"),
                                    Placeholder.unparsed("filter_enabled_ed", user.isPickupEnabled() ? "enabled" : "disabled")
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getEditMenuFilterEnabledButtonCustomModelData() != 0) {
                    filterEnabledItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getEditMenuFilterEnabledButtonCustomModelData());
                    });
                }

                inventoryContents.set(1, plugin.getConfigHandler().getEditMenuFilterEnabledButtonColumnSlot(), ClickableItem.of(
                        filterEnabledItem,
                        e -> {
                            user.setPickupEnabled(!user.isPickupEnabled());

                            // send the player a message that the filter has been turned on/off
                            player.sendMessage(miniMessage.deserialize(
                                    plugin.getConfigHandler().getItemFilterToggledMessage(),
                                    Placeholder.unparsed("new_enabled_state", user.isPickupEnabled() ? "enabled" : "disabled"),
                                    Placeholder.unparsed("new_enabled_state_bool", user.isPickupEnabled() ? "true" : "false"),
                                    Placeholder.unparsed("old_enabled_state", user.isPickupEnabled() ? "disabled" : "enabled"),
                                    Placeholder.unparsed("old_enabled_state_bool", user.isPickupEnabled() ? "false" : "true")
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

                            // Refresh the inventory
                            init(player, inventoryContents);
                        }
                ));
            }

            setupNewProfileButton(user, player, inventoryContents);

            // fill the rest of the bottom row with filler items (black stained glass pane)
            Integer customModelDataForFillerItem = plugin.getConfigHandler().getProfileMenuBottomRowCustomModelData();
            ItemStack fillerItem = new ItemStack(plugin.getConfigHandler().getProfileMenuBottomRowMaterial(), 1);
            fillerItem.editMeta(meta -> {
                meta.displayName(Component.text(" "));
            });

            if (customModelDataForFillerItem != 0) {
                fillerItem.editMeta(meta -> {
                    meta.setCustomModelData(customModelDataForFillerItem);
                });
            }

            for (int i = 0; i < 9; i++) {
                if (inventoryContents.get(1, i).isPresent()) {
                    continue;
                }

                inventoryContents.set(1, i, ClickableItem.empty(fillerItem));
            }
        }

        private void setupNewProfileButton(PickupUser user, Player player, InventoryContents inventoryContents) {
            if (plugin.getConfigHandler().isProfileMenuCreateNewProfileButtonEnabled()) {
                // check if the user is allowed to create a new profile
                int maxAllowedProfilesForUser = plugin.getProfileCreateHandler().getMaxAllowedProfilesForUser(player);
                int numberOfProfiles = user.getPickupProfiles().size();

                if (numberOfProfiles >= maxAllowedProfilesForUser) {
                    return;
                }

                ItemStack createNewProfileItem = new ItemStack(plugin.getConfigHandler().getProfileMenuCreateNewProfileButtonMaterial(), 1);

                createNewProfileItem.editMeta(meta -> {
                    meta.displayName(miniMessage.deserialize(
                            plugin.getConfigHandler().getProfileMenuCreateNewProfileButtonTitle()
                    ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
                    meta.lore(
                            plugin.getConfigHandler().getProfileMenuCreateNewProfileButtonLore().stream().map(loreMsg -> miniMessage.deserialize(
                                    loreMsg
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE)).toList()
                    );
                });

                if (plugin.getConfigHandler().getProfileMenuCreateNewProfileButtonCustomModelData() != 0) {
                    createNewProfileItem.editMeta(meta -> {
                        meta.setCustomModelData(plugin.getConfigHandler().getProfileMenuCreateNewProfileButtonCustomModelData());
                    });
                }

                inventoryContents.set(1, plugin.getConfigHandler().getProfileMenuCreateNewProfileButtonColumnSlot(), ClickableItem.of(
                        createNewProfileItem,
                        e -> {
                            // register that the player has entered the profile creation state
                            plugin.getProfileCreationButtonChatHandler().addPlayerToProfileCreation(player.getUniqueId());

                            // send the player a message that they are now in the profile creation state
                            player.sendMessage(miniMessage.deserialize(
                                    plugin.getConfigHandler().getNewProfileEnterNameInChatMessage()
                            ).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));

                            // close the inventory
                            player.closeInventory();
                        }
                ));
            }
        }
    }

}