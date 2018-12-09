package de.jbamberger.irremote

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {

    lateinit var remoteBase: RemoteBaseView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        remoteBase = findViewById(R.id.remoteBase)
    }

    fun addButton(view: View) {
        remoteBase.addBtn(100f, 100f)
    }
}
