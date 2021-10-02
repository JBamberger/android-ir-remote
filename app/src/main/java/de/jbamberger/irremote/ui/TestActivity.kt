package de.jbamberger.irremote.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import de.jbamberger.irremote.R

class TestActivity : AppCompatActivity() {

    lateinit var remoteBase: RemoteBaseView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        remoteBase = findViewById(R.id.remoteBase)

        val addButtonButton = findViewById<Button>(R.id.addButtonButton)
        addButtonButton.setOnClickListener {
            remoteBase.addBtn(100f, 100f)
        }
    }
}
