package com.csi.csi_organiser;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class GSignin extends AppCompatActivity {
    private SignInButton mGoogleBtn;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 2;
    private GoogleApiClient mGoogleApiClient;
    FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList<Model> memlist;
    HashMap<String,String> users;
    DatabaseReference firebase;
    ValueEventListener ve;
    public static final String  TAG = "Main Activity";
    String personEmail2;
    SQLiteHelper db;
    String personEmail="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        db= new SQLiteHelper(this);
        users=db.getAllValues();
        if(getIntent().getBooleanExtra("EXIT",false))
        {
            finish();
        }
        else if(!users.isEmpty())
        {
            Toast.makeText(GSignin.this,"There is a current User!",Toast.LENGTH_LONG).show();
            if(users.get("priority").matches("1"))
            {
                Intent intent= new Intent(GSignin.this,CoreActivity.class);
                startActivity(intent);

            }
            else if(users.get("priority").matches("0"))
            {
                Intent intent= new Intent(GSignin.this,Members.class);
                startActivity(intent);
            }
            else
            {

                Intent intent =new Intent(GSignin.this,JcActivity.class);
                startActivity(intent);
            }
        }
        firebase= FirebaseDatabase.getInstance().getReference("CSI Members");
        memlist=new ArrayList<>();
        mGoogleBtn = (SignInButton) findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){

                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(GSignin.this,"You got an error",Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }

        });
    }



    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct.getEmail()!=null) {
                personEmail = acct.getEmail();
            }
            personEmail2 = personEmail;
///////////////////////////



            if(!personEmail.isEmpty() && !memlist.isEmpty()){
                Intent intent;
                boolean k = true;
                for (int i = 0; i < memlist.size(); i++) {
                    if (memlist.get(i).getEmail().matches(personEmail)) {
                        k = false;
                        db.addInfo(memlist.get(i).getCurrenttask(),memlist.get(i).getName(), memlist.get(i).getEmail(),
                                memlist.get(i).getNumber(),memlist.get(i).getNeareststation(),memlist.get(i).getTeamtask(),
                                memlist.get(i).getPreference1(), memlist.get(i).getPreference2(),memlist.get(i).getPreference3(),
                                memlist.get(i).getPriority(),memlist.get(i).getRollno(),memlist.get(i).getId());

                        if (memlist.get(i).getPriority().matches("0")) {
                            intent = new Intent(GSignin.this, Members.class);
                        } else if(memlist.get(i).getPriority().matches("1"))
                            {
                                intent = new Intent(GSignin.this, CoreActivity.class);
                        }
                        else
                        {
                            intent = new Intent(GSignin.this, JcActivity.class);
                        }
                        startActivity(intent);
                        break;
                    }
                }
                if (k) {
                    intent = new Intent(GSignin.this, HomeActivity.class);
                    intent.putExtra("email", personEmail);
                    startActivity(intent);
                }
            }
            //////////////////////////////
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(GSignin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });

    }
    protected void onStart() {
        super.onStart();
       firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                memlist.clear();
                for (DataSnapshot fire : dataSnapshot.getChildren()) {
                    Model model = fire.getValue(Model.class);
                    model.setId(fire.getKey());
                    memlist.add(model);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mAuth.addAuthStateListener(mAuthListener);

    }

}

/*

*/