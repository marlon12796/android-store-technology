package com.example.proyectofinal

import android.app.AlertDialog
import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
open class BaseActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu?.let {
            if (it.javaClass.simpleName.equals("MenuBuilder", ignoreCase = true)) {
                try {
                    val method = it.javaClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.javaPrimitiveType)
                    method.isAccessible = true
                    method.invoke(it, true)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.about_us -> {
                startActivity(Intent(this, AboutUs::class.java))
            }
            R.id.close_up -> {
                mostrarDialogoSalir()
            }
            R.id.google_maps -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            R.id.list_products -> {
                startActivity(Intent(this,ProductsListActivity::class.java))
            }
            R.id.list_ventas_menu -> {

                startActivity(Intent(this,ActivitySaleList::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun mostrarDialogoSalir() {
        AlertDialog.Builder(this)
            .setTitle("Salir de la aplicación")
            .setMessage("¿Estás seguro de que quieres salir?")
            .setPositiveButton("Sí") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("No", null)
            .show()
    }
}
