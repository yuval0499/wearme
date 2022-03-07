package com.example.wearme.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import com.example.wearme.ItemListViewModel;
import com.example.wearme.R;
import com.example.wearme.model.Item;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private View.OnClickListener mOnItemClickListener;
    ItemListViewModel viewModel;

    public void setOnItemClickListener(View.OnClickListener clickListener) {
        mOnItemClickListener = clickListener;
    }

    public MyAdapter(ItemListViewModel vViewModel) {
        viewModel = vViewModel;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        ImageView imv;
        TextView tvItemArtist, tvItemPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvItemArtist = itemView.findViewById(R.id.tvItemArtist);
//            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            tv = itemView.findViewById(R.id.list_row_text_v);
            imv = itemView.findViewById(R.id.list_row_image_v);

            itemView.setTag(this);
            itemView.setOnClickListener(mOnItemClickListener);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);

        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = viewModel.getList().getValue().get(position);
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

        if (viewModel.getList().getValue() == null) {
            return 0;
        }
        return viewModel.getList().getValue().size();
    }
}