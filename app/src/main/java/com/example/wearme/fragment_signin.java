package com.example.wearme;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class fragment_signin extends Fragment {

    View view;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    TextView tvStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_signin, container, false);
        TextView registerBtn = view.findViewById(R.id.tvRegister);
        tvStatus = view.findViewById(R.id.tv_status);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_fragment_signin_to_fragment_signup);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText etEmail = view.findViewById(R.id.et_signin_email);
        EditText etPassword = view.findViewById(R.id.et_signin_password);
        Button loginBtn = view.findViewById(R.id.signinLoginButton);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required !");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required !");
                    return;
                }

                // Sign in a user:
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Authentication", "signInWithEmail:success");
                            tvStatus.setText("Welcome!");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Navigation.findNavController(view).popBackStack();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Authentication", "signInWithEmail:failure", task.getException());
                            tvStatus.setText("Invalid user name or password");
                        }
                    }
                });
            }
        });
    }
}