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

                inventoryContents.set(1, 4, ClickableItem.of(
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

                inventoryContents.set(1, 3, ClickableItem.of(
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

                inventoryContents.set(1, 5, ClickableItem.of(
                        nextPageItem,
                        e -> buildMenu().open(player, pagination.next().getPage())
                ));
            }

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
    }

}