package com.example.proyectofinal

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
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
import com.android.volley.Response
import com.example.appbasedatosmysql2025.EndPoints

class GestionVentaActivity : AppCompatActivity() {

    private lateinit var etCodigoProducto: EditText
    private lateinit var requestQueue: RequestQueue
    private val productList = mutableListOf<Product>()
    private var productosCargados = false
    private lateinit var etCodigoBusquedaVenta: EditText
    private lateinit var edtCodigo: EditText
    private lateinit var edtNumeroDocumento: EditText
    private lateinit var edtFechaEmision: EditText
    private lateinit var edtCliente: EditText
    private lateinit var edtSubtotal: TextView
    private lateinit var edtIgv: TextView
    private lateinit var edtTotal: TextView
    private lateinit var btnBuscarVenta: Button
    private lateinit var btnEliminarVenta: Button
    private lateinit var  edtCantidad : TextView
    private lateinit var edtDescuento : TextView
    private lateinit var dbDescuento : CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gestion_venta)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ventaMain)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Referencias a los elementos de la interfaz
        etCodigoBusquedaVenta = findViewById(R.id.etCodigoBusquedaVenta)
        edtCodigo = findViewById(R.id.etCodigoVenta)
        edtNumeroDocumento = findViewById(R.id.etNroDocumento)
        edtFechaEmision = findViewById(R.id.etFecha)
        edtCliente = findViewById(R.id.etCliente)
        edtSubtotal = findViewById(R.id.tvSubtotal)
        edtIgv = findViewById(R.id.tvIgv)
        edtTotal = findViewById(R.id.tvTotal)
        btnBuscarVenta = findViewById(R.id.btnBuscarVenta)

        btnEliminarVenta = findViewById(R.id.btnEliminarVenta)
        etCodigoProducto = findViewById(R.id.etCodigoProducto)

        // Cargar productos en segundo plano
        cargarProductos()
        btnBuscarVenta.setOnClickListener {
            val codigoVenta = etCodigoBusquedaVenta.text.toString().trim()
            if (codigoVenta.isNotEmpty()) {
                buscarVenta(codigoVenta)
            } else {
                Toast.makeText(this, "Ingrese un código de venta", Toast.LENGTH_SHORT).show()
            }
        }
        btnEliminarVenta.setOnClickListener {
            val codigoVenta = edtCodigo.text.toString().trim()
            if (codigoVenta.isNotEmpty()) {
                mostrarDialogoConfirmacion(codigoVenta) // Mostrar diálogo de confirmación
            } else {
                Toast.makeText(this, "No hay una venta seleccionada para eliminar", Toast.LENGTH_SHORT).show()
            }
        }

        etCodigoProducto.setOnClickListener {
            if (productosCargados) {
                mostrarDialogoProductos()
            } else {
                Toast.makeText(this, "Cargando productos, espera un momento...", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun buscarVenta(codigoVenta: String) {
        val url = EndPoints.GET_SALES +"/$codigoVenta"

        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // Obtener datos del JSON
                    val codigo = response.getString("codigo")
                    val numeroDocumento = response.getString("numeroDocumento")
                    val fechaEmision = response.getString("fechaEmision")
                    val cliente = response.getString("cliente")
                    val subtotal = response.getString("subtotal")
                    val igv = response.getString("igv")
                    val total = response.getString("total")

                    // Llenar los campos del formulario
                    edtCodigo.setText(codigo)
                    edtNumeroDocumento.setText(numeroDocumento)
                    edtFechaEmision.setText(fechaEmision)
                    edtCliente.setText(cliente)
                    edtSubtotal.setText(subtotal)
                    edtIgv.setText(igv)
                    edtTotal.setText(total)

                    Toast.makeText(this, "Venta cargada exitosamente", Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "No se encontró la venta", Toast.LENGTH_SHORT).show()
            }
        )
        VolleySingleton.getInstance(this).addToRequestQueue(request)
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
    private fun mostrarDialogoConfirmacion(codigoVenta: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Venta")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta venta?")
        builder.setPositiveButton("Sí") { _, _ ->
            eliminarVenta(codigoVenta)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }
    private fun eliminarVenta(codigoVenta: String) {
        val url = "${EndPoints.DELETE_SALES}/$codigoVenta"

        val request = JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                try {
                    val mensaje = response.getString("message")
                    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()

                    // Limpiar los campos después de eliminar la venta
                    limpiarCamposVenta()
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Error al eliminar la venta", Toast.LENGTH_SHORT).show()
            }
        )

        VolleySingleton.getInstance(this).addToRequestQueue(request)
    }
    private fun limpiarCamposVenta() {
        edtCodigo.setText("")
        edtNumeroDocumento.setText("")
        edtFechaEmision.setText("")
        edtCliente.setText("")
        edtSubtotal.setText("")
        edtIgv.setText("")
        edtTotal.setText("")
        etCodigoProducto.setText("")
        edtCantidad.setText("")

    }
}
