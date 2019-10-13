# compile the sources
mvn compile

# remove dist and generate java runtime
rm -rf dist
jlink --no-header-files --no-man-pages --compress=2 --strip-debug --module-path \
target/classes --add-modules java.base,java.logging,java.management,bigsort --output dist

# create shell script to run
touch dist/run.sh
echo "./bin/java --module bigsort/bigsort.App" > dist/run.sh
chmod +x dist/run.sh