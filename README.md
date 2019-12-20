# AttendiFi

## It is an application that helps a faculty take attendance in the classroom. The only constraint is that faculty and students have to be on the same network. We are open to issues.

### Initial set up ( QR Code Scanning and Generation )
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
	<img src = "./app/src/main/res/drawable/image_scanning.gif" height="500px">

### IP Address and encoding

* Get the IP Address of the device:
	```java
		public String getLocalIpAddress(){
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
					en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
							return inetAddress.getHostAddress();
						}
					}
				}
			} catch (Exception ex) {
				Log.i("IP Address", ex.toString());
			}
			return null;
		};
	```
* Encode the IP Address around random text and mark it with hash for easy decomposition
	```java
		...
		String all = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMONPQRSTUVWXYZ0123456789`~!@$%^&*(){}[]:';\",./<>?";
		public String random(int len){
			String res = "";
			for(int i = 0;i < len;i++){
				res += all.charAt( (int) (Math.random()*all.length()) );
			}
			return res;
		};
		...
		onCreate(){
			...
			ip = "#";

			StringTokenizer ipformatted = new StringTokenizer(getLocalIpAddress(),".");

			ip+=ipformatted.nextToken()+"A"+ipformatted.nextToken()+"B"+ipformatted.nextToken()+"C"+ipformatted.nextToken()+"D";

			ip+="#";

			String text = random(64) + ip + random(64 - ip.length());
			...
		}
		...
	```

	But do not forget to modify the AndroidManifest.xml for permissions:

	```xml
		<uses-permission android:name="android.permission.INTERNET" />
    	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	```

### Complete App Demo

* Faculty Side
	<p><img src = "./app/src/main/res/drawable/faculty.gif" height="500px"></p>

* Student Side
	<p><img src = "./app/src/main/res/drawable/student.gif" height="500px"></p>
