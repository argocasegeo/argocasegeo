// Copyright (c) 1996-2002 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

// File: ParserDisplay.java
// Classes: ParserDisplay
// Original Author:
// $Id: ParserDisplay.java,v 1.39 2002/09/30 16:53:32 d00mst Exp $

// 12 Apr 2002: Jeremy Bennett (mail@jeremybennett.com). Extended to support
// extension points.


package org.argouml.uml.generator;

import java.beans.*;
import java.util.*;
import java.text.ParseException;

import javax.swing.plaf.multi.MultiButtonUI;

import ru.novosoft.uml.foundation.core.*;
import ru.novosoft.uml.foundation.data_types.MMultiplicity;
import ru.novosoft.uml.foundation.data_types.MExpression;
import ru.novosoft.uml.foundation.data_types.MActionExpression;
import ru.novosoft.uml.foundation.data_types.MBooleanExpression;
import ru.novosoft.uml.foundation.data_types.*;
import ru.novosoft.uml.foundation.extension_mechanisms.*;
import ru.novosoft.uml.behavior.use_cases.*;
import ru.novosoft.uml.behavior.common_behavior.*;
import ru.novosoft.uml.behavior.state_machines.*;
import ru.novosoft.uml.behavior.collaborations.*;
import ru.novosoft.uml.model_management.*;

import org.tigris.gef.base.*;
import org.tigris.gef.graph.*;

import org.argouml.kernel.Project;
import org.argouml.model.uml.UmlFactory;
import org.argouml.model.uml.UmlHelper;
import org.argouml.ui.ProjectBrowser;
import org.argouml.uml.MMUtil;
import org.argouml.uml.ProfileJava;
import org.argouml.uml.diagram.static_structure.*;
import org.argouml.uml.diagram.deployment.*;
import org.apache.log4j.Category;
import org.argouml.application.api.*;
import org.argouml.util.MyTokenizer;
import org.argouml.model.uml.foundation.core.*;
import org.argouml.model.uml.foundation.extensionmechanisms.*;
import org.argouml.model.uml.modelmanagement.ModelManagementHelper;

/**
 * Interface specifying the operation to take when a PropertySpecialString
 * is matched.
 *
 * @author Michael Stockman
 * @since 0.11.2
 * @see PropertySpecialString
 */
interface PropertyOperation {
  /**
   * Invoked by PropertySpecialString when it has matched a property name.
   *
   * @param element The element on which the property was set.
   * @param value The value of the property, may be null if no value was
   *		  given.
   */
  public void found(MModelElement element, String value);
}

/**
 * Declares a string that should take special action when it is found as a
 * property in
 * {@link ParserDisplay#setProperties ParserDisplay.setProperties}.
 *
 * <p><b>Example:</b><pre>
 *  _attributeSpecialStrings[0] = new PropertySpecialString("frozen",
 *	new PropertyOperation() {
 *	    public void found(MModelElement element, String value) {
 *		MChangeableKind kind = MChangeableKind.FROZEN;
 *		if (value != null && value.equalsIgnoreCase("false"))
 *		    kind = MChangeableKind.CHANGEABLE;
 *		if (element instanceof MStructuralFeature)
 *		    ((MStructuralFeature)element).setChangeability(kind);
 *	    }
 *	});</pre>
 *
 * <p>Taken from the ParserDisplay constructor. It creates a
 * PropertySpecialString that is invoken when the String "frozen" is found
 * as a property name. Then the found mehod in the anonymous inner class
 * defined on the 2nd line is invoked and performs a custom action on the
 * element on which the property was specified by the user. In this case
 * it does a setChangeability on an attribute instead of setting a tagged
 * value, which would not have the desired effect.
 *
 * @author Michael Stockman
 * @since 0.11.2
 * @see PropertyOperation
 * @see ParserDisplay#setProperties
 */
class PropertySpecialString {
  private String _name;
  private PropertyOperation _op;

  /**
   * Constructs a new PropertySpecialString that will invoke the action in
   * op when {@link #invoke() invoke} is called with name equal to str and
   * then return true from invoke.
   *
   * @param str The name of this PropertySpecialString.
   * @param op An object containing the method to invoke on a match.
   */
  public PropertySpecialString(String str, PropertyOperation op) {
    _name = str;
    _op = op;
  }

  /**
   * Called by {@link ParserDisplay#setProperties() ParserDisplay.setProperties}
   * while searching for an action to invoke for a property. If it returns
   * true, then setProperties may assume that all required actions have been
   * taken and stop searching.
   *
   * @param name The name of a property.
   * @param value The value of a property.
   * @return <b>true</b> if an action is performed, otherwise <b>false</b>.
   */
  public boolean invoke(MModelElement element, String name, String value) {
    if (!_name.equalsIgnoreCase(name))
	return false;
    _op.found(element, value);
    return true;
  }
}

public class ParserDisplay extends Parser {

  public static ParserDisplay SINGLETON = new ParserDisplay();
  /**
   * The standard error etc. logger
   */
  protected static final Category _cat = 
    Category.getInstance(ParserDisplay.class);

  /** The array of special properties for attributes */
  private PropertySpecialString _attributeSpecialStrings[];

  /** The vector of CustomSeparators to use when tokenizing attributes */
  private Vector _attributeCustomSep;

  /** The acceptible base classes for attribute stereotypes */
  private String _attributeBases[] = {"Attribute", "StructuralFeature",
	"Feature", "ModelElement", null};

  /** The array of special properties for operations */
  private PropertySpecialString _operationSpecialStrings[];

  /** The vector of CustomSeparators to use when tokenizing attributes */
  private Vector _operationCustomSep;

  /** The acceptible base classes for operation stereotypes */
  private String _operationBases[] = {"Operation", "BehavioralFeature",
	"Feature", "ModelElement", null};

  /** The vector of CustomSeparators to use when tokenizing parameters */
  private Vector _parameterCustomSep;

  /** The character with a meaning as a visibility at the start of an attribute */   
  private final static String visibilityChars = "+#-";

  /**
   * Constructs the object contained in SINGLETON and initializes some
   * instance variables.
   *
   * @see SINGLETON
   */
  private ParserDisplay() {
    _attributeSpecialStrings = new PropertySpecialString[1];
    _attributeSpecialStrings[0] = new PropertySpecialString("frozen",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		MChangeableKind kind = MChangeableKind.FROZEN;
		if (value != null && value.equalsIgnoreCase("false"))
		    kind = MChangeableKind.CHANGEABLE;
		if (element instanceof MStructuralFeature)
		    ((MStructuralFeature)element).setChangeability(kind);
	    }
	});
    _attributeCustomSep = new Vector();
    _attributeCustomSep.add(MyTokenizer.SINGLE_QUOTED_SEPARATOR);
    _attributeCustomSep.add(MyTokenizer.DOUBLE_QUOTED_SEPARATOR);
    _attributeCustomSep.add(MyTokenizer.PAREN_EXPR_STRING_SEPARATOR);

    _operationSpecialStrings = new PropertySpecialString[8];
    _operationSpecialStrings[0] = new PropertySpecialString("sequential",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		MCallConcurrencyKind kind = MCallConcurrencyKind.SEQUENTIAL;
		if (element instanceof MOperation)
		    ((MOperation)element).setConcurrency(kind);
	    }
	});
    _operationSpecialStrings[1] = new PropertySpecialString("guarded",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		MCallConcurrencyKind kind = MCallConcurrencyKind.GUARDED;
		if (value != null && value.equalsIgnoreCase("false"))
		    kind = MCallConcurrencyKind.SEQUENTIAL;
		if (element instanceof MOperation)
		    ((MOperation)element).setConcurrency(kind);
	    }
	});
    _operationSpecialStrings[2] = new PropertySpecialString("concurrent",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		MCallConcurrencyKind kind = MCallConcurrencyKind.CONCURRENT;
		if (value != null && value.equalsIgnoreCase("false"))
		    kind = MCallConcurrencyKind.SEQUENTIAL;
		if (element instanceof MOperation)
		    ((MOperation)element).setConcurrency(kind);
	    }
	});
    _operationSpecialStrings[3] = new PropertySpecialString("concurrency",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		MCallConcurrencyKind kind = MCallConcurrencyKind.SEQUENTIAL;
		if ("guarded".equalsIgnoreCase(value))
		    kind = MCallConcurrencyKind.GUARDED;
		else if ("concurrent".equalsIgnoreCase(value))
		    kind = MCallConcurrencyKind.CONCURRENT;
		if (element instanceof MOperation)
		    ((MOperation)element).setConcurrency(kind);
	    }
	});
    _operationSpecialStrings[4] = new PropertySpecialString("abstract",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		boolean isAbstract = true;
		if (value != null && value.equalsIgnoreCase("false"))
		    isAbstract = false;
		if (element instanceof MOperation)
		    ((MOperation)element).setAbstract(isAbstract);
	    }
	});
    _operationSpecialStrings[5] = new PropertySpecialString("leaf",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		boolean isLeaf = true;
		if (value != null && value.equalsIgnoreCase("false"))
		    isLeaf = false;
		if (element instanceof MOperation)
		    ((MOperation)element).setLeaf(isLeaf);
	    }
	});
    _operationSpecialStrings[6] = new PropertySpecialString("query",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		boolean isQuery = true;
		if (value != null && value.equalsIgnoreCase("false"))
		    isQuery = false;
		if (element instanceof MBehavioralFeature)
		    ((MBehavioralFeature)element).setQuery(isQuery);
	    }
	});
    _operationSpecialStrings[7] = new PropertySpecialString("root",
	new PropertyOperation() {
	    public void found(MModelElement element, String value) {
		boolean isRoot = true;
		if (value != null && value.equalsIgnoreCase("false"))
		    isRoot = false;
		if (element instanceof MOperation)
		    ((MOperation)element).setRoot(isRoot);
	    }
	});
    _operationCustomSep = new Vector();
    _operationCustomSep.add(MyTokenizer.SINGLE_QUOTED_SEPARATOR);
    _operationCustomSep.add(MyTokenizer.DOUBLE_QUOTED_SEPARATOR);
    _operationCustomSep.add(MyTokenizer.PAREN_EXPR_STRING_SEPARATOR);

    _parameterCustomSep = new Vector();
    _parameterCustomSep.add(MyTokenizer.SINGLE_QUOTED_SEPARATOR);
    _parameterCustomSep.add(MyTokenizer.DOUBLE_QUOTED_SEPARATOR);
    _parameterCustomSep.add(MyTokenizer.PAREN_EXPR_STRING_SEPARATOR);
  }

  ////////////////////////////////////////////////////////////////
  // parsing methods

    /**
     * <p>Parse an extension point.<p>
     *
     * <p>The syntax is "name: location", "name:", "location" or "". The
     *   fields of the extension point are updated appropriately.</p>
     *
     * @param useCase  The use case that owns this extension point
     *
     * @param ep       The extension point concerned
     *
     * @param text     The text to parse
     *
     */

    public void parseExtensionPointFig(MUseCase useCase, MExtensionPoint ep,
                                       String text) {

        // We can do nothing if we don't have both the use case and extension
        // point.

        if ((useCase == null) || (ep == null)) {
            return;
        }

        // Parse the string to creat a new extension point.

        MExtensionPoint newEp = parseExtensionPoint(text);

        // If we got back null we interpret this as meaning delete the
        // reference to the extension point from the use case, otherwise we set
        // the fields of the extension point to the values in newEp.

        if (newEp == null) {
            useCase.removeExtensionPoint(ep);
        }
        else {
            ep.setName(newEp.getName());
            ep.setLocation(newEp.getLocation());
        }
    }

/* not used ?
  public void parseOperationCompartment(MClassifier cls, String s) {
    java.util.StringTokenizer st = new java.util.StringTokenizer(s, "\n\r");
    Vector newOps = new Vector();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      MOperation op = parseOperation(token);
      newOps.add(op);
    }
    // System.out.println("parsed " + newOps.size() + " operations");
	Vector features = new Vector(cls.getFeatures());
	Vector oldOps = new Vector(MMUtil.SINGLETON.getOperations(cls));
	features.removeAll(oldOps);

	// don't forget to remove old Operations!
	for (int i = 0; i < oldOps.size(); i++)
		cls.removeFeature((MOperation)oldOps.elementAt(i));

	// now re-set the attributes
	cls.setFeatures(features);

	//features.addAll(newOps);
	//add features with add-Operation, so a role-added-event is generated
	for (int i=0; i<newOps.size(); i++){
	    MOperation oper=(MOperation)newOps.elementAt(i);
	    cls.addFeature(oper);
        cls.getModel();
        oper.getModel();
        MMUtil.SINGLETON.getReturnParameter(oper).getModel();
        MMUtil.SINGLETON.getReturnParameter(oper).getType().getModel();  
	}
  }
*/

  public void parseOperationFig(MClassifier cls, MOperation op, String text)
		throws ParseException {
    if (cls == null || op == null)
	return;

    parseOperation(text, op);
  }

/*
  // Seems to be obsolete
  public void parseAttributeCompartment(MClassifier cls, String s) {
    java.util.StringTokenizer st = new java.util.StringTokenizer(s, "\n\r");
    Vector newAttrs = new Vector();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      try {
        MAttribute attr = parseAttribute(token);
        newAttrs.add(attr);
      } catch (ParseException pe) {
      }
    }
    // System.out.println("parsed " + newAttrs.size() + " attributes");
	Vector features = new Vector(cls.getFeatures());
	Vector oldAttrs = new Vector(MMUtil.SINGLETON.getAttributes(cls));
	features.removeAll(oldAttrs);

	// don't forget to remove old Attrbutes!
        oldAttrs.clear();

	// now re-set the operations
	cls.setFeatures(features);

	//features.addAll(newAttrs);
	//add features with add-Operation, so a role-added-event is generated
	for (int i=0; i<newAttrs.size(); i++){
	    MAttribute attr=(MAttribute)newAttrs.elementAt(i);
	    cls.addFeature(attr);
	}

  }
*/

  public void parseAttributeFig(MClassifier cls, MAttribute at, String text) throws ParseException {
    if (cls == null || at == null)
	return;

    parseAttribute(text, at);
  }

    /**
     * <p>Parse a string representing an extension point and return a new
     *   extension point.</p>
     *
     * <p>The syntax is "name: location", "name:", "location" or
     *   "". <em>Note</em>. If either field is blank, it will be set to null in
     *   the extension point.</p>
     *
     * <p>We break up the string into tokens at the ":". We must keep the ":"
     *   as a token, so we can distinguish between "name:" and "location". The
     *   number of tokens will distinguish our four cases.</p>
     *
     * @param text  The string to parse
     *
     * @return      A new extension point, with fields set appropriately, or
     *              <code>null</code> if we are given <code>null</code> or a
     *              blank string. <em>Note</em>. The string ":" can be used to
     *              set both name and location to null.
     */

    public MExtensionPoint parseExtensionPoint(String text) {

        // If we are given the null string, return immediately

        if (text == null) {
            return null;
        }

        // Build a new extension point

        MExtensionPoint ep = UmlFactory.getFactory().getUseCases().buildExtensionPoint(null);

        StringTokenizer st = new StringTokenizer(text.trim(), ":", true);
        int numTokens = st.countTokens();

        String epLocation;
        String colon;
        String epName;

        switch (numTokens) {

        case 0:

            // The empty string. Return null

            ep = null;

            break;

        case 1:

            // A string of the form "location". This will be confused by the
            // string ":", so we pick this out as an instruction to clear both
            // name and location.

            epLocation = st.nextToken().trim();

            if (epLocation.equals(":")) {
                ep.setName(null);
                ep.setLocation(null);
            }
            else {
                ep.setName(null);
                ep.setLocation(epLocation);
            }

            break;

        case 2:

            // A string of the form "name:"

            epName = st.nextToken().trim();

            ep.setName(epName);
            ep.setLocation(null);

            break;

        case 3:

            // A string of the form "name:location". Discard the middle token
            // (":")

            epName     = st.nextToken().trim();
            colon      = st.nextToken();
            epLocation = st.nextToken().trim();

            ep.setName(epName);
            ep.setLocation(epLocation);

            break;
        }

        return ep;
    }


  /**
   * Parse a line of text and aligns the MOperation to the specification
   * given. The line should be on the following form:
   *
   * <br>visibility name (parameter list) : return-type-expression
   *	{property-string}
   *
   * <p>All elements are optional and, if left unspecified, will preserve
   *	their old values.
   * <br>A <b>stereotype</b> can be given between any element in the line
   *	on the form: &lt;&lt;stereotype&gt;&gt;
   *
   * <p>The following properties are recognized to have special meaning:
   *	abstract, concurrency, concurrent, guarded, leaf, query, root and
   *	sequential.
   *
   * <p>This syntax is compatible with the UML 1.3 spec.
   *
   * @param s The String to parse.
   * @param op The MOperation to adjust to the spcification in s.
   * @throws java.text.ParseException when it detects an error in the
   *	attribute string. See also ParseError.getErrorOffset().
   */
   /* (formerly visibility name (parameter list) : return-type-expression
    *	{property-string} ) */
   /* (formerly 2nd: [visibility] [keywords] returntype name(params)[;] ) */
  public void parseOperation(String s, MOperation op) throws ParseException {
    MyTokenizer st;
    boolean hasColon = false;
    String name = null;
    String parameterlist = null;
    String stereotype = null;
    String token;
    String type = null;
    String visibility = null;
    Vector properties = null;
    int paramOffset = 0;

    s = s.trim();

    if (s.length() > 0 && visibilityChars.indexOf(s.charAt(0)) >= 0) {
	visibility = s.substring(0, 1);
	s = s.substring(1);
    }

    try {
	st = new MyTokenizer(s, " ,\t,<<,>>,:,=,{,},\\,", _operationCustomSep);
	while (st.hasMoreTokens()) {
	    token = st.nextToken();
	    if (" ".equals(token) || "\t".equals(token) || ",".equals(token)) {
		; // Do nothing
	    } else if ("<<".equals(token)) {
		if (stereotype != null)
		    throw new ParseException("Operations cannot have two " +
			    "stereotypes", st.getTokenIndex());
		stereotype = "";
		while (true) {
		    token = st.nextToken();
		    if (">>".equals(token))
			break;
		    stereotype += token;
		}
	    } else if ("{".equals(token)) {
		String propname = "";
		String propvalue = null;

		if (properties == null)
		    properties = new Vector();
		while (true) {
		    token = st.nextToken();
		    if (",".equals(token) || "}".equals(token)) {
			if (propname.length() > 0) {
			    properties.add(propname);
			    properties.add(propvalue);
			}
			propname = "";
			propvalue = null;

			if ("}".equals(token))
			    break;
		    } else if ("=".equals(token)) {
			if (propvalue != null)
			    throw new ParseException("Property " + propname +
				    " cannot have two values",
				    st.getTokenIndex());
			propvalue = "";
		    } else if (propvalue == null) {
			propname += token;
		    } else {
			propvalue += token;
		    }
		}
		if (propname.length() > 0) {
		    properties.add(propname);
		    properties.add(propvalue);
		}
	    } else if (":".equals(token)) {
		hasColon = true;
	    } else if ("=".equals(token)) {
		throw new ParseException("Operations cannot have " +
			"default values", st.getTokenIndex());
	    } else if (token.charAt(0) == '(' && !hasColon) {
		if (parameterlist != null)
		    throw new ParseException("Operations cannot have two " +
			    "parameter lists", st.getTokenIndex());

		parameterlist = token;
	    } else {
		if (hasColon) {
		    if (type != null)
			throw new ParseException("Operations cannot have " +
				"two types", st.getTokenIndex());

		    if (token.length() > 0 && (token.charAt(0) == '\"' ||
			token.charAt(0) == '\''))
			throw new ParseException("Type cannot be quoted",
				st.getTokenIndex());

		    if (token.length() > 0 && token.charAt(0) == '(')
			throw new ParseException("Type cannot be an " +
				"expression", st.getTokenIndex());

		    type = token;
		} else {
		    if (name != null && visibility != null)
			throw new ParseException("Extra text in Operation",
				st.getTokenIndex());

		    if (token.length() > 0 && (token.charAt(0) == '\"' ||
			token.charAt(0) == '\''))
			throw new ParseException("Name or visibility cannot " +
				"be quoted", st.getTokenIndex());

		    if (token.length() > 0 && token.charAt(0) == '(')
			throw new ParseException("Name or visibility cannot " +
				"be an expression", st.getTokenIndex());

		    if (name == null && visibility == null &&
			    token.length() > 1 &&
			    visibilityChars.indexOf(token.charAt(0)) >= 0) {
			visibility = token.substring(0, 1);
			token = token.substring(1);
		    }

		    if (name != null) {
			visibility = name;
			name = token;
		    } else {
			name = token;
		    }
		}
	    }
	}
    } catch (NoSuchElementException nsee) {
	throw new ParseException("Unexpected end of operation", s.length());
    } catch (ParseException pre) {
	// System.out.println(pre);
	throw pre;
    }
/*
    System.out.println("ParseOperation [name: " + name + " visibility: " +
	    visibility + " type: " + type + " stereo: " + stereotype);

    if (properties != null) {
	for (int i = 0; i + 1 < properties.size(); i += 2) {
	    System.out.println("\tProperty [name: " + properties.get(i) +
		    " = " + properties.get(i+1) + "]");
	}
    }
*/
    if (parameterlist != null) {
	// parameterlist is guaranteed to contain at least "("
	if (parameterlist.charAt(parameterlist.length()-1) != ')')
	    throw new ParseException("The parameter list was incomplete",
		    paramOffset + parameterlist.length() - 1);

	paramOffset++;
	parameterlist = parameterlist.substring(1,parameterlist.length()-1);
	parseParamList(op, parameterlist, paramOffset);
    }

    if (visibility != null) {
	MVisibilityKind vis = getVisibility(visibility.trim());
	op.setVisibility(vis);
    }

    if (name != null)
	op.setName(name.trim());
    else if (op.getName() == null || "".equals(op.getName()))
	op.setName("anonymous");

    if (type != null) {
	MClassifier ow = op.getOwner();
	MNamespace ns = null;
	if (ow != null && ow.getNamespace() != null)
	    ns = ow.getNamespace();
	else
	    ns = op.getModel();
	MClassifier mtype = getType(type.trim(), ns);
	setReturnParameter(op, mtype);
    }

    if (properties != null)
	setProperties(op, properties, _operationSpecialStrings);

    if (stereotype != null) {
	stereotype = stereotype.trim();
	MStereotype stereo = getStereotype(op.getModel(), stereotype,
		_operationBases);
	if (stereo != null)
	    op.setStereotype(stereo);
	else if ("".equals(stereotype))
	    op.setStereotype(null);
    }
  }

  /**
   * Parses a parameter list and aligns the parameter list in op to that
   * specified in param. A parameter list generally has the following syntax:
   *
   * <br>param := [inout] [name] [: type] [= initial value]
   * <br>list := [param] [, param]*
   *
   * <p><b>inout</b> is optional and if omitted the old value preserved.
   *	If no value has been assigned, then <b>in</b> is assumed.
   * <br><b>name</b>, <b>type</b> and <b>initial value</b> are optional
   *	and if omitted the old value preserved.
   * <br><b>type</b> and <b>initial value</b> can be given in any order.
   * <br>Unspecified properties is carried over by position, so if a parameter
   *	is inserted into the list, then it will inherit properties from the
   *	parameter that was there before for unspecified properties.
   *
   * <p>This syntax is compatible with the UML 1.3 specification.
   *
   * @param op The MOperation the parameter list belongs to.
   * @param param The parameter list, without enclosing parentheses.
   * @param paramOffset The offset to the beginning of the parameter list.
   *	Used for error reports.
   * @throws java.text.ParseException when it detects an error in the
   *	attribute string. See also ParseError.getErrorOffset().
   */
  private void parseParamList(MOperation op, String param, int paramOffset)
		throws ParseException {
    MyTokenizer st = new MyTokenizer(param, " ,\t,:,=,\\,", _parameterCustomSep);
    List origParam = op.getParameters();
    MClassifier ow = op.getOwner();
    MNamespace ns = null;
    if (ow != null && ow.getNamespace() != null)
	ns = ow.getNamespace();
    else
	ns = op.getModel();

    Iterator it = origParam.iterator();
    while (st.hasMoreTokens()) {
	String kind = null;
	String name = null;
	String tok;
	String type = null;
	String value = null;
	MParameter p = null;
	boolean hasColon = false;
	boolean hasEq = false;

	while (it.hasNext() && p == null) {
	    p = (MParameter) it.next();
	    if (p.getKind().equals(MParameterDirectionKind.RETURN))
		p = null;
	}

	while (st.hasMoreTokens()) {
	    tok = st.nextToken();

	    if (",".equals(tok)) {
		break;
	    } else if (" ".equals(tok) || "\t".equals(tok)) {
		if (hasEq)
		    value += tok;
	    } else if (":".equals(tok)) {
		hasColon = true;
		hasEq = false;
	    } else if ("=".equals(tok)) {
		if (value != null)
		    throw new ParseException("Parameters cannot have two " +
			"default values", paramOffset + st.getTokenIndex());
		hasEq = true;
		hasColon = false;
		value = "";
	    } else if (hasColon) {
		if (type != null)
		    throw new ParseException("Parameters cannot have two " +
			"types", paramOffset + st.getTokenIndex());

		if (tok.charAt(0) == '\'' || tok.charAt(0) == '\"')
		    throw new ParseException("Parameter type cannot be " +
			"quoted", paramOffset + st.getTokenIndex());

		if (tok.charAt(0) == '(')
		    throw new ParseException("Parameter type cannot be an " +
			"expression", paramOffset + st.getTokenIndex());

		type = tok;
	    } else if (hasEq) {
		value += tok;
	    } else {
		if (name != null && kind != null)
		    throw new ParseException("Extra text in parameter",
			paramOffset + st.getTokenIndex());

		if (tok.charAt(0) == '\'' || tok.charAt(0) == '\"')
		    throw new ParseException("Parameter name/kind cannot be" +
			" quoted", paramOffset + st.getTokenIndex());

		if (tok.charAt(0) == '(')
		    throw new ParseException("Parameter name/kind cannot be" +
			" an expression", paramOffset + st.getTokenIndex());

		kind = name;
		name = tok;
	    }
	}

	if (p == null) {
	    p = CoreFactory.getFactory().buildParameter();
	    op.addParameter(p);
	}

	if (name != null)
	    p.setName(name.trim());

	if (kind != null)
	    p.setKind(getParamKind(kind.trim()));

	if (type != null)
	    p.setType(getType(type.trim(), ns));

	if (value != null) {
	    MExpression initExpr =
		UmlFactory.getFactory().getDataTypes().createExpression(
			Notation.getDefaultNotation().toString(),
			value.trim());
	    p.setDefaultValue(initExpr);
	}
    }

    while (it.hasNext()) {
        MParameter p = (MParameter) it.next();
	if (!p.getKind().equals(MParameterDirectionKind.RETURN))
	    op.removeParameter(p);
    }
  }

  /**
   * Sets the return parameter of op to be of type type. If there is none,
   * one is created. If there are many, all but one are removed.
   */
  private void setReturnParameter(MOperation op, MClassifier type) {
    MParameter param = null;
    Iterator it = op.getParameters().iterator();
    while (it.hasNext()) {
	MParameter p = (MParameter) it.next();
	if (MParameterDirectionKind.RETURN.equals(p.getKind())) {
	    param = p;
	    break;
	}
    }
    while (it.hasNext()) {
	MParameter p = (MParameter) it.next();
	if (MParameterDirectionKind.RETURN.equals(p.getKind()))
	    op.removeParameter(p);
    }
    if (param == null) {
	param = UmlFactory.getFactory().getCore().buildParameter();
	op.addParameter(param);
    }
    param.setType(type);
  }

  private MParameterDirectionKind getParamKind(String s) {
    MParameterDirectionKind kind = MParameterDirectionKind.IN;
    if ("out".equalsIgnoreCase(s))
	kind = MParameterDirectionKind.OUT;
    else if ("inout".equalsIgnoreCase(s))
	kind = MParameterDirectionKind.INOUT;

    return kind;
  }

/**
 * Parses a string for multiplicity and sets the multiplicity with the given 
 * attribute.
 * @param f
 * @param s
 * @return String
 */
protected String parseOutMultiplicity(MAttribute f, String s) {
    s = s.trim();
    String multiString = "";
    int terminatorIndex = s.indexOf(':');
    if (terminatorIndex < 0) terminatorIndex = s.indexOf('=');
    if (terminatorIndex < 0) terminatorIndex = s.indexOf('{');
    if (terminatorIndex < 0) {
        multiString = s;
    }
    else {
        multiString = s.substring(0, terminatorIndex);
    }

    s = s.substring(multiString.length(), s.length());
    
    multiString = multiString.trim();
    int multiStart = 0;
    int multiEnd = multiString.length();
    
    if (multiEnd > 0 && multiString.charAt(0) == '[') multiStart = 1;
    if (multiEnd > 0 && multiString.charAt(multiEnd-1) == ']') --multiEnd;
    multiString = multiString.substring(multiStart, multiEnd).trim();
    
    if (multiString.length() > 0) f.setMultiplicity(UmlFactory.getFactory().getDataTypes().createMultiplicity(multiString));

    return s;
}

   /** Parse a line on the form:<br>
    * visibility name [: type-expression] [= initial-value]
    *<ul>
    * <li>If only one of visibility and name is given, then it is assumed to be
    *	the name and the visibility is left unchanged.
    * <li>Type and initial value can be given in any order.
    * <li>Properties can be given between any element on the form
    *	{[name] [= [value]] [, ...]}.
    * <li>Multiplicity can be given between any element except after the
    *	initial-value and before the type or end (to allow java-style array
    *	indexing in the initial value). It must be given on form
    *	[multiplicity] with the square brackets included.
    * <li>Stereotype can be given between any element except after the
    *	initial-value and before the type or end (to allow java-style
    *   bit-shifts in the initial value). It must be given on form
    *	&lt;&lt;stereotype&gt;&gt;.
    *</ul>
    *
    * <p>The following properties are recognized to have special meaning:
    *	frozen.
    *
    * <p>This syntax is compatible with the UML 1.3 spec.
    *
    * @param s The String to parse.
    * @param attr The attribute to modify to comply with the instructions in s.
    * @throws java.text.ParseException when it detects an error in the
    *	attribute string. See also ParseError.getErrorOffset().
    */
   /* (formerly: visibility name [multiplicity] : type-expression
    *   = initial-value {property-string} ) */
   /* (2nd formerly: [visibility] [keywords] type name [= init] [;] ) */
  public void parseAttribute(String s, MAttribute attr) throws ParseException {
    String multiplicity = null;
    String name = null;
    Vector properties = null;
    String stereotype = null;
    String token;
    String type = null;
    String value = null;
    String visibility = null;
    boolean hasColon = false;
    boolean hasEq = false;
    int multindex = -1;
    MyTokenizer st;

    s = s.trim();
    if (s.length() > 0 && visibilityChars.indexOf(s.charAt(0)) >= 0) {
	visibility = s.substring(0, 1);
	s = s.substring(1);
    }

    try {
	st = new MyTokenizer(s, " ,\t,<<,>>,[,],:,=,{,},\\,", _attributeCustomSep);
	while (st.hasMoreTokens()) {
	    token = st.nextToken();
	    if (" ".equals(token) || "\t".equals(token) || ",".equals(token)) {
		if (hasEq)
		    value += token;
	    } else if ("<<".equals(token)) {
		if (hasEq) {
		    value += token;
		} else {
		    if (stereotype != null)
			throw new ParseException("Attribute cannot have two " +
				"stereotypes", st.getTokenIndex());
		    stereotype = "";
		    while (true) {
			token = st.nextToken();
			if (">>".equals(token))
			    break;
			stereotype += token;
		    }
		}
	    } else if ("[".equals(token)) {
		if (hasEq) {
		    value += token;
		} else {
		    if (multiplicity != null)
			throw new ParseException("Attribute cannot have two" +
				" multiplicities", st.getTokenIndex());
		    multiplicity = "";
		    multindex = st.getTokenIndex() + 1;
		    while (true) {
			token = st.nextToken();
			if ("]".equals(token))
			    break;
			multiplicity += token;
		    }
		}
	    } else if ("{".equals(token)) {
		String propname = "";
		String propvalue = null;

		if (properties == null)
		    properties = new Vector();
		while (true) {
		    token = st.nextToken();
		    if (",".equals(token) || "}".equals(token)) {
			if (propname.length() > 0) {
			    properties.add(propname);
			    properties.add(propvalue);
			}
			propname = "";
			propvalue = null;

			if ("}".equals(token))
			    break;
		    } else if ("=".equals(token)) {
			if (propvalue != null)
			    throw new ParseException("Property " + propname +
				    " cannot have two values",
				    st.getTokenIndex());
			propvalue = "";
		    } else if (propvalue == null) {
			propname += token;
		    } else {
			propvalue += token;
		    }
		}
		if (propname.length() > 0) {
		    properties.add(propname);
		    properties.add(propvalue);
		}
	    } else if (":".equals(token)) {
		hasColon = true;
		hasEq = false;
	    } else if ("=".equals(token)) {
		if (value != null)
		    throw new ParseException("Attribute cannot have two " +
			    "default values", st.getTokenIndex());
		value = "";
		hasColon = false;
		hasEq = true;
	    } else {
		if (hasColon) {
		    if (type != null)
			throw new ParseException("Attribute cannot have two" +
				" types", st.getTokenIndex());
		    if (token.length() > 0 && (token.charAt(0) == '\"' ||
			token.charAt(0) == '\''))
			throw new ParseException("Type cannot be quoted",
				st.getTokenIndex());
		    if (token.length() > 0 && token.charAt(0) == '(')
			throw new ParseException("Type cannot be an " +
				"expression", st.getTokenIndex());
		    type = token;
		} else if (hasEq) {
		    value += token;
		} else {
		    if (name != null && visibility != null)
			throw new ParseException("Extra text in Attribute",
				st.getTokenIndex());
		    if (token.length() > 0 && (token.charAt(0) == '\"' ||
			token.charAt(0) == '\''))
			throw new ParseException("Name or visibility cannot " +
				"be quoted", st.getTokenIndex());
		    if (token.length() > 0 && token.charAt(0) == '(')
			throw new ParseException("Name or visibility cannot " +
				"be an expression", st.getTokenIndex());

		    if (name == null && visibility == null &&
			    token.length() > 1 &&
			    visibilityChars.indexOf(token.charAt(0)) >= 0) {
			visibility = token.substring(0, 1);
			token = token.substring(1);
		    }

		    if (name != null) {
			visibility = name;
			name = token;
		    } else {
			name = token;
		    }
		}
	    }
	}
    } catch (NoSuchElementException nsee) {
	throw new ParseException("Unexpected end of attribute", s.length());
    } catch (ParseException pre) {
	// System.out.println(pre);
	throw pre;
    }
/*
    System.out.println("ParseAttribute [name: " + name + " visibility: " +
	visibility + " type: " + type + " value: " + value + " stereo: " +
	stereotype + " mult: " + multiplicity);

    if (properties != null) {
	for (int i = 0; i + 1 < properties.size(); i += 2) {
	    System.out.println("\tProperty [name: " + properties.get(i) +
		" = " + properties.get(i+1) + "]");
	}
    }
*/

    if (visibility != null) {
	MVisibilityKind vis = getVisibility(visibility.trim());
	attr.setVisibility(vis);
    }

    if (name != null)
	attr.setName(name.trim());
    else if (attr.getName() == null || "".equals(attr.getName()))
	attr.setName("anonymous");

    if (type != null) {
	MClassifier ow = attr.getOwner();
	MNamespace ns = null;
	if (ow != null && ow.getNamespace() != null)
	    ns = ow.getNamespace();
	else
	    ns = attr.getModel();
	attr.setType(getType(type.trim(), ns));
    }

    if (value != null) {
	MExpression initExpr =
		UmlFactory.getFactory().getDataTypes().createExpression(
			Notation.getDefaultNotation().toString(),
			value.trim());
	attr.setInitialValue(initExpr);
    }

    if (multiplicity != null) {
	try {
	    attr.setMultiplicity(
		    UmlFactory.getFactory().getDataTypes().createMultiplicity(
			    multiplicity.trim()));
	} catch (IllegalArgumentException iae) {
	    throw new ParseException("Bad multiplicity (" + iae + ")",
		    multindex);
	}
    }

    if (properties != null)
	setProperties(attr, properties, _attributeSpecialStrings);

    if (stereotype != null) {
	stereotype = stereotype.trim();
	MStereotype stereo = getStereotype(attr.getModel(), stereotype,
		_attributeBases);
	if (stereo != null)
	    attr.setStereotype(stereo);
	else if ("".equals(stereotype))
	    attr.setStereotype(null);
    }
  }

  /**
   * Finds the MClassifier associated with the type named in name.
   *
   * @param name The name of the type to get.
   * @param defaultSpace The default name-space to place the type in.
   * @return The MClassifier associated with the name.
   */
  private MClassifier getType(String name, MNamespace defaultSpace) {
    MClassifier type = null;
    Project p = ProjectBrowser.TheInstance.getProject();
    // Should we be getting this from the GUI? BT 11 aug 2002
    type = p.findType(name);
    if (type == null) { // no type defined yet
	type = UmlFactory.getFactory().getCore().buildClass(name);                
    }
    if (type.getModel() != p.getModel() && !ModelManagementHelper.getHelper().getAllNamespaces(p.getModel()).contains(type.getNamespace())) {
    	type.setNamespace(p.getModel());
    }
    return type;
  }

  /**
   * Finds a MVisibilityKind for the visibility specified by name. If no
   * known visibility can be deduced, private visibility is used.
   *
   * @param name The name of the visibility.
   * @return A MVisibilityKind corresponding to name.
   */
  private MVisibilityKind getVisibility(String name) {
    if ("+".equals(name) || "public".equals(name))
	return MVisibilityKind.PUBLIC;
    else if ("#".equals(name) || "protected".equals(name))
	return MVisibilityKind.PROTECTED;
    else /* if ("-".equals(name) || "private".equals(name)) */
	return MVisibilityKind.PRIVATE;
  }

  /**
   * Applies a Vector of name value pairs of properties to a MModelElement.
   * The name is treaded as the tag of a tagged value unless it is one of the
   * PropertySpecialStrings, in which case the action of the
   * PropertySpecialString is invoked.
   *
   * @param elem An element to apply the properties to.
   * @param prop A Vector with name, value pairs of properties.
   * @param spec An array of PropertySpecialStrings to use.
   */
  private void setProperties(MModelElement elem, Vector prop,
			     PropertySpecialString spec[]) {
    String name;
    String value;
    int i, j;

nextProp:
    for (i = 0; i + 1 < prop.size(); i += 2) {
	name = (String) prop.get(i);
	value = (String) prop.get(i+1);

	if (name == null)
	    continue;

	name = name.trim();
	if (value != null)
	    value = value.trim();

	for (j = i + 2; j < prop.size(); j += 2) {
	    String s = (String) prop.get(j);
	    if (s != null && name.equalsIgnoreCase(s.trim()))
		continue nextProp;
	}

	if (spec != null) {
	    for (j = 0; j < spec.length; j++)
		if (spec[j].invoke(elem, name, value))
		    continue nextProp;
	}

	elem.setTaggedValue(name, value);
    }
  }

  /**
   * Recursively search a hive of a model for a MStereotype with the name
   * given in name.
   *
   * @param root The MModelElement to search from.
   * @param name The name of the MStereotype to search for.
   * @return An MStereotype named name, or null if none is found.
   */
  private MStereotype recFindStereotype(MModelElement root, String name,
	    String base[]) {
    MStereotype stereo;

    if (root == null)
	return null;

    if (root instanceof MStereotype &&
	name.equals(root.getName())) {
	int i;
	for (i = 0; base != null && i < base.length; i++) {
	    String bc = ((MStereotype)root).getBaseClass();
	    if ((base[i] == null && bc == null) ||
		(base[i] != null && base[i].equals(bc)))
		break;
	}

	if (base == null || i < base.length)
	    return (MStereotype) root;
/*
	else
	    System.out.println("Missed stereotype " + ((MStereotype)root).getBaseClass());
*/
    }

    if (!(root instanceof MNamespace))
	return null;

    MNamespace nameSpace     = (MNamespace) root;
    Collection ownedElements = nameSpace.getOwnedElements();

    if(ownedElements == null) {
	return null;
    }

    // Loop through each element in the namespace, recursing.

    Iterator iter = ownedElements.iterator();

    while(iter.hasNext()) {
	stereo = recFindStereotype((MModelElement) iter.next(), name, base);
	if (stereo != null)
	    return stereo;
    }
    return null;
  }

  // Refactoring: static to denote that it doesn't use any class members.
  // Needs-more-work:
  // Idea to move this to MMUtil together with the same function from
  // org/argouml/uml/ui/UMLComboBoxEntry.java
  // org/argouml/uml/cognitive/critics/WizOperName.java
  private static MNamespace findNamespace(MNamespace phantomNS, MModel targetModel) {
    MNamespace ns = null;
    MNamespace targetParentNS = null;
    MNamespace parentNS = phantomNS.getNamespace();
    if(parentNS == null) {
	ns = targetModel;
    } else {
	targetParentNS = findNamespace(parentNS,targetModel);
	//
	//   see if there is already an element with the same name
	//
	Collection ownedElements = targetParentNS.getOwnedElements();
	String phantomName = phantomNS.getName();
	String targetName;
	if(ownedElements != null) {
	    MModelElement ownedElement;
	    Iterator iter = ownedElements.iterator();
	    while(iter.hasNext()) {
		ownedElement = (MModelElement) iter.next();
		targetName = ownedElement.getName();
		if(targetName != null && phantomName.equals(targetName)) {
		    if(ownedElement instanceof MPackage) {
			ns = (MPackage) ownedElement;
			break;
		    }
		}
	    }
	}
	if(ns == null) {
	    ns = targetParentNS.getFactory().createPackage();
	    ns.setName(phantomName);
	    targetParentNS.addOwnedElement(ns);
	}
    }
    return ns;
  }

  /**
   * Finds a MStereotype named name either in the subtree of the model
   * rooted at root, or in the the ProfileJava model.
   *
   * <p>needs-more-work: Should create the MStereotype under root if it
   * isn't found.
   *
   * @param root A tree of MModelElements to search.
   * @param name The name of the MStereotype to search for.
   * @return An MStereotype named name, or possibly null.
   */
  private MStereotype getStereotype(MModelElement root, String name,
	    String base[]) {
    MStereotype stereo;
    boolean phantom = false;

    stereo = recFindStereotype(root, name, base);
    if (stereo == null) {
	stereo = recFindStereotype(
		ProfileJava.getInstance().getProfileModel(),
		name,
		base);

	// This (if it exist) is a phantom stereotype...
	phantom = true;
    }

    if (stereo != null && phantom) {
	if (root instanceof MModel) {
	    MNamespace targetNS = findNamespace(stereo.getNamespace(), (MModel)root);
	    MStereotype clone = null;
	    try {
		clone = (MStereotype) stereo.getClass().getConstructor(new Class[] {}).newInstance(new Object[] {});
		clone.setName(stereo.getName());
		clone.setStereotype(stereo.getStereotype());
		clone.setBaseClass(((MStereotype) stereo).getBaseClass());
		targetNS.addOwnedElement(clone);
		stereo = clone;
	    }
	    catch(Exception ex) {
		ex.printStackTrace();
	    }
	} else {
	    stereo = null;
	}
    }

    //if (stereo == null && root != null) {
    //    Create a new stereotype
    //}

    return stereo;
  }

/**
 * Parses the properties for some attribute a out of a string s. The properties 
 * are all keywords between the braces at the end of a string notation of an 
 * attribute.
 * @param a
 * @param s
 * @return String
 */
  protected String parseOutProperties(MAttribute a, String s) {
    s = s.trim();
    if (s.indexOf("{") >= 0) {
        StringTokenizer tokenizer = new StringTokenizer(
            s.substring(s.indexOf("{")+1, s.indexOf("}")), ",");
        List properties = new ArrayList();
        while (tokenizer.hasMoreElements()) {
            properties.add(tokenizer.nextToken().trim());
        }
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).equals("frozen")) {
                a.setChangeability(MChangeableKind.FROZEN);
            } else {
                String propertyStr = (String)properties.get(i);
                String tagStr = "";
                String valueStr = "";
                if (propertyStr.indexOf("=")>=0) {
                    tagStr = propertyStr.substring(0, propertyStr.indexOf("=")-1);
                    valueStr = propertyStr.substring(propertyStr.indexOf("="+1, propertyStr.length()));
                }
                MTaggedValue tag = UmlFactory.getFactory().getExtensionMechanisms().createTaggedValue();
 
                tag.setTag(tagStr);
                tag.setValue(valueStr);
                a.addTaggedValue(tag);
            }
        }
        return s.substring(s.indexOf("}"), s.length());
    }          
    return s;
  }
  
  protected String parseOutProperties(MOperation op, String s) {
    s = s.trim();
    if (s.indexOf("{") >= 0) {
        StringTokenizer tokenizer = new StringTokenizer(
            s.substring(s.indexOf("{")+1, s.indexOf("}")), ",");
        List properties = new ArrayList();
        while (tokenizer.hasMoreElements()) {
            properties.add(tokenizer.nextToken().trim());
        }
        for (int i = 0; i < properties.size(); i++) {
            if (properties.get(i).equals("query")) {
                op.setQuery(true);
            } else {
                if (properties.get(i).equals("sequential") || 
                    properties.get(i).equals("concurrency=sequential")) {
                        op.setConcurrency(MCallConcurrencyKind.SEQUENTIAL);
                } else {
                    if (properties.get(i).equals("guarded") ||
                        properties.get(i).equals("concurrency=guarded")) {
                        op.setConcurrency(MCallConcurrencyKind.GUARDED);
                    } else {
                        if (properties.get(i).equals("concurrent") || 
                            properties.get(i).equals("concurrency=concurrent")) {
                                op.setConcurrency(MCallConcurrencyKind.CONCURRENT);
                        } else {
                            String propertyStr = (String)properties.get(i);
                            String tagStr = "";
                            String valueStr = "";
                            if (propertyStr.indexOf("=")>=0) {
                                tagStr = propertyStr.substring(0, propertyStr.indexOf("=")-1);
                                valueStr = propertyStr.substring(propertyStr.indexOf("="+1, propertyStr.length()));
                            }
                            MTaggedValue tag = UmlFactory.getFactory().getExtensionMechanisms().createTaggedValue();
                            tag.setTag(tagStr);
                            tag.setValue(valueStr);
                            op.addTaggedValue(tag);
                        }
                    }
                }
            }
        }
        return s.substring(s.indexOf("}"), s.length());
    }          
    return s;
  }
  /*
  public String parseOutMultiplicity(MFeature f, String s) {

    s = s.trim();
    MMultiplicity multi = UmlFactory.getFactory().getDataTypes().createMultiplicity();
    boolean startMulti = false; // start of a multiplicity
    boolean inRange = false;    // true if we are in a range
    boolean formerNumber = false;   // true if last char was a number
    boolean inMultiString = true;  // true if we are still parsing a multi
                                     // string\
    boolean inDots = false      // true if we are in ..
    int dotCounter = 0;          // number of dots we passed
    StringBuffer startMultiSb = new StringBuffer();
    StringBuffer endMultiSb = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        switch (c) {
            case '0' : case '1' : case '2' : case '3' :
            case '4' : case '5' : case '6' : case '7' :
            case '8' : case '9' : case '*' :
                if (!inRange && inMultiString) { // we start possibly a range
                    startMulti = true;
                    startMultiSb.append(c);
                }
                if (inRange && inMultiString) { // end range
                    if (inDots) {               // first char of endrange
                        inDots = false;
                        endMultiSb = new StringBuffer();
                    }
                    endMultiSb.append(c);
                }
                break;
            case '.' :  
                if (!inDots && inMultiString) {
                    inDots = true;
                    dotCounter = 0;
                }
                dotCounter++; 
                break;
            case ',' :
                if (inMultiString) { // we have an end sign of a mutiplicity
                    
                break;
            case ' ' :
                break;
            default : // some other character
                inMultiString = false; //only here if we stopped parsing the
                                        // multistring
                break;
        }
  }
*/

/**
 * Parses a string for visibilitykind. Visibilitykind can both be specified 
 * using the standard #, +, - and the keywords public, private, protected.
 * @param f The feature the visibility is part of
 * @param s The string that possibly identifies some visibility
 * @return String The string s WITHOUT the visibility signs. 
 */
  public String parseOutVisibility(MFeature f, String s) {
    s = s.trim();
    // We only support UML 1.3 notation in this parser
    // get the first char
    String visStr = s.substring(0, 1);
    if (visStr.equals("#")) {
        f.setVisibility(MVisibilityKind.PROTECTED);
        return s.substring(1, s.length());
    } else if (visStr.equals("-")) {
        f.setVisibility(MVisibilityKind.PRIVATE);
        return s.substring(1, s.length());
    } else if (visStr.equals("+")) {
        f.setVisibility(MVisibilityKind.PUBLIC);
        return s.substring(1, s.length());
    } 
    // public, private, protected as keyword
    int firstSpace = s.indexOf(' ');
    if (firstSpace > 0) {
	visStr = s.substring(0, firstSpace);
	if (visStr.equals("public")) {
	    f.setVisibility(MVisibilityKind.PUBLIC);
	    return s.substring(firstSpace, s.length());
	} else if (visStr.equals("protected")) {
	    f.setVisibility(MVisibilityKind.PROTECTED);
	    return s.substring(firstSpace, s.length());
	} else if (visStr.equals("private")) {
	    f.setVisibility(MVisibilityKind.PRIVATE);
	    return s.substring(firstSpace, s.length());
	}
    }
    return s;
  }

/*
 * removed next method since it is obsolete. We now use the standard notation
 * for UML 1.3 as defined in the spec.
  public String parseOutKeywords(MFeature f, String s) {
    s = s.trim();
    int firstSpace = s.indexOf(" ");
    if (firstSpace == -1) return s;
    String visStr = s.substring(0, firstSpace);

      if (visStr.equals("static"))
	f.setOwnerScope(MScopeKind.CLASSIFIER);
      else if (visStr.equals("synchronized") && (f instanceof MOperation))
	((MOperation)f).setConcurrency(MCallConcurrencyKind.GUARDED);
      else if (visStr.equals("transient"))
	System.out.println("'transient' keyword is currently ignored");
      else if (visStr.equals("final"))
	System.out.println("'final' keyword is currently ignored");
      else if (visStr.equals("abstract"))
	System.out.println("'abstract' keyword is currently ignored");
      else {
	return s;
      }

    return parseOutKeywords(f, s.substring(firstSpace+1));
  }
*/

    /**
     * Parses the parameters with an operation. The string containing the 
     * parameters must be the first string within the given string s. It must 
     * start with ( and the end of the string containing the parameters is ).
     * @param op
     * @param s
     * @return String
     */
	public String parseOutParams(MOperation op, String s) {
		s = s.trim();
		String leftOver = s;
		int end = s.lastIndexOf(")");
		if (end != -1) {
			java.util.StringTokenizer st = new java.util.StringTokenizer(s.substring(1,end), ",");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				MParameter p = parseParameter(token);
				if (p != null) op.addParameter(p);
			}
			leftOver = s.substring(end+1);
		}
		return leftOver;
	}

/**
 * Parses the name of modelelement me from some input string s. The name must be
 * the first word of the string. 
 * @param me
 * @param s
 * @return String
 */
  public String parseOutName(MModelElement me, String s) {
    String delim = ": \t()[]{}=;";
    s = s.trim();
    if (s.equals("") || delim.indexOf(s.charAt(0)) >= 0) { //duh there is no name
        if (me.getName() == null || me.getName().equals("")) {
            me.setName("anno");
        } 
        return s;
    }
    // next sign can be: ' ', '=', ':', '{', '}', '\n', '[', ']', '(', ')'
    // any of these indicate that name is to an end.    
    java.util.StringTokenizer st = new java.util.StringTokenizer(s, delim);
    if (!st.hasMoreTokens()) {
      System.out.println("name not parsed");
      return s;
    }
    String nameStr = st.nextToken();

    // needs-more-work: wasteful
     me.setName(nameStr);

    int namePos = s.indexOf(nameStr);
    return s.substring(namePos + nameStr.length());
  }
    
    /**
     * Parses the user given string s for the type of an attribute. The string 
     * should start with :. The part between : and { (if there are properties) 
     * or the end of the string if there are no properties.
     * @param attr
     * @param s
     * @return String
     */
	public String parseOutType(MAttribute attr, String s) {
        s = s.trim();       
        if (s.startsWith(":")) { // we got ourselves a type expression
            MClassifier type = null;
            s = s.substring(1, s.length()).trim();
            String typeExpr = beforeAnyOf(s, " ={").trim();
	    if (typeExpr.length() > 0) {
		Project p = ProjectBrowser.TheInstance.getProject();
		type = p.findType(typeExpr); // Should we be getting this from the GUI? BT 11 aug 2002
		if (type == null) { // no type defined yet
		    type = UmlFactory.getFactory().getCore().buildClass(typeExpr);                
		}
		if (attr.getNamespace() != null) {
		    type.setNamespace(attr.getNamespace());
		}
		attr.setType(type);
		s = s.substring(typeExpr.length(), s.length());
	    }
        }
        return s;
    }
    
    
    /**
     * Parses the return type for an operation.
     * @param op
     * @param s
     * @return String
     */
    protected String parseOutReturnType(MOperation op, String s) {
        s = s.trim();       
        if (s.startsWith(":")) { // we got ourselves a type expression
            MClassifier type = null;
            s = s.substring(1, s.length()).trim();
            String typeExpr = beforeAnyOf(s, " ={").trim();
	    typeExpr = typeExpr.trim();
	    if (typeExpr.length() > 0) {
		Project p = ProjectBrowser.TheInstance.getProject();
		type = p.findType(typeExpr);
		if (type == null) { // no type defined yet
		    type = UmlFactory.getFactory().getCore().buildClass(typeExpr); 
		    // the owner of this type should be the model in which
		    // the class that contains the operation lives
		    // since we don't know that class, the model is not set here
		    // but in the method that calls parseOperation(String s)               
		}
		MParameter param = UmlFactory.getFactory().getCore().buildParameter();
		UmlHelper.getHelper().getCore().setReturnParameter(op,param);
		param.setType(type);
		s = s.substring(typeExpr.length(), s.length());
	    }
        }
        return s;
    }


    protected String parseOutInitValue(MAttribute attr, String s) {
        s = s.trim();
        int equalsIndex = s.indexOf("=");
        int braceIndex = s.indexOf("{");
        if (equalsIndex >=0 && ((braceIndex >= 0 && braceIndex>equalsIndex) || 
            (braceIndex < 0 && equalsIndex >=0 ))) { // we have ourselves some init
                                                    // expression
            s = s.substring(equalsIndex, s.length());
            String initExprStr = s.substring(s.indexOf("=")+1, (braceIndex <0)?
                s.length():s.indexOf("{"));
            MExpression initExpr = UmlFactory.getFactory().getDataTypes().createExpression(Notation.getDefaultNotation().toString(), initExprStr);
            attr.setInitialValue(initExpr);
            return s.substring(initExprStr.length(), s.length());
        }
        return s;
  }

  public MParameter parseParameter(String s) {
    java.util.StringTokenizer st = new java.util.StringTokenizer(s, ": = \t");
    String typeStr = "int";
    String paramNameStr = "parameterName?";

    if (st.hasMoreTokens()) paramNameStr = st.nextToken();
    if (st.hasMoreTokens()) typeStr = st.nextToken();
    Project p = ProjectBrowser.TheInstance.getProject();
    MClassifier cls = p.findType(typeStr);
    MParameter param = UmlFactory.getFactory().getCore().buildParameter();
    param.setType(cls);
    param.setKind(MParameterDirectionKind.IN);
    param.setName(paramNameStr);

    return param;
  }


  //   public abstract Package parsePackage(String s);
//   public abstract MClassImpl parseClassifier(String s);

  public MStereotype parseStereotype(String s) {
    return null;
  }

  public MTaggedValue parseTaggedValue(String s) {
    return null;
  }

//   public abstract MAssociation parseAssociation(String s);
//   public abstract MAssociationEnd parseAssociationEnd(String s);

  /** Parse a string of the form: "range, ...", where range is of the
   *  form "lower..upper", or "integer" */
  public MMultiplicity parseMultiplicity(String s) {
	  return UmlFactory.getFactory().getDataTypes().createMultiplicity(s);
 
  }


  public MState parseState(String s) {
    return null;
  }

  public void parseStateBody(MState st, String s) {
      //remove all old transitions; needs-more-work: this should be done better!!
      st.setEntry(null);
      st.setExit(null);

      Collection trans = new ArrayList();
      java.util.StringTokenizer lines = new java.util.StringTokenizer(s, "\n\r");
      while (lines.hasMoreTokens()) {
	  String line = lines.nextToken().trim();
	  if (line.startsWith("entry")) parseStateEntyAction(st, line);
	  else if (line.startsWith("exit")) parseStateExitAction(st, line);
	  else {
	      MTransition t = parseTransition(
	          UmlFactory.getFactory().getStateMachines().createTransition(),
                  line);
 

	      if (t == null) continue;
	      //System.out.println("just parsed:" + GeneratorDisplay.Generate(t));
	      t.setStateMachine(st.getStateMachine());
	      t.setTarget(st);
	      t.setSource(st);
	      trans.add(t);
	  }
      }

      Vector internals = new Vector(st.getInternalTransitions());
      Vector oldinternals = new Vector(st.getInternalTransitions());
      internals.removeAll(oldinternals); //now the vector is empty

      // don't forget to remove old internals!
      for (int i = 0; i < oldinternals.size(); i++)
      ((MTransition)oldinternals.elementAt(i)).remove();
      internals.addAll(trans);
      st.setInternalTransitions(trans);
  }

    public void parseStateEntyAction(MState st, String s) {
    if (s.startsWith("entry") && s.indexOf("/") > -1)
	s = s.substring(s.indexOf("/")+1).trim();
    MCallAction entryAction=(MCallAction)parseAction(s);
    entryAction.setName("anon");
    st.setEntry(entryAction);
  }

  public void parseStateExitAction(MState st, String s) {
    if (s.startsWith("exit") && s.indexOf("/") > -1)
      s = s.substring(s.indexOf("/")+1).trim();
    MCallAction exitAction=(MCallAction)parseAction(s);
    exitAction.setName("anon");
    st.setExit(exitAction);
  }

  /** Parse a line of the form: "name: trigger [guard] / actions" */
  public MTransition parseTransition(MTransition trans, String s) {
    // strip any trailing semi-colons
    s = s.trim();
    if (s.length() == 0) return null;
    if (s.charAt(s.length()-1) == ';')
      s = s.substring(0, s.length() - 2);

    String name = "";
    String trigger = "";
    String guard = "";
    String actions = "";
    if (s.indexOf(":", 0) > -1) {
      name = s.substring(0, s.indexOf(":")).trim();
      s = s.substring(s.indexOf(":") + 1).trim();
    }

    if (s.indexOf("[", 0) > -1 && s.indexOf("]", 0) > -1) {
      guard = s.substring(s.indexOf("[", 0)+1, s.indexOf("]")).trim();
      s = s.substring(0, s.indexOf("[")) + s.substring(s.indexOf("]")+1);
      s = s.trim();
    }

    if (s.indexOf("/", 0) > -1) {
      actions = s.substring(s.indexOf("/")+1).trim();
      s = s.substring(0, s.indexOf("/")).trim();
    }

    trigger = s;

    /*     System.out.println("name=|" + name +"|");
     System.out.println("trigger=|" + trigger +"|");
     System.out.println("guard=|" + guard +"|");
     System.out.println("actions=|" + actions +"|");
    */
    trans.setName(parseName(name));

    if (trigger.length()>0) {
	MEvent evt=parseEvent(trigger);
	if (evt!=null){
	    trans.setTrigger((MCallEvent)evt);
	}
    }
    else
	trans.setTrigger(null);

    if (guard.length()>0){
	MGuard g=parseGuard(guard);
	if (g!=null){
	    g.setName("anon");
	    g.setTransition(trans);
	    trans.setGuard(g);
	}
    }
    else
	trans.setGuard(null);

    if (actions.length()>0){
	MCallAction effect=(MCallAction)parseAction(actions);
	effect.setName("anon");
	trans.setEffect(effect);
    }
    else
	trans.setEffect(null);

    return trans;
  }

  /**
   * Parses a line on the form:
   *
   * <br>baselist := [base] [, base]*
   * <br>classifierRole := [name] [/ role] [: baselist]
   *
   * <p><b>role</b> and <b>baselist</b> can be given in any order.
   *
   * <p>This syntax is compatible with the UML 1.3 specification.
   *
   * @param cls The MClassifierRole to apply any changes to.
   * @param s The String to parse.
   * @throws java.text.ParseException when it detects an error in the
   *	attribute string. See also ParseError.getErrorOffset().
   */
  /* (formerly: "name: base" ) */
  public void parseClassifierRole(MClassifierRole cls, String s)
		throws ParseException {
    String name = null;
    String token;
    String role = null;
    String base = null;
    Vector bases = null;
    boolean hasColon = false;
    boolean hasSlash = false;

    try {
	MyTokenizer st = new MyTokenizer(s, " ,\t,/,:,\\,");

	while (st.hasMoreTokens()) {
	    token = st.nextToken();
	    if (" ".equals(token) || "\t".equals(token)) {
		; // Do nothing
	    } else if ("/".equals(token)) {
		hasSlash = true;
		hasColon = false;

		if (base != null) {
		    if (bases == null)
			bases = new Vector();
		    bases.add(base);
		}
		base = null;
	    } else if (":".equals(token)) {
		hasColon = true;
		hasSlash = false;

		if (bases == null)
		    bases = new Vector();
		if (base != null)
		    bases.add(base);
		base = null;
	    } else if (",".equals(token)) {
		if (base != null) {
		    if (bases == null)
			bases = new Vector();
		    bases.add(base);
		}
		base = null;
	    } else if (hasColon) {
		if (base != null)
		    throw new ParseException("Extra text in Classifier Role",
			st.getTokenIndex());

		base = token;
	    } else if (hasSlash) {
		if (role != null)
		    throw new ParseException("Extra text in Classifier Role",
			st.getTokenIndex());

		role = token;
	    } else {
		if (name != null)
		    throw new ParseException("Extra text in Classifier Role",
			st.getTokenIndex());

		name = token;
	    }
	}
    } catch (NoSuchElementException nsee) {
	throw new ParseException("Unexpected end of attribute", s.length());
    } catch (ParseException pre) {
	// System.out.println(pre);
	throw pre;
    }

    if (base != null) {
	if (bases == null)
	    bases = new Vector();
	bases.add(base);
    }

// Needs-more-work: What to do about object name???
//    if (name != null)
//	;

    if (role != null)
	cls.setName(role.trim());

    if (bases != null) {
	// Remove bases that aren't there anymore
	Collection b = cls.getBases();
	Iterator it = b.iterator();
	MClassifier c;
	MNamespace ns = cls.getNamespace();
	if (ns != null && ns.getNamespace() != null)
	    ns = ns.getNamespace();
	else
	    ns = cls.getModel();

	while (it.hasNext()) {
	    c = (MClassifier) it.next();
	    if (!bases.contains(c.getName()))
		cls.removeBase(c);
	}

	it = bases.iterator();
addBases:
	while (it.hasNext()) {
	    String d = ((String) it.next()).trim();

	    Iterator it2 = b.iterator();
	    while (it2.hasNext()) {
		c = (MClassifier) it2.next();
		if (d.equals(c.getName()))
		    continue addBases;
	    }
	    c = getType(d, ns);
	    if (c.getNamespace() instanceof MCollaboration)
		c.setNamespace(ns);
	    cls.addBase(c);
	}
    }
  }

  /**
   * Parses a message line on the form:
   *
   * <br>intno := integer|name
   * <br>seq := intno ['.' intno]*
   * <br>recurrance := '*'['//'] | '*'['//']'['<i>iteration</i>']' | '['<i>condition</i>']'
   * <br>seqelem := {[intno] ['['recurrance']']}
   * <br>seq2 := seqelem ['.' seqelem]*
   * <br>ret_list := lvalue [',' lvalue]*
   * <br>arg_list := rvalue [',' rvalue]*
   * <br>message := [seq [',' seq]* '/'] seq2 ':' [ret_list :=] name ([arg_list])
   *
   * <p>Which is rather complex, so a few examples:
   * <br>2: display(x, y)
   * <br>1.3.1: p := find(specs)
   * <br>[x < 0] 4: invert(color)
   * <br>A3, B4/ C3.1*: update()
   *
   * <p>This syntax is compatible with the UML 1.3 specification.
   *
   * <p>Actually, only a subset of this syntax is currently supported, and
   * some is not even planned to be supported. The exceptions are intno, which
   * allows a number possibly followed by a sequence of letters in the range
   * 'a' - 'z', seqelem, which does not allow a recurrance, and message, which
   * does allow one recurrance near seq2.
   *
   * @param mes The MMessage to apply any changes to.
   * @param s The String to parse.
   * @throws java.text.ParseException when it detects an error in the
   *	attribute string. See also ParseError.getErrorOffset().
   */
  /* (formerly: name: action ) */
  public void parseMessage(MMessage mes, String s) throws ParseException {
    String fname = null;
    String guard = null;
    String paramExpr = null;
    String token;
    String varname = null;
    Vector predecessors = new Vector();
    Vector seqno = null;
    Vector currentseq = new Vector();
    Vector args = null;
    boolean mustBePre = false;
    boolean mustBeSeq = false;
    boolean parallell = false;
    boolean iterative = false;
    boolean mayDeleteExpr = false;
    boolean refindOperation = false;
    boolean hasPredecessors = false;
    int i;

    currentseq.add(null);
    currentseq.add(null);

    try {
	MyTokenizer st = new MyTokenizer(s, " ,\t,*,[,],.,:,=,/,\\,",
		MyTokenizer.PAREN_EXPR_STRING_SEPARATOR);

	while (st.hasMoreTokens()) {
	    token = st.nextToken();

	    if (" ".equals(token) || "\t".equals(token)) {
		if (currentseq == null) {
		    if (varname != null && fname == null) {
			varname += token;
		    }
		}
	    } else if ("[".equals(token)) {
		if (mustBePre)
		    throw new ParseException("Predecessors cannot be " +
			"qualified", st.getTokenIndex());
		mustBeSeq = true;

		if (guard != null)
		    throw new ParseException("Messages cannot have several " +
			"guard or iteration specifications",
			st.getTokenIndex());

		guard = "";
		while (true) {
		    token = st.nextToken();
		    if ("]".equals(token))
			break;
		    guard += token;
		}
	    } else if ("*".equals(token)) {
		if (mustBePre)
		    throw new ParseException("Predecessors cannot be " +
			"iterated", st.getTokenIndex());
		mustBeSeq = true;

		if (currentseq != null)
		    iterative = true;
	    } else if (".".equals(token)) {
		if (currentseq == null)
		    throw new ParseException("Unexpected dot ('.')",
			st.getTokenIndex());
		if (currentseq.get(currentseq.size()-2) != null ||
		    currentseq.get(currentseq.size()-1) != null) {
		    currentseq.add(null);
		    currentseq.add(null);
		}
	    } else if (":".equals(token)) {
		if (st.hasMoreTokens()) {
		    String t = st.nextToken();
		    if ("=".equals(t)) {
			st.putToken(":=");
			continue;
		    }
		    st.putToken(t);
		}

		if (mustBePre)
		    throw new ParseException("Predecessors must be " +
			"terminated with \'/\' and not with \':\'",
			st.getTokenIndex());

		if (currentseq != null) {
		    if (currentseq.size() > 2 &&
			currentseq.get(currentseq.size()-2) == null &&
			currentseq.get(currentseq.size()-1) == null) {
			currentseq.remove(currentseq.size()-1);
			currentseq.remove(currentseq.size()-1);
		    }

		    seqno = currentseq;
		    currentseq = null;
		    mayDeleteExpr = true;
		}
	    } else if ("/".equals(token)) {
		if (st.hasMoreTokens()) {
		    String t = st.nextToken();
		    if ("/".equals(t)) {
			st.putToken("//");
			continue;
		    }
		    st.putToken(t);
		}

		if (mustBeSeq) {
		    throw new ParseException("The sequence number must be " +
			"terminated with \':\' and not with \'/\'",
			st.getTokenIndex());
		}

		mustBePre = false;
		mustBeSeq = true;

		if (currentseq.size() > 2 &&
		    currentseq.get(currentseq.size()-2) == null &&
		    currentseq.get(currentseq.size()-1) == null) {
		    currentseq.remove(currentseq.size()-1);
		    currentseq.remove(currentseq.size()-1);
		}

		if (currentseq.get(currentseq.size()-2) != null ||
		    currentseq.get(currentseq.size()-1) != null) {

		    predecessors.add(currentseq);

		    currentseq = new Vector();
		    currentseq.add(null);
		    currentseq.add(null);
		}
		hasPredecessors = true;
	    } else if ("//".equals(token)) {
		if (mustBePre)
		    throw new ParseException("Predecessors cannot be " +
			"parallellized", st.getTokenIndex());
		mustBeSeq = true;

		if (currentseq != null)
		    parallell = true;
	    } else if (",".equals(token)) {
		if (currentseq != null) {
		    if (mustBeSeq)
			throw new ParseException("Messages cannot have many " +
			    "sequence numbers", st.getTokenIndex());
		    mustBePre = true;

		    if (currentseq.size() > 2 &&
			currentseq.get(currentseq.size()-2) == null &&
			currentseq.get(currentseq.size()-1) == null) {

			currentseq.remove(currentseq.size()-1);
			currentseq.remove(currentseq.size()-1);
		    }

		    if (currentseq.get(currentseq.size()-2) != null ||
			currentseq.get(currentseq.size()-1) != null) {

			predecessors.add(currentseq);

			currentseq = new Vector();
			currentseq.add(null);
			currentseq.add(null);
		    }
		    hasPredecessors = true;
		} else {
		    if (varname == null && fname != null) {
			varname = fname + token;
			fname = null;
		    } else if (varname != null && fname == null) {
			varname += token;
		    } else {
			throw new ParseException("Unexpected character (,)",
			    st.getTokenIndex());
		    }
		}
	    } else if ("=".equals(token) || ":=".equals(token)) {
		if (currentseq == null) {
		    if (varname == null) {
			varname = fname;
			fname = "";
		    } else {
			fname = "";
		    }
		}
	    } else if (currentseq == null) {
		if (paramExpr == null && token.charAt(0) == '(') {
		    if (token.charAt(token.length()-1) != ')')
			throw new ParseException("Malformed parameters",
			    st.getTokenIndex());
		    if (fname == null || "".equals(fname))
			throw new ParseException("Must be a function name " +
			    "before the parameters", st.getTokenIndex());
		    if (varname == null)
			varname = "";
		    paramExpr = token.substring(1, token.length() - 1);
		} else if (varname != null && fname == null) {
		    varname += token;
		} else if (fname == null || fname.length() == 0) {
		    fname = token;
		} else {
		    throw new ParseException("Unexpected token (" + token +
			")", st.getTokenIndex());
		}
	    } else {
		boolean hasVal = currentseq.get(currentseq.size() - 2) != null;
		boolean hasOrd = currentseq.get(currentseq.size() - 1) != null;
		boolean assigned = false;
		int bp = findMsgOrderBreak(token);

		if (!hasVal && !assigned && bp == token.length()) {
		    try {
			currentseq.set(currentseq.size() - 2,
				new Integer(token));
			assigned = true;
		    } catch (NumberFormatException nfe) {
		    }
		}

		if (!hasOrd && !assigned && bp == 0) {
		    try {
			currentseq.set(currentseq.size() - 1,
				new Integer(parseMsgOrder(token)));
			assigned = true;
		    } catch (NumberFormatException nfe) {
		    }
		}

		if (!hasVal && !hasOrd && !assigned &&
		    bp > 0 && bp < token.length()) {
		    Integer nbr, ord;
		    try {
			nbr = new Integer(token.substring(0, bp));
			ord = new Integer(parseMsgOrder(token.substring(bp)));
			currentseq.set(currentseq.size() - 2,
				nbr);
			currentseq.set(currentseq.size() - 1,
				ord);
			assigned = true;
		    } catch (NumberFormatException nfe) {
		    }
		}

		if (!assigned) {
		    throw new ParseException("Unexpected token (" + token +
			")", st.getTokenIndex());
		}
	    }
	}
    } catch (NoSuchElementException nsee) {
	throw new ParseException("Unexpected end of message", s.length());
    } catch (ParseException pre) {
	// System.out.println(pre);
	throw pre;
    }

    if (paramExpr != null) {
	MyTokenizer st = new MyTokenizer(paramExpr, "\\,",
		_parameterCustomSep);
	args = new Vector();
	while (st.hasMoreTokens()) {
	    token = st.nextToken();

	    if (",".equals(token)) {
		if (args.size() == 0)
		    args.add(null);
		args.add(null);
	    } else {
		if (args.size() == 0) {
		    if (token.trim().length() == 0)
			continue;
		    args.add(null);
		}
		String arg = (String) args.get(args.size()-1);
		if (arg != null)
		    arg = arg + token;
		else
		    arg = token;
		args.set(args.size()-1, arg);
	    }
	}
    } else if (mayDeleteExpr) {
	args = new Vector();
    }

/*
System.out.println("ParseMessage: " + s);
System.out.print("Message: ");
for (i = 0; seqno != null && i+1 < seqno.size(); i+=2) {
	if (i > 0)
		System.out.print(", ");
	System.out.print(seqno.get(i) + " (" + seqno.get(i+1)  + ")");
}
System.out.println();
System.out.println("predecessors: " + predecessors.size());
for (i = 0; i < predecessors.size(); i++) {
	int j;
	Vector v = (Vector) predecessors.get(i);
	System.out.print("    Predecessor: ");
	for (j = 0; v != null && j+1 < v.size(); j+=2) {
		if (j > 0)
			System.out.print(", ");
		System.out.print(v.get(j) + " (" + v.get(j+1)  + ")");
	}
	System.out.println();
}
System.out.println("guard: " + guard + " it: " + iterative + " pl: " + parallell);
System.out.println(varname + " := " + fname + " ( " + paramExpr + " )");
*/

    if (mes.getAction() == null) {
	MCallAction a = UmlFactory.getFactory().getCommonBehavior()
		.createCallAction();
	mes.getInteraction().getContext().addOwnedElement(a);
	mes.setAction(a);
    }

    if (guard != null) {
	guard = "[" + guard.trim() + "]";
	if (iterative) {
	    if (parallell)
		guard = "*//" + guard;
	    else
		guard = "*" + guard;
	}
	MIterationExpression expr = UmlFactory.getFactory().getDataTypes()
		.createIterationExpression(
			Notation.getDefaultNotation().toString(),
			guard);
	mes.getAction().setRecurrence(expr);
    }

    if (fname == null) {
	if (!mayDeleteExpr && mes.getAction().getScript() != null) {
	    String body = mes.getAction().getScript().getBody();

	    int idx = body.indexOf(":=");
	    if (idx >= 0)
		idx++;
	    else
		idx = body.indexOf("=");

	    if (idx >= 0)
		fname = body.substring(idx + 1);
	    else
		fname = body;
	} else
	    fname = "";
    }

    if (varname == null) {
	if (!mayDeleteExpr && mes.getAction().getScript() != null) {
	    String body = mes.getAction().getScript().getBody();
	    int idx = body.indexOf(":=");
	    if (idx < 0)
		idx = body.indexOf("=");

	    if (idx >= 0)
		varname = body.substring(0, idx);
	    else
		varname = "";
	} else
	    varname = "";
    }

    if (fname != null) {
	String expr = fname.trim();
	if (varname != null && varname.length() > 0)
	    expr = varname.trim() + " := " + expr;

	if (mes.getAction().getScript() == null ||
	    !expr.equals(mes.getAction().getScript().getBody())) {
	    MActionExpression e = UmlFactory.getFactory().getDataTypes()
		.createActionExpression(
			Notation.getDefaultNotation().toString(),
			expr.trim());
	    mes.getAction().setScript(e);
	    refindOperation = true;
	}
    }

    if (args != null) {
	Collection c = mes.getAction().getActualArguments();
	Iterator it = c.iterator();
	int pos;
	for (i = 0; i < args.size(); i++) {
	    MArgument arg = (it.hasNext() ? (MArgument)it.next() : null);
	    if (arg == null) {
		arg = UmlFactory.getFactory().getCommonBehavior()
			.createArgument();
		mes.getAction().addActualArgument(arg);
		refindOperation = true;
	    }
	    if (arg.getValue() == null ||
		!args.get(i).equals(arg.getValue().getBody())) {
		String value =
			(args.get(i) != null ? (String)args.get(i) : "");
		MExpression e = UmlFactory.getFactory().getDataTypes()
			.createExpression(
				Notation.getDefaultNotation().toString(),
				value.trim());
	 	arg.setValue(e);
	    }
	}

	while (it.hasNext()) {
	    mes.getAction().removeActualArgument((MArgument)it.next());
	    refindOperation = true;
	}
    }

    if (seqno != null) {
	MMessage root;
	// Find the preceding message, if any, on either end of the
	// association.
	String pname = "";
	String mname = "";
	String gname = GeneratorDisplay.getInstance()
		.generateMessageNumber(mes);
	boolean swapRoles = false;
	int majval = (seqno.get(seqno.size()-2) != null ?
		Math.max(((Integer)seqno.get(seqno.size()-2)).intValue()-1, 0)
		: 0);
	int minval = (seqno.get(seqno.size()-1) != null ?
		Math.max(((Integer)seqno.get(seqno.size()-1)).intValue(), 0)
		: 0);

	for (i = 0; i + 1 < seqno.size(); i += 2) {
	    int bv = (seqno.get(i) != null ?
		Math.max(((Integer)seqno.get(i)).intValue(), 1) : 1);
	    int sv = (seqno.get(i+1) != null ?
		Math.max(((Integer)seqno.get(i+1)).intValue(), 0) : 0);

	    if (i > 0)
		mname += ".";
	    mname += Integer.toString(bv) + (char)('a' + sv);

	    if (i+3 < seqno.size()) {
		if (i > 0)
		    pname += ".";
		pname += Integer.toString(bv) + (char)('a' + sv);
	    }
	}

	root = null;
	if (pname.length() > 0) {
	    root = findMsg(mes.getSender(), pname);
	    if (root == null) {
		root = findMsg(mes.getReceiver(), pname);
		if (root != null) {
		    swapRoles = true;
		}
	    }
	} else if (!hasMsgWithActivator(mes.getSender(), null) && 
		   hasMsgWithActivator(mes.getReceiver(), null))
	    swapRoles = true;

	if (compareMsgNumbers(mname, gname)) {
	    ; // Do nothing
	} else if (isMsgNumberStartOf(gname, mname)) {
	    throw new ParseException("Cannot move a message into the " +
		"subtree rooted at self", 0);
	} else if (mes.getPredecessors().size() > 1 &&
		   mes.getMessages3().size() > 1) {
	    throw new ParseException("Cannot move a message which is " +
		"both start and end of several threads", 0);
	} else if (root == null && pname.length() > 0) {
	    throw new ParseException("Cannot find the activator for the " +
		"message", 0);
	} else if (swapRoles && mes.getMessages4().size() > 0 &&
	    mes.getSender() != mes.getReceiver()) {
	    throw new ParseException("Cannot reverse the direction of a " +
		"message that is an activator", 0);
	} else {
	    // Disconnect the message from the call graph
	    Collection c = mes.getPredecessors();
	    Collection c2 = mes.getMessages3();
	    Iterator it;

	    it = c2.iterator();
	    while (it.hasNext()) {
		mes.removeMessage3((MMessage) it.next());
	    }

	    it = c.iterator();
	    while (it.hasNext()) {
		Iterator it2 = c2.iterator();
		MMessage pre = (MMessage) it.next();
		mes.removePredecessor(pre);
		while (it2.hasNext()) {
		    ((MMessage) it2.next()).addPredecessor(pre);
		}
	    }

	    // Connect the message at a new spot
	    mes.setActivator(root);
	    if (swapRoles) {
		MClassifierRole r = mes.getSender();
		mes.setSender(mes.getReceiver());
		mes.setReceiver(r);
	    }

	    if (root == null) {
		c = filterWithActivator(mes.getSender().getMessages2(), null);
	    } else {
		c = root.getMessages4();
	    }
	    c2 = findCandidateRoots(c, root, mes);
	    it = c2.iterator();
	    // If c2 is empty, then we're done
	    // (or there is a cycle in the message graph, which would be bad)
	    // If c2 has more than one element, then the model is crappy,
	    // but we'll just use one of them anyway
	    if (majval <= 0) {
		while (it.hasNext())
		    mes.addMessage3((MMessage) it.next());
	    } else if (it.hasNext()) {
		MMessage pre = walk((MMessage) it.next(), majval - 1, false);
		MMessage post = successor(pre, minval);
		if (post != null) {
		    post.removePredecessor(pre);
		    post.addPredecessor(mes);
		}
		insertSuccessor(pre, mes, minval);
	    }
	    refindOperation = true;
	}
    }

    if (fname != null && refindOperation) {
	MClassifierRole role = mes.getReceiver();
	Vector ops = getOperation(role.getBases(), fname.trim(),
		mes.getAction().getActualArguments().size());

	// Needs-more-work: Should someone choose one, if there are more
	// than one?
	if (mes.getAction() instanceof MCallAction) {
	    MCallAction a = (MCallAction) mes.getAction();
	    if (ops.size() > 0)
		a.setOperation((MOperation) ops.get(0));
	    else
		a.setOperation(null);
	}
    }

    // Needs-more-work: Predecessors is not implemented, because it
    // causes some problems that I've not found an easy way to handle yet,
    // d00mst. The specific problem is that the notation currently is
    // ambiguous on second message after a thread split.

    // Why not implement it anyway? d00mst

    if (hasPredecessors) {
	Collection roots = findCandidateRoots(mes.getInteraction()
						.getMessages(), null, null);
	Vector pre = new Vector();
	Iterator it;
predfor:
	for (i = 0; i < predecessors.size(); i++) {
	    it = roots.iterator();
	    while (it.hasNext()) {
		MMessage msg = walkTree((MMessage) it.next(),
				(Vector) predecessors.get(i));
		if (msg != null && msg != mes) {
		    if (isBadPreMsg(mes, msg)) {
			throw new ParseException("One predecessor cannot be " +
				"a predecessor to this message", 0);
		    }
		    pre.add(msg);
		    continue predfor;
		}
	    }
	    throw new ParseException("Could not find predecessor", 0);
	}
	GeneratorDisplay.MsgPtr ptr =
		GeneratorDisplay.getInstance().new MsgPtr();
	GeneratorDisplay.getInstance().recCountPredecessors(mes, ptr);
	if (ptr.message != null && !pre.contains(ptr.message))
	    pre.add(ptr.message);
	mes.setPredecessors(pre);
    }
  }

  /**
   * Examines the call tree from chld to see if ans is an ancestor.
   */
  private boolean isBadPreMsg(MMessage ans, MMessage chld) {
    while (chld != null) {
	if (ans == chld)
	    return true;
	if (isPredecessorMsg(ans, chld, 100))
	    return true;
	chld = chld.getActivator();
    }
    return false;
  }

  /**
   * Examines the call tree from suc to see if pre is a predecessor.
   * This function is recursive and md specifies the maximum level of
   * recursions allowed.
   */
  private boolean isPredecessorMsg(MMessage pre, MMessage suc, int md) {
    Iterator it = suc.getPredecessors().iterator();
    while (it.hasNext()) {
	MMessage m = (MMessage) it.next();
	if (m == pre)
	    return true;
	if (md > 0 && isPredecessorMsg(pre, m, md-1))
	    return true;
    }
    return false;
  }

  /**
   * Walks a call tree from a root node following the directions given in path
   * to a destination node. If the destination node cannot be reached, then
   * null is returned.
   *
   * @param root The root of the call tree.
   * @param path The path to walk in the call tree.
   * @return The message at the end of path, or null.
   */
  private MMessage walkTree(MMessage root, Vector path) {
    int i;
    for (i = 0; i + 1 < path.size(); i+= 2) {
	int bv = (path.get(i) != null ?
		Math.max(((Integer)path.get(i)).intValue()-1, 0) : 0);
	int sv = (path.get(i+1) != null ?
		Math.max(((Integer)path.get(i+1)).intValue(), 0) : 0);
	root = walk(root, bv-1, true);
	if (root == null) {
	    return null;
	}
	if (bv > 0) {
	    root = successor(root, sv);
	    if (root == null) {
		return null;
	    }
	}
	if (i + 3 < path.size()) {
	    Iterator it = findCandidateRoots(root.getMessages4(), root, null)
			.iterator();
	    // Things are strange if there are more than one candidate root,
	    // it has no obvious interpretation in terms of a call tree.
	    if (!it.hasNext())
		return null;
	    root = (MMessage) it.next();
	}
    }
    return root;
  }

  /**
   * Examines a collection to see if any message has the given message
   * as an activator.
   */
  private boolean hasMsgWithActivator(MClassifierRole r, MMessage m) {
    Iterator it = r.getMessages2().iterator();
    while (it.hasNext())
	if (((MMessage) it.next()).getActivator() == m)
	    return true;
    return false;
  }

  /**
   * Inserts message s as the p'th successor of message m.
   */
  private void insertSuccessor(MMessage m, MMessage s, int p) {
    Vector v = new Vector(m.getMessages3());
    if (v.size() > p)
	v.insertElementAt(s, p);
    else
	v.add(s);
    m.setMessages3(v);
  }

  /**
   * Finds the steps'th successor of message r in the sense that it is a
   * direct successor of r. Returns null if r has fewer successors.
   */
  private MMessage successor(MMessage r, int steps) {
    Iterator it = r.getMessages3().iterator();
    while (it.hasNext() && steps > 0) {
	it.next();
	steps--;
    }
    if (it.hasNext())
	return (MMessage) it.next();
    return null;
  }

  /**
   * Finds the steps'th successor of r in the sense that it is a successor
   * of a successor of r (steps times). The first successor with the same
   * activator as r is used in each step. If there are not enough successors,
   * then struct determines the result. If struct is true, then null is
   * returned, otherwise the last successor found.
   */
  private MMessage walk(MMessage r, int steps, boolean strict) {
    MMessage act = r.getActivator();
    while (steps > 0) {
	Iterator it = r.getMessages3().iterator();
	do {
	    if (!it.hasNext())
		return (strict ? null : r);
	    r = (MMessage) it.next();
	} while (r.getActivator() != act);
	steps--;
    }
    return r;
  }

  /**
   * Finds the root candidates in a collection c, ie the messages in c that
   * has the activator a (may be null) and has no predecessor with the same
   * activator. If veto isn't null, then the message in veto will not be
   * included in the Collection of candidates.
   */
  private Collection findCandidateRoots(Collection c, MMessage a,
		MMessage veto) {
    Iterator it = c.iterator();
    Vector v = new Vector();
    while (it.hasNext()) {
	MMessage m = (MMessage) it.next();
	if (m == veto)
	    continue;
	if (m.getActivator() != a)
	    continue;
	Iterator it2 = m.getPredecessors().iterator();
	boolean candidate = true;
	while (it2.hasNext()) {
	    MMessage m2 = (MMessage) it2.next();
	    if (m2.getActivator() == a)
		candidate = false;
	}
	if (candidate)
	    v.add(m);
    }
    return v;
  }

  /**
   * Finds the messages in Collection c that has message a as activator.
   */
  private Collection filterWithActivator(Collection c, MMessage a) {
    Iterator it = c.iterator();
    Vector v = new Vector();
    while (it.hasNext()) {
	MMessage m = (MMessage) it.next();
	if (m.getActivator() == a)
	    v.add(m);
    }
    return v;
  }

  /**
   * Finds the message in ClassifierRole r that has the message number
   * written in n. If it isn't found, null is returned.
   */
  private MMessage findMsg(MClassifierRole r, String n) {
    Collection c = r.getMessages1();
    Iterator it = c.iterator();
    while (it.hasNext()) {
	MMessage msg = (MMessage) it.next();
	String gname = GeneratorDisplay.getInstance().generateMessageNumber(
		msg);
	if (compareMsgNumbers(gname, n))
	    return msg;
    }
    return null;
  }

  /**
   * Compares two message numbers with each other to see if they are equal,
   * in the sense that they refer to the same position in a call tree.
   */
  private boolean compareMsgNumbers(String n1, String n2) {
    return isMsgNumberStartOf(n1, n2) && isMsgNumberStartOf(n2, n1);
  }

  /**
   * Compares two message numbers n1, n2 with each other to determine if n1
   * specifies a the same position as n2 in a call tree or n1 specifies a
   * position that is a father of the position specified by n2.
   */
  private boolean isMsgNumberStartOf(String n1, String n2) {
    int i, j, len, jlen;
    len = n1.length();
    jlen = n2.length();
    i = j = 0;
    for (;i < len;) {
	int ibv, isv;
	int jbv, jsv;

	ibv = 0;
	for (; i < len; i++) {
	    char c = n1.charAt(i);
	    if (c < '0' || c > '9')
		break;
	    ibv *= 10;
	    ibv += c - '0';
	}
	isv = 0;
	for (; i < len; i++) {
	    char c = n1.charAt(i);
	    if (c < 'a' || c > 'z')
		break;
	    isv *= 26;
	    isv += c - 'a';
	}

	jbv = 0;
	for (; j < jlen; j++) {
	    char c = n2.charAt(j);
	    if (c < '0' || c > '9')
		break;
	    jbv *= 10;
	    jbv += c - '0';
	}
	jsv = 0;
	for (; j < jlen; j++) {
	    char c = n2.charAt(j);
	    if (c < 'a' || c > 'z')
		break;
	    jsv *= 26;
	    jsv += c - 'a';
	}

	if (ibv != jbv || isv != jsv) {
	    return false;
	}

	if (i < len && n1.charAt(i) != '.') {
	    return false;
	} else
	    i++;

	if (j < jlen && n2.charAt(j) != '.') {
	    return false;
	} else
	    j++;
    }
    return true;
  }

  /**
   * Finds the operations in Collection c with name name and params number
   * of parameters. If no operation is found, one is created. The applicable
   * operations are returned.
   */
  private Vector getOperation(Collection c, String name, int params) {
    Vector options = new Vector();
    Iterator it;

    if (name == null || name.length() == 0)
	return options;

    it = c.iterator();
    while (it.hasNext()) {
	MClassifier clf = (MClassifier) it.next();
	Collection oe = clf.getFeatures();
	Iterator it2 = oe.iterator();
	while (it2.hasNext()) {
	    MModelElement me = (MModelElement) it2.next();
	    if (!(me instanceof MOperation))
		continue;

	    MOperation op = (MOperation) me;
	    if (!name.equals(op.getName()))
		continue;
	    if (params != countParameters(op))
		continue;
	    options.add(op);
	}
    }
    if (options.size() > 0)
	return options;

    it = c.iterator();
    if (it.hasNext()) {
	String expr = name + "(";
	int i;
	for (i = 0; i < params; i++) {
	    if (i > 0)
		expr += ", ";
	    expr += "param" + (i+1);
	}
	expr += ")";
	MOperation op = UmlFactory.getFactory().getCore().buildOperation(
		(MClassifier) it.next());
	try {
	    parseOperation(expr, op);
	} catch (ParseException pe) {
	    System.out.println("Unexpected ParseException in getOperation: " + pe);
	}
	options.add(op);
    }
    return options;
  }

  /**
   * Counts the number of parameters that are not return values.
   */
  private int countParameters(MBehavioralFeature bf) {
    Collection c = bf.getParameters();
    Iterator it = c.iterator();
    int count = 0;

    while (it.hasNext()) {
	MParameter p = (MParameter) it.next();
	if (MParameterDirectionKind.RETURN.equals(p.getKind()))
	    continue;
	count++;
    }

    return count;
  }

  /**
   * Parses a message order specification.
   */
  private static int parseMsgOrder(String s) {
    int i, t;
    int v = 0;

    t = s.length();
    for (i = 0; i < t; i++) {
	char c = s.charAt(i);
	if (c < 'a' || c > 'z')
	    throw new NumberFormatException();
	v *= 26;
	v += c - 'a';
    }

    return v;
  }

  /**
   * Finds the break between message number and (possibly) message order.
   */
  private static int findMsgOrderBreak(String s) {
    int i, t;
    int v = 0;

    t = s.length();
    for (i = 0; i < t; i++) {
	char c = s.charAt(i);
	if (c < '0' || c > '9')
	    break;
    }
    return i;
  }

  /** Parse a line of the form: "name: action" */
  public void parseStimulus(MStimulus sti, String s) {
    // strip any trailing semi-colons
    s = s.trim();
    if (s.length() == 0) return;
    if (s.charAt(s.length()-1) == ';')
      s = s.substring(0, s.length() - 2);

    //cut trailing string "new Action"
    s = s.trim();
    if (s.length() == 0) return;
    if (s.endsWith("new Action"))
      s = s.substring(0, s.length() - 10);

    String name = "";
    String action = "";
    String actionfirst = "";
    if (s.indexOf(":", 0) > -1) {
      name = s.substring(0, s.indexOf(":")).trim();
      actionfirst = s.substring(s.indexOf(":") + 1).trim();
      if (actionfirst.indexOf(":", 0) > 1) {
        action = actionfirst.substring(0, actionfirst.indexOf(":")).trim();
      }
      else action = actionfirst;
    }
    else name = s;

     MAction act = (MAction) sti.getDispatchAction();
     act.setName(action);
     sti.setName(name);
  }

  public MAction parseAction(String s) {
	  MCallAction a = UmlFactory.getFactory().getCommonBehavior().createCallAction();
 
	  a.setScript(UmlFactory.getFactory().getDataTypes().createActionExpression("Java",s));
 
	  return a;
  }

    /*  public MActionSequence parseActions(String s) {
    MActionSequence as = UmlFactory.getFactory().getCommonBehavior().createActionSequence(s);
 
    as.setName(s);
    return as;
    }*/

  public MGuard parseGuard(String s) {
	MGuard g = UmlFactory.getFactory().getStateMachines().createGuard();
	g.setExpression(UmlFactory.getFactory().getDataTypes().createBooleanExpression("Java",s));
 
        return g;
  }

  public MEvent parseEvent(String s) {
	MCallEvent ce = UmlFactory.getFactory().getStateMachines().createCallEvent();
 
	ce.setName(s);
	ce.setNamespace(ProjectBrowser.TheInstance.getProject().getModel());
        return ce;
  }

  /** Parse a line of the form: "name: base-class" */
  public void parseObject(MObject obj, String s) {
    // strip any trailing semi-colons
    s = s.trim();

    if (s.length() == 0) return;
    if (s.charAt(s.length()-1) == ';')
      s = s.substring(0, s.length() - 2);

    String name = "";
    String basefirst = "";
    String bases = "";
    StringTokenizer baseTokens = null;

    if (s.indexOf(":", 0) > -1) {
      name = s.substring(0, s.indexOf(":",0)).trim();
      bases = s.substring(s.indexOf(":",0) + 1).trim();
      baseTokens = new StringTokenizer(bases,",");
    }
    else {
      name = s;
    }

    obj.setName(name);

    obj.setClassifiers(new Vector());
    if (baseTokens != null) {
      while(baseTokens.hasMoreElements()){
  	String typeString = baseTokens.nextToken();
	MClassifier type = ProjectBrowser.TheInstance.getProject().findType(typeString);
	obj.addClassifier(type);
      }
    }
  }

  /** Parse a line of the form: "name : base-node" */
  public void parseNodeInstance(MNodeInstance noi, String s) {
    // strip any trailing semi-colons
    s = s.trim();
    if (s.length() == 0) return;
    if (s.charAt(s.length()-1) == ';')
      s = s.substring(0, s.length() - 2);



    String name = "";
    String bases = "";
    StringTokenizer tokenizer = null;

    if (s.indexOf(":", 0) > -1) {
      name = s.substring(0, s.indexOf(":")).trim();
      bases = s.substring(s.indexOf(":") + 1).trim();
    }
    else {
      name = s;
    }

    tokenizer = new StringTokenizer(bases,",");

    Vector v = new Vector();
    MNamespace ns = noi.getNamespace();
    if (ns !=null) {
	while (tokenizer.hasMoreElements()) {
	    String newBase = tokenizer.nextToken();
	    MClassifier cls = (MClassifier)ns.lookup(newBase.trim());
	    if (cls != null)
		v.add(cls);
	}
    }

    noi.setClassifiers(v);
    noi.setName(new String(name));

  }

  /** Parse a line of the form: "name : base-component" */
  public void parseComponentInstance(MComponentInstance coi, String s) {
    // strip any trailing semi-colons
    s = s.trim();
    if (s.length() == 0) return;
    if (s.charAt(s.length()-1) == ';')
      s = s.substring(0, s.length() - 2);

    String name = "";
    String bases = "";
    StringTokenizer tokenizer = null;

    if (s.indexOf(":", 0) > -1) {
      name = s.substring(0, s.indexOf(":")).trim();
      bases = s.substring(s.indexOf(":") + 1).trim();
    }
    else {
      name = s;
    }

    tokenizer = new StringTokenizer(bases,",");

    Vector v = new Vector();
    MNamespace ns = coi.getNamespace();
    if (ns !=null) {
	while (tokenizer.hasMoreElements()) {
	    String newBase = tokenizer.nextToken();
	    MClassifier cls = (MClassifier)ns.lookup(newBase.trim());
	    if (cls != null)
		v.add(cls);
	}
    }

    coi.setClassifiers(v);
    coi.setName(new String(name));

  }

  private static String beforeAnyOf(String base, String chars) {
    int i, idx, min;

    if (base == null)
	return null;
    min = base.length();
    for (i = 0; i < chars.length(); i++) {
	idx = base.indexOf(chars.charAt(i));
	if (idx >= 0 && idx < min)
	   min = idx;
    }
    return base.substring(0, min);
  }

} /* end class ParserDisplay */
