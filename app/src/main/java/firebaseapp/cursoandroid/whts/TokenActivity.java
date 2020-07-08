package firebaseapp.cursoandroid.whts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import firebaseapp.cursoandroid.whts.helper.Permissao;
import firebaseapp.cursoandroid.whts.helper.Preferencias;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.Random;

public class TokenActivity extends AppCompatActivity {

    private EditText telefone;
    private EditText codPais;
    private EditText codEstado;
    private EditText nome;

    private Button cadastrar;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.SEND_SMS,
            Manifest.permission.INTERNET
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_token);

        Permissao.validaPermissoes(1, this, permissoesNecessarias);

        telefone = (EditText) findViewById(R.id.etNumero);
        nome = (EditText) findViewById(R.id.etNomeCadastro);
        codEstado = (EditText) findViewById(R.id.etEstado);
        codPais = (EditText) findViewById(R.id.etPais);

        cadastrar = (Button) findViewById(R.id.btCadastrar);


        SimpleMaskFormatter simpleMaskcodPais = new SimpleMaskFormatter("+NN");
        SimpleMaskFormatter simpleMaskcodEstado = new SimpleMaskFormatter("NN");
        SimpleMaskFormatter simpleMaskTelefone = new SimpleMaskFormatter("NNNNN-NNNN");

        MaskTextWatcher maskTelefone = new MaskTextWatcher(telefone, simpleMaskTelefone);
        MaskTextWatcher maskcodPais = new MaskTextWatcher(codPais, simpleMaskcodPais);
        MaskTextWatcher maskcodEstado = new MaskTextWatcher(codEstado, simpleMaskcodEstado);

        telefone.addTextChangedListener( maskTelefone );
        codPais.addTextChangedListener( maskcodPais );
        codEstado.addTextChangedListener( maskcodEstado );

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nomeUsuario = nome.getText().toString();
                String telefoneCompleto =
                        codPais.getText().toString() +
                        codEstado.getText().toString() +
                        telefone.getText().toString();

                String telefoneSemFormatacao = telefoneCompleto.replace("+", "");
                telefoneSemFormatacao = telefoneSemFormatacao.replace("-", "");

                //Gerar token
                Random randomico = new Random();
                int numeroRandomico = randomico.nextInt(9999 - 1000) + 1000;
                String token = String.valueOf( numeroRandomico );
                String mensagemEnvio = "WhatsApp Código de Confirmação: " + token;

                //Salvar os dados para validacao
                Preferencias preferencias = new Preferencias(TokenActivity.this);
                //preferencias.salvarUsuarioPreferencias(nomeUsuario, telefoneSemFormatacao, token);

                //HashMap<String, String> usuario = preferencias.getDadosUsuario();

                //Envio do SMS
                telefoneSemFormatacao = "1212";
                boolean enviadoSMS = enviaSMS( "+" + telefoneSemFormatacao, mensagemEnvio);

                if(enviadoSMS){

                    Intent intent = new Intent(TokenActivity.this, ValidadorActivity.class);
                    startActivity( intent );
                    finish();

                }else{
                    Toast.makeText(TokenActivity.this, "Problema ao enviar o SMS", Toast.LENGTH_LONG).show();
                }

            }
        });


    }
    //Envio do SMS
    private boolean enviaSMS(String telefone, String mensagem){

        try{

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(telefone, null, mensagem, null, null);

            return true;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int resultado : grantResults){

            if(resultado == PackageManager.PERMISSION_DENIED){

                alertaValidacaoPermissao();

            }

        }

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar esse app, é necessário aceitar as permissões");

        builder.setPositiveButton("CONFIRMAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
