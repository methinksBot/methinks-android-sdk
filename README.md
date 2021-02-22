# methinksSDK-Android

The methinks Android SDK allows you to track user journey and to survey specific groups of your mobile app users. Understanding specific user behaviors and journey will be done with single line of code and initialize in-app survey for specific target group of users. Check more detail in [methinks](https://analytics.methinks.io) to create your account and acquire product key to begin with.  If you're interested in IOS version go [here](https://www.methinks.io).

## Requirements

SDK works on AOS at least version 21

## Installation

Step 1:

Please add the following in build.gradle and sync
```
Allprojects{
	Repositories {
		...
		Maven(url 'https://jitpack.io'}
	}
}
```

```
Dependencies {
	implementation 'org.bitbucket.gunyeup:mtk-android-sdk:1.0.1'
}
```

Step 2:

To initialize the sdk:

Please add the following in the AndroidManifest.xml
```xml
	<application>
	...
	...
		<activity android:name = "io.mtksdk.viewcontroller.ViewControllerManager"></activity>
	</application>
```

Get client key and product key from [here](https://www.methinks.io) and implement in the AndroidManifest.xml
```xml
	<application>
		...
		...
		<meta-data android:name = "MtkSDK_API_KEY" android:value = "PRODUCT KEY"/>
		<meta-data android:name = "MtkSDK_Client_Key" android:value = "CLIENT KEY"/>
	</application>
```

After adding above, to initialize the sdk please add the following at your main activity (The first activity class when app starts).
```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.start(this);
		.
		.
		.
	}
```

## User Journey Tracking
methinks SDK tracks user journey based on `UIViewController` class and user's interaction with it. There are a few options you can adjust to get the most comprehensive journey data.


### Custom view controller title
If you enable `autoTracking`, SDK will capture user journey based on `UIViewController` name. If you want to setup more easy-to-understand name, you can set custom tracking view name by using the function `setCustomViewName("Put Custom Name);`.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.setCustomViewName("put custom name here");
	}
```

### Disable view tracking
You can exclude a `UIViewController` from auto tracking if the viewController is meaningless in user journey.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.disableViewTracking(this.getClass().getSimpleName()); // this.getClass().getSimpleName() gives you the current simple class name.
	}
```


### Set User
You can set user id.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.setUser("user id");
	}
```

### Set User's Attributes
You can set user's any attributes such as locale, device model, language, and etc. There are three types that you can set user's attributes : String, Integer, and Boolean.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.setLogUserAttributes("age", 43);
		MtkSDK.setLogUserAttributes("locale", "en_US");
		MtkSDK.setLogUserAttributes("usingSDK", true);
	}
```
