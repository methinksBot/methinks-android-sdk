# methinksSDK-Android

methinks SDK는 앱 사용자의 이동 경로를 추적 할 수 있으며 선택된 특수 유저 그룹에게 설문조사를 보여 줄 수 있습니다. 앱을 설치하기 위한 자세한 내용은 밑에 문서를 봐주시고 SDK를 사용하기 위해 필요한 Client Key 와 Product Key는 [methinks analytics](https://analytics.methinks.io)로 이동하여 계정을 생성하신 후 프로덕트를 생성 후 받으시면 됩니다.

## methinks SDK 적용을 위한 최소 요구사항

API level 21 이상

## SDK 적용하기

### app/build.gradle에 적용하세요. 아래 소스와 같이 maven주소와 SDK를 추가합니다.

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

### AndroidManifest.xml에 설정 추가하기

AndroidManifest.xml에 methinks analytics 웹사이트에서 앱을 생성한 후 발급받은 Client key와 Product Key를 등록해주세요.

```xml
	<application>
		...
		...
    	<!--해당하는 Client Key 발급 받고 등록하기-->
    	<meta-data android:name = "MtkSDK_Client_Key" android:value = "CLIENT KEY"/>

    	<!--해당하는 Product Key 발급 받고 등록하기-->
		<meta-data android:name = "MtkSDK_API_KEY" android:value = "PRODUCT KEY"/>
	</application>
```

AndroidManifest.xml에 `ViewControllerManager` 액티비티를 추가해주세요.

```xml
	<application>
	...
	...
    	<!-- ViewControllerManager 추가하기 -->
		<activity android:name = "io.mtksdk.viewcontroller.ViewControllerManager"></activity>
	</application>
```

### 소스 코드 추가하기

아래와 같이 처음으로 시작되는 MainActivity 클래스 내부에 SDK 함수를 적용합니다.
```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MtkSDK.start(this);
	}
```

## 유저 경로 데이타 수집

methinks SDK는 유저의 이동 경로를 화면 이동을 바탕으로 수집합니다. 더 정확하고 필요한 데이타를 수집하기 위해 몇가지 기능들이 있습니다.

### 화면 이름 커스텀하기

유저의 이동 경로를 수집할때 화면 이름은 자동으로 해당 화면의 class 이름으로 수집이 됩니다. 개발자 입장에서는 쉽게 이해 할 수 있지만 리서처 입장에서는 아닐수 있습니다. 만약에 화면 이름을 바꾸고 싶다면 아래 소스코드를 해당 화면 class 안에 추가해 주세요.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.setCustomViewName("put custom name here");
	}
```

### 이동 경로 데이타 수집하지 않기

해당 화면이 유저한테 중요하거나 아니면 별로 의미가 없는 화면이라서 제외하고 싶을때 아래 소스 코드를 추가해 주세요.
You can exclude a `UIViewController` from auto tracking if the viewController is meaningless in user journey.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.disableViewTracking(this.getClass().getSimpleName()); // this.getClass().getSimpleName() gives you the current simple class name.
	}
```


### 유저 아이디 넣기
더 나은 데이터 수집과 분석을 위해 각 유저마다의 아이디가 있어서 데이타에 넣어 주고 싶을때 아래 소스 코드를 추가해 주세요.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.setUser("user id");
	}
```

### 유저 속성 만들기
예를 들어 유저들의 장소, 언어, 현재 쓰고있는 스마트폰 모델 등 추가적으로 원하는 특별한 속성을 추가하고 싶을때 아래 소스 코드를 추가해 주세요. 이 기능으로 나중에 설문조사를 하고 싶을때 사용 할 수 있습니다.

```java

	import io.mtksdk.methinksdk.MtkSDK;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		MtkSDK.setLogUserAttributes("age", 43);
		MtkSDK.setLogUserAttributes("locale", "en_US");
		MtkSDK.setLogUserAttributes("usingSDK", true);
	}
```
