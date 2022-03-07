package com.example.wearme.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import com.example.wearme.ItemListViewModel;
import com.example.wearme.R;
import com.example.wearme.model.Item;

public class CurrUserAdapter extends RecyclerView.Adapter<CurrUserAdapter.ViewHolder> {
    ItemListViewModel viewModel;
    private View.OnClickListener mOnItemClickListener;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        mOnItemClickListener = clickListener;
    }

    public CurrUserAdapter(ItemListViewModel vViewModel) {
        viewModel = vViewModel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView imv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.list_row_text_v);
            imv = itemView.findViewById(R.id.list_row_image_v);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_user, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = viewModel.getListByCurrOwner().get(position);
        System.out.println(item.getName());
        holder.tv.setText(item.getName());

        holder.imv.setImageResource(R.drawable.progress);
        if (item.getImage() != null) {
            Picasso.get().load(item.getImage()).placeholder(R.drawable.progress).into(holder.imv);
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (mAuth.getCurrentUser() == null || viewModel == null) {
            return 0;
        }
        else if (viewModel.getListByCurrOwner() == null) {
            return 0;
        }
        return viewModel.getListByCurrOwner().size();
    }
}
