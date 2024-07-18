Proximity Test

Setup:
1 IoT Gateway
2 Devices

Files:

Own keys - next.txt, secret-keys.csv
Other keys - device{id}_next.txt, device{id}_secret-keys.csv


Script:
auth.sh <device 1> <device n>

To manually test the script, check the line
#uncomment to test
remove the comment for testing, and comment the other line that is for prod.
In prod environment, make sure to comment the test lines again.

Before running the scripts on each machine, run this command manually based on next.txt key
#nmcli connection add type wifi con-name 75bab779c3fac998732ccbfa8f9160ee autoconnect no ssid 75bab779c3fac998732ccbfa8f9160ee

After running once, place this script in crontab
# * * * * * /opt/utils/auth.sh 3005 3006

Test

Run from IoT gateway:
./auth.sh 3005 3006

Run from device (3005, etc.):
./auth.sh iot-gateway

