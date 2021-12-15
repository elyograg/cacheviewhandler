#!/bin/bash
WORK_LOCATION=/opt
ANT_VERSION=1.10.12
SOLR_VERSION=8.11.0
#-- If multiple JDKs are installed, uncomment the following
#-- and point it at the appropriate version 8 JDK.
#export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64

##############################
mkdir -p ${WORK_LOCATION}
cd ${WORK_LOCATION}
git clone https://github.com/elyograg/cacheviewhandler.git
git clone https://github.com/apache/lucene-solr.git
wget https://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz
tar zxf apache-ant-${ANT_VERSION}-bin.tar.gz
export ANT_HOME=${WORK_LOCATION}/apache-ant-${ANT_VERSION}
cd lucene-solr
git checkout releases/lucene-solr/${SOLR_VERSION}
git apply -v -3 ${WORK_LOCATION}/cacheviewhandler/solr-cache-add-keyset.patch
cd solr
${ANT_HOME}/bin/ant ivy-bootstrap
${ANT_HOME}/bin/ant -Dversion=${SOLR_VERSION} clean dist
rm -f ${WORK_LOCATION}/cacheviewhandler/lib/solr-core-*
cp -vf dist/solr-core-* ${WORK_LOCATION}/cacheviewhandler/lib/.
cd ${WORK_LOCATION}/cacheviewhandler
./gradlew clean assemble
