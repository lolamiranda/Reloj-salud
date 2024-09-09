package com.example.sporty2;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private WifiP2pManager.Channel mChannel;
    private WiFiDirectBroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private static final int SERVER_PORT = 8888;

    private Button btnTrans;
    private ListView listView;
    List<String> registros;
    WifiP2pManager mManager;
    private boolean receivedData = false;
    public boolean conexion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.lista);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        btnTrans = findViewById(R.id.button);


        btnTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                discoverPeers();
                receivedData = false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!conexion) {
            registerReceiver(mReceiver, mIntentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private void discoverPeers() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Discovery Failed: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void connectToServer(WifiP2pDevice device) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        System.out.println("Connecting to device: " + device.deviceName);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // La conexión fue exitosa, ahora obtener la información de conexión
                mManager.requestConnectionInfo(mChannel, info -> {
                    if (info != null && info.groupFormed && !info.isGroupOwner) {
                        System.out.println("Connected to: " + device.deviceName);
                        Toast.makeText(MainActivity.this, "Connected to " + device.deviceName, Toast.LENGTH_SHORT).show();

                        receiveData(info.groupOwnerAddress);
                        conexion=true;

                    }
                });
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connect failed. Retry.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    public void receiveData(InetAddress serverAddress) {
       // new ClientTask(serverAddress).execute();

        if (!receivedData) { // Verifica si no se ha recibido datos previamente
            new ClientTask(serverAddress).execute();
        }
    }

    private class ClientTask extends AsyncTask<Void, Void, List<String>> {
        private InetAddress serverAddress;

        ClientTask(InetAddress serverAddress) {
            this.serverAddress = serverAddress;
        }
        int lastId = 1;

        @Override
        protected List<String> doInBackground(Void... voids) {

            List<String> trainingSessions = new ArrayList<>();
            try {
                Socket socket = new Socket(serverAddress, SERVER_PORT);

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

                // Send the last received ID to the server
                outputStream.writeInt(lastId);
                outputStream.flush();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                trainingSessions = (List<String>) inputStream.readObject();


                System.out.println(trainingSessions);
                Pattern pattern = Pattern.compile("Id: (\\d+)");
                int id = 0;

                for (String objeto : trainingSessions) {
                    // Busca todas las coincidencias del patrón en la cadena
                    Matcher matcher = pattern.matcher(objeto);

                    // Itera sobre todas las coincidencias encontradas
                    while (matcher.find()) {
                        // Extrae y muestra cada ID
                        String idStr = matcher.group(1); // Obtiene el ID como una cadena
                        id = Integer.parseInt(idStr); // Convierte la cadena a un entero


                    }


                }

                lastId=id;
                System.out.println(lastId);

                receivedData = true;


                inputStream.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return trainingSessions;
        }

        @Override
        protected void onPostExecute(List<String> trainingSessions) {
            super.onPostExecute(trainingSessions);
            ListView listView = findViewById(R.id.lista);


            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, trainingSessions);
            listView.setAdapter(adapter);
        }
    }
}
