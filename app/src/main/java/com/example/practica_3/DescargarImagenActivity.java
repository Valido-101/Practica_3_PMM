package com.example.practica_3;

import android.app.Activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DescargarImagenActivity extends Activity implements View.OnClickListener {

    ExecutorService executorService;
    ImageView imgview_imagen;
    Button btn_descarga;
    String url_imagen = "https://upload.wikimedia.org/wikipedia/en/b/bd/Doraemon_character.png";
    Bitmap imagen;
    ProgressDialog progressdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargar_imagen);
        imgview_imagen = (ImageView) findViewById(R.id.imgview_imagen);
        btn_descarga = (Button) findViewById(R.id.btn_descargar_imagen);
        progressdialog = new ProgressDialog(this);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdialog.setTitle("Descargando...");
        btn_descarga.setOnClickListener(this);
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
        Network currentNetwork = connectivityManager.getActiveNetwork();
        NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
        //Si no está conectado a la red se cierra la activity
        if(currentNetwork==null)
        {
            Toast.makeText(this, "No estás conectado/a a la red", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }

    public void doSomeTaskAsync()
    {
        progressdialog.show();
        Runnable runnable = new Runnable(){
            @Override
            public void run(){
                try{
                    Thread.sleep(2000L); // paramos el Thread durante 100 ms
                    URL url = new URL(url_imagen);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        doSomethingOnUI(readStream(in));
                    } finally {
                        urlConnection.disconnect();
                    }
                    // pasamos el resultado del hilo secundario a un método

                    // que se ejecute en el hilo principal

                }
                catch(InterruptedException | MalformedURLException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        executorService.submit(runnable);
    }

    private Bitmap readStream(InputStream in) {
        imagen = BitmapFactory.decodeStream(in);
        return imagen;
    }

    public void doSomethingOnUI(final Bitmap imagen)
    {
        Handler uiThread = new Handler(Looper.getMainLooper());
        uiThread.post(new Runnable(){
            @Override
            public void run(){
            // actualizamos la UI...
                progressdialog.dismiss();
                imgview_imagen.setImageBitmap(imagen);
            }
        });
    }

    @Override
    public void onClick(View v) {
        doSomeTaskAsync();
    }

}