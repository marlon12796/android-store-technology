package com.example.proyectofinal

data class Sale(
    val codigo: String,
    val numeroDocumento: String,
    val fechaEmision: String,
    val cliente: String,
    val subtotal: Double,
    val igv: Double,
    val total: Double,
    val eliminado: Boolean,
    val detallesVenta: List<SaleDetail>
)

data class SaleDetail(
    val id: Int,
    val codigoVenta: String,
    val codigoProducto: String,
    val cantidad: Int,
    val precio: Double,
    val descuento: Double,
    val eliminado: Boolean
)
