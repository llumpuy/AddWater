package com.appdynamics.extensions.addWater;

import org.apache.velocity.VelocityContext;

public interface AddWaterDataSource {

    // the Source's "name" is used as the starting reference for its data
    // within the template. E.g. if a data source has the name "servers"
    // (as a Vector of Vectors, from a CSV file), accessing the first
    // entry from the first column will look in the template as:
    // $servers.get(0).get(0)
    void setName(String name);
    String getName();

    // the source type is an arbitrary name to represent the type of data
    // it contains. It is mostly used for tracing/debugging. Examples can
    // be "CSV" or "MAP"
    void setType(String typeName);
    String getType();

    // the source locator is a string that identifies where data will be extracted
    // from (but may not be sufficient to access it -e.g. authentication details
    // which would need to be managed by the specific  implementation). This can
    // be a file name -on a local file system-, a URL, etc.
    void setSource(String sourceName);
    String getSource();


    // this method allows the specific memory representation of a data source into
    // a Velocity context -for its eventual merge with a template.
    // Each implementation of this interface defines the specific class and access
    // methods that will be made available to the Velocity engine
    public void addToContext(VelocityContext veloContext);

}
