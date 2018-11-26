package Objetos;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.carlos_hc.dogdate.ListaMatchs;
import com.example.carlos_hc.dogdate.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.carlos_hc.dogdate.ListaMatchs;

import java.util.List;

public class PerroAdapter extends RecyclerView.Adapter<PerroAdapter.MyViewHolder> {

    Context miContexto;

    private List<Perro> listaPerrosMatchs;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nombre, raza, email, genero;
        public ImageView foto;

        public MyViewHolder(View view) {
            super(view);
            nombre = (TextView) view.findViewById(R.id.txtNombre);
            raza = (TextView) view.findViewById(R.id.txtRaza);
            email = (TextView) view.findViewById(R.id.txtEmail);
            genero = (TextView) view.findViewById(R.id.txtGenero);
            foto = (ImageView) view.findViewById(R.id.imgPerroRow);
        }

    }

    //constructor
    public PerroAdapter(List<Perro> listaPerrosMatchs, Context miContexto) {
        this.listaPerrosMatchs = listaPerrosMatchs;
        this.miContexto = miContexto;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.perro_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Perro miPerro = listaPerrosMatchs.get(position);
        holder.nombre.setText(miPerro.getNombre());
        holder.raza.setText("Raza: " + miPerro.getRaza());
        holder.email.setText("Email: " + miPerro.getEmail());
        holder.genero.setText("Genero: " + miPerro.getGenero());

        //cargamos la foto
        cargarFotoPorEmail(miPerro.getEmail(), miContexto, holder);

    }

    @Override
    public int getItemCount() {

        return listaPerrosMatchs.size();
    }


    private void cargarFotoPorEmail(String email, Context contexto, MyViewHolder holder) {

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(contexto);
        circularProgressDrawable.setStrokeWidth(10);
        circularProgressDrawable.setCenterRadius(50);
        circularProgressDrawable.start();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference with an initial file path and name
        StorageReference pathReference = storageRef.child("dogDate/" + email + ".jpg");

        // Load the image using Glide
        Glide.with(contexto)
                .using(new FirebaseImageLoader())
                .load(pathReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .placeholder(circularProgressDrawable)
                .into(holder.foto);

    }


}
