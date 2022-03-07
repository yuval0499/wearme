package com.example.wearme;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wearme.adapters.FilterAdapter;

public class SearchFragment extends Fragment {

    com.example.wearme.ItemListViewModel viewModel;

    View view;

    SearchView svSearch;
    RecyclerView rvResult;
    FilterAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
            int i = viewHolder.getAdapterPosition();
            String id = viewModel.getListByFilter(svSearch.getQuery().toString()).get(i).getId();
            SearchFragmentDirections.ActionSearchFragmentToItemDetailsFragment direc = SearchFragmentDirections.actionSearchFragmentToItemDetailsFragment(id);
            Log.e("TAG", "item id chosen: " + id);
            Navigation.findNavController(view).navigate(direc);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        svSearch = view.findViewById(R.id.sv_search);
        rvResult = view.findViewById(R.id.rv_search);
        rvResult.setHasFixedSize(true);
        viewModel = new ViewModelProvider(this).get(com.example.wearme.ItemListViewModel.class);
        adapter = new FilterAdapter(viewModel, "");
        rvResult.setAdapter(adapter);
        layoutManager = new GridLayoutManager(this.getActivity(), 2);
        rvResult.setLayoutManager(layoutManager);
        adapter.setOnItemClickListener(onItemClickListener);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d("Art-Search", "onQueryTextSubmit: ");
                adapter.setFilter(s);
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d("Art-Search", "onQueryTextChange: ");
                adapter.setFilter(s);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        return view;
    }

}
