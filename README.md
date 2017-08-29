
# iSantePlus m2Sys Biometrics Module

To set up this module, so it works properly there are three things needed:
- BioPlugin server 8
- Cloudscanner 
- M2Sys fingerprint reader

This module is using REST to communicate with CloudScanner API and send requests.

Worklflow:

1. M2Sys module for iSantePlus sends requests to CloudScanner Standalone Api using REST
2. CloudScanner Standalone Api uses RabbitMQ to communicate with CloudScanner Client
3. CloudScanner Client gets fingerprints from fingerprint reader
4. CloudScanner Client sends this data back to CloudScanner Standalone Api
5. CloudScanner Standalone Api sends SOAP request to BioPlugin server
6. CloudScanner Standalone Api gets response from BioPlugin server and sends it back to M2Sys module

[![Build Status](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics.svg?branch=master)](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics)
