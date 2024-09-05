/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPO;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Me
 */
public class EcoPcs {

    private int hEntrada, hSalida, hActual;//estas variables controlan los horarios del parque.
    private Lock[] molinetes;//arreglo de locks para los molinetes.
    private Lock entrada = new ReentrantLock();// lock para la entrada al parque
    private Lock mutex = new ReentrantLock();
    private Lock mutex2 = new ReentrantLock();//estos locks controlan los diferentes accesos a datos compartidos entre los hilos "personas" de manera concurrente.
    private Condition esperaEntrada = entrada.newCondition();//cola de espera para la entrada al parque
    ///////////////////////////(creacion de las Actividades)
    private Pulsera pulsera;
    private Shop shop;
    private CarreraGomon carrera;
    private Restaurant[] restaurantes;
    private Faro faro;
    private NadoDelfines[] nadoDelfines;//arreglo que representa la cantidad de piletas.
    private NadoSnorkel[] nadoSnorkel;//arreglo que representa la cantidad de equipos disponibles
    private Delfin[] delfines;//arreglo de delfines que seran enviados como parametros a cada pileta
    /////////////////////////////////////////(hilos de cada equipo que participa en los buceos y los hilos de los delfines.)
    private Thread[] hilosDelfi;
    private Thread[] hilosEquipoRana;
    private Thread[] hilosEquipoSnorkel;
    private Thread[] hilosEquipoSalva;
    /////////////////////////////////////////////
    private int k,r,turnos,pil, eq;
    private Random random = new Random();
    
    private boolean esAbierto = false;
    private boolean listo = false;
    ////////////////////////////////////////////////(esta cantidad de contadores me sirve para llevar a cabo una organizacion dentro del parque, asi como el acceso a molinetes, restoranes,etc
    private int contador = 0,contador2 = 0,contador5 = 0,turnoAct;

    public EcoPcs(int hEnt, int hSal, int k, int r, int b, int pil, int eq, int turn) {
        //k es la cantidad de molinetes
        //r es la cantidad de restoranes
        //pil es la cantidad de piletas
        //eq es la cantidad de equipos de buseo
        this.r = r;
        this.hEntrada = hEnt;
        this.hSalida = hSal;
        this.hActual = 8;
        this.k = k;//indica la cantidad de molinetes que tiene el parque.
        this.turnos = turn;//cantidad de turnos en total
        this.turnoAct = 0;//turno actual
        this.pil = pil;
        this.eq = eq;
        molinetes = new Lock[k];//arreglo de locks para los molinetes
        int i;
        for (i = 0; i < k; i++) {
            molinetes[i] = new ReentrantLock(true);
        }
        ////////////////////////////////////////////////////

        shop = new Shop(2, 3);//cantidad de cajas =2 y cantidad de productos =3
        carrera = new CarreraGomon(20, 10, 15, 15, b);//cantidad de bicicletas,cantidad de gomones dobles,cantidad de gomones simples,capacidad del tren.
        restaurantes = new Restaurant[r];//arreglo de restoranes
        for (i = 0; i < r; i++) {

            restaurantes[i] = new Restaurant(i, 20, 20);
        }

        faro = new Faro(4, 5);//capacidad de la escalera, capacidad de la cola de espera.
        nadoDelfines = new NadoDelfines[pil];
        delfines = new Delfin[pil];//misma cantidad de delfines que de piletas.
        hilosDelfi = new Thread[pil];//arreglo de hilos para los delfines.
        nadoSnorkel = new NadoSnorkel[eq];
        hilosEquipoRana = new Thread[eq];
        hilosEquipoSnorkel = new Thread[eq];
        hilosEquipoSalva = new Thread[eq];
        for (i = 0; i < pil; i++) {
            if (i == 0) {
                nadoDelfines[i] = new NadoDelfines(i, 10, 2, 4);//pileta para 10 personas, tiempo de la barrera (seg), 4 turnos
                hilosDelfi[i] = new Thread(delfines[i] = new Delfin(i, nadoDelfines[i]));//se asigna el delfin correspondiente a cada pileta.
                hilosDelfi[i].start();
            } else {
                nadoDelfines[i] = new NadoDelfines(i, 10, 0, 4);//pileta para 10 personas, sin tiempo de barrera,4 turnos.
                hilosDelfi[i] = new Thread(delfines[i] = new Delfin(i, nadoDelfines[i]));
                hilosDelfi[i].start();
            }

        }
        for (i = 0; i < eq; i++) {

            nadoSnorkel[i] = new NadoSnorkel(i);//creacion de los equipos de buceo.
            hilosEquipoRana[i] = new Thread(new PatasRana(i, nadoSnorkel[i]));
            hilosEquipoSnorkel[i] = new Thread(new Snorkel(i, nadoSnorkel[i]));
            hilosEquipoSalva[i] = new Thread(new Salvavidas(i, nadoSnorkel[i]));
            hilosEquipoRana[i].start();
            hilosEquipoSnorkel[i].start();
            hilosEquipoSalva[i].start();
        }

        for (i = 0; i < r; i++) {

            restaurantes[i] = new Restaurant(i, 20, 20);//3 restoranes, con una capacidad de 20 personas y con una cola de espera de 20 personas
        }

    }

    public void reponer() {//metodo polimorfico
        this.shop.reponer();
    }

    public void ponerBolso(Bolso bolso) {
        carrera.ponerBolso(bolso);
    }//metodo polimorfico

    public Pulsera entrar(int id) {
        //metodo para entrar al parque y devuelve una pulsera
        int i;
        //controla que las personas entren de  9 a 17 hs
        entrada.lock();
        try {
            while (!((hEntrada <= hActual) && (hActual <= hSalida))) {
                try {
                    //si esta fuera de horario espera.
                    esperaEntrada.await();
                } catch (InterruptedException ex) {
                    Logger.getLogger(EcoPcs.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } finally {
            entrada.unlock();
        }
        mutex.lock();
        try {
            i = organizador();//metodo para generar un orden para que las personas pasen por los molinetes
        } finally {
            mutex.unlock();
        }

        molinetes[i].lock();
        try {
            pulsera = new Pulsera(id);//se crea una pulsera y se la asigna a la persona, tiene su mismo id
        } finally {
            molinetes[i].unlock();
        }
        System.out.println("la persona" + id + "entro al parque y paso por el molinete " + i);
        return pulsera;
    }

    public void salir(int id) {
        //metodo para salir del parque.
        System.out.println("La persona" + id + "salio del parque!");
    }

    public void irComprar(int id) {
        //metodo para ir a comprar al shop
        shop.entrar(id);
        shop.tomarProductos(id);
        shop.pagar(id);
        shop.salir(id);

    }

    private void irCarrera(int id) {
        //metodo para que una persona valla a las carreras de gomones
        System.out.println("la persona " + id + " entro a las carreras de gomones");

        if (random.nextBoolean()) {//elije si quiere viajar en bici o en tren
            System.out.println("la persona " + id + " tomó una bici");
            carrera.entrarEnBici(id);
            carrera.viajar();
            carrera.salidaBici(id);
        } else {
            System.out.println("la persona " + id + " tomó un Tren.");
            carrera.entrarEnTren(id);
            carrera.viajar();
        }

        Object bolso = carrera.sacarBolso(id);//saca un bolso para guardar sus pertenencias
        System.out.println("la persona " + id + " tomó un bolso");
        Object llave = carrera.intercambiar(bolso);//intercambia el bolso con la llave
        System.out.println("la persona " + id + " hizo un intercambio");
        mutex2.lock();
        int rand;//variable local
        try {
            rand = organizadorGomon();// aca se organiza a las personas para que ocupen 
        } finally {
            mutex2.unlock();
        }
        if (rand == 0) {//la primera persona en entrar, se le asigna un gomon dimple, las otras dos siguientes en gomones dobles.
            carrera.tomaGomonSimple(id);
            System.out.println("la persona " + id + " tomó un gomon simple");
        } else {
            carrera.tomaGomonDoble(id);
            System.out.println("la persona " + id + " tomó un gomon doble");
        }

        //correr
        carrera.largada(id);//acá esperan las personas para largar la carrera, se activa por tiempo.
        carrera.viajar();
        //terminar carrera
        carrera.ponerBolso(carrera.intercambiar(llave));//intercambia la llave por el bolso y luego devuelve el bolso
        System.out.println("la persona " + id + " hace el intercambio y devuelve el bolso");
        //deveuelven los gomones
        if (rand == 0) {
            carrera.dejaGomonSimple(id);
            System.out.println("la persona " + id + " dejó un gomon simple");
        } else {
            carrera.dejaGomonDoble(id);
            System.out.println("la persona " + id + " dejó un gomon doble");
        }
        carrera.salida(id);
        System.out.println("la persona" + id + " salio de las carreras de gomones");
    }

    private void irFaro(int id) {
        //metodo para ir a la actividad Faro
        System.out.println("la persona " + id + " entró al Faro");
        faro.entrarEscalera(id);
        faro.subir();
        faro.salirEscalera(id);
        faro.mirar();
        faro.hacerCola(id);
        faro.tirarseTobogan(id);
        System.out.println("la persona" + id + " salió del faro");
    }

    private int organizador() {
        //metodo para ordenar a las personas en los molinetes.
        return this.contador = ((this.contador + 1) % (this.k));//cicla entre 0 y k-1.
    }

    public int actividades(int id, int cont, int turno) {
        //metodo que controla todas las actividades en las que participan las personas.
        //el entero que retorna sirve para hacer un conteo de las veces que una persona ha comido.
        int nro = 0, nro2 = 0;

        if (this.esAbierto) {//hago varios if para que en cada de cierre del parque, la peronsa depues de que termine una actividad salga del parque inmediatamente, las actividades cierran a las 18hs
            //actividad delfines
            if (this.esAbierto) {//verifica si el parque está abierto
                if (this.turnoAct == turno) {//verifica si es su turno.
                    nro2 = random.nextInt(pil);
                    if (!nadoDelfines[nro2].verificarEntrada(id)) {//verifica la disponibilidad de la pileta

                        nadoDelfines[nro2].entrar(id);
                        nadoDelfines[nro2].nadar();
                        nadoDelfines[nro2].salir(id);

                    }
                }

            }
            if (this.esAbierto) {

                //actividad faro
                this.irFaro(id);
            }
            if (this.esAbierto) {
                //actividad gomones
                this.irCarrera(id);
            }
            if (this.esAbierto) {
                //actividad buseo
                nro2 = random.nextInt(eq);
                
                if (!nadoSnorkel[nro2].verificaEntrada()) {
                    
                    nadoSnorkel[nro2].entrar(id);
                    nadoSnorkel[nro2].busear(id);
                    nadoSnorkel[nro2].salir();
                    
                }
            }

            if (this.esAbierto) {

                if ((cont <= 2) && (11 <= hActual && hActual <= 15)) {//controla la entrada a los restoranes, solo se puede ir a comer entre las 11 y las 15hs
                    nro = random.nextInt(2);
                    System.out.println("la persona " + id + " entró a comer en el restaurant " + nro);

                    restaurantes[nro].entrar(id);
                    restaurantes[nro].hacerCola(id);
                    restaurantes[nro].comer(id);
                    restaurantes[nro].salir(id);

                    cont++;//este contador sirve para controlar que cada persona solo pueda comer hasta 2 veces nada más
                }
            }
        }
        return cont;
    }

    public void avanzaHora() {//metodo que controla el paso del tiempo
        this.hActual = (this.hActual + 1) % 24;
        if ((hEntrada < hActual) && (hActual < hSalida)) {
            entrada.lock();
            try {
                esperaEntrada.signalAll();
            } finally {
                entrada.unlock();
            }

        }
    }

    public int getHora() {
        return this.hActual;
    }

    public boolean esAbierto() {
        return esAbierto;
    }

    public void setEsAbierto(boolean n) {
        this.esAbierto = n;
    }

    public boolean getLargada() {//metodos polimorficos
        return carrera.getLargada();
    }

    public Object intercambiar(Object o) {//metodos polimorficos
        return carrera.intercambiar(o);
    }

    void viajar() {//metodos polimorficos
        carrera.viajar();
    }

    private int organizadorGomon() {
        return this.contador2 = ((this.contador2 + 1) % (3));//cicla entre 0 y 2.
    }

    public int obtenerTurno() {
        
        
        return random.nextInt(turnos);

    }

    public void avanzarTurno() {
        this.contador5 = ((this.contador5 + 1) % (turnos));//cicla entre 0 y la cantidad de turnos - 1.
        this.turnoAct = this.contador5;
    }

    public void setTurnoActual(int t) {
        this.turnoAct = t;
    }

    public int getTurnoActual() {
        return this.turnoAct;
    }
}
