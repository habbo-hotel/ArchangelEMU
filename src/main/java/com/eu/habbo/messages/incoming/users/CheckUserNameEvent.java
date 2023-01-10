package com.eu.habbo.messages.incoming.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboManager;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.CheckUserNameResultMessageComposer;

import java.util.ArrayList;
import java.util.List;

public class CheckUserNameEvent extends MessageHandler {
    public static String VALID_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-=!?@:,.";

    @Override
    public void handle() {
        if (!this.client.getHabbo().getHabboStats().isAllowNameChange())
            return;

        String name = this.packet.readString();

        int errorCode = CheckUserNameResultMessageComposer.AVAILABLE;

        List<String> suggestions = new ArrayList<>(4);
        if (name.length() < 3) {
            errorCode = CheckUserNameResultMessageComposer.TOO_SHORT;
        } else if (name.length() > 15) {
            errorCode = CheckUserNameResultMessageComposer.TOO_LONG;
        } else if (name.equalsIgnoreCase(this.client.getHabbo().getHabboInfo().getUsername()) || HabboManager.getOfflineHabboInfo(name) != null || ChangeUserNameEvent.changingUsernames.contains(name.toLowerCase())) {
            errorCode = CheckUserNameResultMessageComposer.TAKEN_WITH_SUGGESTIONS;
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
            suggestions.add(name + Emulator.getRandom().nextInt(9999));
        } else if (!Emulator.getGameEnvironment().getWordFilter().filter(name, this.client.getHabbo()).equalsIgnoreCase(name)) {
            errorCode = CheckUserNameResultMessageComposer.NOT_VALID;
        } else {
            String checkName = name;
            for (char c : VALID_CHARACTERS.toCharArray()) {
                checkName = checkName.replace(c + "", "");
            }

            if (!checkName.isEmpty()) {
                errorCode = CheckUserNameResultMessageComposer.NOT_VALID;
            } else {
                this.client.getHabbo().getHabboStats().setChangeNameChecked(name);
            }
        }

        this.client.sendResponse(new CheckUserNameResultMessageComposer(errorCode, name, suggestions));
    }
}
