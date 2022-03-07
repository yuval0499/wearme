package com.example.wearme;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import com.example.wearme.model.Item;
import com.example.wearme.model.Model;

public class AddItem extends Fragment {
    ImageView avatarImageViewIcon;
    ImageView avatarImageViewImage;
    ImageButton editImage;
    EditText idEt;
    EditText nameEt;
    EditText sizeEt;
    EditText priceEt;
    EditText imageReq;
    TextView header;
    TextView secHeader;
    Button publishBtn;
    View view;
    FirebaseUser currentUser;
    com.example.wearme.ItemListViewModel viewModel;
    Item itemToEdit;
    ProgressBar pb;
    String itemId;

    // Create Firebase auth instance:
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_add_item, container, false);
        itemId = AddItemArgs.fromBundle(getArguments()).getItemId();
        avatarImageViewIcon = view.findViewById(R.id.add_item_icon);
        avatarImageViewImage = view.findViewById(R.id.add_item_image);
        editImage = view.findViewById(R.id.edit_image_btn);
        nameEt = view.findViewById(R.id.add_item_name);
        sizeEt = view.findViewById(R.id.add_item_size);
        imageReq = view.findViewById(R.id.image_require);
        imageReq.setVisibility(View.INVISIBLE);
        priceEt = view.findViewById(R.id.add_item_price);
        publishBtn = view.findViewById(R.id.contact_item_btn);
        header = view.findViewById(R.id.add_item_page_header);
        secHeader = view.findViewById(R.id.add_item_page_second_header);
        pb = view.findViewById(R.id.item_list_progress);
        pb.setVisibility(View.VISIBLE);

        if (!itemId.equals("-1")) {
            Model.instance.getItem(itemId, new Model.GetItemListener() {
                @Override
                public void onComplete(Item result) {
                    header.setText("UPDATE YOUR CREATION");
                    itemToEdit = result;
//                    idEt.setText(itemToEdit.getId());
                    nameEt.setText(itemToEdit.getName());
                    sizeEt.setText(itemToEdit.getSize());
                    priceEt.setText(itemToEdit.getPrice().toString());
                    if (itemToEdit.getImage() != null) {
                        Picasso.get().load(itemToEdit.getImage()).placeholder(R.drawable.avatar_icon).into(avatarImageViewImage);
                        avatarImageViewIcon.setVisibility(View.INVISIBLE);
                    }
                    publishBtn.setText("UPDATE");
                }
            });
        } else {
            header.setText("ADD A NEW CREATION");
            secHeader.setText("SHOW US YOUR NEW WORK");
            publishBtn.setText("PUBLISH");
        }

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editImage();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = nameEt.getText().toString().trim();
                String size = sizeEt.getText().toString().trim().toUpperCase();
                String sPrice = priceEt.getText().toString().trim();

                if (avatarImageViewImage.getDrawable() == null) {
                    imageReq.setVisibility(View.VISIBLE);
                    imageReq.setError("Image is required !");
                    return;
                }

                if (TextUtils.isEmpty(name)) {
                    nameEt.setError("Email is required !");
                    return;
                }

                if (TextUtils.isEmpty(size)) {
                    sizeEt.setError("Size is required !");
                    return;
                }

                if (! (size.equals("XS") || size.equals("S") || size.equals("M") || size.equals("L") || size.equals("XL"))) {
                    sizeEt.setError(size);
                    return;
                }

                if (TextUtils.isEmpty(sPrice)) {
                    priceEt.setError("Price is required !");
                    return;
                }

                int price = Integer.parseInt(sPrice);

                if (price < 0) {
                    priceEt.setError("Price can be < 0 !");
                    return;
                }

                if (!itemId.equals("-1")) {
                    updateItem();
                } else {
                    saveItem();
                }
            }
        });

        // Check whether a user is not logged in:
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d("Authentication", "wearme: User not logged in");
            Navigation.findNavController(view).navigate(R.id.fragment_signin);
        }
        pb.setVisibility(View.INVISIBLE);
    }

    public void updateItem() {
        pb.setVisibility(View.VISIBLE);
        Item itemToUpdate = new Item();
        itemToUpdate.setId(nameEt.getText().toString() + sizeEt.getText().toString() + mAuth.getCurrentUser().getEmail());
        itemToUpdate.setName(nameEt.getText().toString());
        itemToUpdate.setSize(sizeEt.getText().toString());
        itemToUpdate.setPrice(Long.valueOf(priceEt.getText().toString()));
        itemToUpdate.setOwnBy(currentUser.getEmail());

        BitmapDrawable drawable = (BitmapDrawable) avatarImageViewImage.getDrawable();
        Bitmap image = drawable.getBitmap();

        Model.instance.uploadImage(image, itemToUpdate.getId(), new Model.UploadImageListener() {
            @Override
            public void onComplete(String url) {
                if (url == null) {
                    displayFailedError();
                } else {
                    itemToUpdate.setImage(url);
                    update(itemToUpdate);
                }

                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void update(Item itemToUpdate) {
        Model.instance.updateItem(itemToUpdate, new Model.DeleteItemListener() {
            @Override
            public void onComplete() {
                Navigation.findNavController(view).navigate(R.id.homeFragment);
            }
        });
    }

    private void saveItem() {
        pb.setVisibility(View.VISIBLE);
        Item item = new Item();
        item.setId(nameEt.getText().toString() + sizeEt.getText().toString() + mAuth.getCurrentUser().getEmail());
        item.setName(nameEt.getText().toString());
        item.setSize(sizeEt.getText().toString());
        item.setPrice(Long.valueOf(priceEt.getText().toString()));
        item.setOwnBy(currentUser.getEmail());

        BitmapDrawable drawable = (BitmapDrawable) avatarImageViewImage.getDrawable();
        Bitmap image = drawable.getBitmap();

        Model.instance.uploadImage(image, item.getId(), new Model.UploadImageListener() {
            @Override
            public void onComplete(String url) {
                if (url == null) {
                    displayFailedError();
                } else {
                    item.setImage(url);
                    Model.instance.addItem(item, new Model.AddItemListener() {
                        @Override
                        public void onComplete() {
                            Navigation.findNavController(view).navigate(R.id.homeFragment);
                            displaySuccessError();
                        }
                    });
                }
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void displayFailedError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Operation Failed");
        builder.setMessage("Couldn't upload artifact");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void displaySuccessError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Operation Succeed");
        builder.setMessage("Artifact saved successfully !");
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void editImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);

                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(intent, "Choose Picture"),1);
//                    Intent pickPhoto = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
//                    startActivityForResult(pickPhoto, 1);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        avatarImageViewImage.setImageBitmap(selectedImage);
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            avatarImageViewImage.setImageURI(selectedImage);
                            avatarImageViewIcon.setVisibility(View.INVISIBLE);
                            imageReq.setVisibility(View.INVISIBLE);
                        }
                    }
                    break;
            }
        }
    }
}