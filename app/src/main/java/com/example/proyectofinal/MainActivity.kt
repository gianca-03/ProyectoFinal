package com.example.proyectofinal

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.proyectofinal.databinding.ActivityMainBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //Objeto Firebase Auth
    private lateinit var auth: FirebaseAuth

    //Pantalla xml
    private  lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializar Auth
        FirebaseApp.initializeApp(this )
        auth = Firebase.auth

        //Click Registro
        binding.btRegistro.setOnClickListener{ registrar() }

        //Click Login
        binding.btLogin.setOnClickListener{ login() }
    }

    private fun registrar() {
        val email = binding.etCorreo.text.toString()
        val clave = binding.etClave.text.toString()

        //Registro de usuario
        auth.createUserWithEmailAndPassword(email,clave).addOnCompleteListener(this){task ->
            if(task.isSuccessful){
                val user = auth.currentUser
                cargarPantalla(user)
            } else {
                Toast.makeText(baseContext, "Fallo ${task.exception.toString()}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun cargarPantalla(user : FirebaseUser?){
        if(user != null) {
            val intent = Intent(this,Home::class.java)
            startActivity(intent)
        }
    }

    private fun login(){
        val email = binding.etCorreo.text.toString()
        val clave = binding.etClave.text.toString()

        //Login
        auth.signInWithEmailAndPassword(email,clave).addOnCompleteListener { result ->
            if (result.isSuccessful){
                val user = auth.currentUser
                cargarPantalla(user)
            }else{
                Toast.makeText(baseContext, getText(R.string.no_login), Toast.LENGTH_LONG).show()
            }
        }
    }

    //override fun onStart() {
      //  super.onStart()
      //  val user = auth.currentUser
       // cargarPantalla(user)
    //}
}