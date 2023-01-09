package com.eu.habbo.habbohotel.messenger;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class MessengerCategory {
    @Setter
    @Getter
    private String name;
    @Getter
    private int user_id;
    @Setter
    @Getter
    private int id;

}

