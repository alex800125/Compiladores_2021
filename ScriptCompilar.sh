find . -type f -name "*.class" -exec rm -f {} \;
javac Main.java
java Main
find . -type f -name "*.class" -exec rm -f {} \;