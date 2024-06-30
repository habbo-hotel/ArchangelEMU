package com.eu.habbo.roleplay.commands.bank;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.roleplay.corp.Corp;
import com.eu.habbo.roleplay.corp.CorpTag;
import com.eu.habbo.roleplay.database.HabboBankAccountRepository;
import com.eu.habbo.roleplay.users.HabboBankAccount;

public class BankAccountOpenCommand extends Command  {

    public BankAccountOpenCommand() {
        super("cmd_bank_open");
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        if (params.length != 2) {
            return true;
        }

        if (!gameClient.getHabbo().getHabboRoleplayStats().isWorking()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.roleplay.must_be_working"));
            return true;
        }

        Corp bankCorp = gameClient.getHabbo().getHabboRoleplayStats().getCorp();

        if (bankCorp == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.corp_not_found"));
            return true;
        }

        if (!bankCorp.getTags().contains(CorpTag.BANK)) {
            gameClient.getHabbo().whisper(Emulator.getTexts()
                    .getValue("roleplay.bank.corp_not_a_bank")
                    .replace(":corpName", bankCorp.getGuild().getName())
            );
            return true;
        }

        if (gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId() !=  bankCorp.getGuild().getRoomId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.generic.not_at_work"));
            return true;
        }

        String username = params[1];
        Habbo targetedUser = Emulator.getGameEnvironment().getHabboManager().getHabbo(username);

        if (targetedUser == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts()
                    .getValue("generic.user_not_found")
                    .replace(":username", username)
            );
            return true;
        }

        if (targetedUser.getRoomUnit().getRoom().getRoomInfo().getId() != gameClient.getHabbo().getRoomUnit().getRoom().getRoomInfo().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts()
                    .getValue("roleplay.generic.user_not_in_room")
                    .replace(":username", username)
            );
            return true;
        }

        HabboBankAccount bankAccount = HabboBankAccountRepository.getInstance().getByUserAndCorpID(targetedUser.getHabboInfo().getId(), bankCorp.getGuild().getId());

        if (bankAccount != null) {
            gameClient.getHabbo().whisper(Emulator.getTexts()
                    .getValue("roleplay.bank.roleplay.bank.account_already_exists")
                    .replace(":username", targetedUser.getHabboInfo().getUsername())
            );
            return true;
        }

        int currentTime = (int) (System.currentTimeMillis() / 1000);
        HabboBankAccountRepository.getInstance().create(targetedUser.getHabboInfo().getId(), bankCorp.getGuild().getId(), 0, currentTime, currentTime);

        gameClient.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.bank.account_started")
                .replace(":username", targetedUser.getHabboInfo().getUsername())
                .replace(":corpName", bankCorp.getGuild().getName())
        );

        gameClient.getHabbo().shout(Emulator.getTexts()
                .getValue("roleplay.bank.assisted_account_start")
                .replace(":username", targetedUser.getHabboInfo().getUsername())
        );

        return true;
    }
}
