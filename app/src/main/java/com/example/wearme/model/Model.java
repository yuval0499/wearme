package com.example.wearme.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import java.util.List;

import com.example.wearme.MyApplication;

public class Model {
    public final static Model instance = new Model();

    ModelFirebase modelFirebase = new ModelFirebase();
    ModeLSql modeLSql = new ModeLSql();

    private Model() {}

    public interface Listener<T> {
        void onComplete(T result);
    }

    LiveData<List<Item>> itemsList;

    public LiveData<List<Item>> getAllItems() {
        if (itemsList == null) {
            itemsList = modeLSql.getAllItems();
            refreshAllItems(null);
        }
        return itemsList;
    }

    List<Item> currUserItemsList;

    public List<Item> getCurrentUserItems(String currUser) {
        currUserItemsList = modeLSql.getCurrentUserItems(currUser);
        refreshAllItems(null);
        return currUserItemsList;
    }


    public interface GetAllItemsListener {
        void onComplete();
    }

    public void refreshAllItems(final GetAllItemsListener listener) {
        final SharedPreferences sp = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long lastUpdated = sp.getLong("lastUpdated", 0);

        modelFirebase.getAllItems(lastUpdated, new ModelFirebase.GetAllItemsListener() {
            @Override
            public void onComplete(List<Item> result) {
                long lastU = 0;
                for (Item item : result) {
                    modeLSql.addItem(item, null);
                    if (item.getLastUpdated() > lastU) {
                        lastU = item.getLastUpdated();
                    }
                }
                sp.edit().putLong("lastUpdated", lastU).commit();
                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    public interface GetItemListener extends Listener<Item> {
        void onComplete(Item item);
    }

    public void getItem(String id, GetItemListener listener) {
        modelFirebase.getItem(id, listener);
    }

    public interface AddItemListener {
        void onComplete();
    }

    public void addItem(Item item, AddItemListener listener) {
        modelFirebase.addItem(item, new AddItemListener() {
            @Override
            public void onComplete() {
                refreshAllItems(new GetAllItemsListener() {
                    @Override
                    public void onComplete() {
                        listener.onComplete();
                    }
                });
            }
        });
    }

    public interface UpdateItemListener extends AddItemListener {}

    public void updateItem(final Item item, final AddItemListener listener) {
        modelFirebase.updateItem(item, listener);
    }

    public interface DeleteItemListener extends AddItemListener {
    }

    public void delete(Item item, DeleteItemListener listener) {
        modelFirebase.delete(item, listener);
        modeLSql.deleteItem(item);
    }

    public interface UploadImageListener extends Listener<String> {
    }

    public void uploadImage(Bitmap imageBmp, String name, final UploadImageListener listener) {
        modelFirebase.uploadImage(imageBmp, name, listener);
    }
}
