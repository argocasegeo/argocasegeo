// Copyright (c) 1996-99 The Regents of the University of California. All
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



// File: CrClassMustBeAbstract.java
// Classes: CrClassMustBeAbstract
// Original Author: jrobbins@ics.uci.edu
// $Id: CrClassMustBeAbstract.java,v 1.3 2002/08/19 08:18:16 kataka Exp $

package org.argouml.uml.cognitive.critics;

import java.util.*;

import ru.novosoft.uml.foundation.core.*;

import org.argouml.cognitive.*;
import org.argouml.cognitive.critics.*;
import org.argouml.model.uml.UmlHelper;
import org.argouml.uml.*;

/** Well-formedness rules [1] and [3] for Class. See page 29 of UML 1.1
 *  Semantics. OMG document ad/97-08-04. */

public class CrClassMustBeAbstract extends CrUML {

  public CrClassMustBeAbstract() {
    setHeadline("Class Must be Abstract");

    addSupportedDecision(CrUML.decINHERITANCE);
    addSupportedDecision(CrUML.decMETHODS);
    setKnowledgeTypes(Critic.KT_SEMANTICS);
  }

  public boolean predicate2(Object dm, Designer dsgr) {
    if (!(dm instanceof MClass)) return NO_PROBLEM;
    MClass cls = (MClass) dm;
    if (!cls.isAbstract()) return NO_PROBLEM;
    //    Collection beh = getInheritedBehavioralFeatures(cls);
    Collection beh = UmlHelper.getHelper().getCore().getOperationsInh(cls);
    Iterator enumeracao = beh.iterator();
    while (enumeracao.hasNext()) {
      MBehavioralFeature bf = (MBehavioralFeature) enumeracao.next();
      //needs-more-work: abstract methods are not part of UML, only java
      //if (bf.getIsAbstract()) return PROBLEM_FOUND;
    }
    return NO_PROBLEM;
  }

} /* end class CrClassMustBeAbstract.java */

