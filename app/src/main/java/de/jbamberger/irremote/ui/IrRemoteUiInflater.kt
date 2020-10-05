package de.jbamberger.irremote.ui

import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import de.jbamberger.irremote.remote.IrRemote
import de.jbamberger.irremote.remote.RemoteDefinition
import de.jbamberger.irremote.remote.VibratorWrapper
import java.util.concurrent.Executors

class IrRemoteUiInflater(private val remoteDefinition: IrRemote) {

    private val exec = Executors.newSingleThreadExecutor()

    private fun sendCommand(commandName: String) {
        exec.execute {
            remoteDefinition.sendCommand(commandName)
        }
    }

    fun inflate(tableLayout: TableLayout) {
        tableLayout.removeAllViews()

        for (row in remoteDefinition.layout.keys) {
            val tr = TableRow(tableLayout.context)
            val params = TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT, 1.0f)
            tr.layoutParams = params
            for (control in row) {
                val b = Button(tr.context)
                val buttonParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f)
                b.layoutParams = buttonParams
                if (control == null) {
                    b.visibility = View.INVISIBLE
                } else {
                    b.setOnTouchListener(RepeatListener(300, initialDelay = 700))
                    b.setOnClickListener {view -> sendCommand(view.tag as String)}
                    b.tag = control.command
                    b.text = control.name
                }
                tr.addView(b)
            }
            with(tableLayout) {
                isMeasureWithLargestChildEnabled = true
                addView(tr)
            }
        }
    }
}