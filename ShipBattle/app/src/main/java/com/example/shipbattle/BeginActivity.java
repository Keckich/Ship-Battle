package com.example.shipbattle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class BeginActivity extends AppCompatActivity {
    Button btnAccount, sign_out, btnCreate, btnFind;
    GoogleSignInClient mGoogleSignInClient;
    EditText editFind;
    TextView textViewLobbyId;
    ImageView imageViewCopy;
    ArrayList<Lobby> lobbies;
    ArrayList<DatabaseReference> lobbyReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);
        lobbies = new ArrayList<>();
        lobbyReferences = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("Lobby");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                lobbies.add(snapshot.getValue(Lobby.class));
                lobbyReferences.add(snapshot.getRef());
                Log.i("added", "da");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addChildEventListener(childEventListener);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //Lobby lobby = (lobbies.get(lobbies.size() - 1));

                Log.e("inDataChanged", "na meste");
                String opponentId = snapshot.getValue(String.class);
                String creatorId = snapshot.getRef().getParent().child("idCreator").toString();
                String lobbyId = snapshot.getRef().getParent().getKey();
                //Log.e("lobby != null", "lobby!=null");
                if (opponentId != null) {
                    Log.e("oppoentId!=null", opponentId);
                    Intent intent = new Intent(BeginActivity.this, GameActivity.class);
                    intent.putExtra("opponentId", opponentId);
                    intent.putExtra("creatorId", creatorId);
                    intent.putExtra("lobbyId", lobbyId);

                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        btnCreate = findViewById(R.id.buttonCreate);
        textViewLobbyId = findViewById(R.id.textViewLobbyId);
        imageViewCopy = findViewById(R.id.imageViewCopy);
        imageViewCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) BeginActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("tag_output", textViewLobbyId.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(BeginActivity.this, "Text copied successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Lobby").push();
                String lobbyId = myRef.getKey();
                Lobby lobby = new Lobby(lobbyId, user.getUid());
                myRef.setValue(lobby).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        myRef.child("idOpponent").addValueEventListener(valueEventListener);
                    }
                });
                textViewLobbyId.setText(lobbyId);
                imageViewCopy.setVisibility(View.VISIBLE);
                //databaseReference.addValueEventListener(valueEventListener);
            }
        });
        btnFind = findViewById(R.id.buttonFind);
        editFind = findViewById(R.id.editTextFind);
        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String editFindText = editFind.getText().toString();
                Log.i("edittext", editFindText);
                Log.i("size", String.valueOf(lobbies.size()));
                for (int i = 0; i < lobbies.size(); i++) {
                    Log.i("lobbyId", lobbies.get(i).getId());
                    if (lobbies.get(i).getId().equals(editFindText)) {

                        lobbies.get(i).setIdOpponent(user.getUid());
                        lobbyReferences.get(i).setValue(lobbies.get(i));
                        Intent intent = new Intent(BeginActivity.this, GameActivity.class);
                        intent.putExtra("opponentId", lobbies.get(i).getIdOpponent());
                        intent.putExtra("creatorId", lobbies.get(i).getIdCreator());
                        intent.putExtra("lobbyId", lobbies.get(i).getId());
                        startActivity(intent);
                        break;
                    }
                }

            }
        });
        btnAccount = findViewById(R.id.buttonAccount);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BeginActivity.this, AccountActivity.class);
                startActivity(intent);
            }
        });
        sign_out = findViewById(R.id.button_sign_out);
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(BeginActivity.this, "Sign out successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }

}