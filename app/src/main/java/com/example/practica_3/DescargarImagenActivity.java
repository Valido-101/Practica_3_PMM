package com.example.practica_3;

import android.Manifest;
import android.app.Activity;

import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DescargarImagenActivity extends AppCompatActivity implements View.OnClickListener {

    ExecutorService executorService;
    ImageView imgview_imagen;
    Button btn_descarga;
    String url_imagen = "https://upload.wikimedia.org/wikipedia/en/b/bd/Doraemon_character.png";
    Bitmap imagen;
    ProgressDialog progressdialog;
    ActivityResultLauncher<String> requestPermissionLauncher;

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
        requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>(){
                            @Override
                            public void onActivityResult(Boolean isGranted){
                                if (isGranted) {
// Permission is granted. Continue the action or workflow in your
// app.
                                    doSomeTaskAsync();
                                }
                            }
                        });
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

                //-------------------------------------------------------
                try {
                    // externalStorage
                    String ExternalStorageDirectory = Environment.getExternalStorageDirectory() + File.separator;

                    //carpeta "imagenesguardadas"
                    String rutacarpeta = "Downloads/";
                    // nombre del nuevo png
                    String nombre = "imagen_doraemon.png";

                    // Compruebas si existe la carpeta "imagenesguardadas", sino, la crea
                    File directorioImagenes = new File(ExternalStorageDirectory + rutacarpeta);
                    if (!directorioImagenes.exists())
                        directorioImagenes.mkdirs();

                    // pones las medidas que quieras del nuevo .png
                    int bitmapWidth = 120; // para utilizar width de la imagen original: bitmap.getWidth();
                    int bitmapHeight = 120; // para utilizar height de la imagen original: bitmap.getHeight();
                    Bitmap bitmapout = Bitmap.createScaledBitmap(imagen, bitmapWidth, bitmapHeight, false);
                    //creas el nuevo png en la nueva ruta
                    bitmapout.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(ExternalStorageDirectory + rutacarpeta + nombre));

                    // le pones parametros necesarios a la imagen para que se muestre en cualquier galería

                    File filefinal = new File(ExternalStorageDirectory + rutacarpeta + nombre);

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "Doraemon");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Doraemon sentao'");
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_ID, filefinal.toString().toLowerCase(Locale.getDefault()).hashCode());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, filefinal.getName().toLowerCase(Locale.getDefault()));
                    values.put("_data", filefinal.getAbsolutePath());
                    ContentResolver cr = getContentResolver();
                    cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Toast.makeText(getApplicationContext(), "Imagen guardada en la carpeta Descargas",Toast.LENGTH_LONG).show();
                    //

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
                //-------------------------------------------------------
            }
        });
    }

    @Override
    public void onClick(View v) {
        //doSomeTaskAsync();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
// You can use the API that requires the permission.
            doSomeTaskAsync();
        }else {
// You can directly ask for the permission.
// The registered ActivityResultCallback gets the result of this request.
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
}