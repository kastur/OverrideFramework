What is it?
-----------
The Override framework proxies the location and sensor updates delivered
to Android apps. A central OverrideCommander service facilitates suppression
and perturbation functions to be applied to location and sensor data
before it is delivered to the end apps.

This system is meant to work together with an "Privacy Firewall" service
that can allow the user to specify context-dependent rules to drive the
perturbation and suppression of sensor data.

For example: see the companion repo at `https://github.com/kastur/PrivacyPOI`
which leverages the Override framework in order to suppress location updates
when the user enters "sensitive" points of interest. e.g. a user can ask to
suppress location updates whenever he/she enters a hospital.

Build Instructions
--------------------

In order to build the OverrideLibrary module, you need to compile against
a modified Android sdk that un-`@hide`s some internal Android API.

If you are a NESLite, you can use the following script to get a copy of the
modified Android SDK: `https://gist.github.com/3376882`


