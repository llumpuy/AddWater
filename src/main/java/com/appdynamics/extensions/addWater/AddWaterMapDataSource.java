package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class AddWaterMapDataSource implements AddWaterDataSource {

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterMapDataSource.class);

    private String name;
    private String type;
    private String sourceFile;

    private HashMap<String, String> valMap;

    public String getName() {
        return name;
    }
    public void setName (String sourceName) {
        name = sourceName;
    }
    public String getType() {
        return type;
    }
    public void setType(String sourceType) {
        type = sourceType;
    }
    public String getSource() {
        return sourceFile;
    }
    public void setSource(String resourceName) {
        sourceFile = resourceName;
    }

    public AddWaterMapDataSource (JSONObject jconfig)  {

        try {
            name = (String) jconfig.get("Name");
            type = (String) jconfig.get("Type");
            sourceFile = (String) jconfig.get("SourceFile");

            if (!type.equals("MAP")) {
                logger.error(String.format("Unexpected data source type for %s", name));
            }

            valMap = loadMap(sourceFile);

        } catch (Exception e) {
            logger.error(String.format("Error parsing data source file %s", sourceFile));
            logger.error(e.toString());
        }
    }

    private HashMap<String, String> loadMap(String sourceFile) {

        HashMap<String, String> valMap = new HashMap<String, String>();

        try {
            FileReader reader = new FileReader(sourceFile);

            JSONParser jsonParser = new JSONParser();
            JSONObject mapData = (JSONObject) jsonParser.parse(reader);

            Set<String> keys = mapData.keySet();
            Iterator<String> keyIter = keys.iterator();
            String iterValue;

            while (keyIter.hasNext()) {
                iterValue = (String) keyIter.next();
                valMap.put(iterValue, (String) mapData.get(iterValue));
            }

        } catch(ParseException jex) {
            logger.error(String.format("Error parsing JSON content in data source file %s", sourceFile));
            logger.error(jex.toString());
        } catch (Exception e) {
            logger.error(String.format("Unknown error loading data source file %s", sourceFile));
            logger.error(e.toString());
        }
        return valMap;
    }

    public void addToContext(VelocityContext veloContext) {
        veloContext.put(name, valMap);
    }

}
