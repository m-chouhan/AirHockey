#!/bin/sh

#simple script to upload our extension to server 

echo "Uploading AirHockeyExtension.jar to server"

scp -i ~/.ssh/airhockey /Users/mahendra.chouhan/projects/SmartFoxServer_2X/SFS2X/extensions/AirHockey/AirHockeyExtension.jar root@142.93.215.3:/home/SmartFoxServer_2X/SFS2X/extensions/AirHockey

echo $?

