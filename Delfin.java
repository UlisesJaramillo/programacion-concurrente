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
public class Delfin implements Runnable{
    private int id;
    private NadoDelfines nado;
    
    public Delfin(int id,NadoDelfines d){
        this.id=id;
        this.nado=d;
        
    }
    
    public void run(){
        while(true){
            nado.entrarDelfin(id);
            nado.nadar();
            
        }
    }
}
