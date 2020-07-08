package firebaseapp.cursoandroid.whts.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import firebaseapp.cursoandroid.whts.R;
import firebaseapp.cursoandroid.whts.model.Conversa;

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private ArrayList<Conversa> conversas;
    private Context context;

    public ConversaAdapter(@NonNull Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.conversas = objects;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view = null;

        if(conversas != null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            view = inflater.inflate(R.layout.lista_conversas, parent, false);

            TextView nome = (TextView) view.findViewById(R.id.txtNome);
            TextView mensagem = (TextView) view.findViewById(R.id.txtMensagem);

            Conversa conversa = conversas.get(position);
            nome.setText(conversa.getNome());
            mensagem.setText(conversa.getMensagem());
        }

        return view;
    }
}
