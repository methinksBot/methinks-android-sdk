package io.mtksdk.activitylogmanager;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.mtksdk.inappsurvey.ViewConstant;
import io.mtksdk.networkmanager.NetworkConstant;
import io.mtksdk.networkmanager.NetworkConnect;
//import io.mtksdk.inappsurvey.BottomSheetFragment;
//import io.mtksdk.inappsurvey.SurveyAlertManager;
//import io.mtksdk.inappsurvey.ViewConstant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kgy 2019. 9. 24.
 */

public class SdkLifecycle{
    protected static List trackLog;
    protected static ArrayList<String> trackLogTemp;
    /*protected static HashSet<String> unResolvedViewNameSet;*/
    protected static NetworkConnect nm = new NetworkConnect();
    protected static Activity currActivity;
    protected String indexForCurrScreenshot;
    protected static Application.ActivityLifecycleCallbacks callbacks;
    private static int countRegister = 0;
    protected  static ArrayList<JSONArray> copyLog;
    protected static String currName;
    protected static Boolean disabled = false;



    public static void init(final Application app) {
        Log.i("SdkLifecycle", "SDK Initiated");
        Log.i("SdkDevId", NetworkConstant.devId);
        callbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
                Log.i("timeLogActivity", "onCreate " + activity.getLocalClassName());
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                Log.i("timeLogActivity", "onStart " + activity.getLocalClassName());
                currActivity = activity;
                currName = activity.getClass().getSimpleName();
                if (ActivityLogConstant.isInit) {
                    ActivityLogConstant.isBackground = false;
                    ActivityLogConstant.isFromBackground = false;
                    ActivityLogConstant.currTime = 0L;
                    ActivityLogConstant.lastPausedTime = 0L;
                    /*ActivityLogConstant.countDirtyLog = 0;*/
                    NetworkConstant.viewDic = ActivityLogConstant.getViewDic("viewDic", activity);
                    /*ActivityLogConstant.actAuthKey = ActivityLogConstant.getAuthKey("aKey", activity);*/
                    NetworkConstant.devId = ActivityLogConstant.getDevId("devId", activity);
                    NetworkConstant.screenSet = ActivityLogConstant.getScreenSet("screenSet", activity);
                    ActivityLogConstant.isInit = false;
                    copyLog = new ArrayList<>();
                    /*NetworkConstant.userId = ActivityLogConstant.getUserId("userId", activity);*/
                }

                if (!NetworkConstant.disableViewSet.contains(currName)) {
                    if (ActivityLogConstant.actAuthKey != null && NetworkConstant.devId != null) {
                        ArrayList<JSONArray> savedBeforeTerm = ActivityLogConstant.getArrayList("saveList", activity);
                        ArrayList<String> savedEventBeforeTerm = ActivityLogConstant.getEventArr("saveEventArr", activity);
                        copyLog = savedBeforeTerm;
                        String sId = ActivityLogConstant.getSid("saveSid", currActivity);
                        if (savedBeforeTerm != null && savedBeforeTerm.size() > 0 && sId != null && sId.length() != 0) {
                            JSONArray logJson = new JSONArray(savedBeforeTerm);
                            sendLogSession(logJson, sId, savedEventBeforeTerm);
                        }
                        //shower gunyeop gi mo zzi

                        if (NetworkConstant.isSetUserCalled != null && NetworkConstant.isSetUserCalled && NetworkConstant.userId != null && NetworkConstant.userId.length() != 0) {
                            setUser(NetworkConstant.userId);
                            NetworkConstant.isSetUserCalled = false;
                        }

                        if (NetworkConstant.isLogUserAttCalled != null && NetworkConstant.isLogUserAttCalled && NetworkConstant.userAttri.length() != 0) {
                            logUserAttribute(NetworkConstant.devId, ActivityLogConstant.actAuthKey, NetworkConstant.userAttri);
                            NetworkConstant.isLogUserAttCalled  = false;
                        }
                    }

                    //Create and change to the custom view name
                    if (NetworkConstant.isCustomViewCalled && NetworkConstant.newCustomViewName != null && NetworkConstant.newCustomViewName.length() != 0) {
                        String newViewName = NetworkConstant.newCustomViewName;
                        if (NetworkConstant.customViewMap == null) {
                            NetworkConstant.customViewMap.put(currName, newViewName);
                            NetworkConstant.customViewMap.put(newViewName, currName);
                        } else if (!NetworkConstant.customViewMap.containsKey(currName)) {
                            NetworkConstant.customViewMap.put(currName, newViewName);
                            NetworkConstant.customViewMap.put(newViewName, currName);
                        }
                        NetworkConstant.isCustomViewCalled = false;
                        NetworkConstant.newCustomViewName = "";
                    }
                    //might need to add promptView here

                    disabled = false;
                } else {
                    disabled  = true;
                }
            }


            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Log.i("timeLogActivity", "onResume save date and time " + activity.getLocalClassName());
                ActivityLogConstant.currTime = getDateAndTime();
                if (ActivityLogConstant.isFromBackground) {                                                 //check if the app from background
                    Long checkBackgroundTime = ActivityLogConstant.currTime - ActivityLogConstant.lastPausedTime;
                    if (checkBackgroundTime > 600L) {
//                            ViewConstant.hasSurveyed = false; // need to take care of showing the survey once per one session
                        NetworkConstant.isNewSession = true;
                        NetworkConstant.sessionCount++;
                        if (ActivityLogConstant.actAuthKey != null && NetworkConstant.devId != null) {
                            ArrayList<JSONArray> savedBeforeBackground = ActivityLogConstant.getArrayList("saveList", activity);
                            ArrayList<String> savedEventBeforeBack = ActivityLogConstant.getEventArr("saveEventArr", activity);
                            copyLog = savedBeforeBackground;
                            String sId = ActivityLogConstant.getSid("saveSid", currActivity);
                            if (savedBeforeBackground != null && savedBeforeBackground.size() > 0 && sId != null && sId.length() != 0) {
                                JSONArray logJson = new JSONArray(savedBeforeBackground);
                                sendLogSession(logJson, sId, savedEventBeforeBack);
                            }
                            ActivityLogConstant.timeLog.clear();
                        }
                    } else {
                        if (ActivityLogConstant.actAuthKey != null && NetworkConstant.devId != null) {
                            ArrayList<JSONArray> savedBeforeBackground = ActivityLogConstant.getArrayList("saveList", activity);
                            ArrayList<String> savedEventBeforeBack = ActivityLogConstant.getEventArr("saveEventArr", activity);
                            copyLog = savedBeforeBackground;
                            String sId = ActivityLogConstant.getSid("saveSid", currActivity);
                            if (savedBeforeBackground != null && savedBeforeBackground.size() > 0 && sId != null && sId.length() != 0) {
                                //Create Send() method to send data
                                JSONArray logJson = new JSONArray(savedBeforeBackground);
                                sendLogSession(logJson, sId, savedEventBeforeBack);
                            }
                            ActivityLogConstant.timeLog.clear();
                        }
                        NetworkConstant.isNewSession = false;
                    }
                    //showing that activity is same as before going to the background
                    ActivityLogConstant.prevActNum = -1;
                    ActivityLogConstant.prevActName = "";
                    NetworkConstant.sId = NetworkConstant.getAlphaNumericString(12);
                    ActivityLogConstant.isFromBackground = false;
                }
                System.out.println(ActivityLogConstant.timeLog.size());
                ActivityLogConstant.isBackground = false;
                if (!disabled) {
                    if (NetworkConstant.customViewMap != null && NetworkConstant.customViewMap.containsKey(currName)) {
                        currName = NetworkConstant.customViewMap.get(currName);
                    }

                    if (!ActivityLogConstant.isInit && NetworkConstant.devId != null && NetworkConstant.viewDic != null && ActivityLogConstant.actAuthKey != null) {
                        if (ActivityLogConstant.timeLogTemp.size() > 0) {
                            for (int i = 0; i < ActivityLogConstant.timeLogTemp.size(); i++) {
                                ArrayList<String> currArr = ActivityLogConstant.timeLogTemp.get(i);
                                trackLog = new ArrayList();
                                if (!NetworkConstant.viewDic.has(currArr.get(0))) {
                                    setViewNames(currArr.get(0));
                                    ActivityLogConstant.countDirtyLog++;
                                    trackLog.add(currArr.get(0));
                                    Long longTime = convertStringToLong(currArr.get(1));
                                    trackLog.add(longTime);
                                    trackLog.add("X");
                                    ActivityLogConstant.timeLog.add(new JSONArray(trackLog));
                                } else {
                                    int viewNameIdxTemp = NetworkConstant.viewDic.optInt(currArr.get(0));
                                    trackLog.add(viewNameIdxTemp);
                                    trackLog.add(ActivityLogConstant.currTime);
                                    ActivityLogConstant.timeLog.add(new JSONArray(trackLog));
                                }
                            }
                            ActivityLogConstant.timeLogTemp.clear();
                        }

                        trackLog = new ArrayList();
                        if (!NetworkConstant.viewDic.has(currName)) {
                            if (ActivityLogConstant.prevActName == null || !ActivityLogConstant.prevActName.equals(currName)) {
                                setViewNames(currName);
                                ActivityLogConstant.countDirtyLog++;
                                trackLog.add(currName);
                                trackLog.add(ActivityLogConstant.currTime);
                                trackLog.add("X");
                                ActivityLogConstant.timeLog.add(new JSONArray(trackLog));
                            }
                        } else {
                            int viewNameIndex = NetworkConstant.viewDic.optInt(currName);
                            if (ActivityLogConstant.prevActNum == -1 || ActivityLogConstant.prevActNum != viewNameIndex) {
                                if (NetworkConstant.screenSet != null && !NetworkConstant.screenSet.contains(viewNameIndex)) {
                                    ActivityLogConstant.hasScreenShot = false;
                                }
                                trackLog.add(viewNameIndex);
                                trackLog.add(ActivityLogConstant.currTime);
                                ActivityLogConstant.timeLog.add(new JSONArray(trackLog));
                            }
                        }
                    } else {
                        trackLogTemp = new ArrayList();
                        trackLogTemp.add(currName);
                        String time = convertLongToString(ActivityLogConstant.currTime);
                        trackLogTemp.add(time);
                        ActivityLogConstant.timeLogTemp.add(trackLogTemp);
                    }

                    System.out.println(ActivityLogConstant.timeLog.size());

                    /*Checking viewDic has been updated and removing dirty log if viewDic has the activity name*/
                    try {
                        for (JSONArray jsonArr : ActivityLogConstant.timeLog) {
                            if (jsonArr.length() == 3 && NetworkConstant.viewDic.has(jsonArr.optString(0))) {
                                int viewDicIndex = NetworkConstant.viewDic.optInt(jsonArr.optString(0));
                                jsonArr.put(0, viewDicIndex);
                                jsonArr.remove(2);
                                ActivityLogConstant.countDirtyLog--;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //survey submission
                    if (!ActivityLogConstant.hasSentAnswer && ViewConstant.hasSurveyed && ActivityLogConstant.actAuthKey != null && ViewConstant.packId != null && NetworkConstant.devId != null) {
                        JSONObject currAnswer = ViewConstant.getInAppAnswer();
                        if (currAnswer != null) {
                            surveyCompletion(ActivityLogConstant.cKey, ActivityLogConstant.actAuthKey, NetworkConstant.devId, ViewConstant.packId, currAnswer);
                        }
                        ActivityLogConstant.hasSentAnswer = true;
                    }
                }
            }


            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Log.i("timeLogActivity", "onPause " + activity.getClass().getSimpleName());
                ActivityLogConstant.isBackground = true;
                if (!disabled) {
                    ActivityLogConstant.prevActName = activity.getClass().getSimpleName();
                    if (ActivityLogConstant.prevActName != null && NetworkConstant.viewDic != null) {
                        if (NetworkConstant.viewDic.has(ActivityLogConstant.prevActName)) {
                            ActivityLogConstant.prevActNum = NetworkConstant.viewDic.optInt(ActivityLogConstant.prevActName);
                        }
                    }
                    ActivityLogConstant.lastPausedTime = getDateAndTime();
                    if (NetworkConstant.devId != null && ActivityLogConstant.actAuthKey != null && !ActivityLogConstant.hasScreenShot) {         //should be screenDic not viewDic
                        String viewName = activity.getClass().getSimpleName();
                        if (NetworkConstant.customViewMap != null && NetworkConstant.customViewMap.containsKey(viewName)) {
                            viewName = NetworkConstant.customViewMap.get(viewName);
                        }
                        Bitmap b = ScreenShotManager.getScreenShot(activity.getWindow().getDecorView().getRootView());
                        if (b != null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            b.compress(Bitmap.CompressFormat.JPEG, 30, stream);
                            byte[] byteArray = stream.toByteArray();
                            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                            logScreenshot(NetworkConstant.devId, ActivityLogConstant.actAuthKey, encoded,viewName);
                        }
                        ActivityLogConstant.hasScreenShot = true;
                    }
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                Log.i("timeLogActivity", "onStop " + activity.getLocalClassName());
                if (ActivityLogConstant.isBackground != null && ActivityLogConstant.isBackground && NetworkConstant.devId != null && ActivityLogConstant.actAuthKey != null) {
                    ActivityLogConstant.isFromBackground = true;
                    Log.i("isBackground", "App is in Background");
                    trackLog = new ArrayList();
                    trackLog.add(0);
                    trackLog.add(ActivityLogConstant.lastPausedTime);
                    ActivityLogConstant.timeLog.add(new JSONArray(trackLog));

                    if (ActivityLogConstant.countDirtyLog > 0) {
                        reportDirtyLog(NetworkConstant.devId, ActivityLogConstant.actAuthKey);
                        ActivityLogConstant.countDirtyLog = 0;
                    }

                    for (int i = 0; i < ActivityLogConstant.timeLog.size(); i++) {
                        Log.i("detailOfLog", ActivityLogConstant.timeLog.get(i).toString());
                    }

                    if (ActivityLogConstant.timeLog.size() > 1) {
                        copyLog = ActivityLogConstant.timeLog;
                        JSONArray logJSON = new JSONArray(ActivityLogConstant.timeLog);

                        sendLogSession(logJSON, NetworkConstant.sId, ActivityLogConstant.eventArr);
                        ActivityLogConstant.eventArr.clear();
                        ActivityLogConstant.timeLog.clear();
                    }
                } else {
                    Log.i("isBackground", "App is in Foreground");
                }

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                Log.i("timeLogActivity", "onDestroy " + activity.getLocalClassName());
                if (ActivityLogConstant.isBackground != null && ActivityLogConstant.isBackground) {
                    app.unregisterActivityLifecycleCallbacks(callbacks);
                    countRegister--;
                }
            }
        };
        if (countRegister == 0) {
            app.registerActivityLifecycleCallbacks(callbacks);
            countRegister++;
        }
    }

    /**
     * this function returns current date and time in epoch format.
     * @return
     */
    public static Long getDateAndTime() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat crunchifyFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");

        String currentTime = crunchifyFormat.format(today);

        Log.i("CurrentTIme", "CurrentTime : " + currentTime);

        try {
            Date date = crunchifyFormat.parse(currentTime);
            long epochTime = date.getTime();
            Log.i("CurrentTimeEpoch", "current time in epoch " + epochTime/1000);
            return epochTime/1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /**
     * convert long to string
     * @param lg 1569358831
     * @return "1569358831"
     */
    protected static String convertLongToString(Long lg) {
        return Long.toString(lg);
    }

    protected static Long convertStringToLong(String time) {
        return Long.parseLong(time);
    }

    /**
     * this function splits the string into string array return last string.
     * @param string "io.methinks.android.activity.MainActivity"
     * @return "MainActivity"
     */
    protected static String getActivityNameOnly(String string) {
        if (string.contains(".")) {
            String[] splits = string.split("\\.");
            return splits[splits.length - 1];
        } else {
            return string;
        }
    }

    /**
     * Server call for setting/updating view names
     * @param viewName
     */
    protected static void setViewNames(String viewName) {
        try {
            if (viewName.length() == 0) {
                return;
            }
            nm.setViewName(ActivityLogConstant.actAuthKey, viewName, NetworkConstant.devId, new NetworkConnect.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("setViewName", response.toString());
                    NetworkConstant.viewDicVer = response.optJSONObject("result").optInt("viewVer");
                    NetworkConstant.viewDic = response.optJSONObject("result").optJSONObject("viewDic");
                    NetworkConstant.screenVer = response.optJSONObject("result").optInt("screenVer");
                    NetworkConstant.screenSet = ActivityLogConstant.convertToSet(response.optJSONObject("result").optJSONArray("screenArr").toString());
                    ActivityLogConstant.saveViewDicVer("viewDicVer", NetworkConstant.viewDicVer, currActivity);
                    ActivityLogConstant.saveViewDic("viewDic", NetworkConstant.viewDic, currActivity);
                    ActivityLogConstant.saveScreenVer("screenVer", NetworkConstant.screenVer, currActivity);
                    ActivityLogConstant.saveScreenSet("screenSet", NetworkConstant.screenSet, currActivity);
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.e("setViewName", e.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Server call for sending log session
     * @param array
     * @param sId
     */
    protected static void sendLogSession(final JSONArray array, String sId, final ArrayList<String> eventArr) {
        try {
            nm.logSession(ActivityLogConstant.actAuthKey, sId, NetworkConstant.devId, NetworkConstant.isNewSession, "android", array, NetworkConstant.sessionCount, eventArr, new NetworkConnect.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("sendLogSession", response.toString());
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.i("sendLogSession", e.toString());
                    ActivityLogConstant.saveArrayList(copyLog, "saveList", currActivity);
                    ActivityLogConstant.saveSid("saveSid", NetworkConstant.sId, currActivity);
                    ActivityLogConstant.saveSIsNew("saveSIsNew", NetworkConstant.isNewSession, currActivity);
                    ActivityLogConstant.saveEventArr(eventArr, "saveEventArr", currActivity);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Server call for setting userId
     * @param userId
     */
    protected static void setUser(String userId) {
        nm.setClientUserId(userId, ActivityLogConstant.actAuthKey, new NetworkConnect.CallbackInterface() {
            @Override
            public void onDownloadSuccess(boolean success, JSONObject response) {
                Log.i("setUser", response.toString());
            }

            @Override
            public void onDownloadFail(boolean fail, Throwable e) {
                Log.e("setUser", "failed" + e.toString());
            }
        });
    }

    /**
     * Server call to report the dirtyLog
     * @param devId
     * @param aKey
     */
    protected static void reportDirtyLog(String devId, String aKey) {
        nm.reportForDirtyLog(devId, aKey, new NetworkConnect.CallbackInterface() {
            @Override
            public void onDownloadSuccess(boolean success, JSONObject response) {
                Log.i("reportDirtyLog", response.toString());
            }

            @Override
            public void onDownloadFail(boolean fail, Throwable e) {
                Log.e("reportDirtyLog", "failed" + e.toString());
            }
        });
    }

    protected static void logScreenshot(String devId, String aKey, String base64, String viewName) {
        try {
            nm.logScreenShot(aKey, devId, base64, viewName, new NetworkConnect.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("logScreenshot", response.toString());
                    NetworkConstant.screenVer = response.optJSONObject("result").optInt("screenVer");
                    NetworkConstant.screenSet = ActivityLogConstant.convertToSet(response.optJSONObject("result").optJSONArray("screenArr").toString());
                    ActivityLogConstant.saveScreenVer("screenVer", NetworkConstant.screenVer, currActivity);
                    ActivityLogConstant.saveScreenSet("screenSet", NetworkConstant.screenSet, currActivity);
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.e("logScreenshot", "failed" + e.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected static void logUserAttribute(String devId, String aKey, JSONObject attributes) {
        try {
            nm.logUserAttributes(devId, aKey, attributes, new NetworkConnect.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("logUserAttribute", response.toString());
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.i("logUserAttribute", "failed" + e.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void surveyCompletion(String cKey, String aKey, String devId, String packId, JSONObject answer) {
        try {
            nm.inAppSurveyCompletion(cKey, aKey, devId, packId, answer, new NetworkConnect.CallbackInterface() {
                @Override
                public void onDownloadSuccess(boolean success, JSONObject response) {
                    Log.i("surveyCompletion", response.toString());
                    ViewConstant.setInAppAnswer(new JSONObject());
                }

                @Override
                public void onDownloadFail(boolean fail, Throwable e) {
                    Log.i("surveyCompletion", e.toString());
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
