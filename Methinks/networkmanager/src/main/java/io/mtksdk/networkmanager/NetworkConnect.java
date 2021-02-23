package io.mtksdk.networkmanager;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;


/**
 * Created by kgy 2019. 9. 24.
 */

public class NetworkConnect extends  Activity{

    //prod

    final String METHINKS_URL_CAMPAIGNITEM = "https://dev.methinks.io/parse/functions/getMyCampaignItems";
    final String METHINKS_URL_CAMPAIGNSUBITEM = "https://dev.methinks.io/parse/functions/getCheckInQuestionsWithCheckinList";
    final static String METHINKS_SDK_URL = "functions/setSDKUser";
    final String METHINKS_SDK_URL_SETVIEWNAME = "functions/setViewName";
    final String METHINKS_SDK_URL_LOGSESSION = "functions/logSession";
    final String METHINKS_SDK_URL_SETUSERID = "functions/setClientUserId";
    final String METHINKS_SDK_URL_REPORTFORDIRTYLOG = "functions/reportForDirtyLog";
    final String METHINKS_SDK_URL_LOGSCREENSHOT = "functions/logScreenShot";
    final String METHINKS_SDK_URL_ATTRIBUTES = "functions/logUserAttributes";
    final String METHINKS_SDK_URL_LOGEVNET = "functions/logEvent";
    final String METHINKS_SDK_URL_GETSURVEYS = "functions/getMyTasks";
    final String METHINKS_SDK_URL_SURVEYQUESTION = "functions/getSurveyQuestions";
    final String METHINKS_SDK_URL_COMPLETE = "functions/surveyCompletion";
    final String METHINKS_SDK_URL_GETFULLSURVEY = "functions/getFullSurveySections";
    final String METHINKS_SDK_URL_INAPPCOMPLETE = "functions/inAppSurveyCompletion";
    final String METHINKS_SDK_URL_GETINAPPSURVEY = "functions/getInAppSurveySections";

    private String campaignItemId;

    public interface CallbackInterface {
        public void onDownloadSuccess(boolean success, JSONObject response);
        public void onDownloadFail(boolean fail, Throwable e);
    }

    public interface CallbackInterfaceQ {
        public void onDownloadSuccess(boolean success, org.json.simple.JSONObject response);
        public void onDownloadFail(boolean fail, Throwable e);
    }

    public NetworkConnect() {}

    public void setViewName(String apiKey, String viewName, String devId, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientSdk = new AsyncHttpClient();
            clientSdk.addHeader("X-Parse-Application-Id",NetworkConstant.AppId);
            clientSdk.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("aKey", apiKey);
            params.put("viewName", viewName);
            params.put("devId", devId);
            params.put("os", "android");


            StringEntity entity = new StringEntity(params.toString());

            clientSdk.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_SETVIEWNAME, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.d("sdkServerViewName", "Code:" + e.toString());
                    Log.d("sdkServerViewName", "StatusCode : " + statusCode);
                    callback.onDownloadFail(false, e);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSDKUser(final String cKey, final String apiKey, final String bundleId, final String uuid, String os, String locale, String timeZoneStr, ArrayList<JSONArray> prevSData, String prevSId, Boolean prevSIsNew, ArrayList<String> prevEvent, String deviceModel, String carrier, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientSdk = new AsyncHttpClient();

            clientSdk.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientSdk.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            /*clientSdk.setTimeout(20000);*/

            JSONObject params = new JSONObject();
            params.put("cKey", cKey);
            params.put("aKey", apiKey);
            params.put("bundleId", bundleId);
            params.put("UUID", uuid);
            params.put("os", os);
            if (prevSData != null && prevSData.size() > 0) {
                JSONArray logJson = new JSONArray(prevSData);
                params.put("prevSData", logJson);
            }
            if (prevEvent != null && prevEvent.size() > 0) {
                JSONArray eventArr = new JSONArray(prevEvent);
                params.put("prevEvents", eventArr);
            }
            if (prevSId != null && prevSId.length() > 0) {
                params.put("prevSId", prevSId);
            }
            if (prevSIsNew != null) {
                params.put("prevSIsNew", prevSIsNew);
            }
            if (locale != null && locale.length() != 0) {
                params.put("locale", locale);
            }
            if (timeZoneStr != null && timeZoneStr.length() != 0) {
                params.put("timeZoneStr", timeZoneStr);
            }
            if (NetworkConstant.devId != null) {
                params.put("devId", NetworkConstant.devId);
            }
            if (NetworkConstant.viewDicVer == null) {
                params.put("viewVer", 0);
            } else {
                params.put("viewVer", NetworkConstant.viewDicVer);
            }
            if (NetworkConstant.screenVer == null) {
                params.put("screenVer", 0);
            } else {
                params.put("screenVer", NetworkConstant.screenVer);
            }
            if (deviceModel != null && deviceModel.length() != 0) {
                params.put("deviceModel", deviceModel);
            }
            if (carrier != null && carrier.length() != 0) {
                params.put("carrier", carrier);
            }

            StringEntity entity = new StringEntity(params.toString());


            clientSdk.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL, entity,"application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(false, e);
                    Log.i("setSDKInfo", "cKey : " + cKey + " aKey : " + apiKey + " uuid : " + uuid + " bundleId : " + bundleId);
                    Log.e("sdkServerSetSDK", "Fail: " + e.toString());
                    Log.d("sdkServerSetSDK", "StatusCode : " + statusCode);
                    /*Toast.makeText(getApplicationContext(), "Resquest Failed", Toast.LENGTH_SHORT).show();*/
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void logSession(String aKey, String sId, String devId, Boolean newSession, String os, JSONArray log, Integer sc, ArrayList<String> eventArr, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientLogSession = new AsyncHttpClient();

            clientLogSession.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientLogSession.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);
            clientLogSession.setTimeout(20000);

            JSONObject params = new JSONObject();
            params.put("aKey", aKey);
            params.put("new", newSession);
            params.put("sId", sId);
            params.put("devId", devId);
            params.put("os", os);
            params.put("log", log);
            params.put("sessionCount", sc);

            if (eventArr != null && eventArr.size() > 0) {
                JSONArray eventJS = new JSONArray(eventArr);
                params.put("currentEvents", eventJS);
            }

            StringEntity entity = new StringEntity(params.toString());

            clientLogSession.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_LOGSESSION, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.e("sdkServerLogSession", "Fail: " + e.toString());
                    Log.d("sdkServerLogSession", "StatusCode : " + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logScreenShot(String aKey, String devId, String base64, String viewName, final CallbackInterface callback) throws UnsupportedEncodingException{
        try {
            AsyncHttpClient clientLogScreenShot = new AsyncHttpClient();

            clientLogScreenShot.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientLogScreenShot.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("devId", devId);
            params.put("aKey", aKey);
            params.put("os", "android");
            params.put("base64", base64);
            params.put("viewName" ,viewName);

            StringEntity entity = new StringEntity(params.toString());

            clientLogScreenShot.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_LOGSCREENSHOT, entity, "application/json", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.e("clientLogScreenShot", "Fail: " + e.toString());
                    Log.d("clientLogScreenShot", "StatusCode : " + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setClientUserId(String userId, String aKey, final CallbackInterface callback) {
        AsyncHttpClient clientUserId = new AsyncHttpClient();

        clientUserId.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
        clientUserId.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("devId", NetworkConstant.devId);
        params.put("aKey", aKey);

        clientUserId.post(NetworkConstant.SERVER_URL + METHINKS_SDK_URL_SETUSERID, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                callback.onDownloadSuccess(true, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("sdkServerClientUserId", "Fail: " + e.toString());
                Log.d("sdkServerClientUserId", "StatusCode :" + statusCode);
            }
        });
    }

    public void reportForDirtyLog(String devId, String aKey, final CallbackInterface callback) {
        AsyncHttpClient clientDirtyLog = new AsyncHttpClient();

        clientDirtyLog.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
        clientDirtyLog.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

        RequestParams params = new RequestParams();
        params.put("aKey", aKey);
        params.put("devId", devId);

        clientDirtyLog.post(NetworkConstant.SERVER_URL + METHINKS_SDK_URL_REPORTFORDIRTYLOG, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                callback.onDownloadSuccess(true, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("reportForDirtyLog", "Fail: " + e.toString());
                Log.d("reportForDirtyLog", "StatusCode :" + statusCode);
            }
        });
    }

    public void logUserAttributes(String devId, String aKey, JSONObject attributes, final CallbackInterface callback) throws  UnsupportedEncodingException{
        try {
            AsyncHttpClient attributesClient = new AsyncHttpClient();

            attributesClient.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            attributesClient.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("devId", devId);
            params.put("aKey", aKey);
            params.put("os", "android");
            params.put("attributes", attributes);

            StringEntity entity = new StringEntity(params.toString());

            attributesClient.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_ATTRIBUTES, entity, "application/json", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);
                    Log.e("logUserAttributes", "Fail: " + e.toString());
                    Log.d("logUserAttributes", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setLogEvent(String aKey, String devId, Double ts, String eventName, String type, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientLogEvent = new AsyncHttpClient();

            clientLogEvent.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientLogEvent.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("aKey", aKey);
            params.put("devId", devId);
            params.put("ts", ts);
            params.put("name", eventName);
            params.put("type", type);
            params.put("os", "android");

            StringEntity entity = new StringEntity(params.toString());

            clientLogEvent.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_LOGEVNET, entity, "application/json", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);
                    Log.e("setLogEvent", "Fail: " + e.toString());
                    Log.d("setLogEvent", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void getMySurverys(String cKey, String aKey, String devId, HashMap<String, ArrayList<Long>> eventMap, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientGetSurvey = new AsyncHttpClient();

            clientGetSurvey.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientGetSurvey.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

        /*clientGetSurvey.setTimeout(20000);
        clientGetSurvey.setConnectTimeout(60000);
        clientGetSurvey.setResponseTimeout(60000);*/

        /*RequestParams params = new RequestParams();
        params.put("aKey", aKey);
        params.put("devId", devId);
        params.put("os", "android");
        if (eventMap != null && eventMap.size() != 0) {
            JSONObject events = new JSONObject(eventMap);
            params.put("events", events);
        }*/

            JSONObject params = new JSONObject();
            params.put("cKey", cKey);
            params.put("aKey", aKey);
            params.put("devId", devId);
            params.put("os", "android");
            if (eventMap != null && eventMap.size() != 0) {
                JSONObject events = new JSONObject(eventMap);
                params.put("events", events);
            }
            StringEntity entity = new StringEntity(params.toString());

            clientGetSurvey.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_GETSURVEYS, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);

                    Log.e("clientGetSurvey", "Fail: " + e.toString());
                    Log.d("clientGetSurvey", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void inAppSurveyCompletion(String cKey, String aKey, String devId, String packId, JSONObject answer, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientInAppComplete = new AsyncHttpClient();

            clientInAppComplete.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientInAppComplete.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("cKey", cKey);
            params.put("aKey", aKey);
            params.put("devId", devId);
            params.put("packId", packId);
            params.put("os", "android");
            params.put("answers", answer);

            StringEntity entity = new StringEntity(params.toString());

            clientInAppComplete.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_INAPPCOMPLETE, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);

                    Log.e("inAppSurveyCompletion", "Fail: " + e.toString());
                    Log.d("inAppSurveyCompletion", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getInAppSurveySections(String aKey, String devId, String packId, final CallbackInterfaceQ callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientInAppSurvSec = new AsyncHttpClient();

            clientInAppSurvSec.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientInAppSurvSec.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("aKey", aKey);
            params.put("devId", devId);
            params.put("packId", packId);
            params.put("os", "android");

            StringEntity entity = new StringEntity(params.toString());

            clientInAppSurvSec.post(this, NetworkConstant.SERVER_URL + METHINKS_SDK_URL_GETINAPPSURVEY, entity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONParser parser = new JSONParser();
                    org.json.simple.JSONObject responseSimple = null;
                    try {
                        Object obj = parser.parse(response.toString());
                        responseSimple = (org.json.simple.JSONObject) obj;
                        callback.onDownloadSuccess(true, responseSimple);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);

                    Log.e("getInAppSurveySections", "Fail: " + e.toString());
                    Log.d("getInAppSurveySections", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSurveyQuestion(String aKey, String surveyId, final CallbackInterface callback) throws UnsupportedEncodingException{
        try {
            AsyncHttpClient clientQuestion = new AsyncHttpClient();

            clientQuestion.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientQuestion.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("aKey", aKey);
            params.put("surveyId", surveyId);

            StringEntity entity = new StringEntity(params.toString());

            clientQuestion.post(this, METHINKS_SDK_URL_SURVEYQUESTION, entity, "application/json", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);

                    Log.e("getSurveyQuestion", "Fail: " + e.toString());
                    Log.d("getSurveyQuestion", "StatusCode :" + statusCode);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void suveryCompletion(String aKey, String devId, String surveyId, JSONObject answer, final CallbackInterface callback) throws UnsupportedEncodingException {
        try {
            AsyncHttpClient clientSurvComp = new AsyncHttpClient();

            clientSurvComp.addHeader("X-Parse-Application-Id", NetworkConstant.AppId);
            clientSurvComp.addHeader("X-Parse-REST-API-Key", NetworkConstant.RestAPIKey);

            JSONObject params = new JSONObject();
            params.put("aKey", aKey);
            params.put("devId", devId);
            params.put("os", "android");
            params.put("surveyId", surveyId);
            if (answer != null && answer.length() != 0) {
                params.put("answers", answer);
            }

            StringEntity entity = new StringEntity(params.toString());

            clientSurvComp.post(this, METHINKS_SDK_URL_COMPLETE, entity, "application/json", new JsonHttpResponseHandler(){
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    callback.onDownloadSuccess(true, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    callback.onDownloadFail(true, e);

                    Log.e("suveryCompletion", "Fail: " + e.toString());
                    Log.d("suveryCompletion", "StatusCode :" + statusCode);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void getCampaignItemId(String userId, String campaignId, final CallbackInterface callback) throws UnsupportedEncodingException {
        AsyncHttpClient clientCampaign = new AsyncHttpClient();

        clientCampaign.addHeader("X-Parse-Application-Id", "myAppId");
        clientCampaign.addHeader("X-Parse-REST-API-Key",NetworkConstant.RestAPIKey);
        clientCampaign.addHeader("X-Parse-Session-Token", "r:8636db71a7d1e8a9f9da6b556367acda");

        org.json.simple.JSONArray campaignItem= new org.json.simple.JSONArray();
        campaignItem.add(campaignId);

        org.json.simple.JSONObject params = new org.json.simple.JSONObject();
        params.put("id", userId);
        params.put("campaignIds", campaignItem);

        StringEntity entity = new StringEntity(params.toString());




        /*RequestParams campaignItemParams = new RequestParams();
        campaignItemParams.put("id", "FVxQIYweVK");
        campaignItemParams.put("campaignIds",campaignItem);

        HashMap<String, Object> params = new HashMap<>();
        params.put("id", "FVxQIYweVK");
        params.put("campaignIds", campaignItem);*/


        clientCampaign.post(this, METHINKS_URL_CAMPAIGNITEM, entity, "application/json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                callback.onDownloadSuccess(true, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("Clima", "Fail " + e.toString());
                Log.d("Clima", "Status code" + statusCode);
                Toast.makeText(getApplicationContext(), "Resquest Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void getCampaignSumItem(String userId, String CampaignId, final CallbackInterface callback) throws UnsupportedEncodingException {
        AsyncHttpClient clientCampaign = new AsyncHttpClient();

        clientCampaign.addHeader("X-Parse-Application-Id", "myAppId");
        clientCampaign.addHeader("X-Parse-REST-API-Key",NetworkConstant.RestAPIKey);
        clientCampaign.addHeader("X-Parse-Session-Token", "r:8636db71a7d1e8a9f9da6b556367acda");


        RequestParams campaignItemParams = new RequestParams();
        campaignItemParams.put("id", userId);
        campaignItemParams.put("campaignItemId",CampaignId);


        clientCampaign.post( METHINKS_URL_CAMPAIGNSUBITEM, campaignItemParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                callback.onDownloadSuccess(true, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.e("Clima", "Fail " + e.toString());
                Log.d("Clima", "Status code" + statusCode);
            }
        });

    }



    private void setCampaignSubItemId(String campaignItemId) {
        this.campaignItemId = campaignItemId;
    }

    private String getCampaignSubItemId() {
        return this.campaignItemId;
    }

}
