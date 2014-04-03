/*
 * © Copyright FOCONIS AG, 2014
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License.
 * 
 */
package org.openntf.domino.formula;

import java.text.ParsePosition;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.openntf.domino.ISimpleDateTime;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;

public class DominoFormatter implements Formatter {

	private static Map<Locale, Formatter> instances = new HashMap<Locale, Formatter>();
	/*----------------------------------------------------------------------------*/
	private Locale iLocale;

	private DominoFormatter(final Locale loc) {
		iLocale = loc;
	}

	public static synchronized Formatter getInstance(Locale loc) {
		if (loc == null)
			loc = Locale.getDefault();
		Formatter ret = instances.get(loc);
		if (ret == null)
			instances.put(loc, ret = new DominoFormatter(loc));
		return ret;
	}

	public static Formatter getDefaultInstance() {
		return getInstance(null);
	}

	public Locale getLocale() {
		return iLocale;
	}

	/*----------------------------------------------------------------------------*/
	public ISimpleDateTime getNewSDTInstance() {
		return new SimpleDateTime(iLocale);
	}

	public ISimpleDateTime getNewInitializedSDTInstance(final Date date, final boolean noDate, final boolean noTime) {
		ISimpleDateTime sdt = getNewSDTInstance();
		sdt.setLocalTime(date);
		if (noDate)
			sdt.setAnyDate();
		if (noTime)
			sdt.setAnyTime();
		return sdt;
	}

	public ISimpleDateTime getCopyOfSDTInstance(final ISimpleDateTime sdt) {
		return getNewInitializedSDTInstance(sdt.toJavaDate(), sdt.isAnyDate(), sdt.isAnyTime());
	}

	public ISimpleDateTime parseDate(final String image) {
		ISimpleDateTime sdt = getNewSDTInstance();
		sdt.setLocalTime(image);
		return sdt;
	}

	public Calendar parseDateToCal(String image, final boolean[] noDT) {
		image = image.trim();
		Calendar ret = Calendar.getInstance(iLocale);
		// Should an empty string lead to a DateTime with noDate=noTime=true?
		// (Lotus doesn't accept empty strings here.)
		char spec = 0;
		if (image.equalsIgnoreCase("TODAY"))
			spec = 'H';
		else if (image.equalsIgnoreCase("TOMORROW"))
			spec = 'M';
		else if (image.equalsIgnoreCase("YESTERDAY"))
			spec = 'G';
		if (spec != 0) {
			ret.setTime(new Date());
			if (spec == 'M')
				ret.add(Calendar.DAY_OF_MONTH, 1);
			else if (spec == 'G')
				ret.add(Calendar.DAY_OF_MONTH, -1);
			noDT[0] = false;
			noDT[1] = true;
			return ret;
		}
		ret.setLenient(false);
		for (;;) {
			ret.clear();
			/*
			 * First attempt: Take a full date-time format MEDIUM
			 */
			DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, iLocale);
			ParsePosition p = new ParsePosition(0);
			df.parse(image, ret, p);
			if (p.getErrorIndex() < 0)
				break;
			if (!ret.isSet(Calendar.DAY_OF_MONTH) || !ret.isSet(Calendar.MONTH)) {
				//Try with SHORT format			
				ret.clear();
				df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, iLocale);
				p.setIndex(0);
				p.setErrorIndex(-1);
				df.parse(image, ret, p);
				if (!ret.isSet(Calendar.DAY_OF_MONTH) || !ret.isSet(Calendar.MONTH)) {	// Give up with date
					ret.clear();
					p.setErrorIndex(0);
				}
			}
			if (ret.isSet(Calendar.MINUTE))
				break;
			/*
			 * If no time found yet (i.e. at least hour+minute like Lotus), try to fish it
			 */
			p.setIndex(p.getErrorIndex());
			p.setErrorIndex(-1);
			df = DateFormat.getTimeInstance(DateFormat.MEDIUM, iLocale);
			df.parse(image, ret, p);
			if (ret.isSet(Calendar.MINUTE))
				break;
			if (ret.isSet(Calendar.DAY_OF_MONTH)) { // Set back possible hour (in accordance with Lotus)
				ret.clear(Calendar.HOUR);
				ret.clear(Calendar.HOUR_OF_DAY);
				break;
			}
			/*
			 * Left: No date found, no time found; so:
			 */
			throw new IllegalArgumentException("Illegal date string '" + image + "'");
		}
		boolean contDate = ret.isSet(Calendar.DAY_OF_MONTH);
		boolean contTime = ret.isSet(Calendar.MINUTE);
		if (ret.isSet(Calendar.YEAR)) {
			if (!contTime)
				ret.set(Calendar.HOUR_OF_DAY, 0);
		} else {
			Calendar now = Calendar.getInstance(iLocale);
			now.setTime(new Date());
			ret.set(Calendar.YEAR, now.get(Calendar.YEAR));
			if (!contDate) {
				ret.set(Calendar.DAY_OF_MONTH, now.get(Calendar.DAY_OF_MONTH));
				ret.set(Calendar.MONTH, now.get(Calendar.MONTH));
			}
		}
		if (!ret.isSet(Calendar.MINUTE))
			ret.set(Calendar.MINUTE, 0);
		if (!ret.isSet(Calendar.SECOND))
			ret.set(Calendar.SECOND, 0);
		try {
			ret.getTime();
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Illegal date string '" + image + "': " + e.getMessage());
		}
		noDT[0] = !contDate;
		noDT[1] = !contTime;
		return ret;
	}

	/*----------------------------------------------------------------------------*/
	public Number parseNumber(final String el) {
		//NumberFormat nf = NumberFormat.getInstance();
		// TODO Auto-generated method stub
		return Double.valueOf(el.replace(',', '.'));
	}

	/*----------------------------------------------------------------------------*/
	public String formatDateTime(final ISimpleDateTime sdt) {
		return sdt.getLocalTime();
	}

	public String formatDateTime(final ISimpleDateTime sdt, final String lotusOpts) {
		// TODO Auto-generated method stub
		return null;
	}

	public String formatCalDateTime(final Calendar cal) {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, iLocale);
		df.setCalendar(cal);
		return df.format(cal.getTime());
	}

	public String formatCalDateOnly(final Calendar cal) {
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, iLocale);
		df.setCalendar(cal);
		return df.format(cal.getTime());
	}

	public String formatCalTimeOnly(final Calendar cal) {
		DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM, iLocale);
		df.setCalendar(cal);
		return df.format(cal.getTime());
	}

	/*----------------------------------------------------------------------------*/
	public String formatNumber(final Number n) {
		// TODO Auto-generated method stub
		return null;
	}

	public String formatNumber(final Number n, final String lotusOpts) {
		// TODO Auto-generated method stub
		return null;
	}
}
