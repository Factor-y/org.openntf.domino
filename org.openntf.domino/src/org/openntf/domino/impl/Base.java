package org.openntf.domino.impl;

import java.util.Vector;

import org.openntf.domino.thread.DominoReference;
import org.openntf.domino.thread.DominoReferenceQueue;
import org.openntf.domino.thread.DominoReferenceSet;

public abstract class Base<T extends org.openntf.domino.Base<D>, D extends lotus.domino.Base> implements org.openntf.domino.Base<D> {
	private static ThreadLocal<DominoReferenceQueue> recycleQueue = new ThreadLocal<DominoReferenceQueue>() {
		@Override
		protected DominoReferenceQueue initialValue() {
			return new DominoReferenceQueue();
		};
	};
	private static final DominoReferenceSet refSet = new DominoReferenceSet();

	protected boolean recycled_;
	protected D delegate_; // NTF final???
	private DominoReference ref_; // this is the PhantomReference that will be enqueued when this Base object HAS BEEN be GC'ed

	protected Base(D delegate) {
		if (delegate != null) {
			delegate_ = delegate;
			ref_ = new DominoReference(this, recycleQueue.get(), delegate);
			refSet.add(delegate);
		}
	}

	protected D getDelegate() {
		return delegate_;
	}

	public static boolean isLocked(lotus.domino.Base base) {
		return refSet.isLocked(base);
	}

	public static void lock(lotus.domino.Base base) {
		refSet.lock(base);
	}

	public static void lock(lotus.domino.Base... allYourBase) {
		for (lotus.domino.Base everyZig : allYourBase) {
			refSet.lock(everyZig);
		}
	}

	public static void recycleAll() {
		refSet.clear();
	}

	public void recycle() {
		recycle(this);
	}

	public static boolean recycle(lotus.domino.local.NotesBase base) {
		boolean result = false;
		if (!isLocked(base)) {
			try {
				base.recycle();
				result = true;
			} catch (Throwable t) {
				// shikata ga nai
			}
		}
		return true;
	}

	public static void recycle(org.openntf.domino.impl.Base<?, ?> base) {
		if (recycle((lotus.domino.local.NotesBase) base.getDelegate())) {
			base.recycled_ = true;
		}
	}

	public boolean isRecycled() {
		return recycled_;
	}

	@SuppressWarnings("rawtypes")
	public void recycle(Vector arg0) {
		for (Object o : arg0) {
			if (o instanceof org.openntf.domino.impl.Base) {
				recycle((org.openntf.domino.impl.Base) o);
			} else if (o instanceof lotus.domino.local.NotesBase) {
				recycle((lotus.domino.local.NotesBase) o);
			}
		}
	}

}
