package com.eu.habbo.messages.rcon;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.CommandsManager;
import com.eu.habbo.habbohotel.users.Habbo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExecuteCommand extends RCONMessage<ExecuteCommand.JSONExecuteCommand> {


    public ExecuteCommand() {
        super(JSONExecuteCommand.class);
    }

    @Override
    public void handle(Gson gson, JSONExecuteCommand json) {
        try {
            Habbo habbo = Emulator.getGameServer().getGameClientManager().getHabbo(json.user_id);

            if (habbo == null) {
                this.status = HABBO_NOT_FOUND;
                return;
            }


            Emulator.getGameEnvironment().getCommandsManager().handleCommand(habbo.getClient(), json.command);
        } catch (Exception e) {
            this.status = STATUS_ERROR;
            log.error("Caught exception", e);
        }
    }

    static class JSONExecuteCommand {

        public int user_id;


        public String command;
    }
}