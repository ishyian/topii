package com.topiichat.app.features.terms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.topiichat.app.R
import com.topiichat.app.core.extension.Constants
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import javax.inject.Inject


class TermsFragment : BaseFragment() {

    @Inject
    lateinit var navigator: Navigator

    override fun layoutId() = R.layout.fragment_terms


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val urlTerms: TextView = view.findViewById(R.id.terms_url)

        urlTerms.setOnClickListener {
            val uri: Uri = Uri.parse(Constants.URL_TERMS) // missing 'http://' will cause crashed

            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        val nextScreen: Button = view.findViewById(R.id.next_screen)
        nextScreen.setOnClickListener {
            navigator = Navigator()
            navigator.showPermission(view.context)
        }
    }
}