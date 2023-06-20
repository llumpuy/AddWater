package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.json.simple.JSONObject;

public class AddWaterPOJODataSource implements AddWaterDataSource{

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterDataSourceFactory.class);

    private String name;
    private String type;
    private String sourceFile;

    @Override
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
    public void setSource(String sourceName) {
        sourceFile = sourceName;
    }

    @Override
    public String getSource() {
        return sourceFile;
    }

    public AddWaterPOJODataSource (JSONObject jconfig)  {

        try {
            name = (String) jconfig.get("Name");
            type = (String) jconfig.get("Type");
            sourceFile = (String) jconfig.get("SourceFile");

            if (!type.equals("POJO")) {
                logger.error(String.format("Unexpected data source type for %s", name));
            }

            //TODO

        } catch (Exception e) {
            logger.error(String.format("Error parsing data source file %s", sourceFile));
            logger.error(e.toString());
        }
    }

    @Override
    public void addToContext(VelocityContext veloContext) {

    }
}
