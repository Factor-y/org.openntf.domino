package org.openntf.domino.xsp.helpers;

import java.security.PrivilegedActionException;

import javax.servlet.ServletException;

import lotus.domino.NotesException;

import org.openntf.domino.AutoMime;
import org.openntf.domino.Session;
import org.openntf.domino.ext.Session.Fixes;
import org.openntf.domino.session.AbstractSessionFactory;
import org.openntf.domino.utils.DominoUtils;
import org.openntf.domino.xsp.xots.FakeHttpRequest;

import com.ibm.designer.runtime.domino.bootstrap.util.StringUtil;
import com.ibm.domino.napi.c.NotesUtil;
import com.ibm.domino.napi.c.xsp.XSPNative;
import com.ibm.domino.xsp.module.nsf.NotesContext;

public class XPageCurrentSessionFactory extends AbstractSessionFactory {
	private String runAs_;

	public XPageCurrentSessionFactory(final Fixes[] fixes, final AutoMime autoMime) {
		super(fixes, autoMime, null);
		init();
	}

	public XPageCurrentSessionFactory() {
		super();
		init();
	}

	private void init() {
		final lotus.domino.Session rawSession = NotesContext.getCurrent().getCurrentSession();
		try {
			runAs_ = rawSession.getEffectiveUserName();
			lotus.domino.Database rawDb = rawSession.getCurrentDatabase();
			if (rawDb != null) {
				if (StringUtil.isEmpty(rawDb.getServer())) {
					currentApiPath_ = rawDb.getFilePath();
				} else {
					currentApiPath_ = rawDb.getServer() + "!!" + rawDb.getFilePath();
				}
			}
		} catch (NotesException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Session createSession() throws PrivilegedActionException {
		if (runAs_ == null) {
			throw new NullPointerException("No username set");
		}
		NotesContext ctx = NotesContext.getCurrentUnchecked();
		if (ctx != null) {
			try {
				ctx.initRequest(new FakeHttpRequest(runAs_));
			} catch (ServletException e) {
				DominoUtils.handleException(e);
				return null;
			}
			return wrapSession(ctx.getCurrentSession(), false);
		}
		try {
			long userHandle = NotesUtil.createUserNameList(runAs_);
			return wrapSession(XSPNative.createXPageSession(runAs_, userHandle, false, true), true);

		} catch (Exception e) {
			DominoUtils.handleException(e);
			return null;
		}
	}

}
