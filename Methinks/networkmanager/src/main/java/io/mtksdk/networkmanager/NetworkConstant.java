package io.mtksdk.networkmanager;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimeZone;

public class NetworkConstant {
    public static JSONObject viewDic = new JSONObject();
    public static HashSet<Integer> screenSet = new HashSet<>();
    public static JSONObject userAttri = new JSONObject();
    public static HashMap<String, ArrayList<HashMap<String, Boolean>>> hasPromptViewMap = new HashMap<>();
    public static ArrayList<JSONObject> noPromptView = new ArrayList<>();
    public static ArrayList<JSONObject> hasPromptView = new ArrayList<>();
    public static Integer viewDicVer;
    public static Integer screenVer;
    public static String devId;
    public static Boolean isActive;
    public static String sId;
    public static Boolean isNewSession = true;
    public static String userId;
    public static Boolean isSetUserCalled;
    public static Boolean isLogUserAttCalled;
    public static String eventName;
    public static String eventType;
    public static Double eventTS;
    public static Integer sessionCount;
    public static String SERVER_URL = "https://sdk.methinks.io/parse/";
    public static String AppId = BuildConfig.AppId_RELEASE;
    public static String RestAPIKey = BuildConfig.RestAPI_RELEASE;
    public static Boolean isCustomViewCalled = false;
    public static String newCustomViewName = "";
    public static HashMap<String, String> customViewMap = new HashMap<>();
    public static HashSet<String> disableViewSet = new HashSet<>();




    public static String getAlphaNumericString(int n) {
        String alphaNumericBound = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (alphaNumericBound.length() * Math.random());
            sb.append(alphaNumericBound.charAt(index));
        }
        return sb.toString();
    }

    public static String getTimeZoneAbbr() {
        String tz = TimeZone.getDefault().getID();
        return tz;
    }

}
