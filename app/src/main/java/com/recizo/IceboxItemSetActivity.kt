package com.recizo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.recizo.model.entity.IceboxItem
import com.recizo.module.IceboxDao
import com.recizo.presenter.ItemCategoryAdapter
import kotlinx.android.synthetic.main.activity_icebox_item_set.*

class IceboxItemSetActivity : AppCompatActivity() {
  var item: IceboxItem? = null

  private fun onSetClicked() {
    if (et_name.text.isBlank()) {
      AlertDialog.Builder(this)
          .setMessage("名前が入力されていません!")
          .setPositiveButton("OK", null)
          .show()
    } else {
      val newItem = IceboxItem(
          id = item?.id ?: 0,
          name = et_name.text.toString(),
          memo = et_memo.text.toString(),
          year = date_picker.year.toString(),
          month = (date_picker.month + 1).toString(),
          day = date_picker.dayOfMonth.toString(),
          category = spinner.selectedItem as IceboxItem.Category)
      if (item == null) IceboxDao.add(newItem)
      else IceboxDao.update(newItem)
      finish()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_icebox_item_set)
    item = intent.getSerializableExtra("item") as IceboxItem?
    spinner.adapter = ItemCategoryAdapter(this)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayShowTitleEnabled(false)

    if (item != null) {
      spinner.setSelection(item!!.category.ordinal)
      et_name.setText(item!!.name)
      et_memo.setText(item!!.memo)
      val dates = item!!.date.split("/")
      val year = dates[0].toInt()
      val month = dates[1].toInt() - 1
      val day = dates[2].toInt()
      date_picker.updateDate(year, month, day)
      nav_set_btn.text = "変更"
    } else nav_set_btn.text = "登録"
    nav_back_btn.setOnClickListener { finish() }
    nav_set_btn.setOnClickListener { onSetClicked() }
  }
}
