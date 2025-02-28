package com.example.proyectofinal
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductoAdapter(private val listaProductos: List<Product>) :
    RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder>() {

    class ProductoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val codigo: TextView = view.findViewById(R.id.tvCodigo)
        val nombre: TextView = view.findViewById(R.id.tvNombre)
        val descripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val precio: TextView = view.findViewById(R.id.tvPrecio)
        val stock: TextView = view.findViewById(R.id.tvStock)
        val categoria: TextView = view.findViewById(R.id.tvCategoria)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductoViewHolder, position: Int) {
        val producto = listaProductos[position]
        holder.codigo.text = "Código: ${producto.codigo}"
        holder.nombre.text = producto.nombre
        holder.descripcion.text = producto.descripcion
        holder.precio.text = "S/. ${producto.precio}"
        holder.stock.text = "Stock: ${producto.stock}"
        holder.categoria.text = "Categoría: ${producto.categoria}"
    }

    override fun getItemCount(): Int = listaProductos.size
}

