package com.eu.habbo.habbohotel.permissions;

public enum PermissionSetting {
    DISALLOWED,
    ALLOWED,
    HAS_ROOM_RIGHTS;

    public static PermissionSetting fromString(String value) {
        return switch (value) {
            case "1" -> ALLOWED;
            case "2" -> HAS_ROOM_RIGHTS;
            default -> DISALLOWED;
        };
    }
}
