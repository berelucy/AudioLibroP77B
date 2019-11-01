package net.berenice.audiolibrop77b;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.snackbar.Snackbar;

import java.util.Vector;

public class SelectorFragment extends Fragment implements Animator.AnimatorListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    AdaptadorLibrosFiltro adaptadorLibros;
    Vector<Libro> vectorLibros;
    AppCompatActivity actividad;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            this.actividad = (AppCompatActivity) context;
            vectorLibros = Libro.ejemploLibros();
            adaptadorLibros = new AdaptadorLibrosFiltro(this.actividad, vectorLibros);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_selector, container, false);
        recyclerView = v.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(actividad, 2));
        recyclerView.setAdapter(adaptadorLibros);
        adaptadorLibros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Toast.makeText(actividad,"seleccionando elemento: "+recyclerView.getChildAdapterPosition(view),Toast.LENGTH_SHORT).show();

                String t = ((TextView)view.findViewById(R.id.titulo)).getText().toString();
                String t2 = vectorLibros.elementAt(recyclerView.getChildAdapterPosition(view)).titulo;

                Toast.makeText(actividad,"seleccionado el elemnto "+t+","+t2,Toast.LENGTH_SHORT).show();*/

                //((MainActivity) actividad).mostrarDetalle(recyclerView.getChildAdapterPosition(view));
                ((MainActivity) actividad).mostrarDetalle((int) adaptadorLibros.getItemId(recyclerView.getChildAdapterPosition(view)));
            }
        });

        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(2000);
        animator.setMoveDuration(2000);
        recyclerView.setItemAnimator(animator);

        adaptadorLibros.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int id = recyclerView.getChildAdapterPosition(v);
                AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
                CharSequence[] opciones = {"Compartir", "Borrar ", "Insertar"};
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Compartir
                                Libro libro = vectorLibros.elementAt(id);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                                i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                                startActivity(Intent.createChooser(i, "Compartir"));
                                break;
                            case 1: //Borrar
                                Snackbar.make(v, "¿Estás seguro?", Snackbar.LENGTH_LONG).setAction("SI",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //vectorLibros.remove(id);
                                               /* Animation anim = AnimationUtils.loadAnimation(actividad,
                                                        R.anim.menguar);
                                                anim.setAnimationListener((Animation.AnimationListener) SelectorFragment.this);
                                                v.startAnimation(anim);

                                                */


                                                Animator anim = AnimatorInflater.loadAnimator(actividad, R.animator.menguar);
                                                anim.addListener(SelectorFragment.this);
                                                anim.setTarget(v);
                                                anim.start();


                                                adaptadorLibros.borrar(id);
                                               // adaptadorLibros.notifyDataSetChanged();
                                                //adaptadorLibros.notifyItemInserted(0);
                                            }
                                        }).show();
                                break;
                            case 2: //Insertar
                                //vectorLibros.add(vectorLibros.elementAt(id));
                                int posicion = recyclerView.getChildLayoutPosition(v);
                                adaptadorLibros.insertar((Libro) adaptadorLibros.getItem(posicion));
                                adaptadorLibros.notifyDataSetChanged();
                                Snackbar.make(v, "Libro insertado", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selector, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_buscar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String query) {
                        adaptadorLibros.setBusqueda(query);
                        adaptadorLibros.notifyDataSetChanged();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                });

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        adaptadorLibros.setBusqueda("");
                        adaptadorLibros.notifyDataSetChanged();
                        return true; // Para permitir cierre
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true; // Para permitir expansión
                    }
                });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) actividad).irUltimoVisitado();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onResume() {
        ((MainActivity) getActivity()).mostrarElementos(true);
        super.onResume();
    }




    @Override
    public void onAnimationStart(Animator animation, boolean isReverse) {

    }

    @Override
    public void onAnimationEnd(Animator animation, boolean isReverse) {

    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {
adaptadorLibros.notifyDataSetChanged();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
