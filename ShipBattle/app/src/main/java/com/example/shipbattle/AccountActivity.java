package com.example.shipbattle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class AccountActivity extends AppCompatActivity {
    TextView name, id, email;
    ImageView photo, editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        photo = findViewById(R.id.imageViewPhoto);
        editor = findViewById(R.id.imageViewEdit);
        name = findViewById(R.id.textViewName);
        email = findViewById(R.id.textViewEmail);
        id = findViewById(R.id.textViewId);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String personName = user.getDisplayName();
            String personEmail = user.getEmail();
            String personId = user.getUid();
            Uri personPhoto = user.getPhotoUrl();

            name.setText(personName);
            email.setText(personEmail);
            id.setText(personId);
            Glide.with(this).load(String.valueOf(personPhoto)).into(photo);
        }

        editor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertEdit = new AlertDialog.Builder(AccountActivity.this);
                alertEdit.setTitle(R.string.Name);
                alertEdit.setMessage(R.string.MsgEdit);
                EditText msgEdit = new EditText(AccountActivity.this);
                alertEdit.setView(msgEdit);
                alertEdit.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enterName = msgEdit.getText().toString();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(enterName)
                                .build();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            name.setText(enterName);
                                            Log.d("Updated profile", "User profile updated.");
                                        }
                                    }
                                });
                    }
                });
                alertEdit.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertEdit.show();
            }
        });

    }
}