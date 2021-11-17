package com.ivyclub.contact.ui.main.settings

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ivyclub.contact.R
import com.ivyclub.contact.databinding.FragmentDialogNotificationTimeBinding
import com.ivyclub.contact.databinding.FragmentSettingsBinding
import com.ivyclub.contact.ui.main.settings.dialog.NotificationTimeDialogFragment
import com.ivyclub.contact.ui.onboard.notification.NotificationTimeFragment
import com.ivyclub.contact.ui.onboard.notification.NotificationTimeFragmentDirections
import com.ivyclub.contact.util.BaseFragment

class SettingsFragment : BaseFragment<FragmentSettingsBinding>(R.layout.fragment_settings) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBackIcon()
        initTimeSettingButton()
    }

    private fun initBackIcon() {
        binding.ivBackIcon.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack()
        }
    }

    private fun initTimeSettingButton() {
        binding.tvNotificationTime.setOnClickListener {
            val notificationTimeDialog = NotificationTimeDialogFragment()
            notificationTimeDialog.show(childFragmentManager, NotificationTimeDialogFragment.TAG)
        }
    }
}