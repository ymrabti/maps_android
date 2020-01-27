package com.ymrabti.osmmaps.tests;


public class ClassWithInterface {


    private int numero;

    public ClassWithInterface(int number) {
        this.numero = number ;
    }

    public interface ResultListener {
        void younes();
        void mrabti();
    }
    public void request(ResultListener resultListener) {

        if (numero == 0) {
            resultListener.younes();
        } else {
            resultListener.mrabti();
        }
    }
}
