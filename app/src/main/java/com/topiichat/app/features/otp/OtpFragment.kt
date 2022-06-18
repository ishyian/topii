package com.topiichat.app.features.otp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.chaos.view.PinView
import com.fredporciuncula.phonemoji.PhonemojiTextInputEditText
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import javax.inject.Inject


class OtpFragment  : BaseFragment() {
    override fun layoutId() = R.layout.fragment_otp
    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val input: PinView = view.findViewById(R.id.firstPinView)
        input.requestFocus()
        val nextScreen: Button = view.findViewById(R.id.next_after_otp)
        nextScreen.setOnClickListener {
            navigator = Navigator()
            navigator.showPinCode(view.context)
        }
    }

}