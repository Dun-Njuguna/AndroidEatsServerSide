package com.dunk.androideatsserverside;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dunk.androideatsserverside.Common.Common;
import com.dunk.androideatsserverside.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignIn extends AppCompatActivity {

    @BindView(R.id.edtPhone)
    MaterialEditText edtPhone;
    @BindView(R.id.edtPassword) MaterialEditText edtPassword;
    @BindView(R.id.btnSignin)
    Button btnSignIn;

    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        //initiating firebase
        db = FirebaseDatabase.getInstance();
        users = db.getReference("User");

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(edtPhone.getText().toString(),edtPassword.getText().toString());
            }
        });
    }

    private void signInUser(String phone, final String password) {
        final ProgressDialog mDIALOG = new ProgressDialog(SignIn.this);
        mDIALOG.setMessage("Signing in...");
        mDIALOG.show();

        final String localPhone = phone;
        final String localPassword = password;

        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(localPhone).exists()){
                    mDIALOG.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);
                    if (Boolean.parseBoolean(user.getIsStaff())){
                        if (user.getPassword().equals(password)) {
                            //login
                            Intent intent = new Intent(SignIn.this,Home.class);
                            Common.currentUser = user;
                            startActivity(intent);

                        }else
                            Toast.makeText(SignIn.this, "wrong password", Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(SignIn.this, "Please login with staff account", Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(SignIn.this, "User does not exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
