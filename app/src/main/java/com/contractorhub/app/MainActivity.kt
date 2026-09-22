package com.contractorhub.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.contractorhub.app.databinding.ActivityMainBinding

/**
 * Single Activity — सर्व screens Navigation Component च्या Fragments म्हणून
 * यातच होस्ट होतात (PART 5: Architecture — UI → ViewModel → Repository → Room).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // बॉटम नॅव्हवर न दिसणारे "पूर्ण-स्क्रीन फॉर्म" destinations — इथे नवीन
    // Add/Edit screen जोडाल की bottom nav आपोआप लपेल (Sites, Labour, Bills...).
    private val fullScreenDestinations = setOf(
        R.id.addEditSiteFragment,
        R.id.clientsFragment,
        R.id.addEditClientFragment,
        R.id.contactsFragment,
        R.id.addEditContactFragment,
        R.id.labourListFragment,
        R.id.addEditLabourFragment,
        R.id.labourProfileFragment,
        R.id.materialListFragment,
        R.id.addEditMaterialFragment,
        R.id.materialProfileFragment,
        R.id.billListFragment,
        R.id.addEditBillFragment,
        R.id.billCameraFragment,
        R.id.expenseListFragment,
        R.id.addEditExpenseFragment,
        R.id.clientPaymentListFragment,
        R.id.addEditClientPaymentFragment,
        R.id.diaryFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id !in fullScreenDestinations
        }
    }
}
