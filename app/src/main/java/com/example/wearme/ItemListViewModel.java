package com.example.wearme;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;
import java.util.List;

import com.example.wearme.model.Item;
import com.example.wearme.model.Model;

public class ItemListViewModel extends ViewModel {
    private LiveData<List<Item>> allItems = Model.instance.getAllItems();

    public List<Item> getListByCurrOwner() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        List<Item> newList = new LinkedList<Item>();
        if (mAuth.getCurrentUser() != null) {
            for (Item item : allItems.getValue()) {
                if (item.getOwnBy().equals(mAuth.getCurrentUser().getEmail())){
                    newList.add(item);
                }
            }
        }
        return newList;
    }

    public LiveData<List<Item>> getList() {
        return allItems;
    }

    public List<Item> getListByFilter(String s) {
        List<Item> newList = new LinkedList<Item>();

        for (Item item : allItems.getValue()) {
            if (item.getName().contains(s.trim())){
                newList.add(item);
            }
        }

        return newList;
    }
}
