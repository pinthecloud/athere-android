package com.pinthecloud.athere.fragment;

import java.io.FileNotFoundException;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pinthecloud.athere.AhGlobalVariable;
import com.pinthecloud.athere.R;
import com.pinthecloud.athere.helper.FileUtil;
import com.pinthecloud.athere.helper.PreferenceHelper;

/**
 * Fragment used for managing interactions for and presentation of a navigation
 * drawer. See the <a href=
 * "https://developer.android.com/design/patterns/navigation-drawer.html#Interaction"
 * > design guidelines</a> for a complete explanation of the behaviors
 * implemented here.
 */
public class SquareListMenuDrawerFragment extends AhFragment {

	/**
	 * A pointer to the current callbacks instance (the Activity).
	 */
	private NavigationDrawerCallbacks mCallbacks;

	/**
	 * Helper component that ties the action bar to the navigation drawer.
	 */
	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private View mFragmentContainerView;
	private ListView mDrawerListView;
	private ImageView profileImage;
	private TextView nickNameText;

	private int mCurrentSelectedPosition = 0;

	private PreferenceHelper pref;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pref = new PreferenceHelper(context);

		// Select either the default item (0) or the last selected item.
		selectItem(mCurrentSelectedPosition);
	}


	/*
	 * Indicate that this fragment would like to influence the set of
	 * actions in the action bar.
	 * (non-Javadoc)
	 * @see android.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_square_list_menu_drawer, container, false);

		/*
		 * Find UI component
		 */
		mDrawerListView = (ListView) view.findViewById(R.id.square_list_menu_drawer_frag_setting_list);
		nickNameText = (TextView) view.findViewById(R.id.square_list_menu_drawer_frag_nick_name_text);
		profileImage = (ImageView) view.findViewById(R.id.square_list_menu_drawer_frag_profile_image);

		/*
		 * Set Profile Image and nick name
		 */
		try {
			nickNameText.setText(pref.getString(AhGlobalVariable.NICK_NAME_KEY));
			profileImage.setImageBitmap(FileUtil.getImageFromInternalStorage(context,
					AhGlobalVariable.PROFILE_IMAGE_FILE_NAME));
		} catch (FileNotFoundException e) {
			// Get profile image to Internal storage error
		}


		/*
		 * set up the drawer's list view with items and click listener
		 */
		mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectItem(position);
			}
		});
		mDrawerListView.setAdapter(new ArrayAdapter<String>(getActionBar().getThemedContext(),
				android.R.layout.simple_list_item_activated_1,
				android.R.id.text1, new String[] {
			getString(R.string.title_section1),
			getString(R.string.title_section2),
			getString(R.string.title_section3), }));
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

		return view;
	}


	/**
	 * Users of this fragment must call this method to set up the navigation drawer interactions.
	 * @param fragmentId
	 *            The android:id of this fragment in its activity's layout.
	 * @param drawerLayout
	 *            The DrawerLayout containing this fragment's UI.
	 */
	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = activity.findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		// Set up Action Bar
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);


		// ActionBarDrawerToggle ties together the the proper interactions
		// between the navigation drawer and the action bar app icon.
		mDrawerToggle = new ActionBarDrawerToggle(
				activity, /* host Activity */
				mDrawerLayout, /* DrawerLayout object */
				R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
				R.string.des_drawer_open, /* "open drawer" description for accessibility */
				R.string.des_drawer_close/* "close drawer" description for accessibility */
				) {

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}

				activity.invalidateOptionsMenu();
				// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}

				activity.invalidateOptionsMenu();
				// onPrepareOptionsMenu()
			}
		};


		// Set a custom shadow that overlays the main content when the drawer opens
		// Defer code dependent on restoration of previous instance state.
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}


	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
		}
	}


	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}


	/*
	 * Forward the new configuration the drawer toggle component.
	 * (non-Javadoc)
	 * @see android.app.Fragment#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (item.getItemId() == R.id.action_example) {
			Toast.makeText(context, "Example action.", Toast.LENGTH_SHORT).show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	private void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}


	public boolean isDrawerOpen() {
		return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}


	private ActionBar getActionBar() {
		return activity.getActionBar();
	}


	/**
	 * Callbacks interface that all activities using this fragment must implement.
	 * Called when an item in the navigation drawer is selected.
	 */
	public static interface NavigationDrawerCallbacks {
		void onNavigationDrawerItemSelected(int position);
	}
}
