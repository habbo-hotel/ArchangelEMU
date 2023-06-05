package com.eu.habbo.habbohotel.modtool;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.messenger.Message;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.rooms.RoomChatMessage;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.messages.outgoing.friends.NewConsoleMessageComposer;
import com.eu.habbo.plugin.events.users.UserTriggerWordFilterEvent;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Slf4j
public class WordFilter {

    private static final Pattern DIACRITICS_AND_FRIENDS = Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");
    //Configuration. Loaded from database & updated accordingly.
    public static boolean ENABLED_FRIENDCHAT = true;
    public static String DEFAULT_REPLACEMENT = "bobba";
    protected final THashSet<WordFilterWord> autoReportWords = new THashSet<>();
    protected final THashSet<WordFilterWord> hideMessageWords = new THashSet<>();
    protected final THashSet<WordFilterWord> words = new THashSet<>();

    public WordFilter() {
        long start = System.currentTimeMillis();
        this.reload();
        log.info("WordFilter -> Loaded! (" + (System.currentTimeMillis() - start) + " MS)");
    }

    private static String stripDiacritics(String str) {
        str = Normalizer.normalize(str, Normalizer.Form.NFD);
        str = DIACRITICS_AND_FRIENDS.matcher(str).replaceAll("");
        return str;
    }

    public synchronized void reload() {
        if (!Emulator.getConfig().getBoolean("hotel.wordfilter.enabled", true))
            return;

        this.autoReportWords.clear();
        this.hideMessageWords.clear();
        this.words.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection(); Statement statement = connection.createStatement()) {
            try (ResultSet set = statement.executeQuery("SELECT * FROM wordfilter")) {
                while (set.next()) {
                    WordFilterWord word;

                    try {
                        word = new WordFilterWord(set);
                    } catch (SQLException e) {
                        log.error("Caught SQL exception", e);
                        continue;
                    }

                    if (word.isAutoReport())
                        this.autoReportWords.add(word);
                    else if (word.isHideMessage())
                        this.hideMessageWords.add(word);

                    this.words.add(word);
                }
            }
        } catch (SQLException e) {
            log.error("Caught SQL exception", e);
        }
    }

    public String normalise(String message) {
        return DIACRITICS_AND_FRIENDS.matcher(Normalizer.normalize(StringUtils.stripAccents(message), Normalizer.Form.NFKD)
                .replaceAll("[,.;:'\"]", " ").replace("I", "l")
                .replaceAll("[^\\p{ASCII}*$]", "").replaceAll("\\p{M}", " ")
                .replaceAll("^\\p{M}*$]", "").replaceAll("[1|]", "i")
                .replace("2", "z").replace("3", "e")
                .replace("4", "a").replace("5", "s")
                .replace("8", "b").replace("0", "o")
                .replace(" ", " ").replace("$", "s")
                .replace("ß", "b").trim()).replaceAll(" ");
    }

    public boolean autoReportCheck(RoomChatMessage roomChatMessage) {
        String message = this.normalise(roomChatMessage.getMessage()).toLowerCase();

        for (WordFilterWord word : this.autoReportWords) {
            if (message.contains(word.getKey())) {
                Emulator.getGameEnvironment().getModToolManager().quickTicket(roomChatMessage.getHabbo(), "Automatic WordFilter", roomChatMessage.getMessage());

                if (Emulator.getConfig().getBoolean("notify.staff.chat.auto.report")) {
                    Emulator.getGameEnvironment().getHabboManager().sendPacketToHabbosWithPermission(new NewConsoleMessageComposer(new Message(roomChatMessage.getHabbo().getHabboInfo().getId(), 0, Emulator.getTexts().getValue("warning.auto.report").replace("%user%", roomChatMessage.getHabbo().getHabboInfo().getUsername()).replace("%word%", word.getKey()))).compose(), "acc_staff_chat");
                }
                return true;
            }
        }

        return false;
    }

    public boolean hideMessageCheck(String message) {
        message = this.normalise(message).toLowerCase();

        for (WordFilterWord word : this.hideMessageWords) {
            if (message.contains(word.getKey())) {
                return true;
            }
        }

        return false;
    }

    public String[] filter(String[] messages) {
        for (int i = 0; i < messages.length; i++) {
            messages[i] = this.filter(messages[i], null);
        }

        return messages;
    }

    public String filter(String message, Habbo habbo) {
        String filteredMessage = message;
        if (!Emulator.getConfig().getBoolean("hotel.wordfilter.enabled", true) || (habbo != null && habbo.hasRight(Permission.ACC_CHAT_NO_FILTER))) {
            return message;
        }
        if (Emulator.getConfig().getBoolean("hotel.wordfilter.normalise")) {
            filteredMessage = this.normalise(filteredMessage);
        }
        boolean foundShit = false;

        for (WordFilterWord word : this.words) {
            if (StringUtils.containsIgnoreCase(message, word.getKey())) {
                if (habbo != null) {
                    if (Emulator.getPluginManager().fireEvent(new UserTriggerWordFilterEvent(habbo, word)).isCancelled())
                        continue;
                }
                filteredMessage = message.replaceAll("(?i)" + word.getKey(), word.getReplacement());
                foundShit = true;
            }
        }

        if (!foundShit) {
            return message;
        }

        return filteredMessage;
    }

    public void filter(RoomChatMessage roomChatMessage, Habbo habbo) {
        String message = roomChatMessage.getMessage().toLowerCase();

        if (Emulator.getConfig().getBoolean("hotel.wordfilter.normalise")) {
            message = this.normalise(message);
        }

        for (WordFilterWord word : this.words) {
            if (StringUtils.containsIgnoreCase(message, word.getKey())) {
                if (habbo != null) {
                    if (Emulator.getPluginManager().fireEvent(new UserTriggerWordFilterEvent(habbo, word)).isCancelled())
                        continue;
                }

                message = message.replace(word.getKey(), word.getReplacement());
                roomChatMessage.filtered = true;
            }
        }

        if (roomChatMessage.filtered) {
            roomChatMessage.setMessage(message);
        }
    }

    public void addWord(WordFilterWord word) {
        this.words.add(word);
    }
}
