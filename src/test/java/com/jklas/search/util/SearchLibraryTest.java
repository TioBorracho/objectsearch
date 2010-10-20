/**
 * Object Search Framework
 *
 * Copyright (C) 2010 Julian Klas
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com.jklas.search.util;

import org.junit.Assert;
import org.junit.Test;

public class SearchLibraryTest {

	@Test
	public void testRound() {
		Assert.assertEquals(new Double(1d) , SearchLibrary.trunc(1.234567890,0) );
		Assert.assertEquals(new Double(1.2d) , SearchLibrary.trunc(1.234567890,1) );
		Assert.assertEquals(new Double(1.23d) , SearchLibrary.trunc(1.234567890,2) );
		Assert.assertEquals(new Double(1.234d) , SearchLibrary.trunc(1.234567890,3) );
		Assert.assertEquals(new Double(1.2345d) , SearchLibrary.trunc(1.234567890,4) );
		Assert.assertEquals(new Double(1.23456d) , SearchLibrary.trunc(1.234567890,5) );
		Assert.assertEquals(new Double(1.234567d) , SearchLibrary.trunc(1.234567890,6) );
		Assert.assertEquals(new Double(1.2345678d) , SearchLibrary.trunc(1.234567890,7) );
		
		Assert.assertEquals(new Double(5d) , SearchLibrary.trunc(5.5555555555,0) );
		Assert.assertEquals(new Double(5.5d) , SearchLibrary.trunc(5.5555555555,1) );
		Assert.assertEquals(new Double(5.55d) , SearchLibrary.trunc(5.5555555555,2) );
		Assert.assertEquals(new Double(5.555d) , SearchLibrary.trunc(5.5555555555,3) );
		Assert.assertEquals(new Double(5.5555d) , SearchLibrary.trunc(5.5555555555,4) );
		Assert.assertEquals(new Double(5.55555d) , SearchLibrary.trunc(5.5555555555,5) );
		Assert.assertEquals(new Double(5.555555d) , SearchLibrary.trunc(5.5555555555,6) );
		Assert.assertEquals(new Double(5.5555555d) , SearchLibrary.trunc(5.5555555555,7) );
		
		Assert.assertEquals(new Double(1d) , SearchLibrary.trunc(1.1111111111,0) );
		Assert.assertEquals(new Double(1.1d) , SearchLibrary.trunc(1.1111111111,1) );
		Assert.assertEquals(new Double(1.11d) , SearchLibrary.trunc(1.1111111111,2) );
		Assert.assertEquals(new Double(1.111d) , SearchLibrary.trunc(1.1111111111,3) );
		Assert.assertEquals(new Double(1.1111d) , SearchLibrary.trunc(1.1111111111,4) );
		Assert.assertEquals(new Double(1.11111d) , SearchLibrary.trunc(1.1111111111,5) );
		Assert.assertEquals(new Double(1.111111d) , SearchLibrary.trunc(1.1111111111,6) );
		Assert.assertEquals(new Double(1.1111111d) , SearchLibrary.trunc(1.1111111111,7) );
		
		Assert.assertEquals(new Double(0d) , SearchLibrary.trunc(0.1111111111,0) );
		Assert.assertEquals(new Double(0.1d) , SearchLibrary.trunc(0.1111111111,1) );
		Assert.assertEquals(new Double(0.11d) , SearchLibrary.trunc(0.1111111111,2) );
		Assert.assertEquals(new Double(0.111d) , SearchLibrary.trunc(0.1111111111,3) );
		Assert.assertEquals(new Double(0.1111d) , SearchLibrary.trunc(0.1111111111,4) );
		Assert.assertEquals(new Double(0.11111d) , SearchLibrary.trunc(0.1111111111,5) );
		Assert.assertEquals(new Double(0.111111d) , SearchLibrary.trunc(0.1111111111,6) );
		Assert.assertEquals(new Double(0.1111111d) , SearchLibrary.trunc(0.1111111111,7) );
		
		Assert.assertEquals(new Double(9d) , SearchLibrary.trunc(9.9999999999,0) );
		Assert.assertEquals(new Double(9.9d) , SearchLibrary.trunc(9.9999999999,1) );
		Assert.assertEquals(new Double(9.99d) , SearchLibrary.trunc(9.9999999999,2) );
		Assert.assertEquals(new Double(9.999d) , SearchLibrary.trunc(9.9999999999,3) );
		Assert.assertEquals(new Double(9.9999d) , SearchLibrary.trunc(9.9999999999,4) );
		Assert.assertEquals(new Double(9.99999d) , SearchLibrary.trunc(9.9999999999,5) );
		Assert.assertEquals(new Double(9.999999d) , SearchLibrary.trunc(9.9999999999,6) );
		Assert.assertEquals(new Double(9.9999999d) , SearchLibrary.trunc(9.9999999999,7) );
	}
	
}
