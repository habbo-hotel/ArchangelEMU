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
import com.eu.habbo.roleplay.messages.incoming.guilds.*;
import com.eu.habbo.roleplay.messages.incoming.guilds.forums.*;
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
import com.eu.habbo.messages.incoming.unknown.UnknownEvent2;
import com.eu.habbo.messages.incoming.users.*;
import com.eu.habbo.messages.incoming.wired.ApplySnapshotEvent;
import com.eu.habbo.messages.incoming.wired.UpdateActionEvent;
import com.eu.habbo.messages.incoming.wired.UpdateConditionEvent;
import com.eu.habbo.messages.incoming.wired.UpdateTriggerEvent;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import gnu.trove.map.hash.THashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class PacketManager {

    private static final List<Integer> logList = new ArrayList<>();
    public static boolean DEBUG_SHOW_PACKETS = false;
    public static boolean MULTI_THREADED_PACKET_HANDLING = false;
    private final THashMap<Integer, Class<? extends MessageHandler>> incoming;
    private final THashMap<Integer, List<ICallable>> callables;
    @Getter
    private final PacketNames names;

    public PacketManager() {
        this.incoming = new THashMap<>();
        this.callables = new THashMap<>();
        this.names = new PacketNames();
        this.names.initialize();

        Arrays.stream(Incoming.values())
                .forEach(this::registerHandler);
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

    public void registerHandler(Incoming incoming) {
        try {
            registerHandler(incoming.getMessageId(), incoming.getMessageClass());
        } catch (Exception e) {
            log.error("Failed to register Message Event");
        }
    }

    public void registerHandler(Integer header, Class<? extends MessageHandler> handler) throws Exception {

        if (header < 0)
            return;

        if (this.incoming.containsKey(header)) {
            throw new Exception("Header already registered. Failed to register " + handler.getName() + " with header " + header);
        }

        if (handler.isInstance(UnknownEvent2.class)) {
            return;
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
}