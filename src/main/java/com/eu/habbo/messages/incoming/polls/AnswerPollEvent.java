package com.eu.habbo.messages.incoming.polls;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.polls.Poll;
import com.eu.habbo.habbohotel.users.HabboBadge;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.users.BadgeReceivedComposer;
import com.eu.habbo.messages.outgoing.wired.WiredRewardResultMessageComposer;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Slf4j
public class AnswerPollEvent extends MessageHandler {
    @Override
    public void handle() {
        int pollId = this.packet.readInt();
        int questionId = this.packet.readInt();
        int count = this.packet.readInt();
        String answers = this.packet.readString();
        
        StringBuilder answer = new StringBuilder();
        for (int i = 0; i < count; i++) {
            answer.append(":").append(answers);
        }

        if(answer.length() <= 0) return;

        if (pollId == 0 && questionId <= 0) {
            this.client.getHabbo().getHabboInfo().getCurrentRoom().handleWordQuiz(this.client.getHabbo(), answer.toString());
            return;
        }

        answer = new StringBuilder(answer.substring(1));

        Poll poll = Emulator.getGameEnvironment().getPollManager().getPoll(pollId);

        if (poll != null) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); PreparedStatement statement = connection.prepareStatement("INSERT INTO polls_answers(poll_id, user_id, question_id, answer) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE answer=VALUES(answer)")) {
                statement.setInt(1, pollId);
                statement.setInt(2, this.client.getHabbo().getHabboInfo().getId());
                statement.setInt(3, questionId);
                statement.setString(4, answer.toString());
                statement.execute();
            } catch (SQLException e) {
                log.error("Caught SQL exception", e);
            }

            if (poll.getLastQuestionId() == questionId && poll.getBadgeReward().length() > 0) {
                if (!this.client.getHabbo().getInventory().getBadgesComponent().hasBadge(poll.getBadgeReward())) {
                    HabboBadge badge = new HabboBadge(0, poll.getBadgeReward(), 0, this.client.getHabbo());
                    Emulator.getThreading().run(badge);
                    this.client.getHabbo().getInventory().getBadgesComponent().addBadge(badge);
                    this.client.sendResponse(new BadgeReceivedComposer(badge));
                    this.client.sendResponse(new WiredRewardResultMessageComposer(WiredRewardResultMessageComposer.REWARD_RECEIVED_BADGE));
                } else {
                    this.client.sendResponse(new WiredRewardResultMessageComposer(WiredRewardResultMessageComposer.REWARD_ALREADY_RECEIVED));
                }
            }
        }
    }
}
