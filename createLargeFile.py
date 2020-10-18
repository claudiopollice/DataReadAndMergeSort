# written in python 2
# HOW TO USE :
# Step 1 : set totalRounds number as large as you wish to create a file as large as you wish
# Step 2 : run command python createLargeFile.py
import os

totalRounds = 18 ## WARNING : Don't make this larger than 19, it will take long too create


inputfileName = "data-template.txt"
outputfileName = "largedatafile.txt"
headerfileName = "dataheader.txt"

os.chmod(outputfileName, 0o777)

inputfile = open(inputfileName, 'r')
initialContent = inputfile.read()

print "Writing initial data to file ..."

outputfile = open(outputfileName, 'w')
outputfile.write(initialContent)

inputfile.close()
outputfile.close()


print "Doubling content %d times to create large file recursively :" % totalRounds

for number in range(0,totalRounds):
    file = open(outputfileName, 'a+')
    file.seek(0)
    content = file.read()
    file.write(content)
    file.close()
    print "."*(totalRounds-number),
    print "Round %d of %d    :   Lower estimate of Filesize = %d KB " % (number +1, totalRounds, 2 * len(content) / 1024),
    print " = %d MB = %d GB" % (2 * len(content) / 1024 / 1024, 2 * len(content) / 1024 / 1024 / 1024)

print "Prepending header..."
headerfile = open(headerfileName, 'r')
header = headerfile.read()
headerfile.close()

outputfile = open(outputfileName, 'r')
data = outputfile.read()
outputfile.close()
outputfile = open(outputfileName, 'w')
content = header + '\n' + data
outputfile.truncate(0)
outputfile.write(content)
outputfile.close()

print "File created."



filenames = ["0.65mb", ]


# Reference Table :
# Round 0      Filesize = 5 KB
# Round 1      Filesize = 10 KB
# Round 3      Filesize = 41 KB
# Round 5      Filesize = 162 KB
# Round 8      Filesize = 1,3 MB
# Round 9      Filesize = 2,6 MB
# Round 10     Filesize = 5 MB
# Round 11     Filesize = 10 MB
# Round 14     Filesize = 82 MB
# Round 15     Filesize = 166 MB
# Round 17     Filesize = 663 MB
# Round 18     Filesize = 1,33 GB
# Round 19     Filesize = 2,65 GB
# Round 20     Filesize = 5,3 GB