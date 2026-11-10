package com.cse489.medialab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

/** Spinner = required first step; both selections navigate to their designated activities. */
class BroadcastFragment : Fragment(R.layout.fragment_broadcast) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val spinner = view.findViewById<Spinner>(R.id.broadcastSpinner)
        val info = view.findViewById<TextView>(R.id.broadcastOptionDescription)
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.broadcast_options, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, selectedView: View?, position: Int, id: Long
            ) {
                info.text = if (position == 0) {
                    "Write text in Step 2. Receive the custom broadcast in Step 3."
                } else {
                    "Open the battery monitor to receive live battery percentage updates."
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }

        view.findViewById<MaterialButton>(R.id.broadcastContinueButton).setOnClickListener {
            val destination = if (spinner.selectedItemPosition == 0) {
                CustomMessageActivity::class.java
            } else {
                BatteryStatusActivity::class.java
            }
            startActivity(Intent(requireContext(), destination))
        }
    }
}
