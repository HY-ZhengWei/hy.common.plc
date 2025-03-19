#!/bin/sh

cd ./bin


rm -R ./org/hy/common/plc/junit


jar cvfm hy.common.plc.jar MANIFEST.MF META-INF org com

cp hy.common.plc.jar ..
rm hy.common.plc.jar
cd ..





cd ./src
jar cvfm hy.common.plc-sources.jar MANIFEST.MF META-INF org com
cp hy.common.plc-sources.jar ..
rm hy.common.plc-sources.jar
cd ..
