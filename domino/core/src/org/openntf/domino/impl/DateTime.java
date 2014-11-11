/*
 * Copyright 2013
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
 */
package org.openntf.domino.impl;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import lotus.domino.NotesException;

import org.openntf.domino.Session;
import org.openntf.domino.WrapperFactory;
import org.openntf.domino.utils.DominoUtils;
import org.openntf.domino.utils.Factory;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.GregorianCalendar;

// TODO: Auto-generated Javadoc
/**
 * The Class DateTime.
 */
public class DateTime extends Base<org.openntf.domino.DateTime, lotus.domino.DateTime, Session> implements org.openntf.domino.DateTime {
	private static final Logger log_ = Logger.getLogger(DateTime.class.getName());
	private static final long serialVersionUID = 1L;

	static {
		Factory.addTerminateHook(new Runnable() {
			@Override
			public void run() {
				Base.s_recycle(lotusWorker.get());
				lotusWorker.set(null);
				calendar.set(null);
			}
		});
	}

	/** The calendar */
	private static ThreadLocal<Calendar> calendar = new ThreadLocal<Calendar>() {
		@Override
		public Calendar get() {
			if (super.get() == null) {
				set(GregorianCalendar.getInstance());
			}
			return super.get();
		};
	};

	private Date date_;

	/** The dst_. */
	private boolean dst_;

	/** The is date only_. */
	private boolean isDateOnly_;

	/** The is time only_. */
	private boolean isTimeOnly_;

	/** The notes zone_. */
	private int notesZone_;

	private static final ThreadLocal<lotus.domino.DateTime> lotusWorker = new ThreadLocal<lotus.domino.DateTime>();

	private static lotus.domino.DateTime generateWorker() {
		try {
			lotus.domino.Session rawsession = toLotus(Factory.getSession());
			return rawsession.createDateTime(new Date());
		} catch (Exception e) {
			log_.log(Level.SEVERE, "Could not create the DateTime worker object. DateTime functions may not work as expected", e);
			return null;
		}

	};

	private lotus.domino.DateTime getWorker() throws NotesException {
		lotus.domino.DateTime ret = lotusWorker.get();

		if (ret == null || Base.isDead(ret)) {
			ret = generateWorker();
			lotusWorker.set(ret);
		}
		if (date_ != null)
			initWorker(ret);
		return ret;
	}

	private void initWorker(final lotus.domino.DateTime worker) throws NotesException {
		if (date_ == null)
			return;
		worker.setLocalTime(date_);
		if (!isTimeOnly_ && !isDateOnly_)
			worker.convertToZone(notesZone_, dst_);
		if (isTimeOnly_)
			worker.setAnyDate();
		if (isDateOnly_)
			worker.setAnyTime();
	}

	private void workDone(final lotus.domino.DateTime worker, final boolean reInit) {
		if (reInit)
			this.initialize(worker);
	}

	/**
	 * Instantiates a new date time.
	 * 
	 * @param delegate
	 *            the delegate
	 * @param parent
	 *            the parent
	 * @param wf
	 *            the wrapperfactory
	 * @param cppId
	 *            the cpp-id
	 */
	public DateTime(final lotus.domino.DateTime delegate, final Session parent, final WrapperFactory wf, final long cppId) {
		super(delegate, parent, wf, cppId, NOTES_TIME);
		initialize(delegate);
		// TODO: Wrapping recycles the caller's object. This may cause issues.
		Base.s_recycle(delegate);
	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.impl.Base#findParent(lotus.domino.Base)
	 */
	@Override
	protected Session findParent(final lotus.domino.DateTime delegate) {
		if (delegate == null) {
			return Factory.getSession(); // the current Session
		}
		return fromLotus(Base.getSession(delegate), Session.SCHEMA, null);
	}

	/**
	 * Instantiates a new date time.
	 * 
	 * @param date
	 *            the date
	 * @param parent
	 *            the parent
	 * @param wf
	 *            the wrapperfactory
	 * @param cppId
	 *            the cpp-id
	 */
	public DateTime(final Date date, final Session parent, final WrapperFactory wf, final long cppId) {
		super(null, parent, wf, cppId, NOTES_TIME);
		initialize(date);
	}

	/**
	 * Needed for clone
	 * 
	 * @param dateTime
	 */
	protected DateTime(final DateTime orig) {
		super(null, orig.getAncestorSession(), orig.getFactory(), 0, NOTES_TIME);
		dst_ = orig.dst_;
		isDateOnly_ = orig.isDateOnly_;
		isTimeOnly_ = orig.isTimeOnly_;
		notesZone_ = orig.notesZone_;
		if (orig.date_ != null) {
			date_ = new Date(orig.date_.getTime());
		}
	}

	/**
	 * Clones the DateTime object.
	 */
	@Override
	public DateTime clone() {
		return new DateTime(this);
	}

	/*
	 * The returned object MUST get recycled
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.impl.Base#getDelegate()
	 */
	@Override
	protected lotus.domino.DateTime getDelegate() {
		try {
			lotus.domino.Session rawsession = toLotus(Factory.getSession(getParent()));
			lotus.domino.DateTime delegate = rawsession.createDateTime(date_);
			delegate.convertToZone(notesZone_, dst_);
			if (isAnyTime()) {
				delegate.setAnyTime();
			}
			if (isAnyDate()) {
				delegate.setAnyDate();
			}
			return delegate;
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
			return null;
		}
	}

	/**
	 * Initialize.
	 * 
	 * @param date
	 *            the date
	 */
	private void initialize(final java.util.Date date) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.setLocalTime(date);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		//	date_ = new Date(date.getTime());	//NTF copy to keep immutable
		//		dst_ = false;
		//		notesZone_ = 0;
	}

	/**
	 * Initialize.
	 * 
	 * @param delegate
	 *            the delegate
	 */
	private void initialize(final lotus.domino.DateTime delegate) {
		try {
			dst_ = delegate.isDST();
			notesZone_ = delegate.getTimeZone();
			String s = delegate.getDateOnly();
			isTimeOnly_ = (s == null || s.length() == 0);
			s = delegate.getTimeOnly();
			isDateOnly_ = (s == null || s.length() == 0);
			try {
				if (isTimeOnly_ && isDateOnly_) {
					date_ = null;
				} else {
					date_ = delegate.toJavaDate();
				}
			} catch (NotesException e1) {
				// System.out.println("Error attempting to initialize a DateTime: " + delegate.getGMTTime());
				throw new RuntimeException(e1);
			}

		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustDay(int, boolean)
	 */
	@Override
	public void adjustDay(final int n, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.adjustDay(n, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustDay(int)
	 */
	@Override
	public void adjustDay(final int n) {
		adjustDay(n, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustHour(int, boolean)
	 */
	@Override
	public void adjustHour(final int n, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.adjustHour(n, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustHour(int)
	 */
	@Override
	public void adjustHour(final int n) {
		adjustHour(n, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMinute(int, boolean)
	 */
	@Override
	public void adjustMinute(final int n, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.adjustMinute(n, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMinute(int)
	 */
	@Override
	public void adjustMinute(final int n) {
		adjustMinute(n, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMonth(int, boolean)
	 */
	@Override
	public void adjustMonth(final int n, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.adjustMonth(n, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustMonth(int)
	 */
	@Override
	public void adjustMonth(final int n) {
		adjustMonth(n, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustSecond(int, boolean)
	 */
	@Override
	public void adjustSecond(final int n, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.adjustSecond(n, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustSecond(int)
	 */
	@Override
	public void adjustSecond(final int n) {
		adjustSecond(n, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustYear(int, boolean)
	 */
	@Override
	public void adjustYear(final int n, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.adjustYear(n, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#adjustYear(int)
	 */
	@Override
	public void adjustYear(final int n) {
		adjustYear(n, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#convertToZone(int, boolean)
	 */
	@Override
	public void convertToZone(final int zone, final boolean isDST) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.convertToZone(zone, isDST);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		// TODO NTF - find out what this actually does. The documentation is... vague
		//throw new UnimplementedException("convertToZone is not yet implemented.");
	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.ext.DateTime#equals(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean equals(final org.openntf.domino.DateTime compareDate) {
		return date_.equals(compareDate.toJavaDate());
	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.ext.DateTime#equalsIgnoreDate(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean equalsIgnoreDate(final org.openntf.domino.DateTime compareDate) {
		Calendar cal = calendar.get();
		cal.setTime(date_);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, 2000);
		Date d1 = cal.getTime();
		cal.setTime(compareDate.toJavaDate());
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.YEAR, 2000);
		Date d2 = cal.getTime();
		return d1.equals(d2);
	}

	/* (non-Javadoc)
	 * @see org.openntf.domino.ext.DateTime#equalsIgnoreTime(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean equalsIgnoreTime(final org.openntf.domino.DateTime compareDate) {
		Calendar cal = calendar.get();
		cal.setTime(date_);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date d1 = cal.getTime();
		cal.setTime(compareDate.toJavaDate());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date d2 = cal.getTime();
		return d1.equals(d2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getDateOnly()
	 */
	@Override
	public String getDateOnly() {
		String ret = null;
		try {
			lotus.domino.DateTime worker = getWorker();
			ret = worker.getDateOnly();
			workDone(worker, false);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getGMTTime()
	 */
	@Override
	public String getGMTTime() {
		String ret = null;
		try {
			lotus.domino.DateTime worker = getWorker();
			ret = worker.getGMTTime();
			workDone(worker, false);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getLocalTime()
	 */
	@Override
	public String getLocalTime() {
		String ret = null;
		try {
			lotus.domino.DateTime worker = getWorker();
			ret = worker.getLocalTime();
			workDone(worker, false);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.impl.Base#getParent()
	 */
	@Override
	public Session getParent() {
		return getAncestor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getTimeOnly()
	 */
	@Override
	public String getTimeOnly() {
		String ret = null;
		try {
			lotus.domino.DateTime worker = getWorker();
			ret = worker.getTimeOnly();
			workDone(worker, false);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getTimeZone()
	 */
	@Override
	public int getTimeZone() {
		return notesZone_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#getZoneTime()
	 */
	@Override
	public String getZoneTime() {
		// TODO NTF - find out what this really does
		String ret = null;
		try {
			lotus.domino.DateTime worker = getWorker();
			ret = worker.getZoneTime();
			workDone(worker, false);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		return ret;
		// throw new UnimplementedException("getZoneTime is not yet implemented.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#isAfter(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean isAfter(final org.openntf.domino.DateTime compareDate) {
		return date_.after(compareDate.toJavaDate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#isBefore(org.openntf.domino.DateTime)
	 */
	@Override
	public boolean isBefore(final org.openntf.domino.DateTime compareDate) {
		return date_.before(compareDate.toJavaDate());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#isDST()
	 */
	@Override
	public boolean isDST() {
		return dst_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setAnyDate()
	 */
	@Override
	public void setAnyDate() {
		isTimeOnly_ = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setAnyTime()
	 */
	@Override
	public void setAnyTime() {
		isDateOnly_ = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalDate(int, int, int, boolean)
	 */
	@Override
	public void setLocalDate(final int year, final int month, final int day, final boolean preserveLocalTime) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.setLocalDate(year, month, day, preserveLocalTime);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalDate(int, int, int)
	 */
	@Override
	public void setLocalDate(final int year, final int month, final int day) {
		setLocalDate(year, month, day, false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(java.util.Calendar)
	 */
	@Override
	public void setLocalTime(final java.util.Calendar calendar) {
		setLocalTime(calendar.getTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ext.DateTime#setLocalTime(com.ibm.icu.util.Calendar)
	 */
	@Override
	public void setLocalTime(final Calendar calendar) {
		setLocalTime(calendar.getTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(java.util.Date)
	 */
	@Override
	public void setLocalTime(final Date date) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.setLocalTime(date);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(int, int, int, int)
	 */
	@Override
	public void setLocalTime(final int hour, final int minute, final int second, final int hundredth) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.setLocalTime(hour, minute, second, hundredth);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setLocalTime(java.lang.String)
	 */
	@Override
	public void setLocalTime(final String time) {
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.setLocalTime(time);
			workDone(worker, true);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#setNow()
	 */
	@Override
	public void setNow() {
		setLocalTime(new Date());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#timeDifference(lotus.domino.DateTime)
	 */
	@Override
	public int timeDifference(final lotus.domino.DateTime dt) {
		Integer[] res = new Integer[1];
		res[0] = new Integer(0);
		timeDifferenceCommon(dt, res);
		return (res[0]);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#timeDifferenceDouble(lotus.domino.DateTime)
	 */
	@Override
	public double timeDifferenceDouble(final lotus.domino.DateTime dt) {
		Double[] res = new Double[1];
		res[0] = new Double(0);
		timeDifferenceCommon(dt, res);
		return (res[0]);
	}

	private void timeDifferenceCommon(final lotus.domino.DateTime dt, final Object[] res) {
		lotus.domino.DateTime dtLocal = dt;
		lotus.domino.DateTime lotusDTTmp = null;
		try {
			if (dtLocal instanceof org.openntf.domino.impl.DateTime) {
				lotusDTTmp = ((org.openntf.domino.impl.DateTime) dtLocal).getDelegate();
				dtLocal = lotusDTTmp;
			}
			lotus.domino.DateTime worker = getWorker();
			if (res[0] instanceof Integer) {
				res[0] = worker.timeDifference(dtLocal);
			} else if (res[0] instanceof Double) {
				res[0] = worker.timeDifferenceDouble(dtLocal);
			}
			workDone(worker, false);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		} finally {
			if (lotusDTTmp != null)
				s_recycle(lotusDTTmp);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#toJavaDate()
	 */
	@Override
	public Date toJavaDate() {
		return new Date(date_.getTime());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String ret = null;
		try {
			lotus.domino.DateTime worker = getWorker();
			ret = worker.toString();
			workDone(worker, false);
		} catch (NotesException ne) {
			DominoUtils.handleException(ne);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.types.SessionDescendant#getAncestorSession()
	 */
	@Override
	public Session getAncestorSession() {
		return this.getParent();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ext.DateTime#isAnyDate()
	 */
	@Override
	public boolean isAnyDate() {
		return isTimeOnly_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.ext.DateTime#isAnyTime()
	 */
	@Override
	public boolean isAnyTime() {
		return isDateOnly_;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openntf.domino.DateTime#toJavaCal()
	 */
	@Override
	public Calendar toJavaCal() {
		Calendar result = GregorianCalendar.getInstance();
		result.setTime(date_);
		return result;
	}

	@Override
	public int compareTo(final org.openntf.domino.DateTime arg0) {
		return date_.compareTo(arg0.toJavaDate());
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
		dst_ = in.readBoolean();
		isDateOnly_ = in.readBoolean();
		isTimeOnly_ = in.readBoolean();
		notesZone_ = in.readInt();
		date_ = new Date(in.readLong());
	}

	/* (non-Javadoc)
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		out.writeBoolean(dst_);
		out.writeBoolean(isDateOnly_);
		out.writeBoolean(isTimeOnly_);
		out.writeInt(notesZone_);
		out.writeLong(date_.getTime());
	}

	/*
	 * Deprecated, but needed for Externalization
	 */
	@Deprecated
	public DateTime() {
		super(null, Factory.getSession(), null, 0, NOTES_TIME);
	}

	public DateTime(final String time) throws java.text.ParseException {
		this();
		try {
			lotus.domino.DateTime worker = getWorker();
			worker.setLocalTime(time);
			workDone(worker, true);
		} catch (NotesException ne) {
			throw new java.text.ParseException(ne.text, 0);
		}
	}

	/*
	 * A few tiny methods needed for the org.openntf.domino.formula.DateTime interface
	 */
	@Override
	public int timeDifference(final org.openntf.formula.DateTime dt) {
		if (dt instanceof lotus.domino.DateTime)
			return timeDifference((lotus.domino.DateTime) dt);
		return (int) timeDifferenceDouble(dt);
	}

	@Override
	public double timeDifferenceDouble(final org.openntf.formula.DateTime dt) {
		if (dt instanceof lotus.domino.DateTime)
			return timeDifferenceDouble((lotus.domino.DateTime) dt);
		Calendar thisCal = this.toJavaCal();
		Calendar thatCal = dt.toJavaCal();
		return (thisCal.getTimeInMillis() - thatCal.getTimeInMillis()) * 1000;
	}

	@Override
	public int compare(final org.openntf.formula.DateTime sdt1, final org.openntf.formula.DateTime sdt2) {
		if (sdt1 instanceof DateTime && sdt2 instanceof DateTime)
			return ((DateTime) sdt1).compareTo((DateTime) sdt2);
		return sdt1.toJavaDate().compareTo(sdt2.toJavaDate());
	}

	@Override
	public void setLocalTime(final String time, final boolean parseLenient) {
		setLocalTime(time);
	}

}