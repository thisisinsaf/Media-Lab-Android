package com.cse489.medialab

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView

/** Hosts the four rubric-required fragments inside one navigation-drawer activity. */
class MainActivity : AppCompatActivity() {
    private lateinit var drawer: DrawerLayout
    private lateinit var navigation: NavigationView
    private lateinit var toolbar: MaterialToolbar
    private var currentDestination = R.id.nav_broadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawer = findViewById(R.id.drawerLayout)
        navigation = findViewById(R.id.navigationView)
        toolbar = findViewById(R.id.topToolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_open, R.string.navigation_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigation.setNavigationItemSelectedListener { item ->
            showDestination(item.itemId)
            drawer.closeDrawers()
            true
        }

        currentDestination = savedInstanceState?.getInt(KEY_DESTINATION) ?: R.id.nav_broadcast
        if (savedInstanceState == null) {
            showDestination(currentDestination)
        } else {
            navigation.setCheckedItem(currentDestination)
            toolbar.title = titleFor(currentDestination)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawer.isDrawerOpen(androidx.core.view.GravityCompat.START)) {
                    drawer.closeDrawers()
                } else if (currentDestination != R.id.nav_broadcast) {
                    showDestination(R.id.nav_broadcast)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun showDestination(id: Int) {
        val fragment: Fragment = when (id) {
            R.id.nav_image -> ImageScaleFragment()
            R.id.nav_video -> VideoFragment()
            R.id.nav_audio -> AudioFragment()
            else -> BroadcastFragment()
        }
        currentDestination = id
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHost, fragment)
            .commit()
        navigation.setCheckedItem(id)
        toolbar.title = titleFor(id)
    }

    private fun titleFor(id: Int): String = getString(
        when (id) {
            R.id.nav_image -> R.string.nav_image
            R.id.nav_video -> R.string.nav_video
            R.id.nav_audio -> R.string.nav_audio
            else -> R.string.nav_broadcast
        }
    )

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_DESTINATION, currentDestination)
        super.onSaveInstanceState(outState)
    }

    private companion object {
        const val KEY_DESTINATION = "current_destination"
    }
}
