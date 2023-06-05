package com.eu.habbo.messages.incoming.guilds;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.catalog.PurchaseErrorMessageComposer;
import com.eu.habbo.messages.outgoing.catalog.PurchaseOKMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildCreatedMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.GuildEditFailedMessageComposer;
import com.eu.habbo.messages.outgoing.guilds.HabboGroupDetailsMessageComposer;
import com.eu.habbo.plugin.events.guilds.GuildPurchasedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateGuildEvent extends GuildBadgeEvent {

    @Override
    public void handle() {
        String name = Emulator.getGameEnvironment().getWordFilter().filter(this.packet.readString(), this.client.getHabbo());
        String description = Emulator.getGameEnvironment().getWordFilter().filter(this.packet.readString(), this.client.getHabbo());


        if(name.length() > 29 || description.length() > 254)
            return;

        if (Emulator.getConfig().getBoolean("catalog.guild.hc_required", true) && !this.client.getHabbo().getHabboStats().hasActiveClub()) {
            this.client.sendResponse(new GuildEditFailedMessageComposer(GuildEditFailedMessageComposer.HC_REQUIRED));
            return;
        }

        if (!this.client.getHabbo().hasRight(Permission.ACC_INFINITE_CREDITS)) {
            int guildPrice = Emulator.getConfig().getInt("catalog.guild.price");
            if (this.client.getHabbo().getHabboInfo().getCredits() >= guildPrice) {
                this.client.getHabbo().giveCredits(-guildPrice);
            } else {
                this.client.sendResponse(new PurchaseErrorMessageComposer(PurchaseErrorMessageComposer.SERVER_ERROR));
                return;
            }
        }

        int roomId = this.packet.readInt();

        Room r = Emulator.getGameEnvironment().getRoomManager().getRoom(roomId);

        if (r != null) {
            if (r.hasGuild()) {
                this.client.sendResponse(new GuildEditFailedMessageComposer(GuildEditFailedMessageComposer.ROOM_ALREADY_IN_USE));
                return;
            }

            if (r.getOwnerId() == this.client.getHabbo().getHabboInfo().getId()) {
                if (r.getGuildId() == 0) {
                    int colorOne = this.packet.readInt();
                    int colorTwo = this.packet.readInt();

                    int count = this.packet.readInt();

                    StringBuilder badge = createBadge(count);

                    Guild guild = Emulator.getGameEnvironment().getGuildManager().createGuild(this.client.getHabbo(), roomId, r.getName(), name, description, badge.toString(), colorOne, colorTwo);

                    r.setGuildId(guild.getId());
                    r.removeAllRights();
                    r.setNeedsUpdate(true);

                    if (Emulator.getConfig().getBoolean("imager.internal.enabled")) {
                        Emulator.getBadgeImager().generate(guild);
                    }

                    this.client.sendResponse(new PurchaseOKMessageComposer());
                    this.client.sendResponse(new GuildCreatedMessageComposer(guild));
                    for (Habbo habbo : r.getHabbos()) {
                        habbo.getClient().sendResponse(new HabboGroupDetailsMessageComposer(guild, habbo.getClient(), false, null));
                    }
                    r.refreshGuild(guild);

                    Emulator.getPluginManager().fireEvent(new GuildPurchasedEvent(guild, this.client.getHabbo()));

                    Emulator.getGameEnvironment().getGuildManager().addGuild(guild);
                }
            } else {
                String message = Emulator.getTexts().getValue("scripter.warning.guild.buy.owner").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%roomname%", r.getName().replace("%owner%", r.getOwnerName()));
                ScripterManager.scripterDetected(this.client, message);
                log.info(message);
            }
        }
    }
}
