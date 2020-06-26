#!/bin/sh

#simple script to upload our extension to server 
if [ -z "$1" ]
then
      echo "filename is empty"
      exit 1
fi
if [ -z "$2" ]
then
      echo "server ip is empty"
      exit 1
fi

echo "Uploading $1 to server $2"

scp -i ~/.ssh/mumbaiV2.pem $1 ec2-user@$2:/home/ec2-user/SmartFoxServer_2X/SFS2X/extensions/AirHockey

returnCode=$?
echo "scp return code $returnCode"
exit $returnCode
