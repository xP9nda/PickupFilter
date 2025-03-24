package xp9nda.pickupFilter.handlers.cmds.base;

import cloud.commandframework.annotations.CommandMethod;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import xp9nda.pickupFilter.PickupFilter;

public class HelpHandler {

    private final PickupFilter plugin;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();

    public HelpHandler(PickupFilter plugin) {
        this.plugin = plugin;
    }

//    @CommandMethod("pickupfilter|pf|itemfilter|if help")
//    private void onEmptyCommand(Player commandSender) {
//        commandSender.sendMessage(miniMessage.deserialize(
//                // header
//                " \n<#B9C0EA><b>PickupFilter</b> <#ffffff>plugin commands:" +
//
//                // command list
//                "\n<#B9C0EA><hover:show_text:'Show plugin commands.'><click:run_command:'/pickupfilter help'>[Help]</click></hover>" +
//                " <#B9C0EA><hover:show_text:'Show plugin info.'><click:run_command:'/pickupfilter info'>[Info]</click></hover> \n "
//        ));
//    }

}
