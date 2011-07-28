#!/bin/sh
KEYSTORE=.keystore
keytool -genkey -alias applet-signing-key -keystore $KEYSTORE -storepass changeit -keypass changeit -dname "CN=OnOAAMDeviceExtensionApplet, OU=OnOAAM, O=OnOAAM.com, L=Minneapolis, ST=Minnesota, C=US"
keytool -selfcert -alias applet-signing-key -keystore $KEYSTORE -storepass changeit -keypass changeit