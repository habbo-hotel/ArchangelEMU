package com.eu.habbo.messages.outgoing.rooms;

import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import gnu.trove.set.hash.THashSet;

public class UpdateStackHeightComposer extends MessageComposer {
    private int x;
    private int y;
    private double height;

    private THashSet<RoomTile> updateTiles;

    public UpdateStackHeightComposer(int x, int y, double height) {
        this.x = x;
        this.y = y;
        this.height = height;
    }

    public UpdateStackHeightComposer(THashSet<RoomTile> updateTiles) {
        this.updateTiles = updateTiles;
    }

    @Override
    public ServerMessage compose() {
        //TODO: THIS IS A TEMP FIX. THERE IS AN ISSUE WITH BAD PACKET STRUCTURE HERE CAUSING ISSUES WITH MOVING LARGE FURNITURE
        this.response.init(Outgoing.UpdateStackHeightComposer);
        if (this.updateTiles != null) {
            if(this.updateTiles.size() > 4) {
                RoomTile[] tiles = this.updateTiles.toArray(new RoomTile[updateTiles.size()]);
                this.response.appendByte(4);
                for(int i = 0; i < 4; i++) {
                    RoomTile t = tiles[i];
                    this.response.appendByte((int) t.x);
                    this.response.appendByte((int) t.y);
                    this.response.appendShort(t.relativeHeight());
                }
                return this.response;
            }

            this.response.appendByte(this.updateTiles.size());
            for (RoomTile t : this.updateTiles) {
                this.response.appendByte((int) t.x);
                this.response.appendByte((int) t.y);
                this.response.appendShort(t.relativeHeight());
            }
        } else {
            this.response.appendByte(1);
            this.response.appendByte(this.x);
            this.response.appendByte(this.y);
            this.response.appendShort((int) (this.height));
        }
        return this.response;
    }
}
