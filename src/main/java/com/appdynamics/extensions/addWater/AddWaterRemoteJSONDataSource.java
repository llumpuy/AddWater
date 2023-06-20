package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.json.simple.JSONObject;

public class AddWaterRemoteJSONDataSource implements AddWaterDataSource{


    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterDataSourceFactory.class);

    private String name;
    private String type;
    private String sourceURL;


    public void setName(String sourceName) {
        name = sourceName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setType(String sourceType) {
        type = sourceType;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setSource(String resourceName) {
        sourceURL = resourceName;
    }

    @Override
    public String getSource() {
        return sourceURL;
    }

    public AddWaterRemoteJSONDataSource(JSONObject jconfig)  {

        try {
            name = (String) jconfig.get("Name");
            type = (String) jconfig.get("Type");
            sourceURL = (String) jconfig.get("sourceURL");

            if (!type.equals("REMOTEMAP")) {
                logger.error(String.format("Unexpected data source type for %s", name));
            }

            //TODO

            // Establish Auth

            // Connect

            // Parse & Instantiate Attributes


        } catch (Exception e) {
            logger.error(String.format("Error parsing data source  %s", name));
            logger.error(e.toString());
        }
    }

    @Override
    public void addToContext(VelocityContext veloContext) {

    }
}
