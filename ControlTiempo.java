/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class ControlTiempo implements Runnable{
    private EcoPcs eco;
    
    public ControlTiempo(EcoPcs e){
        this.eco=e;
    }
    
    public void run(){
        while(true){
            try {
                Thread.sleep(5000);//tiempo que transcurre entre cada hora
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlTiempo.class.getName()).log(Level.SEVERE, null, ex);
            }
            eco.avanzaHora();
            eco.avanzarTurno();
            
            if(18<eco.getHora()||eco.getHora()<9){//cierra el acceso a las actividades.
                eco.setEsAbierto(false);
            }else{
                eco.setEsAbierto(true);
            }
            System.out.println("La hora actual es: "+eco.getHora());
            System.out.println("El turno actual es: "+eco.getTurnoActual());
        }
    }
}
