package com.eu.habbo.roleplay.police;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Getter
public class WantedListManager {

    private static WantedListManager instance;

    public static WantedListManager getInstance() {
        if (instance == null) {
            instance = new WantedListManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(WantedListManager.class);

    private List<Bounty> bounties;


    public Bounty getBountyByUser(int userId) {
        for (Bounty bounty : this.bounties) {
            if (bounty.getHabbo().getHabboInfo().getId() == userId) {
                return bounty;
            }
        }
        return null;
    }

    public void addBounty(Bounty bounty) {
        this.bounties.add(bounty);
    }


    public void removeBounty(Bounty bounty) {
        this.bounties.remove(bounty);
    }

    private WantedListManager() {
        long millis = System.currentTimeMillis();
        this.bounties = new ArrayList<Bounty>();
        LOGGER.info("Wanted List Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }

    public void dispose() {
        this.bounties = null;
        LOGGER.info("Wanted List Manager -> Disposed!");
    }
}