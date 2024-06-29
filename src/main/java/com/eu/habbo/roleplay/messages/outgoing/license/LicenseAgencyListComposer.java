package com.eu.habbo.roleplay.messages.outgoing.license;

import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.Outgoing;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.government.LicenseType;
import com.eu.habbo.roleplay.license.LicenseMapper;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@AllArgsConstructor
public class LicenseAgencyListComposer extends MessageComposer {

    private List<Corp> getLicenseAgencies() {
        LicenseType[] licenseTypes = LicenseType.values();

        List<CompletableFuture<List<Corp>>> futures = Arrays.stream(licenseTypes)
                .map(licenseType -> CompletableFuture.supplyAsync(() -> {
                    CorpTag corpTag = LicenseMapper.licenseTypeToCorpTag(licenseType);
                    if (corpTag == null) {
                        return null;
                    }
                    return CorpManager.getInstance().getCorpsByTag(corpTag);
                }))
                .toList();

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        return allOf.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        ).join();
    }

    @Override
    protected ServerMessage composeInternal() {
        List<Corp> licenseAgencyCorps = this.getLicenseAgencies();
        this.response.init(Outgoing.licenseAgencyListComposer);
        this.response.appendInt(licenseAgencyCorps.size());
        for (Corp licenseAgency : licenseAgencyCorps) {
            this.response.appendString(licenseAgency.getGuild().getId() + ";" + licenseAgency.getGuild().getName() + ";" + LicenseMapper.corpToLicenseType(licenseAgency));
        }
        return this.response;
    }
}
