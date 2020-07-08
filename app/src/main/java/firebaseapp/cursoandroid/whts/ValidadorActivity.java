package firebaseapp.cursoandroid.whts;

import androidx.appcompat.app.AppCompatActivity;
import firebaseapp.cursoandroid.whts.helper.Preferencias;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import java.util.HashMap;

public class ValidadorActivity extends AppCompatActivity {

    private EditText codigoValidacao;
    private Button validar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validador);

        codigoValidacao = (EditText) findViewById(R.id.etCodigo);
        validar = (Button) findViewById(R.id.btValidar);

        SimpleMaskFormatter simpleMaskFormatter = new SimpleMaskFormatter("NNNN");
        MaskTextWatcher mascaraCodigoValidacao = new MaskTextWatcher(codigoValidacao, simpleMaskFormatter);

        codigoValidacao.addTextChangedListener( mascaraCodigoValidacao );

        validar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Preferencias preferencias = new Preferencias( ValidadorActivity.this);
                //HashMap<String, String> usuario = preferencias.getDadosUsuario();

                //String tokenGerado = usuario.get("token");
                //String tokenDigitado = codigoValidacao.getText().toString();

                //if(tokenDigitado.equals( tokenGerado )){
                    Toast.makeText(ValidadorActivity.this, "Token VALIDADO", Toast.LENGTH_LONG).show();
                //}else{
                    Toast.makeText(ValidadorActivity.this, "Token NÃO VALIDADO", Toast.LENGTH_LONG).show();
                }

            //}
        });


    }
}
