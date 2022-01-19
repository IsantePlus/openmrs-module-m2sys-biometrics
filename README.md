
# iSantePlus m2Sys Biometrics Module
[![CI](https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/actions/workflows/ci.yml/badge.svg)](https://github.com/IsantePlus/openmrs-module-m2sys-biometrics/actions/workflows/ci.yml)

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
6. This module sends a REST request to the national CloudABIS server
7. This module gets the response from both the local BioPlugin server and the national CloudABIS server interprets it.
8. If the fingerprint(s) are missing in either of the servers, this module registers them
9. If a patient already exists with a biometric identifier matching the processed Id, this module prompts the use to laod that patient

## Configuration variables

### Template if no scanner exists

If no fingerprint scanner exists, simulations may be performed using a pre-loaded fingerprint template to be stored as a global property:
m2sys-biometrics.server.constTestTemplate - String representation of the fingerprint template

### Local variables
m2sys-biometrics.accessPointID - The m2sys server AccessPointID<br/>
m2sys-biometrics.accessPointMap - A map of IP addresses to Access Point IDs. Has format of IP1:AccessPointID1;IP2:AccessPointID2;...
For example 127.0.0.1:APID1;8.8.8.8:APID2... The IPs must match the IPs of the clients calling the server.<br/>
m2sys-biometrics.captureTimeout - The m2sys server CaptureTimeout<br/>
m2sys-biometrics.customKey - The m2sys server CustomKey<br/>
m2sys-biometrics.locationID - The m2sys server location ID<br/>
m2sys-biometrics.server.password - The m2sys server password<br/>
m2sys-biometrics.server.url - The m2sys server url<br/>
m2sys-biometrics.device.name - The m2sys fingerprint scanning device name<br/>
m2sys-biometrics.server.user - The m2sys server username<br/>
m2sys-biometrics.local-service.url - The URL to the SOAP service of the local (local to the clinic) M2Sys BioPlugin Server.

### National variables
m2sys-biometrics.cloudabis.app.key - The cloud client app key<br/>
m2sys-biometrics.cloudabis.secret.key - The cloud client secret key<br/>
m2sys-biometrics.cloudabis.grant.type - The cloud client grant type<br/>
m2sys-biometrics.cloudabis.api.url - The The URL to the REST service national fingerprint server <br/>
m2sys-biometrics.cloudabis.customer.key - The cloud client customer key<br/>
m2sys-biometrics.cloudabis.engine.name - The cloud client engine name<br/>
m2sys-biometrics.cloudabis.template.format - The cloud client template format used<br/>
