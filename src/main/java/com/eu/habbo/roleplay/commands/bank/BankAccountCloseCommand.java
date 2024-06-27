package com.eu.habbo.roleplay.commands.bank;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.database.HabboBankAccountRepository;
import com.eu.habbo.roleplay.users.HabboBankAccount;

public class BankAccountCloseCommand extends Command  {

    public BankAccountCloseCommand() {
        super("cmd_bank_close");
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

        if (!bankCorp.getTags().contains(CorpTag.BANK)) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_allowed"));
            return true;
        }

        HabboBankAccount bankAccount = HabboBankAccountRepository.getInstance().getByUserAndCorpID(bankMember.getHabboInfo().getId(), bankCorp.getGuild().getId());

        if (bankAccount == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_found"));
            return true;
        }

        if (gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId() != bankMember.getRoomUnit().getRoom().getRoomInfo().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_in_room"));
            return true;
        }

        if (bankMember.getRoomUnit().getRoom().getRoomInfo().getId() != bankCorp.getGuild().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_in_room"));
            return true;
        }

        int bankTotalCredits = bankAccount.getCheckingBalance();

        bankMember.getHabboInfo().setCredits(gameClient.getHabbo().getHabboInfo().getCredits() + bankTotalCredits);
        HabboBankAccountRepository.getInstance().delete(bankAccount.getId());

        bankMember.shout(Emulator.getTexts().getValue("roleplay.bank.close.success").replace(":credits", String.valueOf(bankTotalCredits)));
        gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.bank.close.assist").replace(":username", bankMember.getHabboInfo().getUsername()));

        return true;
    }

}
