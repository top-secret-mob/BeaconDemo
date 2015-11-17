package com.mobica.beacondemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.mobica.repositorysdk.RepositoryServiceAdapter;
import com.mobica.repositorysdk.utils.FluentExecutors;

import javax.inject.Inject;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private ListenableFuture<Void> loginFuture;

    @Inject
    RepositoryServiceAdapter repositoryService;

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
        loginFuture = repositoryService.login();
        Futures.addCallback(loginFuture, loginCallback, FluentExecutors.mainThreadExecutor());
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loginFuture != null && !loginFuture.isDone()) {
            loginFuture.cancel(true);
        }
        progressBar.setVisibility(View.GONE);
    }

    private final FutureCallback<Void> loginCallback = new FutureCallback<Void>() {
        @Override
        public void onSuccess(Void result) {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            finish();
        }

        @Override
        public void onFailure(Throwable t) {
            new AlertDialog.Builder(SplashScreen.this)
                    .setTitle("Registration failed")
                    .setMessage(t.getMessage())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .show();
        }
    };
}
