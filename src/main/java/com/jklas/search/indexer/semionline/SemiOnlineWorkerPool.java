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
