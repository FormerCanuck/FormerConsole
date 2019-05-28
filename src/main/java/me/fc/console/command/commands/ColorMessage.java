package me.fc.console.command.commands;

import me.fc.console.command.Command;

import java.awt.*;
import java.lang.reflect.Field;

public class ColorMessage extends Command {
    @Override
    public void onCommand(String[] args) {
        if (args.length < 2) {
            getConsole().error(getUse());
            return;
        }

        String colorName = args[1];

        Color color = null;
        try {
            Field field = Class.forName("java.awt.Color").getField(colorName);
            color = (Color) field.get(null);
        } catch (Exception e) {
            getConsole().error("You must choose a valid color.");
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            stringBuilder.append(args[i] + " ");
        }

        getConsole().println(stringBuilder.toString(), color);
    }

    @Override
    public String getName() {
        return "color";
    }

    @Override
    public String getUse() {
        return "Used to send a colored message to the console.";
    }
}
