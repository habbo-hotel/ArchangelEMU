package com.eu.habbo.roleplay.police;

import com.eu.habbo.habbohotel.users.Habbo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Bounty {
    public final Habbo habbo;
    public final String crime;
}
