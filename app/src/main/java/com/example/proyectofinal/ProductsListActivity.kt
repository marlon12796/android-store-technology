package com.example.proyectofinal

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request
import com.android.volley.toolbox.Volley
import com.example.appbasedatosmysql2025.EndPoints
import org.json.JSONObject

class ProductsListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private lateinit var etCodigoBusqueda: EditText
    private lateinit var btnBuscar: Button
    private val productList = mutableListOf<Product>()
    private val filteredList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_products_list)

        initViews()
        setupRecyclerView()
        setupListeners()
        fetchProducts()
    }

    private fun initViews() {
        etCodigoBusqueda = findViewById(R.id.etCodigoBusqueda)
        btnBuscar = findViewById(R.id.btnBuscar)
        recyclerView = findViewById(R.id.recyclerView)
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductoAdapter(filteredList)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        btnBuscar.setOnClickListener { buscarProducto() }

        val backButton = findViewById<Button>(R.id.button_regresar3)
        backButton?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun fetchProducts() {
        val url = EndPoints.GET_PRODUCTS
        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            handleProductResponse(response)
        }, { error ->
            Log.e("VolleyError", "Error en la petición", error)
        })

        Volley.newRequestQueue(this).add(request)
    }

    private fun handleProductResponse(response: JSONObject) {
        try {
            val dataArray = response.getJSONArray("data")
            productList.clear()
            filteredList.clear()

            for (i in 0 until dataArray.length()) {
                val item = dataArray.getJSONObject(i)
                val product = Product(
                    item.getString("codigo"),
                    item.getString("nombre"),
                    item.getString("descripcion"),
                    item.getDouble("precio"),
                    item.getInt("stock"),
                    item.getBoolean("activo"),
                    item.getString("categoria")
                )
                productList.add(product)
            }

            filteredList.addAll(productList)
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            Log.e("VolleyError", "Error al procesar JSON", e)
        }
    }

    private fun buscarProducto() {
        val query = etCodigoBusqueda.text.toString().trim()

        if (query.isEmpty()) {
            resetFilteredList()
        } else {
            filterProducts(query)
        }
    }

    private fun resetFilteredList() {
        filteredList.clear()
        filteredList.addAll(productList)
        adapter.notifyDataSetChanged()
    }

    private fun filterProducts(query: String) {
        filteredList.clear()
        filteredList.addAll(productList.filter {
            it.codigo.contains(query, ignoreCase = true) || it.nombre.contains(query, ignoreCase = true)
        })

        if (filteredList.isEmpty()) {
            mostrarError("Producto no encontrado")
        }

        adapter.notifyDataSetChanged()
    }

    private fun mostrarError(mensaje: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(mensaje)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
