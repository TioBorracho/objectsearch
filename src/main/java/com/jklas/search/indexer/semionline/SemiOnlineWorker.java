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

import java.util.concurrent.BlockingQueue;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObject;
import com.jklas.search.indexer.IndexerAction;
import com.jklas.search.indexer.IndexerService;
import com.jklas.search.util.Pair;

public class SemiOnlineWorker implements Runnable {

	private final IndexerService indexerService;

	private final BlockingQueue<Pair<IndexerAction, IndexObject>> workQueue;

	private final SemiOnlineWorkerPool workerPool;

	private int okCount = 0, failedCount = 0;

	private boolean stopped = false;

	private boolean stopWhenQueueEmpties = false;

	public SemiOnlineWorker(SemiOnlineWorkerPool workerPool, BlockingQueue<Pair<IndexerAction, IndexObject>> workQueue, IndexerService indexerService) {
		this.indexerService = indexerService;
		this.workQueue = workQueue;
		this.workerPool = workerPool;
	}

	private void work(IndexerAction action, IndexObject indexObjectDto) {
		try {
			action.execute(indexerService, indexObjectDto);
			okCount++;
		} catch (IndexObjectException e) {			
			failedCount++;
		}
	}

	@Override
	public void run() {
		while(!stopped ) {
			Pair<IndexerAction, IndexObject> task ;
			try {
				if(stopWhenQueueEmpties && workQueue.isEmpty()) {
					return;
				}
				task = workQueue.take();
			}
			catch (InterruptedException e) {throw new RuntimeException("Worker interrupted while waiting for new tasks",e);}
			
			work(task.getFirst(), task.getSecond());
			workerPool.workerIsDone();
		}
	}

	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}

	public void setStopWhenQueueEmpties(boolean stopWhenQueueEmpties) {
		this.stopWhenQueueEmpties = stopWhenQueueEmpties;
	}	
}
