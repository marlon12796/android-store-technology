package com.example.proyectofinal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.toolbox.JsonObjectRequest
import com.example.appbasedatosmysql2025.EndPoints
import com.google.android.material.button.MaterialButton
import org.json.JSONObject
import com.android.volley.Request
import com.android.volley.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editTextUsuario = findViewById<EditText>(R.id.editTextUsuario)
        val editTextContraseña = findViewById<EditText>(R.id.editTextContraseña)
        val buttonLogin = findViewById<MaterialButton>(R.id.buttonLogin)
        val buttonExit = findViewById<MaterialButton>(R.id.buttonCerrar)

        buttonLogin.setOnClickListener {
            val correo = editTextUsuario.text.toString().trim()
            val contrasena = editTextContraseña.text.toString().trim()

            if (correo.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                iniciarSesion(correo, contrasena)
            }
        }

        buttonExit.setOnClickListener {
            mostrarDialogoSalir()
        }
    }

    private fun iniciarSesion(correo: String, contrasena: String) {
        val jsonRequest = JSONObject().apply {
            put("correo", correo)
            put("contrasena", contrasena)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, EndPoints.VERIFY_USER, jsonRequest,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun mostrarDialogoSalir() {
        AlertDialog.Builder(this)
            .setTitle("Salir de la aplicación")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { _, _ -> finishAffinity() }
            .setNegativeButton("No", null)
            .show()
    }
}
