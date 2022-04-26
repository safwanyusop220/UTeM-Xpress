package my.edu.utem.ftmk.utemxpress.model;

import android.widget.Filter;

import java.util.ArrayList;

import my.edu.utem.ftmk.utemxpress.adapter.AdapterProductSeller;
import my.edu.utem.ftmk.utemxpress.adapter.AdapterProductUser;

public class FilterProductUser extends Filter{

    private AdapterProductUser adapter;
    private ArrayList<ModelProduct> filterList;

    public FilterProductUser(AdapterProductUser adapter, ArrayList<ModelProduct> filterList){
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        //validate
        if(constraint != null && constraint.length()>0){
            //search filled not empty, searching something, perform search
            //change to uppercase
            constraint = constraint.toString().toUpperCase();
            //STORE our filtered list
            ArrayList<ModelProduct>filteredModels = new ArrayList<>();
            for (int i=0; i<filterList.size();i++){
                //check, search by title and category
                if(filterList.get(i).getProductTitle().toUpperCase().contains(constraint)||
                filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){

                    //add filtered data to list
                    filteredModels.add(filterList.get(i));
                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            //search filled not empty,not searching,return something, return original/all/compelete
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
            adapter.productsList = (ArrayList<ModelProduct>) results.values;
            //refresh adapter
        adapter.notifyDataSetChanged();
    }
}
