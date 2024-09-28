package com.inoo.dicodingevent.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.inoo.dicodingevent.R
import com.inoo.dicodingevent.databinding.ActivityMainBinding
import com.inoo.dicodingevent.ui.home.HomeFragmentDirections
import com.inoo.dicodingevent.ui.notification.DailyReminderWorker
import com.inoo.dicodingevent.ui.setting.SettingPreferences
import com.inoo.dicodingevent.ui.setting.ViewModelFactory
import com.inoo.dicodingevent.ui.setting.dataStore
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: Toolbar
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(applicationContext, SettingPreferences.getInstance(applicationContext.dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scheduleDailyReminder()
        Thread.sleep(3000)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavView: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_upcoming, R.id.navigation_finished, R.id.navigation_setting)
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_detail -> {
                    hideBottomNavigation()
                    supportActionBar?.show()
                }
                else -> {
                    showBottomNavigation()
                    supportActionBar?.hide()
                }
            }
        }

        viewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                setDefaultNightMode(MODE_NIGHT_YES)
            } else {
                setDefaultNightMode(MODE_NIGHT_NO)
            }
        }

        if (savedInstanceState == null) {
            intent?.let {
                val eventId = it.getStringExtra("eventId")
                if (eventId != null) {
                    val action = HomeFragmentDirections.actionNavigationHomeToNavigationDetail(eventId.toInt())
                    navController.navigate(action)
                }
            }
        }

    }

    fun hideBottomNavigation() {
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
        if (navController.currentDestination?.id == R.id.navigation_detail) {
            navController.popBackStack(R.id.navigation_home, false)
        } else {
            super.onBackPressed()
        }
    }

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)
    }
}
