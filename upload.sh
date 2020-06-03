#!/bin/sh

#simple script to upload our extension to server 

echo "Uploading AirHockeyExtension.jar to server $1"

scp -i ~/.ssh/mumbai.pem /Users/mahendra.chouhan/projects/SmartFoxServer_2X/SFS2X/extensions/AirHockey/AirHockeyExtension.jar ec2-user@$1:/home/ec2-user/SmartFoxServer_2X/SFS2X/extensions/AirHockey

returnCode=$?
echo $returnCode
exit $returnCode

