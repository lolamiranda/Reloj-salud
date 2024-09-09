package com.example.sporty;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.button5).setOnClickListener(v -> openActivity("nadar"));
        findViewById(R.id.button3).setOnClickListener(v -> openActivity("correr"));
        findViewById(R.id.button4).setOnClickListener(v -> openActivity("andar"));
        findViewById(R.id.button6).setOnClickListener(v -> openActivity("bici"));

    }
    private void openActivity(String activityType) {
        Intent intent = new Intent(MainActivity.this, Inicio.class);
        intent.putExtra("activity_type", activityType);
        startActivity(intent);
    }
}