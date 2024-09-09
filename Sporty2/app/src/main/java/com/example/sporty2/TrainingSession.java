package com.example.sporty2;

import java.io.Serializable;
import java.util.Date;

public class TrainingSession implements Serializable {
    private String activityType;
    private long startTime;
    private long endTime;
    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private float kilometers;
    private float calories;

    // Getters, setters y constructor

    @Override
    public String toString() {
        return "Actividad: " + activityType +
                "\nInicio: " + new Date(startTime).toString() + " - Coordenadas: (" + startLatitude + ", " + startLongitude + ")" +
                "\nFin: " + new Date(endTime).toString() + " - Coordenadas: (" + endLatitude + ", " + endLongitude + ")" +
                "\nKilómetros: " + kilometers +
                "\nCalorías: " + calories;
    }
}
