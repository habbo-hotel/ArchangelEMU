package com.eu.habbo.messages;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.incoming.Incoming;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.incoming.achievements.RequestAchievementConfigurationEvent;
import com.eu.habbo.messages.incoming.achievements.RequestAchievementsEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorAlertCommandEvent;
import com.eu.habbo.messages.incoming.ambassadors.AmbassadorVisitCommandEvent;
import com.eu.habbo.messages.incoming.camera.*;
import com.eu.habbo.messages.incoming.catalog.*;
import com.eu.habbo.messages.incoming.catalog.marketplace.*;
import com.eu.habbo.messages.incoming.catalog.recycler.OpenRecycleBoxEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RecycleEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.ReloadRecyclerEvent;
import com.eu.habbo.messages.incoming.catalog.recycler.RequestRecyclerLogicEvent;
import com.eu.habbo.messages.incoming.crafting.*;
import com.eu.habbo.messages.incoming.events.calendar.AdventCalendarForceOpenEvent;
import com.eu.habbo.messages.incoming.events.calendar.AdventCalendarOpenDayEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorRequestBlockedTilesEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorRequestDoorSettingsEvent;
import com.eu.habbo.messages.incoming.floorplaneditor.FloorPlanEditorSaveEvent;
import com.eu.habbo.messages.incoming.friends.*;
import com.eu.habbo.messages.incoming.gamecenter.*;
import com.eu.habbo.messages.incoming.guardians.GuardianAcceptRequestEvent;
import com.eu.habbo.messages.incoming.guardians.GuardianNoUpdatesWantedEvent;
import com.eu.habbo.messages.incoming.guardians.GuardianVoteEvent;
import com.eu.habbo.messages.incoming.guides.*;
import com.eu.habbo.messages.incoming.guilds.*;
import com.eu.habbo.messages.incoming.guilds.forums.*;
import com.eu.habbo.messages.incoming.handshake.*;
import com.eu.habbo.messages.incoming.helper.MySanctionStatusEvent;
import com.eu.habbo.messages.incoming.helper.RequestTalentTrackEvent;
import com.eu.habbo.messages.incoming.hotelview.*;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryBadgesEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryBotsEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryItemsEvent;
import com.eu.habbo.messages.incoming.inventory.RequestInventoryPetsEvent;
import com.eu.habbo.messages.incoming.modtool.*;
import com.eu.habbo.messages.incoming.navigator.*;
import com.eu.habbo.messages.incoming.polls.AnswerPollEvent;
import com.eu.habbo.messages.incoming.polls.CancelPollEvent;
import com.eu.habbo.messages.incoming.polls.GetPollDataEvent;
import com.eu.habbo.messages.incoming.rooms.*;
import com.eu.habbo.messages.incoming.rooms.bots.BotPickupEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotPlaceEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotSaveSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.bots.BotSettingsEvent;
import com.eu.habbo.messages.incoming.rooms.items.*;
import com.eu.habbo.messages.incoming.rooms.items.jukebox.*;
import com.eu.habbo.messages.incoming.rooms.items.lovelock.LoveLockStartConfirmEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentSpaceCancelEvent;
import com.eu.habbo.messages.incoming.rooms.items.rentablespace.RentSpaceEvent;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestPlaylistChange;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestPlaylists;
import com.eu.habbo.messages.incoming.rooms.items.youtube.YoutubeRequestStateChange;
import com.eu.habbo.messages.incoming.rooms.pets.*;
import com.eu.habbo.messages.incoming.rooms.promotions.BuyRoomPromotionEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.RequestPromotionRoomsEvent;
import com.eu.habbo.messages.incoming.rooms.promotions.UpdateRoomPromotionEvent;
import com.eu.habbo.messages.incoming.rooms.users.*;
import com.eu.habbo.messages.incoming.trading.*;
import com.eu.habbo.messages.incoming.unknown.RequestResolutionEvent;
import com.eu.habbo.messages.incoming.inventory.GetBadgePointLimitsEvent;
import com.eu.habbo.messages.incoming.users.*;
import com.eu.habbo.messages.incoming.wired.WiredApplySetConditionsEvent;
import com.eu.habbo.messages.incoming.wired.WiredConditionSaveDataEvent;
import com.eu.habbo.messages.incoming.wired.WiredEffectSaveDataEvent;
import com.eu.habbo.messages.incoming.wired.WiredTriggerSaveDataEvent;
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
        this.registerHandler(Incoming.AmbassadorAlertEvent, AmbassadorAlertCommandEvent.class);
        this.registerHandler(Incoming.FollowFriendEvent, AmbassadorVisitCommandEvent.class);
    }

    private void registerCatalog() throws Exception {
        this.registerHandler(Incoming.RequestRecylerLogicEvent, RequestRecyclerLogicEvent.class);
        this.registerHandler(Incoming.GetBundleDiscountRulesetEvent, RequestDiscountEvent.class);
        this.registerHandler(Incoming.RequestGiftConfigurationEvent, RequestGiftConfigurationEvent.class);
        this.registerHandler(Incoming.GetMarketplaceConfigurationEvent, RequestMarketplaceConfigEvent.class);
        this.registerHandler(Incoming.GetCatalogIndexEvent, RequestCatalogModeEvent.class);
        this.registerHandler(Incoming.BuildersClubQueryFurniCountEvent, RequestCatalogIndexEvent.class);
        this.registerHandler(Incoming.RequestCatalogPageEvent, RequestCatalogPageEvent.class);
        this.registerHandler(Incoming.PurchaseFromCatalogAsGiftEvent, CatalogBuyItemAsGiftEvent.class);
        this.registerHandler(Incoming.CatalogBuyItemEvent, CatalogBuyItemEvent.class);
        this.registerHandler(Incoming.RedeemVoucherEvent, RedeemVoucherEvent.class);
        this.registerHandler(Incoming.GetRecyclerStatusEvent, ReloadRecyclerEvent.class);
        this.registerHandler(Incoming.RecycleItemsEvent, RecycleEvent.class);
        this.registerHandler(Incoming.OpenRecycleBoxEvent, OpenRecycleBoxEvent.class);
        this.registerHandler(Incoming.GetMarketplaceOwnOffersEvent, RequestOwnItemsEvent.class);
        this.registerHandler(Incoming.TakeBackItemEvent, TakeBackItemEvent.class);
        this.registerHandler(Incoming.GetMarketplaceOffersEvent, RequestOffersEvent.class);
        this.registerHandler(Incoming.RequestItemInfoEvent, RequestItemInfoEvent.class);
        this.registerHandler(Incoming.BuyMarketplaceOfferEvent, BuyItemEvent.class);
        this.registerHandler(Incoming.RequestSellItemEvent, RequestSellItemEvent.class);
        this.registerHandler(Incoming.SellItemEvent, SellItemEvent.class);
        this.registerHandler(Incoming.RedeemMarketplaceOfferCreditsEvent, RequestCreditsEvent.class);
        this.registerHandler(Incoming.GetSellablePetPalettesEvent, RequestPetBreedsEvent.class);
        this.registerHandler(Incoming.ApproveNameEvent, CheckPetNameEvent.class);
        this.registerHandler(Incoming.GetClubDataEvent, RequestClubDataEvent.class);
        this.registerHandler(Incoming.RequestClubGiftsEvent, RequestClubGiftsEvent.class);
        this.registerHandler(Incoming.GetProductOfferEvent, CatalogSearchedItemEvent.class);
        this.registerHandler(Incoming.PurchaseTargetedOfferEvent, PurchaseTargetOfferEvent.class);
        this.registerHandler(Incoming.SetTargetedOfferStateEvent, TargetOfferStateEvent.class);
        this.registerHandler(Incoming.SelectClubGiftEvent, CatalogSelectClubGiftEvent.class);
        this.registerHandler(Incoming.RequestClubCenterEvent, RequestClubCenterEvent.class);
        this.registerHandler(Incoming.GetHabboClubExtendOfferEvent, CatalogRequestClubDiscountEvent.class);
        this.registerHandler(Incoming.CatalogBuyClubDiscountEvent, CatalogBuyClubDiscountEvent.class);
    }

    private void registerEvent() throws Exception {
        this.registerHandler(Incoming.OpenCampaignCalendarDoorAsStaffEvent, AdventCalendarOpenDayEvent.class);
        this.registerHandler(Incoming.AdventCalendarForceOpenEvent, AdventCalendarForceOpenEvent.class);
    }

    private void registerHandshake() throws Exception {
        this.registerHandler(Incoming.ReleaseVersionEvent, ReleaseVersionEvent.class);
        this.registerHandler(Incoming.InitDiffieHandshakeEvent, InitDiffieHandshakeEvent.class);
        this.registerHandler(Incoming.CompleteDiffieHandshake, CompleteDiffieHandshakeEvent.class);
        this.registerHandler(Incoming.SSOTicketEvent, SecureLoginEvent.class);
        this.registerHandler(Incoming.UniqueIDEvent, MachineIDEvent.class);
        this.registerHandler(Incoming.GetIgnoredUsersEvent, GetIgnoredUsersEvent.class);
        this.registerHandler(Incoming.LatencyPingRequestEvent, PingEvent.class);
    }

    private void registerFriends() throws Exception {
        this.registerHandler(Incoming.GetMOTDEvent, RequestFriendsEvent.class);
        this.registerHandler(Incoming.ChangeRelationEvent, ChangeRelationEvent.class);
        this.registerHandler(Incoming.RemoveFriendEvent, RemoveFriendEvent.class);
        this.registerHandler(Incoming.HabboSearchEvent, SearchUserEvent.class);
        this.registerHandler(Incoming.RequestFriendEvent, FriendRequestEvent.class);
        this.registerHandler(Incoming.AcceptFriendEvent, AcceptFriendRequestEvent.class);
        this.registerHandler(Incoming.DeclineFriendEvent, DeclineFriendRequestEvent.class);
        this.registerHandler(Incoming.FriendPrivateMessageEvent, FriendPrivateMessageEvent.class);
        this.registerHandler(Incoming.GetFriendRequestsEvent, RequestFriendRequestsEvent.class);
        this.registerHandler(Incoming.StalkFriendEvent, StalkFriendEvent.class);
        this.registerHandler(Incoming.MessengerInitEvent, RequestInitFriendsEvent.class);
        this.registerHandler(Incoming.FindNewFriendsEvent, FindNewFriendsEvent.class);
        this.registerHandler(Incoming.SendRoomInviteEvent, InviteFriendsEvent.class);
    }

    private void registerUsers() throws Exception {
        this.registerHandler(Incoming.RequestUserDataEvent, RequestUserDataEvent.class);
        this.registerHandler(Incoming.GetCreditsInfoEvent, RequestUserCreditsEvent.class);
        this.registerHandler(Incoming.ScrGetUserInfoEvent, RequestUserClubEvent.class);
        this.registerHandler(Incoming.GetSoundSettingsEvent, RequestMeMenuSettingsEvent.class);
        this.registerHandler(Incoming.GetTalentTrackLevelEvent, RequestUserCitizinShipEvent.class);
        this.registerHandler(Incoming.GetExtendedProfileEvent, RequestUserProfileEvent.class);
        this.registerHandler(Incoming.GetRelationshipStatusInfoEvent, RequestProfileFriendsEvent.class);
        this.registerHandler(Incoming.GetWardrobeEvent, RequestUserWardrobeEvent.class);
        this.registerHandler(Incoming.SaveWardrobeEvent, SaveWardrobeEvent.class);
        this.registerHandler(Incoming.ChangeMottoEvent, SaveMottoEvent.class);
        this.registerHandler(Incoming.UpdateFigureDataEvent, UserSaveLookEvent.class);
        this.registerHandler(Incoming.UserWearBadgeEvent, UserWearBadgeEvent.class);
        this.registerHandler(Incoming.GetSelectedBadgesEvent, RequestWearingBadgesEvent.class);
        this.registerHandler(Incoming.SetSoundSettingsEvent, SaveUserVolumesEvent.class);
        this.registerHandler(Incoming.SetRoomCameraPreferencesEvent, SaveBlockCameraFollowEvent.class);
        this.registerHandler(Incoming.SetIgnoreRoomInvitesEvent, SaveIgnoreRoomInvitesEvent.class);
        this.registerHandler(Incoming.SetChatPreferencesEvent, SavePreferOldChatEvent.class);
        this.registerHandler(Incoming.AvatarEffectActivatedEvent, ActivateEffectEvent.class);
        this.registerHandler(Incoming.AvatarEffectSelectedEvent, EnableEffectEvent.class);
        this.registerHandler(Incoming.EventLogEvent, UserActivityEvent.class);
        this.registerHandler(Incoming.NewUserExperienceScriptProceedEvent, UserNuxEvent.class);
        this.registerHandler(Incoming.NewUserExperienceGetGiftsEvent, PickNewUserGiftEvent.class);
        this.registerHandler(Incoming.CheckUserNameEvent, ChangeNameCheckUsernameEvent.class);
        this.registerHandler(Incoming.ChangeUserNameEvent, ConfirmChangeNameEvent.class);
        this.registerHandler(Incoming.SetChatStylePreferenceEvent, ChangeChatBubbleEvent.class);
        this.registerHandler(Incoming.UpdateUIFlagsEvent, UpdateUIFlagsEvent.class);
    }

    private void registerNavigator() throws Exception {
        this.registerHandler(Incoming.GetUserFlatCatsEvent, RequestRoomCategoriesEvent.class);
        this.registerHandler(Incoming.PopularRoomsSearchEvent, RequestPopularRoomsEvent.class);
        this.registerHandler(Incoming.RoomsWithHighestScoreSearchEvent, RequestHighestScoreRoomsEvent.class);
        this.registerHandler(Incoming.MyRoomsSearchEvent, RequestMyRoomsEvent.class);
        this.registerHandler(Incoming.CanCreateRoomEvent, RequestCanCreateRoomEvent.class);
        this.registerHandler(Incoming.GetUnreadForumsCountEvent, RequestPromotedRoomsEvent.class);
        this.registerHandler(Incoming.CreateFlatEvent, RequestCreateRoomEvent.class);
        this.registerHandler(Incoming.RequestTagsEvent, RequestTagsEvent.class);
        this.registerHandler(Incoming.SearchRoomsByTagEvent, SearchRoomsByTagEvent.class);
        this.registerHandler(Incoming.SearchRoomsEvent, SearchRoomsEvent.class);
        this.registerHandler(Incoming.RoomsWhereMyFriendsAreSearchEvent, SearchRoomsFriendsNowEvent.class);
        this.registerHandler(Incoming.MyFriendsRoomsSearchEvent, SearchRoomsFriendsOwnEvent.class);
        this.registerHandler(Incoming.MyRoomRightsSearchEvent, SearchRoomsWithRightsEvent.class);
        this.registerHandler(Incoming.SearchRoomsInGroupEvent, SearchRoomsInGroupEvent.class);
        this.registerHandler(Incoming.MyFavouriteRoomsSearchEvent, SearchRoomsMyFavouriteEvent.class);
        this.registerHandler(Incoming.MyRoomHistorySearchEvent, SearchRoomsVisitedEvent.class);
        this.registerHandler(Incoming.NewNavigatorInitEvent, RequestNewNavigatorDataEvent.class);
        this.registerHandler(Incoming.NewNavigatorSearchEvent, RequestNewNavigatorRoomsEvent.class);
        this.registerHandler(Incoming.ForwardToSomeRoomEvent, NewNavigatorActionEvent.class);
        this.registerHandler(Incoming.GetUserEventCatsEvent, RequestNavigatorSettingsEvent.class);
        this.registerHandler(Incoming.SetNewNavigatorWindowPreferencesEvent, SaveWindowSettingsEvent.class);
        this.registerHandler(Incoming.RequestDeleteRoomEvent, RequestDeleteRoomEvent.class);
        this.registerHandler(Incoming.NavigatorSetSearchCodeViewModeEvent, NavigatorCategoryListModeEvent.class);
        this.registerHandler(Incoming.NavigatorAddCollapsedCategoryEvent, NavigatorCollapseCategoryEvent.class);
        this.registerHandler(Incoming.NavigatorUncollapseCategoryEvent, NavigatorUncollapseCategoryEvent.class);
        this.registerHandler(Incoming.NavigatorAddSavedSearchEvent, AddSavedSearchEvent.class);
        this.registerHandler(Incoming.NavigatorDeleteSavedSearchEvent, DeleteSavedSearchEvent.class);
    }

    private void registerHotelview() throws Exception {
        this.registerHandler(Incoming.QuitEvent, HotelViewEvent.class);
        this.registerHandler(Incoming.HotelViewRequestBonusRareEvent, HotelViewRequestBonusRareEvent.class);
        this.registerHandler(Incoming.GetPromoArticlesEvent, RequestNewsListEvent.class);
        this.registerHandler(Incoming.GetCurrentTimingCodeEvent, HotelViewDataEvent.class);
        this.registerHandler(Incoming.HotelViewRequestBadgeRewardEvent, HotelViewRequestBadgeRewardEvent.class);
        this.registerHandler(Incoming.HotelViewClaimBadgeRewardEvent, HotelViewClaimBadgeRewardEvent.class);
        this.registerHandler(Incoming.HotelViewRequestLTDAvailabilityEvent, HotelViewRequestLTDAvailabilityEvent.class);
        this.registerHandler(Incoming.HotelViewRequestSecondsUntilEvent, HotelViewRequestSecondsUntilEvent.class);
    }

    private void registerInventory() throws Exception {
        this.registerHandler(Incoming.GetBadgesEvent, RequestInventoryBadgesEvent.class);
        this.registerHandler(Incoming.RequestInventoryBotsEvent, RequestInventoryBotsEvent.class);
        this.registerHandler(Incoming.RequestFurniInventoryEvent, RequestInventoryItemsEvent.class);
        this.registerHandler(Incoming.HotelViewInventoryEvent, RequestInventoryItemsEvent.class);
        this.registerHandler(Incoming.GetPetInventoryEvent, RequestInventoryPetsEvent.class);
    }

    void registerRooms() throws Exception {
        this.registerHandler(Incoming.OpenFlatConnectionEvent, RequestRoomLoadEvent.class);
        this.registerHandler(Incoming.RequestHeightmapEvent, RequestRoomHeightmapEvent.class);
        this.registerHandler(Incoming.GetRoomEntryDataEvent, RequestRoomHeightmapEvent.class);
        this.registerHandler(Incoming.RoomVoteEvent, RoomVoteEvent.class);
        this.registerHandler(Incoming.GetGuestRoomEvent, RequestRoomDataEvent.class);
        this.registerHandler(Incoming.SaveRoomSettingsEvent, RoomSettingsSaveEvent.class);
        this.registerHandler(Incoming.PlaceObjectEvent, RoomPlaceItemEvent.class);
        this.registerHandler(Incoming.MoveObjectEvent, RotateMoveItemEvent.class);
        this.registerHandler(Incoming.MoveWallItemEvent, MoveWallItemEvent.class);
        this.registerHandler(Incoming.RoomPickupItemEvent, RoomPickupItemEvent.class);
        this.registerHandler(Incoming.RoomPlacePaintEvent, RoomPlacePaintEvent.class);
        this.registerHandler(Incoming.StartTypingEvent, RoomUserStartTypingEvent.class);
        this.registerHandler(Incoming.CancelTypingEvent, RoomUserStopTypingEvent.class);
        this.registerHandler(Incoming.ToggleFloorItemEvent, ToggleFloorItemEvent.class);
        this.registerHandler(Incoming.UseWallItemEvent, ToggleWallItemEvent.class);
        this.registerHandler(Incoming.SetRoomBackgroundColorDataEvent, RoomBackgroundEvent.class);
        this.registerHandler(Incoming.SetMannequinNameEvent, MannequinSaveNameEvent.class);
        this.registerHandler(Incoming.SetMannequinFigureEvent, MannequinSaveLookEvent.class);
        this.registerHandler(Incoming.FootballGateSaveLookEvent, FootballGateSaveLookEvent.class);
        this.registerHandler(Incoming.AdvertisingSaveEvent, AdvertisingSaveEvent.class);
        this.registerHandler(Incoming.GetRoomSettingsEvent, RequestRoomSettingsEvent.class);
        this.registerHandler(Incoming.RoomDimmerGetPresetsEvent, MoodLightSettingsEvent.class);
        this.registerHandler(Incoming.RoomDimmerChangeStateEvent, MoodLightTurnOnEvent.class);
        this.registerHandler(Incoming.DropCarryItemEvent, RoomUserDropHandItemEvent.class);
        this.registerHandler(Incoming.RoomUserLookAtPoint, RoomUserLookAtPoint.class);
        this.registerHandler(Incoming.ChatEvent, RoomUserTalkEvent.class);
        this.registerHandler(Incoming.ShoutEvent, RoomUserShoutEvent.class);
        this.registerHandler(Incoming.WhisperEvent, RoomUserWhisperEvent.class);
        this.registerHandler(Incoming.AvatarExpressionEvent, RoomUserActionEvent.class);
        this.registerHandler(Incoming.ChangePostureEvent, RoomUserSitEvent.class);
        this.registerHandler(Incoming.DanceEvent, RoomUserDanceEvent.class);
        this.registerHandler(Incoming.SignEvent, RoomUserSignEvent.class);
        this.registerHandler(Incoming.RoomUserWalkEvent, RoomUserWalkEvent.class);
        this.registerHandler(Incoming.RespectUserEvent, RoomUserGiveRespectEvent.class);
        this.registerHandler(Incoming.RoomUserGiveRightsEvent, RoomUserGiveRightsEvent.class);
        this.registerHandler(Incoming.RemoveOwnRoomRightsRoomEvent, RoomRemoveRightsEvent.class);
        this.registerHandler(Incoming.RequestRoomRightsEvent, RequestRoomRightsEvent.class);
        this.registerHandler(Incoming.RemoveAllRightsEvent, RoomRemoveAllRightsEvent.class);
        this.registerHandler(Incoming.RemoveRightsEvent, RoomUserRemoveRightsEvent.class);
        this.registerHandler(Incoming.PlaceBotEvent, BotPlaceEvent.class);
        this.registerHandler(Incoming.BotPickupEvent, BotPickupEvent.class);
        this.registerHandler(Incoming.CommandBotEvent, BotSaveSettingsEvent.class);
        this.registerHandler(Incoming.GetBotCommandConfigurationDataEvent, BotSettingsEvent.class);
        this.registerHandler(Incoming.ThrowDiceEvent, TriggerDiceEvent.class);
        this.registerHandler(Incoming.DiceOffEvent, CloseDiceEvent.class);
        this.registerHandler(Incoming.SpinWheelOfFortuneEvent, TriggerColorWheelEvent.class);
        this.registerHandler(Incoming.CreditFurniRedeemEvent, RedeemItemEvent.class);
        this.registerHandler(Incoming.PlacePetEvent, PetPlaceEvent.class);
        this.registerHandler(Incoming.RoomUserKickEvent, RoomUserKickEvent.class);
        this.registerHandler(Incoming.SetStackHelperHeightEvent, SetStackHelperHeightEvent.class);
        this.registerHandler(Incoming.EnterOneWayDoorEvent, TriggerOneWayGateEvent.class);
        this.registerHandler(Incoming.LetUserInEvent, HandleDoorbellEvent.class);
        this.registerHandler(Incoming.RedeemClothingEvent, RedeemClothingEvent.class);
        this.registerHandler(Incoming.PlacePostItEvent, PostItPlaceEvent.class);
        this.registerHandler(Incoming.PostItRequestDataEvent, PostItRequestDataEvent.class);
        this.registerHandler(Incoming.PostItSaveDataEvent, PostItSaveDataEvent.class);
        this.registerHandler(Incoming.PostItDeleteEvent, PostItDeleteEvent.class);
        this.registerHandler(Incoming.RoomDimmerSavePresetEvent, MoodLightSaveSettingsEvent.class);
        this.registerHandler(Incoming.RentableSpaceRentEvent, RentSpaceEvent.class);
        this.registerHandler(Incoming.RentableSpaceCancelRentEvent, RentSpaceCancelEvent.class);
        this.registerHandler(Incoming.UpdateHomeRoomEvent, SetHomeRoomEvent.class);
        this.registerHandler(Incoming.PassCarryItemEvent, RoomUserGiveHandItemEvent.class);
        this.registerHandler(Incoming.RoomMuteEvent, RoomMuteEvent.class);
        this.registerHandler(Incoming.GetCustomRoomFilterEvent, RequestRoomWordFilterEvent.class);
        this.registerHandler(Incoming.UpdateRoomFilterEvent, RoomWordFilterModifyEvent.class);
        this.registerHandler(Incoming.SubmitRoomToCompetitionEvent, RoomStaffPickEvent.class);
        this.registerHandler(Incoming.GetBannedUsersFromRoomEvent, RoomRequestBannedUsersEvent.class);
        this.registerHandler(Incoming.GetOfficialSongIdEvent, JukeBoxRequestTrackCodeEvent.class);
        this.registerHandler(Incoming.GetSongInfoEvent, JukeBoxRequestTrackDataEvent.class);
        this.registerHandler(Incoming.JukeBoxAddSoundTrackEvent, JukeBoxAddSoundTrackEvent.class);
        this.registerHandler(Incoming.RemoveJukeboxDiskEvent, JukeBoxRemoveSoundTrackEvent.class);
        this.registerHandler(Incoming.GetNowPlayingEvent, JukeBoxRequestPlayListEvent.class);
        this.registerHandler(Incoming.JukeBoxEventOne, JukeBoxEventOne.class);
        this.registerHandler(Incoming.GetJukeboxPlayListEvent, JukeBoxEventTwo.class);
        this.registerHandler(Incoming.AddSpamWallPostItEvent, SavePostItStickyPoleEvent.class);
        this.registerHandler(Incoming.GetRoomAdPurchaseInfoEvent, RequestPromotionRoomsEvent.class);
        this.registerHandler(Incoming.BuyRoomPromotionEvent, BuyRoomPromotionEvent.class);
        this.registerHandler(Incoming.EditRoomPromotionMessageEvent, UpdateRoomPromotionEvent.class);
        this.registerHandler(Incoming.IgnoreUserEvent, IgnoreRoomUserEvent.class);
        this.registerHandler(Incoming.UnignoreUserEvent, UnIgnoreRoomUserEvent.class);
        this.registerHandler(Incoming.RoomUserMuteEvent, RoomUserMuteEvent.class);
        this.registerHandler(Incoming.BanUserWithDurationEvent, RoomUserBanEvent.class);
        this.registerHandler(Incoming.UnbanRoomUserEvent, UnbanRoomUserEvent.class);
        this.registerHandler(Incoming.GetUserTagsEvent, RequestRoomUserTagsEvent.class);
        this.registerHandler(Incoming.YoutubeRequestPlaylists, YoutubeRequestPlaylists.class);
        this.registerHandler(Incoming.ControlYoutubeDisplayPlaybackEvent, YoutubeRequestStateChange.class);
        this.registerHandler(Incoming.SetYoutubeDisplayPlaylistEvent, YoutubeRequestPlaylistChange.class);
        this.registerHandler(Incoming.RoomFavoriteEvent, RoomFavoriteEvent.class);
        this.registerHandler(Incoming.LoveLockStartConfirmEvent, LoveLockStartConfirmEvent.class);
        this.registerHandler(Incoming.DeleteFavouriteRoomEvent, RoomUnFavoriteEvent.class);
        this.registerHandler(Incoming.UseRandomStateItemEvent, UseRandomStateItemEvent.class);
    }

    void registerPolls() throws Exception {
        this.registerHandler(Incoming.PollRejectEvent, CancelPollEvent.class);
        this.registerHandler(Incoming.PollStartEvent, GetPollDataEvent.class);
        this.registerHandler(Incoming.AnswerPollEvent, AnswerPollEvent.class);
    }

    void registerModTool() throws Exception {
        this.registerHandler(Incoming.ModToolRequestRoomInfoEvent, ModToolRequestRoomInfoEvent.class);
        this.registerHandler(Incoming.GetRoomChatlogEvent, ModToolRequestRoomChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestUserInfoEvent, ModToolRequestUserInfoEvent.class);
        this.registerHandler(Incoming.PickIssuesEvent, ModToolPickTicketEvent.class);
        this.registerHandler(Incoming.CloseIssuesEvent, ModToolCloseTicketEvent.class);
        this.registerHandler(Incoming.ReleaseIssuesEvent, ModToolReleaseTicketEvent.class);
        this.registerHandler(Incoming.ModMessageEvent, ModToolAlertEvent.class);
        this.registerHandler(Incoming.ModToolWarnEvent, ModToolWarnEvent.class);
        this.registerHandler(Incoming.ModKickEvent, ModToolKickEvent.class);
        this.registerHandler(Incoming.ModToolRoomAlertEvent, ModToolRoomAlertEvent.class);
        this.registerHandler(Incoming.ModerateRoomEvent, ModToolChangeRoomSettingsEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomVisitsEvent, ModToolRequestRoomVisitsEvent.class);
        this.registerHandler(Incoming.GetCfhChatlogEvent, ModToolRequestIssueChatlogEvent.class);
        this.registerHandler(Incoming.ModToolRequestRoomUserChatlogEvent, ModToolRequestRoomUserChatlogEvent.class);
        this.registerHandler(Incoming.GetUserChatlogEvent, ModToolRequestUserChatlogEvent.class);
        this.registerHandler(Incoming.ModAlertEvent, ModToolSanctionAlertEvent.class);
        this.registerHandler(Incoming.ModMuteEvent, ModToolSanctionMuteEvent.class);
        this.registerHandler(Incoming.ModBanEvent, ModToolSanctionBanEvent.class);
        this.registerHandler(Incoming.ModToolSanctionTradeLockEvent, ModToolSanctionTradeLockEvent.class);
        this.registerHandler(Incoming.ModToolSanctionEvent, ModToolIssueChangeTopicEvent.class);
        this.registerHandler(Incoming.CloseIssueDefaultActionEvent, ModToolIssueDefaultSanctionEvent.class);

        this.registerHandler(Incoming.GetPendingCallsForHelpEvent, RequestReportRoomEvent.class);
        this.registerHandler(Incoming.RequestReportUserBullyingEvent, RequestReportUserBullyingEvent.class);
        this.registerHandler(Incoming.ChatReviewSessionCreateEvent, ReportBullyEvent.class);
        this.registerHandler(Incoming.CallForHelpEvent, ReportEvent.class);
        this.registerHandler(Incoming.CallForHelpFromIMEvent, ReportFriendPrivateChatEvent.class);
        this.registerHandler(Incoming.ReportThreadEvent, ReportThreadEvent.class);
        this.registerHandler(Incoming.CallForHelpFromForumMessageEvent, ReportCommentEvent.class);
        this.registerHandler(Incoming.CallForHelpFromPhotoEvent, ReportPhotoEvent.class);
    }

    void registerTrading() throws Exception {
        this.registerHandler(Incoming.OpenTradingEvent, TradeStartEvent.class);
        this.registerHandler(Incoming.AddItemToTradeEvent, TradeOfferItemEvent.class);
        this.registerHandler(Incoming.AddItemsToTradeEvent, TradeOfferMultipleItemsEvent.class);
        this.registerHandler(Incoming.TradeCancelOfferItemEvent, TradeCancelOfferItemEvent.class);
        this.registerHandler(Incoming.TradeAcceptEvent, TradeAcceptEvent.class);
        this.registerHandler(Incoming.UnacceptTradingEvent, TradeUnAcceptEvent.class);
        this.registerHandler(Incoming.ConfirmAcceptTradingEvent, TradeConfirmEvent.class);
        this.registerHandler(Incoming.CloseTradingEvent, TradeCloseEvent.class);
        this.registerHandler(Incoming.ConfirmDeclineTradingEvent, TradeCancelEvent.class);
    }

    void registerGuilds() throws Exception {
        this.registerHandler(Incoming.RequestGuildBuyRoomsEvent, RequestGuildBuyRoomsEvent.class);
        this.registerHandler(Incoming.RequestGuildPartsEvent, RequestGuildPartsEvent.class);
        this.registerHandler(Incoming.CreateGuildEvent, RequestGuildBuyEvent.class);
        this.registerHandler(Incoming.GetHabboGroupDetailsEvent, RequestGuildInfoEvent.class);
        this.registerHandler(Incoming.GetGuildEditInfoEvent, RequestGuildManageEvent.class);
        this.registerHandler(Incoming.GetGuildMembersEvent, RequestGuildMembersEvent.class);
        this.registerHandler(Incoming.RequestGuildJoinEvent, RequestGuildJoinEvent.class);
        this.registerHandler(Incoming.UpdateGuildIdentityEvent, GuildChangeNameDescEvent.class);
        this.registerHandler(Incoming.UpdateGuildBadgeEvent, GuildChangeBadgeEvent.class);
        this.registerHandler(Incoming.UpdateGuildColorsEvent, GuildChangeColorsEvent.class);
        this.registerHandler(Incoming.GuildRemoveAdminEvent, GuildRemoveAdminEvent.class);
        this.registerHandler(Incoming.GuildRemoveMemberEvent, GuildRemoveMemberEvent.class);
        this.registerHandler(Incoming.GuildChangeSettingsEvent, GuildChangeSettingsEvent.class);
        this.registerHandler(Incoming.GuildAcceptMembershipEvent, GuildAcceptMembershipEvent.class);
        this.registerHandler(Incoming.RejectMembershipRequestEvent, GuildDeclineMembershipEvent.class);
        this.registerHandler(Incoming.AddAdminRightsToMemberEvent, GuildSetAdminEvent.class);
        this.registerHandler(Incoming.GuildSetFavoriteEvent, GuildSetFavoriteEvent.class);
        this.registerHandler(Incoming.RequestOwnGuildsEvent, RequestOwnGuildsEvent.class);
        this.registerHandler(Incoming.GetGuildFurniContextMenuInfoEvent, RequestGuildFurniWidgetEvent.class);
        this.registerHandler(Incoming.GuildConfirmRemoveMemberEvent, GuildConfirmRemoveMemberEvent.class);
        this.registerHandler(Incoming.DeselectFavouriteHabboGroupEvent, GuildRemoveFavoriteEvent.class);
        this.registerHandler(Incoming.DeactivateGuildEvent, GuildDeleteEvent.class);
        this.registerHandler(Incoming.GuildForumListEvent, GuildForumListEvent.class);
        this.registerHandler(Incoming.GuildForumThreadsEvent, GuildForumThreadsEvent.class);
        this.registerHandler(Incoming.GetForumStatsEvent, GuildForumDataEvent.class);
        this.registerHandler(Incoming.GuildForumPostThreadEvent, GuildForumPostThreadEvent.class);
        this.registerHandler(Incoming.UpdateForumSettingsEvent, GuildForumUpdateSettingsEvent.class);
        this.registerHandler(Incoming.GetMessagesEvent, GuildForumThreadsMessagesEvent.class);
        this.registerHandler(Incoming.ModerateMessageEvent, GuildForumModerateMessageEvent.class);
        this.registerHandler(Incoming.ModerateThreadEvent, GuildForumModerateThreadEvent.class);
        this.registerHandler(Incoming.UpdateThreadEvent, GuildForumThreadUpdateEvent.class);
        this.registerHandler(Incoming.GetHabboGroupBadgesEvent, GetHabboGuildBadgesMessageEvent.class);

//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumModerateMessageEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumModerateThreadEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumPostThreadEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumThreadsEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumThreadsMessagesEvent.class);
//        this.registerHandler(Incoming.GuildForumDataEvent,              GuildForumUpdateSettingsEvent.class);
    }

    void registerPets() throws Exception {
        this.registerHandler(Incoming.GetPetInfoEvent, GetPetInfoEvent.class);
        this.registerHandler(Incoming.RemovePetFromFlatEvent, PetPickupEvent.class);
        this.registerHandler(Incoming.RespectPetEvent, ScratchPetEvent.class);
        this.registerHandler(Incoming.GetPetCommandsEvent, RequestPetTrainingPanelEvent.class);
        this.registerHandler(Incoming.CustomizePetWithFurniEvent, PetUseItemEvent.class);
        this.registerHandler(Incoming.TogglePetRidingPermissionEvent, PetRideSettingsEvent.class);
        this.registerHandler(Incoming.MountPetEvent, PetRideEvent.class);
        this.registerHandler(Incoming.RemoveSaddleFromPetEvent, HorseRemoveSaddleEvent.class);
        this.registerHandler(Incoming.ToggleMonsterplantBreedableEvent, ToggleMonsterplantBreedableEvent.class);
        this.registerHandler(Incoming.CompostMonsterplantEvent, CompostMonsterplantEvent.class);
        this.registerHandler(Incoming.BreedPetsEvent, BreedMonsterplantsEvent.class);
        this.registerHandler(Incoming.MovePetEvent, MovePetEvent.class);
        this.registerHandler(Incoming.PetPackageNameEvent, PetPackageNameEvent.class);
        this.registerHandler(Incoming.CancelPetBreedingEvent, StopBreedingEvent.class);
        this.registerHandler(Incoming.ConfirmPetBreedingEvent, ConfirmPetBreedingEvent.class);
    }

    void registerWired() throws Exception {
        this.registerHandler(Incoming.UpdateTriggerEvent, WiredTriggerSaveDataEvent.class);
        this.registerHandler(Incoming.UpdateActionEvent, WiredEffectSaveDataEvent.class);
        this.registerHandler(Incoming.UpdateConditionEvent, WiredConditionSaveDataEvent.class);
        this.registerHandler(Incoming.WiredApplySetConditionsEvent, WiredApplySetConditionsEvent.class);
    }

    void registerUnknown() throws Exception {
        this.registerHandler(Incoming.RequestResolutionEvent, RequestResolutionEvent.class);
        this.registerHandler(Incoming.GetTalentTrackEvent, RequestTalentTrackEvent.class);
        this.registerHandler(Incoming.GetBadgePointLimitsEvent, GetBadgePointLimitsEvent.class);
        this.registerHandler(Incoming.GetCfhStatusEvent, MySanctionStatusEvent.class);
    }

    void registerFloorPlanEditor() throws Exception {
        this.registerHandler(Incoming.FloorPlanEditorSaveEvent, FloorPlanEditorSaveEvent.class);
        this.registerHandler(Incoming.GetOccupiedTilesEvent, FloorPlanEditorRequestBlockedTilesEvent.class);
        this.registerHandler(Incoming.FloorPlanEditorRequestDoorSettingsEvent, FloorPlanEditorRequestDoorSettingsEvent.class);
    }

    void registerAchievements() throws Exception {
        this.registerHandler(Incoming.GetAchievementsEvent, RequestAchievementsEvent.class);
        this.registerHandler(Incoming.RequestAchievementConfigurationEvent, RequestAchievementConfigurationEvent.class);
    }

    void registerGuides() throws Exception {
        this.registerHandler(Incoming.GuideSessionOnDutyUpdateEvent, RequestGuideToolEvent.class);
        this.registerHandler(Incoming.RequestGuideAssistanceEvent, RequestGuideAssistanceEvent.class);
        this.registerHandler(Incoming.GuideUserTypingEvent, GuideUserTypingEvent.class);
        this.registerHandler(Incoming.GuideReportHelperEvent, GuideReportHelperEvent.class);
        this.registerHandler(Incoming.GuideRecommendHelperEvent, GuideRecommendHelperEvent.class);
        this.registerHandler(Incoming.GuideUserMessageEvent, GuideUserMessageEvent.class);
        this.registerHandler(Incoming.GuideSessionRequesterCancelsEvent, GuideCancelHelpRequestEvent.class);
        this.registerHandler(Incoming.GuideSessionGuideDecidesEvent, GuideHandleHelpRequestEvent.class);
        this.registerHandler(Incoming.GuideSessionInviteRequesterEvent, GuideInviteUserEvent.class);
        this.registerHandler(Incoming.GuideSessionGetRequesterRoomEvent, GuideVisitUserEvent.class);
        this.registerHandler(Incoming.GuideCloseHelpRequestEvent, GuideCloseHelpRequestEvent.class);

        this.registerHandler(Incoming.ChatReviewGuideDetachedEvent, GuardianNoUpdatesWantedEvent.class);
        this.registerHandler(Incoming.GuardianAcceptRequestEvent, GuardianAcceptRequestEvent.class);
        this.registerHandler(Incoming.GuardianVoteEvent, GuardianVoteEvent.class);
    }

    void registerCrafting() throws Exception {
        this.registerHandler(Incoming.GetCraftingRecipeEvent, RequestCraftingRecipesEvent.class);
        this.registerHandler(Incoming.CraftingAddRecipeEvent, CraftingAddRecipeEvent.class);
        this.registerHandler(Incoming.CraftingCraftItemEvent, CraftingCraftItemEvent.class);
        this.registerHandler(Incoming.CraftSecretEvent, CraftingCraftSecretEvent.class);
        this.registerHandler(Incoming.GetCraftingRecipesAvailableEvent, RequestCraftingRecipesAvailableEvent.class);
    }

    void registerCamera() throws Exception {
        this.registerHandler(Incoming.RenderRoomEvent, CameraRoomPictureEvent.class);
        this.registerHandler(Incoming.RequestCameraConfigurationEvent, RequestCameraConfigurationEvent.class);
        this.registerHandler(Incoming.PurchasePhotoEvent, CameraPurchaseEvent.class);
        this.registerHandler(Incoming.RenderRoomThumbnailEvent, CameraRoomThumbnailEvent.class);
        this.registerHandler(Incoming.PublishPhotoEvent, CameraPublishToWebEvent.class);
    }

    void registerGameCenter() throws Exception {
        this.registerHandler(Incoming.GameCenterRequestGamesEvent, GameCenterRequestGamesEvent.class);
        this.registerHandler(Incoming.GetGameStatusEvent, GameCenterRequestAccountStatusEvent.class);
        this.registerHandler(Incoming.JoinQueueEvent, GameCenterJoinGameEvent.class);
        this.registerHandler(Incoming.GetWeeklyGameRewardWinnersEvent, GameCenterLoadGameEvent.class);
        this.registerHandler(Incoming.GameUnloadedEvent, GameCenterLeaveGameEvent.class);
        this.registerHandler(Incoming.GetWeeklyGameRewardEvent, GameCenterEvent.class);
        this.registerHandler(Incoming.Game2GetAccountGameStatusEvent, GameCenterRequestGameStatusEvent.class);
    }
}