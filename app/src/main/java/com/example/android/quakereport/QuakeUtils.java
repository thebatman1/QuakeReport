package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HP on 3/13/2017.
 */

public final class QuakeUtils {

    /**
     * Sample JSON response for a USGS query
     */

       /**
     * Create a private constructor because no one should ever create a {@link QuakeUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
    */
    private static final String LOG_TAG = QuakeUtils.class.getSimpleName();
    private static final int RESPONSE_OK = 200;

    /**
     * Return a list of {@link Information} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Information> extractEarthquakes(String requestUrl) {

        // Create an empty ArrayList that we can start adding earthquakes to
        URL url = createUrl(requestUrl);
        String jsonRespose = "";

        try {
            jsonRespose = makeHttpRequest(url);
        }catch (IOException e) {
            Log.e(LOG_TAG , "IOEXCEPTION" , e);
        }



        return getEarthquakesFromJSON(jsonRespose);
    }

    private static URL createUrl(String string) {
        URL url = null;
        try{
            url = new URL(string);
        }catch(MalformedURLException e) {
            Log.e(LOG_TAG , "MalformedURLException" , e);
        }
        return url;
    }

    // Return the list of earthquakes
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if(url==null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try{
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if(urlConnection.getResponseCode()==RESPONSE_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
            else {
                Log.e(LOG_TAG , "ERROR CODE : " + urlConnection.getResponseCode());
            }
        }catch(IOException e){
            Log.e(LOG_TAG , "Problem retrieving the JSON results " , e);
        }finally {
            if(urlConnection!=null) {
                urlConnection.disconnect();
            }
            if(inputStream!=null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }
    //Reads from the InputStream
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if(inputStream!=null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream , Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while(line!=null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //Parse the JSON and return the information
    private static ArrayList<Information> getEarthquakesFromJSON(String jsonResponse) {
        ArrayList<Information> earthquakes = new ArrayList<>();
        if(TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        try{
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray jsonArray = jsonObject.getJSONArray("features");

            for(int i=0 ; i<jsonArray.length() ; ++i) {
                JSONObject c = jsonArray.getJSONObject(i);
                JSONObject properties = c.getJSONObject("properties");

                String mag = properties.getString("mag");
                String place = properties.getString("place");
                String time = properties.getString("time");
                String url = properties.getString("url");

                long timeinms = Long.parseLong(time);
                Date date = new Date(timeinms);

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM DD, yyyy\nhh:mm");
                String displayDate = dateFormat.format(date);


                earthquakes.add(new Information(mag , place , displayDate , url));
            }
            return earthquakes;
        }catch(JSONException e) {
            Log.e(LOG_TAG , "Problem parsing JSON results" , e);
        }
        return null;
    }
}
