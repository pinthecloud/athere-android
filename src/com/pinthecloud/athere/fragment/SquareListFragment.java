package com.pinthecloud.athere.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.athere.R;
import com.pinthecloud.athere.activity.SquareListActivity;

public class SquareListFragment extends AhFragment{

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static SquareListFragment newInstance(int sectionNumber) {
    	SquareListFragment fragment = new SquareListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_square_list, container, false);
        // TODO Set view
        return view;
    }

    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((SquareListActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
