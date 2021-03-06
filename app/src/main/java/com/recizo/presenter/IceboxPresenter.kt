package com.recizo.presenter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import com.daimajia.swipe.SwipeLayout
import com.recizo.model.entity.IceboxItem
import com.recizo.module.IceboxDao
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class IceboxPresenter(private val activity: Activity, val fragment: IceboxButtons) {
  var onLengthChangeListener: ListLengthChangeListener? = null
  private var searchList = mutableSetOf<Long>()
  private var garbageList = mutableSetOf<Long>()
  private var recyclerView: RecyclerView? = null
  private var sort = Sort.CREATED
  private val iceboxAdapter = IceboxAdapter()
  init {
    iceboxAdapter.setEventListener(object : IceboxAdapter.EventListener {
      override fun onViewAttached(holder: IceboxAdapter.IceboxViewHolder) { checkHolder(holder) }
      override fun onBindViewHolder(holder: IceboxAdapter.IceboxViewHolder, position: Int) {}
      override fun onItemOpen(dragEdge: SwipeLayout.DragEdge, itemId: Long) {
        when(dragEdge) {
          SwipeLayout.DragEdge.Left -> {
            searchList.add(itemId)
            fragment.changeBtnVisibility(search = true, undo = true)
          }
          SwipeLayout.DragEdge.Right -> {
            garbageList.add(itemId)
            fragment.changeBtnVisibility(delete = true, undo = true)
          }
          else -> return
        }
      }
      override fun onItemClosed(itemId: Long) {
        garbageList.remove(itemId)
        searchList.remove(itemId)
        if (garbageList.size == 0 && searchList.size == 0) fragment.changeBtnVisibility(add = true)
      }
      override fun onItemClicked(item: IceboxItem) { fragment.toIceboxItemSetActivity(item) }
      override fun onDeleteClicked(itemId: Long) { removeItem(itemId) }
      override fun onSearchClicked(item: IceboxItem) {
        activity.fragmentManager.beginTransaction().addToBackStack("icebox").commit()
        fragment.toSearchActivity(getSearchItemList())
      } // todo アイテム１つで検索？
    })
  }

  fun setRecyclerView(view: RecyclerView) {
    recyclerView = view
    view.adapter = iceboxAdapter
    dataUpdated()
  }

  fun dataUpdated() {
    val list = IceboxDao.getAll().toMutableList()
    iceboxAdapter.setItemList(sortList(list, sort))
    fragment.changeBtnVisibility(add = true)
    checkHolders()
    onLengthChangeListener?.onChange(iceboxAdapter.itemCount -1)
  }

  fun sortItems(type: Sort) {
    sort = type
    iceboxAdapter.setItemList(sortList(iceboxAdapter.getItemList(), sort))
    searchList.clear()
    garbageList.clear()
    fragment.changeBtnVisibility(add = true)
    fragment.onSortMethodChange(sort)
  }

  fun onUndoClicked() {
    searchList.plus(garbageList).map {
      val item = recyclerView?.findViewHolderForItemId(it)
      if(item != null) (item as IceboxAdapter.IceboxViewHolder).swipeLayout.close()
    }
    searchList.clear()
    garbageList.clear()
    fragment.changeBtnVisibility(add = true)
  }

  fun onDeleteClicked() {
    garbageList.map {
      val id = it
      IceboxDao.delete(id.toInt())
      val index: Int = iceboxAdapter.getItemList().indexOfFirst { it.id.toLong() == id }
      iceboxAdapter.removeItem(index)
    }
    garbageList.clear()
    onLengthChangeListener?.onChange(iceboxAdapter.itemCount -1)
    if(searchList.size != 0) fragment.changeBtnVisibility(search = true)
    else fragment.changeBtnVisibility(add = true)
  }

  fun getSearchItemList(): Set<String> =
      searchList.map { id -> iceboxAdapter.getItemList().first { it.id.toLong() == id }.name }.toSet()

  private fun checkHolder(holder: IceboxAdapter.IceboxViewHolder) = launch(UI) {
    delay(100)
    if(garbageList.contains(holder.itemId)) holder.swipeLayout.open(true, false, SwipeLayout.DragEdge.Right)
    else if(searchList.contains(holder.itemId)) holder.swipeLayout.open(true, false, SwipeLayout.DragEdge.Left)
    else holder.swipeLayout.close(false)
  }

  private fun checkHolders() = launch(UI) {
    delay(100)
    garbageList.map {
      val item = recyclerView?.findViewHolderForItemId(it) as IceboxAdapter.IceboxViewHolder?
      item?.swipeLayout?.open(SwipeLayout.DragEdge.Right)
    }
    searchList.map {
      val item = recyclerView?.findViewHolderForItemId(it) as IceboxAdapter.IceboxViewHolder?
      item?.swipeLayout?.open(SwipeLayout.DragEdge.Left)// false にするとLeft方向にだけ動かない
    }
  }

  private fun sortList(list: List<IceboxItem>, type: Sort): List<IceboxItem> = when(type) {
    Sort.NAME -> list.sortedBy { it.name }
    Sort.DATE -> list.sortedBy { it.date }
    Sort.CATEGORY -> list.sortedBy { it.category }
    Sort.CREATED -> list.sortedBy { it.id }
  }

  private fun removeItem(id: Long) {
    IceboxDao.delete(id.toInt())
    garbageList.remove(id)
    val index: Int = iceboxAdapter.getItemList().indexOfFirst { it.id.toLong() == id }
    iceboxAdapter.removeItem(index)
    onLengthChangeListener?.onChange(iceboxAdapter.itemCount -1)
    if(garbageList.size == 0) fragment.changeBtnVisibility(add = true)
  }

  enum class Sort {
    NAME, DATE, CATEGORY, CREATED
  }

  interface ListLengthChangeListener {
    fun onChange(len: Int)
  }

  interface IceboxButtons {
    fun changeBtnVisibility(add: Boolean = false, undo: Boolean = false, search: Boolean = false, delete: Boolean = false)
    fun toIceboxItemSetActivity(item: IceboxItem)
    fun toSearchActivity(set: Set<String>)
    fun onSortMethodChange(type: Sort)
  }
}