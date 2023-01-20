package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.HabboInfo;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.ChangeUserNameResultMessageEvent;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.messages.outgoing.rooms.users.UserNameChangedMessageComposer;
import com.eu.habbo.messages.outgoing.users.CheckUserNameResultMessageComposer;
import com.eu.habbo.messages.outgoing.users.UserObjectComposer;
import com.eu.habbo.plugin.events.users.UserNameChangedEvent;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ChangeUserNameEvent extends MessageHandler {

    public static final List<String> changingUsernames = new ArrayList<>(2);

    @Override
    public void handle() {
        if (!this.client.getHabbo().getHabboStats().isAllowNameChange())
            return;

        String name = this.packet.readString();

        if (name.equalsIgnoreCase(this.client.getHabbo().getHabboInfo().getUsername())) {
            this.client.getHabbo().getHabboStats().setAllowNameChange(false);
            this.client.sendResponse(new ChangeUserNameResultMessageEvent(this.client.getHabbo()));
            this.client.sendResponse(new UserNameChangedMessageComposer(this.client.getHabbo()).compose());
            this.client.sendResponse(new UserObjectComposer(this.client.getHabbo()));
            AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("Name"));
            return;
        }

        if (name.equals(this.client.getHabbo().getHabboStats().getChangeNameChecked())) {
            HabboInfo habboInfo = HabboManager.getOfflineHabboInfo(name);

            if (habboInfo == null) {
                synchronized (changingUsernames) {
                    if (changingUsernames.contains(name))
                        return;

                    changingUsernames.add(name);
                }

                String oldName = this.client.getHabbo().getHabboInfo().getUsername();
                this.client.getHabbo().getHabboStats().setAllowNameChange(false);
                this.client.getHabbo().getHabboInfo().setUsername(name);
                this.client.getHabbo().getHabboInfo().run();

                Emulator.getPluginManager().fireEvent(new UserNameChangedEvent(this.client.getHabbo(), oldName));
                for (Room room : Emulator.getGameEnvironment().getRoomManager().getRoomsForHabbo(this.client.getHabbo())) {
                    room.setOwnerName(name);
                    room.setNeedsUpdate(true);
                    room.save();
                }

                synchronized (changingUsernames) {
                    changingUsernames.remove(name);
                }

                this.client.sendResponse(new ChangeUserNameResultMessageEvent(this.client.getHabbo()));

                if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                    this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserNameChangedMessageComposer(this.client.getHabbo()).compose());
                } else {
                    this.client.sendResponse(new UserNameChangedMessageComposer(this.client.getHabbo()).compose());
                }

                this.client.getHabbo().getMessenger().connectionChanged(this.client.getHabbo(), true, this.client.getHabbo().getHabboInfo().getCurrentRoom() != null);
                this.client.getHabbo().getClient().sendResponse(new UserObjectComposer(this.client.getHabbo()));

                try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO namechange_log (user_id, old_name, new_name, timestamp) VALUES (?, ?, ?, ?) ")) {
                    statement.setInt(1, this.client.getHabbo().getHabboInfo().getId());
                    statement.setString(2, oldName);
                    statement.setString(3, name);
                    statement.setInt(4, Emulator.getIntUnixTimestamp());
                    statement.execute();
                } catch (SQLException e) {
                    log.error("Caught SQL exception", e);
                }
            } else {
                this.client.sendResponse(new CheckUserNameResultMessageComposer(CheckUserNameResultMessageComposer.TAKEN_WITH_SUGGESTIONS, name, new ArrayList<>()));
            }
        }
        AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("Name"));
    }
}
