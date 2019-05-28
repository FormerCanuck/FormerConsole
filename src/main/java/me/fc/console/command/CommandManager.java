package me.fc.console.command;

import me.fc.console.Console;
import me.fc.console.command.commands.Clear;
import me.fc.console.command.commands.ColorMessage;
import me.fc.console.command.commands.Help;

import java.awt.*;
import java.util.HashMap;
import java.util.TreeMap;

public class CommandManager {

    private static final String PREFIX = "[Command Manager]: ";

    private HashMap<String, Command> commandList = new HashMap<>();

    private Console console;

    public CommandManager(Console console) {
        this.console = console;
        console.println(PREFIX + "Starting to add all the commands...", false, Color.GREEN);
        // Add default commands here.
        addCommand(new Clear());
        addCommand(new ColorMessage());
        addCommand(new Help());

        console.println(PREFIX + "Finished adding all the commands...", false, Color.GREEN);
    }

    public HashMap<String, Command> getCommandList() {
        return this.commandList;
    }

    public void addCommand(Command command) {
        command.setConsole(console);
        commandList.put(command.getName(), command);
        console.println(PREFIX + "Adding command: " + command.getName(), false, Color.GREEN);
        sortCommands();
    }

    public TreeMap<String, Command> sortCommands() {
        TreeMap<String, Command> sorted = new TreeMap<>();
        sorted.putAll(commandList);
        return sorted;
    }

    public void onCommand(String[] args) {
        for (Command command : commandList.values()) {
            if (command.getName().equalsIgnoreCase(args[0].substring(1))) {
                command.onCommand(args);
                return;
            }
        }

        getConsole().error("There is no command with the name: " + args[0].substring(1));
    }

    public Console getConsole() {
        return console;
    }
}
