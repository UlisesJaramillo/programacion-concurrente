/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class NadoSnorkel {

    private int id,cant;
    private CyclicBarrier esperaEquipos;
    private boolean lleno = false;
    private Lock lock;

    public NadoSnorkel(int id) {
        this.id = id;
        this.lock = new ReentrantLock();
        esperaEquipos = new CyclicBarrier(4);//4 porque son tres hilos por los elementos para hacer el buseo,( snorke, salvavidas y patas de rana) y el otro permiso es para la persona

    }

    public void equipar(int id) {//metodo utilizado por los hilos de los equipos

        try {
            esperaEquipos.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(NadoSnorkel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            Logger.getLogger(NadoSnorkel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void entrar(int id) {

        try {
            System.out.println("La persona " + id + " entró a hacer buceo");
            esperaEquipos.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(NadoSnorkel.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            Logger.getLogger(NadoSnorkel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public boolean verificaEntrada() {

        lock.lock();

        try {
            if (!this.lleno) {
                
                cant++;
                if(cant==2){//si ya hay una persona, no va a poder entrar a bucear
                    this.lleno = true;
                    cant=0;
                }

                

            } else {
                System.out.println("el equipo " + this.id + " está ocupado");
            }

        } finally {
            lock.unlock();
        }

        return this.lleno;

    }

    public void salir() {
        lock.lock();
        try {
            if (this.lleno = true) {
                this.lleno = false;
                
            }
        } finally {
            lock.unlock();
        }
    }

    public void busear(int id) {
        try {
            System.out.println("La persona " + id + " está buceando");
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(NadoSnorkel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
