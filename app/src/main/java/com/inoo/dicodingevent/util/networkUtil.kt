package com.inoo.dicodingevent.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object networkUtil {

    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }

    fun checkInternet(context: Context) {
        if (!isConnected(context)) {
            Toast.makeText(context, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show()
            showNoInternetDialog(context)
        }
    }
    private fun showNoInternetDialog(context: Context) {
        AlertDialog.Builder(context).apply {
            setTitle("Tidak Ada Koneksi")
            setMessage("Anda tidak terhubung ke internet. Silakan periksa koneksi Anda.")
            setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            create()
            show()
        }
    }
}