package io.mtksdk.methinksdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.mtksdk.activitylogmanager.ActivityLogConstant;
import io.mtksdk.activitylogmanager.SdkLifecycle;
import io.mtksdk.fingerprintmanager.FingerPrintManager;
import io.mtksdk.networkmanager.BuildConfig;
import io.mtksdk.networkmanager.NetworkConstant;
import io.mtksdk.networkmanager.NetworkConnect;
import io.mtksdk.inappsurvey.SurveyAlertManager;
import io.mtksdk.inappsurvey.ViewConstant;

public class MtkSDK {

    protected static String uuid;
    protected static String apiKey;
    protected static String cKey;
    protected static String locale;
    protected static String timeZoneStr;
    protected static NetworkConnect nm;
    protected static Application currApp;
    protected static Context thirdUser;
    protected static ArrayList<JSONArray> prevSData;
    protected static String prevSid;
    protected static Boolean prevSIsNew;
    protected static ArrayList<String> prevEvent;
    protected static String bundleId;
    protected static String deviceModel;
    protected static String carrier;
    protected static Boolean isInitiated = false;
    //commit testing



    public static void start(Activity activity) {
        init(activity);
    }

    public static void start(Activity activity, boolean debug) {
        if (debug) {
            NetworkConstant.SERVER_URL = "https://sdk-dev.methinks.io/parse/";
            NetworkConstant.AppId = BuildConfig.AppId_DEBUG;
            NetworkConstant.RestAPIKey = BuildConfig.RestAPI_DEBUG;
        }
        init(activity);
    }

    protected static void init(final Activity activity) {
        try {
            ActivityLogConstant.isInit = true;
            nm = new NetworkConnect();
            currApp = activity.getApplication();
            thirdUser = activity;
            locale = activity.getResources().getConfiguration().locale.toString();
            timeZoneStr = NetworkConstant.getTimeZoneAbbr();
            prevSData = ActivityLogConstant.getArrayList("saveList", thirdUser);
            prevSid = ActivityLogConstant.getSid("saveSid", thirdUser);
            prevSIsNew = ActivityLogConstant.getSIsNew("saveSIsNew", thirdUser);
            prevEvent = ActivityLogConstant.getEventArr("saveEventArr", thirdUser);
            NetworkConstant.sId = NetworkConstant.getAlphaNumericString(12);
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            apiKey = bundle.getString("MtkSDK_API_KEY");
            cKey = bundle.getString("MtkSDK_Client_Key");
            bundleId = activity.getPackageName();
            deviceModel = FingerPrintManager.getDeviceName();
            carrier = getCarrier();
            ActivityLogConstant.actAuthKey = apiKey;
            ActivityLogConstant.cKey = cKey;

            uuid = ActivityLogConstant.getUUID("UUID", thirdUser);
            if (uuid == null || uuid == "unknown") {
                uuid = FingerPrintManager.getUuid();
                ActivityLogConstant.saveUUID("UUID", uuid, thirdUser);
            }

            if (ActivityLogConstant.getAuthKey("aKey", thirdUser) == null || !ActivityLogConstant.getAuthKey("aKey", thirdUser).equals(apiKey)) {
                ActivityLogConstant.saveAuthKey("aKey", apiKey, thirdUser);
            }
            if (ActivityLogConstant.getViewDicVer("viewDicVer", thirdUser) != null && ActivityLogConstant.getViewDicVer("viewDicVer", thirdUser) != -1) {
                NetworkConstant.viewDicVer = ActivityLogConstant.getViewDicVer("viewDicVer", thirdUser);
            }
            if (ActivityLogConstant.getScreenVer("screenVer", thirdUser) != null && ActivityLogConstant.getScreenVer("screenVer", thirdUser) != -1) {
                NetworkConstant.screenVer = ActivityLogConstant.getScreenVer("screenVer", thirdUser);
            }
            nm.setSDKUser(cKey, apiKey, bundleId, uuid, "android", locale, timeZoneStr, prevSData, prevSid, prevSIsNew, prevEvent, deviceModel, carrier, new NetworkConnect.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    isInitiated = true;
                    NetworkConstant.devId = response.optJSONObject("result").optString("devId");
                    ActivityLogConstant.saveDevId("devId", NetworkConstant.devId, thirdUser);
                    NetworkConstant.sessionCount = response.optJSONObject("result").optInt("sessionCount");
                    if (NetworkConstant.viewDicVer == null || NetworkConstant.viewDicVer == 0 || NetworkConstant.viewDicVer != response.optJSONObject("result").optInt("viewVer")) {
                        if (response.optJSONObject("result").has("viewVer") && response.optJSONObject("result").has("viewDic")) {
                            NetworkConstant.viewDicVer = response.optJSONObject("result").optInt("viewVer");
                            NetworkConstant.viewDic = response.optJSONObject("result").optJSONObject("viewDic");
                            ActivityLogConstant.saveViewDicVer("viewDicVer", NetworkConstant.viewDicVer, thirdUser);
                            ActivityLogConstant.saveViewDic("viewDic", NetworkConstant.viewDic, thirdUser);
                        }
                    }
                    if (NetworkConstant.screenVer == null || NetworkConstant.screenVer == 0 || NetworkConstant.screenVer != response.optJSONObject("result").optInt("screenVer")) {
                        if (response.optJSONObject("result").has("screenVer") && response.optJSONObject("result").has("screenArr")) {
                            NetworkConstant.screenSet = ActivityLogConstant.convertToSet(response.optJSONObject("result").optJSONArray("screenArr").toString());
                            NetworkConstant.screenVer = response.optJSONObject("result").optInt("screenVer");
                            ActivityLogConstant.saveScreenVer("screenVer", NetworkConstant.screenVer, thirdUser);
                            ActivityLogConstant.saveScreenSet("screenSet", NetworkConstant.screenSet, thirdUser);
                        }
                    }
                    HashMap<String, ArrayList<Long>> savedMap = ActivityLogConstant.getEventMap("eventMap", thirdUser); //-> use this later
                    getMySurvey(cKey, ActivityLogConstant.actAuthKey, NetworkConstant.devId, savedMap);
                    SdkLifecycle.init(currApp);
                }
                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    isInitiated = false;
                    Log.e("setSDKUser", "failed" + e.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setUser(String userId) {
        if (isInitiated) {
            NetworkConstant.userId = userId;
            NetworkConstant.isSetUserCalled = true;
        }
    }

    public static void setCustomViewName(String viewName) {
        if (isInitiated) {
            if (NetworkConstant.customViewMap == null || !NetworkConstant.customViewMap.containsKey(viewName)) {
                NetworkConstant.isCustomViewCalled = true;
                NetworkConstant.newCustomViewName = viewName;
            }
        }
    }

    public static void disableViewTracking(String viewName) {
        if (isInitiated) {
            NetworkConstant.disableViewSet.add(viewName);
        }
    }

    public static void setLogUserAttributes(String key, String value) {
        if (isInitiated) {
            if (key == null || value == null) {
                return;
            }
            NetworkConstant.isLogUserAttCalled = true;
            try {
                NetworkConstant.userAttri.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setLogUserAttributes(String key, Integer value) {
        if (isInitiated) {
            if (key == null || value == null) {
                return;
            }
            NetworkConstant.isLogUserAttCalled = true;
            try {
                NetworkConstant.userAttri.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setLogUserAttributes(String key, Boolean value) {
        if (isInitiated) {
            if (key == null || value == null) {
                return;
            }
            NetworkConstant.isLogUserAttCalled = true;
            try {
                NetworkConstant.userAttri.put(key, value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setLogEvent(String eventName, String type) {
        if (isInitiated) {
            if (eventName == null || type == null || currApp == null) {
                return;
            }
            ActivityLogConstant.eventArr.add(eventName);
            ArrayList<Long> tempArr = new ArrayList<>();
            Long lastUsed = SdkLifecycle.getDateAndTime();

            HashMap<String, ArrayList<Long>> savedMap = ActivityLogConstant.getEventMap("eventMap", currApp.getApplicationContext());
            if (savedMap != null) {
                Long count = savedMap.containsKey(eventName) ? savedMap.get(eventName).get(0) : 0L;


                tempArr.add(count + 1L);
                tempArr.add(lastUsed);

                savedMap.put(eventName, tempArr);

            } else {
                savedMap = new HashMap<>();
                tempArr.add(1L);
                tempArr.add(lastUsed);
                savedMap.put(eventName, tempArr);
            }
            ActivityLogConstant.saveEventMap("eventMap", savedMap, currApp.getApplicationContext());

            NetworkConstant.eventName = eventName;
            NetworkConstant.eventType = type;
            NetworkConstant.eventTS = (double) SdkLifecycle.getDateAndTime();
            if (ActivityLogConstant.actAuthKey != null && NetworkConstant.devId != null && NetworkConstant.eventType != null && NetworkConstant.eventName != null && NetworkConstant.eventTS != null) {
                try {
                    nm.setLogEvent(ActivityLogConstant.actAuthKey, NetworkConstant.devId, NetworkConstant.eventTS, NetworkConstant.eventName, NetworkConstant.eventType, new NetworkConnect.CallbackInterface() {
                        @Override
                        public void onDownloadSuccess(boolean success, JSONObject response) {
                            Log.i("logEvent", response.toString());
                        }

                        @Override
                        public void onDownloadFail(boolean fail, Throwable e) {
                            Log.i("logEvent", "failed" + e.toString());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected static void getMySurvey(final String cKey, final String aKey, final String devId, HashMap<String, ArrayList<Long>> savedMap) {
        try {
            nm.getMySurverys(cKey, aKey, devId, savedMap, new NetworkConnect.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("getMySurvey", response.toString());
                    if (response.optJSONObject("result").has("availableSurveys")) {
                        JSONArray jsonArray = response.optJSONObject("result").optJSONArray("availableSurveys");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Log.i("getJsonOb", jsonArray.optJSONObject(i).toString());
                            if (jsonArray.optJSONObject(i).has("packType")) {
                                String packType = jsonArray.optJSONObject(i).optString("packType");
                                if (packType.equals("inapp")) {
                                    boolean isRequired;
                                    if (jsonArray.optJSONObject(i).has("isRequired")) {
                                        isRequired = jsonArray.optJSONObject(i).optBoolean("isRequired");
                                    } else {
                                        isRequired = true;
                                    }
                                    String packId = jsonArray.optJSONObject(i).optString("objectId");
                                    getEachQuestion(aKey, devId, packId, isRequired);
                                    Log.i("surveyManager", "we can start");
                                    break;
                                }
                            }
/*                            if (jsonArray.optJSONObject(i).has("promptView")) {
                                Log.i("tempPromptView", jsonArray.optJSONObject(i).optJSONObject("promptView").optString("android"));
                                Log.i("tempSurveyId", jsonArray.optJSONObject(i).optString("objectId"));
                                String tempPromptView = jsonArray.optJSONObject(i).optJSONObject("promptView").optString("android");
                                String tempSurveyId = jsonArray.optJSONObject(i).optString("objectId");
                                Boolean isRequired;
                                if (jsonArray.optJSONObject(i).has("required")) {
                                    isRequired = jsonArray.optJSONObject(i).optBoolean("required");
                                } else {
                                    isRequired = false;
                                }
                                ArrayList<HashMap<String, Boolean>> tempList;
                                if (NetworkConstant.hasPromptViewMap.containsKey(tempPromptView)) {
                                    HashMap<String, Boolean> tempMap = new HashMap<>();
                                    tempMap.put(tempSurveyId, isRequired);
                                    tempList = NetworkConstant.hasPromptViewMap.get(tempPromptView);
                                    tempList.add(tempMap);
                                } else {
                                    tempList = new ArrayList<>();
                                    HashMap<String, Boolean> tempMap = new HashMap<>();
                                    tempMap.put(tempSurveyId, isRequired);
                                    tempList.add(tempMap);
                                }
                                NetworkConstant.hasPromptViewMap.put(tempPromptView, tempList);
                            } else {
                                Log.i("promptView", "No promptView in the response");
                                NetworkConstant.noPromptView.add(jsonArray.optJSONObject(i));
                            }
                        }
                        if (jsonArray.length() != 0 && !jsonArray.optJSONObject(0).has("promptView")) {
                            String surId = jsonArray.optJSONObject(0).optString("objectId");
                            Boolean isRequired;
                            if (jsonArray.optJSONObject(0).has("required")) {
                                isRequired = jsonArray.optJSONObject(0).optBoolean("required");
                            } else {
                                isRequired = false;
                            }
                            ActivityLogConstant.saveSurvId("survId", surId, thirdUser);
                            SurveyAlertManager.showDialog(thirdUser, aKey, surId, devId, isRequired);*/
                       }
                    }
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.i("getMySurver", e.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getEachQuestion(String aKey, String devId, final String packId, final boolean isRequired) {
        try {
            nm.getInAppSurveySections(aKey, devId, packId, new NetworkConnect.CallbackInterfaceQ() {
                @Override
                public void onDownloadSuccess(boolean success, org.json.simple.JSONObject response) {
                    Log.i ("getEachQuestion", response.toString());
                    SurveyAlertManager.showDialog(thirdUser, response, packId, isRequired);
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.i("getEachQuestion", e.toString());

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCarrier() {
        TelephonyManager manager = (TelephonyManager)thirdUser.getSystemService(Context.TELEPHONY_SERVICE);
        String carrierName = manager.getNetworkOperatorName();
        return carrierName;
    }



    /*
        SurveyAlertManager.showDialog(thirdUser);
*/

}
