/* Generated By:JJTree: Do not edit this line. Node.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
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
 */

package org.openntf.domino.formula.ast;

import org.openntf.domino.formula.EvaluateException;
import org.openntf.domino.formula.FormulaContext;
import org.openntf.domino.formula.ParseException;
import org.openntf.domino.formula.ValueHolder;

/* All AST nodes must implement this interface.  It provides basic
   machinery for constructing the parent and child relationships
   between nodes. */

public interface Node {

	/**
	 * This method is called after the node has been made the current node. It indicates that child nodes can now be added to it.
	 */
	public void jjtOpen();

	/**
	 * This method is called after all the child nodes have been added.
	 * 
	 * @throws ParseException
	 *             throws parseException e.g. on parameter mismatch <br>
	 *             <b>NOTE:</b> After a javacc recompile you must add the <code>throws</code> declaration manually in
	 *             "JJTAtFormulaParserState"
	 */
	public void jjtClose() throws ParseException;

	/**
	 * This pair of methods are used to inform the node of its parent.
	 */
	public void jjtSetParent(Node n);

	public Node jjtGetParent();

	/**
	 * This method tells the node to add its argument to the node's list of children.
	 */
	public void jjtAddChild(Node n, int i);

	/**
	 * This method returns a child node. The children are numbered from zero, left to right.
	 */
	public Node jjtGetChild(int i);

	/** Return the number of children the node has. */
	public int jjtGetNumChildren();

	public void dump(final String prefix);

	public ValueHolder evaluate(FormulaContext ctx) throws EvaluateException;

	public void toFormula(StringBuilder sb);
}
/* JavaCC - OriginalChecksum=54dec3b6b2c592c5fbe2fc5be72328d2 (do not edit this line) */