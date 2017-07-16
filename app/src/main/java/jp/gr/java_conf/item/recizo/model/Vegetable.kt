package jp.gr.java_conf.item.recizo.model

/**
 * Created by user on 17/07/11.
 */
data class Vegetable(val id: Int, val name: String, val memo: String, val date: String) {
  constructor(id: Int, name: String, memo: String, year: String, month: String, day: String)
      : this (id, name, memo, "$year/$month/$day")
}