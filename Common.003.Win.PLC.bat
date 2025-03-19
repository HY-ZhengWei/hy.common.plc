

del /Q hy.common.plc.jar
del /Q hy.common.plc-sources.jar


call mvn clean package
cd .\target\classes

rd /s/q .\org\hy\common\plc\junit


jar cvfm hy.common.plc.jar META-INF/MANIFEST.MF META-INF org

copy hy.common.plc.jar ..\..
del /q hy.common.plc.jar
cd ..\..





cd .\src\main\java
xcopy /S ..\resources\* .
jar cvfm hy.common.plc-sources.jar META-INF\MANIFEST.MF META-INF org
copy hy.common.plc-sources.jar ..\..\..
del /Q hy.common.plc-sources.jar
rd /s/q META-INF
cd ..\..\..

pause