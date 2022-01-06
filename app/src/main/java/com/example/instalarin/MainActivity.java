package com.example.instalarin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private ImageView iconImage;
    private Button registerButton;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        linearLayout = findViewById(R.id.linearLayout);
        iconImage = findViewById(R.id.icon_image);
        registerButton = findViewById(R.id.registerButton);
        logInButton = findViewById(R.id.loginButton);

        linearLayout.animate().alpha(0f).setDuration(1);


                TranslateAnimation animation = new TranslateAnimation(0,0,0,-1500);
                animation.setDuration(1000);
                animation.setFillAfter(false);
                animation.setAnimationListener(new MyAnimationListener());
                iconImage.setAnimation(animation);


    }

    public void setRegisterButton(View v){
        startActivity(new Intent(this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    public void setLogInButton(View v) {
        startActivity(new Intent(this, LogInActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    private class MyAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            iconImage.clearAnimation();
            iconImage.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
            finish();
        }
    }
}