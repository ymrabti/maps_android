package com.ymrabti.osmmaps.tests;


public enum Enume {
    MODE_SOMBRE(0),
    MODE_CLAIRE(1);

    final int id;

    private Enume(int id) {
        this.id = id;
    }

    int toInt() {
        return this.id;
    }
}
