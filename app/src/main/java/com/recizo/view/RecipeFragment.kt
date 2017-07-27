package com.recizo.view

import android.app.Fragment
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.recizo.R
import com.recizo.presenter.RecipePresenter
import kotlinx.android.synthetic.main.searched_recipe_list.*

class RecipeFragment : Fragment() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
    super.onCreateView(inflater, container, savedInstanceState)
    return inflater!!.inflate(R.layout.searched_recipe_list, container, false)
  }

  override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    searched_recyclerView.layoutManager = LinearLayoutManager(activity)
    searched_recyclerView.addItemDecoration(DividerItemDecoration(
            searched_recyclerView.context,
            LinearLayoutManager(activity).orientation)
    )

    val recipePresenter = RecipePresenter(searched_recyclerView, listOf("鹿","トマト"))
    recipePresenter.setProgressBar(object:RecipePresenter.IProgressBar{
      override fun showProgressBar() {
        searched_recipe_progressBar.visibility = View.VISIBLE
      }
      override fun hideProgressBar() {
        searched_recipe_progressBar.visibility = View.GONE
      }
    })
    recipePresenter.startRecipeListCreate()

    searched_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        recipePresenter.addRecipeList(recyclerView, dy)
      }
    })
  }

  override fun onStart() {
    super.onStart()
  }

}