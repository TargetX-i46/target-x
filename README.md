## Target-X SafeKey server

This software generates the keys for safekey cloud, 5G, proximity, and login scenarios.

Database: postgres

## Installation

* mvn clean install
* mvn clean compile assembly:single


## API:

*Create keys*

```bash
$server_protocol://$server_host:$server_port/safekey/device
postDataJson='{ "deviceName" : "'$device_name'", "deviceDescription" : "'$device_description'" }'
```


*Download keys*

```bash
$server_protocol://$server_host:$server_port/safekey/keys/download?uuid=$device_uuid
```

*Reset key cloud*

```bash
$server_protocol://$server_host:$server_port/safekey/reset?uuid=$device_uuid
```


