package com.ivyclub.contact.ui.main.friend

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.ivyclub.contact.R
import com.ivyclub.contact.databinding.FragmentFriendBinding
import com.ivyclub.contact.ui.main.friend.dialog.AddGroupDialogFragment
import com.ivyclub.contact.ui.main.friend.dialog.SelectGroupFragment
import com.ivyclub.contact.util.BaseFragment
import com.ivyclub.contact.util.changeVisibilityWithDirection
import com.ivyclub.contact.util.hideKeyboard
import com.ivyclub.contact.util.showKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendFragment : BaseFragment<FragmentFriendBinding>(R.layout.fragment_friend) {

    private val viewModel: FriendViewModel by viewModels()
    private val onBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when {
                    viewModel.isSearchViewVisible.value == true -> {
                        viewModel.setSearchViewVisibility()
                        initFriendList()
                    }
                    viewModel.isInLongClickedState.value == true -> {
                        friendListAdapter.setAllClickedClear(viewModel.longClickedId)
                        viewModel.clearLongClickedId()
                    }
                    else -> {
                        requireActivity().finish()
                    }
                }
            }
        }
    }
    private val friendListAdapter: FriendListAdapter by lazy {
        FriendListAdapter(
            onGroupClick = viewModel::manageGroupFolded,
            onFriendClick = this::navigateToFriendDetailFragment,
            onFriendLongClick = viewModel::setLongClickedId
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.viewModel = viewModel
        initBackPressedCallback()
        initAddButton()
        initSettingsButton()
        initClearButton()
        initFriendListAdapter()
        observeSearchViewVisibility()
        observeFriendList()
        getGroupSelectFragmentResult()
    }

    override fun onDetach() {
        super.onDetach()
        onBackPressedCallback.remove()
    }

    fun loadFriendList() {
        viewModel.getFriendDataWithFlow()
    }

    private fun initBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun initAddButton() = with(binding) {
        ivAddFriendIcon.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), it)
            val menuInflater = popupMenu.menuInflater
            if (friendListAdapter.isOneOfItemLongClicked()) {
                menuInflater.inflate(R.menu.menu_set_friends_at_friendlist, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.item_move_friends_to -> {
                            SelectGroupFragment().show(
                                childFragmentManager,
                                SelectGroupFragment.TAG
                            )
                        }
                    }
                    false
                }
            } else {
                menuInflater.inflate(R.menu.menu_friend_and_group, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.item_new_friend -> {
                            findNavController().navigate(R.id.action_navigation_friend_to_addFriendFragment)
                        }
                        R.id.item_new_group -> {
                            showDialog()
                        }
                    }
                    false
                }
            }
            popupMenu.show()
        }
    }

    private fun initSettingsButton() = with(binding) {
        ivSettingsIcon.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_friend_to_settingsFragment)
        }
    }

    private fun initClearButton() = with(binding) {
        ivRemoveEt.setOnClickListener {
            etSearch.setText("")
            initFriendList()
        }
    }

    private fun showDialog() {
        AddGroupDialogFragment().show(childFragmentManager, ADD_GROUP_DIALOG_TAG)
    }

    private fun initFriendListAdapter() {
        binding.rvFriendList.adapter = friendListAdapter
    }

    private fun observeSearchViewVisibility() {
        viewModel.isSearchViewVisible.observe(viewLifecycleOwner) { newVisibilityState ->
            with(binding) {
                if (newVisibilityState) { // 안보이던 상황에서 -> 보이던 상황으로 될 때
                    showKeyboard()
                    etSearch.changeVisibilityWithDirection(
                        Gravity.TOP,
                        View.VISIBLE,
                        ANIMATION_TIME,
                        this@FriendFragment::requestFocus
                    )
                } else { // 보이던 상황에서 -> 안보이던 상황으로 될 때
                    hideKeyboard()
                    etSearch.changeVisibilityWithDirection(
                        Gravity.TOP,
                        View.GONE,
                        ANIMATION_TIME
                    )
                    etSearch.text.clear()
                    ivRemoveEt.visibility = View.GONE
                }
            }
        }
    }

    private fun requestFocus() {
        binding.etSearch.requestFocus()
    }

    private fun observeFriendList() {
        viewModel.friendList.observe(viewLifecycleOwner) { newFriendList ->
            // 새로운 리스트로 리사이클러뷰 갱신
            friendListAdapter.submitList(newFriendList)
        }
    }

    private fun navigateToFriendDetailFragment(friendId: Long) {
        findNavController().navigate(
            FriendFragmentDirections.actionNavigationFriendToFriendDetailFragment(
                friendId
            )
        )
    }

    private fun getGroupSelectFragmentResult() {
        childFragmentManager.setFragmentResultListener("requestKey", this) { key, bundle ->
            val result = bundle.getString("bundleKey")
            viewModel.updateFriendsGroup(result) // 뷰모델에서 클릭 된 아이템 처리 해제
            friendListAdapter.clearLongClickedItemCount() // 리스트 어댑터에서 클릭 된 아이템 처리 해제
            Snackbar.make(binding.root, "성공적으로 옮겨졌습니다.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun initFriendList() {
        friendListAdapter.submitList(viewModel.getOrderedEntireFriendList()) {
            binding.rvFriendList.scrollToPosition(0)
        }
    }

    companion object {
        private const val ANIMATION_TIME = 150L
        private const val ADD_GROUP_DIALOG_TAG = "AddGroupDialog"
    }
}