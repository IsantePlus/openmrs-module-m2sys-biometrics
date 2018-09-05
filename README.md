
# iSantePlus m2Sys Biometrics Module [![Build Status](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics.svg?branch=master)](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics)

This an implementation of the Biometrics interfaces from the registration core module that use the m2sys Biometrics server. The module uses the CloudScanner API by m2sys in order to communicate with the server and invoke the fingerprint reader.

## Requirements

To set up this module, so it works properly there are three things needed:
- BioPlugin server 8
- Cloudscanner 
- M2Sys fingerprint reader

This module is using REST to communicate with CloudScanner API and send requests.

## Building the module

In order to build the module you must use mvn command. Example below:

```bash
mvn clean install
```

Note: in the api/src/main/resource/wsdl directory you can find the BioPlugin Web Service WSDL file.
Currently the module uses the **V8** version of the service.

## Worklflow

1. M2Sys module for iSantePlus sends requests to CloudScanner Standalone Api using REST
2. CloudScanner Standalone Api uses RabbitMQ to communicate with CloudScanner Client
3. CloudScanner Client gets fingerprints from fingerprint reader
4. CloudScanner Client sends the fingerprint data back to this module
5. This module sends a SOAP request to the local BioPlugin server
6. This module gets the response from the BioPlugin server and interprets it.

## Configuration variables

m2sys-biometrics.accessPointID - The m2sys server AccessPointID<br/>
m2sys-biometrics.accessPointMap - A map of IP addresses to Access Point IDs. Has format of IP1:AccessPointID1;IP2:AccessPointID2;...
For example 127.0.0.1:APID1;8.8.8.8:APID2... The IPs must match the IPs of the clients calling the server.<br/>
m2sys-biometrics.captureTimeout - The m2sys server CaptureTimeout<br/>
m2sys-biometrics.customKey - The m2sys server CustomKey<br/>
m2sys-biometrics.locationID - The m2sys server location ID<br/>
m2sys-biometrics.server.password - The m2sys server password<br/>
m2sys-biometrics.server.url - The m2sys server url<br/>
m2sys-biometrics.server.user - The m2sys server username<br/>
m2sys-biometrics.local-service.url - The URL to the SOAP service of the local (local to the clinic) M2Sys BioPlugin Server.
