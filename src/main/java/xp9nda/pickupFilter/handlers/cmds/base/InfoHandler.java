package xp9nda.pickupFilter.handlers.cmds.base;

import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;

public class InfoHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public InfoHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

    @CommandMethod("pickupfilter|pf|itemfilter|if info")
    private void onInfoCommand(Player commandSender) {
        commandSender.sendMessage(miniMessage.deserialize(
                // header
                " \n<#B9C0EA><b>PickupFilter</b>" +
                "\n\n<#ffffff>This server is running plugin version <#B9C0EA>" + plugin.getPluginMeta().getVersion() +
                "\n<#ffffff><hover:show_text:'Open Discord URL.'><click:open_url:'https://discord.gg/88j6jF5WYK'>[⭐ Support Discord]</click></hover>" +

                // created by field
                "\n\n<#ffffff>Created by <#B9C0EA>xP9nda" +
                " <#ffffff><hover:show_text:'Open GitHub URL.'><click:open_url:'https://github.com/xP9nda/'>[GitHub]</click></hover>" +
                " <#ffffff><hover:show_text:'Open Modrinth URL.'><click:open_url:'https://modrinth.com/user/xP9nda'>[Modrinth]</click></hover>" +
                "\n<#ffffff><hover:show_text:'Open Ko-fi URL.'><click:open_url:'https://ko-fi.com/xp9nda'>[❤ Support The Development]</click></hover>\n "
        ));
    }

}
