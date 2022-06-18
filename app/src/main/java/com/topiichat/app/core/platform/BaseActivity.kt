//package com.topiichat.app.core.platform
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.topiichat.app.R
//import com.topiichat.app.core.extension.inTransaction
//
//
//import dagger.hilt.android.AndroidEntryPoint
//
//
///**
// * Base Activity class with helper methods for handling fragment transactions and back button
// * events.
// *
// * @see AppCompatActivity
// */
//@AndroidEntryPoint
//abstract class BaseActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_layout)
//
////        addFragment(savedInstanceState)
//    }
//
//    override fun onBackPressed() {
////        (supportFragmentManager.findFragmentById(R.id.fragmentContainer) as BaseFragment).onBackPressed()
////        super.onBackPressed()
//    }
//
////    private fun addFragment(savedInstanceState: Bundle?) =
////        savedInstanceState ?: supportFragmentManager.inTransaction {
////            add(
////                R.id.fragmentContainer,
////                fragment()
////            )
////        }
//
////    abstract fun fragment(): BaseFragment
//}
