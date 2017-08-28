package com.csi.csi_organiser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Members extends AppCompatActivity {
    private EditText mReasonBox;
    private TextView mTaskDesc;
    private Button mYesBtn;
    private Button mNoBtn;
    private Button mSubmitBtn;
    Toolbar toolbar;
    SQLiteHelper db;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        mReasonBox = (EditText) findViewById(R.id.reasonBox);
        mTaskDesc = (TextView) findViewById(R.id.taskDesc);
        mYesBtn= (Button) findViewById(R.id.yesBtn);
        db=new SQLiteHelper(this);
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("TASK MANAGER");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        db = new SQLiteHelper(this);
        mNoBtn = (Button) findViewById(R.id.noBtn);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Task");
        mDatabase= FirebaseDatabase.getInstance().getReference();
       /* mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTaskDesc.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });*/
      Toast.makeText(Members.this,db.getAllValues().get("name"),Toast.LENGTH_SHORT).show();
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase.child("Reason").setValue(mReasonBox.getText().toString().trim());
            }
        });
        mNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReasonBox.setVisibility(View.VISIBLE);
                mSubmitBtn.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Members.this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }
}
