/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
class Caja {
    int nro;
    Lock mutex = new ReentrantLock(true);
    Condition espera = mutex.newCondition();
    boolean atendiendo=false;
    
    public Caja(int nro){
        this.nro=nro;
        
        
    }
    
    public void entrar(int id){//metodo para entrar a la caja.
        
        mutex.lock();
        try {
            
            while (atendiendo) {
                
                try {
                    espera.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Caja.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        } finally {
            atendiendo=true;
            System.out.println("la persona "+id+" entró a la caja "+nro);
            mutex.unlock();
        }
        
        
    }
    
    public void salir(){
        mutex.lock();
        try {
            atendiendo=false;
            espera.signal();
        } finally {
            mutex.unlock();
        }
    }
    
    public void cobrar(int id){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Caja.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("La persona "+id+" terminó de pagar los articulos que compró");
    }
}
