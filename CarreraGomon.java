/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class CarreraGomon {

    private int bici, pos, pos2;
    private int h2, h;//cantidad de gomones dobles (h2) y cantidad de gomones simples (h)
    private Lock lock1 = new ReentrantLock();
    private Condition esperaBici = lock1.newCondition();
    private Lock lock2 = new ReentrantLock();
    private Condition esperaGomon2 = lock2.newCondition();
    private Lock lock3 = new ReentrantLock();
    private Condition esperaGomon = lock3.newCondition();
    private CyclicBarrier tren;
    private CyclicBarrier comienzoCarrera;
    private Exchanger intercambio;
    private ArrayBlockingQueue colBolsos;
    private ArrayBlockingQueue colBolsosAgente;
    private boolean largada = false;

    public CarreraGomon(int bici, int gomonesDob, int gomonesSim, int n, int b) {
        this.intercambio = new Exchanger();
        this.bici = bici;
        this.h2 = gomonesDob;
        this.h = gomonesSim;
        this.tren = new CyclicBarrier(n);
        this.colBolsos = new ArrayBlockingQueue(b);
        this.colBolsosAgente = new ArrayBlockingQueue(b);
        comienzoCarrera = new CyclicBarrier(h + (2 * h2));//la barrera se levanta cuando se llega a la cantidad de personas igual a 1 por cada gomon simple y dos por cada gomon doble
        //además la barrera de largada, se levanta cada cierto tiempo, por si no estan todos los participantes listos.

    }

    public void entrarEnBici(int id) {

        lock1.lock();
        try {
            while (bici <= 0) {
                try {
                    System.out.println("No hay bicis!!...");
                    esperaBici.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CarreraGomon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            bici--;
            System.out.println("la persona " + id + " salió en bici");
        } finally {
            lock1.unlock();
        }

    }

    public void entrarEnTren(int id) {
        try {

            tren.await(5, TimeUnit.SECONDS);//la barrera se levanta cada cierto tiempo...
        } catch (InterruptedException | BrokenBarrierException ex) {
        } catch (TimeoutException ex) {
            tren.reset();//resetea la barrera
            System.out.println("El tren salio, por tiempo, no está lleno");
        }

        System.out.println("La persona " + id + " salio en tren!! :D");
    }

    public void viajar() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(CarreraGomon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void salidaBici(int id) {

        lock1.lock();
        try {
            bici++;
            esperaBici.signalAll();
        } finally {
            lock1.unlock();
        }
    }

    public void tomaGomonDoble(int id) {
        lock2.lock();
        try {
            while (h2 <= 0) {
                try {
                    esperaGomon2.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CarreraGomon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            pos++;
            if (pos == 2) {
                h2--;
                pos = 0;//me aseguro que hallan dos personas por gomon
            }

        } finally {
            lock2.unlock();
        }
    }

    public void dejaGomonDoble(int id) {
        lock2.lock();
        try {
            pos2++;
            if (pos2 == 2) {
                pos2 = 0;
                h2++;
                esperaGomon2.signalAll();
            }
        } finally {
            lock2.unlock();
        }

    }

    public void tomaGomonSimple(int id) {
        lock3.lock();
        try {
            while (h <= 0) {
                try {
                    esperaGomon.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(CarreraGomon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            h--;
        } finally {
            lock3.unlock();
        }
    }

    public void dejaGomonSimple(int id) {
        lock3.lock();
        try {
            h++;
            esperaGomon.signalAll();
        } finally {
            lock3.unlock();
        }
    }

    public void salida(int id) {

        this.largada = false;
    }

    public void largada(int id) {
        try {
            comienzoCarrera.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException | BrokenBarrierException ex) {
        } catch (TimeoutException ex) {

            comienzoCarrera.reset();//resetea la barrera
            System.out.println("La carrera empezo antes de que estén todas las personas");
        }
        System.out.println("LARGADAA!!!");

        this.largada = true;
    }

    public void ponerBolso(Object bolso) {//este  metodo lo utiliza el agente para poner los bolsos creados, tambien lo utiliza la persona cuando ya no lo utiliza.
        try {
            colBolsos.put(bolso);
        } catch (InterruptedException ex) {
            Logger.getLogger(CarreraGomon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Object sacarBolso(int id) {//metodo que utiliza la persona para retirar un bolso.
        Object bolso = null;
        try {
            bolso = colBolsos.take();
        } catch (InterruptedException ex) {
            Logger.getLogger(CarreraGomon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bolso;
    }

    public Object intercambiar(Object objeto) {//intercambio de bolso por llave y viceversa
        Object obj = null;

        try {
            obj = intercambio.exchange(objeto);
        } catch (InterruptedException ex) {
            Logger.getLogger(CarreraGomon.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
        return obj;
    }
    public boolean getLargada() {
        return largada;
    }

}
