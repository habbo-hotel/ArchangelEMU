package com.eu.habbo.messages;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.incoming.achievements.GetAchievementsEvent;
import com.eu.habbo.messages.incoming.achievements.RequestAchievementConfigurationEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorAlertEvent;
import com.eu.habbo.messages.incoming.ambassadors.FollowFriendEvent;
import com.eu.habbo.messages.incoming.camera.*;
import com.eu.habbo.messages.incoming.campaign.OpenCampaignCalendarDoorAsStaffEvent;
import com.eu.habbo.messages.incoming.campaign.OpenCampaignCalendarDoorEvent;
import com.eu.habbo.messages.incoming.catalog.*;
import com.eu.habbo.messages.incoming.catalog.marketplace.*;
import com.eu.habbo.messages.incoming.catalog.recycler.GetRecyclerPrizesEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.GetRecyclerStatusEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.PresentOpenEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RecycleItemsEvent;
import com.eu.habbo.messages.incoming.crafting.*;
import com.eu.habbo.messages.incoming.floorplaneditor.GetOccupiedTilesEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.GetRoomEntryTileEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.UpdateFloorPropertiesEvent;
import com.eu.habbo.messages.incoming.friends.*;
import com.eu.habbo.messages.incoming.gamecenter.*;
import com.eu.habbo.messages.incoming.guardians.ChatReviewGuideDecidesOnOfferEvent;
import com.eu.habbo.messages.incoming.guardians.ChatReviewGuideDetachedEvent;
import com.eu.habbo.messages.incoming.guardians.ChatReviewGuideVoteEvent;
import com.eu.habbo.messages.incoming.guides.*;
import com.eu.habbo.messages.incoming.guilds.*;
import com.eu.habbo.messages.incoming.guilds.forums.*;
import com.eu.habbo.messages.incoming.handshake.*;
import com.eu.habbo.messages.incoming.helper.GetCfhStatusEvent;
import com.eu.habbo.messages.incoming.helper.GetTalentTrackEvent;
import com.eu.habbo.messages.incoming.hotelview.*;
import com.eu.habbo.messages.incoming.inventory.*;
import com.eu.habbo.messages.incoming.modtool.*;
import com.eu.habbo.messages.incoming.navigator.*;
import com.eu.habbo.messages.incoming.polls.AnswerPollEvent;
import com.eu.habbo.messages.incoming.polls.PollRejectEvent;
import com.eu.habbo.messages.incoming.polls.PollStartEvent;
import com.eu.habbo.messages.incoming.rooms.*;
import com.eu.habbo.messages.incoming.rooms.bots.CommandBotEvent;
import com.eu.habbo.messages.incoming.rooms.bots.GetBotCommandConfigurationDataEvent;
import com.eu.habbo.messages.incoming.rooms.bots.PlaceBotEvent;
import com.eu.habbo.messages.incoming.rooms.bots.RemoveBotFromFlatEvent;
import com.eu.habbo.messages.incoming.rooms.items.*;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.*;
import com.eu.habbo.messages.incoming.rooms.items.lovelock.FriendFurniConfirmLockEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentableSpaceCancelRentEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentableSpaceRentEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.ControlYoutubeDisplayPlaybackEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.GetYoutubeDisplayStatusEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.SetYoutubeDisplayPlaylistEvent;
import com.eu.habbo.messages.incoming.rooms.pets.*;
import com.eu.habbo.messages.incoming.rooms.promotions.EditEventEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.GetRoomAdPurchaseInfoEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.PurchaseRoomAdEvent;
import com.eu.habbo.messages.incoming.rooms.users.*;
import com.eu.habbo.messages.incoming.trading.*;
import com.eu.habbo.messages.incoming.unknown.GetResolutionAchievementsEvent;
import com.eu.habbo.messages.incoming.users.*;
import com.eu.habbo.messages.incoming.wired.ApplySnapshotEvent;
import com.eu.habbo.messages.incoming.wired.UpdateActionEvent;
import com.eu.habbo.messages.incoming.wired.UpdateConditionEvent;
import com.eu.habbo.messages.incoming.wired.UpdateTriggerEvent;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import gnu.trove.map.hash.THashMap;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PacketManager {

    private static final List<Integer> logList = new ArrayList<>();
    public static boolean DEBUG_SHOW_PACKETS = false;
    public static boolean MULTI_THREADED_PACKET_HANDLING = false;
    private final THashMap<Integer, Class<? extends MessageHandler>> incoming;
    private final THashMap<Integer, List<ICallable>> callables;
    private final PacketNames names;

    public PacketManager() throws Exception {
        this.incoming = new THashMap<>();
        this.callables = new THashMap<>();
        this.names = new PacketNames();
        this.names.initialize();

        this.registerHandshake();
        this.registerCatalog();
        this.registerEvent();
        this.registerFriends();
        this.registerNavigator();
        this.registerUsers();
        this.registerHotelview();
        this.registerInventory();
        this.registerRooms();
        this.registerPolls();
        this.registerUnknown();
        this.registerModTool();
        this.registerTrading();
        this.registerGuilds();
        this.registerPets();
        this.registerWired();
        this.registerAchievements();
        this.registerFloorPlanEditor();
        this.registerAmbassadors();
        this.registerGuides();
        this.registerCrafting();
        this.registerCamera();
        this.registerGameCenter();
    }

    public PacketNames getNames() {
        return names;
    }

    @EventHandler
    public static void onConfigurationUpdated(EmulatorConfigUpdatedEvent event) {
        logList.clear();

        for (String s : Emulator.getConfig().getValue("debug.show.headers").split(";")) {
            try {
                logList.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {

            }
        }
    }

    public void registerHandler(Integer header, Class<? extends MessageHandler> handler) throws Exception {
        if (header < 0)
            return;

        if (this.incoming.containsKey(header)) {
            throw new Exception("Header already registered. Failed to register " + handler.getName() + " with header " + header);
        }

        this.incoming.putIfAbsent(header, handler);
    }

    public void registerCallable(Integer header, ICallable callable) {
        this.callables.putIfAbsent(header, new ArrayList<>());
        this.callables.get(header).add(callable);
    }

    public void unregisterCallables(Integer header, ICallable callable) {
        if (this.callables.containsKey(header)) {
            this.callables.get(header).remove(callable);
        }
    }

    public void unregisterCallables(Integer header) {
        if (this.callables.containsKey(header)) {
            this.callables.clear();
        }
    }

    public void handlePacket(GameClient client, ClientMessage packet) {
        if (client == null || Emulator.isShuttingDown)
            return;

        try {
            if (this.isRegistered(packet.getMessageId())) {
                Class<? extends MessageHandler> handlerClass = this.incoming.get(packet.getMessageId());

                if (handlerClass == null) throw new Exception("Unknown message " + packet.getMessageId());

                if (client.getHabbo() == null && !handlerClass.isAnnotationPresent(NoAuthMessage.class)) {
                    if (DEBUG_SHOW_PACKETS) {
                        log.warn("Client packet {} requires an authenticated session.", packet.getMessageId());
                    }

                    return;
                }

                final MessageHandler handler = handlerClass.getDeclaredConstructor().newInstance();

                if (handler.getRatelimit() > 0) {
                    if (client.messageTimestamps.containsKey(handlerClass) && System.currentTimeMillis() - client.messageTimestamps.get(handlerClass) < handler.getRatelimit()) {
                        if (PacketManager.DEBUG_SHOW_PACKETS) {
                            log.warn("Client packet {} was ratelimited.", packet.getMessageId());
                        }

                        return;
                    } else {
                        client.messageTimestamps.put(handlerClass, System.currentTimeMillis());
                    }
                }

                if (logList.contains(packet.getMessageId()) && client.getHabbo() != null) {
                    log.info("User {} sent packet {} with body {}", client.getHabbo().getHabboInfo().getUsername(), packet.getMessageId(), packet.getMessageBody());
                }

                handler.client = client;
                handler.packet = packet;

                if (this.callables.containsKey(packet.getMessageId())) {
                    for (ICallable callable : this.callables.get(packet.getMessageId())) {
                        callable.call(handler);
                    }
                }

                if (!handler.isCancelled) {
                    handler.handle();
                }
            }
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }

    boolean isRegistered(int header) {
        return this.incoming.containsKey(header);
    }

    private void registerAmbassadors() throws Exception {
        this.registerHandler(Incoming.ambassadorAlertEvent, AmbassadorAlertEvent.class);
        this.registerHandler(Incoming.followFriendEvent, FollowFriendEvent.class);
    }

    private void registerCatalog() throws Exception {
        this.registerHandler(Incoming.getRecyclerPrizesEvent, GetRecyclerPrizesEvent.class);
        this.registerHandler(Incoming.getBundleDiscountRulesetEvent, GetBundleDiscountRulesetEvent.class);
        this.registerHandler(Incoming.getGiftWrappingConfigurationEvent, GetGiftWrappingConfigurationEvent.class);
        this.registerHandler(Incoming.getMarketplaceConfigurationEvent, GetMarketplaceConfigurationEvent.class);
        this.registerHandler(Incoming.getCatalogIndexEvent, GetCatalogIndexEvent.class);
        this.registerHandler(Incoming.buildersClubQueryFurniCountEvent, BuildersClubQueryFurniCountEvent.class);
        this.registerHandler(Incoming.getCatalogPageEvent, GetCatalogPageEvent.class);
        this.registerHandler(Incoming.purchaseFromCatalogAsGiftEvent, PurchaseFromCatalogAsGiftEvent.class);
        this.registerHandler(Incoming.purchaseFromCatalogEvent, PurchaseFromCatalogEvent.class);
        this.registerHandler(Incoming.redeemVoucherEvent, RedeemVoucherEvent.class);
        this.registerHandler(Incoming.getRecyclerStatusEvent, GetRecyclerStatusEvent.class);
        this.registerHandler(Incoming.recycleItemsEvent, RecycleItemsEvent.class);
        this.registerHandler(Incoming.presentOpenEvent, PresentOpenEvent.class);
        this.registerHandler(Incoming.getMarketplaceOwnOffersEvent, GetMarketplaceOwnOffersEvent.class);
        this.registerHandler(Incoming.cancelMarketplaceOfferEvent, CancelMarketplaceOfferEvent.class);
        this.registerHandler(Incoming.getMarketplaceOffersEvent, GetMarketplaceOffersEvent.class);
        this.registerHandler(Incoming.getMarketplaceItemStatsEvent, GetMarketplaceItemStatsEvent.class);
        this.registerHandler(Incoming.buyMarketplaceOfferEvent, BuyMarketplaceOfferEvent.class);
        this.registerHandler(Incoming.getMarketplaceCanMakeOfferEvent, GetMarketplaceCanMakeOfferEvent.class);
        this.registerHandler(Incoming.makeOfferEvent, MakeOfferEvent.class);
        this.registerHandler(Incoming.redeemMarketplaceOfferCreditsEvent, RedeemMarketplaceOfferCreditsEvent.class);
        this.registerHandler(Incoming.getSellablePetPalettesEvent, GetSellablePetPalettesEvent.class);
        this.registerHandler(Incoming.approveNameEvent, ApproveNameEvent.class);
        this.registerHandler(Incoming.getClubOffersEvent, GetClubOffersEvent.class);
        this.registerHandler(Incoming.getClubGiftInfo, GetClubGiftInfo.class);
        this.registerHandler(Incoming.getProductOfferEvent, GetProductOfferEvent.class);
        this.registerHandler(Incoming.purchaseTargetedOfferEvent, PurchaseTargetedOfferEvent.class);
        this.registerHandler(Incoming.setTargetedOfferStateEvent, SetTargetedOfferStateEvent.class);
        this.registerHandler(Incoming.selectClubGiftEvent, SelectClubGiftEvent.class);
        this.registerHandler(Incoming.scrGetKickbackInfoEvent, ScrGetKickbackInfoEvent.class);
        this.registerHandler(Incoming.buildersClubPlaceWallItemEvent, BuildersClubPlaceWallItemEvent.class);
        this.registerHandler(Incoming.purchaseVipMembershipExtensionEvent, PurchaseVipMembershipExtensionEvent.class);
    }

    private void registerEvent() throws Exception {
        this.registerHandler(Incoming.openCampaignCalendarDoorAsStaffEvent, OpenCampaignCalendarDoorAsStaffEvent.class);
        this.registerHandler(Incoming.openCampaignCalendarDoorEvent, OpenCampaignCalendarDoorEvent.class);
    }

    private void registerHandshake() throws Exception {
        this.registerHandler(Incoming.clientHelloEvent, ClientHelloEvent.class);
        this.registerHandler(Incoming.initDiffieHandshakeEvent, InitDiffieHandshakeEvent.class);
        this.registerHandler(Incoming.completeDiffieHandshakeEvent, CompleteDiffieHandshakeEvent.class);
        this.registerHandler(Incoming.sSOTicketEvent, SSOTicketEvent.class);
        this.registerHandler(Incoming.uniqueIDEvent, UniqueIDEvent.class);
        this.registerHandler(Incoming.getIgnoredUsersEvent, GetIgnoredUsersEvent.class);
        this.registerHandler(Incoming.latencyPingRequestEvent, LatencyPingRequestEvent.class);
    }

    private void registerFriends() throws Exception {
        this.registerHandler(Incoming.getMOTDEvent, GetMOTDEvent.class);
        this.registerHandler(Incoming.setRelationshipStatusEvent, SetRelationshipStatusEvent.class);
        this.registerHandler(Incoming.removeFriendEvent, RemoveFriendEvent.class);
        this.registerHandler(Incoming.habboSearchEvent, HabboSearchEvent.class);
        this.registerHandler(Incoming.requestFriendEvent, RequestFriendEvent.class);
        this.registerHandler(Incoming.acceptFriendEvent, AcceptFriendEvent.class);
        this.registerHandler(Incoming.declineFriendEvent, DeclineFriendEvent.class);
        this.registerHandler(Incoming.sendMsgEvent, SendMsgEvent.class);
        this.registerHandler(Incoming.getFriendRequestsEvent, GetFriendRequestsEvent.class);
        this.registerHandler(Incoming.visitUserEvent, VisitUserEvent.class);
        this.registerHandler(Incoming.messengerInitEvent, MessengerInitEvent.class);
        this.registerHandler(Incoming.findNewFriendsEvent, FindNewFriendsEvent.class);
        this.registerHandler(Incoming.sendRoomInviteEvent, SendRoomInviteEvent.class);
    }

    private void registerUsers() throws Exception {
        this.registerHandler(Incoming.infoRetrieveEvent, InfoRetrieveEvent.class);
        this.registerHandler(Incoming.getCreditsInfoEvent, GetCreditsInfoEvent.class);
        this.registerHandler(Incoming.scrGetUserInfoEvent, ScrGetUserInfoEvent.class);
        this.registerHandler(Incoming.getSoundSettingsEvent, GetSoundSettingsEvent.class);
        this.registerHandler(Incoming.getTalentTrackLevelEvent, GetTalentTrackLevelEvent.class);
        this.registerHandler(Incoming.getExtendedProfileEvent, GetExtendedProfileEvent.class);
        this.registerHandler(Incoming.getRelationshipStatusInfoEvent, GetRelationshipStatusInfoEvent.class);
        this.registerHandler(Incoming.getWardrobeEvent, GetWardrobeEvent.class);
        this.registerHandler(Incoming.saveWardrobeOutfitEvent, SaveWardrobeOutfitEvent.class);
        this.registerHandler(Incoming.changeMottoEvent, ChangeMottoEvent.class);
        this.registerHandler(Incoming.updateFigureDataEvent, UpdateFigureDataEvent.class);
        this.registerHandler(Incoming.setActivatedBadgesEvent, SetActivatedBadgesEvent.class);
        this.registerHandler(Incoming.getSelectedBadgesEvent, GetSelectedBadgesEvent.class);
        this.registerHandler(Incoming.setSoundSettingsEvent, SetSoundSettingsEvent.class);
        this.registerHandler(Incoming.setRoomCameraPreferencesEvent, SetRoomCameraPreferencesEvent.class);
        this.registerHandler(Incoming.setIgnoreRoomInvitesEvent, SetIgnoreRoomInvitesEvent.class);
        this.registerHandler(Incoming.setChatPreferencesEvent, SetChatPreferencesEvent.class);
        this.registerHandler(Incoming.avatarEffectActivatedEvent, AvatarEffectActivatedEvent.class);
        this.registerHandler(Incoming.avatarEffectSelectedEvent, AvatarEffectSelectedEvent.class);
        this.registerHandler(Incoming.eventLogEvent, EventLogEvent.class);
        this.registerHandler(Incoming.newUserExperienceScriptProceedEvent, NewUserExperienceScriptProceedEvent.class);
        this.registerHandler(Incoming.newUserExperienceGetGiftsEvent, NewUserExperienceGetGiftsEvent.class);
        this.registerHandler(Incoming.checkUserNameEvent, CheckUserNameEvent.class);
        this.registerHandler(Incoming.changeUserNameEvent, ChangeUserNameEvent.class);
        this.registerHandler(Incoming.setChatStylePreferenceEvent, SetChatStylePreferenceEvent.class);
        this.registerHandler(Incoming.updateUIFlagsEvent, UpdateUIFlagsEvent.class);
    }

    private void registerNavigator() throws Exception {
        this.registerHandler(Incoming.getUserFlatCatsEvent, GetUserFlatCatsEvent.class);
        this.registerHandler(Incoming.popularRoomsSearchEvent, PopularRoomsSearchEvent.class);
        this.registerHandler(Incoming.roomsWithHighestScoreSearchEvent, RoomsWithHighestScoreSearchEvent.class);
        this.registerHandler(Incoming.myRoomsSearchEvent, MyRoomsSearchEvent.class);
        this.registerHandler(Incoming.canCreateRoomEvent, CanCreateRoomEvent.class);
        this.registerHandler(Incoming.getUnreadForumsCountEvent, GetUnreadForumsCountEvent.class);
        this.registerHandler(Incoming.createFlatEvent, CreateFlatEvent.class);
        this.registerHandler(Incoming.getPopularRoomTagsEvent, GetPopularRoomTagsEvent.class);
        this.registerHandler(Incoming.searchRoomsByTagEvent, SearchRoomsByTagEvent.class);
        this.registerHandler(Incoming.roomTextSearchEvent, RoomTextSearchEvent.class);
        this.registerHandler(Incoming.roomsWhereMyFriendsAreSearchEvent, RoomsWhereMyFriendsAreSearchEvent.class);
        this.registerHandler(Incoming.myFriendsRoomsSearchEvent, MyFriendsRoomsSearchEvent.class);
        this.registerHandler(Incoming.myRoomRightsSearchEvent, MyRoomRightsSearchEvent.class);
        this.registerHandler(Incoming.myGuildBasesSearchEvent, MyGuildBasesSearchEvent.class);
        this.registerHandler(Incoming.myFavouriteRoomsSearchEvent, MyFavouriteRoomsSearchEvent.class);
        this.registerHandler(Incoming.myRoomHistorySearchEvent, MyRoomHistorySearchEvent.class);
        this.registerHandler(Incoming.newNavigatorInitEvent, NewNavigatorInitEvent.class);
        this.registerHandler(Incoming.newNavigatorSearchEvent, NewNavigatorSearchEvent.class);
        this.registerHandler(Incoming.forwardToSomeRoomEvent, ForwardToSomeRoomEvent.class);
        this.registerHandler(Incoming.getUserEventCatsEvent, GetUserEventCatsEvent.class);
        this.registerHandler(Incoming.setNewNavigatorWindowPreferencesEvent, SetNewNavigatorWindowPreferencesEvent.class);
        this.registerHandler(Incoming.deleteRoomEvent, DeleteRoomEvent.class);
        this.registerHandler(Incoming.navigatorSetSearchCodeViewModeEvent, NavigatorSetSearchCodeViewModeEvent.class);
        this.registerHandler(Incoming.navigatorAddCollapsedCategoryEvent, NavigatorAddCollapsedCategoryEvent.class);
        this.registerHandler(Incoming.navigatorRemoveCollapsedCategoryEvent, NavigatorRemoveCollapsedCategoryEvent.class);
        this.registerHandler(Incoming.navigatorAddSavedSearchEvent, NavigatorAddSavedSearchEvent.class);
        this.registerHandler(Incoming.navigatorDeleteSavedSearchEvent, NavigatorDeleteSavedSearchEvent.class);
        this.registerHandler(Incoming.toggleStaffPickEvent, ToggleStaffPickEvent.class);
    }

    private void registerHotelview() throws Exception {
        this.registerHandler(Incoming.quitEvent, QuitEvent.class);
        this.registerHandler(Incoming.getBonusRareInfoEvent, GetBonusRareInfoEvent.class);
        this.registerHandler(Incoming.getPromoArticlesEvent, GetPromoArticlesEvent.class);
        this.registerHandler(Incoming.getCurrentTimingCodeEvent, GetCurrentTimingCodeEvent.class);
        this.registerHandler(Incoming.hotelViewRequestBadgeRewardEvent, HotelViewRequestBadgeRewardEvent.class);
        this.registerHandler(Incoming.hotelViewClaimBadgeRewardEvent, HotelViewClaimBadgeRewardEvent.class);
        this.registerHandler(Incoming.getLimitedOfferAppearingNextEvent, GetLimitedOfferAppearingNextEvent.class);
        this.registerHandler(Incoming.hotelViewRequestSecondsUntilEvent, HotelViewRequestSecondsUntilEvent.class);
    }

    private void registerInventory() throws Exception {
        this.registerHandler(Incoming.getBadgesEvent, GetBadgesEvent.class);
        this.registerHandler(Incoming.getBotInventoryEvent, GetBotInventoryEvent.class);
        this.registerHandler(Incoming.requestFurniInventoryEvent, RequestFurniInventoryWhenNotInRoomEvent.class);
        this.registerHandler(Incoming.requestFurniInventoryWhenNotInRoomEvent, RequestFurniInventoryWhenNotInRoomEvent.class);
        this.registerHandler(Incoming.getPetInventoryEvent, GetPetInventoryEvent.class);
    }

    void registerRooms() throws Exception {
        this.registerHandler(Incoming.openFlatConnectionEvent, OpenFlatConnectionEvent.class);
        this.registerHandler(Incoming.getFurnitureAliasesEvent, GetRoomEntryDataEvent.class);// should this be seperate event classes?
        this.registerHandler(Incoming.getRoomEntryDataEvent, GetRoomEntryDataEvent.class);// should this be seperate event classes?
        this.registerHandler(Incoming.rateFlatEvent, RateFlatEvent.class);
        this.registerHandler(Incoming.getGuestRoomEvent, GetGuestRoomEvent.class);
        this.registerHandler(Incoming.saveRoomSettingsEvent, SaveRoomSettingsEvent.class);
        this.registerHandler(Incoming.placeObjectEvent, PlaceObjectEvent.class);
        this.registerHandler(Incoming.moveObjectEvent, MoveObjectEvent.class);
        this.registerHandler(Incoming.moveWallItemEvent, MoveWallItemEvent.class);
        this.registerHandler(Incoming.pickupObjectEvent, PickupObjectEvent.class);
        this.registerHandler(Incoming.requestRoomPropertySet, RequestRoomPropertySet.class);
        this.registerHandler(Incoming.startTypingEvent, StartTypingEvent.class);
        this.registerHandler(Incoming.cancelTypingEvent, CancelTypingEvent.class);
        this.registerHandler(Incoming.useFurnitureEvent, UseFurnitureEvent.class);
        this.registerHandler(Incoming.useWallItemEvent, UseWallItemEvent.class);
        this.registerHandler(Incoming.setRoomBackgroundColorDataEvent, SetRoomBackgroundColorDataEvent.class);
        this.registerHandler(Incoming.setMannequinNameEvent, SetMannequinNameEvent.class);
        this.registerHandler(Incoming.setMannequinFigureEvent, SetMannequinFigureEvent.class);
        this.registerHandler(Incoming.setClothingChangeDataEvent, SetClothingChangeDataEvent.class);
        this.registerHandler(Incoming.setObjectDataEvent, SetObjectDataEvent.class);
        this.registerHandler(Incoming.getRoomSettingsEvent, GetRoomSettingsEvent.class);
        this.registerHandler(Incoming.roomDimmerGetPresetsEvent, RoomDimmerGetPresetsEvent.class);
        this.registerHandler(Incoming.roomDimmerChangeStateEvent, RoomDimmerChangeStateEvent.class);
        this.registerHandler(Incoming.dropCarryItemEvent, DropCarryItemEvent.class);
        this.registerHandler(Incoming.lookToEvent, LookToEvent.class);
        this.registerHandler(Incoming.chatEvent, ChatEvent.class);
        this.registerHandler(Incoming.shoutEvent, ShoutEvent.class);
        this.registerHandler(Incoming.whisperEvent, WhisperEvent.class);
        this.registerHandler(Incoming.avatarExpressionEvent, AvatarExpressionEvent.class);
        this.registerHandler(Incoming.changePostureEvent, ChangePostureEvent.class);
        this.registerHandler(Incoming.danceEvent, DanceEvent.class);
        this.registerHandler(Incoming.signEvent, SignEvent.class);
        this.registerHandler(Incoming.moveAvatarEvent, MoveAvatarEvent.class);
        this.registerHandler(Incoming.respectUserEvent, RespectUserEvent.class);
        this.registerHandler(Incoming.assignRightsEvent, AssignRightsEvent.class);
        this.registerHandler(Incoming.removeOwnRoomRightsRoomEvent, RemoveOwnRoomRightsRoomEvent.class);
        this.registerHandler(Incoming.getFlatControllersEvent, GetFlatControllersEvent.class);
        this.registerHandler(Incoming.removeAllRightsEvent, RemoveAllRightsEvent.class);
        this.registerHandler(Incoming.removeRightsEvent, RemoveRightsEvent.class);
        this.registerHandler(Incoming.placeBotEvent, PlaceBotEvent.class);
        this.registerHandler(Incoming.removeBotFromFlatEvent, RemoveBotFromFlatEvent.class);
        this.registerHandler(Incoming.commandBotEvent, CommandBotEvent.class);
        this.registerHandler(Incoming.getBotCommandConfigurationDataEvent, GetBotCommandConfigurationDataEvent.class);
        this.registerHandler(Incoming.throwDiceEvent, ThrowDiceEvent.class);
        this.registerHandler(Incoming.diceOffEvent, DiceOffEvent.class);
        this.registerHandler(Incoming.spinWheelOfFortuneEvent, SpinWheelOfFortuneEvent.class);
        this.registerHandler(Incoming.creditFurniRedeemEvent, CreditFurniRedeemEvent.class);
        this.registerHandler(Incoming.placePetEvent, PlacePetEvent.class);
        this.registerHandler(Incoming.roomUserKickEvent, RoomUserKickEvent.class);
        this.registerHandler(Incoming.setCustomStackingHeightEvent, SetCustomStackingHeightEvent.class);
        this.registerHandler(Incoming.enterOneWayDoorEvent, EnterOneWayDoorEvent.class);
        this.registerHandler(Incoming.letUserInEvent, LetUserInEvent.class);
        this.registerHandler(Incoming.customizeAvatarWithFurniEvent, CustomizeAvatarWithFurniEvent.class);
        this.registerHandler(Incoming.placePostItEvent, PlacePostItEvent.class);
        this.registerHandler(Incoming.getItemDataEvent, GetItemDataEvent.class);
        this.registerHandler(Incoming.setItemDataEvent, SetItemDataEvent.class);
        this.registerHandler(Incoming.removeItemEvent, RemoveItemEvent.class);
        this.registerHandler(Incoming.roomDimmerSavePresetEvent, RoomDimmerSavePresetEvent.class);
        this.registerHandler(Incoming.rentableSpaceRentEvent, RentableSpaceRentEvent.class);
        this.registerHandler(Incoming.rentableSpaceCancelRentEvent, RentableSpaceCancelRentEvent.class);
        this.registerHandler(Incoming.updateHomeRoomEvent, UpdateHomeRoomEvent.class);
        this.registerHandler(Incoming.passCarryItemEvent, PassCarryItemEvent.class);
        this.registerHandler(Incoming.muteAllInRoomEvent, MuteAllInRoomEvent.class);
        this.registerHandler(Incoming.getCustomRoomFilterEvent, GetCustomRoomFilterEvent.class);
        this.registerHandler(Incoming.updateRoomFilterEvent, UpdateRoomFilterEvent.class);
        this.registerHandler(Incoming.submitRoomToCompetitionEvent, SubmitRoomToCompetitionEvent.class);
        this.registerHandler(Incoming.getBannedUsersFromRoomEvent, GetBannedUsersFromRoomEvent.class);
        this.registerHandler(Incoming.getOfficialSongIdEvent, GetOfficialSongIdEvent.class);
        this.registerHandler(Incoming.getSongInfoEvent, GetSongInfoEvent.class);
        this.registerHandler(Incoming.addJukeboxDiskEvent, AddJukeboxDiskEvent.class);
        this.registerHandler(Incoming.removeJukeboxDiskEvent, RemoveJukeboxDiskEvent.class);
        this.registerHandler(Incoming.getNowPlayingEvent, GetNowPlayingEvent.class);
        this.registerHandler(Incoming.jukeBoxEventOne, JukeBoxEventOne.class); // this doesnt even exist in the swf src for this prod.
        this.registerHandler(Incoming.getJukeboxPlayListEvent, GetJukeboxPlayListEvent.class);
        this.registerHandler(Incoming.addSpamWallPostItEvent, AddSpamWallPostItEvent.class);
        this.registerHandler(Incoming.getRoomAdPurchaseInfoEvent, GetRoomAdPurchaseInfoEvent.class);
        this.registerHandler(Incoming.purchaseRoomAdEvent, PurchaseRoomAdEvent.class);
        this.registerHandler(Incoming.editEventEvent, EditEventEvent.class);
        this.registerHandler(Incoming.ignoreUserEvent, IgnoreUserEvent.class);
        this.registerHandler(Incoming.unignoreUserEvent, UnignoreUserEvent.class);
        this.registerHandler(Incoming.roomUserMuteEvent, RoomUserMuteEvent.class);
        this.registerHandler(Incoming.banUserWithDurationEvent, BanUserWithDurationEvent.class);
        this.registerHandler(Incoming.unbanUserFromRoomEvent, UnbanUserFromRoomEvent.class);
        this.registerHandler(Incoming.getUserTagsEvent, GetUserTagsEvent.class);
        this.registerHandler(Incoming.getYoutubeDisplayStatusEvent, GetYoutubeDisplayStatusEvent.class);
        this.registerHandler(Incoming.controlYoutubeDisplayPlaybackEvent, ControlYoutubeDisplayPlaybackEvent.class);
        this.registerHandler(Incoming.setYoutubeDisplayPlaylistEvent, SetYoutubeDisplayPlaylistEvent.class);
        this.registerHandler(Incoming.addFavouriteRoomEvent, AddFavouriteRoomEvent.class);
        this.registerHandler(Incoming.friendFurniConfirmLockEvent, FriendFurniConfirmLockEvent.class);
        this.registerHandler(Incoming.deleteFavouriteRoomEvent, DeleteFavouriteRoomEvent.class);
        this.registerHandler(Incoming.setRandomStateEvent, SetRandomStateEvent.class);
    }

    void registerPolls() throws Exception {
        this.registerHandler(Incoming.pollRejectEvent, PollRejectEvent.class);
        this.registerHandler(Incoming.pollStartEvent, PollStartEvent.class);
        this.registerHandler(Incoming.answerPollEvent, AnswerPollEvent.class);
    }

    void registerModTool() throws Exception {
        this.registerHandler(Incoming.getModeratorRoomInfoEvent, GetModeratorRoomInfoEvent.class);
        this.registerHandler(Incoming.getRoomChatlogEvent, GetRoomChatlogEvent.class);
        this.registerHandler(Incoming.getModeratorUserInfoEvent, GetModeratorUserInfoEvent.class);
        this.registerHandler(Incoming.pickIssuesEvent, PickIssuesEvent.class);
        this.registerHandler(Incoming.closeIssuesEvent, CloseIssuesEvent.class);
        this.registerHandler(Incoming.releaseIssuesEvent, ReleaseIssuesEvent.class);
        this.registerHandler(Incoming.modMessageEvent, ModMessageEvent.class);
        this.registerHandler(Incoming.modToolWarnEvent, ModToolWarnEvent.class);
        this.registerHandler(Incoming.modKickEvent, ModKickEvent.class);
        this.registerHandler(Incoming.moderatorActionEvent, ModeratorActionEvent.class);
        this.registerHandler(Incoming.moderateRoomEvent, ModerateRoomEvent.class);
        this.registerHandler(Incoming.getRoomVisitsEvent, GetRoomVisitsEvent.class);
        this.registerHandler(Incoming.getCfhChatlogEvent, GetCfhChatlogEvent.class);
        this.registerHandler(Incoming.modToolRequestRoomUserChatlogEvent, ModToolRequestRoomUserChatlogEvent.class);
        this.registerHandler(Incoming.getUserChatlogEvent, GetUserChatlogEvent.class);
        this.registerHandler(Incoming.modAlertEvent, ModAlertEvent.class);
        this.registerHandler(Incoming.modMuteEvent, ModMuteEvent.class);
        this.registerHandler(Incoming.modBanEvent, ModBanEvent.class);
        this.registerHandler(Incoming.modTradingLockEvent, ModTradingLockEvent.class);
        this.registerHandler(Incoming.modToolSanctionEvent, ModToolSanctionEvent.class);
        this.registerHandler(Incoming.closeIssueDefaultActionEvent, CloseIssueDefaultActionEvent.class);

        this.registerHandler(Incoming.getPendingCallsForHelpEvent, GetPendingCallsForHelpEvent.class);
        this.registerHandler(Incoming.getGuideReportingStatusEvent, GetGuideReportingStatusEvent.class);
        this.registerHandler(Incoming.chatReviewSessionCreateEvent, ChatReviewSessionCreateEvent.class);
        this.registerHandler(Incoming.callForHelpEvent, CallForHelpEvent.class);
        this.registerHandler(Incoming.callForHelpFromIMEvent, CallForHelpFromIMEvent.class);
        this.registerHandler(Incoming.callForHelpFromForumThreadEvent, CallForHelpFromForumThreadEvent.class);
        this.registerHandler(Incoming.callForHelpFromForumMessageEvent, CallForHelpFromForumMessageEvent.class);
        this.registerHandler(Incoming.callForHelpFromPhotoEvent, CallForHelpFromPhotoEvent.class);
    }

    void registerTrading() throws Exception {
        this.registerHandler(Incoming.openTradingEvent, OpenTradingEvent.class);
        this.registerHandler(Incoming.addItemToTradeEvent, AddItemToTradeEvent.class);
        this.registerHandler(Incoming.addItemsToTradeEvent, AddItemsToTradeEvent.class);
        this.registerHandler(Incoming.removeItemFromTradeEvent, RemoveItemFromTradeEvent.class);
        this.registerHandler(Incoming.acceptTradingEvent, AcceptTradingEvent.class);
        this.registerHandler(Incoming.unacceptTradingEvent, UnacceptTradingEvent.class);
        this.registerHandler(Incoming.confirmAcceptTradingEvent, ConfirmAcceptTradingEvent.class);
        this.registerHandler(Incoming.closeTradingEvent, CloseTradingEvent.class);
        this.registerHandler(Incoming.confirmDeclineTradingEvent, ConfirmDeclineTradingEvent.class);
    }

    void registerGuilds() throws Exception {
        this.registerHandler(Incoming.getGuildCreationInfoEvent, GetGuildCreationInfoEvent.class);
        this.registerHandler(Incoming.getGuildEditorDataEvent, GetGuildEditorDataEvent.class);
        this.registerHandler(Incoming.createGuildEvent, CreateGuildEvent.class);
        this.registerHandler(Incoming.getHabboGroupDetailsEvent, GetHabboGroupDetailsEvent.class);
        this.registerHandler(Incoming.getGuildEditInfoEvent, GetGuildEditInfoEvent.class);
        this.registerHandler(Incoming.getGuildMembersEvent, GetGuildMembersEvent.class);
        this.registerHandler(Incoming.joinHabboGroupEvent, JoinHabboGroupEvent.class);
        this.registerHandler(Incoming.updateGuildIdentityEvent, UpdateGuildIdentityEvent.class);
        this.registerHandler(Incoming.updateGuildBadgeEvent, UpdateGuildBadgeEvent.class);
        this.registerHandler(Incoming.updateGuildColorsEvent, UpdateGuildColorsEvent.class);
        this.registerHandler(Incoming.removeAdminRightsFromMemberEvent, RemoveAdminRightsFromMemberEvent.class);
        this.registerHandler(Incoming.kickMemberEvent, KickMemberEvent.class);
        this.registerHandler(Incoming.updateGuildSettingsEvent, UpdateGuildSettingsEvent.class);
        this.registerHandler(Incoming.approveMembershipRequestEvent, ApproveMembershipRequestEvent.class);
        this.registerHandler(Incoming.rejectMembershipRequestEvent, RejectMembershipRequestEvent.class);
        this.registerHandler(Incoming.addAdminRightsToMemberEvent, AddAdminRightsToMemberEvent.class);
        this.registerHandler(Incoming.selectFavouriteHabboGroupEvent, SelectFavouriteHabboGroupEvent.class);
        this.registerHandler(Incoming.getGuildMembershipsEvent, GetGuildMembershipsEvent.class);
        this.registerHandler(Incoming.getGuildFurniContextMenuInfoEvent, GetGuildFurniContextMenuInfoEvent.class);
        this.registerHandler(Incoming.getMemberGuildItemCountEvent, GetMemberGuildItemCountEvent.class);
        this.registerHandler(Incoming.deselectFavouriteHabboGroupEvent, DeselectFavouriteHabboGroupEvent.class);
        this.registerHandler(Incoming.deactivateGuildEvent, DeactivateGuildEvent.class);
        this.registerHandler(Incoming.getForumsListEvent, GetForumsListEvent.class);
        this.registerHandler(Incoming.getThreadsEvent, GetThreadsEvent.class);
        this.registerHandler(Incoming.getForumStatsEvent, GetForumStatsEvent.class);
        this.registerHandler(Incoming.postMessageEvent, PostMessageEvent.class);
        this.registerHandler(Incoming.updateForumSettingsEvent, UpdateForumSettingsEvent.class);
        this.registerHandler(Incoming.getMessagesEvent, GetMessagesEvent.class);
        this.registerHandler(Incoming.moderateMessageEvent, ModerateMessageEvent.class);
        this.registerHandler(Incoming.moderateThreadEvent, ModerateThreadEvent.class);
        this.registerHandler(Incoming.updateThreadEvent, UpdateThreadEvent.class);
        this.registerHandler(Incoming.getHabboGroupBadgesEvent, GetHabboGroupBadgesEvent.class);

//        this.registerHandler(Incoming.guildForumDataEvent,              GuildForumModerateMessageEvent.class);
//        this.registerHandler(Incoming.guildForumDataEvent,              GuildForumModerateThreadEvent.class);
//        this.registerHandler(Incoming.guildForumDataEvent,              GuildForumPostThreadEvent.class);
//        this.registerHandler(Incoming.guildForumDataEvent,              GuildForumThreadsEvent.class);
//        this.registerHandler(Incoming.guildForumDataEvent,              GuildForumThreadsMessagesEvent.class);
//        this.registerHandler(Incoming.guildForumDataEvent,              GuildForumUpdateSettingsEvent.class);
    }

    void registerPets() throws Exception {
        this.registerHandler(Incoming.getPetInfoEvent, GetPetInfoEvent.class);
        this.registerHandler(Incoming.removePetFromFlatEvent, RemovePetFromFlatEvent.class);
        this.registerHandler(Incoming.respectPetEvent, RespectPetEvent.class);
        this.registerHandler(Incoming.getPetCommandsEvent, GetPetCommandsEvent.class);
        this.registerHandler(Incoming.customizePetWithFurniEvent, CustomizePetWithFurniEvent.class);
        this.registerHandler(Incoming.togglePetRidingPermissionEvent, TogglePetRidingPermissionEvent.class);
        this.registerHandler(Incoming.mountPetEvent, MountPetEvent.class);
        this.registerHandler(Incoming.removeSaddleFromPetEvent, RemoveSaddleFromPetEvent.class);
        this.registerHandler(Incoming.togglePetBreedingPermissionEvent, TogglePetBreedingPermissionEvent.class);
        this.registerHandler(Incoming.compostPlantEvent, CompostPlantEvent.class);
        this.registerHandler(Incoming.breedPetsEvent, BreedPetsEvent.class);
        this.registerHandler(Incoming.movePetEvent, MovePetEvent.class);
        this.registerHandler(Incoming.openPetPackageEvent, OpenPetPackageEvent.class);
        this.registerHandler(Incoming.cancelPetBreedingEvent, CancelPetBreedingEvent.class);
        this.registerHandler(Incoming.confirmPetBreedingEvent, ConfirmPetBreedingEvent.class);
    }

    void registerWired() throws Exception {
        this.registerHandler(Incoming.updateTriggerEvent, UpdateTriggerEvent.class);
        this.registerHandler(Incoming.updateActionEvent, UpdateActionEvent.class);
        this.registerHandler(Incoming.updateConditionEvent, UpdateConditionEvent.class);
        this.registerHandler(Incoming.applySnapshotEvent, ApplySnapshotEvent.class);
    }

    void registerUnknown() throws Exception {
        this.registerHandler(Incoming.getResolutionAchievementsEvent, GetResolutionAchievementsEvent.class);
        this.registerHandler(Incoming.getTalentTrackEvent, GetTalentTrackEvent.class);
        this.registerHandler(Incoming.getBadgePointLimitsEvent, GetBadgePointLimitsEvent.class);
        this.registerHandler(Incoming.getCfhStatusEvent, GetCfhStatusEvent.class);
    }

    void registerFloorPlanEditor() throws Exception {
        this.registerHandler(Incoming.updateFloorPropertiesEvent, UpdateFloorPropertiesEvent.class);
        this.registerHandler(Incoming.getOccupiedTilesEvent, GetOccupiedTilesEvent.class);
        this.registerHandler(Incoming.getRoomEntryTileEvent, GetRoomEntryTileEvent.class);
    }

    void registerAchievements() throws Exception {
        this.registerHandler(Incoming.getAchievementsEvent, GetAchievementsEvent.class);
        this.registerHandler(Incoming.requestAchievementConfigurationEvent, RequestAchievementConfigurationEvent.class);
    }

    void registerGuides() throws Exception {
        this.registerHandler(Incoming.guideSessionOnDutyUpdateEvent, GuideSessionOnDutyUpdateEvent.class);
        this.registerHandler(Incoming.guideSessionCreateEvent, GuideSessionCreateEvent.class);
        this.registerHandler(Incoming.guideSessionIsTypingEvent, GuideSessionIsTypingEvent.class);
        this.registerHandler(Incoming.guideSessionReportEvent, GuideSessionReportEvent.class);
        this.registerHandler(Incoming.guideSessionFeedbackEvent, GuideSessionFeedbackEvent.class);
        this.registerHandler(Incoming.guideSessionMessageEvent, GuideSessionMessageEvent.class);
        this.registerHandler(Incoming.guideSessionRequesterCancelsEvent, GuideSessionRequesterCancelsEvent.class);
        this.registerHandler(Incoming.guideSessionGuideDecidesEvent, GuideSessionGuideDecidesEvent.class);
        this.registerHandler(Incoming.guideSessionInviteRequesterEvent, GuideSessionInviteRequesterEvent.class);
        this.registerHandler(Incoming.guideSessionGetRequesterRoomEvent, GuideSessionGetRequesterRoomEvent.class);
        this.registerHandler(Incoming.guideSessionResolvedEvent, GuideSessionResolvedEvent.class);

        this.registerHandler(Incoming.chatReviewGuideDetachedEvent, ChatReviewGuideDetachedEvent.class);
        this.registerHandler(Incoming.chatReviewGuideDecidesOnOfferEvent, ChatReviewGuideDecidesOnOfferEvent.class);
        this.registerHandler(Incoming.chatReviewGuideVoteEvent, ChatReviewGuideVoteEvent.class);
    }

    void registerCrafting() throws Exception {
        this.registerHandler(Incoming.getCraftingRecipeEvent, GetCraftingRecipeEvent.class);
        this.registerHandler(Incoming.getCraftableProductsEvent, GetCraftableProductsEvent.class);
        this.registerHandler(Incoming.craftEvent, CraftEvent.class);
        this.registerHandler(Incoming.craftSecretEvent, CraftSecretEvent.class);
        this.registerHandler(Incoming.getCraftingRecipesAvailableEvent, GetCraftingRecipesAvailableEvent.class);
    }

    void registerCamera() throws Exception {
        this.registerHandler(Incoming.renderRoomEvent, RenderRoomEvent.class);
        this.registerHandler(Incoming.requestCameraConfigurationEvent, RequestCameraConfigurationEvent.class);
        this.registerHandler(Incoming.purchasePhotoEvent, PurchasePhotoEvent.class);
        this.registerHandler(Incoming.renderRoomThumbnailEvent, RenderRoomThumbnailEvent.class);
        this.registerHandler(Incoming.publishPhotoEvent, PublishPhotoEvent.class);
    }

    void registerGameCenter() throws Exception {
        this.registerHandler(Incoming.getGameListEvent, GetGameListEvent.class);
        this.registerHandler(Incoming.getGameStatusEvent, GetGameStatusEvent.class);
        this.registerHandler(Incoming.joinQueueEvent, JoinQueueEvent.class);
        this.registerHandler(Incoming.getWeeklyGameRewardWinnersEvent, GetWeeklyGameRewardWinnersEvent.class);
        this.registerHandler(Incoming.gameUnloadedEvent, GameUnloadedEvent.class);
        this.registerHandler(Incoming.getWeeklyGameRewardEvent, GetWeeklyGameRewardEvent.class);
        this.registerHandler(Incoming.game2GetAccountGameStatusEvent, Game2GetAccountGameStatusEvent.class);
    }
}