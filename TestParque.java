/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Me
 */
public class TestParque {

    //main del parque
    public static void main(String[] args) {
        int i, n = 100, b = 60,r=3,m=5,pil=4,eq=5,turnos=4;//n es la cantidad de personas, b es la cantidad de bolsos, r es la cantidad de restaurantes, m es la cantidad de molinetes, pil es la cantidad de piletas
                                                            //eq es la cantidad de equipos de buceo y turnos es la cantidad de turnos en que las personas pueden elegir para nadar con los delfines.
        CyclicBarrier tour = new CyclicBarrier(25);// esta barrera simula el viaje en tour en la cual pueden acceder las personas.
        EcoPcs parque = new EcoPcs(9, 17, m, r,b,pil,eq,turnos);//horario de 9 a 17.

        Personas[] personas = new Personas[n];

        Thread[] hilosPersonas = new Thread[n];

        ControlTiempo ctrlTiempo = new ControlTiempo(parque);

        Thread hiloTiempo = new Thread(ctrlTiempo);

        Repositor repositor = new Repositor(parque);

        Thread hiloRepo = new Thread(repositor);

        Agente agente = new Agente(parque, b);

        Thread hiloAgente = new Thread(agente);


        hiloTiempo.start();
        hiloRepo.start();
        hiloAgente.start();


        for (i = 0; i < n; i++) {
            hilosPersonas[i] = new Thread(new Personas(i, parque, tour));
            hilosPersonas[i].start();
        }

    }
}
