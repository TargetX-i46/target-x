#!/bin/bash

#before running script, run manually based on next.txt key
#nmcli connection add type wifi con-name 75bab779c3fac998732ccbfa8f9160ee autoconnect no ssid 75bab779c3fac998732ccbfa8f9160ee
#also run the same command (replace respective keys) in the other devices
# * * * * * /opt/utils/auth.sh

KEY_FILE=secret-keys.csv
NEXT_FILE=next.txt

if [ -z "$1" ]; then
 echo "Enter the device ids you want to detect. Ex. 3005 3006 3007"
 exit 1
else
 OTHER_KEY_FILE1=device$1_secret-keys.csv
 OTHER_NEXT_FILE1=device$1_next.txt
fi


if [ -s $KEY_FILE ]  && [ -s $NEXT_FILE ]; then
 KEY=$(cat $NEXT_FILE)
 echo "Current key" $KEY

 #my key
 LINE=$(awk '/'$KEY'/{ print NR; exit }' $KEY_FILE)
 NEXT=$((LINE + 1))
 MY_NEXT_KEY=$(awk 'NR == '$NEXT $KEY_FILE)
 echo "Next key" $MY_NEXT_KEY
 echo $MY_NEXT_KEY | tee $NEXT_FILE >> /dev/null
 nmcli connection delete id $KEY
 nmcli connection add type wifi con-name $MY_NEXT_KEY autoconnect no ssid $MY_NEXT_KEY
 nmcli connection show | grep $MY_NEXT_KEY

  #finding the other devices

  for i in "$@"
  do
     OTHER_KEY_FILE=device"$i"_secret-keys.csv
     OTHER_NEXT_FILE=device"$i"_next.txt

     if [ -s $OTHER_KEY_FILE ]  && [ -s $OTHER_NEXT_FILE ]; then
         OTHER_KEY=$(cat $OTHER_NEXT_FILE)
         echo "Device "$i" Current key" $OTHER_KEY
         #uncomment to test
         #RESPONSE=$(nmcli connection show | grep $OTHER_KEY)
         #comment on test
         RESPONSE=$(nmcli -f SSID,BSSID,DEVICE dev wifi | grep $OTHER_KEY)
         echo $RESPONSE
         if [ -z "$RESPONSE" ]; then
            echo "Device "$i" not found"
            exit 1
         else
            RESPONSE_KEY=$(echo $RESPONSE | awk '{print $i;}')
            echo $RESPONSE_KEY
            echo "Device "$i" is within range"

              OTHER_LINE=$(awk '/'$OTHER_KEY'/{ print NR; exit }' $OTHER_KEY_FILE)
              OTHER_NEXT=$((OTHER_LINE + 1))
              OTHER_NEXT_KEY=$(awk 'NR == '$OTHER_NEXT $OTHER_KEY_FILE)
              echo "Device "$i" next key" $OTHER_NEXT_KEY
              #uncomment to test
              #nmcli connection add type wifi con-name $OTHER_NEXT_KEY autoconnect no ssid $OTHER_NEXT_KEY
             echo $OTHER_NEXT_KEY | tee $OTHER_NEXT_FILE >> /dev/null
         fi
       else
         echo "Device "$i" key file not found"
       fi
  done
else
  echo "Key file not found"
fi
