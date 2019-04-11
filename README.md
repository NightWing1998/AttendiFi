# AttendiFi

------------------------------------------
### Initial set up
* Change the build.gradle(Module : app) file with the following code:
	```java
			...
			android {
				...
				compileSdkVersion 26
				defaultConfig {
					...
					minSdkVersion 15
        			targetSdkVersion 26
        			multiDexEnabled true
					...
    			}
				...
			}
			...
			dependencies {
				...
    			implementation fileTree(dir: 'libs', include: ['*.jar'])
    			implementation 'com.android.support:appcompat-v7:26.1.0'
    			implementation 'com.google.android.gms:play-services:12.0.1'
    			implementation 'com.android.support:multidex:1.0.3'
				...
			}
			...
	```
* Install Google Play services
	<img src = "./app/src/main/res/drawable/play_install.gif">

* Scanning done!!
	<p>Please refer to the the commit with the same message to view the changes</p>

* Modify build.gradle again to use Zxing library
	```java
		dependencies{
			...
			implementation 'com.google.zxing:core:3.2.1'
			implementation 'com.journeyapps:zxing-android-embedded:3.2.0@aar'
			...
		}
	```
* QR Code Generation done!!
	<p>Please refer to the commit with the same message to view the changes</p>
	
* Initial scan and process:
	<img src = "./app/src/main/res/drawable/image_scanning.gif">