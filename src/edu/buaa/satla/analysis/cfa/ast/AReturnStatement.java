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
package edu.buaa.satla.analysis.cfa.ast;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;

import com.google.common.base.Optional;

public abstract class AReturnStatement extends AstNode implements IAReturnStatement {

  private final Optional<? extends IAExpression> expression;

  public AReturnStatement(final FileLocation pFileLocation,
      final Optional<? extends IAExpression> pExpression) {
    super(pFileLocation);
    expression = checkNotNull(pExpression);
  }

  @Override
  public String toASTString() {
    return "return"
        + (expression.isPresent() ? " " + expression.get().toASTString() : "")
        + ";";
  }

  @Override
  public Optional<? extends IAExpression> getReturnValue() {
    return expression;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 7;
    result = prime * result + Objects.hashCode(expression);
    result = prime * result + super.hashCode();
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof AReturnStatement)
        || !super.equals(obj)) {
      return false;
    }

    AReturnStatement other = (AReturnStatement) obj;

    return Objects.equals(other.expression, expression);
  }

}