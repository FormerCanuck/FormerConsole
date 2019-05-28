package me.fc.console.command.commands;

import me.fc.console.command.Command;

import java.awt.*;

public class Clear extends Command {

    @Override
    public void onCommand(String[] args) {
        getConsole().clearConsole();
        getConsole().println("-= Console was cleared =-", false, Color.GREEN);
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getUse() {
        return "This command is used to clear the jTextPane.";
    }
}
