package de.jbamberger.irremote.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import de.jbamberger.irremote.R
import de.jbamberger.irremote.remote.IrRemote

class RemoteUiFragment : Fragment() {

    private val irRemotesViewModel: IrRemotesViewModel by activityViewModels()
    private var remote: IrRemote? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val remoteName = arguments?.getString(ARG_REMOTE_NAME)
        remote = irRemotesViewModel.getRemote(remoteName)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_remote, container, false)

        remote?.let {
            val tableLayout = root.findViewById<TableLayout>(R.id.remoteBase)
            IrRemoteUiInflater(it).inflate(tableLayout)
        }
        return root
    }

    companion object {
        private const val ARG_REMOTE_NAME = "remote_name"

        @JvmStatic
        fun newInstance(remoteName: String): RemoteUiFragment {
            return RemoteUiFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_REMOTE_NAME, remoteName)
                }
            }
        }
    }
}