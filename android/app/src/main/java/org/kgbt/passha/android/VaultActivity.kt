package org.kgbt.passha.android

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import org.kgbt.passha.core.VaultManager
import org.kgbt.passha.core.db.SpecialPassword
import org.kgbt.passha.core.db.Vault
import org.kgbt.passha.android.R


class VaultActivity : AppCompatActivity() {

    private var aListView: ListView? = null
    private var aEditTextView: EditText? = null
    private var aVault: Vault? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vault)

        aListView = findViewById(R.id.password_list)
        aEditTextView = findViewById(R.id.filter_field)

        val vaultName = intent.getStringExtra("VAULT_NAME")
        aVault = VaultManager.getInstance().activateVault(vaultName)

        val adapter = FilteredListAdapter(this, R.layout.passwords_list, aVault!!.passwords.toMutableList())
        aListView!!.adapter = adapter

        aEditTextView!!.addTextChangedListener(FilterChangeWatcher(adapter))

        aListView!!.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val entry = parent!!.getItemAtPosition(position) as SpecialPassword
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(entry.name, entry.password)
                clipboard.primaryClip = clip
                moveTaskToBack(true)
            }
        }
    }
}
