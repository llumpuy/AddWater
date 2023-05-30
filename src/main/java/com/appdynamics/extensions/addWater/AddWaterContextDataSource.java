package com.appdynamics.extensions.addWater;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.velocity.VelocityContext;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AddWaterContextDataSource {

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterContextDataSource.class);

    final private String COMMA_DELIMITER = ",";

    private String name;
    private String type;
    private String sourceFile;

    // valMap supports simple property maps; they can be referred to in the templates as
    //  $sourceName.get(<property>)
    private HashMap<String, String> valMap;

    // valMat supports a matrix of attributes -in columns- with value sets -in rows; they
    // can be accessed in templates as
    // $sourceName[i].get(<property>)

    // Note: there is redundancy on this data structure, as (references to) hashmap keys
    // are replicated for each row. The decision favours usability in the template vs memory use
    // Time will tell if it is a reasonable tradeoff
    private Vector<HashMap<String, String>> valMat;
    //private ArrayList<HashMap<String, String>> valMat;

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getSourceFile() {
        return sourceFile;
    }
    public HashMap<String, String> getMap() {
        if (type.equals("MAP")) {
            return valMap;
        } else{
            return new HashMap<String, String>();
        }
    }
    public Vector<HashMap<String, String>> getTable() {
        if (type.equals("CSV")) {
            return valMat;
        } else{
            return new Vector<HashMap<String, String>>();
        }
    }

    AddWaterContextDataSource (JSONObject jconfig) {

        try {
            name = (String) jconfig.get("Name");
            type = (String) jconfig.get("Type");
            sourceFile = (String) jconfig.get("SourceFile");

            // load contents from file depending on type
            switch (type) {
                case "MAP":
                    valMap = loadMap(sourceFile);
                    break;
                case "CSV":
                    valMat = loadArray(sourceFile);
                    break;
                default:
                    logger.error(String.format("Unknown data source time in %s", sourceFile));
            }
        } catch (Exception e) {
            logger.error(String.format("Error parsing data source file %s", sourceFile));
            logger.error(e.toString());
        }
    }

    HashMap<String, String> loadMap(String sourceFile) {

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

    Vector<HashMap<String, String>> loadArray(String sourceFile) {

        Vector<HashMap<String, String>> mat = new Vector<HashMap<String, String>>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(sourceFile));

            // read header line and set hashmap
            String headerLine;
            String[] headers;

            if ((headerLine = reader.readLine()) != null) {
                headers = headerLine.split(COMMA_DELIMITER);

            } else {
                logger.error(String.format("Empty data source file %s", sourceFile));
                return null;
            }

            // populate hashmap with rows
            String row;
            int rowNum = 0;
            HashMap<String, String> element;

            while ((row = reader.readLine()) != null ) {
                String[] values = row.split(COMMA_DELIMITER);
                element = new HashMap<String, String>();

                for (int i = 0; i < values.length; i++) {
                    element.put(headers[i], values[i]);
                }
                mat.add(rowNum, element);
                rowNum++;
            }

        }  catch (Exception e) {
            logger.error(String.format("Error parsing data source file %s", sourceFile));
            logger.error(e.toString());
        }

        return mat;
    }

    void addToContext(VelocityContext veloContext) {
        HashMap<String, String> element = new HashMap<String, String>();

        switch (type) {
            case "MAP":
                veloContext.put(name, valMap);
                break;
            case "CSV":
                veloContext.put(name, valMat);
                veloContext.put("element", element);
            default:
        }
    }

}
