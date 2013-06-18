/**
 * 
 */
package org.openntf.domino.schema;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.regex.Pattern;

import org.openntf.domino.ext.Formula;
import org.openntf.domino.schema.exceptions.ItemException;

public class ItemValidation implements Externalizable {
	private boolean required_;
	private boolean unique_;
	private Formula uniqueFormula_;
	private Pattern expression_;
	private long maxValue_;
	private long minValue_;
	private int maxMembers_;
	private int minMembers_;

	public Pattern getPattern() {
		return expression_;
	}

	public void setRegex(String expression) {
		expression_ = Pattern.compile(expression);
	}

	public boolean isRequired() {
		return required_;
	}

	public void setRequired(boolean required) {
		required_ = required;
	}

	public boolean isUnique() {
		return unique_;
	}

	public void setUnique(boolean unique) {
		unique_ = unique;
	}

	public Formula getUniqueFormula() {
		return uniqueFormula_;
	}

	public void setUniqueFormula(Formula uniqueFormula) {
		uniqueFormula_ = uniqueFormula;
	}

	public long getMaxValue() {
		return maxValue_;
	}

	public void setMaxValue(long maxValue) {
		maxValue_ = maxValue;
	}

	public long getMinValue() {
		return minValue_;
	}

	public void setMinValue(long minValue) {
		minValue_ = minValue;
	}

	public int getMaxMembers() {
		return maxMembers_;
	}

	public void setMaxMembers(int maxMembers) {
		maxMembers_ = maxMembers;
	}

	public int getMinMembers() {
		return minMembers_;
	}

	public void setMinMembers(int minMembers) {
		minMembers_ = minMembers;
	}

	public boolean validateItem(org.openntf.domino.Item item) throws ItemException {
		boolean result = true;

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		required_ = in.readBoolean();
		unique_ = in.readBoolean();
		maxValue_ = in.readLong();
		minValue_ = in.readLong();
		maxMembers_ = in.readInt();
		minMembers_ = in.readInt();
		uniqueFormula_ = (Formula) in.readObject();
		expression_ = (Pattern) in.readObject();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeBoolean(required_);
		out.writeBoolean(unique_);
		out.writeLong(maxValue_);
		out.writeLong(minValue_);
		out.writeInt(maxMembers_);
		out.writeInt(minMembers_);
		out.writeObject(uniqueFormula_);
		out.writeObject(expression_);
	}

}