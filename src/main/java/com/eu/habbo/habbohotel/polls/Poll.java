package com.eu.habbo.habbohotel.polls;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

@Getter
public class Poll {

    private  final int id;
    private  final String title;
    private  final String thanksMessage;
    private  final String badgeReward;
    @Setter
    private  int lastQuestionId;

    private final ArrayList<PollQuestion> questions;

    public Poll(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.title = set.getString("title");
        this.thanksMessage = set.getString("thanks_message");
        this.badgeReward = set.getString("reward_badge");
        this.questions = new ArrayList<>();
    }

    public ArrayList<PollQuestion> getQuestions() {
        return this.questions;
    }

    public PollQuestion getQuestion(int id) {
        for (PollQuestion q : this.questions) {
            if (q.getId() == id) {
                return q;
            }
        }

        return null;
    }

    public void addQuestion(PollQuestion question) {
        this.questions.add(question);

        Collections.sort(this.questions);
    }
}
