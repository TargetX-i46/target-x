Proximity Test

Setup:
1 IoT Gateway
1~n Devices

Files:

Own keys - next.txt, secret-keys.csv
Other keys - device{id}_next.txt, device{id}_secret-keys.csv


Script:
auth.sh <device 1> <device n>

To manually test the script, check the line
#uncomment to test
remove the comment for testing, and comment the other line that is for prod.
In prod environment, make sure to comment the test lines again.

Before running the scripts on each machine, run this command manually on the gateway. This is based on next.txt of the devices the gateway wants to detect
nmcli connection add type wifi con-name ebeccfefae5fb095a524d6eae466170b autoconnect no ssid ebeccfefae5fb095a524d6eae466170b
nmcli connection add type wifi con-name 55bf04a61467ba92692bd4a6b11ee6e0 autoconnect no ssid 55bf04a61467ba92692bd4a6b11ee6e0

After running the manual commands, place this script in crontab

Run from IoT gateway:
sudo su
crontab -e
* * * * * /opt/utils/gateway/auth.sh 3005 3006 >> /opt/utils/gateway.log

Run from device (3005, etc.):
sudo su
crontab -e
* * * * * /opt/utils/device3005/auth.sh gateway >> /opt/utils/device.log

