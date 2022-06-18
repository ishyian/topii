package com.topiichat.app.features.valid_phone_number

import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.core.content.ContextCompat.getSystemService
import com.fredporciuncula.phonemoji.PhonemojiTextInputEditText
import com.google.android.material.textfield.TextInputEditText
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import javax.inject.Inject


class ValidPhoneNumberFragment : BaseFragment() {

    @Inject
    lateinit var navigator: Navigator

    override fun layoutId() = R.layout.fragment_valid_phone_number

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val input: PhonemojiTextInputEditText = view.findViewById(R.id.editext)
        input.requestFocus()
        val nextScreen: Button = view.findViewById(R.id.next_after_validate)
        nextScreen.setOnClickListener {
            navigator = Navigator()
            navigator.showOtp(view.context)
        }
    }




}