package com.mobica.beacondemo;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.mobica.beacondemo.nfc.ItemTag;
import com.mobica.beacondemo.nfc.NdefReaderTask;
import com.mobica.beacondemo.settings.SettingsActivity;
import com.mobica.discoverysdk.nfc.NfcTag;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NdefReaderTask.NfcReaderListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    // Request code to attempt to resolve Google Play services connection failures.
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private NdefReaderTask nfcReader;
    private CoordinatorLayout snackbarPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.snackbarPosition = (CoordinatorLayout) findViewById(R.id.snackbarPosition);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkPlayServices();

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (nfcReader != null) {
            nfcReader.cancel(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_manage) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void handleIntent(Intent intent) {
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            nfcReader = new NdefReaderTask(this);
            nfcReader.execute(tag);
            // TODO: display progress bar
        }
    }

    @Override
    public void onNfcTagParsed(NfcTag tag) {
        if (!(tag instanceof ItemTag)) {
            return;
        }

        Snackbar.make(snackbarPosition, "Discovered item=" + ((ItemTag) tag).getItemId(), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onNfcTagParsingFailed(String error) {
        Log.e(TAG, "Failed to parse NFC tag: " + error);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, CONNECTION_FAILURE_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
