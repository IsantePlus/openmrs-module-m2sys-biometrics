
# iSantePlus m2Sys Biometrics Module [![Build Status](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics.svg?branch=master)](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics)

This an implementation of the Biometrics interfaces from the registration core module that use the m2sys Biometrics server. The module uses the CloudScanner API by m2sys in order to communicate with the server and invoke the fingerprint reader.

## Requirements

To set up this module, so it works properly there are three things needed:
- BioPlugin server 8
- Cloudscanner 
- M2Sys fingerprint reader

This module is using REST to communicate with CloudScanner API and send requests.

## Building the module

In order to build the module you must pass the location of the BioPlugin Web Service WSDL
using the property **biopluginWsdlUrl**. You can pass it to Maven like this:

```bash
mvn clean install -DbiopluginWsdlUrl=https://<HOST>/M2Sys.BioPluginWeb/BioPluginServiceV8.asmx?wsdl
``` 

Make sure to replace `<HOST>` with either the IP or the domain of the server. Also make
sure to use the **V8** version of the service, like in te example above.

Note: it is not important which instance of the M2Sys server is using during building as long
as it is the same version that wil be used.

## Worklflow

1. M2Sys module for iSantePlus sends requests to CloudScanner Standalone Api using REST
2. CloudScanner Standalone Api uses RabbitMQ to communicate with CloudScanner Client
3. CloudScanner Client gets fingerprints from fingerprint reader
4. CloudScanner Client sends this data back to CloudScanner Standalone Api
5. CloudScanner Standalone Api sends SOAP request to BioPlugin server
6. CloudScanner Standalone Api gets response from BioPlugin server and sends it back to M2Sys module

## Configuration variables

m2sys-biometrics.accessPointID - The m2sys server AccessPointID<br/>
m2sys-biometrics.captureTimeout - The m2sys server CaptureTimeout<br/>
m2sys-biometrics.customKey - The m2sys server CustomKey<br/>
m2sys-biometrics.locationID - The m2sys server location ID<br/>
m2sys-biometrics.server.password - The m2sys server password<br/>
m2sys-biometrics.server.url - The m2sys server url<br/>
m2sys-biometrics.server.user - The m2sys server username<br/>
