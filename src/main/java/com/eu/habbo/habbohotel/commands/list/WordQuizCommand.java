package com.eu.habbo.habbohotel.commands.list;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.rooms.RoomChatMessageBubbles;

import java.util.Arrays;

public class WordQuizCommand extends Command {
    public WordQuizCommand() {
        super("cmd_word_quiz");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (!gameClient.getHabbo().getHabboInfo().getCurrentRoom().hasActiveWordQuiz()) {
            if(params.length == 1) {
                gameClient.getHabbo().whisper(getTextsValue("commands.description.cmd_word_quiz"), RoomChatMessageBubbles.ALERT);
                return  true;
            }
            StringBuilder question = new StringBuilder();
            int duration = 60;

            try {
                duration =  Integer.parseInt(params[params.length-1]);
                params = Arrays.copyOf(params, params.length-1);
            }
            catch (Exception ignored) {}

            for (int i = 1; i < params.length; i++) {
                question.append(" ").append(params[i]);
            }

            gameClient.getHabbo().getHabboInfo().getCurrentRoom().startWordQuiz(question.toString(), duration * 1000);
        }
        return true;
    }
}