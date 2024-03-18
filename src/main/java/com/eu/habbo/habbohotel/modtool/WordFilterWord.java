package com.eu.habbo.habbohotel.modtool;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
@Getter
public class WordFilterWord {
    private  final String key;
    private  final String replacement;
    private  final boolean hideMessage;
    private  final boolean autoReport;
    private  final int muteTime;

    public WordFilterWord(ResultSet set) throws SQLException {
        this.key = set.getString("key");
        this.replacement = set.getString("replacement");
        this.hideMessage = set.getInt("hide") == 1;
        this.autoReport = set.getInt("report") == 1;
        this.muteTime = set.getInt("mute");
    }

    public WordFilterWord(String key, String replacement) {
        this.key = key;
        this.replacement = replacement;
        this.hideMessage = false;
        this.autoReport = false;
        this.muteTime = 0;
    }
}
