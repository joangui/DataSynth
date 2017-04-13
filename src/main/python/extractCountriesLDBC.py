
import sys

# 1st arg city to country file
# 2nd arg person to city file
# 3rd arg country to countryname file
# 4rd arg output file

cityToCountryFile = open(sys.argv[1],'r')
cityToCountry = {}
index = 0
for line in cityToCountryFile.readlines():
    if index > 0:
        fields = line.split("|")
        cityToCountry[int(fields[0])] = int(fields[1])
    index+=1
cityToCountryFile.close()

personToCityFile = open(sys.argv[2],'r')
personToCity = {}

index = 0
for line in personToCityFile.readlines():
    if index > 0:
        fields = line.split("|")
        personToCity[int(fields[0])] = int(fields[1])
    index+=1
personToCityFile.close()

countryToNameFile = open(sys.argv[3],'r')
countryToName = {}
index = 0
for line in countryToNameFile.readlines():
    if index > 0:
        fields = line.split("|")
        countryToName[int(fields[0])] = str(fields[1])
    index+=1
countryToNameFile.close()

output = open(sys.argv[4],'w')
for person in personToCity:
    output.write(str(person)+" "+countryToName[cityToCountry[personToCity[person]]]+"\n")
output.close()

