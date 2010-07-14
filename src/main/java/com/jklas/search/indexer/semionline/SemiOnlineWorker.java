package com.jklas.search.indexer.semionline;

import java.util.concurrent.BlockingQueue;

import com.jklas.search.exception.IndexObjectException;
import com.jklas.search.index.dto.IndexObjectDto;
import com.jklas.search.indexer.IndexerAction;
import com.jklas.search.indexer.IndexerService;
import com.jklas.search.util.Pair;

public class SemiOnlineWorker implements Runnable {

	private final IndexerService indexerService;

	private final BlockingQueue<Pair<IndexerAction, IndexObjectDto>> workQueue;

	private final SemiOnlineWorkerPool workerPool;

	private int okCount = 0, failedCount = 0;

	private boolean stopped = false;

	private boolean stopWhenQueueEmpties = false;

	public SemiOnlineWorker(SemiOnlineWorkerPool workerPool, BlockingQueue<Pair<IndexerAction, IndexObjectDto>> workQueue, IndexerService indexerService) {
		this.indexerService = indexerService;
		this.workQueue = workQueue;
		this.workerPool = workerPool;
	}

	private void work(IndexerAction action, IndexObjectDto indexObjectDto) {
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
			Pair<IndexerAction, IndexObjectDto> task ;
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
