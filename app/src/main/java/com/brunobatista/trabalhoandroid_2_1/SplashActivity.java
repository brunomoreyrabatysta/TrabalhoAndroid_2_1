package com.brunobatista.trabalhoandroid_2_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends Activity {

    private FirebaseAuth firebaseAuth;
    private ProgressBar pgbBarraProgresso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuthSignleton.getInstance();
        pgbBarraProgresso = findViewById(R.id.pgbBarraProgresso);
        pgbBarraProgresso.getProgressDrawable().setColorFilter(
                Color.BLUE, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    @Override
    protected void onStart() {
        super.onStart();

        setProgressValue(0);
        NavegacaoTela();
    }

    private void NavegacaoTela() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    System.out.println("Firebase Auth.: " + currentUser.getEmail());
                    startActivity(new Intent(SplashActivity.this, PrincipalActivity.class));
                } else {
                    System.out.println("Firebase Auth.: Não logado");
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }

                finish();
            }
        }, 5000);
    }

    private void setProgressValue(final int progress) {

        // set the progress
        pgbBarraProgresso.setProgress(progress);
        // thread is used to change the progress value
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setProgressValue(progress + 10);
            }
        });
        thread.start();
    }
}