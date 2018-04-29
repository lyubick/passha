package org.kgbt.passha.android

import android.text.Editable
import android.text.TextWatcher

class FilterChangeWatcher(filteredListAdapter: FilteredListAdapter) : TextWatcher {

    private val aFilteredListAdapter = filteredListAdapter

    override fun afterTextChanged(s: Editable?) {
        aFilteredListAdapter.filter.filter(s!!.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Do nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Do nothing
    }


}