package com.eu.habbo.messages.incoming.camera;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.messages.incoming.MessageHandler;
import com.eu.habbo.messages.outgoing.camera.ThumbnailStatusMessageComposer;
import com.eu.habbo.networking.camera.CameraClient;
import com.eu.habbo.networking.camera.messages.outgoing.CameraRenderImageComposer;
import com.eu.habbo.util.crypto.ZIP;

public class RenderRoomThumbnailEvent extends MessageHandler {
    @Override
    public void handle() {
        if (!this.client.getHabbo().hasRight(Permission.ACC_CAMERA)) {
            this.client.getHabbo().alert(Emulator.getTexts().getValue("camera.permission"));
            return;
        }

        if (!this.client.getHabbo().getHabboInfo().getCurrentRoom().isOwner(this.client.getHabbo()))
            return;

        if (CameraClient.isLoggedIn) {
            this.packet.getBuffer().readFloat();
            byte[] data = this.packet.getBuffer().readBytes(this.packet.getBuffer().readableBytes()).array();
            String content = new String(ZIP.inflate(data));

            CameraRenderImageComposer composer = new CameraRenderImageComposer(this.client.getHabbo().getHabboInfo().getId(), this.client.getHabbo().getHabboInfo().getCurrentRoom().getBackgroundTonerColor().getRGB(), 110, 110, content);

            this.client.getHabbo().getHabboInfo().setPhotoJSON(Emulator.getConfig().getValue("camera.extradata").replace("%timestamp%", composer.timestamp + ""));
            this.client.getHabbo().getHabboInfo().setPhotoTimestamp(composer.timestamp);

            Emulator.getCameraClient().sendMessage(composer);
        } else {
            this.client.sendResponse(new ThumbnailStatusMessageComposer());
            this.client.getHabbo().alert(Emulator.getTexts().getValue("camera.disabled"));
        }
    }
}