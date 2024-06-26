package com.eu.habbo.roleplay.commands.bank;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpManager;
import com.eu.habbo.roleplay.database.HabboBankAccountRepository;
import com.eu.habbo.roleplay.users.HabboBankAccount;

public class BankAccountDepositCommand extends Command  {

    public BankAccountDepositCommand() {
        super("cmd_bank_deposit");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {

        if (params == null) {
            return true;
        }

        if (params.length != 3) {
            return true;
        }

        int corpID = Integer.parseInt(params[0]);
        Corp bankCorp = CorpManager.getInstance().getCorpByID(corpID);

        int depositAmount =Integer.parseInt(params[1]);

        HabboBankAccount bankAccount = HabboBankAccountRepository.getInstance().getByUserAndCorpID(gameClient.getHabbo().getHabboInfo().getId(), corpID);

        if (bankCorp == null || bankAccount == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_found"));
            return true;
        }

        if (gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId() != gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_in_room"));
            return true;
        }

        if (gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId() != corpID) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.not_in_room"));
            return true;
        }

        if (gameClient.getHabbo().getHabboInfo().getCredits() < depositAmount) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.bank.deposit.not_enough"));
            return true;
        }

        gameClient.getHabbo().getHabboInfo().setCredits(gameClient.getHabbo().getHabboInfo().getCredits() - depositAmount);
        bankAccount.setCreditBalance(bankAccount.getDebitBalance() + depositAmount);
        HabboBankAccountRepository.getInstance().update(bankAccount);

        gameClient.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.bank.deposit.success")
                .replace(":credits", String.valueOf(depositAmount))
        );

        return true;
    }
}
