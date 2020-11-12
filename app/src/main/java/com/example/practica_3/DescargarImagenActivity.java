package com.example.practica_3;

import android.app.Activity;

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
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DescargarImagenActivity extends Activity implements View.OnClickListener {

    ConnectivityManager connectivityManager = getSystemService(ConnectivityManager.class);
    Network currentNetwork = connectivityManager.getActiveNetwork();
    NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(currentNetwork);
    ImageView imgview_imagen;
    Button btn_descarga;
    String url_imagen = "https://upload.wikimedia.org/wikipedia/en/b/bd/Doraemon_character.png";
    Bitmap imagen;
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descargar_imagen);
        imgview_imagen = (ImageView) findViewById(R.id.imgview_imagen);
        btn_descarga = (Button) findViewById(R.id.btn_descargar_imagen);
        btn_descarga.setOnClickListener(this);
        bmOptions.inSampleSize = 1;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Si no está conectado a la red se cierra la activity
        if(caps==null)
        {
            Toast.makeText(this, "No estás conectado/a a la red", Toast.LENGTH_SHORT).show();
            this.finishActivity(0);
        }
    }

    private void hiloDescargaImagen() {
        ExecutorService executors = Executors.newFixedThreadPool(1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // your async code goes here.
                try {
                    Thread.sleep(10_000);

                    /*create message and pass any object here doesn't matter
                    for a simple example I have used a simple string
                    String msg = "My Message!";
                    actualizaImageView(msg);
                     */
                    imagen = LoadImage(url_imagen, bmOptions);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        executors.submit(runnable);
        executors.shutdown();
    }

    private void actualizaImageView(final Bitmap imagen) {
        Handler uiThread = new Handler(Looper.getMainLooper());
        uiThread.post(new Runnable() {
            @Override
            public void run() {
                // now update your UI here
                // cast response to whatever you specified earlier

                imgview_imagen.setImageBitmap(imagen);
            }
        });
    }

    private Bitmap LoadImage(String URL, BitmapFactory.Options options)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            in.close();
        } catch (IOException e1) {
        }
        return bitmap;
    }

    private InputStream OpenHttpConnection(String strURL) throws IOException{
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try{
            HttpURLConnection httpConn = (HttpURLConnection)conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        }
        catch (Exception ex)
        {
        }
        return inputStream;
    }

    @Override
    public void onClick(View v) {
        hiloDescargaImagen();
        actualizaImageView(imagen);
    }
}