package com.appdynamics.extensions.addWater;

import com.appdynamics.extensions.addWater.AddWaterDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.velocity.VelocityContext;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AddWaterXMLFileDataSource implements AddWaterDataSource {

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterDataSourceFactory.class);

    private String name;
    private String type;
    private String sourceFile;

    private org.w3c.dom.Document valDoc;
    private org.w3c.dom.NodeList valNodeList;
    private org.json.JSONObject valJO;

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

    public AddWaterXMLFileDataSource(JSONObject jconfig)  {

        try {
            name = (String) jconfig.get("Name");
            type = (String) jconfig.get("Type");
            sourceFile = (String) jconfig.get("SourceFile");

            if (!type.equals("XMLFILE")) {
                logger.error(String.format("Unexpected data source type for %s", name));
            }

            loadXO(sourceFile);

        } catch (Exception e) {
            logger.error(String.format("Error parsing data source  %s", name));
            logger.error(e.toString());
        }
    }

    private void loadXO(String fileName) {
        DocumentBuilder builder;

        try {

            Path filePath = Paths.get(fileName);

            byte[] bytes = Files.readAllBytes(filePath);
            valJO = org.json.XML.toJSONObject(new String(bytes));

        } catch (IOException e) {
            logger.error(String.format("Error accessing % for data source  %s", fileName, name));
            logger.error(e.toString());
        }
    }

    @Override
    public void addToContext(VelocityContext veloContext) {

        // note: Velocity will associate to these classes:
        // class com.sun.org.apache.xerces.internal.dom.DeferredDocumentImpl
        // class com.sun.org.apache.xerces.internal.dom.DeepNodeListImpl
        veloContext.put(name, valJO);
    }
}
