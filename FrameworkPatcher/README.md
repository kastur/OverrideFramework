Patching Rooted Android Phone with OverrideFramwork
---------------------------------------------------

The scripts in this directory aid in patching an Android device or emulator
with the Override framework.

You can only patch the framework if the phone is rooted! This is tested on
the JRO03C version of Jellybean on Galaxy Nexus.

1.  `git clone https://github.com/nesl/OverrideFramework`

2.  `cd OverrideFramework/FrameworkPatcher`

3.  `source scripts/envsetup.sh` to setup the `$PATH`.
    Also make sure the `platform-tools` directory of the Android SDK is on
    the `$PATH`, you should be able to do a `adb` command.

##### Patching a `system.img` to patch ROM, can be used with emulator

1.  copy `system.img` into the `FrameworkPatcher` directory. For the
    emulator, you can find this file in 
    `~/android-sdk/system-images/andrroid*/armeabi*/system.img`

2.  `run_from_system_img` This will unpack system.img and then patch
    the framework.jar inside it. **If you get HUNK failed** errors
    when patching see the patching `framework.jar` directly section's
    "step 2"

3.  After successfully patching, there will be a new img file in
    `inout/system_new.img`. Copy this back into the ROM or emulator
    directory.


##### Patching `framework.jar` directly on a device

1.  `pull_framework` pulls your the framework.jar from your Android device. Make sure your phone is connected and ROOTED. `ls` should a `framework.jar` file. **MAKE SURE YOU TAKE A BACKUP of the original framework.jar in safe location** This can come in handy if the phone does not boot with the patched framework.jar!

2.  `run_from_framework_jar` This will decompile the framework.jar
    and OverrideService.apk and do the necessary surgery to patch a new
    framework.jar that contains the OverrideLocationManager as a system
    service. **In case you get HUNK failed output from `run_from_scratch`,
    you need to manually patch the
    `inout/framework_decoded/smali/android/app/ContextImpl.smali` file.
    Refer to the `./android_app_ContextImpl.patch` file**

3.  `push_framework` Pushes the new framework back onto the Android device.
   Also clears the dalvik cache.

4.  `adb reboot` Reboot the device for the new framework to take effect.
    You should see a "Android Upgrading" screen on next bootup.
