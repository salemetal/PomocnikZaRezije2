package com.sale.pomocnikzarezije;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.drive.Drive;
import com.sale.pomocnikzarezije.db.AndroidDatabaseManager;
import com.sale.pomocnikzarezije.db.DBHandler;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

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
            case R.id.menu_backup:
                confirmBackupGoogleDrive(googleApiClient, this.getApplicationContext());
                return true;
            case R.id.menu_restore:
                confirmRestoreGoogleDrive(googleApiClient);
                return true;
            case R.id.menu_reset:
                confirmResetDb(this.getApplicationContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

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

    private void confirmRestoreGoogleDrive(final GoogleApiClient googleApiClient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        try {
            builder
                    .setMessage(R.string.want_bckp_google_drive)
                    .setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            restoreFromGoogleDrive(googleApiClient);
                        }
                    })
                    .setNegativeButton(R.string.ne, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void confirmBackupGoogleDrive(final GoogleApiClient googleApiClient, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        try {
            builder
                    .setMessage(R.string.want_backup_google_drive)
                    .setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            backuptoGoogleDrive(googleApiClient, context);
                        }
                    })
                    .setNegativeButton(R.string.ne, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void confirmResetDb(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        try {
            builder
                    .setMessage("želite li stvarno obrisati sve podatke iz baze podataka?")
                    .setPositiveButton(R.string.da, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            resetDb(context);

                        }
                    })
                    .setNegativeButton(R.string.ne, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void resetDb(Context context) {
        try
        {
            DBHandler dbHandler = new DBHandler(context);
            dbHandler.resetDb();
            refresh();
            Toast.makeText(this.getApplicationContext(), R.string.db_reset, Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.e("Bckp GD error: ", e.getMessage());
        }
    }


    private void restoreFromGoogleDrive(GoogleApiClient googleApiClient) {
        try
        {
            Backup backup = new Backup();
            backup.restoreFromGoogleDrive(googleApiClient, this.getApplicationContext());
            refresh();
        }
        catch (Exception e)
        {
            Log.e("Bckp GD error: ", e.getMessage());
        }

    }

    private void backuptoGoogleDrive(GoogleApiClient googleApiClient, Context context) {
        try
        {
            Backup backup = new Backup();
            backup.backupDBToGoogleDrive(googleApiClient, context);
        }
        catch (Exception e)
        {
            Log.e("Bckp GD error: ", e.getMessage());
        }

    }

    private void refresh()
    {
        finish();
        startActivity(getIntent());
    }
}