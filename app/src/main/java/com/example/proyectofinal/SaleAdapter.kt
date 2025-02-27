package com.example.proyectofinal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectofinal.R
import com.example.proyectofinal.Sale
import java.util.Locale
import java.text.SimpleDateFormat

class SalesAdapter(private val salesList: List<Sale>) : RecyclerView.Adapter<SalesAdapter.SalesViewHolder>() {

    class SalesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtCodigoVenta: TextView = itemView.findViewById(R.id.txtCodigoVenta)
        val txtCliente: TextView = itemView.findViewById(R.id.txtCliente)
        val txtFechaEmision: TextView = itemView.findViewById(R.id.txtFechaEmision)
        val txtProducto: TextView = itemView.findViewById(R.id.txtProducto)
        val txtCantidad: TextView = itemView.findViewById(R.id.txtCantidad)
        val txtSubtotal: TextView = itemView.findViewById(R.id.txtSubtotal)
        val txtIgv: TextView = itemView.findViewById(R.id.txtIgv)
        val txtTotal: TextView = itemView.findViewById(R.id.txtTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale_layout, parent, false)
        return SalesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesViewHolder, position: Int) {
        val sale = salesList[position]
        val formattedDate = formatFecha(sale.fechaEmision)
        // ✅ Asignamos los valores correctamente
        holder.txtCodigoVenta.text = "Código de Venta: ${sale.codigo}"
        holder.txtCliente.text = "Cliente: ${sale.cliente}"
        holder.txtFechaEmision.text = "Fecha de Emisión: $formattedDate"

        // ✅ Mostramos el primer producto de la lista de detalles
        if (sale.detallesVenta.isNotEmpty()) {
            holder.txtProducto.text = "Producto: ${sale.detallesVenta[0].codigoProducto}"
            "Cantidad: ${sale.detallesVenta[0].cantidad}".also { holder.txtCantidad.text = it }
        } else {
            "Producto: No disponible".also { holder.txtProducto.text = it }
            holder.txtCantidad.text = "Cantidad: 0"
        }

        // ✅ Formateamos los valores monetarios correctamente
        "Subtotal: S/. ${String.format(Locale("es", "PE"), "%.2f", sale.subtotal)}".also { holder.txtSubtotal.text = it }
        "IGV: S/. ${String.format(Locale("es", "PE"), "%.2f", sale.igv)}".also { holder.txtIgv.text = it }
        "Total: S/. ${String.format(Locale("es", "PE"), "%.2f", sale.total)}".also { holder.txtTotal.text = it }
    }

    override fun getItemCount(): Int = salesList.size
    private fun formatFecha(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // Formato original
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "PE")) // Formato deseado
            val date = inputFormat.parse(fecha)
            outputFormat.format(date!!) // Convertir a String formateado
        } catch (e: Exception) {
            "Fecha no disponible"
        }
    }
}
