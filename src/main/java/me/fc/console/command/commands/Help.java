package me.fc.console.command.commands;

import me.fc.console.command.Command;

import java.awt.*;
import java.util.Map;

public class Help extends Command {

    @Override
    public void onCommand(String[] args) {
        if (args.length == 1) {
            this.getConsole().println("Here is a list of available commands: ", Color.ORANGE);

            for (Map.Entry<String, Command> entry : getConsole().getCommandManager().sortCommands().entrySet())
                this.getConsole().println("[" + entry.getValue().getName() + "]: " + entry.getValue().getUse(), Color.ORANGE);
            return;
        }

        String cmd = args[1];

        for (Command command : getConsole().getCommandManager().getCommandList().values()) {
            if (command.getName().equalsIgnoreCase(cmd)) {
                getConsole().println("Usage for /" + cmd + " " + command.getUse(), Color.ORANGE);
            }
        }
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUse() {
        return "Display info about command by typing /help <command name>";
    }
}
