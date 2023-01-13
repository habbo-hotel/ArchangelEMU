package com.eu.habbo.database;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.ConfigurationManager;
import com.zaxxer.hikari.HikariDataSource;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class Database {
    private HikariDataSource dataSource;
    private DatabasePool databasePool;

    public Database(ConfigurationManager config) {
        long millis = System.currentTimeMillis();

        boolean SQLException = false;

        try {
            this.databasePool = new DatabasePool();
            if (!this.databasePool.getStoragePooling(config)) {
                log.info("Failed to connect to the database. Please check config.ini and make sure the MySQL process is running. Shutting down...");
                SQLException = true;
                return;
            }
            this.dataSource = this.databasePool.getDatabase();
        } catch (Exception e) {
            SQLException = true;
            log.error("Failed to connect to your database.", e);
        } finally {
            if (SQLException) {
                Emulator.prepareShutdown();
            }
        }

        log.info("Database -> Connected! ({} MS)", System.currentTimeMillis() - millis);
    }

    public void dispose() {
        if (this.databasePool != null) {
            this.databasePool.getDatabase().close();
        }

        this.dataSource.close();
    }

    public HikariDataSource getDataSource() {
        return this.dataSource;
    }

    public DatabasePool getDatabasePool() {
        return this.databasePool;
    }

    public static PreparedStatement preparedStatementWithParams(Connection connection, String query, THashMap<String, Object> queryParams) throws SQLException {
        THashMap<Integer, Object> params = new THashMap<>();
        THashSet<String> quotedParams = new THashSet<>();

        for (String key : queryParams.keySet()) {
            quotedParams.add(Pattern.quote(key));
        }

        String regex = "(" + String.join("|", quotedParams) + ")";

        Matcher m = Pattern.compile(regex).matcher(query);

        int i = 1;

        while (m.find()) {
            try {
                params.put(i, queryParams.get(m.group(1)));
                i++;
            } catch (Exception ignored) {
            }
        }

        PreparedStatement statement = connection.prepareStatement(query.replaceAll(regex, "?"));

        for (Map.Entry<Integer, Object> set : params.entrySet()) {
            if (set.getValue().getClass() == String.class) {
                statement.setString(set.getKey(), (String) set.getValue());
            } else if (set.getValue().getClass() == Integer.class) {
                statement.setInt(set.getKey(), (Integer) set.getValue());
            } else if (set.getValue().getClass() == Double.class) {
                statement.setDouble(set.getKey(), (Double) set.getValue());
            } else if (set.getValue().getClass() == Float.class) {
                statement.setFloat(set.getKey(), (Float) set.getValue());
            } else if (set.getValue().getClass() == Long.class) {
                statement.setLong(set.getKey(), (Long) set.getValue());
            } else {
                statement.setObject(set.getKey(), set.getValue());
            }
        }

        return statement;
    }

    public static final class Achievements {
        public static final String TABLE_NAME = "achievements";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String CATEGORY = "category";
        public static final String LEVEL = "level";
        public static final String REWARD_AMOUNT = "reward_amount";
        public static final String REWARD_TYPE = "reward_type";
        public static final String POINTS = "points";
        public static final String PROGRESS_NEEDED = "progress_needed";
        public static final String VISIBLE = "visible";
    }

    public static final class Achievement_Talents {
        public static final String TABLE_NAME = "achievements_talents";

        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String LEVEL = "level";
        public static final String ACHIEVEMENT_IDS = "achievement_ids";
        public static final String ACHIEVEMENT_LEVELS = "achievement_levels";
        public static final String REWARD_FURNI = "reward_furni";
        public static final String REWARD_PERKS = "reward_perks";
        public static final String REWARD_BADGES = "reward_badges";
    }

    public static final class Bans {
        public static final String TABLE_NAME = "bans";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String IP = "ip";
        public static final String MACHINE_ID = "machine_id";
        public static final String USER_STAFF_ID = "user_staff_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String BAN_EXPIRE = "ban_expire";
        public static final String BAN_REASON = "ban_reason";
        public static final String TYPE = "type";
        public static final String CFH_TOPIC = "cfh_topic";
    }

    public static final class Bots {
        public static final String TABLE_NAME = "bots";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String ROOM_ID = "room_id";
        public static final String NAME = "name";
        public static final String MOTTO = "motto";
        public static final String FIGURE = "figure";
        public static final String GENDER = "gender";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String Z = "z";
        public static final String ROT = "rot";
        public static final String CHAT_LINES = "chat_lines";
        public static final String CHAT_AUTO = "chat_auto";
        public static final String CHAT_RANDOM = "chat_random";
        public static final String CHAT_DELAY = "chat_delay";
        public static final String DANCE = "dance";
        public static final String FREEROM = "freeroam";
        public static final String TYPE = "type";
        public static final String EFFECT = "effect";
        public static final String BUBBLE_ID = "bubble_id";
    }

    public static final class Bot_Serves {
        public static final String TABLE_NAME = "bot_serves";

        public static final String KEYS = "keys";
        public static final String ITEM = "item";
    }

    public static final class Calender_Campaigns {
        public static final String TABLE_NAME = "calender_campaigns";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String IMAGE = "image";
        public static final String START_TIMESTAMP = "start_timestamp";
        public static final String TOTAL_DAYS = "total_days";
        public static final String LOCK_EXPIRED = "lock_expired";
        public static final String ENABLED = "enabled";
    }

    public static final class Calender_Rewards {
        public static final String TABLE_NAME = "calendar_rewards";

        public static final String ID = "id";
        public static final String CAMPAIGN_ID = "campaign_id";
        public static final String PRODUCT_NAME = "product_name";
        public static final String CUSTOM_IMAGE = "custom_image";
        public static final String CREDITS = "credits";
        public static final String PIXELS = "pixels";
        public static final String POINTS = "points";
        public static final String POINTS_TYPE = "points_type";
        public static final String BADGE = "badge";
        public static final String ITEM_ID = "item_id";
        public static final String SUBSCRIPTION_TYPE = "subscription_type";
        public static final String SUBSCRIPTION_DAYS = "subscription_days";
    }

    public static final class Calender_Rewards_Claimed {
        public static final String TABLE_NAME = "calendar_rewards_claimed";

        public static final String USER_ID = "user_id";
        public static final String CAMPAIGN_ID = "campaign_id";
        public static final String DAY = "day";
        public static final String REWARD_ID = "reward_id";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Camera_Web {
        public static final String TABLE_NAME = "camera_web";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String ROOM_ID = "room_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String URL = "url";
    }

    public static final class Catalog_Clothing {
        public static final String TABLE_NAME = "catalog_clothing";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String SETID = "setid";

    }

    public static final class Catalog_Club_Offers {
        public static final String TABLE_NAME = "catalog_club_offers";

        public static final String ID = "id";
        public static final String ENABLED = "enabled";
        public static final String NAME = "name";
        public static final String DAYS = "days";
        public static final String CREDITS = "credits";
        public static final String POINTS = "points";
        public static final String POINTS_TYPE = "points_type";
        public static final String TYPE = "type";
        public static final String DEAL = "deal";
        public static final String GIFTABLE = "giftable";

    }

    public static final class Catalog_Featured_Pages {
        public static final String TABLE_NAME = "catalog_featured_pages";

        public static final String SLOT_ID = "slot_id";
        public static final String IMAGE = "image";
        public static final String CAPTION = "caption";
        public static final String TYPE = "type";
        public static final String EXPIRE_TIMESTAMP = "expire_timestamp";
        public static final String PAGE_NAME = "page_name";
        public static final String PAGE_ID = "page_id";
        public static final String PRODUCT_NAME = "product_name";
    }

    public static final class Catalog_Items {
        public static final String TABLE_NAME = "catalog_items";

        public static final String ID = "id";
        public static final String ITEM_IDS = "item_ids";
        public static final String PAGE_ID = "page_id";
        public static final String CATALOG_NAME = "catalog_name";
        public static final String COST_CREDITS = "cost_credits";
        public static final String COST_POINTS = "cost_points";
        public static final String POINTS_TYPE = "points_type";
        public static final String AMOUNT = "amount";
        public static final String LIMITED_STACK = "limited_stack";
        public static final String LIMITED_SELLS = "limited_sells";
        public static final String ORDER_NUMBER = "order_number";
        public static final String OFFER_ID = "offer_id";
        public static final String SONG_ID = "song_id";
        public static final String EXTRADATA = "extradata";
        public static final String HAVE_OFFER = "have_offer";
        public static final String CLUB_ONLY = "club_only";
    }

    public static final class Catalog_Items_Bc {
        public static final String TABLE_NAME = "catalog_items_bc";

        public static final String ID = "id";
        public static final String ITEM_IDS = "item_ids";
        public static final String PAGE_ID = "page_id";
        public static final String CATALOG_NAME = "catalog_name";
        public static final String ORDER_NUMBER = "order_number";
        public static final String EXTRADATA = "extradata";
    }

    public static final class Catalog_Items_Limited {
        public static final String TABLE_NAME = "catalog_items_limited";

        public static final String CATALOG_ITEM_ID = "catalog_item_id";
        public static final String NUMBER = "number";
        public static final String USER_ID = "user_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String ITEM_ID = "item_id";
    }

    public static final class Catalog_Pages {
        public static final String TABLE_NAME = "catalog_pages";

        public static final String ID = "id";
        public static final String PARENT_ID = "parent_id";
        public static final String CAPTION_SAVE = "caption_save";
        public static final String CAPTION = "caption";
        public static final String PAGE_LAYOUT = "page_layout";
        public static final String ICON_COLOR = "icon_color";
        public static final String ICON_IMAGE = "icon_image";
        public static final String MIN_RANK = "min_rank";
        public static final String ORDER_NUM = "order_num";
        public static final String VISIBLE = "visible";
        public static final String ENABLED = "enabled";
        public static final String CLUB_ONLY = "club_only";
        public static final String VIP_ONLY = "vip_only";
        public static final String PAGE_HEADLINE = "page_headline";
        public static final String PAGE_TEASER = "page_teaser";
        public static final String PAGE_SPECIAL = "page_special";
        public static final String PAGE_TEXT1 = "page_text1";
        public static final String PAGE_TEXT2 = "page_text2";
        public static final String PAGE_TEXT_DETAILS = "page_text_details";
        public static final String PAGE_TEXT_TEASER = "page_text_teaser";
        public static final String ROOM_ID = "room_id";
        public static final String INCLUDES = "includes";
    }

    public static final class Catalog_Pages_Bc {
        public static final String TABLE_NAME = "catalog_pages_bc";

        public static final String ID = "id";
        public static final String PARENT_ID = "parent_id";
        public static final String CAPTION = "caption";
        public static final String PAGE_LAYOUT = "page_layout";
        public static final String ICON_COLOR = "icon_color";
        public static final String ICON_IMAGE = "icon_image";
        public static final String ORDER_NUM = "order_num";
        public static final String VISIBLE = "visible";
        public static final String ENABLED = "enabled";
        public static final String PAGE_HEADLINE = "page_headline";
        public static final String PAGE_TEASER = "page_teaser";
        public static final String PAGE_SPECIAL = "page_special";
        public static final String PAGE_TEXT1 = "page_text1";
        public static final String PAGE_TEXT2 = "page_text2";
        public static final String PAGE_TEXT_DETAILS = "page_text_details";
        public static final String PAGE_TEXT_TEASER = "page_text_teaser";
    }

    public static final class Catalog_Target_Offers {
        public static final String TABLE_NAME = "catalog_pages_bc";

        public static final String ID = "id";
        public static final String OFFER_CODE = "offer_code";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String IMAGE = "image";
        public static final String ICON = "icon";
        public static final String END_TIMESTAMP = "end_timestamp";
        public static final String CREDITS = "credits";
        public static final String POINTS = "points";
        public static final String POINTS_TYPE = "points_type";
        public static final String PURCHASE_LIMIT = "purchase_limit";
        public static final String CATALOG_ITEM = "catalog_item";
        public static final String VARS = "vars";

    }

    public static final class Chatlogs_Private {
        public static final String TABLE_NAME = "chatlogs_private";

        public static final String ID = "id";
        public static final String USER_FROM_ID = "user_from_id";
        public static final String USER_TO_ID = "user_to_id";
        public static final String MESSAGE = "message";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Chatlogs_Room {
        public static final String TABLE_NAME = "chatlogs_room";

        public static final String ROOM_ID = "room_id";
        public static final String USER_FROM_ID = "user_from_id";
        public static final String USER_TO_ID = "user_to_id";
        public static final String MESSAGE = "message";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Commandlogs {
        public static final String TABLE_NAME = "commandlogs";

        public static final String USER_ID = "user_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String COMMAND = "command";
        public static final String PARAMS = "params";
        public static final String SUCCES = "succes";
    }

    public static final class Crafting_Altars_Recipes {
        public static final String TABLE_NAME = "crafting_altars_recipes";

        public static final String ALTAR_ID = "altar_id";
        public static final String RECIPE_ID = "recipe_id";
    }

    public static final class Crafting_Recipes {
        public static final String TABLE_NAME = "crafting_recipes";

        public static final String ID = "id";
        public static final String PRODUCT_NAME = "product_name";
        public static final String REWARD = "reward";
        public static final String ENABLED = "enabled";
        public static final String ACHIEVEMENT = "achievement";
        public static final String SECRET = "secret";
        public static final String LIMITED = "limited";
        public static final String REMAINING = "remaining";
    }

    public static final class Crafting_Recipes_Ingredients {
        public static final String TABLE_NAME = "crafting_recipes_ingredients";

        public static final String RECIPE_ID = "recipe_id";
        public static final String ITEM_ID = "item_id";
        public static final String AMOUNT = "amount";
    }

    public static final class Emulator_Errors {
        public static final String TABLE_NAME = "emulator_errors";

        public static final String ID = "id";
        public static final String TIMESTAMP = "timestamp";
        public static final String VERSION = "version";
        public static final String BUILD_HASH = "build_hash";
        public static final String TYPE = "type";
        public static final String STACKTRACE = "stacktrace";
    }

    public static final class Emulator_Settings {
        public static final String TABLE_NAME = "emulator_settings";

        public static final String KEY = "key";
        public static final String VALUE = "value";
    }

    public static final class Emulator_Texts {
        public static final String TABLE_NAME = "emulator_texts";

        public static final String KEY = "key";
        public static final String VALUE = "value";
    }

    public static final class Gift_Wrappers {
        public static final String TABLE_NAME = "gift_wrappers";

        public static final String ID = "id";
        public static final String SPRITE_ID = "sprite_id";
        public static final String ITEM_ID = "item_id";
        public static final String TYPE = "type";
    }

    public static final class Groups_Items {
        public static final String TABLE_NAME = "groups_items";

        public static final String TYPE = "type";
        public static final String ID = "id";
        public static final String FIRSTVALUE = "firstvalue";
        public static final String SECONDVALUE = "secondvalue";
        public static final String ENABLED = "enabled";
    }

    public static final class Guilds {
        public static final String TABLE_NAME = "guilds";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String ROOM_ID = "room_id";
        public static final String STATE = "state";
        public static final String RIGHTS = "rights";
        public static final String COLOR_ONE = "color_one";
        public static final String COLOR_TWO = "color_two";
        public static final String BADGE = "badge";
        public static final String DATE_CREATED = "date_created";
        public static final String FORUM = "forum";
        public static final String READ_FORUM = "read_forum";
        public static final String POST_MESSAGES = "post_messages";
        public static final String POST_THREADS = "post_threads";
        public static final String MOD_FORUM = "mod_forum";
    }

    public static final class Guilds_Elements {
        public static final String TABLE_NAME = "guilds_elements";

        public static final String ID = "id";
        public static final String FIRSTVALUE = "firstvalue";
        public static final String SECONDVALUE = "secondvalue";
        public static final String TYPE = "type";
        public static final String ENABLED = "enabled";
    }

    public static final class Guilds_Forums_Comments {
        public static final String TABLE_NAME = "guilds_forums_comments";

        public static final String ID = "id";
        public static final String THREAD_ID = "thread_id";
        public static final String USER_ID = "user_id";
        public static final String MESSAGE = "message";
        public static final String CREATED_AT = "created_at";
        public static final String STATE = "state";
        public static final String ADMIN_ID = "admin_id";
    }

    public static final class Guilds_Forums_Threads {
        public static final String TABLE_NAME = "guilds_forums_threads";

        public static final String ID = "id";
        public static final String GUILD_ID = "guild_id";
        public static final String OPENER_ID = "opener_id";
        public static final String SUBJECT = "subject";
        public static final String POSTS_COUNT = "posts_count";
        public static final String CREATED_AT = "created_at";
        public static final String UPDATED_AT = "updated_at";
        public static final String STATE = "state";
        public static final String PINNED = "pinned";
        public static final String LOCKED = "locked";
        public static final String ADMIN_ID = "admin_id";
    }

    public static final class Guilds_Members {
        public static final String TABLE_NAME = "guilds_members";

        public static final String ID = "id";
        public static final String GUILD_ID = "guild_id";
        public static final String USER_ID = "user_id";
        public static final String LEVEL_ID = "level_id";
        public static final String MEMBER_SINCE = "member_since";
    }

    public static final class Guild_Forum_Views {
        public static final String TABLE_NAME = "guild_forum_views";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String GUILD_ID = "guild_id";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Hotelview_News {
        public static final String TABLE_NAME = "hotelview_news";

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String TEXT = "text";
        public static final String BUTTON_TEXT = "button_text";
        public static final String BUTTON_TYPE = "button_type";
        public static final String BUTTON_LINK = "button_link";
        public static final String IMAGE = "image";
    }

    public static final class Items {
        public static final String TABLE_NAME = "items";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String ROOM_ID = "room_id";
        public static final String ITEM_ID = "item_id";
        public static final String WALL_POS = "wall_pos";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String Z = "z";
        public static final String ROT = "rot";
        public static final String EXTRA_DATA = "extra_data";
        public static final String WIRED_DATA = "wired_data";
        public static final String LIMITED_DATA = "limited_data";
        public static final String GUILD_ID = "guild_id";
    }

    public static final class Items_Base {
        public static final String TABLE_NAME = "items_base";

        public static final String ID = "id";
        public static final String SPRITE_ID = "sprite_id";
        public static final String PUBLIC_NAME = "public_name";
        public static final String ITEM_NAME = "item_name";
        public static final String TYPE = "type";
        public static final String WIDTH = "width";
        public static final String LENGTH = "length";
        public static final String STACK_HEIGHT = "stack_height";
        public static final String ALLOW_STACK = "allow_stack";
        public static final String ALLOW_SIT = "allow_sit";
        public static final String ALLOW_LAY = "allow_lay";
        public static final String ALLOW_WALK = "allow_walk";
        public static final String ALLOW_GIFT = "allow_gift";
        public static final String ALLOW_TRADE = "allow_trade";
        public static final String ALLOW_RECYCLE = "allow_recycle";
        public static final String ALLOW_MARKETPLACE_SELL = "allow_marketplace_sell";
        public static final String ALLOW_INVENTORY_STACK = "allow_inventory_stack";
        public static final String INTERACTION_TYPE = "interaction_type";
        public static final String INTERACTION_MODES_COUNT = "interaction_modes_count";
        public static final String VENDING_IDS = "vending_ids";
        public static final String MULTIHEIGHT = "multiheight";
        public static final String CUSTOMPARAMS = "customparams";
        public static final String EFFECT_ID_MALE = "effect_id_male";
        public static final String EFFECT_ID_FEMALE = "effect_id_female";
        public static final String CLOTHING_ON_WALK = "clothing_on_walk";
    }

    public static final class Items_Crackable {
        public static final String TABLE_NAME = "items_crackable";

        public static final String ITEM_ID = "item_id";
        public static final String ITEM_NAME = "item_name";
        public static final String COUNT = "count";
        public static final String PRIZES = "prizes";
        public static final String ACHIEVEMENT_TICK = "achievement_tick";
        public static final String ACHIEVEMENT_CRACKED = "achievement_cracked";
        public static final String REQUIRED_EFFECT = "required_effect";
        public static final String SUBSCRIPTION_DURATION = "subscription_duration";
        public static final String SUBSCRIPTION_TYPE = "subscription_type";
    }

    public static final class Items_Highscore_Data {
        public static final String TABLE_NAME = "items_highscore_data";

        public static final String ID = "id";
        public static final String ITEM_ID = "item_id";
        public static final String USER_IDS = "user_ids";
        public static final String SCORE = "score";
        public static final String IS_WIN = "is_win";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Items_Hoppers {
        public static final String TABLE_NAME = "items_hoppers";

        public static final String ITEM_ID = "item_id";
        public static final String BASE_ITEM = "base_item";
    }

    public static final class Items_Presents {
        public static final String TABLE_NAME = "items_presents";

        public static final String ITEM_ID = "item_id";
        public static final String BASE_ITEM_REWARD = "base_item_reward";
    }

    public static final class Items_Teleports {
        public static final String TABLE_NAME = "items_teleports";

        public static final String TELEPORT_ONE_ID = "teleport_one_id";
        public static final String TELEPORT_TWO_ID = "teleport_two_id";
    }

    public static final class Logs_Hc_Payday {
        public static final String TABLE_NAME = "logs_hc_payday";

        public static final String ID = "id";
        public static final String TIMESTAMP = "timestamp";
        public static final String USER_ID = "user_id";
        public static final String HC_STREAK = "hc_streak";
        public static final String TOTAL_COINS_SPENT = "total_coins_spent";
        public static final String REWARD_COINS_SPENT = "reward_coins_spent";
        public static final String REWARD_STREAK = "reward_streak";
        public static final String TOTAL_PAYOUT = "total_payout";
        public static final String CURRENCY = "currency";
        public static final String CLAIMED = "claimed";
    }

    public static final class Logs_Shop_Purchases {
        public static final String TABLE_NAME = "logs_shop_purchases";

        public static final String ID = "id";
        public static final String TIMESTAMP = "timestamp";
        public static final String USER_ID = "user_id";
        public static final String CATALOG_ITEM_ID = "catalog_item_id";
        public static final String ITEM_IDS = "item_ids";
        public static final String CATALOG_NAME = "catalog_name";
        public static final String COST_CREDITS = "cost_credits";
        public static final String COST_POINTS = "cost_points";
        public static final String POINTS_TYPE = "points_type";
        public static final String AMOUNT = "amount";
    }

    public static final class Marketplace_Items {
        public static final String TABLE_NAME = "marketplace_items";

        public static final String ID = "id";
        public static final String ITEM_ID = "item_id";
        public static final String USER_ID = "user_id";
        public static final String PRICE = "price";
        public static final String TIMESTAMP = "timestamp";
        public static final String SOLD_TIMESTAMP = "sold_timestamp";
        public static final String STATE = "state";
    }

    public static final class Messenger_Categories {
        public static final String TABLE_NAME = "messenger_categories";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String USER_ID = "user_id";
    }

    public static final class Messenger_Friendrequests {
        public static final String TABLE_NAME = "messenger_friendrequests";

        public static final String ID = "id";
        public static final String USER_TO_ID = "user_to_id";
        public static final String USER_FROM_ID = "user_from_id";
    }

    public static final class Messenger_Friendships {
        public static final String TABLE_NAME = "messenger_friendships";

        public static final String ID = "id";
        public static final String USER_ONE_ID = "user_one_id";
        public static final String USER_TWO_ID = "user_two_id";
        public static final String RELATION = "relation";
        public static final String FRIENDS_SINCE = "friends_since";
        public static final String CATEGORY = "category";
    }

    public static final class Messenger_Offline {
        public static final String TABLE_NAME = "messenger_offline";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String USER_FROM_ID = "user_from_id";
        public static final String MESSAGE = "message";
        public static final String SENDED_ON = "sended_on";
    }

    public static final class Namechange_Log {
        public static final String TABLE_NAME = "namechange_log";

        public static final String USER_ID = "user_id";
        public static final String OLD_NAME = "old_name";
        public static final String NEW_NAME = "new_name";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Navigator_Filter {
        public static final String TABLE_NAME = "navigator_filter";

        public static final String KEY = "key";
        public static final String FIELD = "field";
        public static final String COMPARE = "compare";
        public static final String DATABASE_QUERY = "database_query";
    }

    public static final class Navigator_Flatcats {
        public static final String TABLE_NAME = "navigator_flatcats";

        public static final String ID = "id";
        public static final String MIN_RANK = "min_rank";
        public static final String CAPTION_SAVE = "caption_save";
        public static final String CAPTION = "caption";
        public static final String CAN_TRADE = "can_trade";
        public static final String MAX_USER_COUNT = "max_user_count";
        public static final String PUBLIC = "public";
        public static final String LIST_TYPE = "list_type";
        public static final String ORDER_NUM = "order_num";
    }

    public static final class Navigator_Publiccats {
        public static final String TABLE_NAME = "navigator_publiccats";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String IMAGE = "image";
        public static final String VISIBLE = "visible";
        public static final String ORDER_NUM = "order_num";
    }

    public static final class Navigator_Publics {
        public static final String TABLE_NAME = "navigator_publics";

        public static final String PUBLIC_CAT_ID = "public_cat_id";
        public static final String ROOM_ID = "room_id";
        public static final String VISIBLE = "visible";
    }

    public static final class Nux_Gifts {
        public static final String TABLE_NAME = "nux_gifts";

        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String VALUE = "value";
        public static final String IMAGE = "image";
    }

    public static final class Old_Guilds_Forums {
        public static final String TABLE_NAME = "old_guilds_forums";

        public static final String ID = "id";
        public static final String GUILD_ID = "guild_id";
        public static final String USER_ID = "user_id";
        public static final String SUBJECT = "subject";
        public static final String MESSAGE = "message";
        public static final String STATE = "state";
        public static final String ADMIN_ID = "admin_id";
        public static final String PINNED = "pinned";
        public static final String LOCKED = "locked";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Old_Guilds_Forums_Comments {
        public static final String TABLE_NAME = "old_guilds_forums_comments";

        public static final String ID = "id";
        public static final String THREAD_ID = "thread_id";
        public static final String USER_ID = "user_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String MESSAGE = "message";
        public static final String STATE = "state";
        public static final String ADMIN_ID = "admin_id";
    }

    public static final class Permissions {
        public static final String TABLE_NAME = "permissions";

        public static final String ID = "id";
        public static final String RANK_NAME = "rank_name";
        public static final String BADGE = "badge";
        public static final String LEVEL = "level";
        public static final String ROOM_EFFECT = "room_effect";
        public static final String LOG_COMMANDS = "log_commands";
        public static final String PREFIX = "prefix";
        public static final String PREFIX_COLOR = "prefix_color";
        public static final String CMD_ABOUT = "cmd_about";
        public static final String CMD_ALERT = "cmd_alert";
        public static final String CMD_ALLOW_TRADING = "cmd_allow_trading";
        public static final String CMD_BADGE = "cmd_badge";
        public static final String CMD_BAN = "cmd_ban";
        public static final String CMD_BLOCKALERT = "cmd_blockalert";
        public static final String CMD_BOTS = "cmd_bots";
        public static final String CMD_BUNDLE = "cmd_bundle";
        public static final String CMD_CALENDAR = "cmd_calendar";
        public static final String CMD_CHANGENAME = "cmd_changename";
        public static final String CMD_CHATCOLOR = "cmd_chatcolor";
        public static final String CMD_COMMANDS = "cmd_commands";
        public static final String CMD_CONNECT_CAMERA = "cmd_connect_camera";
        public static final String CMD_CONTROL = "cmd_control";
        public static final String CMD_COORDS = "cmd_coords";
        public static final String CMD_CREDITS = "cmd_credits";
        public static final String CMD_SUBSCRIPTION = "cmd_subscription";
        public static final String CMD_DANCEALL = "cmd_danceall";
        public static final String CMD_DIAGONAL = "cmd_diagonal";
        public static final String CMD_DISCONNECT = "cmd_disconnect";
        public static final String CMD_DUCKETS = "cmd_duckets";
        public static final String CMD_EJECTALL = "cmd_ejectall";
        public static final String CMD_EMPTY = "cmd_empty";
        public static final String CMD_EMPTY_BOTS = "cmd_empty_bots";
        public static final String CMD_EMPTY_PETS = "cmd_empty_pets";
        public static final String CMD_ENABLE = "cmd_enable";
        public static final String CMD_EVENT = "cmd_event";
        public static final String CMD_FACELESS = "cmd_faceless";
        public static final String CMD_FASTWALK = "cmd_fastwalk";
        public static final String CMD_FILTERWORD = "cmd_filterword";
        public static final String CMD_FREEZE = "cmd_freeze";
        public static final String CMD_FREEZE_BOTS = "cmd_freeze_bots";
        public static final String CMD_GIFT = "cmd_gift";
        public static final String CMD_GIVE_RANK = "cmd_give_rank";
        public static final String CMD_HA = "cmd_ha";
        public static final String ACC_CAN_STALK = "acc_can_stalk";
        public static final String CMD_HAL = "cmd_hal";
        public static final String CMD_INVISIBLE = "cmd_invisible";
        public static final String CMD_IP_BAN = "cmd_ip_ban";
        public static final String CMD_MACHINE_BAN = "cmd_machine_ban";
        public static final String CMD_HAND_ITEM = "cmd_hand_item";
        public static final String CMD_HAPPYHOUR = "cmd_happyhour";
        public static final String CMD_HIDEWIRED = "cmd_hidewired";
        public static final String CMD_KICKALL = "cmd_kickall";
        public static final String CMD_SOFTKICK = "cmd_softkick";
        public static final String CMD_MASSBADGE = "cmd_massbadge";
        public static final String CMD_ROOMBADGE = "cmd_roombadge";
        public static final String CMD_MASSCREDITS = "cmd_masscredits";
        public static final String CMD_MASSDUCKETS = "cmd_massduckets";
        public static final String CMD_MASSGIFT = "cmd_massgift";
        public static final String CMD_MASSPOINTS = "cmd_masspoints";
        public static final String CMD_MOONWALK = "cmd_moonwalk";
        public static final String CMD_MIMIC = "cmd_mimic";
        public static final String CMD_MULTI = "cmd_multi";
        public static final String CMD_MUTE = "cmd_mute";
        public static final String CMD_PET_INFO = "cmd_pet_info";
        public static final String CMD_PICKALL = "cmd_pickall";
        public static final String CMD_PLUGINS = "cmd_plugins";
        public static final String CMD_POINTS = "cmd_points";
        public static final String CMD_PROMOTE_OFFER = "cmd_promote_offer";
        public static final String CMD_PULL = "cmd_pull";
        public static final String CMD_PUSH = "cmd_push";
        public static final String CMD_REDEEM = "cmd_redeem";
        public static final String CMD_RELOAD_ROOM = "cmd_reload_room";
        public static final String CMD_ROOMALERT = "cmd_roomalert";
        public static final String CMD_ROOMCREDITS = "cmd_roomcredits";
        public static final String CMD_ROOMEFFECT = "cmd_roomeffect";
        public static final String CMD_ROOMGIFT = "cmd_roomgift";
        public static final String CMD_ROOMITEM = "cmd_roomitem";
        public static final String CMD_ROOMMUTE = "cmd_roommute";
        public static final String CMD_ROOMPIXELS = "cmd_roompixels";
        public static final String CMD_ROOMPOINTS = "cmd_roompoints";
        public static final String CMD_SAY = "cmd_say";
        public static final String CMD_SAY_ALL = "cmd_say_all";
        public static final String CMD_SETMAX = "cmd_setmax";
        public static final String CMD_SET_POLL = "cmd_set_poll";
        public static final String CMD_SETPUBLIC = "cmd_setpublic";
        public static final String CMD_SETSPEED = "cmd_setspeed";
        public static final String CMD_SHOUT = "cmd_shout";
        public static final String CMD_SHOUT_ALL = "cmd_shout_all";
        public static final String CMD_SHUTDOWN = "cmd_shutdown";
        public static final String CMD_SITDOWN = "cmd_sitdown";
        public static final String CMD_STAFFALERT = "cmd_staffalert";
        public static final String CMD_STAFFONLINE = "cmd_staffonline";
        public static final String CMD_SUMMON = "cmd_summon";
        public static final String CMD_SUMMONRANK = "cmd_summonrank";
        public static final String CMD_SUPER_BAN = "cmd_super_ban";
        public static final String CMD_STALK = "cmd_stalk";
        public static final String CMD_SUPERPULL = "cmd_superpull";
        public static final String CMD_TAKE_BADGE = "cmd_take_badge";
        public static final String CMD_TALK = "cmd_talk";
        public static final String CMD_TELEPORT = "cmd_teleport";
        public static final String CMD_TRASH = "cmd_trash";
        public static final String CMD_TRANSFORM = "cmd_transform";
        public static final String CMD_UNBAN = "cmd_unban";
        public static final String CMD_UNLOAD = "cmd_unload";
        public static final String CMD_UNMUTE = "cmd_unmute";
        public static final String CMD_UPDATE_ACHIEVEMENTS = "cmd_update_achievements";
        public static final String CMD_UPDATE_BOTS = "cmd_update_bots";
        public static final String CMD_UPDATE_CATALOGUE = "cmd_update_catalogue";
        public static final String CMD_UPDATE_CONFIG = "cmd_update_config";
        public static final String CMD_UPDATE_GUILDPARTS = "cmd_update_guildparts";
    }

    public static final class Pet_Actions {
        public static final String TABLE_NAME = "pet_actions";

        public static final String ID = "id";
        public static final String PET_TYPE = "pet_type";
        public static final String PET_NAME = "pet_name";
        public static final String OFFSPRING_TYPE = "offspring_type";
        public static final String HAPPY_ACTIONS = "happy_actions";
        public static final String TIRED_ACTIONS = "tired_actions";
        public static final String RANDOM_ACTIONS = "random_actions";
        public static final String CAN_SWIM = "can_swim";
    }

    public static final class Pet_Breeding {
        public static final String TABLE_NAME = "pet_breeding";

        public static final String PET_ID = "pet_id";
        public static final String OFFSPRING_ID = "offspring_id";
    }

    public static final class Pet_Breeding_Races {
        public static final String TABLE_NAME = "pet_breeding_races";

        public static final String PET_TYPE = "pet_type";
        public static final String RARITY_LEVEL = "rarity_level";
        public static final String BREED = "breed";
    }

    public static final class Pet_Breeds {
        public static final String TABLE_NAME = "pet_breeds";

        public static final String RACE = "race";
        public static final String COLOR_ONE = "color_one";
        public static final String COLOR_TWO = "color_two";
        public static final String HAS_COLOR_ONE = "has_color_one";
        public static final String HAS_COLOR_TWO = "has_color_two";
    }

    public static final class Pet_Commands {
        public static final String TABLE_NAME = "pet_commands";

        public static final String PET_ID = "pet_id";
        public static final String COMMAND_ID = "command_id";
    }

    public static final class Pet_Commands_Data {
        public static final String TABLE_NAME = "pet_commands_data";

        public static final String COMMAND_ID = "command_id";
        public static final String TEXT = "text";
        public static final String REQUIRED_LEVEL = "required_level";
        public static final String REWARD_XP = "reward_xp";
        public static final String COST_HAPPINESS = "cost_happiness";
        public static final String COST_ENERGY = "cost_energy";
    }

    public static final class Pet_Drinks {
        public static final String TABLE_NAME = "pet_drinks";

        public static final String PET_ID = "pet_id";
        public static final String ITEM_ID = "item_id";
    }

    public static final class Pet_Foods {
        public static final String TABLE_NAME = "pet_foods";

        public static final String PET_ID = "pet_id";
        public static final String ITEM_ID = "item_id";
    }

    public static final class Pet_Items {
        public static final String TABLE_NAME = "pet_items";

        public static final String PET_ID = "pet_id";
        public static final String ITEM_ID = "item_id";
    }

    public static final class Pet_Vocals {
        public static final String TABLE_NAME = "pet_vocals";

        public static final String PET_ID = "pet_id";
        public static final String TYPE = "type";
        public static final String MESSAGE = "message";
    }

    public static final class Polls {
        public static final String TABLE_NAME = "polls";

        public static final String ID = "id";
        public static final String TITLE = "title";
        public static final String THANKS_MESSAGE = "thanks_message";
        public static final String REWARD_BADGE = "reward_badge";
    }

    public static final class Polls_Answers {
        public static final String TABLE_NAME = "polls_answers";

        public static final String POLL_ID = "poll_id";
        public static final String USER_ID = "user_id";
        public static final String QUESTION_ID = "question_id";
        public static final String ANSWER = "answer";
    }

    public static final class Polls_Questions {
        public static final String TABLE_NAME = "polls_questions";

        public static final String ID = "id";
        public static final String PARENT_ID = "parent_id";
        public static final String POLL_ID = "poll_id";
        public static final String ORDER = "order";
        public static final String QUESTION = "question";
        public static final String TYPE = "type";
        public static final String MIN_SELECTIONS = "min_selections";
        public static final String OPTIONS = "options";
    }

    public static final class Recycler_Prizes {
        public static final String TABLE_NAME = "recycler_prizes";

        public static final String RARITY = "rarity";
        public static final String ITEM_ID = "item_id";
    }

    public static final class Rooms {
        public static final String TABLE_NAME = "rooms";

        public static final String ID = "id";
        public static final String OWNER_ID = "owner_id";
        public static final String OWNER_NAME = "owner_name";
        public static final String NAME = "name";
        public static final String DESCRIPTION = "description";
        public static final String MODEL = "model";
        public static final String PASSWORD = "password";
        public static final String STATE = "state";
        public static final String USERS = "users";
        public static final String USERS_MAX = "users_max";
        public static final String GUILD_ID = "guild_id";
        public static final String CATEGORY = "category";
        public static final String SCORE = "score";
        public static final String PAPER_FLOOR = "paper_floor";
        public static final String PAPER_WALL = "paper_wall";
        public static final String PAPER_LANDSCAPE = "paper_landscape";
        public static final String THICKNESS_WALL = "thickness_wall";
        public static final String WALL_HEIGHT = "wall_height";
        public static final String THICKNESS_FLOOR = "thickness_floor";
        public static final String MOODLIGHT_DATA = "moodlight_data";
        public static final String TAGS = "tags";
        public static final String IS_PUBLIC = "is_public";
        public static final String IS_STAFF_PICKED = "is_staff_picked";
        public static final String ALLOW_OTHER_PETS = "allow_other_pets";
        public static final String ALLOW_OTHER_PETS_EAT = "allow_other_pets_eat";
        public static final String ALLOW_WALKTHROUGH = "allow_walkthrough";
        public static final String ALLOW_HIDEWALL = "allow_hidewall";
        public static final String CHAT_MODE = "chat_mode";
        public static final String CHAT_WEIGHT = "chat_weight";
        public static final String CHAT_SPEED = "chat_speed";
        public static final String CHAT_HEARING_DISTANCE = "chat_hearing_distance";
        public static final String CHAT_PROTECTION = "chat_protection";
        public static final String OVERRIDE_MODEL = "override_model";
        public static final String WHO_CAN_MUTE = "who_can_mute";
        public static final String WHO_CAN_KICK = "who_can_kick";
        public static final String WHO_CAN_BAN = "who_can_ban";
        public static final String POLL_ID = "poll_id";
        public static final String ROLLER_SPEED = "roller_speed";
        public static final String PROMOTED = "promoted";
        public static final String TRADE_MODE = "trade_mode";
        public static final String MOVE_DIAGONALLY = "move_diagonally";
        public static final String JUKEBOX_ACTIVE = "jukebox_active";
        public static final String HIDEWIRED = "hidewired";
        public static final String IS_FORSALE = "is_forsale";
    }

    public static final class Room_Bans {
        public static final String TABLE_NAME = "room_bans";

        public static final String ROOM_ID = "room_id";
        public static final String USER_ID = "user_id";
        public static final String ENDS = "ends";
    }

    public static final class Room_Enter_Log {
        public static final String TABLE_NAME = "room_enter_log";

        public static final String ROOM_ID = "room_id";
        public static final String USER_ID = "user_id";
        public static final String TIMESTAMP = "timestamp";
        public static final String EXIT_TIMESTAMP = "exit_timestamp";
    }

    public static final class Room_Game_Scores {
        public static final String TABLE_NAME = "room_game_scores";

        public static final String ROOM_ID = "room_id";
        public static final String GAME_START_TIMESTAMP = "game_start_timestamp";
        public static final String GAME_NAME = "game_name";
        public static final String USER_ID = "user_id";
        public static final String TEAM_ID = "team_id";
        public static final String SCORE = "score";
        public static final String TEAM_SCORE = "team_score";
    }

    public static final class Room_Models {
        public static final String TABLE_NAME = "room_models";

        public static final String NAME = "name";
        public static final String DOOR_X = "door_x";
        public static final String DOOR_Y = "door_y";
        public static final String DOOR_DIR = "door_dir";
        public static final String HEIGHTMAP = "heightmap";
        public static final String PUBLIC_ITEMS = "public_items";
        public static final String CLUB_ONLY = "club_only";
    }

    public static final class Room_Models_Custom {
        public static final String TABLE_NAME = "room_models_custom";

        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String DOOR_X = "door_x";
        public static final String DOOR_Y = "door_y";
        public static final String DOOR_DIR = "door_dir";
        public static final String HEIGHTMAP = "heightmap";
    }

    public static final class Room_Mutes {
        public static final String TABLE_NAME = "room_mutes";

        public static final String ROOM_ID = "room_id";
        public static final String USER_ID = "user_id";
        public static final String ENDS = "ends";
    }

    public static final class Room_Promotions {
        public static final String TABLE_NAME = "room_promotions";

        public static final String ROOM_ID = "room_id";
        public static final String TITLE = "title";
        public static final String DESCRIPTION = "description";
        public static final String END_TIMESTAMP = "end_timestamp";
        public static final String START_TIMESTAMP = "start_timestamp";
        public static final String CATEGORY = "category";
    }

    public static final class Room_Rights {
        public static final String TABLE_NAME = "room_rights";

        public static final String ROOM_ID = "room_id";
        public static final String USER_ID = "user_id";
    }

    public static final class Room_Trade_Log {
        public static final String TABLE_NAME = "room_trade_log";

        public static final String ID = "id";
        public static final String USER_ONE_ID = "user_one_id";
        public static final String USER_TWO_ID = "user_two_id";
        public static final String USER_ONE_IP = "user_one_ip";
        public static final String USER_TWO_IP = "user_two_ip";
        public static final String TIMESTAMP = "timestamp";
        public static final String USER_ONE_ITEM_COUNT = "user_one_item_count";
        public static final String USER_TWO_ITEM_COUNT = "user_two_item_count";
    }

    public static final class Room_Trade_Log_Items {
        public static final String TABLE_NAME = "room_trade_log_items";

        public static final String ID = "id";
        public static final String ITEM_ID = "item_id";
        public static final String USER_ID = "user_id";
    }

    public static final class Room_Trax {
        public static final String TABLE_NAME = "room_trax";

        public static final String ROOM_ID = "room_id";
        public static final String TRAX_ITEM_ID = "trax_item_id";
    }

    public static final class Room_Trax_Playlist {
        public static final String TABLE_NAME = "room_trax_playlist";

        public static final String ROOM_ID = "room_id";
        public static final String ITEM_ID = "item_id";
    }

    public static final class Room_Votes {
        public static final String TABLE_NAME = "room_votes";

        public static final String USER_ID = "user_id";
        public static final String ROOM_ID = "room_id";
    }

    public static final class Room_Wordfilter {
        public static final String TABLE_NAME = "room_wordfilter";

        public static final String ROOM_ID = "room_id";
        public static final String WORD = "word";
    }

    public static final class Sanctions {
        public static final String TABLE_NAME = "sanctions";

        public static final String ID = "id";
        public static final String HABBO_ID = "habbo_id";
        public static final String SANCTION_LEVEL = "sanction_level";
        public static final String PROBATION_TIMESTAMP = "probation_timestamp";
        public static final String REASON = "reason";
        public static final String TRADE_LOCKED_UNTIL = "trade_locked_until";
        public static final String IS_MUTED = "is_muted";
        public static final String MUTE_DURATION = "mute_duration";
    }

    public static final class Sanction_Levels {
        public static final String TABLE_NAME = "sanction_levels";

        public static final String LEVEL = "level";
        public static final String TYPE = "type";
        public static final String HOUR_LENGTH = "hour_length";
        public static final String PROBATION_DAYS = "probation_days";
    }

    public static final class Soundtracks {
        public static final String TABLE_NAME = "soundtracks";

        public static final String ID = "id";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String AUTHOR = "author";
        public static final String TRACK = "track";
        public static final String LENGTH = "length";
    }

    public static final class Special_Enables {
        public static final String TABLE_NAME = "special_enables";

        public static final String EFFECT_ID = "effect_id";
        public static final String MIN_RANK = "min_rank";
    }

    public static final class Support_Cfh_Categories {
        public static final String TABLE_NAME = "support_cfh_categories";

        public static final String ID = "id";
        public static final String NAME_INTERNAL = "name_internal";
        public static final String NAME_EXTERNAL = "name_external";
    }


    public static final class Support_Cfh_Topics {
        public static final String TABLE_NAME = "support_cfh_topics";

        public static final String ID = "id";
        public static final String CATEGORY_ID = "category_id";
        public static final String NAME_INTERNAL = "name_internal";
        public static final String NAME_EXTERNAL = "name_external";
        public static final String ACTION = "action";
        public static final String IGNORE_TARGET = "ignore_target";
        public static final String AUTO_REPLY = "auto_reply";
        public static final String DEFAULT_SANCTION = "default_sanction";
    }

    public static final class Support_Issue_Categories {
        public static final String TABLE_NAME = "support_issue_categories";

        public static final String ID = "id";
        public static final String NAME = "name";
    }

    public static final class Support_Issue_Presets {
        public static final String TABLE_NAME = "support_issue_presets";

        public static final String ID = "id";
        public static final String CATEGORY = "category";
        public static final String NAME = "name";
        public static final String MESSAGE = "message";
        public static final String REMINDER = "reminder";
        public static final String BAN_FOR = "ban_for";
        public static final String MUTE_FOR = "mute_for";
    }

    public static final class Support_Presets {
        public static final String TABLE_NAME = "support_presets";

        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String PRESET = "preset";
    }

    public static final class Support_Tickets {
        public static final String TABLE_NAME = "support_tickets";

        public static final String ID = "id";
        public static final String STATE = "state";
        public static final String TYPE = "type";
        public static final String TIMESTAMP = "timestamp";
        public static final String SCORE = "score";
        public static final String SENDER_ID = "sender_id";
        public static final String REPORTED_ID = "reported_id";
        public static final String ROOM_ID = "room_id";
        public static final String MOD_ID = "mod_id";
        public static final String ISSUE = "issue";
        public static final String CATEGORY = "category";
        public static final String GROUP_ID = "group_id";
        public static final String THREAD_ID = "thread_id";
        public static final String COMMENT_ID = "comment_id";
        public static final String PHOTO_ITEM_ID = "photo_item_id";
    }

    public static final class Trax_Playlist {
        public static final String TABLE_NAME = "trax_playlist";

        public static final String TRAX_ITEM_ID = "trax_item_id";
        public static final String ITEM_ID = "item_id";
    }

    public static final class Users {
        public static final String TABLE_NAME = "users";

        public static final String ID = "id";
        public static final String USERNAME = "username";
        public static final String REAL_NAME = "real_name";
        public static final String PASSWORD = "password";
        public static final String MAIL = "mail";
        public static final String MAIL_VERIFIED = "mail_verified";
        public static final String ACCOUNT_CREATED = "account_created";
        public static final String ACCOUNT_DAY_OF_BIRTH = "account_day_of_birth";
        public static final String LAST_LOGIN = "last_login";
        public static final String LAST_ONLINE = "last_online";
        public static final String MOTTO = "motto";
        public static final String LOOK = "look";
        public static final String GENDER = "gender";
        public static final String RANK = "rank";
        public static final String CREDITS = "credits";
        public static final String PIXELS = "pixels";
        public static final String POINTS = "points";
        public static final String ONLINE = "online";
        public static final String AUTH_TICKET = "auth_ticket";
        public static final String IP_REGISTER = "ip_register";
        public static final String IP_CURRENT = "ip_current";
        public static final String MACHINE_ID = "machine_id";
        public static final String HOME_ROOM = "home_room";
        public static final String SECRET_KEY = "secret_key";
        public static final String PINCODE = "pincode";
        public static final String EXTRA_RANK = "extra_rank";
    }

    public static final class Users_Achievements {
        public static final String TABLE_NAME = "users_achievements";

        public static final String USER_ID = "user_id";
        public static final String ACHIEVEMENT_NAME = "achievement_name";
        public static final String PROGRESS = "progress";
    }

    public static final class Users_Achievements_Queue {
        public static final String TABLE_NAME = "users_achievements_queue";

        public static final String USER_ID = "user_id";
        public static final String ACHIEVEMENT_ID = "achievement_id";
        public static final String AMOUNT = "amount";
    }

    public static final class Users_Badges {
        public static final String TABLE_NAME = "users_badges";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String SLOT_ID = "slot_id";
        public static final String BADGE_CODE = "badge_code";
    }

    public static final class Users_Clothing {
        public static final String TABLE_NAME = "users_clothing";

        public static final String USER_ID = "user_id";
        public static final String CLOTHING_ID = "clothing_id";
    }

    public static final class Users_Currency {
        public static final String TABLE_NAME = "users_currency";

        public static final String USER_ID = "user_id";
        public static final String TYPE = "type";
        public static final String AMOUNT = "amount";
    }

    public static final class Users_Effects {
        public static final String TABLE_NAME = "users_effects";

        public static final String USER_ID = "user_id";
        public static final String EFFECT = "effect";
        public static final String DURATION = "duration";
        public static final String ACTIVATION_TIMESTAMP = "activation_timestamp";
        public static final String TOTAL = "total";
    }

    public static final class Users_Favorite_Rooms {
        public static final String TABLE_NAME = "users_favorite_rooms";

        public static final String USER_ID = "user_id";
        public static final String ROOM_ID = "room_id";
    }

    public static final class Users_Ignored {
        public static final String TABLE_NAME = "users_ignored";

        public static final String USER_ID = "user_id";
        public static final String TARGET_ID = "target_id";
    }

    public static final class Users_Navigator_Settings {
        public static final String TABLE_NAME = "users_navigator_settings";

        public static final String USER_ID = "user_id";
        public static final String CAPTION = "caption";
        public static final String LIST_TYPE = "list_type";
        public static final String DISPLAY = "display";
    }

    public static final class Users_Pets {
        public static final String TABLE_NAME = "users_pets";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String ROOM_ID = "room_id";
        public static final String NAME = "name";
        public static final String RACE = "race";
        public static final String TYPE = "type";
        public static final String COLOR = "color";
        public static final String HAPPYNESS = "happyness";
        public static final String EXPERIENCE = "experience";
        public static final String ENERGY = "energy";
        public static final String HUNGER = "hunger";
        public static final String THIRST = "thirst";
        public static final String RESPECT = "respect";
        public static final String CREATED = "created";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String Z = "z";
        public static final String ROT = "rot";
        public static final String HAIR_STYLE = "hair_style";
        public static final String HAIR_COLOR = "hair_color";
        public static final String SADDLE = "saddle";
        public static final String RIDE = "ride";
        public static final String MP_TYPE = "mp_type";
        public static final String MP_COLOR = "mp_color";
        public static final String MP_NOSE = "mp_nose";
        public static final String MP_NOSE_COLOR = "mp_nose_color";
        public static final String MP_EYES = "mp_eyes";
        public static final String MP_EYES_COLOR = "mp_eyes_color";
        public static final String MP_MOUTH = "mp_mouth";
        public static final String MP_MOUTH_COLOR = "mp_mouth_color";
        public static final String MP_DEATH_TIMESTAMP = "mp_death_timestamp";
        public static final String MP_BREEDABLE = "mp_breedable";
        public static final String MP_ALLOW_BREED = "mp_allow_breed";
        public static final String GNOME_DATA = "gnome_data";
        public static final String MP_IS_DEAD = "mp_is_dead";
        public static final String SADDLE_ITEM_ID = "saddle_item_id";
    }

    public static final class Users_Recipes {
        public static final String TABLE_NAME = "users_recipes";

        public static final String USER_ID = "user_id";
        public static final String RECIPE = "recipe";
    }

    public static final class Users_Saved_Searches {
        public static final String TABLE_NAME = "users_saved_searches";

        public static final String ID = "id";
        public static final String SEARCH_CODE = "search_code";
        public static final String FILTER = "filter";
        public static final String USER_ID = "user_id";
    }

    public static final class Users_Settings {
        public static final String TABLE_NAME = "users_settings";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String CREDITS = "credits";
        public static final String ACHIEVEMENT_SCORE = "achievement_score";
        public static final String DAILY_RESPECT_POINTS = "daily_respect_points";
        public static final String DAILY_PET_RESPECT_POINTS = "daily_pet_respect_points";
        public static final String RESPECTS_GIVEN = "respects_given";
        public static final String RESPECTS_RECEIVED = "respects_received";
        public static final String GUILD_ID = "guild_id";
        public static final String CAN_CHANGE_NAME = "can_change_name";
        public static final String CAN_TRADE = "can_trade";
        public static final String IS_CITIZEN = "is_citizen";
        public static final String CITIZEN_LEVEL = "citizen_level";
        public static final String HELPER_LEVEL = "helper_level";
        public static final String TRADELOCK_AMOUNT = "tradelock_amount";
        public static final String CFH_SEND = "cfh_send";
        public static final String CFH_ABUSIVE = "cfh_abusive";
        public static final String CFH_WARNINGS = "cfh_warnings";
        public static final String CFH_BANS = "cfh_bans";
        public static final String BLOCK_FOLLOWING = "block_following";
        public static final String BLOCK_FRIENDREQUESTS = "block_friendrequests";
        public static final String BLOCK_ROOMINVITES = "block_roominvites";
        public static final String VOLUME_SYSTEM = "volume_system";
        public static final String VOLUME_FURNI = "volume_furni";
        public static final String VOLUME_TRAX = "volume_trax";
        public static final String OLD_CHAT = "old_chat";
        public static final String BLOCK_CAMERA_FOLLOW = "block_camera_follow";
        public static final String CHAT_COLOR = "chat_color";
        public static final String HOME_ROOM = "home_room";
        public static final String ONLINE_TIME = "online_time";
        public static final String TAGS = "tags";
        public static final String CLUB_EXPIRE_TIMESTAMP = "club_expire_timestamp";
        public static final String LOGIN_STREAK = "login_streak";
        public static final String RENT_SPACE_ID = "rent_space_id";
        public static final String RENT_SPACE_ENDTIME = "rent_space_endtime";
        public static final String HOF_POINTS = "hof_points";
        public static final String BLOCK_ALERTS = "block_alerts";
        public static final String TALENT_TRACK_CITIZENSHIP_LEVEL = "talent_track_citizenship_level";
        public static final String TALENT_TRACK_HELPERS_LEVEL = "talent_track_helpers_level";
        public static final String IGNORE_BOTS = "ignore_bots";
        public static final String IGNORE_PETS = "ignore_pets";
        public static final String NUX = "nux";
        public static final String MUTE_END_TIMESTAMP = "mute_end_timestamp";
        public static final String ALLOW_NAME_CHANGE = "allow_name_change";
        public static final String PERK_TRADE = "perk_trade";
        public static final String FORUMS_POST_COUNT = "forums_post_count";
        public static final String UI_FLAGS = "ui_flags";
        public static final String HAS_GOTTEN_DEFAULT_SAVED_SEARCHES = "has_gotten_default_saved_searches";
        public static final String HC_GIFTS_CLAIMED = "hc_gifts_claimed";
        public static final String LAST_HC_PAYDAY = "last_hc_payday";
        public static final String MAX_ROOMS = "max_rooms";
        public static final String MAX_FRIENDS = "max_friends";
    }

    public static final class Users_Subscriptions {
        public static final String TABLE_NAME = "users_subscriptions";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String SUBSCRIPTION_TYPE = "subscription_type";
        public static final String TIMESTAMP_START = "timestamp_start";
        public static final String DURATION = "duration";
        public static final String ACTIVE = "active";
    }

    public static final class Users_Target_Offer_Purchases {
        public static final String TABLE_NAME = "users_target_offer_purchases";

        public static final String USER_ID = "user_id";
        public static final String OFFER_ID = "offer_id";
        public static final String STATE = "state";
        public static final String AMOUNT = "amount";
        public static final String LAST_PURCHASE = "last_purchase";
    }

    public static final class Users_Wardrobe {
        public static final String TABLE_NAME = "users_wardrobe";

        public static final String ID = "id";
        public static final String USER_ID = "user_id";
        public static final String SLOT_ID = "slot_id";
        public static final String LOOK = "look";
        public static final String GENDER = "gender";
    }

    public static final class User_Window_Settings {
        public static final String TABLE_NAME = "user_window_settings";

        public static final String USER_ID = "user_id";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String OPEN_SEARCHES = "open_searches";
    }

    public static final class Vouchers {
        public static final String TABLE_NAME = "vouchers";

        public static final String ID = "id";
        public static final String CODE = "code";
        public static final String CREDITS = "credits";
        public static final String POINTS = "points";
        public static final String POINTS_TYPE = "points_type";
        public static final String CATALOG_ITEM_ID = "catalog_item_id";
        public static final String AMOUNT = "amount";
        public static final String LIMIT = "limit";
    }

    public static final class Voucher_History {
        public static final String TABLE_NAME = "voucher_history";

        public static final String ID = "id";
        public static final String VOUCHER_ID = "voucher_id";
        public static final String USER_ID = "user_id";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Wired_Rewards_Given {
        public static final String TABLE_NAME = "wired_rewards_given";

        public static final String WIRED_ITEM = "wired_item";
        public static final String USER_ID = "user_id";
        public static final String REWARD_ID = "reward_id";
        public static final String TIMESTAMP = "timestamp";
    }

    public static final class Wordfilter {
        public static final String TABLE_NAME = "wordfilter";

        public static final String KEY = "key";
        public static final String REPLACEMENT = "replacement";
        public static final String HIDE = "hide";
        public static final String REPORT = "report";
        public static final String MUTE = "mute";
    }

    public static final class Youtube_Playlists {
        public static final String TABLE_NAME = "youtube_playlists";

        public static final String ITEM_ID = "item_id";
        public static final String PLAYLIST_ID = "playlist_id";
        public static final String ORDER = "order";
    }
}
