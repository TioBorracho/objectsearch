package com.jklas.search;

import org.junit.Assert;
import org.junit.Test;

public class SearchTest {

	@Test
	public void testResetErasesConfigurationWhenNoOneWasCreated() {
		SearchEngine.getInstance().reset();
		
		Assert.assertFalse(SearchEngine.getInstance().isConfigured());
		
		Assert.assertNull(SearchEngine.getInstance().getConfiguration());
	}
	
	@Test
	public void testResetErasesConfigurationWhenWeCreatedOne() {
		SearchEngine.getInstance().newConfiguration();
		
		SearchEngine.getInstance().reset();
				
		Assert.assertFalse(SearchEngine.getInstance().isConfigured());
		
		Assert.assertNull(SearchEngine.getInstance().getConfiguration());
	}
	
}
