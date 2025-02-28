package com.example.proyectofinal


import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

class GestionProductoActivity : AppCompatActivity() {

    private lateinit var etCodigoBusqueda: EditText
    private lateinit var btnEliminar: Button
    private lateinit var etCodigo: EditText
    private lateinit var etNombre: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var etPrecio: EditText
    private lateinit var etStock: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var btnAgregar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestion_producto)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.gestion_producto_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etCodigo = findViewById(R.id.etCodigo)
        etNombre = findViewById(R.id.etNombre)
        etDescripcion = findViewById(R.id.etDescripcion)
        etPrecio = findViewById(R.id.etPrecio)
        etStock = findViewById(R.id.etStock)
        spinnerCategoria = findViewById(R.id.spinnerCategoria)
        btnAgregar = findViewById(R.id.btnAgregar)
        etCodigoBusqueda = findViewById(R.id.etCodigoBusqueda)
        btnEliminar = findViewById(R.id.btnEliminar)
        val spinnerCategoria: Spinner = findViewById(R.id.spinnerCategoria)
        // Opciones del Spinner
        val categorias = listOf("laptop", "celular", "tablet", "monitor", "accesorio", "otro")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategoria.adapter = adapter
         // agregar
        btnAgregar.setOnClickListener {
            agregarProducto()
        }
        // eliminar

        spinnerCategoria.adapter = adapter
        btnEliminar.setOnClickListener {
            val codigo = etCodigoBusqueda.text.toString().trim()
            if (codigo.isNotEmpty()) {
                consultarProducto(codigo)
            } else {
                Toast.makeText(this, "Ingrese un código de producto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun consultarProducto(codigo: String) {
        val url = "http://192.168.18.6:4500/products/$codigo"

        val request = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val success = response.getBoolean("success")
                if (success) {
                    val data = response.getJSONObject("data")
                    mostrarDialogoEliminar(data)
                } else {
                    Toast.makeText(this, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al obtener producto", Toast.LENGTH_SHORT).show()
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }

    private fun mostrarDialogoEliminar(data: JSONObject) {
        val codigo = data.getString("codigo")
        val nombre = data.getString("nombre")
        val descripcion = data.getString("descripcion")
        val precio = data.getString("precio")
        val stock = data.getInt("stock")

        val mensaje = """
            Código: $codigo
            Nombre: $nombre
            Descripción: $descripcion
            Precio: $precio
            Stock: $stock
        """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("Confirmar Eliminación")
            .setMessage(mensaje)
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(codigo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto(codigo: String) {
        val url = "http://192.168.18.6:4500/products/$codigo"

        val request = JsonObjectRequest(Request.Method.DELETE, url, null,
            { response ->
                val success = response.getBoolean("success")
                if (success) {
                    Toast.makeText(this, "Producto eliminado correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al eliminar producto", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
    private fun agregarProducto() {
        val codigo = etCodigo.text.toString().trim()
        val nombre = etNombre.text.toString().trim()
        val descripcion = etDescripcion.text.toString().trim()
        val precio = etPrecio.text.toString().trim().toDoubleOrNull()
        val stock = etStock.text.toString().trim().toIntOrNull()
        val categoria = spinnerCategoria.selectedItem.toString().lowercase() // Convertir a minúsculas

        if (codigo.isEmpty() || nombre.isEmpty() || descripcion.isEmpty() || precio == null || stock == null) {
            Toast.makeText(this, "Todos los campos son obligatorios y deben ser válidos", Toast.LENGTH_SHORT).show()
            return
        }

        val categoriasValidas = listOf("laptop", "celular", "tablet", "monitor", "accesorio", "otro")
        if (!categoriasValidas.contains(categoria)) {
            Toast.makeText(this, "Categoría inválida", Toast.LENGTH_SHORT).show()
            return
        }

        val jsonRequest = JSONObject().apply {
            put("codigo", codigo)
            put("nombre", nombre)
            put("descripcion", descripcion)
            put("precio", precio)
            put("stock", stock)
            put("categoria", categoria)
        }

        val url = "http://192.168.18.6:4500/products"

        val request = JsonObjectRequest(Request.Method.POST, url, jsonRequest,
            { response ->
                try {
                    val success = response.getBoolean("success")
                    if (success) {
                        Toast.makeText(this, "Producto agregado correctamente", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    } else {
                        val message = response.optString("message", "Error desconocido")
                        Log.e("API_RESPONSE", "Error en respuesta: $message")
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Log.e("API_RESPONSE", "Error procesando respuesta JSON: ${e.message}")
                    Toast.makeText(this, "Error procesando la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.networkResponse?.let {
                    val statusCode = it.statusCode
                    val errorMessage = String(it.data, Charsets.UTF_8)
                    Log.e("API_ERROR", "Error $statusCode: $errorMessage") // Imprime el error en Logcat
                    Toast.makeText(this, "Error $statusCode: $errorMessage", Toast.LENGTH_LONG).show()
                } ?: run {
                    Log.e("API_ERROR", "Error desconocido de conexión")
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            })

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }


    private fun limpiarCampos() {
        etCodigo.text.clear()
        etNombre.text.clear()
        etDescripcion.text.clear()
        etPrecio.text.clear()
        etStock.text.clear()
        spinnerCategoria.setSelection(0)
    }
}
