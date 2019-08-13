package net.rong.italker.push.frags.search;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.rong.italker.push.R;
import net.rong.italker.push.activities.SearchActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchGroupFragment extends net.rong.italker.common.app.Fragment
        implements SearchActivity.SearchFragment{


    public SearchGroupFragment() {
        // Required empty public constructor
    }


    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_search_group;
    }

    @Override
    public void search(String content) {

    }
}
