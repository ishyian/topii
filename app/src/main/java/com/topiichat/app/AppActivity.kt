package com.topiichat.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.topiichat.app.features.MainScreens
import dagger.hilt.android.AndroidEntryPoint
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import javax.inject.Inject

@AndroidEntryPoint
class AppActivity : AppCompatActivity() {

    private val navigator = SupportAppNavigator(this, R.id.container)

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    @Inject
    lateinit var router: Router

    private var _navController: NavController? = null
    val navController get() = _navController ?: error("nav controller exception")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        if (savedInstanceState == null) {
            router.newRootScreen(MainScreens.Splash)
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        navigatorHolder.removeNavigator()
    }
}