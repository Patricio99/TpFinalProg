package com.tp8prog.Patricio.tpjuego;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import org.cocos2d.actions.interval.MoveTo;
import org.cocos2d.actions.interval.ScaleBy;
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
    private CCSize PantallaDelDispositivo;
    Sprite CoheteUsuario, Imagenfondo;
    Sprite Meteorito;
    float AltoPantalla, AnchoPantalla;
    int contadormeteoritos=0;
    ArrayList<Sprite> arrMeteorit;
    float alturameteorito;
    Label lblchoque;
    float posicioninicialtocoX, posicioninicialtocoY;
    float posicionfinaltocoX, posicionfinaltocoY;
    boolean estoyRecorriendoMeteoritos;
    int puntaje = 0;
    Label lblpuntaje, lblsegundos;
    int segundos  = 0;
    boolean estajugando;
    Timer Relojsegundos;
    Timer relojEliminarMeteorit;
    Timer relojponeMeteorit ;
    Timer relojverificarimpacto ;
    Context _Contexto;

    public clsJuego(CCGLSurfaceView VistaDelJuego, Context contextop) {
        _VistaDelJuego = VistaDelJuego;
        _Contexto = contextop;
    }

    public void ComenzarJuego() {
        Director.sharedDirector().attachInView(_VistaDelJuego);

        PantallaDelDispositivo = Director.sharedDirector().displaySize();


        AltoPantalla = PantallaDelDispositivo.getHeight();
        AnchoPantalla = PantallaDelDispositivo.getWidth();


        estoyRecorriendoMeteoritos= false;




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
            estajugando =true;

            Relojsegundos=new Timer();
            relojEliminarMeteorit=new Timer();
            relojponeMeteorit = new Timer();
            relojverificarimpacto =new Timer();

            ponercoheteposicionInicial();
            this.setIsTouchEnabled(true);
            arrMeteorit = new ArrayList<Sprite>();
            TimerTask tareasumarsegundo=new TimerTask() {
                @Override
                public void run() {

                    segundos++;
                    ponerlblpuntaje(10);
                }
            };


            Relojsegundos.schedule(tareasumarsegundo,0,1000);


            TimerTask tareaBorrarmeteoritos=new TimerTask() {
                @Override
                public void run() {
                    verificarsilosacodelarray(arrMeteorit,alturameteorito);
                }
            };


            relojEliminarMeteorit.schedule(tareaBorrarmeteoritos,0,100);


            TimerTask tareaponermeteorit;

            tareaponermeteorit =new TimerTask() {
                @Override
                public void run() {

                    ponermeteorit();
                    contadormeteoritos++;

                    Log.d("meteoritos2", " tengo   " + contadormeteoritos + " meteoritos");

                }
            };




            if (contadormeteoritos <= 10) {
                relojponeMeteorit.schedule(tareaponermeteorit, 0, 1500);
            } else if (contadormeteoritos >= 10 && contadormeteoritos <= 30) {
                relojponeMeteorit.schedule(tareaponermeteorit, 0, 900);
            } else {
                relojponeMeteorit.schedule(tareaponermeteorit, 0, 700);
            }

            TimerTask verificarimpactos;
            verificarimpactos = new TimerTask() {
                @Override
                public void run() {


                    detectarchoque(arrMeteorit,"Meteoritos");

                }
            };



            relojverificarimpacto.schedule(verificarimpactos,0,100);



        }
        @Override
        public boolean ccTouchesBegan(MotionEvent event)
        {

            posicioninicialtocoX = event.getX();
            posicioninicialtocoY = PantallaDelDispositivo.getHeight() - event.getY();

            return true;
        }
        @Override
        public boolean ccTouchesMoved(MotionEvent event) {


            return true;
        }
        @Override
        public boolean ccTouchesEnded(MotionEvent event) {

            if(!estajugando) {
                super.removeChild(CoheteUsuario, true);
                super.removeChild(lblchoque, true);
                arrMeteorit.clear();
                super.removeChild(lblpuntaje, true);
                super.removeChild(Meteorito, true);

                contadormeteoritos = 0;
                puntaje = 0;
                segundos = 0;


                super.removeChild(lblchoque, true);


                estajugando = true;

                Relojsegundos = new Timer();
                relojEliminarMeteorit = new Timer();
                relojponeMeteorit = new Timer();
                relojverificarimpacto = new Timer();

                ponercoheteposicionInicial();
                this.setIsTouchEnabled(true);
                arrMeteorit = new ArrayList<Sprite>();
                TimerTask tareasumarsegundo = new TimerTask() {
                    @Override
                    public void run() {

                        segundos++;
                        ponerlblpuntaje(10);
                    }
                };


                Relojsegundos.schedule(tareasumarsegundo, 0, 1000);


                TimerTask tareaBorrarmeteoritos = new TimerTask() {
                    @Override
                    public void run() {
                        verificarsilosacodelarray(arrMeteorit,alturameteorito);
                    }
                };


                relojEliminarMeteorit.schedule(tareaBorrarmeteoritos, 0, 100);


                TimerTask tareaponermeteorit;

                tareaponermeteorit = new TimerTask() {
                    @Override
                    public void run() {

                        ponermeteorit();
                        contadormeteoritos++;

                        Log.d("meteoritos2", " tengo   " + contadormeteoritos + " meteoritos");

                    }
                };


                if (contadormeteoritos <= 10) {
                    relojponeMeteorit.schedule(tareaponermeteorit, 0, 1500);
                } else if (contadormeteoritos >= 10 && contadormeteoritos <= 30) {
                    relojponeMeteorit.schedule(tareaponermeteorit, 0, 900);
                } else {
                    relojponeMeteorit.schedule(tareaponermeteorit, 0, 700);
                }

                TimerTask verificarimpactos;
                verificarimpactos = new TimerTask() {
                    @Override
                    public void run() {

                        detectarchoque(arrMeteorit,"Meteoritos");

                    }
                };


                relojverificarimpacto.schedule(verificarimpactos, 0, 100);

            }
            posicionfinaltocoX = event.getX();
            posicionfinaltocoY = PantallaDelDispositivo.getHeight() - event.getY();
            float supuestocarril1,supuestocarril2,supuestocarril3;
            supuestocarril1 = PantallaDelDispositivo.width/2 - 190;
            supuestocarril2 = PantallaDelDispositivo.width/2 - 10;
            supuestocarril3=        PantallaDelDispositivo.width/2 +180;
            if (CoheteUsuario.getPositionX() == supuestocarril1 )
            {
                if (posicionfinaltocoX > posicioninicialtocoX ){
                    CoheteUsuario.runAction(MoveTo.action(0.1f,supuestocarril2,100));
                }
            }
            if (CoheteUsuario.getPositionX() == supuestocarril2 )
            {
                if (posicionfinaltocoX > posicioninicialtocoX ){
                    CoheteUsuario.runAction(MoveTo.action(0.1f,supuestocarril3,100));
                }
                if (posicionfinaltocoX < posicioninicialtocoX ){
                    CoheteUsuario.runAction(MoveTo.action(0.1f,supuestocarril1,100));
                }
            }
            if (CoheteUsuario.getPositionX() == supuestocarril3 )
            {
                if (posicionfinaltocoX < posicioninicialtocoX ){
                    CoheteUsuario.runAction(MoveTo.action(0.1f,supuestocarril2,100));
                }
            }
            return true;


        }

        private void ponerlblchoque() {

            lblchoque = Label.label("CHOCASTE, Toque para volver a jugar", "Verdana", 45);
            CCColor3B color = new CCColor3B(100,5000, 0);
            lblchoque.setColor(color);
            Float altodeltitulo;
            altodeltitulo= lblchoque.getHeight();
            lblchoque.setPosition(PantallaDelDispositivo.width/2, PantallaDelDispositivo.height /2);
            super.addChild(lblchoque);


        }



        private void ponerlblpuntaje(int PuntajeASumar){
            super.removeChild(lblpuntaje, true);
            puntaje = puntaje +PuntajeASumar;
            lblpuntaje = Label.label("PUNTAJE:  " + puntaje, "Verdana", 85);
            CCColor3B color = new CCColor3B(100,5000, 0);
            lblpuntaje.setColor(color);
            Float altodelpuntaje;
            altodelpuntaje= lblpuntaje.getHeight();
            lblpuntaje.setPosition(PantallaDelDispositivo.width/2, PantallaDelDispositivo.height - altodelpuntaje/2);
            super.addChild(lblpuntaje);


        }
        private void ponercoheteposicionInicial(){
            CoheteUsuario = Sprite.sprite("cohete.png");

            CoheteUsuario.setPosition((AnchoPantalla/2)-10 , 100);
            super.addChild(CoheteUsuario);

        }

        void ponermeteorit(){

            Meteorito = Sprite.sprite("meteorito.png");
            int posicionInicialX , posicioninicialY;

            alturameteorito = Meteorito.getHeight();
            posicioninicialY = (int) (PantallaDelDispositivo.getHeight() + alturameteorito/2);

            Random  azar;
            azar = new Random();
            int auxiliar;
            auxiliar = azar.nextInt(3);

            if (auxiliar == 0)
            {
                posicionInicialX = (int) (PantallaDelDispositivo.width/2 - 190);

            }else if (auxiliar ==1)
            {
                posicionInicialX = (int) (PantallaDelDispositivo.width/2 - 10);

            }
            else{
                posicionInicialX = (int) (PantallaDelDispositivo.width/2 +180);
            }


            Meteorito.setPosition(posicionInicialX, posicioninicialY);
            int posicionfinalX, posicionfinalY;
            posicionfinalX = posicionInicialX;
            posicionfinalY = (int) - alturameteorito/2;
            if (contadormeteoritos <20){
                Meteorito.runAction(MoveTo.action(5, posicionfinalX, posicionfinalY));
            }
            else if (contadormeteoritos >=20 && contadormeteoritos <= 40)
            {
                Meteorito.runAction(MoveTo.action(4, posicionfinalX, posicionfinalY));
            }
            else
            {
                Meteorito.runAction(MoveTo.action(3, posicionfinalX, posicionfinalY));
            }
            arrMeteorit.add(Meteorito);
            Log.d("ponermeteorit","Hay "+arrMeteorit.size()+ "meteoritos" );
            super.addChild(Meteorito);

        }

        boolean EstaEntre (int NumeroAComparar, int NumeroMenor, int NumeroMayor){
            boolean Devolver;

            if(NumeroMenor > NumeroMayor)
            {
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

            boolean Devolver;

            Devolver=false;

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


                Devolver=true;

            }

//Borde izq y borde sup de Sprite 1 está dentro de Sprite 2

            if (EstaEntre(Sprite1Izquierda, Sprite2Izquierda, Sprite2Derecha) &&

                    EstaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {


                Devolver=true;

            }

//Borde der y borde sup de Sprite 1 está dentro de Sprite 2

            if (EstaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) &&

                    EstaEntre(Sprite1Arriba, Sprite2Abajo, Sprite2Arriba)) {

                Devolver=true;

            }

//Borde der y borde inf de Sprite 1 está dentro de Sprite 2

            if (EstaEntre(Sprite1Derecha, Sprite2Izquierda, Sprite2Derecha) &&

                    EstaEntre(Sprite1Abajo, Sprite2Abajo, Sprite2Arriba)) {


                Devolver=true;

            }

//Borde izq y borde inf de Sprite 2 está dentro de Sprite 1

            if (EstaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) &&

                    EstaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {


                Devolver=true;

            }

//Borde izq y borde sup de Sprite 1 está dentro de Sprite 1

            if (EstaEntre(Sprite2Izquierda, Sprite1Izquierda, Sprite1Derecha) &&

                    EstaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {


                Devolver=true;

            }

//Borde der y borde sup de Sprite 2 está dentro de Sprite 1

            if (EstaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) &&

                    EstaEntre(Sprite2Arriba, Sprite1Abajo, Sprite1Arriba)) {


                Devolver=true;

            }

//Borde der y borde inf de Sprite 2 está dentro de Sprite 1

            if (EstaEntre(Sprite2Derecha, Sprite1Izquierda, Sprite1Derecha) &&

                    EstaEntre(Sprite2Abajo, Sprite1Abajo, Sprite1Arriba)) {


                Devolver=true;

            }

            return Devolver;

        }

        void detectarchoque(ArrayList<Sprite> arrayList, String Objeto){

            Log.d ("detectarchoque", "voy a verificar los " + arrMeteorit.size() +" meteoritos");
            boolean hubochoque;
            hubochoque = false;

            if (!estoyRecorriendoMeteoritos) {
                estoyRecorriendoMeteoritos=true;
                for (int i = 0; i < arrayList.size(); i++) {

                    if (InterseccionEntreSprites(CoheteUsuario, arrayList.get(i))) {
                        if(Objeto.equals("Monedas")){
                            ponerlblpuntaje(100);
                            super.removeChild(arrayList.get(i),true);
                        }else{
                            hubochoque = true;
                            estajugando = false;
                            ponerlblchoque();
                            super.removeChild( lblpuntaje,true);
                            Relojsegundos.cancel();
                            relojEliminarMeteorit.cancel();
                            relojponeMeteorit.cancel();
                            relojverificarimpacto.cancel();
                        }
                    }
                }
            }
            estoyRecorriendoMeteoritos=false;
            if (hubochoque ==true)
            {
                Log.d("hubochoque ", "entre");

            }
            else
            {
                Log.d("hubochoque", " no");
            }
        }



        void verificarsilosacodelarray(ArrayList<Sprite> ArraySprites, float altura){

            ArrayList<Sprite> spriteAElminiar=new ArrayList<>();
            int posfinalene =  (int) - altura/2;
            boolean sefue = false;
            if (!estoyRecorriendoMeteoritos) {
                estoyRecorriendoMeteoritos = true;
                for (Sprite SpriteAVerificar : ArraySprites) {

                    if (SpriteAVerificar.getPositionY() == posfinalene) {
                        spriteAElminiar.add(SpriteAVerificar);
                    }
                }
            }
            estoyRecorriendoMeteoritos=false;

            for (Sprite unmeteorito:spriteAElminiar)
            {
                eliminarmeteorito(unmeteorito, ArraySprites);
            }

        }


        public void eliminarmeteorito(Sprite meteoritoAEliminar,ArrayList<Sprite> ArraySprites )
        {
            if (!estoyRecorriendoMeteoritos) {
                estoyRecorriendoMeteoritos= true;
                super.removeChild(meteoritoAEliminar, true);
                Log.d("meteEnArray","Hay: "+ArraySprites.size());
                ArraySprites.remove(meteoritoAEliminar);

                Log.d("meteEnArray","Ahora quedan: "+ArraySprites.size());
            }
            estoyRecorriendoMeteoritos= false;
        }



    }
    class capafondo extends Layer {
        public capafondo() {
            Imagenfondo = Sprite.sprite("background.jpg");

            Imagenfondo.setPosition(AnchoPantalla / 2, AltoPantalla / 2);


            Imagenfondo.setPosition(PantallaDelDispositivo.width/2, PantallaDelDispositivo.height/2);
            Imagenfondo.runAction(ScaleBy.action(0.01f,2.f,2.0f));
            super.addChild(Imagenfondo);
        }


    }
}