package me.fc.console.files;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.awt.*;
import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigFile {

    private Yaml yaml;
    private File parentFolderFile = new File("FConsole");
    private File file;

    private LinkedHashMap<String, Object> config;
    private LinkedHashMap<String, Object> defaults;

    private boolean exists = false;

    private Logger logger = Logger.getLogger("Config File");

    public ConfigFile(String fileName) {
        defaults = new LinkedHashMap<>();
        if (!parentFolderFile.exists()) {
            if (parentFolderFile.mkdir()) {
                logger.info("[ConfigFile]: Created the main dir.");
            } else {
                logger.severe("[ConfigFile]: Could not create the main dir.");
            }
        }

        file = new File(parentFolderFile, fileName + ".yml");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    logger.info("[ConfigFile]: Created the config file: " + fileName);
                } else {
                    logger.severe("[ConfigFile]: Could not create the config file: " + fileName);
                }
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        } else exists = true;

        yaml = new Yaml();

        try {
            InputStream inputStream = new FileInputStream(file);
            config = yaml.load(inputStream);
        } catch (FileNotFoundException e) {
            logger.severe(e.getMessage());
        }

        if (config == null) {
            config = new LinkedHashMap<String, Object>();
        }
    }

    public boolean exists() {
        return exists;
    }

    public void addDefaults() {
        config = defaults;
        save();
    }

    public void addDefault(String key, Object value) {
        defaults.put(key, value);
    }

    public Integer getInt(String key) {
        return (Integer) config.get(key);
    }

    public Color getColor(String key) {
        return new Color(getInt(key + ".r"), getInt(key + ".g"), getInt(key + ".b"));
    }

    public void setColor(String key, Color value) {
        config.put(key + ".r", value.getRed());
        config.put(key + ".g", value.getGreen());
        config.put(key + ".b", value.getBlue());
        save();
    }

    public void save() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
        dumperOptions.setPrettyFlow(true);
        yaml = new Yaml(dumperOptions);

        try {
            FileWriter writer = new FileWriter(file);
            yaml.dump(config, writer);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public boolean contains(String key) {
        return config.containsKey(key);
    }

    public void set(String key, Object value) {
        config.put(key, value);
        save();
    }

    public Object get(String key) {
        return config.get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }
}
