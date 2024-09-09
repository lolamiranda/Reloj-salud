package com.example.sporty;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerActivity extends AppCompatActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private static final int SERVER_PORT = 8888;
    private BaseDatos mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mDbHelper = new BaseDatos(this, "entrenos.db", null, 1);


        Button buttonVolver = findViewById(R.id.volver);
        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir la otra actividad (ActivityB)
                Intent intent = new Intent(ServerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Start server thread to listen for connections
        new ServerTask().execute();
    }

    private class ServerTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

                    // Get data from the database
                    List<String> registros = obtenerRegistros();

                    // Send data to the client
                    out.writeObject(registros);

                    int lastReceivedId = inputStream.readInt();
                    System.out.println(lastReceivedId);

                    inputStream.close();
                    out.close();
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private ArrayList<String> obtenerRegistros() {
        ArrayList<String> registros = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                BaseDatos.COLUMN_ID,
                BaseDatos.COLUMN_ACTIVITY_TYPE,
                BaseDatos.COLUMN_CIUDAD,
                BaseDatos.COLUMN_TIME,
                BaseDatos.COLUMN_DISTANCE,
                BaseDatos.COLUMN_DATE,
                BaseDatos.COLUMN_CALORIES
        };
        Cursor cursor = db.query(
                BaseDatos.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_ID));
            String actividad = cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_ACTIVITY_TYPE));
            String ciudad = cursor.getString(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_CIUDAD));
            long endTime = cursor.getLong(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_TIME));
            float distancia = cursor.getFloat(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_DISTANCE));
            long fecha = cursor.getLong(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_DATE));
            float calorias = cursor.getFloat(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_CALORIES));
            // Construye una cadena con los datos del registro y agrégala a la lista
            String registro = "Id: " + id +",Actividad: " + actividad + ", Ciudad: " + ciudad + ", Tiempo: " + endTime + ", Distancia: " + distancia + ", Fecha: " + fecha + ", Calorías: " + calorias;
            registros.add(registro);
        }
        cursor.close();
        return registros;
    }
}
