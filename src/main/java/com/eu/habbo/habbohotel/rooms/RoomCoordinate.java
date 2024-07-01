package com.eu.habbo.habbohotel.rooms;

import java.util.Objects;

public class RoomCoordinate {

        private final short x;
        private final short y;

        public RoomCoordinate(short x, short y) {
            this.x = x;
            this.y = y;
        }

        public short getX() {
            return x;
        }

        public short getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RoomCoordinate that = (RoomCoordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
