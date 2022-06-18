//package com.topiichat.app.features.loader
//
//import android.os.Bundle
//import android.os.Handler
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import com.topiichat.app.core.platform.BaseFragment
//import com.topiichat.app.databinding.FragmentLoaderBinding
//import javax.inject.Inject
//
//class LoaderFragment : BaseFragment<FragmentLoaderBinding>() {
//
////    @Inject
////    internal lateinit var navigator: Navigator
//
//    override val tagId: Int get() = TODO("Not yet implemented")
//
//    override fun initBinding(
//        inflater: LayoutInflater,
//        container: ViewGroup?
//    ): FragmentLoaderBinding {
//        return FragmentLoaderBinding.inflate(inflater, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        loaderStart()
//    }
//
//    private fun loaderStart() {
//        Handler().postDelayed({
//            Handler().run {
////                navigator = Navigator()
////                navigator.showTerms(requireActivity());
//            }
//
//        }, 3000)
//    }
//}