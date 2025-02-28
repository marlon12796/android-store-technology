package com.example.proyectofinal

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapterListDialog(
    private val productList: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapterListDialog.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product_dialog_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.tvNombre.text = product.nombre
        holder.tvPrecio.text = "S/ ${product.precio}"

        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount(): Int = productList.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvNombre)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
    }
}
