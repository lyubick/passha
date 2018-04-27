package org.kgbt.passha.android

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks

import android.database.Cursor
import android.os.AsyncTask

import android.os.Bundle
import android.text.TextUtils
import android.view.View

import android.content.*
import android.util.Log
import android.widget.*
import android.support.v4.content.ContextCompat
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.content.Intent
import org.kgbt.passha.core.VaultManager
import org.kgbt.passha.core.common.Exceptions
import org.kgbt.passha.core.logger.Logger
import org.kgbt.passha.core.logger.Output
import org.kgbt.passha.android.R


class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {

    private val TAG = "LoginView"

    // UI references.
    private var aPasswordView: EditText? = null
    private var aLoginFormView: View? = null


    // HERE STARTS COPYPASTED CODE: https://stackoverflow.com/questions/20714058/file-exists-and-is-directory-but-listfiles-returns-null
    val EXTERNAL_PERMS = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    val EXTERNAL_REQUEST = 138          // This is copypasted along with request permission code. Dunno why its 138

    private fun requestForPermission(): Boolean {
        var isPermissionOn = true
        val version = Build.VERSION.SDK_INT
        if (version >= 23) {
            if (!canAccessExternalSd()) {
                isPermissionOn = false
                requestPermissions(EXTERNAL_PERMS, EXTERNAL_REQUEST)
            }
        }

        return isPermissionOn
    }

    private fun canAccessExternalSd(): Boolean {
        return hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    private fun hasPermission(perm: String): Boolean {
        return PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm)
    }

    // HERE ENDS COPYPASTED CODE


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        requestForPermission()
        // Set up the login form.
        aPasswordView = findViewById(R.id.password)
        aLoginFormView = findViewById(R.id.login_form)

        val goButton = findViewById<Button>(R.id.locate_file_button)
        goButton.setOnClickListener { attemptLogin() }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        aPasswordView!!.error = null

        // Store values at the time of the login attempt.
        val password = aPasswordView!!.text.toString()


        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            Log.e(TAG, "Password is empty!");
            aPasswordView!!.error = getString(R.string.error_field_required)
            aPasswordView!!.requestFocus()
        } else {
            Log.v(TAG, "Try authentication");
            try {
                Logger.loggerON(null, PasshaLogsOutput())
                VaultManager.init()
                val vault = VaultManager.getInstance().addVault(password, false)
                val intent = Intent(baseContext, VaultActivity::class.java);
                intent.putExtra("VAULT_NAME", vault.name)
                startActivity(intent)
            } catch (e: Exceptions) {
                Log.e(TAG, "Error", e)
                aPasswordView!!.error = getString(R.string.error_incorrect_password)
                aPasswordView!!.requestFocus()
            }
        }
    }

    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> {
        // Not sure if this is required, or can just pass null
        return CursorLoader(this)
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }
}

