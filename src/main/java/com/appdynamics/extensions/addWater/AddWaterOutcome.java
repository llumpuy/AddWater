package com.appdynamics.extensions.addWater;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;

import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class AddWaterOutcome {

    //initialise logger
    private static final Logger logger = LogManager.getLogger(AddWaterOutcome.class);

    private VelocityEngine ve;
    private VelocityContext vc;
    private String name;
    private String outFileName;
    private FileWriter outFile;
    private Template templ;
    private String templName;

    private ArrayList<AddWaterDataSource> dataSources = new ArrayList<AddWaterDataSource>();

    VelocityEngine getVelocityEngine() {
        return ve;
    }

    VelocityContext getVelocityContext() {
        return vc;
    }

    Template getTemplate() {
        return templ;
    }

    String getTemplName() {
        return templName;
    }

    AddWaterOutcome(VelocityEngine veloE, JSONObject spec) {
        ve = veloE;
        vc = new VelocityContext();
        AddWaterDataSourceFactory sourceFactory = new AddWaterDataSourceFactory();

        try {
            name = (String) spec.get("Name");
            templName = (String) spec.get("TemplateFile");
            outFileName = (String) spec.get("OutputFile");

            // iterate over data sources specs, instantiate and add to context vc;
            JSONArray jobArray = (JSONArray) spec.get("DataSources");
            Iterator jai = jobArray.iterator();
            while (jai.hasNext()) {
                JSONObject dspec = (JSONObject) jai.next();
                AddWaterDataSource ds = sourceFactory.makeDataSource(dspec);
                //AddWaterContextDataSource ds = new AddWaterContextDataSource(dspec);

                //Temporary exclusion until data source types V1.0 are completed
                if (ds != null) {
                    ds.addToContext(vc);
                    dataSources.add(ds);
                }
            }

            // as utility context items, include classes in the contezt; this allows for
            // simple conversions & arithmetic from their static methods

            vc.put(Integer.class.getSimpleName(), Integer.class);
            vc.put(String.class.getSimpleName(), String.class);
            vc.put(Date.class.getSimpleName(), Date.class);

        } catch (Exception ex) {
            logger.error(String.format("Error parsing Outcome %s", name));
            logger.error(String.format("%s", ex.toString()));
        }

        // get the template for this outcome
        try {
            templ = ve.getTemplate(templName);
        } catch (ResourceNotFoundException rnfe) {
            logger.error(String.format("Could not find template %s", templName));
            logger.debug(rnfe.toString());
        }
    }

    void apply() {
        try {
            StringWriter writer = new StringWriter();
            outFile = new FileWriter(outFileName);

            templ.merge(vc, writer);

            outFile.write(writer.toString());
            outFile.close();
        } catch (ResourceNotFoundException rnfe) {
            logger.error(String.format("Could not find template %s for outcome %s", templName, name));
            logger.error(rnfe.toString());
        } catch (ParseErrorException pee) {
            logger.error(String.format("Error parsing template %s for outcome %s", templName, name));
            logger.error(pee.toString());
        } catch (MethodInvocationException mie) {
            logger.error(String.format("Error in method invocation within template %s for outcome %s", templName, name));
            logger.error(mie.toString());
        } catch (Exception e) {
            logger.error(String.format("Unknown error merging data with template %s for outcome %s", templName, name));
            logger.error(e.toString());
        }
    }
}


