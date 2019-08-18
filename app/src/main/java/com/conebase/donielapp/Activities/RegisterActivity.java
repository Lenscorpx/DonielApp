package com.conebase.donielapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.conebase.donielapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    ImageView userPhoto;
    static int PreqCode = 1;
    static int REQUESCODE = 1;
    Uri pickedImgUri;
    private EditText user_mail, user_pwd, user_cnf_pwd, user_name, user_phone;
    private ProgressBar progressBarr;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        user_mail=findViewById(R.id.reg_mail);
        user_pwd=findViewById(R.id.reg_pwd);
        user_cnf_pwd=findViewById(R.id.reg_confirm_pwd);
        user_name=findViewById(R.id.reg_nom);
        user_phone=findViewById(R.id.reg_numero);

        progressBarr = findViewById(R.id.progressBar);
        regBtn=findViewById(R.id.reg_btn_send);
        progressBarr.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                regBtn.setVisibility(View.INVISIBLE);
                progressBarr.setVisibility(View.VISIBLE);
                final String email = user_mail.getText().toString();
                final String passwd = user_pwd.getText().toString();
                final String telephone = user_phone.getText().toString();
                final String confirm_pwd = user_cnf_pwd.getText().toString();
                final String x_name = user_name.getText().toString();

                if(email.isEmpty() || x_name.isEmpty() || passwd.isEmpty() || telephone.isEmpty() || !passwd.equals(confirm_pwd)){


                    showMessage("Veuillez verifiez les informatioins saisies!");
                    regBtn.setVisibility(View.VISIBLE);
                    progressBarr.setVisibility(View.INVISIBLE);
                }
                else {
                    CreateUserAccount(email,x_name,passwd,telephone);
                }

            }
        });
        userPhoto = findViewById(R.id.imgv_user);
        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    OpenGallery();
                }
            }
        });
    }

    private void CreateUserAccount(String email, final String x_name, String passwd, String telephone) {

        mAuth.createUserWithEmailAndPassword(email,passwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Utilisateur bien enregistré! Merci");

                            misajourInfo(x_name,pickedImgUri,mAuth.getCurrentUser());
                        }
                        else
                        {
                            showMessage("Echec de creation de compte!"+ task.getException().getMessage());
                            Log.d("Erreur ",task.getException().getMessage());
                            regBtn.setVisibility(View.VISIBLE);
                            progressBarr.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void misajourInfo(final String x_name, Uri pickedImgUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImgUri.getLastPathSegment());
        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(x_name)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            showMessage("Modification faite avec succes");
                                            udpateUI();
                                        }
                                    }
                                });

                    }
                });
            }
        });
    }

    private void udpateUI() {

        Intent homeActivity = new Intent (getApplicationContext(), HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void showMessage(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void OpenGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUESCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==REQUESCODE && data !=null){
            pickedImgUri=data.getData();
            userPhoto.setImageURI(pickedImgUri);
        }

    }

    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Veuillez valider la permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE}, PreqCode);
            }
        } else {
            OpenGallery();
        }


    }
}

