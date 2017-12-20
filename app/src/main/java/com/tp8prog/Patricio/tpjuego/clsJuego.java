package com.tp8prog.Patricio.tpjuego;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.MotionEvent;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.ScaleBy;
import org.cocos2d.actions.interval.ScaleTo;
import org.cocos2d.layers.Layer;
import org.cocos2d.nodes.Director;
import org.cocos2d.nodes.Label;
import org.cocos2d.nodes.Scene;
import org.cocos2d.nodes.Sprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.types.CCColor3B;
import org.cocos2d.types.CCSize;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class clsJuego  {

    private CCGLSurfaceView _VistaDelJuego;
    private CCSize DeviceDisplay;
    Sprite Astronauta, backgroundImage;
    Sprite Planeta, Roca, Roca1;
    float ScreenHeight, ScreenWidth;
    float PlanetaHeight, RocaHeight, Roca1Heigth;
    float getInitialTouchX, getInitialTouchY;
    float getFinalTouchX, getFinalTouchY;
    float Carril1, Carril2, Carril3;
    int PlanetaCont = 0;
    int RocaCont = 0;
    int Roca1Cont = 0;
    int Puntuacion = 0;
    int segundos = 0;
    ArrayList<Sprite> arrayPlaneta;
    ArrayList<Sprite> arrayRoca;
    ArrayList<Sprite> arrayRoca1;
    Label lblPerdiste;
    Label lblpuntaje, lblsegundos;
    boolean boolRecorriendoPlanetas;
    boolean boolRecorriendoRocas;
    boolean boolRecorriendoRocas1;
    boolean playing;
    Timer Relojsegundos;
    Timer timLimpiarPlanetas;
    Timer timPonerPlaneta;
    Timer timCheckImpact;
    MediaPlayer miMusica;
    Context _Contexto;

    public clsJuego(CCGLSurfaceView VistaDelJuego, Context contexto) {
        _VistaDelJuego = VistaDelJuego;
        _Contexto = contexto;
    }

    public void ComenzarJuego() {
        Director.sharedDirector().attachInView(_VistaDelJuego);
        DeviceDisplay = Director.sharedDirector().displaySize();

        ScreenHeight = DeviceDisplay.getHeight();
        ScreenWidth = DeviceDisplay.getWidth();

        boolRecorriendoPlanetas = false;

        Director.sharedDirector().runWithScene(EscenaDelJuego());
    }

    private Scene EscenaDelJuego() {
        Scene EscenaADevolver;
        EscenaADevolver = Scene.node();
        capafondo MiCapaFondo;
        MiCapaFondo = new capafondo();
        capafrente MiCapaFrente;
        MiCapaFrente = new capafrente();

        EscenaADevolver.addChild(MiCapaFondo, -10);
        EscenaADevolver.addChild(MiCapaFrente, 10);

        return EscenaADevolver;
    }

    class capafrente extends Layer {

        public capafrente() {
            playing = true;

            Relojsegundos=new Timer();
            timLimpiarPlanetas =new Timer();
            timPonerPlaneta = new Timer();
            timCheckImpact =new Timer();

            ponerAstronauta();
            this.setIsTouchEnabled(true);
            arrayPlaneta = new ArrayList<Sprite>();
            arrayRoca = new ArrayList<Sprite>();
            arrayRoca1 = new ArrayList<Sprite>();
            miMusica = MediaPlayer.create(_Contexto, R.raw.music);
            miMusica.start();
            miMusica.setVolume(0.5f, 0.5f);
            miMusica.setLooping(true);
            TimerTask puntosPorTiempo=new TimerTask() {
                @Override
                public void run() {

                    segundos++;
                    ponerlblpuntaje(segundos);
                }
            };

            Relojsegundos.schedule(puntosPorTiempo,0,1000);

            TimerTask taskRemoverPlanetas=new TimerTask() {
                @Override
                public void run() {
                    VerifyArray(arrayPlaneta, PlanetaHeight);
                    VerifyArray(arrayRoca,RocaHeight);

                }
            };

            timLimpiarPlanetas.schedule(taskRemoverPlanetas,0,100);
            TimerTask taskNuevoPlaneta;
            taskNuevoPlaneta =new TimerTask() {
                @Override
                public void run() {
                    nuevoPlaneta();
                    PlanetaCont++;

                    Log.d("PlanetCont", " tengo   " + PlanetaCont + " planetas");
                }
            };

            TimerTask taskPonerRoca;

            taskPonerRoca = new TimerTask() {
                @Override
                public void run() {
                    nuevaRoca();
                    RocaCont++;

                    Log.d("Rocas", " tengo " + RocaCont + " rocas");
                }
            };

            TimerTask taskPonerRoca1;

            taskPonerRoca1 = new TimerTask() {
                @Override
                public void run() {
                    nuevaRoca1();
                    Roca1Cont++;

                    Log.d("Rocas", " tengo " + Roca1Cont + " rocas");
                }
            };

            if (PlanetaCont <= 10 && RocaCont <= 10 && Roca1Cont <= 10) {
                timPonerPlaneta.schedule(taskNuevoPlaneta, 0, 1500);
                timPonerPlaneta.schedule(taskPonerRoca, 0 , 2000);
                timPonerPlaneta.schedule(taskPonerRoca1, 0 , 2000);
            } else if (PlanetaCont >= 10 && PlanetaCont <= 30 && RocaCont >= 10 && RocaCont <= 30 && Roca1Cont >= 10 && Roca1Cont <= 30) {
                timPonerPlaneta.schedule(taskNuevoPlaneta, 0, 900);
                timPonerPlaneta.schedule(taskPonerRoca, 0, 2000);
                timPonerPlaneta.schedule(taskPonerRoca1, 0 , 2000);
            } else {
                timPonerPlaneta.schedule(taskNuevoPlaneta, 0, 700);
                timPonerPlaneta.schedule(taskPonerRoca, 0, 2000);
                timPonerPlaneta.schedule(taskPonerRoca1, 0 , 2000);
            }

            TimerTask verificarImpactos;
            verificarImpactos = new TimerTask() {
                @Override
                public void run() {

                    detectarChoque(arrayPlaneta,"Planetas");
                    detectarChoque(arrayRoca,"Rocas");
                    detectarChoque(arrayRoca1,"Rocas");

                }
            };

            timCheckImpact.schedule(verificarImpactos,0,100);
        }

        @Override
        public boolean ccTouchesBegan(MotionEvent event)
        {
            getInitialTouchX = event.getX();
            getInitialTouchY = DeviceDisplay.getHeight() - event.getY();

            return true;
        }
        @Override
        public boolean ccTouchesMoved(MotionEvent event) {

            return true;
        }
        @Override
        public boolean ccTouchesEnded(MotionEvent event) {

            if(!playing) {
                super.removeChild(Astronauta, true);
                super.removeChild(lblPerdiste, true);
                arrayPlaneta.clear();
                arrayRoca.clear();
                super.removeChild(lblpuntaje, true);
                super.removeChild(Planeta, true);
                super.removeChild(Roca, true);
                super.removeChild(Roca1, true);

                PlanetaCont = 0;
                RocaCont = 0;
                Roca1Cont = 0;
                Puntuacion = 0;
                segundos = 0;

                super.removeChild(lblPerdiste, true);

                playing = true;

                Relojsegundos = new Timer();
                timLimpiarPlanetas = new Timer();
                timPonerPlaneta = new Timer();
                timCheckImpact = new Timer();

                ponerAstronauta();
                this.setIsTouchEnabled(true);
                arrayPlaneta = new ArrayList<Sprite>();
                arrayRoca = new ArrayList<Sprite>();
                arrayRoca1 = new ArrayList<Sprite>();
                TimerTask tareasumarsegundo = new TimerTask() {
                    @Override
                    public void run() {

                        segundos++;
                        ponerlblpuntaje(segundos);
                    }
                };

                Relojsegundos.schedule(tareasumarsegundo, 0, 1000);

                TimerTask limpiarPlanetas = new TimerTask() {
                    @Override
                    public void run() {
                        VerifyArray(arrayPlaneta, PlanetaHeight);
                        VerifyArray(arrayRoca, RocaHeight);
                        VerifyArray(arrayRoca1, RocaHeight);
                    }
                };

                timLimpiarPlanetas.schedule(limpiarPlanetas, 0, 100);

                TimerTask taskPonerPlaneta;

                taskPonerPlaneta = new TimerTask() {
                    @Override
                    public void run() {
                        nuevoPlaneta();
                        PlanetaCont++;

                        Log.d("NewPlanet", " tengo   " + PlanetaCont + " planetas");
                    }
                };

                TimerTask taskNuevaRoca;

                taskNuevaRoca = new TimerTask() {
                    @Override
                    public void run() {
                        nuevaRoca();
                        RocaCont++;

                        Log.d("Roca2", " tengo " + RocaCont + " rocas");

                    }
                };

                TimerTask taskNuevaRoca1;

                taskNuevaRoca1 = new TimerTask() {
                    @Override
                    public void run() {
                        nuevaRoca1();
                        Roca1Cont++;

                        Log.d("Roca2", " tengo " + Roca1Cont + " rocas");

                    }
                };

                if (PlanetaCont <= 10 && RocaCont <= 10 && Roca1Cont <= 10) {
                    timPonerPlaneta.schedule(taskPonerPlaneta, 0, 2500);
                    timPonerPlaneta.schedule(taskNuevaRoca, 0, 1500);
                    timPonerPlaneta.schedule(taskNuevaRoca1, 0, 1500);
                } else if (PlanetaCont >= 10 && PlanetaCont <= 30  && RocaCont >= 10 && RocaCont <= 30) {
                    timPonerPlaneta.schedule(taskPonerPlaneta, 0, 2000);
                    timPonerPlaneta.schedule(taskNuevaRoca, 0, 1000);
                    timPonerPlaneta.schedule(taskNuevaRoca1, 0, 1000);
                } else {
                    timPonerPlaneta.schedule(taskPonerPlaneta, 0, 1500);
                    timPonerPlaneta.schedule(taskNuevaRoca, 0, 650);
                    timPonerPlaneta.schedule(taskNuevaRoca1, 0, 650);
                }

                TimerTask verificarImpactos;
                verificarImpactos = new TimerTask() {
                    @Override
                    public void run() {

                        detectarChoque(arrayPlaneta,"Planetas");
                        detectarChoque(arrayRoca,"Rocas");
                        detectarChoque(arrayRoca1,"Rocas");
                    }
                };

                timCheckImpact.schedule(verificarImpactos, 0, 100);

            }
            getFinalTouchX = event.getX();
            getFinalTouchY = DeviceDisplay.getHeight() - event.getY();
            Carril1 = DeviceDisplay.width/2 - 190;
            Carril2 = DeviceDisplay.width/2 - 10;
            Carril3 = DeviceDisplay.width/2 +180;
            if (Astronauta.getPositionX() == Carril1)
            {
                if (getFinalTouchX > getInitialTouchX){
                    Astronauta.runAction(MoveTo.action(0.1f, Carril2,100));
                }
            }
            if (Astronauta.getPositionX() == Carril2)
            {
                if (getFinalTouchX > getInitialTouchX){
                    Astronauta.runAction(MoveTo.action(0.1f, Carril3,100));
                }
                if (getFinalTouchX < getInitialTouchX){
                    Astronauta.runAction(MoveTo.action(0.1f, Carril1,100));
                }
            }
            if (Astronauta.getPositionX() == Carril3)
            {
                if (getFinalTouchX < getInitialTouchX){
                    Astronauta.runAction(MoveTo.action(0.1f, Carril2,100));
                }
            }
            return true;
        }

        private void lblPerdiste() {
            lblPerdiste = Label.label("Perdiste!" + " Toque para reiniciar", "Verdana", 45);
            CCColor3B color = new CCColor3B(100,5000, 0);
            lblPerdiste.setColor(color);
            lblPerdiste.setPosition(DeviceDisplay.width/2, DeviceDisplay.height /2);

            super.addChild(lblPerdiste);
        }
        private void ponerlblpuntaje(int PuntajeASumar){
            super.removeChild(lblpuntaje, true);
            Puntuacion = Puntuacion + PuntajeASumar;
            lblpuntaje = Label.label("Puntuación: " + Puntuacion, "Verdana", 60);
            CCColor3B color = new CCColor3B(100,5000, 0);
            lblpuntaje.setColor(color);
            Float puntajePos;
            puntajePos= lblpuntaje.getHeight();
            lblpuntaje.setPosition(DeviceDisplay.width/2, DeviceDisplay.height - puntajePos/2);

            super.addChild(lblpuntaje);
        }

        private void ponerAstronauta(){
            Astronauta = Sprite.sprite("astronauta.png");
            Astronauta.setPosition((ScreenWidth/2)-10 , 100);

            super.addChild(Astronauta);
        }

        void nuevoPlaneta(){
            Planeta = Sprite.sprite("planeta1.png");
            int posInicialX , posInicialY;

            PlanetaHeight = Planeta.getHeight();
            posInicialY = (int) (DeviceDisplay.getHeight() + PlanetaHeight /2);

            Random  azar;
            azar = new Random();
            int auxiliar;
            auxiliar = azar.nextInt(3);

            if (auxiliar == 0){
                posInicialX = (int) (DeviceDisplay.width/2 - 190);
            }else if (auxiliar ==1){
                posInicialX = (int) (DeviceDisplay.width/2 - 10);
            } else{
                posInicialX = (int) (DeviceDisplay.width/2 +180);
            }

            Planeta.setPosition(posInicialX, posInicialY);
            int posFinalX, posFinalY;
            posFinalX = posInicialX;
            posFinalY = (int) -PlanetaHeight/2;
            if (PlanetaCont <20){
                Planeta.runAction(MoveTo.action(5, posFinalX, posFinalY));
            }
            else if (PlanetaCont >=20 && PlanetaCont <= 40){
                Planeta.runAction(MoveTo.action(4, posFinalX, posFinalY));
            }
            else {
                Planeta.runAction(MoveTo.action(3, posFinalX, posFinalY));
            }
            arrayPlaneta.add(Planeta);
            Log.d("nuevoPlaneta","Hay "+ arrayPlaneta.size()+ "meteoritos");
            super.addChild(Planeta);

        }

        void nuevaRoca(){

            Roca = Sprite.sprite("Roca.png");
            Roca.runAction(ScaleTo.action(0.02f));
            int posInicialX, posInicialY;

            float RocaHeight = Roca.getHeight();
            posInicialY = (int) (DeviceDisplay.getHeight() + RocaHeight/2);

            Random  azar;
            azar = new Random();
            int auxiliar;
            auxiliar = azar.nextInt(3);

            if (auxiliar == 0)
            {
                posInicialX = (int) (DeviceDisplay.width/2 - 190);

            }else if (auxiliar ==1)
            {
                posInicialX = (int) (DeviceDisplay.width/2 - 10);

            }
            else{
                posInicialX = (int) (DeviceDisplay.width/2 +180);
            }

            Roca.setPosition(posInicialX, posInicialY);
            int posFinalX, posFinalY;
            posFinalX = posInicialX;
            posFinalY = (int) - RocaHeight/2;
            if (PlanetaCont <20){
                Roca.runAction(MoveTo.action(5, posFinalX, posFinalY));
            }
            else if (PlanetaCont >=20 && PlanetaCont <= 40)
            {
                Roca.runAction(MoveTo.action(4, posFinalX, posFinalY));
            }
            else
            {
                Roca.runAction(MoveTo.action(3, posFinalX, posFinalY));
            }
            arrayRoca.add(Roca);
            Log.d("PongoRoca","Hay "+arrayRoca.size()+ " Rocas" );
            super.addChild(Roca);

        }

        void nuevaRoca1(){

            Roca1 = Sprite.sprite("Roca1.png");
            Roca1.runAction(ScaleTo.action(0.02f));
            int posInicialX, posInicialY;

            float RocaHeight = Roca1.getHeight();
            posInicialY = (int) (DeviceDisplay.getHeight() + RocaHeight/2);

            Random  azar;
            azar = new Random();
            int auxiliar;
            auxiliar = azar.nextInt(3);

            if (auxiliar == 0)
            {
                posInicialX = (int) (DeviceDisplay.width/2 - 190);

            }else if (auxiliar ==1)
            {
                posInicialX = (int) (DeviceDisplay.width/2 - 10);

            }
            else{
                posInicialX = (int) (DeviceDisplay.width/2 +180);
            }

            Roca1.setPosition(posInicialX, posInicialY);
            int posFinalX, posFinalY;
            posFinalX = posInicialX;
            posFinalY = (int) - RocaHeight/2;
            if (PlanetaCont <20){
                Roca1.runAction(MoveTo.action(5, posFinalX, posFinalY));
            }
            else if (PlanetaCont >=20 && PlanetaCont <= 40)
            {
                Roca1.runAction(MoveTo.action(4, posFinalX, posFinalY));
            }
            else
            {
                Roca1.runAction(MoveTo.action(3, posFinalX, posFinalY));
            }
            arrayRoca1.add(Roca1);
            Log.d("PongoRoca1","Hay "+arrayRoca1.size()+ " Rocas1" );
            super.addChild(Roca1);

        }

        boolean EstaEntre (int NumeroAComparar, int NumeroMenor, int NumeroMayor){
            boolean Devolver;

            if(NumeroMenor > NumeroMayor) {
                int auxiliar;
                auxiliar = NumeroMayor;
                NumeroMayor = NumeroMenor;
                NumeroMenor = auxiliar;
            }
            if (NumeroAComparar >= NumeroMenor && NumeroAComparar <= NumeroMayor){
                Devolver = true;
            }
            else{
                Devolver = false;
            }
            return  Devolver;

        }
        boolean InterseccionEntreSprites (Sprite Sprite1, Sprite Sprite2) {
            boolean Response;
            Response = false;
            int Sprite1Izquierda, Sprite1Derecha, Sprite1Abajo, Sprite1Arriba;
            int Sprite2Izquierda, Sprite2Derecha, Sprite2Abajo, Sprite2Arriba;
            Sprite1Izquierda=(int) (Sprite1.getPositionX() - Sprite1.getWidth()/2);
            Sprite1Derecha=(int) (Sprite1.getPositionX() + Sprite1.getWidth()/2);
            Sprite1Abajo=(int) (Sprite1.getPositionY() - Sprite1.getHeight()/2);
            Sprite1Arriba=(int) (Sprite1.getPositionY() + Sprite1.getHeight()/2);
            Sprite2Izquierda=(int) (Sprite2.getPositionX() - Sprite2.getWidth()/2);
            Sprite2Derecha=(int) (Sprite2.getPositionX() + Sprite2.getWidth()/2);
            Sprite2Abajo=(int) (Sprite2.getPositionY() - Sprite2.getHeight()/2);
            Sprite2Arriba=(int) (Sprite2.getPositionY() + Sprite2.getHeight()/2);
            Log.d("Interseccion", "Sp1 - Izq: "+Sprite1Izquierda+" - Der: "+Sprite1Derecha+" - Aba:"
                    +Sprite1Abajo+" - Arr: "+Sprite1Arriba);
            Log.d(";Interseccion", "Sp2 - Izq: "+Sprite2Izquierda+" - Der: " +Sprite2Derecha+" - Aba:"
                    +Sprite2Abajo+" - Arr:" +Sprite2Arriba);
//Borde izq y borde inf de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Izquierda, Sprite2Izquierda, Sprite2Derecha) &&
                    EstaEntre(Sprite1Abajo, Sprite2Abajo, Sprite2Arriba)) {

                Response=true;
            }
//Borde izq y borde sup de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Izquierda, Sprite2Izquierda, Sprite2Derecha) &&
                    EstaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {

                Response=true;
            }
//Borde der y borde sup de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) && EstaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {

                Response=true;
            }
//Borde der y borde inf de Sprite 1 está dentro de Sprite 2
            if (EstaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) && EstaEntre(Sprite1Abajo, Sprite2Abajo, Sprite2Arriba)) {

                Response=true;
            }
//Borde izq y borde inf de Sprite 2 está dentro de Sprite 1
            if (EstaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) && EstaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {

                Response=true;
            }
//Borde izq y borde sup de Sprite 1 está dentro de Sprite 1
            if (EstaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) && EstaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {

                Response=true;
            }
//Borde der y borde sup de Sprite 2 está dentro de Sprite 1
            if (EstaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) && EstaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {

                Response=true;
            }
//Borde der y borde inf de Sprite 2 está dentro de Sprite 1
            if (EstaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) && EstaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {

                Response=true;
            }
            return Response;
        }
        void detectarChoque(ArrayList<Sprite> arrayList, String Objeto){

            Log.d ("detectarChoque", "verifico los " + arrayPlaneta.size() +" planetas");
            boolean Choque;
            Choque = false;
            Sprite Eliminado = Sprite.sprite("planeta1.png");

            if (!boolRecorriendoPlanetas) {
                boolRecorriendoPlanetas =true;
                for (int i = 0; i < arrayList.size(); i++) {

                    if (InterseccionEntreSprites(Astronauta, arrayList.get(i))) {
                        if(Objeto.equals("Planetas")){
                            ponerlblpuntaje(100);
                            super.removeChild(arrayList.get(i),true);
                        }else{
                            Choque = true;
                            playing = false;
                            lblPerdiste();
                            super.removeChild(lblpuntaje, true);
                            Relojsegundos.cancel();
                            timLimpiarPlanetas.cancel();
                            timPonerPlaneta.cancel();
                            timCheckImpact.cancel();
                        }
                    }
                }
                arrayList.remove(Eliminado);
            }
            boolRecorriendoPlanetas =false;
        }
        void VerifyArray(ArrayList<Sprite> ArraySprites, float altura){

            ArrayList<Sprite> spriteAElminiar=new ArrayList<>();
            int FinalPos =  (int) - altura/2;
            if (!boolRecorriendoPlanetas) {
                boolRecorriendoPlanetas = true;
                for (Sprite SpriteAVerificar : ArraySprites) {
                    if (SpriteAVerificar.getPositionY() == FinalPos) {
                        spriteAElminiar.add(SpriteAVerificar);
                    }
                }
            }
            boolRecorriendoPlanetas =false;

            for (Sprite Planeta:spriteAElminiar) {
                removerPlaneta(Planeta, ArraySprites);
            }
        }

        public void removerPlaneta(Sprite planetaAEliminar, ArrayList<Sprite> ArraySprites )
        {
            if (!boolRecorriendoPlanetas) {
                boolRecorriendoPlanetas = true;
                super.removeChild(planetaAEliminar, true);
                Log.d("meteEnArray","Hay: "+ArraySprites.size());
                ArraySprites.remove(planetaAEliminar);

                Log.d("meteEnArray","Ahora quedan: "+ArraySprites.size());
            }
            boolRecorriendoPlanetas = false;
        }
    }

    class capafondo extends Layer {
        public capafondo() {
            backgroundImage = Sprite.sprite("background.jpg");

            backgroundImage.setPosition(ScreenWidth / 2, ScreenHeight / 2);
            backgroundImage.setPosition(DeviceDisplay.width/2, DeviceDisplay.height/2);
            backgroundImage.runAction(ScaleBy.action(0.01f,2.f,2.0f));
            super.addChild(backgroundImage);
        }
    }
}