package com.example.sporty2;

    import android.annotation.SuppressLint;
    import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
    import android.net.wifi.p2p.WifiP2pDevice;
    import android.net.wifi.p2p.WifiP2pManager;
    import android.util.Log;
    import android.widget.Toast;


public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
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
                System.out.println("wifi on");
            } else {
                Toast.makeText(context, "Wi-Fi Direct is not enabled", Toast.LENGTH_SHORT).show();
                System.out.println("wifi off");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                Log.d("WifiDirectBroadcastReceiver", "Peers changed");
                mManager.requestPeers(mChannel, peerList -> {
                    if (peerList.getDeviceList().size() == 0) {
                        Toast.makeText(context, "No devices found", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (WifiP2pDevice device : peerList.getDeviceList()) {
                        Log.d("WifiDirectBroadcastReceiver", "Device found: " + device.deviceName);
                        if ("VELVET".equals(device.deviceName)) {
                            // Si se encuentra el dispositivo "VELVET", intenta conectar
                            System.out.println("nos intentamos conectar al velvet");
                            mActivity.connectToServer(device);

                                mManager.stopPeerDiscovery(mChannel, null);// Marcamos que el descubrimiento ya se detuvo
                                break;

                            }

                        }

                });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                Toast.makeText(context, "Manager is null", Toast.LENGTH_SHORT).show();
                return;
            }

            mManager.requestConnectionInfo(mChannel, info -> {
                if (info != null && info.groupFormed) {
                    if (info.isGroupOwner) {
                        Toast.makeText(context, "Group owner - Server", Toast.LENGTH_SHORT).show();
                        Log.d("WifiDirectBroadcastReceiver", "Group owner - Server");
                    } else {
                        Toast.makeText(context, "Group member - Client", Toast.LENGTH_SHORT).show();
                        Log.d("WifiDirectBroadcastReceiver", "Group member - Client");
                        mActivity.receiveData(info.groupOwnerAddress);
                    }
                }
            });
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Toast.makeText(context, "This device changed", Toast.LENGTH_SHORT).show();
            Log.d("WifiDirectBroadcastReceiver", "This device changed");
        }
    }
}
