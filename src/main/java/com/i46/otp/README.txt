Proximity Test

Setup:
1 IoT Gateway
2 Devices

Files:
IoT Gateway - next.txt, secret-keys.csv
Device - device{id}_next.txt, device{id}_secret-keys.csv

Scripts:
1 script for IoT gateway - auth-gateway.sh
1 script for each device - auth.sh

To manually test the script, check the line
#uncomment to test
remove the comment for testing, and comment the other line that is for prod.
In prod environment, make sure to comment the test lines again.

Before running the scripts on each machines, run this command manually based on next.txt key
#nmcli connection add type wifi con-name 75bab779c3fac998732ccbfa8f9160ee autoconnect no ssid 75bab779c3fac998732ccbfa8f9160ee

After running once, place this script in crontab
# * * * * * /opt/utils/auth.sh