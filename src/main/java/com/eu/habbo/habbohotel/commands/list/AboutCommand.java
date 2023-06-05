package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.catalog.CatalogManager;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.generic.alerts.MOTDNotificationComposer;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class AboutCommand extends Command {
    public static final String CREDITS = """
            Arcturus Morningstar is an opensource project based on Arcturus By TheGeneral\s
            The Following people have all contributed to this emulator:
             TheGeneral
             Beny
             Alejandro
             Capheus
             Skeletor
             Harmonic
             Mike
             Remco
             zGrav\s
             Quadral\s
             Harmony
             Swirny
             ArpyAge
             Mikkel
             Rodolfo
             Rasmus
             Kitt Mustang
             Snaiker
             nttzx
             necmi
             Dome
             Jose Flores
             Cam
             Oliver
             Narzo
             Tenshie
             MartenM
             Ridge
             SenpaiDipper
             Snaiker
             Thijmen""";

    public AboutCommand() {
        super("cmd_about");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {

        Emulator.getRuntime().gc();

        int seconds = Emulator.getIntUnixTimestamp() - Emulator.getTimeStarted();
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24L);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        String message = "<b>" + Emulator.VERSION + "</b>\r\n";

        if (Emulator.getConfig().getBoolean("info.shown", true)) {
            message += "<b>Hotel Statistics</b>\r" +
                    "- Online Users: " + Emulator.getGameEnvironment().getHabboManager().getOnlineCount() + "\r" +
                    "- Active Rooms: " + Emulator.getGameEnvironment().getRoomManager().getActiveRooms().size() + "\r" +
                    "- Shop:  " + Emulator.getGameEnvironment().getCatalogManager().catalogPages.size() + " pages and " + CatalogManager.catalogItemAmount + " items. \r" +
                    "- Furni: " + Emulator.getGameEnvironment().getItemManager().getItems().size() + " item definitions" + "\r" +
                    "\n" +
                    "<b>Server Statistics</b>\r" +
                    "- Uptime: " + day + (day > 1 ? " days, " : " day, ") + hours + (hours > 1 ? " hours, " : " hour, ") + minute + (minute > 1 ? " minutes, " : " minute, ") + second + (second > 1 ? " seconds!" : " second!") + "\r" +
                    "- RAM Usage: " + (Emulator.getRuntime().totalMemory() - Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "/" + (Emulator.getRuntime().freeMemory()) / (1024 * 1024) + "MB\r" +
                    "- CPU Cores: " + Emulator.getRuntime().availableProcessors() + "\r" +
                    "- Total Memory: " + Emulator.getRuntime().maxMemory() / (1024 * 1024) + "MB" + "\r\n";
        }

        message += "\r" +

                "<b>Thanks for using Arcturus. Report issues on the forums. http://arcturus.wf \r\r" +
                "    - The General";
        gameClient.getHabbo().alert(message);
        gameClient.sendResponse(new MOTDNotificationComposer(Collections.singletonList(CREDITS)));
        return true;
    }
}
