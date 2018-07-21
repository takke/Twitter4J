rem set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_121\jre

for %%i in (..\lib\*.jar) do set CLASSPATH=!CLASSPATH!;%%i


rem set CLASSPATH=!CLASSPATH!;twitter4j-examples-4.1.0-beta4.jar
set CLASSPATH=%CLASSPATH%;D:\Src\twitter4j_work\twitter4j\twitter4j-examples\build\libs\twitter4j-examples-4.1.0-beta4.jar
set CLASSPATH=%CLASSPATH%;D:\Src\twitter4j_work\twitter4j\twitter4j-core\build\libs\twitter4j-core-4.1.0-beta4.jar
set CLASSPATH=%CLASSPATH%;D:\Src\twitter4j_work\twitter4j\twitter4j-stream\build\libs\twitter4j-stream-4.1.0-beta4.jar

set MEM_ARGS=-Xms30m -Xmx30m
