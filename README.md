# iSantePlus m2Sys Biometrics Module

[![Build Status](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics.svg?branch=master)](https://travis-ci.org/IsantePlus/openmrs-module-m2sys-biometrics)

Is the HTTP connection between the JS and the OpenMRS server is kept alive?
    Conection between JS and OpenMRS server is being kept alive, Tomcat is not disconnectiong on it's own.

Do we need anything changed in Tomcat? 
    If we want it to close the connection when some time has passed, we can set the reply timeout by setting    JK_REPLY_TIMEOUT enviroment variable. 
What is the default?
    When it's not set, or it's set to negative value the default reply timeout of the worker will be used. If JK_REPLY_TIMEOUT contains the value "0", then the reply timeout will be disabled for the request.

WARNING! If JK aborts waiting for a response, because a reply timeout fired, there is no way to stop processing on the backend. Although you free processing resources in your web server, the request will continue to run on the backend - without any way to send back a result once the reply timeout fired. 

Is there is an nginx or other proxy used?
    I have not seen any nginx or other proxy instance used in tomcat yet, although there is a possibility of using 

https://tomcat.apache.org/connectors-doc/common_howto/timeouts.html
