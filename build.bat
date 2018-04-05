@echo off

rem
rem set JAVA_HOME=C:\java\jdk1.8.0_152
rem set PATH=%PATH%;%JAVA_HOME%\bin
rem
rem jar cvfm ..\lib\pjm-connect-0.0.1.jar ..\manifest\manifestPJMConnect.txt .\org\enb\iso\*
rem
rem keytool -list -keystore "%JAVA_HOME%/jre/lib/security/cacerts"
rem
rem keytool -importkeystore -srckeystore C:\java\jdk1.8.0_152\jre\lib\security\cacerts -destkeystore C:\java\jdk1.8.0_152\jre\lib\security\cacerts -deststoretype pkcs12
rem
rem keytool.exe -importkeystore -srckeystore mrm-oati-cert.pfx -srcstoretype pkcs12 -destkeystore %JAVA_HOME%jre\lib\security\cacerts -deststoretype JKS
rem
rem keytool -import -v -trustcacerts -alias markets.midwestiso.org -file markets.midwestiso.org.crt -keystore %JAVA_HOME%/jre/lib/security/cacerts -keypass changeit -storepass changeit
rem

set oldDir=%cd%
set rootDir="c:\Users\edwardk3\PortableApps\LivITy\.babun\cygwin\home\edwardk3\workspace\jiso-tool"
rem set javaRoot="D:\Openlink\Endur\V16_1_12012017MR_12152017_1042\bin.win64\olf_dependencies\java\jdk1.8.0_45\bin\"
rem set javaRoot=

cd %rootDir%
%javaRoot%javac -d . -cp ..\lib\* PJMConnect.java >nul 2>&1
%javaRoot%jar cvfm ..\lib\pjm-connect-0.0.1.jar ..\manifest\manifestPJMConnect.txt .\org\enb\iso\* >nul 2>&1
%javaRoot%javac -d . -cp ..\lib\* SamplePJMConnect.java >nul 2>&1

echo Below are two sample report requests
echo -r = Report Name
echo -c = Record Count
echo -s = Start Row

echo.

echo Running: java -cp ..\lib\*;. SamplePJMConnect -r agg_definitions -c2 -s2
%javaRoot%java -cp ..\lib\*;. SamplePJMConnect -r agg_definitions -c2 -s2

echo.

echo Running: java -cp ..\lib\*;. SamplePJMConnect -r agg_definitions -c2 -s3 -d
%javaRoot%java -cp ..\lib\*;. SamplePJMConnect -r agg_definitions -c2 -s3 -d

cd %oldDir%
