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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.example.wearme.adapters.CurrUserAdapter;
import com.example.wearme.model.Model;

public class EditPersonalDetailsFragment extends Fragment {

    RecyclerView recyclerView;
    CurrUserAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    com.example.wearme.ItemListViewModel viewModel;
    ProgressBar pb;

    Button btnLogout;
    Button btnUpdate;
    TextView accountNameHeader;

    EditText etAccountName;
    EditText etAccountEmail;
    EditText etAccountPhone;

    Boolean isPasswordUpdated;
    Boolean isUpdateDBUser;
    Boolean isPasswordChangeWanted;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int i = viewHolder.getAdapterPosition();
            String id = viewModel.getListByCurrOwner().get(i).getId();
            EditPersonalDetailsFragmentDirections.ActionEditPersonalDetailsFragmentToItemDetailsFragment direction = EditPersonalDetailsFragmentDirections.actionEditPersonalDetailsFragmentToItemDetailsFragment(id);
            Log.e("TAG", "item id chosen: " + id);
            Navigation.findNavController(view).navigate(direction);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_personal_details, container, false);
        viewModel = new ViewModelProvider(this).get(com.example.wearme.ItemListViewModel.class);

        btnLogout = view.findViewById(R.id.btnSignOut);
        btnUpdate = view.findViewById(R.id.btnUpdate);
        accountNameHeader = view.findViewById(R.id.tvAccountName);

        etAccountName = view.findViewById(R.id.etAccountName);
        etAccountEmail = view.findViewById(R.id.etAccountEmail);
        etAccountPhone = view.findViewById(R.id.etAccountPhone);

        recyclerView = view.findViewById(R.id.rvAccountItems);
        recyclerView.setHasFixedSize(true);

        adapter = new CurrUserAdapter(viewModel);
        recyclerView.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this.getActivity(), 1, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(onItemClickListener);
        reloadData();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Check whether a user is not logged in:
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Log.d("Authentication", "wearme: User not logged in");
            Navigation.findNavController(view).navigate(R.id.fragment_signin);
        } else {
            db.collection("users")
                    .whereEqualTo("email", currentUser.getEmail())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("Authentication", "user-db: read user success" + document.getData());
                                    accountNameHeader.setText((String) document.get("name"));
                                    etAccountName.setText((String) document.get("name"));
                                    etAccountEmail.setText((String) document.get("email"));
                                    etAccountPhone.setText((String) document.get("phone"));
                                }
                            } else {
                                Log.w("Authentication", "user-db: read user failed");
                            }
                        }
                    });

            accountNameHeader.setText(currentUser.getDisplayName());

        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To sign out:
                mAuth.signOut();
                Log.d("Authentication","wearme: User signed out");
                Navigation.findNavController(view).navigate(R.id.fragment_signin);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();

                String name = etAccountName.getText().toString().trim();
                String phoneNumber = etAccountPhone.getText().toString().trim();

                isUpdateDBUser = false;
                isPasswordUpdated = false;
                isPasswordChangeWanted = false;

                if (TextUtils.isEmpty(name)) {
                    etAccountName.setError("Name is required !");
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    etAccountPhone.setError("Phone Number is required !");
                    return;
                }

                db.collection("users")
                        .document(currentUser.getEmail())
                        .update("name", name, "phone", phoneNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("user-update-db", "update user success");
                                    isUpdateDBUser = true;
                                    accountNameHeader.setText(name);

                                    Log.d("user-update-db", "isPasswordUpdated:" + isPasswordUpdated);
                                    Log.d("user-update-db", "isUpdateDBUser:" + isUpdateDBUser);

                                    if (isPasswordChangeWanted) {
                                        if (isPasswordUpdated) {
                                            Toast.makeText(getActivity(), "UPDATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "UPDATED SUCCESSFULLY", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.w("user-update-db", "update user failed");
                                    isUpdateDBUser = false;
                                    Toast.makeText(getActivity(), "update user failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    void reloadData() {
        Model.instance.refreshAllItems(new Model.GetAllItemsListener() {
            @Override
            public void onComplete() {
            }
        });
    }
}