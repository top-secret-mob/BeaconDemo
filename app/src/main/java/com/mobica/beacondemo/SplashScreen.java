package com.mobica.beacondemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.mobica.beacondemo.registration.RegistrationProvider;

import javax.inject.Inject;

public class SplashScreen extends AppCompatActivity implements RegistrationProvider.RegistrationProviderListener {
    private ProgressBar progressBar;

    @Inject
    RegistrationProvider registrationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BeaconApplication.getGraph().inject(this);

        setContentView(R.layout.activity_spash_screen);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registrationProvider.login(this);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        registrationProvider.cancel();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onOperationSucceeded() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onOperationFailed(String error) {
        new AlertDialog.Builder(this)
                .setTitle("Registration failed")
                .setMessage(error)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }
}
