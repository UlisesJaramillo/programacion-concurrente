/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class NadoDelfines {

    private int id, t, cont, c;
    private Boolean lleno = false;//la variable lleno sirve para indicar si la pileta esta llena.
    private CyclicBarrier entradaPileta;
    private Lock delfi;
    private Condition esperaDelfi;
    private Lock lock;

    public NadoDelfines(int id, int c, int t, int turnos) {// c es la cantidad de personas que entran, t es el tiempo de la barrera, si es 0 no tiene tiempo, turnos es para indicar la cantidad de turnos
        this.id = id;
        this.c = c;
        this.t = t;
        this.entradaPileta = new CyclicBarrier(c);
        this.delfi = new ReentrantLock();
        this.lock = new ReentrantLock();
        this.esperaDelfi = delfi.newCondition();
        this.cont = 0;

    }

    public boolean verificarEntrada(int id) {
        lock.lock();
        try {
            if (!this.lleno) {
                //verifica que entren c cantidad de personas e indique si la pileta se llenó

                cont++;
                if (cont == this.c + 1) {
                    this.lleno = true;
                    cont = 0;
                    System.out.println("La pileta " + this.id + " está llena!.");
                }
            }

        } finally {
            lock.unlock();
        }

        return this.lleno;
    }

    //entrar,( si el tiempo es 0 , la barrera no tendra interrupcion por tiempo. al liberarse, libera un permiso de un delfin
    public void entrar(int id) {
        if (this.t <= 0) {
            try {
                entradaPileta.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(NadoDelfines.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BrokenBarrierException ex) {
                Logger.getLogger(NadoDelfines.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                entradaPileta.await(t, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(NadoDelfines.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BrokenBarrierException ex) {

            } catch (TimeoutException ex) {
                entradaPileta.reset();
            }

        }
        delfi.lock();
        try {
            esperaDelfi.signal();
        } finally {
            delfi.unlock();
        }
        System.out.println("La persona " + id + " entró a la pileta " + this.id);

    }

    public void nadar() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(NadoDelfines.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void salir(int id) {
        lock.lock();
        try {
            if (this.lleno = true) {
                this.lleno = false;
            }
            System.out.println("La persona " + id + " salió de la pileta " + this.id);
        } finally {
            lock.unlock();
        }
    }

    public void entrarDelfin(int id) {
        delfi.lock();
        try {
            System.out.println("el delfin " + id + " está esperando en la pileta " + this.id);
            esperaDelfi.await();
            System.out.println("el delfin " + id + " entró a la pileta " + this.id);
        } catch (InterruptedException ex) {
            Logger.getLogger(NadoDelfines.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            delfi.unlock();
        }
    }

}
