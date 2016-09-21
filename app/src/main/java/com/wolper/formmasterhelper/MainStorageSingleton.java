package com.wolper.formmasterhelper;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MainStorageSingleton {
    private static volatile MainStorageSingleton instance;
    public volatile String password;
    public volatile String server;
    //queue for daving playloads redy to be sent to server asyncroniously
    Queue<PlayLoad> queue;


    public static MainStorageSingleton getInstance() {
        MainStorageSingleton localInstance = instance;
        if (localInstance == null) {
            synchronized (MainStorageSingleton.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new MainStorageSingleton();
                }
            }
        }
        return localInstance;
    }

    private MainStorageSingleton() {
        server= "192.168.44.55";
        password="schtirliz";
        queue = new ConcurrentLinkedQueue();
    }

    public synchronized Queue<PlayLoad> getQueue(){
        return queue;
    }
}



