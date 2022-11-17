package com.topiichat.app.features.chats.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.yourbestigor.chat.R;
import com.yourbestigor.chat.databinding.ActivityPickServerBinding;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import eu.siacs.conversations.entities.Account;
import eu.siacs.conversations.ui.XmppActivity;

public class PickServerActivity extends XmppActivity {

    @Override
    protected void refreshUiReal() {

    }

    @Override
    public void onBackendConnected() {

    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, eu.siacs.conversations.ui.WelcomeActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, WelcomeActivity.class));
        super.onBackPressed();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            setIntent(intent);
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityPickServerBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pick_server);
        setSupportActionBar(binding.toolbar);
        configureActionBar(getSupportActionBar());
        binding.useCim.setOnClickListener(v -> {
            final Intent intent = new Intent(this, MagicCreateActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
        binding.useOwnProvider.setOnClickListener(v -> {
            List<Account> accounts = xmppConnectionService.getAccounts();
            Intent intent = new Intent(this, EditAccountActivity.class);
            intent.putExtra(EditAccountActivity.EXTRA_FORCE_REGISTER, true);
            if (accounts.size() == 1) {
                intent.putExtra("jid", accounts.get(0).getJid().asBareJid().toString());
                intent.putExtra("init", true);
            } else if (accounts.size() >= 1) {
                //intent = new Intent(this, ManageAccountActivity.class);
                intent.putExtra("jid", accounts.get(0).getJid().asBareJid().toString());
                intent.putExtra("init", true);
            }
            startActivity(intent);
        });

    }


    public static void launch(AppCompatActivity activity) {
        Intent intent = new Intent(activity, PickServerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

}
