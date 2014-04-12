package org.kjg.garderobe;

import java.util.ArrayList;

import org.kjg.garderobe.NavigationDrawer.DrawerEntryAdapter;
import org.kjg.garderobe.NavigationDrawer.EntryItem;
import org.kjg.garderobe.NavigationDrawer.HeaderItem;
import org.kjg.garderobe.NavigationDrawer.ListItem;
import org.kjg.garderobe.NavigationDrawer.SpinnerItem;

import Model.Party;
import Model.Serializer;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final boolean D = true;
	private final String TAG = "Main";

	private ListView drawerListView;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;

	private Party currentParty;
	private DrawerEntryAdapter drawerAdapter;

	private final String KEY_FRAGMENT = "fragment";

	// private final String KEY_PARTY = "selected_party";

	public Party getCurrentParty() {
		return currentParty;
	}

	public void setCurrentParty(Party currentParty) {
		if (D)
			Log.i(TAG, "Set party: " + currentParty.getName());

		Toast.makeText(
				this,
				String.format(
						getResources().getString(R.string.toast_party_selected),
						currentParty.getName()), Toast.LENGTH_SHORT).show();
		this.currentParty = currentParty;
		this.getActionBar().setTitle(this.currentParty.getName());
		drawerAdapter.setSpinnerPartyActive(currentParty.getName());
	}

	public void saveCurrentParty() {
		if (D)
			Log.i(TAG, "Serialize current party");

		Serializer.serializeParty(getCurrentParty(), this);
	}

	public void closeDrawer() {
		drawerLayout.closeDrawer(drawerListView);
	}

	public DrawerEntryAdapter getDrawerAdapter() {
		return drawerAdapter;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(null);
		setContentView(R.layout.activity_main);

		if (D)
			Log.i(TAG, "***Begin-OnCreate***");

		// Navigation drawer
		this.drawerListView = (ListView) findViewById(R.id.drawer_listview);
		drawerAdapter = new DrawerEntryAdapter(this, getDrawerItemsArray());
		this.drawerListView.setAdapter(drawerAdapter);

		// Action bar toggle
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerToggle = new ActionBarDrawerToggle(this, this.drawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close);
		this.drawerLayout.setDrawerListener(drawerToggle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.drawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// react to click events in navigation drawer
		this.drawerListView.setOnItemClickListener(new OnItemClickListener() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onItemClick(AdapterView parent, View view,
					int position, long id) {
				// drawer item clicked

				Fragment f = null;

				switch (position) {
				case 2: // schedule
					if (getCurrentParty() != null) {
						f = new ScheduleFragment();
					}
					break;
				case 3: // numbers
					if (getCurrentParty() != null) {
						f = new NumbersFragment();
					}
					break;
				case 4: // bags
					if (getCurrentParty() != null) {
						f = new BagsFragment();
					}
					break;
				case 6: // new party
					f = new NewPartyFragment();
					break;

				case 7:// ChecklistActivity
					f = null;
					startChecklistActivity();
					break;
				case 8: // settings
					break;
				}

				if (f != null) {
					if (D)
						Log.i(TAG, "Fragment change :"
								+ f.getClass().toString());

					FragmentManager fm = getFragmentManager();
					fm.beginTransaction()
							.setCustomAnimations(R.animator.slide_in_left,
									R.animator.slide_out_right, 0, 0)
							.replace(R.id.frame_container, f, KEY_FRAGMENT)
							.addToBackStack(null).commit();
				}

				// close drawer
				drawerLayout.closeDrawer(drawerListView);
			}

		});

		// if (savedInstanceState != null) {
		// Log.i("Load", KEY_FRAGMENT);
		//
		// FragmentManager fm = this.getFragmentManager();
		// Fragment f = fm.findFragmentByTag(KEY_FRAGMENT);
		//
		// Log.i(TAG, "F == null: " + String.valueOf(f == null));
		//
		// fm.beginTransaction()
		// .replace(R.id.frame_container, f, KEY_FRAGMENT).commit();
		// }

		if (D)
			Log.i(TAG, "***End-OnCreate***");

	}

	@Override
	public void onPause() {
		super.onPause();
		if (D)
			Log.i(TAG, "***OnPause***");
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);
		if (D)
			Log.i(TAG, "***OnSaveInstanceState***");
		// Save the current state

		// Log.i("Save", currentFragmentTag);
		// savedInstanceState.putSerializable(this.KEY_PARTY,
		// currentFragmentTag);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		if (D)
			Log.i(TAG, "***OnPostCreate***");
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (D)
			Log.i(TAG, "***onConfigurationChanged***");

		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void startChecklistActivity() {
		Intent i = new Intent(this, ChecklistActivity.class);
		this.startActivity(i);
	}

	private ArrayList<ListItem> getDrawerItemsArray() {
		if (D)
			Log.i(TAG, "Populate drawer");

		ArrayList<ListItem> a = new ArrayList<ListItem>();
		a.add(new SpinnerItem());

		a.add(new HeaderItem(getString(R.string.drawer_header_view)));
		a.add(new EntryItem(getString(R.string.drawer_item_schedule)));
		a.add(new EntryItem(getString(R.string.drawer_item_numbers)));
		a.add(new EntryItem(getString(R.string.drawer_item_bags)));

		a.add(new HeaderItem(getString(R.string.drawer_header_management)));
		a.add(new EntryItem(getString(R.string.drawer_item_newparty)));
		a.add(new EntryItem(getString(R.string.drawer_item_checklist)));
		a.add(new EntryItem(getString(R.string.drawer_item_settings)));

		return a;
	}
}