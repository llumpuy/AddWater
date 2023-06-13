package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

public class AddWaterCSVDataSource implements AddWaterDataSource{

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterContextDataSource.class);

    final private String COMMA_DELIMITER = ",";


    private String name;
    private String type;
    private String sourceFile;

    // Note: there is redundancy on this data structure, as (references to) hashmap keys
    // are replicated for each row. The decision favours usability in the template vs memory use
    // Time will tell if it is a reasonable tradeoff
    private Vector<Vector<String>> valMat;

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

    public AddWaterCSVDataSource (JSONObject jconfig)  {

        try {
            name = (String) jconfig.get("Name");
            type = (String) jconfig.get("Type");
            sourceFile = (String) jconfig.get("SourceFile");

            if (!type.equals("CSV")) {
                logger.error(String.format("Unexpected data source type for %s", name));
            }

            valMat = loadArray(sourceFile);

        } catch (Exception e) {
            logger.error(String.format("Error parsing data source file %s", sourceFile));
            logger.error(e.toString());
        }
    }

    private Vector<Vector<String>> loadArray(String sourceFile) {

        Vector<Vector<String>> mat = new Vector<Vector<String>>();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(sourceFile));

            // populate hashmap with rows
            String readRow;
            int rowNum = 0;
            Vector<String> matRow;
            int colSize = 0;
            int refCols = 0;

            while ((readRow = reader.readLine()) != null ) {
                String[] values = readRow.split(COMMA_DELIMITER);
                // skip empty or 'truncated' lines; use first row as reference
                if (rowNum == 0) {
                    refCols = values.length;
                } else if (values.length != refCols) {
                    continue;
                }
                matRow = new Vector<String>(Arrays.asList(values));

                mat.add(rowNum, matRow);
                rowNum++;
            }

        }  catch (Exception e) {
            logger.error(String.format("Error parsing data source file %s", sourceFile));
            logger.error(e.toString());
        }

        return mat;
    }

    public void addToContext(VelocityContext veloContext) {
        veloContext.put(name, valMat);
    }
}
