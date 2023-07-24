package com.example.viewpager2test.presenter.adapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyViewModel : ViewModel() {

    private val _viewPagerCount = MutableStateFlow(0)
    val viewPagerCount = _viewPagerCount.asStateFlow()

    fun setViewPagerCount(count: Int) {
        viewModelScope.launch {
            _viewPagerCount.emit(count)
        }
    }

    fun setViewPagerCountPlus() {
        viewModelScope.launch {
            _viewPagerCount.emit(_viewPagerCount.value++)
        }
    }
}
