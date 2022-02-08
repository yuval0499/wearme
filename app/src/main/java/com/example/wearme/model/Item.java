package com.example.wearme.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Item {
    @PrimaryKey
    @NonNull
    private String id;
    private String ownBy;
    private String name;
    private String size;
    private Long price;
    private String image;
    private Long lastUpdated;

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("ownBy", ownBy);
        result.put("size", size);
        result.put("price", price);
        result.put("image", image);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        return result;
    }

    public void fromMap(Map<String, Object> map) {
        id = (String)map.get("id");
        name = (String)map.get("name");
        size = (String)map.get("size");
//        price = Long.valueOf(11);
        price = (Long)map.get("price");
        ownBy = (String)map.get("ownBy");
        image = (String)map.get("image");
        Timestamp ts = (Timestamp)map.get("lastUpdated");
        lastUpdated = ts.getSeconds();
//        long time = ts.toDate().getTime();
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public String getId() {
        return id;
    }

    public String getOwnBy() {
        return ownBy;
    }

    public String getName() {
        return name;
    }

    public String getSize() {
        return size;
    }

    public Long getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public void setOwnBy(String ownBy) {
        this.ownBy = ownBy;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

}
