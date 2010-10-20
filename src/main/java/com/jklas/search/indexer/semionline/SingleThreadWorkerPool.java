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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.indexer.DefaultIndexerService;
import com.jklas.search.indexer.IndexerAction;
import com.jklas.search.util.Pair;

public class SingleThreadWorkerPool implements SemiOnlineWorkerPool {

	private final Thread singleThread;

	private final SemiOnlineWorker singleWorker;

	private final BlockingQueue<Pair<IndexerAction, IndexObjectDto>> workQueue = new LinkedBlockingQueue<Pair<IndexerAction, IndexObjectDto>>();	

	private AtomicInteger remainingJobs = new AtomicInteger(0);

	private boolean latchIsSet = false;

	private CountDownLatch workerFinished = new CountDownLatch(0);

	private boolean isClosed = false;

	private boolean started;

	public SingleThreadWorkerPool(DefaultIndexerService indexerService) {
		this.singleWorker = new SemiOnlineWorker(this, workQueue, indexerService);
		singleThread = new Thread(singleWorker);		
	}

	@Override
	public void newTask(List<Pair<IndexerAction, IndexObjectDto>> objectsToIndex) {
		if(!isClosed) {
			for (Pair<IndexerAction, IndexObjectDto> pair : objectsToIndex) {
				newTask(pair.getFirst(), pair.getSecond());
			}
		}
	}


	@Override
	public void newTask(IndexerAction indexerAction, IndexObjectDto objectToIndex) {
		if(!isClosed) {			
			workQueue.add(new Pair<IndexerAction,IndexObjectDto>(indexerAction,objectToIndex));
		}
	}

	public void destroy() {
		isClosed = true;
		singleWorker.setStopped(true);		
	}

	@Override
	public void destroyWhenFinished() {
		isClosed = true;
		singleWorker.setStopWhenQueueEmpties(true);
	}

	@Override
	public void waitForAllWorkersToFinish() throws InterruptedException {
		workerFinished.await();
	}

	@Override
	public synchronized void workerIsDone() {
		int currentRemainingJobs = remainingJobs.addAndGet(-1);

		if(currentRemainingJobs == 0 && latchIsSet) {			
			workerFinished.countDown();
		}
	}

	private synchronized void renewLatch() {
		if(!latchIsSet){
			latchIsSet = true;
			workerFinished = new CountDownLatch(1);			
		}
	}

	@Override
	public synchronized void enableWait() {
		renewLatch();
	}

	@Override
	public void start() {
		if(!started) {
			this.started = true;
			singleThread.start();					
		}
	}

	@Override
	public int getGlobalTaskCount() {
		return this.remainingJobs.get();
	}

	@Override
	public void setGlobalTaskCount(int taskCount) {
		this.remainingJobs = new AtomicInteger(taskCount);
	}

}
