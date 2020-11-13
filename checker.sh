rm -rf result
rm -rf bin

javac -sourcepath src -cp "libs/*:.jar" src/main/Main.java -d bin

java -cp ".:bin/:libs/jackson-annotations-2.9.3.jar:libs/jackson-core-2.9.3.jar:libs/jackson-databind-2.9.9.3.jar:libs/json-20140107.jar:libs/json-lib-2.4-jdk15.jar:libs/org.json.simple-0.3-incubating.jar" main.Main

rm -rf result
rm -rf bin