#!/bin/bash

path="$0"

while [ -L "$path" ]; do
	dir=`dirname "$path"`
	path=`ls -l "$path" | sed -e 's/.* -> //'`
	cd "$dir"
done

dir=`dirname "$path"`
file=`basename "$path"`
cdir=`cd "$dir" && pwd -P`
TCRUNIJ_SH="$cdir/$file"
TCRUNIJ_HOME="$(dirname "${TCRUNIJ_SH}")"

for jar in `ls $TCRUNIJ_HOME/lib`;
do
	if [ -z "${CLASSPATH}" ];
	then
		CLASSPATH="lib/${jar}"
	else
		CLASSPATH="${CLASSPATH}:lib/${jar}"
	fi
done

JAVA=`which java`

if [ -n "${JAVA_HOME}" ]
then
	JAVA="${JAVA_HOME}/bin/java"
fi

if [ ! -x "${JAVA}" ]
then
	echo "Unable to find java, please either set JAVA_HOME or add the directory where java executable is to the PATH."
	exit -1
fi

export CLASSPATH

TESTRUNID=`date |sed -e 's/ /-/g' -e 's/:/-/g'`

export TESTRUNID

"${JAVA}" ${JAVA_OPTS} -Dlogback.configurationFile=conf/logging-config.xml org.tcrun.cmd.Main "$@"

if [ -L "${TCRUNIJ_HOME}/results/last" ]
then
	rm -f "${TCRUNIJ_HOME}/results/last"
fi

ln -s "${TCRUNIJ_HOME}/results/${TESTRUNID}" "${TCRUNIJ_HOME}/results/last"
