package com.eu.habbo.roleplay.commands.billing;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.messages.outgoing.users.CreditBalanceComposer;
import com.eu.habbo.roleplay.billing.BillingManager;
import com.eu.habbo.roleplay.billing.UserBill;
import com.eu.habbo.roleplay.messages.outgoing.user.UserRoleplayStatsChangeComposer;

public class PayBillCommand extends Command {
    public PayBillCommand() {
        super("cmd_pay_bill");
    }
    @Override
    public boolean handle(GameClient gameClient, String[] params) {
        if (params == null) {
            return true;
        }

        if (params[1] == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.billing_statement_not_found"));
            return true;
        }

        UserBill userBill = BillingManager.getInstance().getBillByID(Integer.parseInt(params[1]));

        if (userBill == null) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.billing_statement_not_found"));
            return true;
        }

        if (userBill.userID != gameClient.getHabbo().getHabboInfo().getId()) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.billing_statement_not_found"));
            return true;
        }

        if (userBill.amountPaid == userBill.amountCharged) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("roleplay.billing_statement_already_paid"));
            return true;
        }

        if (gameClient.getHabbo().getHabboInfo().getCredits() < userBill.amountCharged) {
            gameClient.getHabbo().whisper(Emulator.getTexts().getValue("generic.not_enough_credits"));
            return true;
        }

        gameClient.getHabbo().getHabboInfo().setCredits(gameClient.getHabbo().getHabboInfo().getCredits() - userBill.amountCharged);
        gameClient.getHabbo().shout(Emulator.getTexts().getValue("roleplay.billing_statement_paid").replace(":billName", userBill.title));
        gameClient.sendResponse(new CreditBalanceComposer((gameClient.getHabbo())));
        gameClient.sendResponse(new UserRoleplayStatsChangeComposer(gameClient.getHabbo()));

        userBill.getBillingItem().onBillPaid(gameClient.getHabbo());

        return true;
    }
}
