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

import edu.buaa.satla.analysis.cfa.ast.c.CComplexTypeDeclaration;
import edu.buaa.satla.analysis.cfa.ast.c.CFunctionDeclaration;
import edu.buaa.satla.analysis.cfa.ast.c.CSimpleDeclaration;
import edu.buaa.satla.analysis.cfa.parser.Scope;
import edu.buaa.satla.analysis.cfa.types.c.CComplexType;
import edu.buaa.satla.analysis.cfa.types.c.CType;
import edu.buaa.satla.analysis.core.automaton.AutomatonParser;

/**
 * For all languages, where parsing of single or blocks of statements is not yet implemented,
 * use this dummy scope when parsing an automaton {@link AutomatonParser}.
 */
public class DummyScope implements Scope {

  private static final DummyScope DUMMYSCOPE = new DummyScope();

  private DummyScope() {} // Private constructor to insure one instance.

  public static DummyScope getInstance() {
    return DUMMYSCOPE;
  }

  @Override
  public boolean isGlobalScope() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean variableNameInUse(String pName, String pOrigName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CSimpleDeclaration lookupVariable(String pName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CFunctionDeclaration lookupFunction(String pName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CComplexType lookupType(String pName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public CType lookupTypedef(String pName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void registerDeclaration(CSimpleDeclaration pDeclaration) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean registerTypeDeclaration(CComplexTypeDeclaration pDeclaration) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String createScopedNameOf(String pName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getRenamedTypeName(String pType) {
    throw new UnsupportedOperationException();
  }

}
