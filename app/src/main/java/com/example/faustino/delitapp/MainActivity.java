package com.example.faustino.delitapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements
        LogInFragment.OnFragmentInteractionListener,
        InformationFragment.OnFragmentInteractionListener,
        CreateAccountFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    FloatingActionButton FlAcBuAdd;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean close = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        FlAcBuAdd = (FloatingActionButton) findViewById(R.id.fab);
        FlAcBuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        R.string.NotRep, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ReportDELIT.class);
                startActivity(intent);
            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.My_frame_layout,
                                    InformationFragment.newInstance("", ""))
                            .addToBackStack(null)
                            .commit();
                    FlAcBuAdd.setVisibility(View.VISIBLE);
                    close = true;
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.My_frame_layout,
                                    LogInFragment.newInstance("", ""))
                            .commit();
                    FlAcBuAdd.setVisibility(View.GONE);
                }
                // ...

            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    public void onBackPressed() {
        if (close)
            finish();

        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLoginSuccess() {
        Toast.makeText(this, R.string.LogInNotification, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLoginFailed() {
        Toast.makeText(this, R.string.FailLogIn, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onCreateAccount() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.My_frame_layout,
                        CreateAccountFragment.newInstance("", ""))
                .addToBackStack("LogInFragment")
                .commit();
    }

    @Override
    public void onAccountSuccessfullyCreated() {
        Toast.makeText(this, R.string.RegNotification, Toast.LENGTH_SHORT).show();
    }

    public void logout() {
        if (mAuth != null) {
            mAuth.signOut();
            close = false;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
