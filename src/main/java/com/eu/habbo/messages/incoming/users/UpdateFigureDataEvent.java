package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.modtool.ScripterManager;
import com.eu.habbo.habbohotel.users.HabboGender;
import com.eu.habbo.habbohotel.users.clothingvalidation.ClothingValidationManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.rooms.users.UserChangeMessageComposer;
import com.eu.habbo.messages.outgoing.users.FigureUpdateComposer;
import com.eu.habbo.plugin.events.users.UserSavedLookEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UpdateFigureDataEvent extends MessageHandler {

    @Override
    public void handle() {
        String genderCode = this.packet.readString();
        HabboGender gender;

        try {
            gender = HabboGender.valueOf(genderCode);
        } catch (IllegalArgumentException e) {
            String message = Emulator.getTexts().getValue("scripter.warning.look.gender").replace("%username%", this.client.getHabbo().getHabboInfo().getUsername()).replace("%gender%", genderCode);
            ScripterManager.scripterDetected(this.client, message);
            log.info(message);
            return;
        }

        String look = this.packet.readString();

        UserSavedLookEvent lookEvent = new UserSavedLookEvent(this.client.getHabbo(), gender, look);
        Emulator.getPluginManager().fireEvent(lookEvent);
        if (lookEvent.isCancelled())
            return;

        this.client.getHabbo().getHabboInfo().setLook(ClothingValidationManager.VALIDATE_ON_CHANGE_LOOKS ? ClothingValidationManager.validateLook(this.client.getHabbo(), lookEvent.getNewLook(), lookEvent.getGender().name()) : lookEvent.getNewLook());
        this.client.getHabbo().getHabboInfo().setGender(lookEvent.getGender());
        Emulator.getThreading().run(this.client.getHabbo().getHabboInfo());
        this.client.sendResponse(new FigureUpdateComposer(this.client.getHabbo()));
        if (this.client.getHabbo().getHabboInfo().getCurrentRoom() != null) {
            this.client.getHabbo().getHabboInfo().getCurrentRoom().sendComposer(new UserChangeMessageComposer(this.client.getHabbo()).compose());
        }

        AchievementManager.progressAchievement(this.client.getHabbo(), Emulator.getGameEnvironment().getAchievementManager().getAchievement("AvatarLooks"));
    }
}
