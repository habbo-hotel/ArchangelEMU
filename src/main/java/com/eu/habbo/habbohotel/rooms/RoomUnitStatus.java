package com.eu.habbo.habbohotel.rooms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoomUnitStatus {
    MOVE("mv", true),

    // SIT
    SIT_IN("sit-in"),
    SIT("sit", true),
    SIT_OUT("sit-out"),

    // lay or down
    LAY_IN("lay-in"),
    LAY("lay", true),
    LAY_OUT("lay-out"),

    // eat
    EAT_IN("eat-in"),
    EAT("eat"),
    EAT_OUT("eat-out"),

    // basic controls and gestures
    FLAT_CONTROL("flatctrl"),
    SIGN("sign"),
    GESTURE("gst"),
    WAVE("wav"), // this doesnt exist?
    TRADING("trd"), // this doesnt exist?
    KICK("kck"), // kicks leg, horse atleast
    SPEAK("spk"), // speak animation
    SRP("srp"), // SURPRISED very funny with monkey
    SRP_IN("srp-in"),
    SWIM("swm"), // supposedly should happen when monkey reaches water | essentially dip is useless

    // sleep
    SLEEP_IN("slp-in"),
    SLEEP("slp", true), // sleep
    SLEEP_OUT("slp-out"),

    // play dead
    DEAD_IN("ded-in"),
    DEAD("ded", true), // play dead
    DEAD_OUT("ded-out"),

    // jump
    JUMP_IN("jmp-in"),
    JUMP("jmp", true), // jump
    JUMP_OUT("jmp-out"),

    // play with toy
    PLAY_IN("pla-in"),
    PLAY("pla", true), // play
    PLAY_OUT("pla-out"),

    // specific commands
    DIP("dip"), // walks towards random water
    BEG("beg", true), // begs for food
    WAG_TAIL("wag"), // self-explanatory
    DANCE("dan"), // dances, for example spider and monkey
    AMS("ams"), // SOME WEIRD PUPPET SHIT PROBABLY SPEAK FOR MONKEY
    TURN("trn"), // turns
    SPIN("spn"), // spinny spin
    CROAK("crk"), // speak but for frog
    FLAT("flt"), // flat, falls on stomach (spider n shit)
    FLAT_IN("flt-in"), // flat-in? dunno dc
    BOUNCE("bnc"), // bounces once
    RELAX("rlx"),
    WINGS("wng", true), // spreads wings dragon
    FLAME("flm"), // breathe fire
    RINGOFFIRE("rng"), // ring of fire for dragon related to toy
    SWING("swg"), // same as roll but less energic, related to Dragon tree toy.
    HANG("hg"), // hang, related to Dragon tree toy. just hangs under the branch
    ROLL("rll"), // roll, related to Dragon tree toy. rolls around the branch

    // monsterplant shit i aint touching that
    RIP("rip"),
    GROW("grw"),
    GROW_1("grw1"),
    GROW_2("grw2"),
    GROW_3("grw3"),
    GROW_4("grw4"),
    GROW_5("grw5"),
    GROW_6("grw6"),
    GROW_7("grw7");

    private final String key;
    private final boolean removeWhenWalking;

    RoomUnitStatus(String key) {
        this.key = key;
        this.removeWhenWalking = false;
    }


    public static RoomUnitStatus fromString(String key) {
        for (RoomUnitStatus status : values()) {
            if (status.key.equalsIgnoreCase(key)) {
                return status;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.key;
    }
}
