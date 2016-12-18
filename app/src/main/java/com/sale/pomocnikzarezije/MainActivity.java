package com.sale.pomocnikzarezije;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.sale.pomocnikzarezije.db.AndroidDatabaseManager;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    DriveId folderId;
    private GoogleApiClient googleApiClient;
    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.kategorije));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Mjesečno));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.Godišnje));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final AdapterPager adapter = new AdapterPager
                (getSupportFragmentManager(), tabLayout.getTabCount());

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        buildGoogleApiClient();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.db:
                Intent intent = new Intent(this, AndroidDatabaseManager.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

            /*MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(getString(R.string.app_name)).build();
            Drive.DriveApi.getRootFolder(googleApiClient)
                    .createFolder(googleApiClient, changeSet)
                    .setResultCallback(folderCreatedCallback);*/

        createFolderApiDrive();
    }

    /*ResultCallback<DriveFolder.DriveFolderResult> folderCreatedCallback = new ResultCallback<DriveFolder.DriveFolderResult>() {
        @Override
        public void onResult(DriveFolder.DriveFolderResult result) {
            if (!result.getStatus().isSuccess()) {
                Toast.makeText(getApplicationContext(), R.string.create_folder_error, Toast.LENGTH_LONG).show();
                return;
            }
            folderId = result.getDriveFolder().getDriveId();
            Toast.makeText(getApplicationContext(), R.string.create_folder_success, Toast.LENGTH_LONG).show();

            //back folder created, save info to settings


        }
    };*/


    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
        Log.e("Connected?", String.valueOf(googleApiClient.isConnected()));
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("Connection suspened", "Connection suspended");
    }

    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(getString(R.string.conn_failed), String.valueOf(googleApiClient.isConnected()));
    }*/

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    googleApiClient.connect();
                }
                break;
        }
    }

    private void createFolderApiDrive() {
        Query query = new Query.Builder()
                .addFilter(Filters.and(Filters.eq(
                        SearchableField.TITLE, getString(R.string.app_name)),
                        Filters.eq(SearchableField.TRASHED, false)))
                .build();
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(DriveApi.MetadataBufferResult result) {
                        if (!result.getStatus().isSuccess()) {
                            Toast.makeText(getApplicationContext(), R.string.cant_create_folder, Toast.LENGTH_LONG).show();
                        } else {
                            boolean isFound = false;
                            for (Metadata m : result.getMetadataBuffer()) {
                                if (m.getTitle().equals(getString(R.string.app_name))) {
                                    Toast.makeText(getApplicationContext(), R.string.folder_exists, Toast.LENGTH_LONG).show();
                                    isFound = true;
                                    break;
                                }
                            }
                            if (!isFound) {
                                Toast.makeText(getApplicationContext(), R.string.creting_folder, Toast.LENGTH_LONG).show();
                                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                        .setTitle(getString(R.string.app_name))
                                        .build();
                                Drive.DriveApi.getRootFolder(googleApiClient)
                                        .createFolder(googleApiClient, changeSet)
                                        .setResultCallback(new ResultCallback<DriveFolder.DriveFolderResult>() {
                                            @Override
                                            public void onResult(DriveFolder.DriveFolderResult result) {
                                                if (!result.getStatus().isSuccess()) {
                                                    Toast.makeText(getApplicationContext(), R.string.error_creating_folder, Toast.LENGTH_LONG).show();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), R.string.bckp_folder_created, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }
}