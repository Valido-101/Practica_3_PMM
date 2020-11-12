package com.example.practica_3;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{

    //Creamos los intents para abrir las otras actividades
    Intent act_imagen = new Intent(this, DescargarImagenActivity.class);
    Intent act_xml = new Intent(this, DescargarXMLActivity.class);

    //Creamos los objetos botón en los que guardaremos los botones que hay en el layout
    Button btn_imagen;
    Button btn_xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtenemos los botones
        btn_imagen = (Button)findViewById(R.id.btn_imagen);
        btn_xml = (Button)findViewById(R.id.btn_xml);

        //Establecemos los listeners a los botones
        btn_imagen.setOnClickListener(this);
        btn_xml.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        //Si el id de la view que ha sido pulsada coincide con el del botón imagen, se abre esa actividad
        if(v.getId()==btn_imagen.getId())
        {
            startActivity(act_imagen);
        }
        //Si no, se abre la actividad de xml
        else
        {
            startActivity(act_xml);
        }
    }
}