package firebaseapp.cursoandroid.whts.model;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import firebaseapp.cursoandroid.whts.config.ConfiguracaoFirebase;

public class Usuario {

    private String nome;
    private String email;
    private String senha;
    private String id;

    public Usuario() {

    }

    public void salvar(){
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child( getId() ).setValue( this );
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
