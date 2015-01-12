#!/bin/bash
##############################################################################
# Copyright 2013 Institute for Molecular Imaging Instrumentation (I3M)
# 
# Licensed under the EUPL, Version 1.1 or - as soon they will be approved by 
# the European Commission - subsequent versions of the EUPL (the "Licence");
# You may not use this work except in compliance with the Licence.
# You may obtain a copy of the Licence at:
#
#   http://ec.europa.eu/idabc/eupl
#
# Unless required by applicable law or agreed to in writing, software 
# distributed under the Licence is distributed on an "AS IS" basis,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the Licence for the specific language governing permissions and 
# limitations under the Licence.
#
# This product combines work with different licenses. See the "NOTICE" text
# file for details on the various modules and licenses.
# The "NOTICE" text file is part of the distribution. Any derivative works
# that you distribute must include a readable copy of the "NOTICE" text file.
##############################################################################

##############################################################################
#
# Install Gpf4Med study service. Usage:
#
#   $ install-gpf4med.sh {version} {home} {templates} {connectors} {port}
#
# Example:
#
#   $ install-gpf4med.sh "1.0.0" "/tmp/gpf4med" "/tmp/gpf4med/data/templates" \
#     "/tmp/gpf4med/data/connectors" "8080"
#
# or call it with no arguments to install with defaults (same values as listed 
# in the previous example):
# 
#   $ install-gpf4med.sh
#
##############################################################################

#
# Set your preferences here
#
BASE_DIR=`pwd`
LOGFILE="${BASE_DIR}/install-gpf4med.log"

#
# Helper functions
#
logmsg () {
  if [ -z "$1" ]; then
    echo "-WARNING: No parameter passed to the log function-"
  else
    echo -n $1
    echo
    # write to log file
    echo >> ${LOGFILE}
    echo "#-#########################" >> ${LOGFILE}
    echo $1 >> ${LOGFILE}
    echo "#-#########################" >> ${LOGFILE}
    echo >> ${LOGFILE}
  fi
}

file_from_url () {
  if [ -z "$1" ]; then
    echo "-WARNING: No parameter passed to the file_from_url function-"
  else
    local VAR1=${1##*/}
    echo "${VAR1%%\?*}"
  fi
}

download_and_check () {
  if [ -z "$1" ] || [ -z "$2" ] || [ -z "$3" ] || [ -z "$4" ]; then
    echo "-WARNING: Invalid parameters passed to the download_and_check function-"
  else
    local OBJ_URL="$1"
    local OBJ_FILE="$2"
    
    local MD5_URL="$3"            
    local MD5_FILE="$4"    
    
    local CURL_TIME_COND=""
    if [ -f ${OBJ_FILE} ]; then
      CURL_TIME_COND="-z ${OBJ_FILE}"
    fi
    curl -m 120 --retry 3 --retry-delay 1 ${CURL_TIME_COND} -J -L \
-o "${OBJ_FILE}" "${OBJ_URL}" >> ${LOGFILE} 2>&1
    if [ "$?" -ne 0 ]; then
      echo
      echo "Download ${OBJ_URL} failed, see logs for details."
      exit 1
    fi
    
    logmsg "-Downloading: ${MD5_URL} to ${MD5_FILE}-"
    
    CURL_TIME_COND=""
    if [ -f ${MD5_FILE} ]; then
      CURL_TIME_COND="-z ${MD5_FILE}"
    fi
    curl -m 120 --retry 3 --retry-delay 1 ${CURL_TIME_COND} -J -L \
-o "${MD5_FILE}" "${MD5_URL}" >> ${LOGFILE} 2>&1
    if [ "$?" -ne 0 ]; then
      echo
      echo "Download ${MD5_URL} failed, see logs for details."
      exit 1
    fi
    
    logmsg "-Checking ${OBJ_FILE} using MD5 sum ${MD5_FILE}-"
    
    md5sum -c ${MD5_FILE} >> ${LOGFILE} 2>&1
    if [ "$?" -ne 0 ]; then
      echo
      echo "${OBJ_FILE} MD5 sums mismatches, see logs for details."
      exit 1
    fi
    
  fi
}

#
# Clean old logs
#
logmsg "-Removing old logs-"
cd ${BASE_DIR}
rm -f ${LOGFILE} >/dev/null 2>&1

#
# Check for proper number of command line arguments
#
if [ $# -ne "5" ] && [ $# -ne "0" ]; then
  APP_NAME=`basename $0`
  logmsg "-Usage- ${APP_NAME} {version} {home} {templates} {connectors} {port} | ${APP_NAME}"
  exit 1
fi

#
# Setup variables or use defaults
#
if [ -z "${1:-}" ]; then
  GPF4MED_VERSION="1.0.0"
  logmsg "-GPF4MED_VERSION- has not been declared or initialized, set to default $GPF4MED_VERSION"
else
  GPF4MED_VERSION="$1"
fi

if [ -z "${2:-}" ]; then
  GPF4MED_HOME="/tmp/gpf4med"
  logmsg "-GPF4MED_HOME- has not been declared or initialized, set to default $GPF4MED_HOME"
else
  GPF4MED_HOME="$2"
fi

if [ -z "${3:-}" ]; then
  GPF4MED_DICOM_TPLS="${GPF4MED_HOME}/data/templates"
  logmsg "-GPF4MED_DICOM_TPLS- has not been declared or initialized, set to default $GPF4MED_DICOM_TPLS"
else
  GPF4MED_DICOM_TPLS="$3"
fi

if [ -z "${4:-}" ]; then
  GPF4MED_GRAPH_CONN="${GPF4MED_HOME}/data/connectors"
  logmsg "-GPF4MED_GRAPH_CONN- has not been declared or initialized, set to default $GPF4MED_GRAPH_CONN"
else
  GPF4MED_GRAPH_CONN="$4"
fi

if [ -z "${5:-}" ]; then
  GPF4MED_PORT="8080"
  logmsg "-GPF4MED_PORT- has not been declared or initialized, set to default $GPF4MED_PORT"
else
  GPF4MED_PORT="$5"
fi

#
# Do the dirty work here
#

BASE_REPO_URL="http://sourceforge.net/projects/gpf4med/files/gpf4med/${GPF4MED_VERSION}"
DISTRO_FILE="gpf4med-distro-${GPF4MED_VERSION}.tar.gz"

logmsg "-Downloading Gpf4Med distribution-"

download_and_check "${BASE_REPO_URL}/${DISTRO_FILE}/download" "${DISTRO_FILE}" \
"${BASE_REPO_URL}/${DISTRO_FILE}.md5/download" "${DISTRO_FILE}.md5"

logmsg "-Installing Gpf4Med-"
mkdir -p "${GPF4MED_HOME}" >> ${LOGFILE} 2>&1 && tar xzf "${DISTRO_FILE}" -C "${GPF4MED_HOME}" >> ${LOGFILE} 2>&1
if [ "$?" -ne 0 ]; then
  echo
  echo "Extract Gpf4Med failed, see logs for details."
  exit 1
fi

if [[ ! "$GPF4MED_DICOM_TPLS" =~ ^[a-zA-Z]+[/]*:[^\\\\] ]]; then
  logmsg "-Installing Gpf4Med DICOM-SR templates-"
  TEMPLATES_FILE="dicom-sr-templates-${GPF4MED_VERSION}.tar.gz"
  
  mkdir -p "${GPF4MED_DICOM_TPLS}/${GPF4MED_VERSION}"
  if [ "$?" -ne 0 ]; then
    echo
    echo "Create Gpf4Med templates directory failed, see logs for details."
    exit 1
  fi  
  
  download_and_check "${BASE_REPO_URL}/${TEMPLATES_FILE}/download" "${TEMPLATES_FILE}" \
"${BASE_REPO_URL}/${TEMPLATES_FILE}.md5" "${TEMPLATES_FILE}.md5"
  
  tar xzf "${TEMPLATES_FILE}" -C "${GPF4MED_DICOM_TPLS}/${GPF4MED_VERSION}" >> ${LOGFILE} 2>&1
  if [ "$?" -ne 0 ]; then
    echo
    echo "Install Gpf4Med templates failed, see logs for details."
    exit 1
  fi
  
  echo "# DICOM Graph Store Templates" > "${GPF4MED_DICOM_TPLS}/${GPF4MED_VERSION}/index.txt"
  for f in $(ls "${GPF4MED_DICOM_TPLS}/${GPF4MED_VERSION}" | sed '/\.xml$/!d'); do
    {
      echo "file://${GPF4MED_DICOM_TPLS}/${GPF4MED_VERSION}/${f}"
    } >> "${GPF4MED_DICOM_TPLS}/${GPF4MED_VERSION}/index.txt"
  done
fi

if [[ ! "$GPF4MED_GRAPH_CONN" =~ ^[a-zA-Z]+[/]*:[^\\\\] ]]; then
  logmsg "-Installing Gpf4Med base plug-in-"
  BASE_GRAPH_FILE="gpf4med-graph-base-${GPF4MED_VERSION}.jar"
  
  mkdir -p "${GPF4MED_GRAPH_CONN}/${GPF4MED_VERSION}"
  if [ "$?" -ne 0 ]; then
    echo
    echo "Create Gpf4Med plug-in directory failed, see logs for details."
    exit 1
  fi
  
  download_and_check "${BASE_REPO_URL}/${BASE_GRAPH_FILE}/download" "${BASE_GRAPH_FILE}" \
"${BASE_REPO_URL}/${BASE_GRAPH_FILE}.md5" "${BASE_GRAPH_FILE}.md5"
  
  mv "${BASE_GRAPH_FILE}" "${GPF4MED_GRAPH_CONN}/${GPF4MED_VERSION}" >> ${LOGFILE} 2>&1
  if [ "$?" -ne 0 ]; then
    echo
    echo "Install Gpf4Med base plug-in failed, see logs for details."
    exit 1
  fi
  
  echo "# DICOM Graph Store Connectors" > "${GPF4MED_GRAPH_CONN}/${GPF4MED_VERSION}/index.txt"
  for f in $(ls "${GPF4MED_GRAPH_CONN}/${GPF4MED_VERSION}" | sed '/\.jar$/!d'); do
    {
      echo "file://${GPF4MED_GRAPH_CONN}/${GPF4MED_VERSION}/${f}"
    } >> "${GPF4MED_GRAPH_CONN}/${GPF4MED_VERSION}/index.txt"
  done  
fi

logmsg "-Applying configuration-"
{
  echo "<config>"
  echo "  <gpf4med-root>${GPF4MED_HOME}</gpf4med-root>"
  echo "  <storage>"
  echo "    <templates>${GPF4MED_DICOM_TPLS}</templates>"
  echo "    <connectors>${GPF4MED_GRAPH_CONN}</connectors>"
  echo "    <local-cache>${GPF4MED_HOME}/var/cache/gpf4med</local-cache>"
  echo "    <htdocs>${GPF4MED_HOME}/htdocs</htdocs>"
  echo "  </storage>"
  echo "  <security>"
  echo "    <encrypt-local-storage>true</encrypt-local-storage>"
  echo "    <use-strong-cryptography>false</use-strong-cryptography>"
  echo "  </security>"
  echo "  <dicom>"
  echo "    <version>${GPF4MED_VERSION}</version>"
  echo "    <index>\${storage.templates}/\${dicom.version}/index.txt</index>"
  echo "  </dicom>"
  echo "  <graph>"
  echo "    <version>${GPF4MED_VERSION}</version>"
  echo "    <index>\${storage.connectors}/\${graph.version}/index.txt</index>"
  echo "  </graph>"
  echo "</config>"
} > "gpf4med.xml"

{
  echo "<config>"
  echo "  <service-container>"
  echo "    <port>${GPF4MED_PORT}</port>"
  echo "  </service-container>"
  echo "</config>"
} > "gpf4med-container.xml"

mv -f gpf4med.xml gpf4med-container.xml ${GPF4MED_HOME}/etc/ >> ${LOGFILE} 2>&1
if [ "$?" -ne 0 ]; then
  echo
  echo "Configure Gpf4Med failed, see logs for details."
  exit 1
fi

sed -i.bkp -e 's|^[[:space:]]*<property.*name[[:space:]]*=[[:space:]]*[\"]*LOG_FILES_LOCATION[\"]*[[:space:]]*.*|    <property name=\"LOG_FILES_LOCATION\" value=\"'${GPF4MED_HOME}/var/log/gpf4med'\" \/>|' "${GPF4MED_HOME}/etc/logback.xml" >> ${LOGFILE} 2>&1

logmsg "-Starting service-"
${GPF4MED_HOME}/bin/gpf4med start >> ${LOGFILE} 2>&1
if [ "$?" -ne 0 ]; then
  echo
  echo "Start Gpf4Med failed, see logs for details."
  exit 1
fi

sleep 10

if [ "$(${GPF4MED_HOME}/bin/gpf4med status)" != "gpf4med is running" ]; then
  echo
  echo "Gpf4Med is stopped, see logs for details."
  exit 1
fi

#
# we are done, exiting
#
logmsg "-Exiting-"
cd $BASE_DIR

exit 0
