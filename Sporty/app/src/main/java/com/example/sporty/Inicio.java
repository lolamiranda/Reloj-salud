package com.example.sporty;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;


public class Inicio extends AppCompatActivity implements SensorEventListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 2;
    private TextView tvActivity;
    private Button btnEmpezar;
    private Button btnAcabar;
    private String activityType;
    private long startTime;
    private Location inicio;
    private Location fin;
    private float totalDistance;
    private BaseDatos mDbHelper;

    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isSensorPresent = false;
    private int stepCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        tvActivity = findViewById(R.id.textView);
        btnEmpezar = findViewById(R.id.button);
        btnAcabar = findViewById(R.id.button2);

        btnEmpezar.setEnabled(true);
        btnAcabar.setEnabled(false);

        mDbHelper = new BaseDatos(this, "entrenos.db", null, 1);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isSensorPresent = true;
        }

        activityType = getIntent().getStringExtra("activity_type");
        tvActivity.setText("Actividad: " + activityType);




        btnEmpezar.setOnClickListener(v -> empezar());
        btnAcabar.setOnClickListener(v -> end());

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
            //sino tenemos permiso lo solicitamos
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no se ha otorgado el permiso, solicitarlo
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
        }
    }

    private void empezar() {
        LocationManager locationManager = (LocationManager) Inicio.this.getSystemService(Context.LOCATION_SERVICE);
         LocationListener locationListener1 = new  LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                inicio = location;
                System.out.println("Inicio: " + inicio);

                if (locationManager != null) {
                    locationManager.removeUpdates(this);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }
        };
        int permissionCheck = ContextCompat.checkSelfPermission(Inicio.this, Manifest.permission.ACCESS_FINE_LOCATION);
 locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener1);


        startTime = System.currentTimeMillis();
        totalDistance = 0;
        stepCount = 0;
        if (isSensorPresent) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        btnEmpezar.setEnabled(false);
        btnAcabar.setEnabled(true);
    }

    private void end() {
        final int[] pasos = {0};

        ProgressDialog progressDialog = new ProgressDialog(Inicio.this);
        progressDialog.setMessage("Guardando sesión de entrenamiento...");
        progressDialog.setCancelable(false); // Evita que el usuario cancele el diálogo
        progressDialog.show();

        LocationManager locationManager = (LocationManager) Inicio.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                fin = location;
                System.out.println("Fin: " + fin);

                if (locationManager != null) {
                    locationManager.removeUpdates(this);
                }

                if (inicio != null && fin != null) {
                    totalDistance = inicio.distanceTo(fin);
                } else {
                    totalDistance = 0;
                }

                if (isSensorPresent) {
                    sensorManager.unregisterListener(Inicio.this);
                    pasos[0] = stepCount;
                }

                long endTime = System.currentTimeMillis();
                long duration1 = endTime - startTime;
                long duration = duration1 / 1000;


                float kilometers = totalDistance / 1000;

                saveTrainingSession(inicio, duration,kilometers, pasos[0]);

                openActivity();
            }

                @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }
        };

        int permissionCheck = ContextCompat.checkSelfPermission(Inicio.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            // Manejar el caso en que no se tiene permiso
            return;
        }

    }



    private void saveTrainingSession(Location inicio,long duration, float kilometers, int pasos) {
        float calorias;
        String ciudad = "hola";
        ciudad = getAddressFromLocation(inicio);

        System.out.println(ciudad);
        System.out.println(inicio);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (activityType) {
            case "nadar":
                 calorias = (10 * duration * kilometers) / 100;
                 pasos=0;
                break;
            case "correr":
                calorias = (20 * duration * kilometers) / 100;
                break;
            case "bici":
                calorias = (15 * duration * kilometers) / 100;
                pasos=0;
                break;
            case "caminar":
                calorias = (5 * duration * kilometers) / 100;
                break;
            default:
                calorias = 0;
                break;
        }


        ContentValues values = new ContentValues();
        values.put(BaseDatos.COLUMN_ACTIVITY_TYPE, activityType);
        values.put(BaseDatos.COLUMN_CIUDAD, ciudad);
        values.put(BaseDatos.COLUMN_DISTANCE, kilometers);
        values.put(BaseDatos.COLUMN_TIME, duration);
        values.put(BaseDatos.COLUMN_PASOS, pasos);

        long fechaActualMillis = System.currentTimeMillis();
        values.put(BaseDatos.COLUMN_DATE, fechaActualMillis);

        values.put(BaseDatos.COLUMN_CALORIES, calorias);

        long newRowId = db.insert(BaseDatos.TABLE_NAME, null, values);

        if (newRowId != -1) {
            System.out.println("Datos de sesión de entrenamiento guardados en la base de datos");
        } else {
            System.out.println("Error al guardar los datos de la sesión de entrenamiento en la base de datos");
        }
    }


    private void openActivity() {
        Intent intent = new Intent(Inicio.this, Registro.class);
        startActivity(intent);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            stepCount = (int) event.values[0];
            System.out.println(stepCount);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No se usa en este caso
    }

    private String getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException ioException) {
            // Catch network or other I/O problems.
            ioException.printStackTrace();
            return "Error al obtener la dirección.";
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values.
            illegalArgumentException.printStackTrace();
            return "Coordenadas inválidas.";
        }

        // If the reverse geocode returned an address
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String addressText = String.format(
                    "%s, %s, %s",
                    // If there's a street address, add it
                    address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                    // Locality is usually a city
                    address.getLocality(),
                    // The country of the address
                    address.getCountryName());
            return addressText;
        } else {
            return "Dirección no encontrada.";
        }
    }

}


