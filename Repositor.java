/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

/**
 *
 * @author Me
 */
public class Repositor implements Runnable{
    private EcoPcs eco;
    
    public Repositor(EcoPcs e){
        this.eco=e;
    }
    
    public void run(){
        while(true){
            eco.reponer();
        }
    }
    
}
