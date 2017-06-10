/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Information>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private ListView earthquakeListView;
    private InformationAdapter adapter;
    private TextView emptyText;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);
        emptyText = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(emptyText);
        progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        //progressBar.setIndeterminate(true);

        ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkActive = cm.getActiveNetworkInfo();

        if (!(networkActive!=null
                && networkActive.isConnectedOrConnecting())) {
            progressBar.setVisibility(View.GONE);
            emptyText.setText(R.string.no_internet_conncection);
        }

        else {
            getLoaderManager().initLoader(0 , null , this);
        }


        // Create a new adapter that takes an empty list of earthquakes as input
        adapter = new InformationAdapter(EarthquakeActivity.this, new ArrayList<Information>());
        earthquakeListView.setAdapter(adapter);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(adapter.getItem(position).getUrl()));
                startActivity(intent);
            }
        });
    }

    private void updateUi(final ArrayList<Information> earthquakes) {

        //Empty text to show no data waas received
        emptyText.setText(R.string.no_earthquakes);

        // Create a new {@link ArrayAdapter} of earthquakes
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        adapter.clear();
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
                adapter.addAll(earthquakes);
                adapter.notifyDataSetChanged();
        }

    }

    @Override
    public Loader<ArrayList<Information>> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPreferences.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitde_default)
        );

        String orderBy = sharedPreferences.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder builder = baseUri.buildUpon();

        builder.appendQueryParameter("format" , "geojson");
        builder.appendQueryParameter("limit" , "10");
        builder.appendQueryParameter("minmag" , minMagnitude);
        builder.appendQueryParameter("orderby" , orderBy);

        return new EarthquakeLoader(this , builder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Information>> loader, ArrayList<Information> data) {
        progressBar.setVisibility(View.GONE);
        updateUi(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Information>> loader) {
        adapter.clear();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
