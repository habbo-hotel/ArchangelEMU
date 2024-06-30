package com.eu.habbo.roleplay.corp;

public class LicenseMapper {

    public static CorpTag licenseTypeToCorpTag(LicenseType licenseType) {
        if (licenseType == LicenseType.DRIVER) {
            return CorpTag.DRIVING_AUTHORITY;
        }

        if (licenseType == LicenseType.FARMING) {
            return CorpTag.FARMING_AUTHORITY;
        }

        if (licenseType == LicenseType.FISHING) {
            return CorpTag.FISHING_AUTHORITY;
        }

        if (licenseType == LicenseType.MINING) {
            return CorpTag.MINING_AUTHORITY;
        }

        if (licenseType == LicenseType.LUMBERJACK) {
            return CorpTag.LUMBERJACK_AUTHORITY;
        }

        if (licenseType == LicenseType.WEAPON) {
            return CorpTag.WEAPONS_AUTHORITY;
        }

        return null;
    }

    public static LicenseType corpTagToLicenseType(CorpTag corpTag) {
        if (corpTag == CorpTag.DRIVING_AUTHORITY) {
            return LicenseType.DRIVER;
        }

        if (corpTag == CorpTag.FARMING_AUTHORITY) {
            return LicenseType.FARMING;
        }

        if (corpTag == CorpTag.FISHING_AUTHORITY) {
            return LicenseType.FISHING;
        }

        if (corpTag == CorpTag.MINING_AUTHORITY) {
            return LicenseType.MINING;
        }

        if (corpTag == CorpTag.LUMBERJACK_AUTHORITY) {
            return LicenseType.LUMBERJACK;
        }

        if (corpTag == CorpTag.WEAPONS_AUTHORITY) {
            return LicenseType.WEAPON;
        }

        return null;
    }

    public static LicenseType corpToLicenseType(Corp corp) {
        for (CorpTag corpTag : corp.getTags()) {
            LicenseType licenseType = LicenseMapper.corpTagToLicenseType(corpTag);
            if (licenseType != null) {
                return licenseType;
            }
        }
        return null;
    }

}
