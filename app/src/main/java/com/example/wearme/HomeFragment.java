package com.example.wearme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.wearme.adapters.MyAdapter;
import com.example.wearme.model.Item;
import com.example.wearme.model.Model;

public class HomeFragment extends Fragment {
    com.example.wearme.ItemListViewModel viewModel;

    ProgressBar pb;

    RecyclerView recyclerView;
    MyAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int i = viewHolder.getAdapterPosition();
            String id = viewModel.getList().getValue().get(i).getId();
            HomeFragmentDirections.ActionHomeFragmentToItemDetailsFragment direc = HomeFragmentDirections.actionHomeFragmentToItemDetailsFragment(id);
            Log.e("TAG", "item id chosen: " + id);
            Navigation.findNavController(view).navigate(direc);
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        viewModel = new ViewModelProvider(this).get(com.example.wearme.ItemListViewModel.class);

        recyclerView = view.findViewById(R.id.items_list);
        pb = view.findViewById(R.id.item_list_progress);
        pb.setVisibility(View.INVISIBLE);

        adapter = new MyAdapter(viewModel);
        recyclerView.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this.getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(onItemClickListener);
        viewModel.getList().observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                adapter.notifyDataSetChanged();
            }
        });
        reloadData();
        return view;
    }

    void reloadData() {
        pb.setVisibility(View.VISIBLE);
        Model.instance.refreshAllItems(new Model.GetAllItemsListener() {
            @Override
            public void onComplete() {
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }
}
