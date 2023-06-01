# AddWater V0.5
A tool to merge datafiles (maps, csvs) with a Velocity template

AddWater uses Apache Velocity to merge VTL templates with data sources (as today: json files -to be contextualised in Velocity as HashMap<String,String>-, or csv files -contextualised as Vector<Vector\<String>>).
  
The original motivation is to allow the creation of custom AppD configuration files/API payloads (for Health Rules, Dashboards, etc.) from a general structure (the template) that needs to be applied to a list of objects. Typical use cases are:
* a custom dashboard with a set of  widgets, one set for each key server specified on a CSV file
* the API payload to update http request templates that must be sensitive to the environment they must be imported into
* the API payload to create users from a CSV file 

Executing the jar requires only one parameter, the name of the configuration file. This configuration file allows to specify a list of outcomes (files) to be produced from a given VTL template and a set of data sources. For each outcome, if is possible to specify multiple data sources. Each data source has a name to be used when referring to it within the template.

This is a commented example of a configuration file:

\<TODO\>


You can find detailed information on creating VTL templates in https://velocity.apache.org/engine/devel/user-guide.html


 
