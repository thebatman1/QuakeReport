package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

/**
 * Created by HP on 5/25/2017.
 */

public class EarthquakeLoader extends AsyncTaskLoader<ArrayList<Information>> {


    private String mURL;

    public EarthquakeLoader(Context context , String url) {
        super(context);

        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<Information> loadInBackground() {
        return mURL==null?null:QuakeUtils.extractEarthquakes(mURL);
    }
}
