package com.example.wearme.model;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ModeLSql {
    public LiveData<List<Item>> getAllItems() {
        return AppLocalDB.db.itemDao().getAllItems();
    }

    public List<Item> getCurrentUserItems(String currUser) {
        class MyAsyncTask extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                return AppLocalDB.db.itemDao().getCurrentUserItems(currUser);
            }
        };
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
        return null;
    }

    public interface AddItemListener {
        void onComplete();
    }
    public void addItem(Item item, Model.AddItemListener listener) {
        class MyAsyncTask extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDB.db.itemDao().insert(item);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        };
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }

    public void deleteItem(Item item) {
        class MyAsyncTask extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDB.db.itemDao().delete(item.getId());
                return null;
            }
        };
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }
    public void deleteAllItems() {
        class MyAsyncTask extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDB.db.itemDao().deleteAll();
                return null;
            }
        };
        MyAsyncTask task = new MyAsyncTask();
        task.execute();
    }
}
