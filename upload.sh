#!/bin/sh

#simple script to upload our extension to server 

echo "Uploading AirHockeyExtension.jar to server"

#Old address
#scp -i ~/.ssh/airhockey /Users/mahendra.chouhan/projects/SmartFoxServer_2X/SFS2X/extensions/AirHockey/AirHockeyExtension.jar root@142.93.215.3:/home/SmartFoxServer_2X/SFS2X/extensions/AirHockey

scp -i ~/.ssh/mchouhanofficial.pem /Users/mahendra.chouhan/projects/SmartFoxServer_2X/SFS2X/extensions/AirHockey/AirHockeyExtension.jar ec2-user@3.135.237.116:/home/ec2-user/SmartFoxServer_2X/SFS2X/extensions/AirHockey

echo $?

