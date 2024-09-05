/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class Agente implements Runnable {// esta clase se encarga solamente del intercambio de las llaves con los bolsos.

    private EcoPcs eco;
    private Object[] estantes;
    private Stack pila;

    public Agente(EcoPcs eco, int b) {
        this.eco = eco;
        int i;
        pila=new Stack();
        estantes = new Object[b];
        for (i = 0; i < b; i++) {
            eco.ponerBolso(new Bolso(i));
            pila.push(new Llave(i));
        }

    }

    public void run() {
        int numero;
        while (true) {
            pila.push(eco.intercambiar(pila.pop()));//en caso de que no halla nadie intentado intercambiar una llave o bolso, quedará a la espera en el intercambio

        }

    }

}
