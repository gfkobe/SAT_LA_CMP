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

import java.util.List;
import java.util.SortedMap;

import org.sosy_lab.common.Pair;

import com.google.common.collect.SortedSetMultimap;

import edu.buaa.satla.analysis.cfa.ast.IADeclaration;
import edu.buaa.satla.analysis.cfa.model.CFANode;
import edu.buaa.satla.analysis.cfa.model.FunctionEntryNode;

/**
 * Class representing the result of parsing a C file before function calls
 * are bound to their targets.
 *
 * It consists of a map that stores the CFAs for each function and a list of
 * declarations of global variables.
 *
 * This class is immutable, but it does not ensure that it's content also is.
 * It is recommended to use it only as a "transport" data class, not for
 * permanent storage.
 */
public class ParseResult {

  private final SortedMap<String, FunctionEntryNode> functions;

  private final SortedSetMultimap<String, CFANode> cfaNodes;

  private final List<Pair<IADeclaration, String>> globalDeclarations;

  private final Language language;

  public ParseResult(SortedMap<String, FunctionEntryNode> pFunctions,
      SortedSetMultimap<String, CFANode> pCfaNodes,
      List<Pair<IADeclaration, String>> pGlobalDeclarations,
      Language pLanguage) {
    functions = pFunctions;
    cfaNodes = pCfaNodes;
    globalDeclarations = pGlobalDeclarations;
    language = pLanguage;

  }

  public boolean isEmpty() {
    return functions.isEmpty();
  }

  public SortedMap<String, FunctionEntryNode> getFunctions() {
    return functions;
  }

  public SortedSetMultimap<String, CFANode> getCFANodes() {
    return cfaNodes;
  }

  public List<Pair<IADeclaration, String>> getGlobalDeclarations() {
    return globalDeclarations;
  }

  public Language getLanguage() {
    return language;
  }
}
