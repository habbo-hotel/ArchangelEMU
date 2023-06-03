package com.eu.habbo.messages.incoming.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.modtool.ModToolSanctionItem;
import com.eu.habbo.habbohotel.modtool.ModToolSanctions;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.modtool.IssueCloseNotificationMessageComposer;
import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;

public class ModTradingLockEvent extends MessageHandler {
    @Override
    public void handle() {
        int userId = this.packet.readInt();
        String message = this.packet.readString();
        int duration = this.packet.readInt();
        int cfhTopic = this.packet.readInt();

        if (this.client.getHabbo().hasRight(Permission.ACC_SUPPORTTOOL)) {
            Habbo habbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(userId);

            if (habbo != null) {
                ModToolSanctions modToolSanctions = Emulator.getGameEnvironment().getModToolSanctions();

                if (Emulator.getConfig().getBoolean("hotel.sanctions.enabled")) {
                    THashMap<Integer, ArrayList<ModToolSanctionItem>> modToolSanctionItemsHashMap = Emulator.getGameEnvironment().getModToolSanctions().getSanctions(userId);
                    ArrayList<ModToolSanctionItem> modToolSanctionItems = modToolSanctionItemsHashMap.get(userId);

                    if (modToolSanctionItems != null && !modToolSanctionItems.isEmpty()) {
                        ModToolSanctionItem item = modToolSanctionItems.get(modToolSanctionItems.size() - 1);
                        modToolSanctions.run(userId, this.client.getHabbo(), item.getSanctionLevel(), cfhTopic, message, duration, false, 0);

                    } else {
                        modToolSanctions.run(userId, this.client.getHabbo(), 0, cfhTopic, message, duration, false, 0);
                    }
                } else {
                    habbo.getHabboStats().setAllowTrade(false);
                    habbo.alert(message);
                }
            } else {
                this.client.sendResponse(new IssueCloseNotificationMessageComposer(Emulator.getTexts().getValue("generic.user.not_found").replace("%user%", Emulator.getConfig().getValue("hotel.player.name"))));
            }
        }
    }
}