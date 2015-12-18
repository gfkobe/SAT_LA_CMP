/*
 *  CPAchecker is a tool for configurable software verification.
 *  This file is part of CPAchecker.
 *
 *  Copyright (C) 2007-2014  Dirk Beyer
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 *  CPAchecker web page:
 *    http://cpachecker.sosy-lab.org
 */
package edu.buaa.satla.analysis.cfa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.buaa.satla.analysis.cfa.ast.AFunctionCall;
import edu.buaa.satla.analysis.cfa.model.AStatementEdge;
import edu.buaa.satla.analysis.cfa.model.CFAEdge;
import edu.buaa.satla.analysis.util.CFATraversal;

/** This Visitor collects all functioncalls.
 *  It should visit the CFA of each functions BEFORE creating super-edges (functioncall- and return-edges). */
public class FunctionCallCollector extends CFATraversal.DefaultCFAVisitor {
  // TODO this class is copied from CFASecondPassBuilder, can we merge this class with the other visitor?
  // TODO in FunctionCallDumper there exists a similiar class, should we merge?

  private final List<AStatementEdge> functionCalls = new ArrayList<>();

  public Collection<AStatementEdge> getFunctionCalls() {
    return Collections.unmodifiableCollection(functionCalls);
  }

  @Override
  public CFATraversal.TraversalProcess visitEdge(final CFAEdge pEdge) {
    switch (pEdge.getEdgeType()) {
      case StatementEdge: {
        final AStatementEdge edge = (AStatementEdge) pEdge;
        if (edge.getStatement() instanceof AFunctionCall) {
          functionCalls.add(edge);
        }
        break;
      }

      case FunctionCallEdge:
      case FunctionReturnEdge:
      case CallToReturnEdge:
        throw new AssertionError("functioncall- and return-edges should not exist at this time.");
    }
    return CFATraversal.TraversalProcess.CONTINUE;
  }
}
