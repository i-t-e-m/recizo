package com.recizo.view


import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import com.recizo.R
import android.preference.Preference
import android.preference.PreferenceManager
import com.recizo.module.AppContextHolder
import com.recizo.setting_avtivitys.AboutMeActivity
import com.recizo.setting_avtivitys.AlertSettingActivity
import com.recizo.setting_avtivitys.LicenceActivity

class SettingFragment : PreferenceFragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.preferences)

    // postcode setting
    val editTextPreference = findPreference("edit_postcode_key")
    editTextPreference.summary =
            PreferenceManager.getDefaultSharedPreferences(AppContextHolder.context).getString("edit_postcode_key", "")
    editTextPreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, v ->
      if(v.toString().matches("""^\d{7}$""".toRegex())){
        editTextPreference.summary = v.toString()
        true
      }else false //todo impl
    }

    // alert settings
    val alertPreference = findPreference("alert_item") as SettingItemView
    alertPreference.summary = "1日前8時"
    alertPreference.setListOnClickListener(object : SettingItemView.OnListClickListener{
      override fun onListClick() {
        activity.startActivity(Intent(activity, AlertSettingActivity::class.java))
      }
    })

    // about this app
    (findPreference("about_me_item") as SettingItemView).setListOnClickListener(
            object : SettingItemView.OnListClickListener{
              override fun onListClick() {
                activity.startActivity(Intent(activity, AboutMeActivity::class.java))
              }
            }
    )


    // ライセンス
    (findPreference("licence_item") as SettingItemView).setListOnClickListener(
            object : SettingItemView.OnListClickListener{
              override fun onListClick() {
                activity.startActivity(Intent(activity, LicenceActivity::class.java))
              }
            }
    )
  }
}
