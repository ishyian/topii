package eu.siacs.conversations.ui

import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

abstract class ActionBarActivity : AppCompatActivity() {
    fun setSupportActionBar(toolbar: View?) {
        super.setSupportActionBar(toolbar as Toolbar?)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmOverloads fun configureActionBar(actionBar: ActionBar?, upNavigation: Boolean = true) {
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(upNavigation)
                actionBar.setDisplayHomeAsUpEnabled(upNavigation)
            }
        }
    }
}