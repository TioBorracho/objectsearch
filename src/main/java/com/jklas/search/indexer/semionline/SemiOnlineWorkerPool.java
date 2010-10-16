package com.jklas.search.indexer.semionline;

import java.util.List;

import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.indexer.IndexerAction;
import com.jklas.search.util.Pair;

// All implementations must be thread safe
public interface SemiOnlineWorkerPool {

	public void setGlobalTaskCount(int taskCount);
	
	public int getGlobalTaskCount();
	
	public void newTask(IndexerAction indexerAction, IndexObjectDto objectToIndex);
	
	public void newTask(List<Pair<IndexerAction,IndexObjectDto>> objectsToIndex);
	
	public void destroy();

	// stops accepting new tasks, waits for current tasks to be completed and stops
	public void destroyWhenFinished();

	// blocks until all workers finish their work
	public void waitForAllWorkersToFinish() throws InterruptedException;

	public void workerIsDone();

	// if you want to wait for workers to finish, you must
	// set a point in time first
	public void enableWait();

	public void start();
	
}
