package com.example.sporty;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
        import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.Toast;

    public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

        private WifiP2pManager mManager;
        private WifiP2pManager.Channel mChannel;
        private ServerActivity mActivity;


        public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ServerActivity activity) {
            this.mManager = manager;
            this.mChannel = channel;
            this.mActivity = activity;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("WifiDirectBroadcastReceiver", "Action received: " + action);

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(context, "Wi-Fi Direct is enabled", Toast.LENGTH_SHORT).show();
                    System.out.println("wifi si");
                } else {
                    Toast.makeText(context, "Wi-Fi Direct is not enabled", Toast.LENGTH_SHORT).show();
                    System.out.println("wifi no");
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (mManager != null) {
                    Log.d("WifiDirectBroadcastReceiver", "Peers changed");
                    mManager.requestPeers(mChannel, peerList -> {
                        if (peerList.getDeviceList().size() == 0) {
                            Toast.makeText(context, "No devices found", Toast.LENGTH_SHORT).show();
                            System.out.println("no encontramos");
                            return;
                        }

                        for (WifiP2pDevice device : peerList.getDeviceList()) {
                            Log.d("WifiDirectBroadcastReceiver", "Device found: " + device.deviceName);
                            System.out.println("Estamos esperando a que se conecte alguien");

                        }
                    });
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (mManager == null) {
                    Toast.makeText(context, "Manager is null", Toast.LENGTH_SHORT).show();
                    return;
                }


            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                Toast.makeText(context, "This device changed", Toast.LENGTH_SHORT).show();
                Log.d("WifiDirectBroadcastReceiver", "This device changed");
                System.out.println("cmbia ");
            }
        }
    }