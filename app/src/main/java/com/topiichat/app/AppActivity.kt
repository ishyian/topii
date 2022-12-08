package com.topiichat.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.kyc.email.data.AliceSdkListener
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
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

    private val FragmentManager.currentFragment: Fragment?
        get() = this.findFragmentById(R.id.container)

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

    @Suppress("Deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == ONBOARDING_CODE) {
            val userInfo = data?.getStringExtra("userStatus")
            userInfo?.let {
                val userId = JSONObject(it).getString("user_id")
                val currentFragment = supportFragmentManager.currentFragment
                if (currentFragment is AliceSdkListener) {
                    currentFragment.onVerificationSuccess(userId)
                }
            }
        }
    }

    companion object {
        const val ONBOARDING_CODE = 34587
    }
}