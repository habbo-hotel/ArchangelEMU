package com.eu.habbo.messages;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.incoming.achievements.RequestAchievementConfigurationEvent;
import com.eu.habbo.messages.incoming.achievements.GetAchievementsEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorAlertEvent;
import com.eu.habbo.messages.incoming.ambassadors.FollowFriendEvent;
import com.eu.habbo.messages.incoming.camera.*;
import com.eu.habbo.messages.incoming.campaign.OpenCampaignCalendarDoorAsStaffEvent;
import com.eu.habbo.messages.incoming.campaign.OpenCampaignCalendarDoorEvent;
import com.eu.habbo.messages.incoming.catalog.*;
import com.eu.habbo.messages.incoming.catalog.marketplace.*;
import com.eu.habbo.messages.incoming.catalog.recycler.PresentOpenEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RecycleItemsEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.GetRecyclerStatusEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.GetRecyclerPrizesEvent;
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
import com.eu.habbo.messages.incoming.inventory.GetBadgesEvent;
import com.eu.habbo.messages.incoming.inventory.GetBotInventoryEvent;
import com.eu.habbo.messages.incoming.inventory.RequestFurniInventoryWhenNotInRoomEvent;
import com.eu.habbo.messages.incoming.inventory.GetPetInventoryEvent;
import com.eu.habbo.messages.incoming.modtool.*;
import com.eu.habbo.messages.incoming.navigator.*;
import com.eu.habbo.messages.incoming.polls.AnswerPollEvent;
import com.eu.habbo.messages.incoming.polls.PollRejectEvent;
import com.eu.habbo.messages.incoming.polls.PollStartEvent;
import com.eu.habbo.messages.incoming.rooms.*;
import com.eu.habbo.messages.incoming.rooms.bots.RemoveBotFromFlatEvent;
import com.eu.habbo.messages.incoming.rooms.bots.PlaceBotEvent;
import com.eu.habbo.messages.incoming.rooms.bots.CommandBotEvent;
import com.eu.habbo.messages.incoming.rooms.bots.GetBotCommandConfigurationDataEvent;
import com.eu.habbo.messages.incoming.rooms.items.*;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.*;
import com.eu.habbo.messages.incoming.rooms.items.lovelock.FriendFurniConfirmLockEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentableSpaceCancelRentEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentableSpaceRentEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.SetYoutubeDisplayPlaylistEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.GetYoutubeDisplayStatusEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.ControlYoutubeDisplayPlaybackEvent;
import com.eu.habbo.messages.incoming.rooms.pets.*;
import com.eu.habbo.messages.incoming.rooms.promotions.PurchaseRoomAdEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.GetRoomAdPurchaseInfoEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.EditEventEvent;
import com.eu.habbo.messages.incoming.rooms.users.*;
import com.eu.habbo.messages.incoming.trading.*;
import com.eu.habbo.messages.incoming.unknown.GetResolutionAchievementsEvent;
import com.eu.habbo.messages.incoming.inventory.GetBadgePointLimitsEvent;
import com.eu.habbo.messages.incoming.users.*;
import com.eu.habbo.messages.incoming.wired.ApplySnapshotEvent;
import com.eu.habbo.messages.incoming.wired.UpdateConditionEvent;
import com.eu.habbo.messages.incoming.wired.UpdateActionEvent;
import com.eu.habbo.messages.incoming.wired.UpdateTriggerEvent;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import gnu.trove.map.hash.THashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PacketManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketManager.class);

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
                logList.add(Integer.valueOf(s));
            } catch (NumberFormatException e) {

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
                        LOGGER.warn("Client packet {} requires an authenticated session.", packet.getMessageId());
                    }

                    return;
                }

                final MessageHandler handler = handlerClass.newInstance();

                if (handler.getRatelimit() > 0) {
                    if (client.messageTimestamps.containsKey(handlerClass) && System.currentTimeMillis() - client.messageTimestamps.get(handlerClass) < handler.getRatelimit()) {
                        if (PacketManager.DEBUG_SHOW_PACKETS) {
                            LOGGER.warn("Client packet {} was ratelimited.", packet.getMessageId());
                        }

                        return;
                    } else {
                        client.messageTimestamps.put(handlerClass, System.currentTimeMillis());
                    }
                }

                if (logList.contains(packet.getMessageId()) && client.getHabbo() != null) {
                    LOGGER.info("User {} sent packet {} with body {}", client.getHabbo().getHabboInfo().getUsername(), packet.getMessageId(), packet.getMessageBody());
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
            LOGGER.error("Caught exception", e);
        }
    }

    boolean isRegistered(int header) {
        return this.incoming.containsKey(header);
    }

    private void registerAmbassadors() throws Exception {
        this.registerHandler(Incoming.AmbassadorAlertEvent, AmbassadorAlertEvent.class);
        this.registerHandler(Incoming.FollowFriendEvent, FollowFriendEvent.class);
    }

    private void registerCatalog() throws Exception {
        this.registerHandler(Incoming.GetRecyclerPrizesEvent, GetRecyclerPrizesEvent.class);
        this.registerHandler(Incoming.GetBundleDiscountRulesetEvent, GetBundleDiscountRulesetEvent.class);
        this.registerHandler(Incoming.GetGiftWrappingConfigurationEvent, GetGiftWrappingConfigurationEvent.class);
        this.registerHandler(Incoming.GetMarketplaceConfigurationEvent, GetMarketplaceConfigurationEvent.class);
        this.registerHandler(Incoming.GetCatalogIndexEvent, GetCatalogIndexEvent.class);
        this.registerHandler(Incoming.BuildersClubQueryFurniCountEvent, BuildersClubQueryFurniCountEvent.class);
        this.registerHandler(Incoming.GetCatalogPageEvent, GetCatalogPageEvent.class);
        this.registerHandler(Incoming.PurchaseFromCatalogAsGiftEvent, PurchaseFromCatalogAsGiftEvent.class);
        this.registerHandler(Incoming.PurchaseFromCatalogEvent, PurchaseFromCatalogEvent.class);
        this.registerHandler(Incoming.RedeemVoucherEvent, RedeemVoucherEvent.class);
        this.registerHandler(Incoming.GetRecyclerStatusEvent, GetRecyclerStatusEvent.class);
        this.registerHandler(Incoming.RecycleItemsEvent, RecycleItemsEvent.class);
        this.registerHandler(Incoming.PresentOpenEvent, PresentOpenEvent.class);
        this.registerHandler(Incoming.GetMarketplaceOwnOffersEvent, GetMarketplaceOwnOffersEvent.class);
        this.registerHandler(Incoming.CancelMarketplaceOfferEvent, CancelMarketplaceOfferEvent.class);
        this.registerHandler(Incoming.GetMarketplaceOffersEvent, GetMarketplaceOffersEvent.class);
        this.registerHandler(Incoming.GetMarketplaceItemStatsEvent, GetMarketplaceItemStatsEvent.class);
        this.registerHandler(Incoming.BuyMarketplaceOfferEvent, BuyMarketplaceOfferEvent.class);
        this.registerHandler(Incoming.GetMarketplaceCanMakeOfferEvent, GetMarketplaceCanMakeOfferEvent.class);
        this.registerHandler(Incoming.MakeOfferEvent, MakeOfferEvent.class);
        this.registerHandler(Incoming.RedeemMarketplaceOfferCreditsEvent, RedeemMarketplaceOfferCreditsEvent.class);
        this.registerHandler(Incoming.GetSellablePetPalettesEvent, GetSellablePetPalettesEvent.class);
        this.registerHandler(Incoming.ApproveNameEvent, ApproveNameEvent.class);
        this.registerHandler(Incoming.GetClubOffersEvent, GetClubOffersEvent.class);
        this.registerHandler(Incoming.GetClubGiftInfo, GetClubGiftInfo.class);
        this.registerHandler(Incoming.GetProductOfferEvent, GetProductOfferEvent.class);
        this.registerHandler(Incoming.PurchaseTargetedOfferEvent, PurchaseTargetedOfferEvent.class);
        this.registerHandler(Incoming.SetTargetedOfferStateEvent, SetTargetedOfferStateEvent.class);
        this.registerHandler(Incoming.SelectClubGiftEvent, SelectClubGiftEvent.class);
        this.registerHandler(Incoming.ScrGetKickbackInfoEvent, ScrGetKickbackInfoEvent.class);
        this.registerHandler(Incoming.BuildersClubPlaceWallItemEvent, BuildersClubPlaceWallItemEvent.class);
        this.registerHandler(Incoming.PurchaseVipMembershipExtensionEvent, PurchaseVipMembershipExtensionEvent.class);
    }

    private void registerEvent() throws Exception {
        this.registerHandler(Incoming.OpenCampaignCalendarDoorAsStaffEvent, OpenCampaignCalendarDoorAsStaffEvent.class);
        this.registerHandler(Incoming.OpenCampaignCalendarDoorEvent, OpenCampaignCalendarDoorEvent.class);
    }

    private void registerHandshake() throws Exception {
        this.registerHandler(Incoming.ClientHelloEvent, ClientHelloEvent.class);
        this.registerHandler(Incoming.InitDiffieHandshakeEvent, InitDiffieHandshakeEvent.class);
        this.registerHandler(Incoming.CompleteDiffieHandshakeEvent, CompleteDiffieHandshakeEvent.class);
        this.registerHandler(Incoming.SSOTicketEvent, SSOTicketEvent.class);
        this.registerHandler(Incoming.UniqueIDEvent, UniqueIDEvent.class);
        this.registerHandler(Incoming.GetIgnoredUsersEvent, GetIgnoredUsersEvent.class);
        this.registerHandler(Incoming.LatencyPingRequestEvent, LatencyPingRequestEvent.class);
    }

    private void registerFriends() throws Exception {
        this.registerHandler(Incoming.GetMOTDEvent, GetMOTDEvent.class);
        this.registerHandler(Incoming.SetRelationshipStatusEvent, SetRelationshipStatusEvent.class);
        this.registerHandler(Incoming.RemoveFriendEvent, RemoveFriendEvent.class);
        this.registerHandler(Incoming.HabboSearchEvent, HabboSearchEvent.class);
        this.registerHandler(Incoming.RequestFriendEvent, RequestFriendEvent.class);
        this.registerHandler(Incoming.AcceptFriendEvent, AcceptFriendEvent.class);
        this.registerHandler(Incoming.DeclineFriendEvent, DeclineFriendEvent.class);
        this.registerHandler(Incoming.SendMsgEvent, SendMsgEvent.class);
        this.registerHandler(Incoming.GetFriendRequestsEvent, GetFriendRequestsEvent.class);
        this.registerHandler(Incoming.VisitUserEvent, VisitUserEvent.class);
        this.registerHandler(Incoming.MessengerInitEvent, MessengerInitEvent.class);
        this.registerHandler(Incoming.FindNewFriendsEvent, FindNewFriendsEvent.class);
        this.registerHandler(Incoming.SendRoomInviteEvent, SendRoomInviteEvent.class);
    }

    private void registerUsers() throws Exception {
        this.registerHandler(Incoming.InfoRetrieveEvent, InfoRetrieveEvent.class);
        this.registerHandler(Incoming.GetCreditsInfoEvent, GetCreditsInfoEvent.class);
        this.registerHandler(Incoming.ScrGetUserInfoEvent, ScrGetUserInfoEvent.class);
        this.registerHandler(Incoming.GetSoundSettingsEvent, GetSoundSettingsEvent.class);
        this.registerHandler(Incoming.GetTalentTrackLevelEvent, GetTalentTrackLevelEvent.class);
        this.registerHandler(Incoming.GetExtendedProfileEvent, GetExtendedProfileEvent.class);
        this.registerHandler(Incoming.GetRelationshipStatusInfoEvent, GetRelationshipStatusInfoEvent.class);
        this.registerHandler(Incoming.GetWardrobeEvent, GetWardrobeEvent.class);
        this.registerHandler(Incoming.SaveWardrobeOutfitEvent, SaveWardrobeOutfitEvent.class);
        this.registerHandler(Incoming.ChangeMottoEvent, ChangeMottoEvent.class);
        this.registerHandler(Incoming.UpdateFigureDataEvent, UpdateFigureDataEvent.class);
        this.registerHandler(Incoming.SetActivatedBadgesEvent, SetActivatedBadgesEvent.class);
        this.registerHandler(Incoming.GetSelectedBadgesEvent, GetSelectedBadgesEvent.class);
        this.registerHandler(Incoming.SetSoundSettingsEvent, SetSoundSettingsEvent.class);
        this.registerHandler(Incoming.SetRoomCameraPreferencesEvent, SetRoomCameraPreferencesEvent.class);
        this.registerHandler(Incoming.SetIgnoreRoomInvitesEvent, SetIgnoreRoomInvitesEvent.class);
        this.registerHandler(Incoming.SetChatPreferencesEvent, SetChatPreferencesEvent.class);
        this.registerHandler(Incoming.AvatarEffectActivatedEvent, AvatarEffectActivatedEvent.class);
        this.registerHandler(Incoming.AvatarEffectSelectedEvent, AvatarEffectSelectedEvent.class);
        this.registerHandler(Incoming.EventLogEvent, EventLogEvent.class);
        this.registerHandler(Incoming.NewUserExperienceScriptProceedEvent, NewUserExperienceScriptProceedEvent.class);
        this.registerHandler(Incoming.NewUserExperienceGetGiftsEvent, NewUserExperienceGetGiftsEvent.class);
        this.registerHandler(Incoming.CheckUserNameEvent, CheckUserNameEvent.class);
        this.registerHandler(Incoming.ChangeUserNameEvent, ChangeUserNameEvent.class);
        this.registerHandler(Incoming.SetChatStylePreferenceEvent, SetChatStylePreferenceEvent.class);
        this.registerHandler(Incoming.UpdateUIFlagsEvent, UpdateUIFlagsEvent.class);
    }

    private void registerNavigator() throws Exception {
        this.registerHandler(Incoming.GetUserFlatCatsEvent, GetUserFlatCatsEvent.class);
        this.registerHandler(Incoming.PopularRoomsSearchEvent, PopularRoomsSearchEvent.class);
        this.registerHandler(Incoming.RoomsWithHighestScoreSearchEvent, RoomsWithHighestScoreSearchEvent.class);
        this.registerHandler(Incoming.MyRoomsSearchEvent, MyRoomsSearchEvent.class);
        this.registerHandler(Incoming.CanCreateRoomEvent, CanCreateRoomEvent.class);
        this.registerHandler(Incoming.GetUnreadForumsCountEvent, GetUnreadForumsCountEvent.class);
        this.registerHandler(Incoming.CreateFlatEvent, CreateFlatEvent.class);
        this.registerHandler(Incoming.GetPopularRoomTagsEvent, GetPopularRoomTagsEvent.class);
        this.registerHandler(Incoming.SearchRoomsByTagEvent, SearchRoomsByTagEvent.class);
        this.registerHandler(Incoming.RoomTextSearchEvent, RoomTextSearchEvent.class);
        this.registerHandler(Incoming.RoomsWhereMyFriendsAreSearchEvent, RoomsWhereMyFriendsAreSearchEvent.class);
        this.registerHandler(Incoming.MyFriendsRoomsSearchEvent, MyFriendsRoomsSearchEvent.class);
        this.registerHandler(Incoming.MyRoomRightsSearchEvent, MyRoomRightsSearchEvent.class);
        this.registerHandler(Incoming.MyGuildBasesSearchEvent, MyGuildBasesSearchEvent.class);
        this.registerHandler(Incoming.MyFavouriteRoomsSearchEvent, MyFavouriteRoomsSearchEvent.class);
        this.registerHandler(Incoming.MyRoomHistorySearchEvent, MyRoomHistorySearchEvent.class);
        this.registerHandler(Incoming.NewNavigatorInitEvent, NewNavigatorInitEvent.class);
        this.registerHandler(Incoming.NewNavigatorSearchEvent, NewNavigatorSearchEvent.class);
        this.registerHandler(Incoming.ForwardToSomeRoomEvent, ForwardToSomeRoomEvent.class);
        this.registerHandler(Incoming.GetUserEventCatsEvent, GetUserEventCatsEvent.class);
        this.registerHandler(Incoming.SetNewNavigatorWindowPreferencesEvent, SetNewNavigatorWindowPreferencesEvent.class);
        this.registerHandler(Incoming.DeleteRoomEvent, DeleteRoomEvent.class);
        this.registerHandler(Incoming.NavigatorSetSearchCodeViewModeEvent, NavigatorSetSearchCodeViewModeEvent.class);
        this.registerHandler(Incoming.NavigatorAddCollapsedCategoryEvent, NavigatorAddCollapsedCategoryEvent.class);
        this.registerHandler(Incoming.NavigatorRemoveCollapsedCategoryEvent, NavigatorRemoveCollapsedCategoryEvent.class);
        this.registerHandler(Incoming.NavigatorAddSavedSearchEvent, NavigatorAddSavedSearchEvent.class);
        this.registerHandler(Incoming.NavigatorDeleteSavedSearchEvent, NavigatorDeleteSavedSearchEvent.class);
    }

    private void registerHotelview() throws Exception {
        this.registerHandler(Incoming.QuitEvent, QuitEvent.class);
        this.registerHandler(Incoming.GetBonusRareInfoEvent, GetBonusRareInfoEvent.class);
        this.registerHandler(Incoming.GetPromoArticlesEvent, GetPromoArticlesEvent.class);
        this.registerHandler(Incoming.GetCurrentTimingCodeEvent, GetCurrentTimingCodeEvent.class);
        this.registerHandler(Incoming.HotelViewRequestBadgeRewardEvent, HotelViewRequestBadgeRewardEvent.class);
        this.registerHandler(Incoming.HotelViewClaimBadgeRewardEvent, HotelViewClaimBadgeRewardEvent.class);
        this.registerHandler(Incoming.GetLimitedOfferAppearingNextEvent, GetLimitedOfferAppearingNextEvent.class);
        this.registerHandler(Incoming.HotelViewRequestSecondsUntilEvent, HotelViewRequestSecondsUntilEvent.class);
    }

    private void registerInventory() throws Exception {
        this.registerHandler(Incoming.GetBadgesEvent, GetBadgesEvent.class);
        this.registerHandler(Incoming.GetBotInventoryEvent, GetBotInventoryEvent.class);
        this.registerHandler(Incoming.RequestFurniInventoryEvent, RequestFurniInventoryWhenNotInRoomEvent.class);
        this.registerHandler(Incoming.RequestFurniInventoryWhenNotInRoomEvent, RequestFurniInventoryWhenNotInRoomEvent.class);
        this.registerHandler(Incoming.GetPetInventoryEvent, GetPetInventoryEvent.class);
    }

    void registerRooms() throws Exception {
        this.registerHandler(Incoming.OpenFlatConnectionEvent, OpenFlatConnectionEvent.class);
        this.registerHandler(Incoming.GetFurnitureAliasesEvent, GetRoomEntryDataEvent.class);// should this be seperate event classes?
        this.registerHandler(Incoming.GetRoomEntryDataEvent, GetRoomEntryDataEvent.class);// should this be seperate event classes?
        this.registerHandler(Incoming.RateFlatEvent, RateFlatEvent.class);
        this.registerHandler(Incoming.GetGuestRoomEvent, GetGuestRoomEvent.class);
        this.registerHandler(Incoming.SaveRoomSettingsEvent, SaveRoomSettingsEvent.class);
        this.registerHandler(Incoming.PlaceObjectEvent, PlaceObjectEvent.class);
        this.registerHandler(Incoming.MoveObjectEvent, MoveObjectEvent.class);
        this.registerHandler(Incoming.MoveWallItemEvent, MoveWallItemEvent.class);
        this.registerHandler(Incoming.PickupObjectEvent, PickupObjectEvent.class);
        this.registerHandler(Incoming.RequestRoomPropertySet, RequestRoomPropertySet.class);
        this.registerHandler(Incoming.StartTypingEvent, StartTypingEvent.class);
        this.registerHandler(Incoming.CancelTypingEvent, CancelTypingEvent.class);
        this.registerHandler(Incoming.UseFurnitureEvent, UseFurnitureEvent.class);
        this.registerHandler(Incoming.UseWallItemEvent, UseWallItemEvent.class);
        this.registerHandler(Incoming.SetRoomBackgroundColorDataEvent, SetRoomBackgroundColorDataEvent.class);
        this.registerHandler(Incoming.SetMannequinNameEvent, SetMannequinNameEvent.class);
        this.registerHandler(Incoming.SetMannequinFigureEvent, SetMannequinFigureEvent.class);
        this.registerHandler(Incoming.SetClothingChangeDataEvent, SetClothingChangeDataEvent.class);
        this.registerHandler(Incoming.SetObjectDataEvent, SetObjectDataEvent.class);
        this.registerHandler(Incoming.GetRoomSettingsEvent, GetRoomSettingsEvent.class);
        this.registerHandler(Incoming.RoomDimmerGetPresetsEvent, RoomDimmerGetPresetsEvent.class);
        this.registerHandler(Incoming.RoomDimmerChangeStateEvent, RoomDimmerChangeStateEvent.class);
        this.registerHandler(Incoming.DropCarryItemEvent, DropCarryItemEvent.class);
        this.registerHandler(Incoming.LookToEvent, LookToEvent.class);
        this.registerHandler(Incoming.ChatEvent, ChatEvent.class);
        this.registerHandler(Incoming.ShoutEvent, ShoutEvent.class);
        this.registerHandler(Incoming.WhisperEvent, WhisperEvent.class);
        this.registerHandler(Incoming.AvatarExpressionEvent, AvatarExpressionEvent.class);
        this.registerHandler(Incoming.ChangePostureEvent, ChangePostureEvent.class);
        this.registerHandler(Incoming.DanceEvent, DanceEvent.class);
        this.registerHandler(Incoming.SignEvent, SignEvent.class);
        this.registerHandler(Incoming.MoveAvatarEvent, MoveAvatarEvent.class);
        this.registerHandler(Incoming.RespectUserEvent, RespectUserEvent.class);
        this.registerHandler(Incoming.AssignRightsEvent, AssignRightsEvent.class);
        this.registerHandler(Incoming.RemoveOwnRoomRightsRoomEvent, RemoveOwnRoomRightsRoomEvent.class);
        this.registerHandler(Incoming.GetFlatControllersEvent, GetFlatControllersEvent.class);
        this.registerHandler(Incoming.RemoveAllRightsEvent, RemoveAllRightsEvent.class);
        this.registerHandler(Incoming.RemoveRightsEvent, RemoveRightsEvent.class);
        this.registerHandler(Incoming.PlaceBotEvent, PlaceBotEvent.class);
        this.registerHandler(Incoming.RemoveBotFromFlatEvent, RemoveBotFromFlatEvent.class);
        this.registerHandler(Incoming.CommandBotEvent, CommandBotEvent.class);
        this.registerHandler(Incoming.GetBotCommandConfigurationDataEvent, GetBotCommandConfigurationDataEvent.class);
        this.registerHandler(Incoming.ThrowDiceEvent, ThrowDiceEvent.class);
        this.registerHandler(Incoming.DiceOffEvent, DiceOffEvent.class);
        this.registerHandler(Incoming.SpinWheelOfFortuneEvent, SpinWheelOfFortuneEvent.class);
        this.registerHandler(Incoming.CreditFurniRedeemEvent, CreditFurniRedeemEvent.class);
        this.registerHandler(Incoming.PlacePetEvent, PlacePetEvent.class);
        this.registerHandler(Incoming.RoomUserKickEvent, RoomUserKickEvent.class);
        this.registerHandler(Incoming.SetCustomStackingHeightEvent, SetCustomStackingHeightEvent.class);
        this.registerHandler(Incoming.EnterOneWayDoorEvent, EnterOneWayDoorEvent.class);
        this.registerHandler(Incoming.LetUserInEvent, LetUserInEvent.class);
        this.registerHandler(Incoming.CustomizeAvatarWithFurniEvent, CustomizeAvatarWithFurniEvent.class);
        this.registerHandler(Incoming.PlacePostItEvent, PlacePostItEvent.class);
        this.registerHandler(Incoming.GetItemDataEvent, GetItemDataEvent.class);
        this.registerHandler(Incoming.SetItemDataEvent, SetItemDataEvent.class);
        this.registerHandler(Incoming.RemoveItemEvent, RemoveItemEvent.class);
        this.registerHandler(Incoming.RoomDimmerSavePresetEvent, RoomDimmerSavePresetEvent.class);
        this.registerHandler(Incoming.RentableSpaceRentEvent, RentableSpaceRentEvent.class);
        this.registerHandler(Incoming.RentableSpaceCancelRentEvent, RentableSpaceCancelRentEvent.class);
        this.registerHandler(Incoming.UpdateHomeRoomEvent, UpdateHomeRoomEvent.class);
        this.registerHandler(Incoming.PassCarryItemEvent, PassCarryItemEvent.class);
        this.registerHandler(Incoming.MuteAllInRoomEvent, MuteAllInRoomEvent.class);
        this.registerHandler(Incoming.GetCustomRoomFilterEvent, GetCustomRoomFilterEvent.class);
        this.registerHandler(Incoming.UpdateRoomFilterEvent, UpdateRoomFilterEvent.class);
        this.registerHandler(Incoming.SubmitRoomToCompetitionEvent, SubmitRoomToCompetitionEvent.class);
        this.registerHandler(Incoming.GetBannedUsersFromRoomEvent, GetBannedUsersFromRoomEvent.class);
        this.registerHandler(Incoming.GetOfficialSongIdEvent, GetOfficialSongIdEvent.class);
        this.registerHandler(Incoming.GetSongInfoEvent, GetSongInfoEvent.class);
        this.registerHandler(Incoming.AddJukeboxDiskEvent, AddJukeboxDiskEvent.class);
        this.registerHandler(Incoming.RemoveJukeboxDiskEvent, RemoveJukeboxDiskEvent.class);
        this.registerHandler(Incoming.GetNowPlayingEvent, GetNowPlayingEvent.class);
        this.registerHandler(Incoming.JukeBoxEventOne, JukeBoxEventOne.class); // this doesnt even exist in the swf src for this prod.
        this.registerHandler(Incoming.GetJukeboxPlayListEvent, GetJukeboxPlayListEvent.class);
        this.registerHandler(Incoming.AddSpamWallPostItEvent, AddSpamWallPostItEvent.class);
        this.registerHandler(Incoming.GetRoomAdPurchaseInfoEvent, GetRoomAdPurchaseInfoEvent.class);
        this.registerHandler(Incoming.PurchaseRoomAdEvent, PurchaseRoomAdEvent.class);
        this.registerHandler(Incoming.EditEventEvent, EditEventEvent.class);
        this.registerHandler(Incoming.IgnoreUserEvent, IgnoreUserEvent.class);
        this.registerHandler(Incoming.UnignoreUserEvent, UnignoreUserEvent.class);
        this.registerHandler(Incoming.RoomUserMuteEvent, RoomUserMuteEvent.class);
        this.registerHandler(Incoming.BanUserWithDurationEvent, BanUserWithDurationEvent.class);
        this.registerHandler(Incoming.UnbanUserFromRoomEvent, UnbanUserFromRoomEvent.class);
        this.registerHandler(Incoming.GetUserTagsEvent, GetUserTagsEvent.class);
        this.registerHandler(Incoming.GetYoutubeDisplayStatusEvent, GetYoutubeDisplayStatusEvent.class);
        this.registerHandler(Incoming.ControlYoutubeDisplayPlaybackEvent, ControlYoutubeDisplayPlaybackEvent.class);
        this.registerHandler(Incoming.SetYoutubeDisplayPlaylistEvent, SetYoutubeDisplayPlaylistEvent.class);
        this.registerHandler(Incoming.AddFavouriteRoomEvent, AddFavouriteRoomEvent.class);
        this.registerHandler(Incoming.FriendFurniConfirmLockEvent, FriendFurniConfirmLockEvent.class);
        this.registerHandler(Incoming.DeleteFavouriteRoomEvent, DeleteFavouriteRoomEvent.class);
        this.registerHandler(Incoming.SetRandomStateEvent, SetRandomStateEvent.class);
    }

    void registerPolls() throws Exception {
        this.registerHandler(Incoming.PollRejectEvent, PollRejectEvent.class);
        this.registerHandler(Incoming.PollStartEvent, PollStartEvent.class);
        this.registerHandler(Incoming.AnswerPollEvent, AnswerPollEvent.class);
    }

    void registerModTool() throws Exception {
        this.registerHandler(Incoming.GetModeratorRoomInfoEvent, GetModeratorRoomInfoEvent.class);
        this.registerHandler(Incoming.GetRoomChatlogEvent, GetRoomChatlogEvent.class);
        this.registerHandler(Incoming.GetModeratorUserInfoEvent, GetModeratorUserInfoEvent.class);
        this.registerHandler(Incoming.PickIssuesEvent, PickIssuesEvent.class);
        this.registerHandler(Incoming.CloseIssuesEvent, CloseIssuesEvent.class);
        this.registerHandler(Incoming.ReleaseIssuesEvent, ReleaseIssuesEvent.class);
        this.registerHandler(Incoming.ModMessageEvent, ModMessageEvent.class);
        this.registerHandler(Incoming.ModToolWarnEvent, ModToolWarnEvent.class);
        this.registerHandler(Incoming.ModKickEvent, ModKickEvent.class);
        this.registerHandler(Incoming.ModeratorActionEvent, ModeratorActionEvent.class);
        this.registerHandler(Incoming.ModerateRoomEvent, ModerateRoomEvent.class);
        this.registerHandler(Incoming.GetRoomVisitsEvent, GetRoomVisitsEvent.class);
        this.registerHandler(Incoming.GetCfhChatlogEvent, GetCfhChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomUserChatlogEvent, ModToolRequestRoomUserChatlogEvent.class);
        this.registerHandler(Incoming.GetUserChatlogEvent, GetUserChatlogEvent.class);
        this.registerHandler(Incoming.ModAlertEvent, ModAlertEvent.class);
        this.registerHandler(Incoming.ModMuteEvent, ModMuteEvent.class);
        this.registerHandler(Incoming.ModBanEvent, ModBanEvent.class);
        this.registerHandler(Incoming.ModTradingLockEvent, ModTradingLockEvent.class);
        this.registerHandler(Incoming.ModToolSanctionEvent, ModToolSanctionEvent.class);
        this.registerHandler(Incoming.CloseIssueDefaultActionEvent, CloseIssueDefaultActionEvent.class);

        this.registerHandler(Incoming.GetPendingCallsForHelpEvent, GetPendingCallsForHelpEvent.class);
        this.registerHandler(Incoming.GetGuideReportingStatusEvent, GetGuideReportingStatusEvent.class);
        this.registerHandler(Incoming.ChatReviewSessionCreateEvent, ChatReviewSessionCreateEvent.class);
        this.registerHandler(Incoming.CallForHelpEvent, CallForHelpEvent.class);
        this.registerHandler(Incoming.CallForHelpFromIMEvent, CallForHelpFromIMEvent.class);
        this.registerHandler(Incoming.CallForHelpFromForumThreadEvent, CallForHelpFromForumThreadEvent.class);
        this.registerHandler(Incoming.CallForHelpFromForumMessageEvent, CallForHelpFromForumMessageEvent.class);
        this.registerHandler(Incoming.CallForHelpFromPhotoEvent, CallForHelpFromPhotoEvent.class);
    }

    void registerTrading() throws Exception {
        this.registerHandler(Incoming.OpenTradingEvent, OpenTradingEvent.class);
        this.registerHandler(Incoming.AddItemToTradeEvent, AddItemToTradeEvent.class);
        this.registerHandler(Incoming.AddItemsToTradeEvent, AddItemsToTradeEvent.class);
        this.registerHandler(Incoming.RemoveItemFromTradeEvent, RemoveItemFromTradeEvent.class);
        this.registerHandler(Incoming.AcceptTradingEvent, AcceptTradingEvent.class);
        this.registerHandler(Incoming.UnacceptTradingEvent, UnacceptTradingEvent.class);
        this.registerHandler(Incoming.ConfirmAcceptTradingEvent, ConfirmAcceptTradingEvent.class);
        this.registerHandler(Incoming.CloseTradingEvent, CloseTradingEvent.class);
        this.registerHandler(Incoming.ConfirmDeclineTradingEvent, ConfirmDeclineTradingEvent.class);
    }

    void registerGuilds() throws Exception {
        this.registerHandler(Incoming.GetGuildCreationInfoEvent, GetGuildCreationInfoEvent.class);
        this.registerHandler(Incoming.GetGuildEditorDataEvent, GetGuildEditorDataEvent.class);
        this.registerHandler(Incoming.CreateGuildEvent, CreateGuildEvent.class);
        this.registerHandler(Incoming.GetHabboGroupDetailsEvent, GetHabboGroupDetailsEvent.class);
        this.registerHandler(Incoming.GetGuildEditInfoEvent, GetGuildEditInfoEvent.class);
        this.registerHandler(Incoming.GetGuildMembersEvent, GetGuildMembersEvent.class);
        this.registerHandler(Incoming.JoinHabboGroupEvent, JoinHabboGroupEvent.class);
        this.registerHandler(Incoming.UpdateGuildIdentityEvent, UpdateGuildIdentityEvent.class);
        this.registerHandler(Incoming.UpdateGuildBadgeEvent, UpdateGuildBadgeEvent.class);
        this.registerHandler(Incoming.UpdateGuildColorsEvent, UpdateGuildColorsEvent.class);
        this.registerHandler(Incoming.RemoveAdminRightsFromMemberEvent, RemoveAdminRightsFromMemberEvent.class);
        this.registerHandler(Incoming.KickMemberEvent, KickMemberEvent.class);
        this.registerHandler(Incoming.UpdateGuildSettingsEvent, UpdateGuildSettingsEvent.class);
        this.registerHandler(Incoming.ApproveMembershipRequestEvent, ApproveMembershipRequestEvent.class);
        this.registerHandler(Incoming.RejectMembershipRequestEvent, RejectMembershipRequestEvent.class);
        this.registerHandler(Incoming.AddAdminRightsToMemberEvent, AddAdminRightsToMemberEvent.class);
        this.registerHandler(Incoming.SelectFavouriteHabboGroupEvent, SelectFavouriteHabboGroupEvent.class);
        this.registerHandler(Incoming.GetGuildMembershipsEvent, GetGuildMembershipsEvent.class);
        this.registerHandler(Incoming.GetGuildFurniContextMenuInfoEvent, GetGuildFurniContextMenuInfoEvent.class);
        this.registerHandler(Incoming.GetMemberGuildItemCountEvent, GetMemberGuildItemCountEvent.class);
        this.registerHandler(Incoming.DeselectFavouriteHabboGroupEvent, DeselectFavouriteHabboGroupEvent.class);
        this.registerHandler(Incoming.DeactivateGuildEvent, DeactivateGuildEvent.class);
        this.registerHandler(Incoming.GetForumsListEvent, GetForumsListEvent.class);
        this.registerHandler(Incoming.GetThreadsEvent, GetThreadsEvent.class);
        this.registerHandler(Incoming.GetForumStatsEvent, GetForumStatsEvent.class);
        this.registerHandler(Incoming.PostMessageEvent, PostMessageEvent.class);
        this.registerHandler(Incoming.UpdateForumSettingsEvent, UpdateForumSettingsEvent.class);
        this.registerHandler(Incoming.GetMessagesEvent, GetMessagesEvent.class);
        this.registerHandler(Incoming.ModerateMessageEvent, ModerateMessageEvent.class);
        this.registerHandler(Incoming.ModerateThreadEvent, ModerateThreadEvent.class);
        this.registerHandler(Incoming.UpdateThreadEvent, UpdateThreadEvent.class);
        this.registerHandler(Incoming.GetHabboGroupBadgesEvent, GetHabboGroupBadgesEvent.class);

//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumModerateMessageEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumModerateThreadEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumPostThreadEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumThreadsEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumThreadsMessagesEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumUpdateSettingsEvent.class);
    }

    void registerPets() throws Exception {
        this.registerHandler(Incoming.GetPetInfoEvent, GetPetInfoEvent.class);
        this.registerHandler(Incoming.RemovePetFromFlatEvent, RemovePetFromFlatEvent.class);
        this.registerHandler(Incoming.RespectPetEvent, RespectPetEvent.class);
        this.registerHandler(Incoming.GetPetCommandsEvent, GetPetCommandsEvent.class);
        this.registerHandler(Incoming.CustomizePetWithFurniEvent, CustomizePetWithFurniEvent.class);
        this.registerHandler(Incoming.TogglePetRidingPermissionEvent, TogglePetRidingPermissionEvent.class);
        this.registerHandler(Incoming.MountPetEvent, MountPetEvent.class);
        this.registerHandler(Incoming.RemoveSaddleFromPetEvent, RemoveSaddleFromPetEvent.class);
        this.registerHandler(Incoming.TogglePetBreedingPermissionEvent, TogglePetBreedingPermissionEvent.class);
        this.registerHandler(Incoming.CompostPlantEvent, CompostPlantEvent.class);
        this.registerHandler(Incoming.BreedPetsEvent, BreedPetsEvent.class);
        this.registerHandler(Incoming.MovePetEvent, MovePetEvent.class);
        this.registerHandler(Incoming.OpenPetPackageEvent, OpenPetPackageEvent.class);
        this.registerHandler(Incoming.CancelPetBreedingEvent, CancelPetBreedingEvent.class);
        this.registerHandler(Incoming.ConfirmPetBreedingEvent, ConfirmPetBreedingEvent.class);
    }

    void registerWired() throws Exception {
        this.registerHandler(Incoming.UpdateTriggerEvent, UpdateTriggerEvent.class);
        this.registerHandler(Incoming.UpdateActionEvent, UpdateActionEvent.class);
        this.registerHandler(Incoming.UpdateConditionEvent, UpdateConditionEvent.class);
        this.registerHandler(Incoming.ApplySnapshotEvent, ApplySnapshotEvent.class);
    }

    void registerUnknown() throws Exception {
        this.registerHandler(Incoming.GetResolutionAchievementsEvent, GetResolutionAchievementsEvent.class);
        this.registerHandler(Incoming.GetTalentTrackEvent, GetTalentTrackEvent.class);
        this.registerHandler(Incoming.GetBadgePointLimitsEvent, GetBadgePointLimitsEvent.class);
        this.registerHandler(Incoming.GetCfhStatusEvent, GetCfhStatusEvent.class);
    }

    void registerFloorPlanEditor() throws Exception {
        this.registerHandler(Incoming.UpdateFloorPropertiesEvent, UpdateFloorPropertiesEvent.class);
        this.registerHandler(Incoming.GetOccupiedTilesEvent, GetOccupiedTilesEvent.class);
        this.registerHandler(Incoming.GetRoomEntryTileEvent, GetRoomEntryTileEvent.class);
    }

    void registerAchievements() throws Exception {
        this.registerHandler(Incoming.GetAchievementsEvent, GetAchievementsEvent.class);
        this.registerHandler(Incoming.RequestAchievementConfigurationEvent, RequestAchievementConfigurationEvent.class);
    }

    void registerGuides() throws Exception {
        this.registerHandler(Incoming.GuideSessionOnDutyUpdateEvent, GuideSessionOnDutyUpdateEvent.class);
        this.registerHandler(Incoming.GuideSessionCreateEvent, GuideSessionCreateEvent.class);
        this.registerHandler(Incoming.GuideSessionIsTypingEvent, GuideSessionIsTypingEvent.class);
        this.registerHandler(Incoming.GuideSessionReportEvent, GuideSessionReportEvent.class);
        this.registerHandler(Incoming.GuideSessionFeedbackEvent, GuideSessionFeedbackEvent.class);
        this.registerHandler(Incoming.GuideSessionMessageEvent, GuideSessionMessageEvent.class);
        this.registerHandler(Incoming.GuideSessionRequesterCancelsEvent, GuideSessionRequesterCancelsEvent.class);
        this.registerHandler(Incoming.GuideSessionGuideDecidesEvent, GuideSessionGuideDecidesEvent.class);
        this.registerHandler(Incoming.GuideSessionInviteRequesterEvent, GuideSessionInviteRequesterEvent.class);
        this.registerHandler(Incoming.GuideSessionGetRequesterRoomEvent, GuideSessionGetRequesterRoomEvent.class);
        this.registerHandler(Incoming.GuideSessionResolvedEvent, GuideSessionResolvedEvent.class);

        this.registerHandler(Incoming.ChatReviewGuideDetachedEvent, ChatReviewGuideDetachedEvent.class);
        this.registerHandler(Incoming.ChatReviewGuideDecidesOnOfferEvent, ChatReviewGuideDecidesOnOfferEvent.class);
        this.registerHandler(Incoming.ChatReviewGuideVoteEvent, ChatReviewGuideVoteEvent.class);
    }

    void registerCrafting() throws Exception {
        this.registerHandler(Incoming.GetCraftingRecipeEvent, GetCraftingRecipeEvent.class);
        this.registerHandler(Incoming.GetCraftableProductsEvent, GetCraftableProductsEvent.class);
        this.registerHandler(Incoming.CraftEvent, CraftEvent.class);
        this.registerHandler(Incoming.CraftSecretEvent, CraftSecretEvent.class);
        this.registerHandler(Incoming.GetCraftingRecipesAvailableEvent, GetCraftingRecipesAvailableEvent.class);
    }

    void registerCamera() throws Exception {
        this.registerHandler(Incoming.RenderRoomEvent, RenderRoomEvent.class);
        this.registerHandler(Incoming.RequestCameraConfigurationEvent, RequestCameraConfigurationEvent.class);
        this.registerHandler(Incoming.PurchasePhotoEvent, PurchasePhotoEvent.class);
        this.registerHandler(Incoming.RenderRoomThumbnailEvent, RenderRoomThumbnailEvent.class);
        this.registerHandler(Incoming.PublishPhotoEvent, PublishPhotoEvent.class);
    }

    void registerGameCenter() throws Exception {
        this.registerHandler(Incoming.GetGameListEvent, GetGameListEvent.class);
        this.registerHandler(Incoming.GetGameStatusEvent, GetGameStatusEvent.class);
        this.registerHandler(Incoming.JoinQueueEvent, JoinQueueEvent.class);
        this.registerHandler(Incoming.GetWeeklyGameRewardWinnersEvent, GetWeeklyGameRewardWinnersEvent.class);
        this.registerHandler(Incoming.GameUnloadedEvent, GameUnloadedEvent.class);
        this.registerHandler(Incoming.GetWeeklyGameRewardEvent, GetWeeklyGameRewardEvent.class);
        this.registerHandler(Incoming.Game2GetAccountGameStatusEvent, Game2GetAccountGameStatusEvent.class);
    }
}