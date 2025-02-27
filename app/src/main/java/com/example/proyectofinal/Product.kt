package com.example.proyectofinal

data class Product(
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val activo: Boolean,
    val categoria: String
)