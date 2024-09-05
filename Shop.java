/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class Shop {
    private int contador=0,c,p;
    private Caja[] colCajas;
    private Lock mutex = new ReentrantLock();
    private Lock[] lockProd;
    private Condition[] esperaProd;
    private Lock repositor;
    private int[] colProductos;
    private Condition esperaRep;
    private boolean lleno=false;
    private int contador1;
    private Lock prod = new ReentrantLock();
    
    public Shop(int cj,int p){
        int i;
        this.c=cj;
        this.p=p;
        lockProd =new Lock[p];//arreglo de locks para acceder a cada producto
        esperaProd= new Condition[p];//conditions para la espera de adquisicion de productos.
        colCajas = new Caja[cj];//coleccion de cajas.
        colProductos = new int[p];//coleccion de productos
        repositor = new ReentrantLock();
        esperaRep= repositor.newCondition();
        for(i=0;i<c;i++){
            colCajas[i] = new Caja(i);
        }
        for(i=0;i<p;i++){
            colProductos[i]=20;
            lockProd[i]= new ReentrantLock();
            esperaProd[i]=lockProd[i].newCondition();
        }
        
    }
    
    
    public void entrar(int id){
        
            System.out.println("la persona "+id+" entro al Shop.");//no tiene limite de cantidad de personas
        
        
    }
    
    public void salir(int id){
        System.out.println("la persona "+id+" salió del shop");
    }
    
    public void tomarProductos(int id){
        prod.lock();
        int i;//variable local
        try {
            i = organizadorProd();
        } finally {
            prod.unlock();
        }
        lockProd[i].lock();
        try {
            while(colProductos[i]<=0){
                try {
                    repositor.lock();
                    try {
                        lleno=false;//variable compartida con el repositor
                        esperaRep.signal();//si no hay productos, avisa al repositor y luego espera.
                    System.out.println("no hay producto "+i);
                    } finally {
                        repositor.unlock();
                    }
                    esperaProd[i].await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            colProductos[i]--;
            System.out.println("la persona "+id+" tomo el producto "+i);
        } finally {
            lockProd[i].unlock();
        }
      
    }
    
    public void pagar(int id){
        int i;
        mutex.lock();
        try {
            i = organizador();
        } finally {
            mutex.unlock();
        }
        colCajas[i].entrar(id);
        colCajas[i].cobrar(id);
        colCajas[i].salir();
                
        
    }
    
    private int organizador(){
        //metodo para ordenar a las personas en las cajas.
        return this.contador = ((this.contador+1) % (this.c));//cicla entre 0 y c-1. ( c es la cantidad de cajas)
    }
    
    private int organizadorProd(){
        //metodo para ordenar a las personas en las cajas.
        return this.contador1 = ((this.contador1+1) % (this.p));//cicla entre 0 y p-1. ( p es la cantidad de productos)
    }
    
    public void reponer(){
        repositor.lock();
        try {
            while(lleno){
                try {
                    esperaRep.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Shop.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            int i;
            for(i=0;i<p;i++){
                if(colProductos[i]<=0){
                    lockProd[i].lock();
                    try {
                        colProductos[i]=100;//busca cual producto se acabó y lo repone
                        System.out.println("Reposicion del producto "+i);
                        esperaProd[i].signalAll();
                    } finally {
                        lockProd[i].unlock();
                    }
                }
            }
            lleno=true;
            
        } finally {
            repositor.unlock();
        }
    }
}
