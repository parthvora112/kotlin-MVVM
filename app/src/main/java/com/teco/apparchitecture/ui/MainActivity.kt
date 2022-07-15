package com.teco.apparchitecture.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.teco.apparchitecture.R
import com.teco.apparchitecture.listener.AppLifeCycle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var appLifeCycle: AppLifeCycle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(R.layout.activity_main)
        initDefaultActionBar()
        initLifeCycleObserver()
    }

    private fun initLifeCycleObserver() {
        lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                appLifeCycle?.onPauseApplication()
            }

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                appLifeCycle?.onStartApplication()
            }

        })
    }

    private fun initDefaultActionBar() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
        val navController = navHostFragment?.navController
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        setSupportActionBar(toolbar)
        val appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.homeFragment, R.id.chatListFragment)).build()
        toolbar.setupWithNavController(navController!!, appBarConfiguration)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    fun setAppLifeCycleLister(appLifeCycle: AppLifeCycle) {
        this.appLifeCycle = appLifeCycle
    }
}