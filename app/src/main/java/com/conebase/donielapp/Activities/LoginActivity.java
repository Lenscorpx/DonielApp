package com.conebase.donielapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.conebase.donielapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText userMail, userPwd;
    private Button btnLogin;
    private ProgressBar loginProgress;
    private FirebaseAuth mAuth;
    private Intent HomeActivity;
    private ImageView loginPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail=findViewById(R.id.edt_mail);
        userPwd=findViewById(R.id.edt_paswd);

        btnLogin=findViewById(R.id.btn_connect);
        loginProgress=findViewById(R.id.login_progress);
        mAuth=FirebaseAuth.getInstance();
        HomeActivity= new Intent(this,com.conebase.donielapp.Activities.HomeActivity.class);
        loginPhoto=findViewById(R.id.imgV);
        loginPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent registerActivity = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(registerActivity);
                finish();
            }
        });

        loginProgress.setVisibility(View.INVISIBLE);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginProgress.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPwd.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    showMessage("Veuillez verifier tous les champs!");
                }
                else
                {
                    signIn(mail, password);
                }
            }
        });
}

    private void signIn(String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    loginProgress.setVisibility((View.INVISIBLE));
                    btnLogin.setVisibility(View.VISIBLE);
                    updateUI();
                }
                else
                {
                    showMessage((task.getException().getMessage().toString()));
                }
            }
        });
    }

    private void updateUI() {

        startActivity(HomeActivity);
        finish();
    }

    private void showMessage(String s) {

        Toast.makeText(getApplicationContext(),s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();

        if(user!= null){
            updateUI();
        }
    }
}
