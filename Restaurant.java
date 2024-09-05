/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class Restaurant {
    private int cantPersonas;
    private int capacidad;
    private BlockingQueue cola;
    private int id;
    
    public Restaurant(int id,int cap,int c){
        this.cantPersonas=0;
        this.capacidad=cap;
        this.id=id;
        this.cola = new ArrayBlockingQueue(c);
    }
    
    public synchronized void  entrar(int id){
        while(cantPersonas>=capacidad){
            try {
                System.out.println("En espera Restaurant"+id);
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Restaurant.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        cantPersonas++;
    }
    
    public void hacerCola(int id){
        try {
            cola.put(this);//hace la cola
        } catch (InterruptedException ex) {
            Logger.getLogger(Restaurant.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void comer(int id){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Restaurant.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public synchronized void salir(int id){
        try {
            cola.take();//permite que otra persona que estaba esperando en la cola, pueda comer
        } catch (InterruptedException ex) {
            Logger.getLogger(Restaurant.class.getName()).log(Level.SEVERE, null, ex);
        }
        cantPersonas--;
        System.out.println("La persona "+id+" salio del restaurant "+this.id);
        this.notifyAll();
        
    }

}
