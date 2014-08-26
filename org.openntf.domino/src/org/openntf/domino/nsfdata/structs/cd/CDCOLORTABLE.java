package org.openntf.domino.nsfdata.structs.cd;

import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import org.openntf.domino.nsfdata.structs.SIG;

/**
 * A color table is one of the optional records following a CDBITMAPHEADER record. The color table specifies the mapping between 8-bit
 * bitmap samples and 24-bit Red/Green/Blue colors. (editods.h)
 *
 */
public class CDCOLORTABLE extends CDRecord {

	public CDCOLORTABLE(final SIG signature, final ByteBuffer data) {
		super(signature, data);
	}

	public Color[] getColors() {
		int count = getDataLength() / 3;
		Color[] result = new Color[count];
		ByteBuffer data = getData().duplicate();
		data.order(ByteOrder.LITTLE_ENDIAN);
		for (int i = 0; i < count; i++) {
			int r = data.get() & 0xFF;
			int g = data.get() & 0xFF;
			int b = data.get() & 0xFF;
			result[i] = new Color(r, g, b);
		}
		return result;
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + ", Colors: " + Arrays.asList(getColors()) + "]";
	}
}