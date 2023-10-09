package athallah.neardeal

import android.content.Intent
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import athallah.neardeal.api.EndpointFactory
import athallah.neardeal.databinding.ActivityMainBinding
import athallah.neardeal.sharedPrefs.LoginSharedPref
import athallah.neardeal.utils.dismissLoading
import athallah.neardeal.utils.executeApi
import athallah.neardeal.utils.showAlert
import athallah.neardeal.utils.showLoading

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_my_cart
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.logout_menu -> {
                showAlert(
                    title = "logout",
                    message = "are you sure?",
                    listener = { logout()}
                )
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val endpoint = EndpointFactory.userEndPoint
        val LoginSharedPref = LoginSharedPref(this)
        showLoading("Logging out")
        endpoint.logout(LoginSharedPref.tokenBearer).executeApi(
            this,
            onSuccess = {
                dismissLoading()
                startLoginActivity()
                LoginSharedPref.clear()
            },
            onFailed = {
                dismissLoading()
                showAlert(message = it.message?: "Unknown error occured")
            }
        )
    }

    private fun startLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}