package com.example.wearme;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.wearme.model.User;

public class fragment_signup extends Fragment {

    View view;

    TextView tvRegResult;
    EditText etFullName, etEmail, etPhoneNumber, etPassword;
    Button btnJoin;
    ProgressBar pbRegistration;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Create Firebase auth instance:
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_signup, container, false);

        tvRegResult = view.findViewById(R.id.tvRegResult);
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etPassword = view.findViewById(R.id.etPassword);
        btnJoin = view.findViewById(R.id.signupRegisterButton);
        pbRegistration = view.findViewById(R.id.pbRegistration);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String phoneNumber = etPhoneNumber.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    etFullName.setError("Name is required !");
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required !");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    etPhoneNumber.setError("Phone Number is required !");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required !");
                    return;
                }

                pbRegistration.setVisibility(View.VISIBLE);

                // Register user in firebase
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Authentication", "User Registered");
                            User newUser = new User(name, email, phoneNumber);
                            db.collection("users")
                                    .document(email)
                                    .set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("user-db", "user inserted to db");
                                    Toast.makeText(getActivity(), "WELCOME TO OUR FAMILY", Toast.LENGTH_SHORT).show();
                                    pbRegistration.setVisibility(View.INVISIBLE);
                                    Log.d("Authentication", "RegisterWithEmail:success");
                                    Navigation.findNavController(view).navigate(R.id.editPersonalDetailsFragment);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("user-db", "user failed insertion to db");
                                    tvRegResult.setText("Something went wrong");
                                    Log.d("Authentication", "User Bad Reg");
                                    pbRegistration.setVisibility(View.INVISIBLE);
                                }
                            });
                        } else {
                            tvRegResult.setText("Something went wrong");
                            Log.d("Authentication", "User Bad Reg");
                            pbRegistration.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        });

        return  view;
    }
}