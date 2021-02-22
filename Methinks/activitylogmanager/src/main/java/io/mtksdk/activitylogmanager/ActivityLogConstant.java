package io.mtksdk.activitylogmanager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by kgy on 2019. 9. 24.
 */

public class ActivityLogConstant extends AppCompatActivity {
    public static ArrayList<JSONArray> timeLog = new ArrayList();
    public static ArrayList<ArrayList<String>> timeLogTemp = new ArrayList();
    public static HashSet<String> hasTime = new HashSet<>();
    public static Long lastPausedTime;
    public static Boolean isBackground;
    public static Boolean isInit = false;
    public static String actAuthKey;
    public static Long currTime;
    public static Boolean isFromBackground = false;
    public static Integer countDirtyLog = 0;
    public static Boolean hasScreenShot = true;
    public static Integer countLogs = 0;
    public static HashSet<Integer> logSet = new HashSet<>();
    public static Boolean hasSentAnswer = false;
    public static String prevActName = new String();
    public static Integer prevActNum = -1;
    public static ArrayList<String> eventArr = new ArrayList<>();
    public static String cKey;



    /**
     * saving session log when sending data failed
     * @param list : session log arraylist
     * @param key setting the key for this saved data
     * @param activity
     */
    public static void saveArrayList(ArrayList<JSONArray> list, String key, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String saveList = gson.toJson(list);
        editor.putString(key, saveList);
        editor.apply();
    }

    public static void saveEventArr(ArrayList<String> list, String key, Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String eventArr = gson.toJson(list);
        editor.putString(key, eventArr);
        editor.apply();
    }

    /**
     * Saving device id into local (string)
     * @param key : required to retrieve stored devId
     * @param devId : device id from the server
     * @param activity : required to save data into getDefaultSharedPreferences
     */
    public static void saveDevId(String key, String devId, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, devId);
        editor.apply();
    }

    /**
     *Saving view dictionary version (int)
     * @param key : required to retrieve stored viewDicVer
     * @param viewDicVer : viewDicVer from the server
     * @param activity : required to save data into getDefaultSharedPreferences
     */
    public static void saveViewDicVer(String key, Integer viewDicVer, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, viewDicVer);
        editor.apply();
    }

    /**
     * Saving api key (string)
     * @param key : required to retrieve stored api key
     * @param aKey : methinks provided this to client
     * @param activity
     */
    public static void saveAuthKey(String key, String aKey, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, aKey);
        editor.apply();
    }

    /**
     * Saving view dictionary (array)
     * @param key : required to retrieve stored view dictionary
     * @param viewDic : most updated version of viewDic from the server
     * @param activity
     */
    public static void saveViewDic(String key, JSONObject viewDic, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String saveList = gson.toJson(viewDic);
        editor.putString(key, saveList);
        editor.apply();
    }

    public static void saveIsKilled(String key, JSONObject isKilled, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String saveIsKilled = gson.toJson(isKilled);
        editor.putString(key, saveIsKilled);
        editor.apply();
    }

    public static void saveUserId(String key, String userId, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, userId);
        editor.apply();
    }

    public static void saveIsActive(String key, Boolean isActive, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, isActive);
        editor.apply();
    }

    public static void saveScreenVer(String key, Integer screenVer, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(key, screenVer);
        editor.apply();
    }

    public static void saveScreenSet(String key, HashSet<Integer> screenSet, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String saveScreen = gson.toJson(screenSet);
        editor.putString(key, saveScreen);
        editor.apply();
    }

    public static void saveEventMap(String key, HashMap<String, ArrayList<Long>> map, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String saveMap = gson.toJson(map);
        editor.putString(key, saveMap);
        editor.apply();
    }

    public static void saveUUID(String key, String uuid, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, uuid);
        editor.apply();
    }

    public static void saveSid(String key, String sId, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, sId);
        editor.apply();
    }

    public static void saveSIsNew(String key, Boolean isNew, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, isNew);
        editor.apply();
    }

    public static void saveSurvId(String key, String surId, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, surId);
        editor.apply();
    }


    public static String getSurvId(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(key)) {
            String survId = prefs.getString(key, null);
            return survId;
        } else {
            return null;
        }
    }

    public static Boolean getSIsNew(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(key)) {
            Boolean output = prefs.getBoolean(key, true);
            editor.remove(key);
            editor.apply();
            return output;
        } else {
            return null;
        }
    }

    public static String getSid(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(key)) {
            String sId =prefs.getString(key, null);
            editor.remove(key);
            editor.apply();
            return sId;
        } else {
            return null;
        }
    }


    public static String getUUID(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getString(key, null);
        } else {
            return null;
        }
    }


    public static HashMap<String, ArrayList<Long>> getEventMap(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        if (json != null) {
            Type type = new TypeToken<HashMap<String, ArrayList<Long>>>(){}.getType();
            return gson.fromJson(json, type);
        } else {
            return null;
        }
    }

    public static HashSet<Integer> getScreenSet(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        if (json != null) {
            Type type = new TypeToken<HashSet<Integer>>(){}.getType();
            return gson.fromJson(json, type);
        } else {
            return null;
        }
    }

    public static Integer getScreenVer(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getInt(key, -1);
        } else {
            return null;
        }
    }


    public static Boolean getIsActive(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getBoolean(key, false);
        } else {
            return null;
        }
    }

    /**
     * Get devId
     * @param key : key that putted when saving the devId
     * @param activity
     * @return
     */
    public static String getDevId(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getString(key, null);
        } else {
            return null;
        }
    }

    /**
     * Get api key
     * @param key : key that putted when saving authKey
     * @param activity
     * @return
     */
    public static String getAuthKey(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getString(key, null);
        } else {
            return null;
        }
    }

    /**
     * Get view dictionary version
     * @param key : key that putted when saving viewDicVer
     * @param activity
     * @return
     */
    public static Integer getViewDicVer(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getInt(key, -1);
        } else {
            return null;
        }
    }

    public static JSONObject getIsKilled(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        if (json != null) {
            Type type = new TypeToken<JSONObject>(){}.getType();
            return gson.fromJson(json, type);
        } else {
            return null;
        }
    }

    /**
     * Get most updated viewDic
     * @param key : key that putted when saving
     * @param activity
     * @return
     */
    public static JSONObject getViewDic(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        if (json != null) {
            Type type = new TypeToken<JSONObject>(){}.getType();
            /*SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.apply();*/
            return gson.fromJson(json, type);
        } else {
            return null;
        }
    }

    public static String getUserId(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        if (prefs.contains(key)) {
            return prefs.getString(key, null);
        } else {
            return null;
        }
    }

    /**
     * Retrieving saved data to update or to send it to server
     * @param key should use same key when using saveArrayList() function
     * @param activity
     * @return arraylist of saved session log
     */
    public static ArrayList<JSONArray> getArrayList(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<JSONArray>>(){}.getType();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
        return gson.fromJson(json, type);
    }

    public static ArrayList<String> getEventArr(String key, Context activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        editor.apply();
        return gson.fromJson(json, type);
    }

    public static HashSet<Integer> convertToSet(String jsonArr) {
        HashSet<Integer> mySet = new Gson().fromJson(jsonArr, new TypeToken<HashSet<Integer>>(){}.getType());
        return mySet;
    }

    public static ArrayList<JSONObject> convertToArrayList(String jsonArr) {
        ArrayList<JSONObject> myList = new Gson().fromJson(jsonArr, new TypeToken<ArrayList<JSONObject>>(){}.getType());
        Log.i("convertArrList", myList.get(0).toString());
        return myList;
    }
}
