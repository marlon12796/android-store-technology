package com.example.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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

class ProductsListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductoAdapter
    private val productList = mutableListOf<Product>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_products_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ProductoAdapter(productList)
        recyclerView.adapter = adapter

        val backButton = findViewById<Button>(R.id.button_regresar3)
        backButton?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        fetchProducts()
    }
    private fun fetchProducts() {
        val url = EndPoints.GET_PRODUCTS

        val request = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            try {
                val dataArray = response.getJSONArray("data")
                productList.clear()

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
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Log.e("VolleyError", "Error al procesar JSON", e)
            }
        }, { error ->
            Log.e("VolleyError", "Error en la petición", error)
        })

        Volley.newRequestQueue(this).add(request)
    }
}