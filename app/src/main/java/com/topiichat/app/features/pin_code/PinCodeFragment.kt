package com.topiichat.app.features.pin_code

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import javax.inject.Inject


class PinCodeFragment : BaseFragment() {
    @Inject
    lateinit var navigator: Navigator

    override fun layoutId() = R.layout.fragment_pin_code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val input: EditText = view.findViewById(R.id.editText1)
        val showHideBtn: ImageView = view.findViewById(R.id.show_pass_btn)
        input.requestFocus()
        var pwdCheck: Boolean = false

        showHideBtn.setOnClickListener {
            if(pwdCheck==false){
                input.transformationMethod = HideReturnsTransformationMethod.getInstance()
                pwdCheck = true;
                showHideBtn.setImageResource(R.drawable.ic_password_yes);
            } else{
                pwdCheck = false;
                input.transformationMethod = PasswordTransformationMethod.getInstance()
                showHideBtn.setImageResource(R.drawable.ic_password_not);

            }
        }
    }


}