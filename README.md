# cacheviewhandler

As of mid-December 2021, this handler requires changes to Solr source code in order to function.  It is hoped that this functionality can be integrated directly into Solr so this package will no longer be required.

If you want to edit this code, it imports into Eclipse without trouble.  It will probably also import into IntelliJ Idea and other IDEs that can handle Java gradle projects.

If using Eclipse, before importing the project, go to the eclipse marketplace and install Spring Tools 4 and Spring Tools 3 Add-On for Spring Tools 4.  You will also need Buildship, for Gradle integration, but this should be installed by default in recent eclipse versions.

There is probably some kind of Spring Tools package for IntelliJ as well that should be installed before importing this project.

## Tools needed to build:

* A POSIX operating system.  Linux is known to work.
* perl 5.x
* wget
* git, a recent version is strongly recommended.
* A JDK, version 8, preferably the latest 8.x version you can get.

## Building:

* If you are running the Solr version that is mentioned in the setupandbuild.sh script, you can skip the rest of these steps and just run this one command to build:
    * ./gradlew clean assemble
* Edit the setupandbuild.sh script to customize it for your environment.
    * The usual work location is /opt.  If you want to put it somewhere else, change that variable.  Once things are built, you will need to copy jars from that location to your Solr install.
    * Update the SOLR_VERSION variable if necessary.
    * The build will work without JAVA_HOME if java programs are in the PATH, and there is only one JDK installed.  Otherwise, you will need to find where the JDK lives on your system and set the variable.  The path included by default is where openjdk version 8 is located on Ubuntu.
* Run the setupandbuild.sh script.  This will download nearly 600MB and then build Solr from scratch, so it will take a while to run.

## Installing this package:

* Build the package as outlined above.
* Create a directory called lib in the Solr Home.  This is typically where solr.xml and core directories live.  Copy the jar with "-plain" in the filename from the build/libs directory to that new lib directory.
* Replace the solr-core jar in your Solr webapp with a custom one.
    * If you skipped most of the build instructions because you're running the same Solr version that the build script mentions, then the source for the solr-core jar will be the "lib" directory in this repo.  If you did the full build, the proper solr-core jar will be in ${WORK_LOCATION}/lucene-solr/solr/dist instead.
    * Copy the solr-core jar to your Solr install directory, in the server/solr-webapp/webapp/WEB-INF/lib directory.  If you did things right, the new jar will have the same name as the old one and will overwrite it.  If you find that you have two versions of the solr-core jar, delete the one that you just copied, and try the build again, reading the instructions more carefully.
* In a core's solrconfig.xml, add this line next to other requestHandler definitions:
    * <requestHandler name="/admin/info/cache" class="org.elyograg.solr.handler.CacheViewHandler" />
* To use, access the following URL, substituting correct values for the all-uppercase parts:
    * http://SERVER:PORT/solr/CORE/admin/info/cache?cache=filter
    * So far, the only cache that is available is filter.
