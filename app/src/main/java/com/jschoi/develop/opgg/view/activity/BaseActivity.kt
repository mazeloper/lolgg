package com.jschoi.develop.opgg.view.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(getLayoutView())

        initPresenter()

        initViews()

        initEventListener()
    }

    /**
     * set Layout
     */
    abstract fun getLayoutView(): View

    abstract fun initPresenter()

    abstract fun initViews()

    abstract fun initEventListener()

    /**
     * ImageView Glide 적용
     */
    fun <T> setImageViewToGlide(view: ImageView, image: T) {
        Glide.with(this)
            .load(image)
            .centerCrop()
            .into(view);
    }


    /**
     * 키보드 내리기
     */
    fun View.hideSoftKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}