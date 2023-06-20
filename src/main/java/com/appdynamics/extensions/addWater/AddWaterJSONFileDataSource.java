package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;

public class AddWaterJSONFileDataSource implements AddWaterDataSource{

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterDataSourceFactory.class);

    private String name;
    private String type;
    private String sourceFile;

    private JSONObject valJO;

    @Override
    public void setName(String sourceName) {
        name = sourceName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setType(String typeName) {
        type = typeName;
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

    public AddWaterJSONFileDataSource(JSONObject jconfig)  {

        try {
            name = (String) jconfig.get("Name");
            type = (String) jconfig.get("Type");
            sourceFile = (String) jconfig.get("SourceFile");

            if (!type.equals("JSONFILE")) {
                logger.error(String.format("Unexpected data source type for %s", name));
            }

            valJO = loadJO(sourceFile);

        } catch (Exception e) {
            logger.error(String.format("Error parsing data source  %s", name));
            logger.error(e.toString());
        }
    }

    private JSONObject loadJO(String sourceFile) {

        JSONObject jo = null;

        try {
            FileReader reader = new FileReader(sourceFile);

            JSONParser jsonParser = new JSONParser();
            jo = (JSONObject) jsonParser.parse(reader);
            return jo;

        } catch(ParseException jex) {
            logger.error(String.format("Error parsing JSON content in data source file %s", sourceFile));
            logger.error(jex.toString());
        } catch (Exception e) {
            logger.error(String.format("Unknown error loading data source file %s", sourceFile));
            logger.error(e.toString());
        }
        return new JSONObject();

    }


    @Override
    public void addToContext(VelocityContext veloContext) {
        veloContext.put(name, valJO);
    }
}
