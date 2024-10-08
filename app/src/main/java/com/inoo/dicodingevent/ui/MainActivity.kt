package com.inoo.dicodingevent.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inoo.dicodingevent.R
import com.inoo.dicodingevent.databinding.ActivityMainBinding
import com.inoo.dicodingevent.util.NetworkUtil

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(3000)
        installSplashScreen()
        NetworkUtil.checkInternet(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailFragment -> {
                    hideBottomNavigation()
                    supportActionBar?.show()
                }
                else -> {
                    showBottomNavigation()
                    supportActionBar?.hide()
                }
            }
        }
    }

    private fun hideBottomNavigation() {
        binding.navView.apply {
            if (visibility == BottomNavigationView.VISIBLE) {
                visibility = BottomNavigationView.GONE
            }
        }
    }

    private fun showBottomNavigation() {
        binding.navView.apply {
            if (visibility == BottomNavigationView.GONE) {
                visibility = BottomNavigationView.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        if (navController.currentDestination?.id == R.id.detailFragment) {
            navController.popBackStack(R.id.navigation_home, false)
        } else {
            super.onBackPressed()
        }
    }
}
