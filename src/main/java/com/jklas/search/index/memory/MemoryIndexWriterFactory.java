package com.jklas.search.index.memory;

import com.jklas.search.index.MasterAndInvertedIndexWriter;
import com.jklas.search.index.IndexWriterFactory;

public class MemoryIndexWriterFactory implements IndexWriterFactory {

	private final static MemoryIndexWriterFactory instance = new MemoryIndexWriterFactory();

	private MemoryIndexWriterFactory() { }
	
	public static MemoryIndexWriterFactory getInstance() {
		return instance;
	}

	@Override
	public MasterAndInvertedIndexWriter getIndexWriter() {
		return new MemoryIndexWriter();
	}

}
