# Installation guide

**Please refer to the project's Web site for the latest version of the documentation.**

## Quick build

``$ cd WORKSPACE/gpf4med/``

``$ cd clean generate-sources -P generate``

``$ mvn clean install -pl gpf4med-external-deps,gpf4med-core,gpf4med-api,gpf4med-data,gpf4med-service``

``$ mvn clean package -pl gpf4med-distro``

``$ java -jar gpf4med-distro/target/gpf4med.jar -d``

``$ mvn clean package -pl gpf4med-graph-base``

``$ scp gpf4med-graph-base/target/gpf4med-graph-base-1.1.0.jar dedalo.i3m.upv.es:/usr/local/sw/gpf4med/connectors/1.1.0/``

``$ mvn clean package -pl gpf4med-enactor``

```$ mvn clean install -pl gpf4med-external-deps,gpf4med-core,gpf4med-api,gpf4med-data ; mvn clean package -pl gpf4med-graph-base ; \
  scp gpf4med-graph-base/target/gpf4med-graph-base-1.1.0.jar \
  dedalo.i3m.upv.es:/usr/local/sw/gpf4med/connectors/1.1.0/ ; \
  mvn clean install -pl gpf4med-service ; mvn clean package -pl gpf4med-distro ; \
  mvn clean package -pl gpf4med-enactor```

## MD5 sum

``$ md5sum gpf4med-distro-1.1.0.tar.gz > gpf4med-distro-1.1.0.tar.gz.md5``

## DICOM templates

``$ cd gpf4med-core/src/test/resources/files/templates``

``$ tar cvzf dicom-sr-templates-1.1.0.tar.gz *.xml``

``$ md5sum dicom-sr-templates-1.1.0.tar.gz > dicom-sr-templates-1.1.0.tar.gz.md5``

## Development

``$ export JAVA_HOME=/usr/local/sw/java/jdk1.7.0_45 ; export PATH=$JAVA_HOME/bin:$PATH ; java -version``

``List of Maven archetypes: http://maven-repository.com/artifact/org.glassfish.jersey.archetypes``

```$ mvn archetype:generate -DarchetypeGroupId=org.glassfish.jersey.archetypes \
-DarchetypeArtifactId=jersey-quickstart-grizzly2 -DarchetypeVersion=2.4.1```

``$ mvn clean compile exec:java -pl gpf4med-service``

``http://localhost:8080/myapp/application.wadl``

``http://localhost:8080/myapp/myresource``

* Jersey Grizzly support: https://jersey.java.net/apidocs/latest/jersey/index.html?org/glassfish/jersey/grizzly2/httpserver/GrizzlyHttpServerFactory.html
* Grizzly core configuration: https://grizzly.java.net/coreconfig.html

## Deprecated

$ mvn archetype:generate -DarchetypeGroupId=org.glassfish.jersey.archetypes \
-DarchetypeArtifactId=jersey-quickstart-webapp -DarchetypeVersion=2.4.1

## TODO

* Grizzly can serve static HTTP content from JAR files. This feature could be used to serve documentation 
  from the connectors. An idea could be to add the documentation to the plugins in a separated JAR file
  that could be server by Grizzly when the connector plugin is loaded.
  
  https://mytecc.wordpress.com/2013/06/06/grizzly-2-3-3-serving-static-http-resources-from-jar-files/

* Authentication: the container could have a list of authorized DICOM stores and connector providers.
