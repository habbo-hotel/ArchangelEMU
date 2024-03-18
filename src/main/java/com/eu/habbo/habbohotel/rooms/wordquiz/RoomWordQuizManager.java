package com.eu.habbo.habbohotel.rooms.wordquiz;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.types.IRoomManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.polls.infobus.QuestionAnsweredComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.QuestionComposer;
import com.eu.habbo.messages.outgoing.polls.infobus.QuestionFinishedComposer;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RoomWordQuizManager extends IRoomManager {
    private final List<Integer> userVotes;

    private String wordQuiz = "";
    private int noVotes = 0;
    private int yesVotes = 0;
    private int wordQuizEnd = 0;

    public RoomWordQuizManager(Room room) {
        super(room);
        this.userVotes = new ArrayList<>();
        this.wordQuiz = "";
        this.yesVotes = 0;
        this.noVotes = 0;
    }

    public void handleWordQuiz(Habbo habbo, String answer) {
        synchronized (this.userVotes) {
            if (!this.wordQuiz.isEmpty() && !this.hasVotedInWordQuiz(habbo)) {
                answer = answer.replace(":", "");

                if (answer.equals("0")) {
                    this.noVotes++;
                } else if (answer.equals("1")) {
                    this.yesVotes++;
                }

                this.sendComposer(new QuestionAnsweredComposer(habbo.getHabboInfo().getId(), answer, this.noVotes, this.yesVotes).compose());
                this.userVotes.add(habbo.getHabboInfo().getId());
            }
        }
    }

    public void startWordQuiz(String question, int duration) {
        if (!this.hasActiveWordQuiz()) {
            this.wordQuiz = question;
            this.noVotes = 0;
            this.yesVotes = 0;
            this.userVotes.clear();
            this.wordQuizEnd = Emulator.getIntUnixTimestamp() + (duration / 1000);
            this.sendComposer(new QuestionComposer(duration, question).compose());
        }
    }

    public boolean hasActiveWordQuiz() {
        return Emulator.getIntUnixTimestamp() < this.wordQuizEnd;
    }

    public boolean hasVotedInWordQuiz(Habbo habbo) {
        return this.userVotes.contains(habbo.getHabboInfo().getId());
    }

    public void onHabboEntered(Habbo habbo) {
        if (room.getRoomWordQuizManager().hasActiveWordQuiz()) {
            habbo.getClient().sendResponse(new QuestionComposer((Emulator.getIntUnixTimestamp() - room.getRoomWordQuizManager().getWordQuizEnd()) * 1000, room.getRoomWordQuizManager().getWordQuiz()));

            if (room.getRoomWordQuizManager().hasVotedInWordQuiz(habbo)) {
                habbo.getClient().sendResponse(new QuestionFinishedComposer(room.getRoomWordQuizManager().getNoVotes(), room.getRoomWordQuizManager().getYesVotes()));
            }
        }
    }
}
