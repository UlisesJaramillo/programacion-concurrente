/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class Personas implements Runnable{
    private int id,turno;
    private EcoPcs eco;
    private Random random;
    private Pulsera p;
    private CyclicBarrier barrera;
    
    
    public Personas(int id,EcoPcs eco,CyclicBarrier barr){
        this.id=id;
        this.eco=eco;
        this.random=new Random();
        this.barrera=barr;
    }
    public int getTurno(){
        return this.turno;
    }
    
    public void setTurno(int t){
        this.turno=t;
    }
    
    public void run(){
        while(true){
            int i=0;
            if(random.nextBoolean()){
                System.out.println("La persona "+id+" decide ir en un Tour");
                
                try {
                    barrera.await();
                } catch (InterruptedException | BrokenBarrierException ex) {
                    Logger.getLogger(Personas.class.getName()).log(Level.SEVERE, null, ex);
                }finally{
                    System.out.println("La persona "+id+" Llegó en un Tour");
                }
                
            }
            p=eco.entrar(id);//obtiene una pulsera
            System.out.println("la persona" + id + " recibio una pulsera");
            this.turno=eco.obtenerTurno();
            if(random.nextBoolean()){//decide si va de compras o no.
                System.out.println("la persona" + id + "decidio ir de compras");
                eco.irComprar(id);
            }
            System.out.println("la persona" + id + "va a las atracciones");
            while(eco.esAbierto()){
                
                eco.actividades(id,i,this.turno);
            }
            System.out.println("La persona "+id+" Salió del parque");
            
        }
    }
}
