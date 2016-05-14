package com.anda.moments.entity;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Province implements Serializable
{
	private String name;

	private int type;

	private ArrayList<Sub> sub;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<Sub> getSub() {
		return sub;
	}

	public void setSub(ArrayList<Sub> sub) {
		this.sub = sub;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	public class Sub implements Serializable{
		private String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}


	}
}
