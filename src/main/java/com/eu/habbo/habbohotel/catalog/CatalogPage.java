package com.eu.habbo.habbohotel.catalog;

import com.eu.habbo.messages.ISerialize;
import com.eu.habbo.messages.ServerMessage;
import gnu.trove.TCollections;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Slf4j
public abstract class CatalogPage implements Comparable<CatalogPage>, ISerialize {

    @Getter
    protected final TIntArrayList offerIds = new TIntArrayList();
    @Getter
    protected final THashMap<Integer, CatalogPage> childPages = new THashMap<>();
    @Getter
    private final TIntObjectMap<CatalogItem> catalogItems = TCollections.synchronizedMap(new TIntObjectHashMap<>());
    @Getter
    private final ArrayList<Integer> included = new ArrayList<>();
    @Getter
    protected int id;
    @Getter
    protected int parentId;
    @Getter
    @Setter
    protected int rank;
    @Getter
    protected String caption;
    @Getter
    protected String pageName;
    @Getter
    protected int iconColor;
    @Getter
    protected int iconImage;
    @Getter
    protected int orderNum;
    @Getter
    protected boolean visible;
    @Getter
    protected boolean enabled;
    @Getter
    protected boolean clubOnly;
    @Getter
    protected String layout;
    @Getter
    protected String headerImage;
    @Getter
    protected String teaserImage;
    @Getter
    protected String specialImage;
    @Getter
    protected String textOne;
    @Getter
    protected String textTwo;
    @Getter
    protected String textDetails;
    @Getter
    protected String textTeaser;

    public CatalogPage() {
    }

    public CatalogPage(ResultSet set) throws SQLException {
        if (set == null)
            return;

        this.id = set.getInt("id");
        this.parentId = set.getInt("parent_id");
        this.rank = set.getInt("min_rank");
        this.caption = set.getString("caption");
        this.pageName = set.getString("caption_save");
        this.iconColor = set.getInt("icon_color");
        this.iconImage = set.getInt("icon_image");
        this.orderNum = set.getInt("order_num");
        this.visible = set.getBoolean("visible");
        this.enabled = set.getBoolean("enabled");
        this.clubOnly = set.getBoolean("club_only");
        this.layout = set.getString("page_layout");
        this.headerImage = set.getString("page_headline");
        this.teaserImage = set.getString("page_teaser");
        this.specialImage = set.getString("page_special");
        this.textOne = set.getString("page_text1");
        this.textTwo = set.getString("page_text2");
        this.textDetails = set.getString("page_text_details");
        this.textTeaser = set.getString("page_text_teaser");

        if (!set.getString("includes").isEmpty()) {
            for (String id : set.getString("includes").split(";")) {
                try {
                    this.included.add(Integer.parseInt(id));
                } catch (Exception e) {
                    log.error("Caught exception", e);
                    log.error("Failed to parse includes column value of (" + id + ") for catalog page (" + this.id + ")");
                }
            }
        }
    }

    public void addOfferId(int offerId) {
        this.offerIds.add(offerId);
    }

    public void addItem(CatalogItem item) {
        this.catalogItems.put(item.getId(), item);
    }

    public CatalogItem getCatalogItem(int id) {
        return this.catalogItems.get(id);
    }

    public void addChildPage(CatalogPage page) {
        this.childPages.put(page.getId(), page);

        if (page.getRank() < this.getRank()) {
            page.setRank(this.getRank());
        }
    }

    protected void appendImagesAndText(ServerMessage message) {
        message.appendInt(3);
        message.appendString(getHeaderImage());
        message.appendString(getTeaserImage());
        message.appendString(getSpecialImage());
        message.appendInt(3);
        message.appendString(getTextOne());
        message.appendString(getTextDetails());
        message.appendString(getTextTeaser());
    }

    protected void appendGuildImagesAndText(ServerMessage message) {
        message.appendInt(2);
        message.appendString(getHeaderImage());
        message.appendString(getTeaserImage());
        message.appendInt(3);
        message.appendString(getTextOne());
        message.appendString(getTextDetails());
        message.appendString(getTextTeaser());
    }

    protected void appendPetImagesAndText(ServerMessage message){
        message.appendInt(2);
        message.appendString(getHeaderImage());
        message.appendString(getTeaserImage());
        message.appendInt(4);
        message.appendString(getTextOne());
        message.appendString(getTextTwo());
        message.appendString(getTextDetails());
        message.appendString(getTextTeaser());
    }
    @Override
    public int compareTo(CatalogPage page) {
        return this.getOrderNum() - page.getOrderNum();
    }

    @Override
    public abstract void serialize(ServerMessage message);
}
