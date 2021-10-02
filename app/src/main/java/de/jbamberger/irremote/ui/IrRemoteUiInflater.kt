/*
 *    Copyright 2021 Jannik Bamberger
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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