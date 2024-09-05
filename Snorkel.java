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
public class Snorkel implements Runnable{
    private int id;
    private NadoSnorkel snk;
    
    public Snorkel(int id,NadoSnorkel s){
        this.id=id;
        this.snk=s;
    }
    
    
    
    
    public void run() {
        while (true) {
            snk.equipar(id);
            snk.busear(id);

        }
    }
}
