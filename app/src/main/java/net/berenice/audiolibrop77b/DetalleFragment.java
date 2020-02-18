package net.berenice.audiolibrop77b;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import java.io.IOException;

public class DetalleFragment extends Fragment implements View.OnTouchListener {

    public static String ARG_ID_LIBRO = "id_libro";
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    String guardar = "";
    String guardarlibro = "";
    boolean first = false;
    MisServicioEnlazado misServicioEnlazado;

    boolean mBound = false;
    int idlibro;

    public final String NotificiacionChanelID = "chanel_id";

    @Override
    public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        Intent intent = new Intent(getActivity().getApplicationContext(), MisServicioEnlazado.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Libro libro;
        View vista = inflador.inflate(R.layout.fragment_detalle,
                contenedor, false);
        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            idlibro = args.getInt(ARG_ID_LIBRO);
            libro = ((Aplicacion) getActivity().getApplication())
                    .getVectorLibros().elementAt(position);
            ponInfoLibro(position, vista);
        } else {
            idlibro = 0;
            ponInfoLibro(0, vista);
            libro = ((Aplicacion) getActivity().getApplication())
                    .getVectorLibros().elementAt(0);

        }
//        ContextCompat.startForegroundService(getActivity().getApplicationContext(),intent);
        return vista;
    }

    private void ponInfoLibro(int id, View vista) {
        Libro libro = ((Aplicacion) getActivity().getApplication())
                .getVectorLibros().elementAt(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        ((ImageView) vista.findViewById(R.id.portada))
                .setImageResource(libro.recursoImagen);
        vista.setOnTouchListener(this);


//        Toast.makeText(getActivity(), servicio.getRandomNumber(), Toast.LENGTH_SHORT).show();
    }

    public void ponInfoLibro(int id) {
        ponInfoLibro(id, getView());
    }


    @Override
    public boolean onTouch(View vista, MotionEvent evento) {

        try {
            if (misServicioEnlazado.getMediaPlayer() != null) {
                if (misServicioEnlazado.libroID != this.idlibro) {
                    if (idlibro >= 0) {
                        NotificationCompat.Builder noti = new NotificationCompat.Builder(getActivity().getApplicationContext(),
                                NotificiacionChanelID).setContentTitle("AudioLibros").setContentText("Reproducionedo Audio").setPriority(NotificationCompat.PRIORITY_LOW);
                        Intent intent = new Intent(getActivity().getApplicationContext(), MisServicioEnlazado.class);
                        misServicioEnlazado.startForeground(0, noti.build());
                        misServicioEnlazado.notificacion();
                        Libro libro = ((Aplicacion) getActivity().getApplication())
                                .getVectorLibros().elementAt(idlibro);
                        misServicioEnlazado.setMediaPlayer(getActivity(), libro, this.getView(), idlibro);
                    }
                } else {
                    misServicioEnlazado.showMedia();
                }

            } else {
                if (idlibro >= 0) {
                    NotificationCompat.Builder noti = new NotificationCompat.Builder(getActivity().getApplicationContext(), NotificiacionChanelID).setContentTitle("AudioLibros").setContentText("Reproducionedo Audio").setPriority(NotificationCompat.PRIORITY_LOW);

                    misServicioEnlazado.startForeground(0, noti.build());
                    Intent intent = new Intent(getActivity().getApplicationContext(), MisServicioEnlazado.class);
//                    ContextCompat.startForegroundService(getActivity().getApplicationContext(),intent);
                    misServicioEnlazado.startForeground(0, new Notification());
                    misServicioEnlazado.notificacion();
                    Libro libro = ((Aplicacion) getActivity().getApplication())
                            .getVectorLibros().elementAt(idlibro);
                    misServicioEnlazado.setMediaPlayer(getActivity(), libro, this.getView(), idlibro);
                }
            }

        } catch (Exception e) {
            Toast.makeText(getActivity(), "algo esta pasando \n" + e, Toast.LENGTH_SHORT).show();
        }


//        servicio.showMedia();
        return false;
    }

    @Override
    public void onStop() {

        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getActivity());

        if (!preferencias.getBoolean("pref_autoreproducir", true)) {
            try {
                misServicioEnlazado.Stop();
            } catch (Exception e) {

            }
        }


        super.onStop();

    }

    @Override
    public void onDestroy() {
        misServicioEnlazado.stopSelf();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        DetalleFragment detalleFragment = (DetalleFragment)
                getFragmentManager().findFragmentById(R.id.fragment_detalle);
        if (detalleFragment == null) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }
        super.onResume();
    }


    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MisServicioEnlazado.MiBinder binder = (MisServicioEnlazado.MiBinder) service;
            misServicioEnlazado = binder.getService();
            mBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
}



