package com.jklas.search.index.memory;

import com.jklas.search.index.IndexId;
import com.jklas.search.index.IndexReaderFactory;
import com.jklas.search.index.MasterAndInvertedIndexReader;

public class MemoryIndexReaderFactory implements IndexReaderFactory {

	private static MemoryIndexReaderFactory instance = new MemoryIndexReaderFactory();

	public static MemoryIndexReaderFactory getInstance() {
		return instance;
	}

	private MemoryIndexReaderFactory() {
	}

	@Override
	public MasterAndInvertedIndexReader getIndexReader() {
		return new MemoryIndexReader();
	}

	@Override
	public MasterAndInvertedIndexReader getIndexReader(IndexId indexId) {
		return new MemoryIndexReader(indexId);
	}

}
