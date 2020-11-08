package com.example.exchange_rate.model;

import java.util.ArrayList;
import java.util.HashMap;

public class HashArrayMap<K, V>{
    private HashMap<K, ArrayList<V>> hashMap = new HashMap<>();

    public void put(K key, V value){

        ArrayList<V> arrayList = new ArrayList<>();
        if(!hashMap.containsKey(key)){
            arrayList = new ArrayList<>();
            arrayList.add(value);
            hashMap.put(key, arrayList);
        }
        else{
            arrayList = hashMap.get(key);
            if (arrayList != null){
                arrayList.add(value);
                hashMap.put(key, arrayList);
            }

        }
    }

    public ArrayList<V> get(K key){
        return hashMap.get(key);
    }

}
