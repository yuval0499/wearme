package com.example.wearme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import com.example.wearme.model.Item;
import com.example.wearme.model.ModeLSql;
import com.example.wearme.model.Model;


public class ItemDetailsFragment extends Fragment {
    Item item;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);
        ProgressBar pb;
        ImageView avatarImageView = view.findViewById(R.id.item_image);
        TextView nameEt = view.findViewById(R.id.item_header);
        TextView sizeEt = view.findViewById(R.id.item_size);
        TextView priceEt = view.findViewById(R.id.item_price);
        Button btnContact = view.findViewById(R.id.contact_item_btn);
        TextView tvEmail = view.findViewById(R.id.tvDetailsEmail);
        Button btnDelete = view.findViewById(R.id.delete_item_btn);
        Button btnUpdate = view.findViewById(R.id.update_item_btn);

        btnDelete.setVisibility(View.INVISIBLE);
        btnUpdate.setVisibility(View.INVISIBLE);
        avatarImageView.setVisibility(View.INVISIBLE);
        nameEt.setVisibility(View.INVISIBLE);
        sizeEt.setVisibility(View.INVISIBLE);
        priceEt.setVisibility(View.INVISIBLE);
        btnContact.setVisibility(View.INVISIBLE);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        ModeLSql modeLSql = new ModeLSql();

        final String itemId = ItemDetailsFragmentArgs.fromBundle(getArguments()).getItemId();

        pb = view.findViewById(R.id.item_list_progress);
        pb.setVisibility(View.VISIBLE);

        Model.instance.getItem(itemId, new Model.GetItemListener() {
            @Override
            public void onComplete(Item result) {
                item = result;
                nameEt.setText(item.getName());
                sizeEt.setText(item.getSize());
                priceEt.setText(item.getPrice().toString());
                tvEmail.setText(item.getOwnBy());
                if (item.getImage() != null) {
                    Picasso.get().load(item.getImage()).placeholder(R.drawable.avatar_icon).into(avatarImageView);
                }
                if (mAuth.getCurrentUser() == null || !mAuth.getCurrentUser().getEmail().equals(item.getOwnBy())) {
                    btnDelete.setVisibility(View.INVISIBLE);
                    btnUpdate.setVisibility(View.INVISIBLE);
                    btnContact.setVisibility(View.VISIBLE);
                } else {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnUpdate.setVisibility(View.VISIBLE);
                    btnContact.setVisibility(View.INVISIBLE);
                }
                pb.setVisibility(View.INVISIBLE);
                avatarImageView.setVisibility(View.VISIBLE);
                nameEt.setVisibility(View.VISIBLE);
                sizeEt.setVisibility(View.VISIBLE);
                priceEt.setVisibility(View.VISIBLE);
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvEmail.setVisibility(View.VISIBLE);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setId(nameEt.getText().toString() + sizeEt.getText().toString() + currentUser.getEmail());
                Log.d("delete id", "onClick: " + item.getId());
                pb.setVisibility(View.VISIBLE);
                Model.instance.delete(item, new Model.DeleteItemListener() {
                    @Override
                    public void onComplete() {
                        Navigation.findNavController(view).navigate(R.id.homeFragment);
                        pb.setVisibility(View.INVISIBLE);
                    }
                });
                Model.instance.refreshAllItems(new Model.GetAllItemsListener() {
                    @Override
                    public void onComplete() {
                    }
                });
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDetailsFragmentDirections.ActionItemDetailsFragmentToAddItem direc = ItemDetailsFragmentDirections.actionItemDetailsFragmentToAddItem();
                direc.setItemId(item.getId());
                Navigation.findNavController(view).navigate(direc);
            }
        });

        return view;
    }
}