package org.openntf.domino.session;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

import lotus.domino.local.Session;

import org.openntf.domino.utils.DominoUtils;

public enum LotusSessionFactory {
	;
	private static Method getSessionMethod(final String name, final Class<?>... parameterTypes) {
		return AccessController.doPrivileged(new PrivilegedAction<Method>() {
			@Override
			public Method run() {
				try {
					Method m = lotus.domino.local.Session.class.getDeclaredMethod(name, parameterTypes);
					m.setAccessible(true);
					return m;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});

	}

	private static Field F_webuser;
	static {
		try {
			F_webuser = lotus.domino.local.Session.class.getDeclaredField("webuser");
			F_webuser.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//----------------------- FindOrCreateSession 
	private static Method M_FindOrCreateSession = getSessionMethod("FindOrCreateSession", long.class, int.class);

	private static lotus.domino.local.Session FindOrCreateSession(final long cpp_id, final int unknown) throws IllegalArgumentException,
	IllegalAccessException, InvocationTargetException {
		return (lotus.domino.local.Session) M_FindOrCreateSession.invoke(null, cpp_id, unknown);
	}

	// ----------------- createNativeSession --------------
	private static Method M_NCreateSession = getSessionMethod("NCreateSession", int.class);

	private static long NCreateSession(final int unknown) throws IllegalArgumentException, IllegalAccessException,
	InvocationTargetException {
		return (Long) M_NCreateSession.invoke(null, unknown);
	}

	static Session createSession() {
		try {

			long cpp = NCreateSession(0);  // don't know what parameter means
			return FindOrCreateSession(cpp, 0);
		} catch (Exception e) {
			DominoUtils.handleException(e);
			return null;
		}
	}

	// ----------------- createTrustedSession --------------
	private static Method M_NCreateTrustedSession = getSessionMethod("NCreateTrustedSession", boolean.class);

	private static long NCreateTrustedSession(final boolean unknown) throws IllegalArgumentException, IllegalAccessException,
	InvocationTargetException {
		return (Long) M_NCreateTrustedSession.invoke(null, unknown);
	}

	static Session createTrustedSession() {
		try {
			long cpp = NCreateTrustedSession(false); // don't know what parameter means
			return FindOrCreateSession(cpp, 0);
		} catch (Exception e) {
			DominoUtils.handleException(e);
			return null;
		}
	}

	// ----------------- createSessionWithFullAccess --------------
	private static Method M_NCreateSessionWithFullAccess = getSessionMethod("NCreateSessionWithFullAccess", String.class);

	private static long NCreateSessionWithFullAccess(final String userName) throws IllegalArgumentException, IllegalAccessException,
	InvocationTargetException {
		return (Long) M_NCreateSessionWithFullAccess.invoke(null, userName);
	}

	static Session createSessionWithFullAccess(final String userName) {
		try {
			long l = NCreateSessionWithFullAccess(userName);
			return FindOrCreateSession(l, 0);
		} catch (Exception e) {
			DominoUtils.handleException(e);
			return null;
		}
	}

	// ----------------- createSessionWithTokenEx --------------
	private static Method M_NCreateSessionWithTokenEx = getSessionMethod("NCreateSessionWithTokenEx", String.class);

	private static long NCreateSessionWithTokenEx(final String userName) throws IllegalArgumentException, IllegalAccessException,
	InvocationTargetException {
		return (Long) M_NCreateSessionWithTokenEx.invoke(null, userName);
	}

	static Session createSessionWithTokenEx(final String paramString) {
		try {
			long l = NCreateSessionWithTokenEx(paramString);
			Session ret = FindOrCreateSession(l, 0);
			F_webuser.set(ret, true);
			return ret;
		} catch (Exception e) {
			DominoUtils.handleException(e);
			return null;
		}
	}
}
