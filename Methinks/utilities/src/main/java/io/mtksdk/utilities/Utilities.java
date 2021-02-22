package io.mtksdk.utilities;

import org.json.JSONObject;

import java.util.HashMap;

public class Utilities {

    public String getStringfyJson(HashMap <String, String> map) {
        JSONObject json = new JSONObject(map);
        return json.toString();
    }

}
