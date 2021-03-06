package com.stepango.okspam

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {

                // Launch service right away - the user has already previously granted permission
                launchMainService()
            } else {

                // Check that the user has granted permission, and prompt them if not
                checkDrawOverlayPermission()
            }
        } else {
            launchMainService()
        }
    }

    private fun launchMainService() {

        val svc = Intent(this, MainService::class.java)
        svc.putExtras(Bundle().apply {
            putParcelable(KEY_SPAM, SpamRequest(
                "https://i.giphy.com/Myp2C0iWDMh2g.gif",
                message = "Silliness!"
            ))
        })
        stopService(svc)
        startService(svc)

        finish()
    }

    private fun checkDrawOverlayPermission() {

        // Checks if app already has permission to draw overlays
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))

            // Launch Intent, with the supplied request code
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {

        // Check if a request code is received that matches that which we provided for the overlay draw request
        if (requestCode == REQUEST_CODE) {

            // Double-check that the user granted it, and didn't just dismiss the request
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&  Settings.canDrawOverlays(this)) {

                // Launch the service
                launchMainService()
            } else {

                Toast.makeText(this, "Sorry. Can't draw overlays without permission...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        const val REQUEST_CODE = 10101
    }

}
