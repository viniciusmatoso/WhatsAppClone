package firebaseapp.cursoandroid.whts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import firebaseapp.cursoandroid.whts.Adapter.MensagemAdapter;
import firebaseapp.cursoandroid.whts.config.ConfiguracaoFirebase;
import firebaseapp.cursoandroid.whts.helper.Base64Custom;
import firebaseapp.cursoandroid.whts.helper.Preferencias;
import firebaseapp.cursoandroid.whts.model.Conversa;
import firebaseapp.cursoandroid.whts.model.Mensagem;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConversaActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etMensagem;
    private ImageButton btEnviar;
    private DatabaseReference firebase;

    //dados do destinatario
    private String nomeUsuarioDestinatario;
    private String idUsuarioDestinatario;

    //dados do rementente
    private String idUsuarioRemetente;
    private String nomeUsuarioRemetente;

    private ListView listView;

    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;

    private ValueEventListener valueEventListenerMensagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversa);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);

        etMensagem = (EditText) findViewById(R.id.et_mensagem);
        btEnviar = (ImageButton) findViewById(R.id.bt_enviar);

        listView = (ListView) findViewById(R.id.lv_conversas);

        //dados do usuário logado
        Preferencias preferencias = new Preferencias(ConversaActivity.this);
        idUsuarioRemetente = preferencias.getIdentificador();
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if(extra != null){
            nomeUsuarioDestinatario = extra.getString("nome");
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Custom.codificarBase64(emailDestinatario);
        }

        toolbar.setTitle(nomeUsuarioDestinatario);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        //Monta listview e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ConversaActivity.this, mensagens);
        listView.setAdapter( adapter );

        //recuperar mensagens do firebase
        firebase = ConfiguracaoFirebase.getFirebase()
                .child("mensagem")
                .child( idUsuarioRemetente )
                .child( idUsuarioDestinatario);

        //criar listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mensagens.clear();

                //recupera mensagens
                for (DataSnapshot dados: dataSnapshot.getChildren() ){
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add( mensagem );
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        firebase.addValueEventListener( valueEventListenerMensagem );

        btEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoMensagem = etMensagem.getText().toString();

                if(textoMensagem.isEmpty()){
                    Toast.makeText(ConversaActivity.this, "Digite a sua mensagem", Toast.LENGTH_LONG).show();
                }else{

                    Mensagem mensagem = new Mensagem();
                    mensagem.setIdUsuario( idUsuarioRemetente);
                    mensagem.setMensagem( textoMensagem);

                    //salvamos mensagem para o remetente
                    Boolean retornoMensagemRemetente = salvarMensagem(idUsuarioRemetente, idUsuarioDestinatario, mensagem);
                    if( !retornoMensagemRemetente){
                        Toast.makeText(ConversaActivity.this,
                                "Problema ao salvar a mensagem, tente novamente!",
                                Toast.LENGTH_SHORT
                        ).show();
                    }else{
                        //salvamos mensagem para o destinatario
                        Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, idUsuarioRemetente, mensagem);
                        if( !retornoMensagemDestinatario){
                            Toast.makeText(ConversaActivity.this,
                                    "Problema ao salvar a mensagem, tente novamente!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }

                    //salvamos conversa para o remetente
                    Conversa conversa = new Conversa();
                    conversa.setIdUsuario( idUsuarioDestinatario );
                    conversa.setMensagem( textoMensagem );
                    conversa.setNome( nomeUsuarioDestinatario );

                    Boolean retornoConversaRemetente = salvarConversa(idUsuarioRemetente, idUsuarioDestinatario, conversa);
                    if( !retornoConversaRemetente ){
                        Toast.makeText(
                                ConversaActivity.this,
                                "Problema ao salvar conversa, tente novamente!",
                                Toast.LENGTH_LONG
                        ).show();
                    }else{
                        //salvamos Conversa para o destinatario
                        conversa = new Conversa();
                        conversa.setIdUsuario( idUsuarioRemetente );
                        conversa.setNome( nomeUsuarioRemetente );
                        conversa.setMensagem( textoMensagem );

                        Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, idUsuarioRemetente, conversa);
                        if( !retornoConversaDestinatario ){
                            Toast.makeText(
                                    ConversaActivity.this,
                                    "Problema ao salvar conversa, tente novamente!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    etMensagem.setText("");
                }
            }
        });
    }

    private boolean salvarMensagem(String idUsuarioRemetente, String idDestinatario, Mensagem mensagem) {
        try{

            firebase = ConfiguracaoFirebase.getFirebase().child("mensagem");

            firebase.child(idUsuarioRemetente)
                    .child(idDestinatario)
                    .push()
                    .setValue( mensagem );

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa){
        try{
            firebase = ConfiguracaoFirebase.getFirebase().child("conversas");
            firebase.child( idRemetente )
                    .child( idDestinatario )
                    .setValue(conversa);
            return true;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener( valueEventListenerMensagem );
    }


}

