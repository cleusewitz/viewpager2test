package com.example.viewpager2test.presenter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * Base Class 의 로직 추가는, 불필요한 Coupling 을 피하기 위해 꼭 필요한 경우에 한해서만 최소한으로 유지
 * 추후 코드 추가 시 사전 협의 필요
 */
abstract class BaseDataBindingFragment<B : ViewDataBinding>(
    private val layoutRes: Int
) : Fragment() {
    protected lateinit var binding: B

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutRes, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater =
        super.onGetLayoutInflater(savedInstanceState).run {
            getOverrideThemeResId()?.let { cloneInContext(ContextThemeWrapper(context, it)) } ?: this
        }

    @StyleRes
    protected open fun getOverrideThemeResId(): Int? = null

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
