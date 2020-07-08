package firebaseapp.cursoandroid.whts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import firebaseapp.cursoandroid.whts.config.ConfiguracaoFirebase;
import firebaseapp.cursoandroid.whts.helper.Base64Custom;
import firebaseapp.cursoandroid.whts.helper.Preferencias;
import firebaseapp.cursoandroid.whts.model.Usuario;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class CadastroUsuarioActivity extends AppCompatActivity {

    private EditText nome;
    private EditText email;
    private EditText senha;
    private Button botaoCadastrar;

    private FirebaseAuth autenticacao;
    private Usuario usuario;

    private DatabaseReference firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_cadastro);

        nome = (EditText) findViewById(R.id.etNomeCadastro);
        senha = (EditText) findViewById(R.id.etEmail);
        email = (EditText) findViewById(R.id.etEmailCadastro);
        botaoCadastrar = (Button) findViewById(R.id.btCadastrar);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuario = new Usuario();
                usuario.setNome(nome.getText().toString() );
                usuario.setEmail(email.getText().toString() );
                usuario.setSenha(senha.getText().toString() );
                cadastrarUsuario();

            }
        });

    }

    private void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroUsuarioActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful() ) {

                    //FirebaseUser usuarioFirebase = task.getResult().getUser();

                    String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId( identificadorUsuario);
                    usuario.salvar();

                    Preferencias preferencias = new Preferencias(CadastroUsuarioActivity.this);
                    preferencias.salvarDados(identificadorUsuario, usuario.getNome() );

                    abrirLoginUsuario();

                    //autenticacao.signOut();
                    //finish();

                    Toast.makeText(CadastroUsuarioActivity.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG).show();
                }else{

                    String erroExcecao = "";

                    try{
                        throw task.getException();

                    }catch(FirebaseAuthWeakPasswordException e){
                        erroExcecao = "Digite uma senha mais forte, contendo caracteres e com letras e números!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "O email digitado é inválido, digite um novo email!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        erroExcecao = "Esse email já está em uso no App!";
                    } catch (Exception e) {
                        erroExcecao = "Ao efetuar cadastro!";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroUsuarioActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void abrirLoginUsuario(){
        Intent intent = new Intent(CadastroUsuarioActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
