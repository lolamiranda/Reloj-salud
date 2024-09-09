package com.example.sporty;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Registro extends AppCompatActivity {
    private BaseDatos mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mDbHelper = new BaseDatos(this, "entrenos.db", null, 1);

        // Obtener referencia al ListView en el layout
        ListView listView = findViewById(R.id.listView);

        // Obtener datos de la base de datos y mostrarlos en el ListView
        ArrayList <String> registros = obtenerRegistros();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, registros);
        listView.setAdapter(adapter);

        Button btnVolver = findViewById(R.id.button7);

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registro.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btnTransfer = findViewById(R.id.button8);

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registro.this, ServerActivity.class);
                startActivity(intent);
            }
        });
    }
    private ArrayList<String> obtenerRegistros() {
        ArrayList<String> registros = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                BaseDatos.COLUMN_ID,
                BaseDatos.COLUMN_ACTIVITY_TYPE,
                BaseDatos.COLUMN_CIUDAD,
                BaseDatos.COLUMN_DISTANCE,
                BaseDatos.COLUMN_DATE,
                BaseDatos.COLUMN_PASOS,
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

            float distancia = cursor.getFloat(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_DISTANCE));
            float pasos = cursor.getFloat(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_PASOS));

            long fechaLong = cursor.getLong(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_DATE));
            Date fechaDate = new Date(fechaLong);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fechaFormateada = sdf.format(fechaDate);


            float calorias = cursor.getFloat(cursor.getColumnIndexOrThrow(BaseDatos.COLUMN_CALORIES));
            // Construye una cadena con los datos del registro y agrégala a la lista
            String registro = "ID:" + id + ",Actividad: " + actividad + ", Ciudad: " + ciudad +  ", Distancia: " + distancia + ", Pasos: " + pasos + ", Fecha: " + fechaFormateada + ", Calorías: " + calorias;
            registros.add(registro);
        }
        cursor.close();
        return registros;
    }
}

