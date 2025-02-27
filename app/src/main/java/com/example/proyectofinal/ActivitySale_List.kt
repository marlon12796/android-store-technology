package com.example.proyectofinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import com.android.volley.Request
import com.example.appbasedatosmysql2025.EndPoints
import com.example.proyectofinal.adapters.SalesAdapter

class ActivitySaleList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SalesAdapter
    private val salesList = mutableListOf<Sale>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sale_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewSales)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)  // Mejora el rendimiento

        adapter = SalesAdapter(salesList)
        recyclerView.adapter = adapter  // 📌 IMPORTANTE: Asignar el adapter
        val backButton = findViewById<Button>(R.id.button_regresar2)
        backButton?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        fetchSales()
    }

    private fun fetchSales() {
        val url = EndPoints.GET_SALES

        val request = JsonArrayRequest(Request.Method.GET, url, null, { response ->
            parseSales(response)
        }, { error ->
            Log.e("VolleyError", "Error en la petición", error)
        })

        Volley.newRequestQueue(this).add(request)
    }

    private fun parseSales(response: JSONArray) {
        try {
            salesList.clear()
            for (i in 0 until response.length()) {
                val saleObject = response.getJSONObject(i)
                val detallesArray = saleObject.getJSONArray("detallesVenta")

                val detallesVenta = mutableListOf<SaleDetail>()
                for (j in 0 until detallesArray.length()) {
                    val detalleObject = detallesArray.getJSONObject(j)
                    val detalle = SaleDetail(
                        detalleObject.getInt("id"),
                        detalleObject.getString("codigoVenta"),
                        detalleObject.getString("codigoProducto"),
                        detalleObject.getInt("cantidad"),
                        detalleObject.getDouble("precio"),
                        detalleObject.getDouble("descuento"),
                        detalleObject.getBoolean("eliminado")
                    )
                    detallesVenta.add(detalle)
                }

                val sale = Sale(
                    saleObject.getString("codigo"),
                    saleObject.getString("numeroDocumento"),
                    saleObject.getString("fechaEmision"),
                    saleObject.getString("cliente"),
                    saleObject.getDouble("subtotal"),
                    saleObject.getDouble("igv"),
                    saleObject.getDouble("total"),
                    saleObject.getBoolean("eliminado"),
                    detallesVenta
                )
                salesList.add(sale)
            }
            adapter.notifyDataSetChanged()  // 📌 IMPORTANTE: Notificar cambios en los datos
        } catch (e: Exception) {
            Log.e("VolleyError", "Error al procesar JSON", e)
        }
    }
}
