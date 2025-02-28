package com.example.appbasedatosmysql2025


object EndPoints {
    private val URL_ROOT = "http://192.168.18.6:4500/"
    val VERIFY_USER = URL_ROOT + "users/login"
    val GET_PRODUCTS = URL_ROOT + "products"
    val GET_SALES = URL_ROOT+"sales"
    val DELETE_SALES= URL_ROOT+"sales"
}