package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

public class AddWaterDataSourceFactory {

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterDataSourceFactory.class);

    public AddWaterDataSource makeDataSource(JSONObject spec) {

        String type;
        String name = "<unknown>";

        try {
            name = (String) spec.get("Name");
            type = (String) spec.get("Type");

            switch (type) {
                case "MAP":
                    return new AddWaterMapDataSource(spec);
                case "CSV":
                    return new AddWaterCSVDataSource(spec);
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.error(String.format("Error parsing data source  %s", name));
            logger.error(e.toString());
        }

        return null;
    }
}
