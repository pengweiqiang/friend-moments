package com.anda.moments.views.widget.slide;

import com.anda.moments.entity.User;

import java.util.Comparator;


public class PinyinComparator implements Comparator<User> {

	@Override
	public int compare(User o1, User o2) {
		if (o1.getSortLetters().equals("☆")) {
			return -1;
		} else if (o2.getSortLetters().equals("☆")) {
			return 1;
		} else if (o1.getSortLetters().equals("#")) {
			return -1;
		} else if (o2.getSortLetters().equals("#")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
