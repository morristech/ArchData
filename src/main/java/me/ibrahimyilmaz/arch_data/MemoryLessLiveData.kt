package me.ibrahimyilmaz.arch_data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

internal class MemoryLessLiveData<T> : MutableLiveData<T>() {

    private val singleDataEvent = mutableLiveDataOf<SingleLiveDataEvent<T>>()

    private val observerMap = mutableMapOf<Observer<in T>, SingleLiveEventObserver<T>>()

    private val observers: MutableCollection<Observer<in T>> get() = observerMap.keys

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        val singleEventObserver = singleEventObserverOf(observer)
        observerMap[observer] = singleEventObserver
        singleDataEvent.observe(owner, singleEventObserver)
    }

    override fun observeForever(observer: Observer<in T>) {
        val singleEventObserver = singleEventObserverOf(observer)
        observerMap[observer] = singleEventObserver
        singleDataEvent.observeForever(singleEventObserver)
    }

    override fun removeObserver(observer: Observer<in T>) {
        observerMap.remove(observer)?.let {
            singleDataEvent.removeObserver(it)
        }
    }

    override fun removeObservers(owner: LifecycleOwner) {
        observerMap.clear()
        singleDataEvent.removeObservers(owner)
    }

    override fun postValue(value: T) =
            singleDataEvent.postValue(SingleLiveDataEvent(observers, value))

    override fun setValue(value: T) {
        singleDataEvent.value = SingleLiveDataEvent(observers, value)
    }

}
