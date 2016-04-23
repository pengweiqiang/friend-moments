package com.anda.moments.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.anda.moments.R;
import com.anda.moments.apdater.CityAdapter;
import com.anda.moments.commons.AppManager;
import com.anda.moments.data.CityData;
import com.anda.moments.entity.CityItem;
import com.anda.moments.ui.base.BaseActivity;
import com.anda.moments.views.ActionBar;
import com.anda.moments.views.widget.city.ContactItemInterface;
import com.anda.moments.views.widget.city.ContactListViewImpl;
import com.anda.moments.views.widget.pinyin.PinYin;

public class CityListActivity extends BaseActivity implements TextWatcher
{
	private Context context_ = CityListActivity.this;
	private ActionBar mActionBar;
	private ContactListViewImpl listview;

	private EditText searchBox;
	private String searchString;

	private Object searchLock = new Object();
	boolean inSearchMode = false;

	private final static String TAG = "MainActivity2";

	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	private SearchListTask curSearchTask = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.citylist);

		filterList = new ArrayList<ContactItemInterface>();
		contactList = CityData.getSampleContactList();
		
		mActionBar = (ActionBar)findViewById(R.id.actionBar);
		mActionBar.setTitle("选择城市");
		mActionBar.setLeftActionButtonListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AppManager.getAppManager().finishActivity();
			}
		});

		CityAdapter adapter = new CityAdapter(this,R.layout.city_item, contactList);

		listview = (ContactListViewImpl) this.findViewById(R.id.listview);
		listview.setFastScrollEnabled(true);
		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView parent, View v, int position,
					long id)
			{
				List<ContactItemInterface> searchList = inSearchMode ? filterList
						: contactList;
				String city = searchList.get(position).getDisplayInfo();
				Intent data = new Intent();
				data.putExtra("city", city);
				setResult(0, data);
				AppManager.getAppManager().finishActivity();
			}
		});

		searchBox = (EditText) findViewById(R.id.input_search_query);
		searchBox.addTextChangedListener(this);
	}

	@Override
	public void afterTextChanged(Editable s)
	{
		searchString = searchBox.getText().toString().trim().toUpperCase();

		if (curSearchTask != null
				&& curSearchTask.getStatus() != AsyncTask.Status.FINISHED)
		{
			try
			{
				curSearchTask.cancel(true);
			} catch (Exception e)
			{
				Log.i(TAG, "Fail to cancel running search task");
			}

		}
		curSearchTask = new SearchListTask();
		curSearchTask.execute(searchString); 
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		// do nothing
	}

	private class SearchListTask extends AsyncTask<String, Void, String>
	{

		@Override
		protected String doInBackground(String... params)
		{
			filterList.clear();

			String keyword = params[0];

			inSearchMode = (keyword.length() > 0);

			if (inSearchMode)
			{
				// get all the items matching this
				for (ContactItemInterface item : contactList)
				{
					CityItem contact = (CityItem) item;
					
					if(contact.getFullName().equals("#")){
						
						String hotP = PinYin.getPinYin(contact.getNickName());
						boolean isPinyin = hotP.toUpperCase().indexOf(keyword) > -1;
						if (isPinyin)
						{
							filterList.add(item);
						}
						
					}else{
						boolean isPinyin = contact.getFullName().toUpperCase().indexOf(keyword) > -1;
						boolean isChinese = contact.getNickName().indexOf(keyword) > -1;
						
						if (isPinyin || isChinese)
						{
							filterList.add(item);
						}
					}
					

				}

			}
			return null;
		}

		protected void onPostExecute(String result)
		{

			synchronized (searchLock)
			{

				if (inSearchMode)
				{

					CityAdapter adapter = new CityAdapter(context_,R.layout.city_item, filterList);
					adapter.setInSearchMode(true);
					listview.setInSearchMode(true);
					listview.setAdapter(adapter);
				} else
				{
					CityAdapter adapter = new CityAdapter(context_,R.layout.city_item, contactList);
					adapter.setInSearchMode(false);
					listview.setInSearchMode(false);
					listview.setAdapter(adapter);
				}
			}

		}
	}
	@Override
	public void initView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		
	}


}
