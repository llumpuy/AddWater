package com.appdynamics.extensions.addWater;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;

public class Main {

    private static LocalDateTime procStartTime = LocalDateTime.now();

    //initialise logger
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(@org.jetbrains.annotations.NotNull String[] args) {

        logger.info(String.format("Process completed by %s", procStartTime));

        String configFile = (args.length < 1 ? args[0] : "config.json");

        // create Velocity Engine wrapper from configuration
        AddWaterEngineWrapper engineWrapper = new AddWaterEngineWrapper(configFile);

        // produce all outcomes specified
        engineWrapper.apply();

        logger.info(String.format("Process completed by %s", LocalDateTime.now().toString()));

    }

}