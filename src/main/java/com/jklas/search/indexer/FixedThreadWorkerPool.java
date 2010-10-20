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
package com.jklas.search.indexer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.indexer.semionline.SemiOnlineWorker;
import com.jklas.search.indexer.semionline.SemiOnlineWorkerPool;
import com.jklas.search.util.Pair;

public class FixedThreadWorkerPool implements SemiOnlineWorkerPool {

	public final static int DEFAULT_POOL_SIZE = 5;

	private final List<SemiOnlineWorker> singleWorkerList;

	private final BlockingQueue<Pair<IndexerAction, IndexObjectDto>> workQueue = new LinkedBlockingQueue<Pair<IndexerAction, IndexObjectDto>>();

	private AtomicInteger remainingJobs ;

	private boolean isClosed;

	private boolean latchIsSet;

	private CountDownLatch workerFinished = new CountDownLatch(0);

	private final IndexerService indexerService ;
	
	private final int poolSize;

	private boolean started;
	
	public FixedThreadWorkerPool(IndexerService indexerService) {
		this(indexerService, DEFAULT_POOL_SIZE);
	}

	public FixedThreadWorkerPool(IndexerService indexerService, int poolSize) {
		this.poolSize = poolSize;
		this.indexerService = indexerService;
		this.singleWorkerList = new ArrayList<SemiOnlineWorker>(poolSize);
	}

	@Override
	public void destroy() {
		isClosed = true;
		for (SemiOnlineWorker worker : singleWorkerList) {
			worker.setStopped(true);
		}
	}

	@Override
	public void destroyWhenFinished() {
		isClosed = true;
		for (SemiOnlineWorker worker : singleWorkerList) {
			worker.setStopWhenQueueEmpties(true);
		}
	}

	@Override
	public void newTask(IndexerAction indexerAction, IndexObjectDto objectToIndex) {
		if(!isClosed) {			
			workQueue.add(new Pair<IndexerAction,IndexObjectDto>(indexerAction,objectToIndex));
		}
	}

	@Override
	public void newTask(List<Pair<IndexerAction, IndexObjectDto>> objectsToIndex) {
		for (Pair<IndexerAction, IndexObjectDto> pair : objectsToIndex) {
			newTask(pair.getFirst(), pair.getSecond());
		}
	}

	@Override
	public void waitForAllWorkersToFinish() throws InterruptedException {
		//System.out.println("Waiting...");
		workerFinished.await();
		//System.out.println("Done...");
	}

	@Override
	public synchronized void workerIsDone() {
		if(!latchIsSet) return;
		
		int currentRemainingJobs = remainingJobs.addAndGet(-1);
		//System.out.println("Done job: "+currentRemainingJobs);

		if(currentRemainingJobs == 0 && latchIsSet) {
			workerFinished.countDown();
		}
	}

	private synchronized void renewLatch() {
		System.out.println("Latch Renewed...");

		if(!latchIsSet){
			latchIsSet = true;
			workerFinished = new CountDownLatch(1);			
		}
	}

	@Override
	public synchronized void enableWait() {
		//System.out.println("Enabling wait...");
		renewLatch();
	}
	
	@Override	
	public synchronized void start() {
		if(!started) {
			
			this.started = true;
			
			for (int i = 0; i < poolSize; i++) {
				Thread thread = new Thread(new SemiOnlineWorker(this, workQueue, indexerService));
				thread.start();
			}	
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
