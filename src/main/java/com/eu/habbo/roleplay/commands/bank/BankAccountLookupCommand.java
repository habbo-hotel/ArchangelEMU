package com.eu.habbo.roleplay.commands.bank;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.database.HabboBankAccountRepository;
import com.eu.habbo.roleplay.messages.outgoing.bank.BankAccountInfoComposer;
import com.eu.habbo.roleplay.users.HabboBankAccount;

public class BankAccountLookupCommand extends Command  {

    public BankAccountLookupCommand() {
        super("cmd_bank_lookup");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        if (params.length != 3) {
            return true;
        }

        int corpID = Integer.parseInt(params[1]);
        Corp bankCorp = CorpManager.getInstance().getCorpByID(corpID);

        String username = params[2];
        Habbo bankMember = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);

        if (bankCorp == null || bankMember == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_found"));
            return true;
        }

        HabboBankAccount bankAccount = HabboBankAccountRepository.getInstance().getByUserAndCorpID(bankMember.getHabboInfo().getId(), corpID);

        if (bankAccount == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_found"));
            return true;
        }

       gameClient.sendResponse(new BankAccountInfoComposer(bankAccount));

        return true;
    }
}
