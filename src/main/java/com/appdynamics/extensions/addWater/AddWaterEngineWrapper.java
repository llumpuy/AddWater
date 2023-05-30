package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.velocity.app.VelocityEngine;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

public class AddWaterEngineWrapper {

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterEngineWrapper.class);

    private VelocityEngine ve;
    private String vEncode;
    private String vLoader;
    private String vLoaderDesc;
    private String vFileLoaderClass;
    private String vFileLoaderPath;
    private String vFileCache;
    private String configFileName;

    private ArrayList<AddWaterOutcome> outcomeList = new ArrayList<AddWaterOutcome>();

    public VelocityEngine getVe() {
        return ve;
    }
    public String getvEncode() {
        return vEncode;
    }
    public String getvLoader() {
        return vLoader;
    }
    public String getvLoaderDesc() {
        return vLoaderDesc;
    }
    public String getvFileLoaderClass() {
        return vFileLoaderClass;
    }
    public String getvFileLoaderPath() {
        return vFileLoaderPath;
    }
    public String getvFileCache() {
        return vFileCache;
    }
    public String getConfigFileName() {
        return configFileName;
    }

    AddWaterEngineWrapper(String confFile) {

        configFileName = confFile;
        logger.info(String.format("Loading configuration file %s", configFileName));

        try {
            FileReader reader = new FileReader(configFileName);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonConfig = (JSONObject) jsonParser.parse(reader);

            // Get Velocity Engine Attributes
            JSONObject veloConf = (JSONObject) jsonConfig.get("Velocity");

            vEncode = (String) veloConf.get("Encoding");
            vLoader = (String) veloConf.get("Loader");
            vLoaderDesc = (String) veloConf.get("Description");
            vFileLoaderClass = (String) veloConf.get("FileLoaderClass");
            vFileLoaderPath = (String) veloConf.get("FileLoaderPath");
            vFileCache = (String) veloConf.get("FileLoaderCache");

            // Create & initialise Velocity Engine
            Properties prop = new Properties();
            /*
            prop.put("runtime.log.logsystem.class", "org.apache.logging.log4j.Logger");
            prop.put("runtime.log.logsystem.log4j.category", "velocity");
            prop.put("runtime.log.logsystem.log4j.logger", "velocity");
            */

            prop.setProperty("input.encoding", vEncode);
            prop.setProperty("output.encoding", vEncode);
            prop.setProperty("resource.loader", vLoader);
            prop.setProperty("file.resource.loader.description", vLoaderDesc);
            prop.setProperty("file.resource.loader.class", vFileLoaderClass);
            prop.setProperty("file.resource.loader.path", vFileLoaderPath);
            prop.setProperty("file.resource.loader.cache", vFileCache);
            prop.setProperty("runtime.log", "./logs/velocity.log");
            prop.setProperty("runtime.strict_mode.enable", "true");

            ve = new VelocityEngine(prop);
            ve.init(prop);

            // iterate over all Outcome specs & instantiate them
            JSONArray jobArray = (JSONArray) jsonConfig.get("Outcomes");
            Iterator jai = jobArray.iterator();
            while (jai.hasNext()) {
                JSONObject outSpec = (JSONObject) jai.next();
                outcomeList.add(new AddWaterOutcome(ve, outSpec));
            }
        } catch (ParseException jex) {
            logger.error(String.format("Error parsing configuration file %s", configFileName));
            logger.error(String.format("%s", jex.toString()));
        } catch (java.io.FileNotFoundException fnf) {
            logger.error(String.format("Configuration file %s not found", configFileName));
            logger.error(String.format("%s", fnf.toString()));
        } catch (Exception ex) {
            logger.error(String.format("Error loading configuration"));
            logger.error(String.format("%s", ex.toString()));
        }
        logger.info(String.format("Configuration file %s loaded successfully", configFileName));
    }

    void apply() {
        // apply across all elements in outcomeList
        // note again  the current implementation assumes a sequential execution of
        // outcomes, as specified in the config file; this is to allow dependent executions
        // (e.g. one outcome producing as output a template that a subsequent outcome will use)

        for (int i = 0; i < outcomeList.size(); i++) {
            outcomeList.get(i).apply();
        }
        logger.info(String.format("%d outcomes applied", outcomeList.size()));
    }

}
