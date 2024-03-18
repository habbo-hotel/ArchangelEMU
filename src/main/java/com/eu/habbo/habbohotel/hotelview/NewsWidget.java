package com.eu.habbo.habbohotel.hotelview;

import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewsWidget {
    @Getter
    private final int id;

    @Getter
    private final String title;

    @Getter
    private final String message;

    @Getter
    private final String buttonMessage;

    @Getter
    private final int type;

    @Getter
    private final String link;

    @Getter
    private final String image;

    public NewsWidget(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.title = set.getString("title");
        this.message = set.getString("text");
        this.buttonMessage = set.getString("button_text");
        this.type = set.getString("button_type").equals("client") ? 1 : 0;
        this.link = set.getString("button_link");
        this.image = set.getString("image");
    }
}
