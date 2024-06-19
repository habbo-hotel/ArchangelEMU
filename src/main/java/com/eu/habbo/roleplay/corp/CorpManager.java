package com.eu.habbo.roleplay.corp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.roleplay.database.CorpPositionRepository;
import com.eu.habbo.roleplay.database.CorpRepository;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class CorpManager {

    private static CorpManager instance;

    public static CorpManager getInstance() {
        if (instance == null) {
            instance = new CorpManager();
        }
        return instance;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(CorpManager.class);

    private TIntObjectHashMap<Corp> corporations;

    public void createCorp(Guild guild, String tags) {
        CorpRepository.getInstance().upsertCorp(guild.getId(), tags);
        CorpPositionRepository.getInstance().upsertCorpPosition(
                guild.getId(),
                1,
                Emulator.getTexts().getValue("roleplay.corp.default_position_name"),
                Emulator.getTexts().getValue("roleplay.corp.default_position_desc"),
                0,
                "-",
                "-",
                false,
                false,
                false,
                false,
                false
        );
        CorpPosition newPosition = CorpPositionRepository.getInstance().getCorpPosition(guild.getId(), 1);
        Emulator.getGameEnvironment().getHabboManager().getHabbo(guild.getOwnerId()).getHabboRoleplayStats().setCorp(guild.getId(), newPosition.getId());
    }

    public Corp getCorpByID(int corporationID) {
        return this.corporations.get(corporationID);
    }

    public Corp getCorpsByName(String corpName) {
        int[] keys = this.corporations.keys();
        for (int key : keys) {
            Corp corp = this.corporations.get(key);
            if (corp.getGuild().getName().equalsIgnoreCase(corpName)) {
                return corp;
            }
        }
        return null;
    }

    public List<Corp> getCorpsByTag(CorpTag tag) {
        if (corporations == null || corporations.isEmpty()) {
            return Collections.emptyList();
        }
        return corporations.valueCollection().stream()
                .filter(corp -> corp.getTags() != null && corp.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    @Getter
    @Setter
    private CorpShiftManager corpShiftManager;


    private CorpManager() {
        long millis = System.currentTimeMillis();
        this.corporations = CorpRepository.getInstance().getAllCorps();
        this.corpShiftManager = CorpShiftManager.getInstance();

        this.reload();

        LOGGER.info("Corp Manager -> Loaded! (" + (System.currentTimeMillis() - millis) + " MS)");
    }
    public void reload() {
        this.corporations = CorpRepository.getInstance().getAllCorps();
    }

    public void dispose() {
        this.corporations = null;
        CorpManager.LOGGER.info("Corp Manager -> Disposed!");
    }
}