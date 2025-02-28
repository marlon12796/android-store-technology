package com.example.proyectofinal

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONException
import com.android.volley.Request
import com.example.appbasedatosmysql2025.EndPoints

class GestionVentaActivity : AppCompatActivity() {

    private lateinit var etCodigoProducto: EditText
    private lateinit var requestQueue: RequestQueue
    private val productList = mutableListOf<Product>()
    private var productosCargados = false // Indica si ya se cargaron los productos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestion_venta)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ventaMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etCodigoProducto = findViewById(R.id.etCodigoProducto)

        // Cargar productos en segundo plano
        cargarProductos()

        // Mostrar diálogo solo cuando el usuario haga clic
        etCodigoProducto.setOnClickListener {
            if (productosCargados) {
                mostrarDialogoProductos()
            } else {
                Toast.makeText(this, "Cargando productos, espera un momento...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarProductos() {
        val url = EndPoints.GET_PRODUCTS // Reemplaza con tu API real

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val dataArray = response.getJSONArray("data")
                    productList.clear()
                    for (i in 0 until dataArray.length()) {
                        val obj = dataArray.getJSONObject(i)
                        val producto = Product(
                            obj.getString("codigo"),
                            obj.getString("nombre"),
                            obj.getString("descripcion"),
                            obj.getDouble("precio"),
                            obj.getInt("stock"),
                            obj.getBoolean("activo"),
                            obj.getString("categoria")
                        )
                        productList.add(producto)
                    }

                    // Indicar que los productos ya están cargados
                    productosCargados = true

                    runOnUiThread {
                        Toast.makeText(this, "Productos cargados exitosamente", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            { error ->
                error.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error al obtener los productos", Toast.LENGTH_SHORT).show()
                }
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    private fun mostrarDialogoProductos() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.dialog_productos, null)
        val rvProductos: RecyclerView = view.findViewById(R.id.rvProductos)

        rvProductos.layoutManager = LinearLayoutManager(this)
        val adapter = ProductAdapterListDialog(productList) { producto ->
            etCodigoProducto.setText(producto.codigo)
            Toast.makeText(this, "Producto seleccionado: ${producto.nombre}", Toast.LENGTH_SHORT).show()
        }

        rvProductos.adapter = adapter
        builder.setView(view)
        builder.setTitle("Seleccionar Producto")
        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
