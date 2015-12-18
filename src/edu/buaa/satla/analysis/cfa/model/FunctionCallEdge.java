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
package edu.buaa.satla.analysis.cfa.model;

import java.util.List;

import com.google.common.base.Optional;

import edu.buaa.satla.analysis.cfa.ast.AFunctionCall;
import edu.buaa.satla.analysis.cfa.ast.FileLocation;
import edu.buaa.satla.analysis.cfa.ast.IAExpression;


public class FunctionCallEdge extends AbstractCFAEdge {

  protected final AFunctionCall functionCall;
  protected final FunctionSummaryEdge summaryEdge;


  protected FunctionCallEdge(String pRawStatement, FileLocation pFileLocation, CFANode pPredecessor, CFANode pSuccessor,
      AFunctionCall pFunctionCall, FunctionSummaryEdge pSummaryEdge) {
    super(pRawStatement, pFileLocation, pPredecessor, pSuccessor);
    functionCall = pFunctionCall;
    summaryEdge = pSummaryEdge;
  }

  @Override
  public CFAEdgeType getEdgeType() {
    return CFAEdgeType.FunctionCallEdge;
  }

  public FunctionSummaryEdge getSummaryEdge() {
    return  summaryEdge;
  }



  public List<? extends IAExpression> getArguments() {
    return functionCall.getFunctionCallExpression().getParameterExpressions();
  }

  @Override
  public String getCode() {
    return functionCall.getFunctionCallExpression().toASTString();
  }

  @Override
  public Optional<? extends AFunctionCall> getRawAST() {
    return Optional.of(functionCall);
  }

  @Override
  public FunctionEntryNode getSuccessor() {
    // the constructor enforces that the successor is always a FunctionEntryNode
    return (FunctionEntryNode)super.getSuccessor();
  }
}