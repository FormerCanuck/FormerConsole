package me.fc.console.command;

import me.fc.console.Console;

public abstract class Command {

    private Console console;

    public abstract void onCommand(String[] args);

    public abstract String getName();

    public abstract String getUse();

    public void setConsole(Console console) {
        this.console = console;
    }

    public Console getConsole() {
        return console;
    }
}
