/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class Faro {

    private Semaphore sem;//Semaforo que controla el acceso concurrente a la escalera
    private Semaphore sem1 = new Semaphore(1);//tobogan 1
    private Semaphore sem2 = new Semaphore(1);//tobogan 2
    private Semaphore sem3 = new Semaphore(1);//semaforo que controla el acceso concurrente al organizador, que decide en que tobogan se tiran las personas
    ArrayBlockingQueue cola;//cola de espera en común que tienen los dos toboganes
    private int contador = 0;

    public Faro(int s, int c) {
        sem = new Semaphore(s);//capacidad de la escalera
        cola = new ArrayBlockingQueue(c);//capacidad de personas que hacen cola
    }

    public void entrarEscalera(int id) {
        try {
            sem.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Faro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void subir() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Faro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mirar() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Faro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salirEscalera(int id) {
        sem.release();
    }

    public void hacerCola(int id) {
        try {

            cola.put(this); //ingresa a la cola común de espera          
        } catch (InterruptedException ex) {
            Logger.getLogger(Faro.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tirarseTobogan(int id) {
        int i = 0;
        try {
            sem3.acquire();
            i = administrador();//metodo que organiza a las personas para que se tiren en cualquiera de los dos toboganes que tiene ésta actividad
        } catch (InterruptedException ex) {
            Logger.getLogger(Faro.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            sem3.release();
        }

        if (i == 0) {
            try {
                sem1.acquire();//solamente se pueden tirar del tobogan de a una persona a la vez
                cola.take();
                Thread.sleep(1000);
                System.out.println("la persona" + id + " se tiró por el tobogan 1");
            } catch (InterruptedException ex) {
                Logger.getLogger(Faro.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                sem1.release();
            }

        } else {
            try {
                sem2.acquire();
                cola.take();
                Thread.sleep(1000);
                System.out.println("la persona" + id + " se tiró por el tobogan 2");
            } catch (InterruptedException ex) {
                Logger.getLogger(Faro.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                sem2.release();
            }
        }

    }

    private int administrador() {
        //metodo para ordenar a las personas en los toboganes.
        return this.contador = ((this.contador + 1) % (2));//cicla entre 0 y 1.
    }
}
