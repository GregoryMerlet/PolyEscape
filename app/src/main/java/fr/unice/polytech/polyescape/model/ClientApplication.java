package fr.unice.polytech.polyescape.model;

import android.app.Application;

/**
 * Allows to create an unique Client.
 */
public class ClientApplication extends Application {
    public static Client client;

    private static ClientApplication singleton;

    public static ClientApplication getInstance() {
        return singleton;
    }

    public static void createClient(String addr, int port) throws Exception {
        client = new Client(addr, port);
        client.execute();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }
}
