# About the app

Commandline app.
* Currently implementation configured for reading CSV / TSV but intended for more generic data files.
* Using CSVReader does not add benefit over current impl.
* Other data type configurations easily added. 
* Reads into memory line by line and sorts in memory by default.
* Uses merge sort after file size is beyond configurable size
* Optional filter on project

#### Files included for testing
* ```smalldata.txt``` has header and few lines to test POC.
* ```createLargeFile.py``` is a python script to create arbitrarily large files for both functional and performance test. (Warning: can use a lot of disk space :))
* ```dataheader.txt``` and ```data-template.txt``` are used by the python script. ```data-template.txt``` contains no header, just content + empty lines + commented lines.
* ```emptyFile.txt``` for that usecase.
* ```largedatafile.txt``` doesn't exist yet, but is the output of createLargeFile.py.

### Create a large file:
* Step 1: inside createLargeFile.py set totalRounds to a number (most fun = 8-15, after that takes long)
* Step 2: run python createLargeFile.py

It takes ```data-template.txt``` and doubles it ```<totalRounds>``` amount of times, and finally prepends the header from ```dataheader.txt```

The ratio of content in ```data-template.txt``` and therefore in created files in this way is:
* Total 57 lines
* 12 empty lines (21 %)
* 5 commented lines (9 %)
* project 1 has 5 lines (9 %)
* project 2 has 7 lines (12 %)
* project 3 has 7 lines (12 %)
* project 4 has 7 lines (12 %)
* project 5 has 7 lines (12 %)
* project 6 has 15 lines (26 %)

See many lines are in a created file : ```cat filename | wc -l```

# Build project (no need to start with this, jar provided in /target)

Requirements:
* Maven
* Java 11

Run only unit tests: ```mvn test```

Complete build: ```mvn clean install```

Build without unit tests: ```mvn clean install -DskipTests```

# Use the application

* add file with option ```-f``` or ```--file```
* enable sort by start date with option ```-s``` or ```--sortByStartDate```
* enable filter ```<project-name>``` with option ```-p <project-name>``` or ```--project <project-name>```


### Easy to run happy flow use cases (from root of project):

Simple process of smalldatafile.txt
```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f smalldatafile.txt```

Process smalldatafile.txt and filter on project 2
```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f smalldatafile.txt -p 2```

Process smalldatafile.txt and sort
```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f smalldatafile.txt -s```

Process smalldatafile.txt and sort and filter on project 6 (in this case same date)
```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f smalldatafile.txt -s -p 6```

These are all more fun to run with large files.


### Easy to run error use cases:

#### Empty / non existing file:

```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f emptyFile.txt -s -p 6```

```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f anyNameForNonExistingFile -s -p 6```

Both will output: ```USER ERROR -- File does not exist or is empty.```

#### Too large file (currently set @ 2GB, configurable in Config.Java) :
* Step 1: set totalRounds to a number > 19
* Step 2: run process with or without optional parameters see error output as below

```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f largedatafile.txt```
USER ERROR -- Input file exceeds allowance. Max input file size is 2 GB.)

#### Bad format file (missing header)

```java -jar target/commandline-app-1.0-SNAPSHOT-jar-with-dependencies.jar -f data-template.txt```

output:

```
USER ERROR -- First non commented line must be valid header line.
Non-compliant value: 2.
Line content: 2	Harmonize Lactobacillus acidophilus sourcing	2014-01-01 00:00:00.000	Dairy	Daisy Milks	NULL	NULL	Simple.
Error occured in line: 1.
```
More error use cases can be find in the unit tests, or implemented tested by you.

# How long will it take?