package com.example.automotive.dummy;

import java.util.UUID;

//public class DiscoveryItem {
public class DiscoveryItem {

    public static final int SERVICE = 1;
    public static final int CHARACTERISTIC = 2;
    final int type;
    final String description;
    final UUID uuid;

    public DiscoveryItem(int type, String description, UUID uuid) {
        this.type = type;
        this.description = description;
        this.uuid = uuid;
    }

    @Override
    public String toString() {
        return "DiscoveryItem{" +
                "type=" + type +
                ", description='" + description + '\'' +
                ", uuid=" + uuid +
                '}';
    }
}
//}
