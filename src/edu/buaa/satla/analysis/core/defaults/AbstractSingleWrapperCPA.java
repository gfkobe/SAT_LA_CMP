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
package edu.buaa.satla.analysis.core.defaults;

import java.util.Collection;

import org.sosy_lab.cpachecker.cfa.model.CFANode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import edu.buaa.satla.analysis.core.interfaces.ConfigurableProgramAnalysis;
import edu.buaa.satla.analysis.core.interfaces.Precision;
import edu.buaa.satla.analysis.core.interfaces.Statistics;
import edu.buaa.satla.analysis.core.interfaces.StatisticsProvider;
import edu.buaa.satla.analysis.core.interfaces.WrapperCPA;

/**
 * Base class for CPAs which wrap exactly one other CPA.
 */
public abstract class AbstractSingleWrapperCPA implements ConfigurableProgramAnalysis, WrapperCPA, StatisticsProvider {

  private final ConfigurableProgramAnalysis wrappedCpa;

  public AbstractSingleWrapperCPA(ConfigurableProgramAnalysis pCpa) {
    Preconditions.checkNotNull(pCpa);

    wrappedCpa = pCpa;
  }

  protected ConfigurableProgramAnalysis getWrappedCpa() {
    return wrappedCpa;
  }

  @Override
  public Precision getInitialPrecision(CFANode pNode) {
    return wrappedCpa.getInitialPrecision(pNode);
  }

  @Override
  public void collectStatistics(Collection<Statistics> pStatsCollection) {
    if (wrappedCpa instanceof StatisticsProvider) {
      ((StatisticsProvider)wrappedCpa).collectStatistics(pStatsCollection);
    }
  }

  @Override
  public <T extends ConfigurableProgramAnalysis> T retrieveWrappedCpa(Class<T> pType) {
    if (pType.isAssignableFrom(getClass())) {
      return pType.cast(this);
    } else if (pType.isAssignableFrom(wrappedCpa.getClass())) {
      return pType.cast(wrappedCpa);
    } else if (wrappedCpa instanceof WrapperCPA) {
      return ((WrapperCPA)wrappedCpa).retrieveWrappedCpa(pType);
    } else {
      return null;
    }
  }

  @Override
  public ImmutableList<ConfigurableProgramAnalysis> getWrappedCPAs() {
    return ImmutableList.of(wrappedCpa);
  }
}