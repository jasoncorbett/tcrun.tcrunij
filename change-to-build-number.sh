#!/bin/bash

find . -name 'pom.xml' |xargs perl -pi -e "s/SNAPSHOT/${BUILD_NUMBER}/g"
