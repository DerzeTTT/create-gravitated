package dev.dsddr.aeronauticsaddon.service;

import dev.dsddr.aeronauticsaddon.AeronauticsAddon;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class AddonHoneyGlueRange {
    public static final int DEFAULT_HONEY_GLUE_RANGE = 64;
    private static final String RANGE_KEY = "honey_glue_range";
    private static final Path CONFIG_PATH = Path.of("config", "create-gravitated.properties");
    private static final Object LOCK = new Object();

    private static volatile boolean loaded;
    private static volatile int honeyGlueRange = DEFAULT_HONEY_GLUE_RANGE;

    private AddonHoneyGlueRange() {
    }

    public static int getRange() {
        ensureLoaded();
        return honeyGlueRange;
    }

    public static double getRangeAsDouble() {
        return (double) getRange();
    }

    public static void setRange(final int range) {
        ensureLoaded();
        synchronized (LOCK) {
            honeyGlueRange = sanitize(range);
            saveUnsafe();
        }
    }

    public static void resetRange() {
        setRange(DEFAULT_HONEY_GLUE_RANGE);
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }

        synchronized (LOCK) {
            if (loaded) {
                return;
            }

            loadUnsafe();
            loaded = true;
        }
    }

    private static void loadUnsafe() {
        if (!Files.exists(CONFIG_PATH)) {
            honeyGlueRange = DEFAULT_HONEY_GLUE_RANGE;
            saveUnsafe();
            return;
        }

        final Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            properties.load(reader);
            honeyGlueRange = sanitize(Integer.parseInt(properties.getProperty(RANGE_KEY, Integer.toString(DEFAULT_HONEY_GLUE_RANGE))));
        } catch (final IOException | NumberFormatException exception) {
            AeronauticsAddon.LOGGER.warn("Failed to read {}, using the default Honey Glue range.", CONFIG_PATH, exception);
            honeyGlueRange = DEFAULT_HONEY_GLUE_RANGE;
            saveUnsafe();
        }
    }

    private static void saveUnsafe() {
        final Properties properties = new Properties();
        properties.setProperty(RANGE_KEY, Integer.toString(honeyGlueRange));

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                properties.store(writer, "Create: Gravitated");
            }
        } catch (final IOException exception) {
            AeronauticsAddon.LOGGER.warn("Failed to save {}.", CONFIG_PATH, exception);
        }
    }

    private static int sanitize(final int range) {
        return Math.max(1, range);
    }
}
