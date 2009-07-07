/*******************************************************************************
 * Copyright (c) 2009 Mountainminds GmbH & Co. KG and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 * $Id: $
 *******************************************************************************/
package org.jacoco.core.analysis;

import static org.jacoco.core.analysis.ICoverageNode.CounterEntity.BLOCK;
import static org.jacoco.core.analysis.ICoverageNode.CounterEntity.CLASS;
import static org.jacoco.core.analysis.ICoverageNode.CounterEntity.INSTRUCTION;
import static org.jacoco.core.analysis.ICoverageNode.CounterEntity.LINE;
import static org.jacoco.core.analysis.ICoverageNode.CounterEntity.METHOD;
import static org.jacoco.core.analysis.ICoverageNode.ElementType.CUSTOM;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.junit.Test;

/**
 * Unit tests for {@link CoverageNodeImpl}.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class CoverageNodeImplTest {

	@Test
	public void testProperties() {
		ICoverageNode node = new CoverageNodeImpl(CUSTOM, "sample", false);
		assertEquals(CUSTOM, node.getElementType());
		assertEquals("sample", node.getName());
	}

	@Test
	public void testInitWithoutLines() {
		ICoverageNode node = new CoverageNodeImpl(CUSTOM, "sample", false);
		assertEquals(CounterImpl.COUNTER_0_0, node.getBlockCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getInstructionCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getLineCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getMethodCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getClassCounter());
		assertNull(node.getLines());
	}

	@Test
	public void testInitWithLines() {
		ICoverageNode node = new CoverageNodeImpl(CUSTOM, "sample", true);
		assertEquals(CounterImpl.COUNTER_0_0, node.getBlockCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getInstructionCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getLineCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getMethodCounter());
		assertEquals(CounterImpl.COUNTER_0_0, node.getClassCounter());
		assertNotNull(node.getLines());
	}

	@Test
	public void testIncrementWithoutLines() {
		CoverageNodeImpl parent = new CoverageNodeImpl(CUSTOM, "sample", false);
		ICoverageNode child = new CoverageNodeImpl(CUSTOM, "sample", false) {
			{
				instructionCounter = CounterImpl.getInstance(42, 41);
				blockCounter = CounterImpl.getInstance(32, 31);
				lineCounter = CounterImpl.getInstance(8, 3);
				methodCounter = CounterImpl.getInstance(22, 21);
				classCounter = CounterImpl.getInstance(12, 11);
			}
		};
		parent.increment(child);
		assertEquals(CounterImpl.getInstance(42, 41), parent
				.getCounter(INSTRUCTION));
		assertEquals(CounterImpl.getInstance(42, 41), parent
				.getInstructionCounter());
		assertEquals(CounterImpl.getInstance(32, 31), parent.getCounter(BLOCK));
		assertEquals(CounterImpl.getInstance(32, 31), parent.getBlockCounter());
		assertEquals(CounterImpl.getInstance(8, 3), parent.getCounter(LINE));
		assertEquals(CounterImpl.getInstance(8, 3), parent.getLineCounter());
		assertEquals(CounterImpl.getInstance(22, 21), parent.getCounter(METHOD));
		assertEquals(CounterImpl.getInstance(22, 21), parent.getMethodCounter());
		assertEquals(CounterImpl.getInstance(12, 11), parent.getCounter(CLASS));
		assertEquals(CounterImpl.getInstance(12, 11), parent.getClassCounter());
	}

	@Test
	public void testIncrementWithLines() {
		CoverageNodeImpl node = new CoverageNodeImpl(ElementType.CUSTOM,
				"sample", true);
		ICoverageNode child = new CoverageNodeImpl(ElementType.CUSTOM,
				"sample", true) {
			{
				instructionCounter = CounterImpl.getInstance(42, 41);
				blockCounter = CounterImpl.getInstance(32, 31);
				lines.increment(new int[] { 1, 2 }, false);
				lines.increment(new int[] { 3, 4 }, true);
				methodCounter = CounterImpl.getInstance(22, 21);
				classCounter = CounterImpl.getInstance(12, 11);
			}
		};
		node.increment(child);
		assertEquals(CounterImpl.getInstance(42, 41), node
				.getInstructionCounter());
		assertEquals(CounterImpl.getInstance(32, 31), node.getBlockCounter());
		assertEquals(CounterImpl.getInstance(22, 21), node.getMethodCounter());
		assertEquals(CounterImpl.getInstance(12, 11), node.getClassCounter());
		assertEquals(CounterImpl.getInstance(4, 2), node.getLineCounter());
		assertEquals(ILines.NOT_COVERED, node.getLines().getStatus(1));
		assertEquals(ILines.NOT_COVERED, node.getLines().getStatus(2));
		assertEquals(ILines.FULLY_COVERED, node.getLines().getStatus(3));
		assertEquals(ILines.FULLY_COVERED, node.getLines().getStatus(4));
	}

	@Test
	public void testIncrementCollection() {
		CoverageNodeImpl parent = new CoverageNodeImpl(CUSTOM, "sample", false);
		ICoverageNode child1 = new CoverageNodeImpl(CUSTOM, "sample", false) {
			{
				blockCounter = CounterImpl.getInstance(5, 2);
			}
		};
		ICoverageNode child2 = new CoverageNodeImpl(CUSTOM, "sample", false) {
			{
				blockCounter = CounterImpl.getInstance(3, 3);
			}
		};
		parent.increment(Arrays.asList(child1, child2));
		assertEquals(CounterImpl.getInstance(8, 5), parent.getBlockCounter());
	}

}