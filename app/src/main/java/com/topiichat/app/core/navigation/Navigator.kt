//package com.topiichat.app.core.navigation
//
//import android.content.Context
//import android.view.View
//import com.topiichat.app.features.activate_permission.PermissionActivity
//import com.topiichat.app.features.home.HomeActivity
//import com.topiichat.app.features.loader.LoaderActivity
//import com.topiichat.app.features.otp.OtpActivity
//import com.topiichat.app.features.pin_code.PinCodeActivity
//import com.topiichat.app.features.signup.SignupActivity
//import com.topiichat.app.features.splash.SplashActivity
//import com.topiichat.app.features.terms.TermsActivity
//import com.topiichat.app.features.valid_phone_number.ValidPhoneNumberActivity
//
//import javax.inject.Inject
//import javax.inject.Singleton
//
//
//@Singleton
//class Navigator
//@Inject constructor() {
//
//    fun showSignUp(context: Context) =
//        context.startActivity(SignupActivity.callingIntent(context))
//    fun showLoader(context: Context) =
//        context.startActivity(LoaderActivity.callingIntent(context))
//    fun showSplash(context: Context) =
//        context.startActivity(SplashActivity.callingIntent(context))
//    fun showTerms(context: Context) =
//        context.startActivity(TermsActivity.callingIntent(context))
//    fun showPermission(context: Context) =
//        context.startActivity(PermissionActivity.callingIntent(context))
//    fun showOtp(context: Context) =
//        context.startActivity(OtpActivity.callingIntent(context))
//    fun showPinCode(context: Context) =
//        context.startActivity(PinCodeActivity.callingIntent(context))
//    fun showHome(context: Context) =
//        context.startActivity(HomeActivity.callingIntent(context))
//    fun showValidatePhone(context: Context) =
//        context.startActivity(ValidPhoneNumberActivity.callingIntent(context))
//
////    fun showMain(context: Context) {
////        when (authenticator.userLoggedIn()) {
////            true -> showMovies(context)
////            false -> showLogin(context)
////        }
//}
//
//class Extras(val transitionSharedElement: View)
//
//
