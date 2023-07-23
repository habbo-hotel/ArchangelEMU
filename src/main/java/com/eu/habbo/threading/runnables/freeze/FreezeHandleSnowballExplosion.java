package com.eu.habbo.threading.runnables.freeze;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.achievements.AchievementManager;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.freeze.FreezeGamePlayer;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeBlock;
import com.eu.habbo.habbohotel.items.interactions.games.freeze.InteractionFreezeTile;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.entities.items.RoomItem;
import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.set.hash.THashSet;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
class FreezeHandleSnowballExplosion implements Runnable {

    private final FreezeThrowSnowball thrownData;


    @Override
    public void run() {
        try {
            if (this.thrownData == null || this.thrownData.habbo.getHabboInfo().getGamePlayer() == null)
                return;

            FreezeGamePlayer player = (FreezeGamePlayer) this.thrownData.habbo.getHabboInfo().getGamePlayer();

            if (player == null)
                return;

            player.addSnowball();

            THashSet<RoomTile> tiles = new THashSet<>();

            FreezeGame game = ((FreezeGame) this.thrownData.room.getGame(FreezeGame.class));

            if (game == null)
                return;

            if (player.nextHorizontal) {
                tiles.addAll(game.affectedTilesByExplosion(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY(), this.thrownData.radius + 1));
            }

            if (player.nextDiagonal) {
                tiles.addAll(game.affectedTilesByExplosionDiagonal(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY(), this.thrownData.radius + 1));
                player.nextDiagonal = false;
            }

            THashSet<InteractionFreezeTile> freezeTiles = new THashSet<>();

            for (RoomTile roomTile : tiles) {
                THashSet<RoomItem> items = this.thrownData.room.getRoomItemManager().getItemsAt(roomTile);

                for (RoomItem freezeTile : items) {
                    if (freezeTile instanceof InteractionFreezeTile || freezeTile instanceof InteractionFreezeBlock) {
                        int distance = 0;
                        if (freezeTile.getX() != this.thrownData.targetTile.getX() && freezeTile.getY() != this.thrownData.targetTile.getY()) {
                            distance = Math.abs(freezeTile.getX() - this.thrownData.targetTile.getX());
                        } else {
                            distance = (int) Math.ceil(this.thrownData.room.getLayout().getTile(this.thrownData.targetTile.getX(), this.thrownData.targetTile.getY()).distance(roomTile));
                        }

                        if (freezeTile instanceof InteractionFreezeTile) {
                            freezeTile.setExtradata("11" + String.format("%03d", distance * 100)); //TODO Investigate this further. Probably height dependent or something.
                            freezeTiles.add((InteractionFreezeTile) freezeTile);
                            this.thrownData.room.updateItem(freezeTile);


                            THashSet<Habbo> habbos = new THashSet<>();
                            RoomTile tile = this.thrownData.room.getLayout().getTile(freezeTile.getX(), freezeTile.getY());
                            habbos.addAll(this.thrownData.room.getRoomUnitManager().getHabbosAt(tile));

                            for (Habbo habbo : habbos) {
                                if (habbo.getHabboInfo().getGamePlayer() != null && habbo.getHabboInfo().getGamePlayer() instanceof FreezeGamePlayer hPlayer) {
                                    if (!hPlayer.canGetFrozen())
                                        continue;

                                    if (hPlayer.getTeamColor().equals(player.getTeamColor()))
                                        player.addScore(-FreezeGame.FREEZE_LOOSE_POINTS);
                                    else
                                        player.addScore(FreezeGame.FREEZE_LOOSE_POINTS);

                                    ((FreezeGamePlayer) habbo.getHabboInfo().getGamePlayer()).freeze();

                                    if (this.thrownData.habbo != habbo) {
                                        AchievementManager.progressAchievement(habbo, Emulator.getGameEnvironment().getAchievementManager().getAchievement("EsA"));
                                    }
                                }
                            }
                        } else {
                            if (freezeTile.getExtradata().equalsIgnoreCase("0")) {
                                game.explodeBox((InteractionFreezeBlock) freezeTile, distance * 100);
                                player.addScore(FreezeGame.DESTROY_BLOCK_POINTS);
                            }
                        }
                    }
                }
            }

            Emulator.getThreading().run(new FreezeResetExplosionTiles(freezeTiles, this.thrownData.room), 1000);
        } catch (Exception e) {
            log.error("Caught exception", e);
        }
    }
}
