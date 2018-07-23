rem set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_121\jre

for %%i in (..\lib\*.jar) do set CLASSPATH=!CLASSPATH!;%%i

set T4A_VER=4.1.0-beta5
set CLASSPATH=%CLASSPATH%;..\..\twitter4a-examples\build\libs\twitter4a-examples-%T4A_VER%.jar
set CLASSPATH=%CLASSPATH%;..\..\twitter4a-core\build\libs\twitter4a-core-%T4A_VER%.jar
set CLASSPATH=%CLASSPATH%;..\..\twitter4a-stream\build\libs\twitter4a-stream-%T4A_VER%.jar

set MEM_ARGS=-Xms30m -Xmx30m
