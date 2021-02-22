package io.mtksdk.fingerprintmanager;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kgy 2019. 9. 24.
 * Getting host's device info
 */

public class FingerPrintManager extends Activity{

    protected Context thirdUser;
    public FingerPrintManager(Context act) {
        this.thirdUser = act;
    }

    public HashMap<String, String> getInfo(Context act) {
        HashMap<String, String> deviceInfo = new HashMap<>();
        deviceInfo.put("serial", Build.SERIAL);
        deviceInfo.put("model", Build.MODEL);
        deviceInfo.put("id", Build.ID);
        deviceInfo.put("manufacture", Build.MANUFACTURER);
        deviceInfo.put("brand", Build.BRAND);
        deviceInfo.put("type", Build.TYPE);
        deviceInfo.put("user", Build.USER);
        deviceInfo.put("sdk", Build.VERSION.SDK);
        deviceInfo.put("host", Build.HOST);
        deviceInfo.put("fingerPrint", Build.FINGERPRINT);

        for (Map.Entry<String, String> entry : deviceInfo.entrySet()){
            if (entry.getValue() == null) {
                deviceInfo.put(entry.getKey(), "");
            }
        }

        if (deviceInfo != null) {
            JSONObject json = new JSONObject(deviceInfo);
            Log.d("convertJson", json.toString());
        }


        /*if (deviceInfo.get("serial") != null) {
            Log.d("FingerPrintGetInfo", "the serial number is " + deviceInfo.get("serial"));
        } else {
            Log.d("FingerPrintGetInfo", "I did not get the information fix it");
        }
        //viewInfo(deviceInfo, act);*/
        return deviceInfo;
    }

    public static String getUuid() {
        String deviceId = "";
        if (Build.VERSION.SDK_INT >= 26) {
            deviceId = deviceId + getAlphaNumericString(12) + getDateAndTime();
        } else {
            deviceId = Build.SERIAL;
        }
        return deviceId;
    }

    public void viewInfo(HashMap<String, String> map, Context act) {
        if (map.get("serial") != null) {
            Log.d("FingerPrintViewInfo", "the serial number is " + map.get("serial"));
        } else {
            Log.d("FingerPrintViewInfo", "I did not get the information fix it");
        }
        Toast.makeText(act, "Name of Brand is " + map.get("Brand") + " and Model is " + map.get("Manufacture"), Toast.LENGTH_LONG).show();
    }

    public static String getDateAndTime() {
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat crunchifyFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");

        String currentTime = crunchifyFormat.format(today);

        Log.i("CurrentTIme", "CurrentTime : " + currentTime);

        try {
            Date date = crunchifyFormat.parse(currentTime);
            long epochTime = date.getTime();
            Log.i("CurrentTimeEpoch", "current time in epoch " + epochTime/1000);
            epochTime = epochTime/1000;
            return Long.toString(epochTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getAlphaNumericString(int n) {
        String alphaNumericBound = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int) (alphaNumericBound.length() * Math.random());
            sb.append(alphaNumericBound.charAt(index));
        }
        return sb.toString();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.toLowerCase().startsWith(manufacturer.toLowerCase())) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
