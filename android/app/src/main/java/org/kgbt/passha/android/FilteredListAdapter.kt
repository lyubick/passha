package org.kgbt.passha.android

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.kgbt.passha.core.db.SpecialPassword
import org.kgbt.passha.android.R

class FilteredListAdapter(context: Context?, resource: Int, objects: List<SpecialPassword>?) : BaseAdapter(), Filterable {

    val TAG = "FilteredListAdapter"

    val aSpecialPasswords = objects!!
    var aFilteredPasswords = objects!!
    val aInflater = LayoutInflater.from(context);
    val aResource = resource

    // Example is here: https://www.simplifiedcoding.net/custom-listview-android/
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //getting the view
        val view = aInflater.inflate(aResource, null, false)

        //getting the view elements of the list from the view
        val textViewName = view.findViewById<TextView>(R.id.textView)

        //adding values to the list item
        textViewName.setText(aFilteredPasswords[position].getName())

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()        // Holds the results of a filtering operation in values
                val filteredList = ArrayList<SpecialPassword>()

                if (constraint == null || constraint.isEmpty() || constraint.isBlank()) {

                    // set the Original result to return
                    results.count = aSpecialPasswords.size
                    results.values = aSpecialPasswords
                } else {
                    val lowerConstraint: String = constraint.toString().toLowerCase()
                    for (i in 0 until aSpecialPasswords.size) {
                        val data = aSpecialPasswords.get(i)
                        if (data.name.toLowerCase().contains(lowerConstraint)) {
                            filteredList.add(data)
                        }
                    }
                    // set the Filtered result to return
                    results.count = filteredList.size
                    results.values = filteredList
                }

                val lst = (results.values as ArrayList<SpecialPassword>).map { sp -> sp.name }

                Log.d(TAG, lst.toString())

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                aFilteredPasswords = results!!.values as List<SpecialPassword> // has the filtered values
                notifyDataSetChanged()  // notifies the data with new filtered values
            }
        }
    }

    override fun getItem(position: Int): Any {
        return aFilteredPasswords[position]
    }

    override fun getItemId(position: Int): Long {
        return aFilteredPasswords[position].name.hashCode().toLong()
    }

    override fun getCount(): Int {
        return aFilteredPasswords.size
    }

}