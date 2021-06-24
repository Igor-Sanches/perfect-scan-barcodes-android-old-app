package com.igordutrasanches.perfectscan.manager.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    public Map<String,SkuDetails> mSkuMap = new HashMap<>();
    public Map<String,Purchase> mPurchaseMap = new HashMap<>();

    Inventory() { }

    public SkuDetails getSkuDetails(String sku) {
        return mSkuMap.get(sku);
    }

    public Purchase getPurchase(String sku) {
        return mPurchaseMap.get(sku);
    }

    public SkuDetails getDetails(String sku) {
        return mSkuMap.get(sku);
    }

    public boolean hasPurchase(String sku) {
        return mPurchaseMap.containsKey(sku);
    }

    public boolean hasDetails(String sku) {
        return mSkuMap.containsKey(sku);
    }

    public void erasePurchase(String sku) {
        if (mPurchaseMap.containsKey(sku)) mPurchaseMap.remove(sku);
    }

    List<String> getAllOwnedSkus() {
        return new ArrayList<String>(mPurchaseMap.keySet());
    }

    List<String> getAllOwnedSkus(String itemType) {
        List<String> result = new ArrayList<>();
        for (Purchase p : mPurchaseMap.values()) {
            if (p.getItemType().equals(itemType)) result.add(p.getSku());
        }
        return result;
    }

    List<Purchase> getAllPurchases() {
        return new ArrayList<>(mPurchaseMap.values());
    }

    void addSkuDetails(SkuDetails d) {
        mSkuMap.put(d.getSku(), d);
    }

    void addPurchase(Purchase p) {
        mPurchaseMap.put(p.getSku(), p);
    }
}
