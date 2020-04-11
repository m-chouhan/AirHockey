package com.airhockey.entities;

public class Vector2D {
    float x;
    float y;

    double abs() {
        return Math.sqrt(x*x + y*y);
    }
}
