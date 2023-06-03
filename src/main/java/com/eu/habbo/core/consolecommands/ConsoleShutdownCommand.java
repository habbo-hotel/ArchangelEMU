package com.eu.habbo.core.consolecommands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.list.ShutdownCommand;

public class ConsoleShutdownCommand extends ConsoleCommand {
    public ConsoleShutdownCommand() {
        super("stop", "Stop the emulator.");
    }

    @Override
    public void handle(String[] args) {
        Emulator.getGameEnvironment().getCommandsManager().handleCommand(null, args.toString());
    }
}