package com.harsh.stockmarkettests;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FirebaseHandler {

    public static FirebaseAuth mAuth;
    public static FirebaseDatabase myDatabase;

    Context ctx;
    Activity activity;

    public FirebaseHandler(Context ctx, FirebaseAuth firebaseAuth, Activity activity)
    {
        mAuth = firebaseAuth;
        this.ctx = ctx;
        this.activity = activity;
        myDatabase = FirebaseDatabase.getInstance();
    }

    public void login(String email, String pass)
    {
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    ctx.startActivity(new Intent(ctx, MainActivity2.class));
                    activity.finish();
                }
                else
                    Toast.makeText(ctx, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signup(String email, String pass)
    {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    String val = mAuth.getUid();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("Users").child(val);
                    UserDataModel userDataModel = new UserDataModel(email, pass, "", "", 0f);
                    myRef.setValue(userDataModel);
                    Toast.makeText(ctx, "Account Created", Toast.LENGTH_SHORT).show();
                    login(email, pass);
                }
                else
                {
                    Toast.makeText(ctx, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Get Methods
    public List<stock> getWatchListFromFirebase()
    {
        List<stock> stockList = new ArrayList<>();
        FirebaseDatabase myDatabase = FirebaseDatabase.getInstance();
        String val = FirebaseHandler.mAuth.getUid();
        DatabaseReference databaseReference = myDatabase.getReference("Users").child(val).child("watchlist");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String value = snapshot.getValue(String.class);
                try {
                    JSONArray jsonArray = new JSONArray(value);
                    for(int i=0; i < jsonArray.length(); i++)
                    {
                        stock st = new stock("-", jsonArray.get(i).toString(), "-");
                        stockList.add(st);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ctx, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return stockList;
    }
}
