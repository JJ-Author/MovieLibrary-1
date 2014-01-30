package jffsss.util.concurrent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.pivot.util.concurrent.TaskExecutionException;

public abstract class AbstractBufferedExecutor<A, B>
{
	private Map<A, List<Output>> _Buffer;
	private Queue<A> _Inputs;
	private ScheduledExecutorService _ExecutorService;
	private long _BaseDelay;
	private long _DiceDelay;
	private RunnableImpl _Runnable;

	public AbstractBufferedExecutor(int _PoolSize, long _BaseDelay, long _DiceDelay)
	{
		this._Buffer = new HashMap<A, List<Output>>();
		this._Inputs = new LinkedList<A>();
		if (_PoolSize == 1)
			this._ExecutorService = Executors.newSingleThreadScheduledExecutor();
		else
			this._ExecutorService = Executors.newScheduledThreadPool(_PoolSize);
		this._BaseDelay = _BaseDelay;
		this._DiceDelay = _DiceDelay;
		this._Runnable = new RunnableImpl();
	}

	public B execute(A _Input) throws TaskExecutionException
	{
		Output _Output = new Output();
		synchronized (this)
		{
			List<Output> _Outputs = this._Buffer.get(_Input);
			if (_Outputs == null)
			{
				_Outputs = new ArrayList<Output>();
				this._Buffer.put(_Input, _Outputs);
				this._Inputs.add(_Input);
			}
			_Outputs.add(_Output);
		}
		if (this._BaseDelay == 0 && this._DiceDelay == 0)
			this._ExecutorService.submit(this._Runnable);
		else
		{
			Random _Random = new Random();
			long _Delay = this._BaseDelay + ((long) (_Random.nextDouble() * (this._DiceDelay - this._BaseDelay)));
			this._ExecutorService.schedule(this._Runnable, _Delay, TimeUnit.MILLISECONDS);
		}
		return _Output.getResult();
	}

	protected abstract void execute();

	protected synchronized List<A> pollInputs(int _Count)
	{
		List<A> _Inputs = new ArrayList<A>();
		for (int i = 0; i < _Count; i++)
		{
			A _Input = this.pollInput();
			if (_Input == null)
				break;
			else
				_Inputs.add(_Input);
		}
		return _Inputs;
	}

	protected synchronized A pollInput()
	{
		return _Inputs.poll();
	}

	protected synchronized boolean isEmpty()
	{
		return this._Buffer.isEmpty();
	}

	protected void setResult(A _Input, B _Result)
	{
		List<Output> _Outputs;
		synchronized (this)
		{
			_Outputs = this._Buffer.remove(_Input);
		}
		if (_Outputs != null)
			for (Output _Output : _Outputs)
				_Output.setResult(_Result);
	}

	protected synchronized void setResult(B _Result)
	{
		for (List<Output> _Outputs : this._Buffer.values())
			for (Output _Output : _Outputs)
				_Output.setResult(_Result);
		this._Buffer.clear();
	}

	protected void setFault(A _Input, Throwable _Fault)
	{
		List<Output> _Outputs;
		synchronized (this)
		{
			_Outputs = this._Buffer.remove(_Input);
		}
		if (_Outputs != null)
			for (Output _Output : _Outputs)
				_Output.setFault(_Fault);
	}

	protected synchronized void setFault(Throwable _Fault)
	{
		for (List<Output> _Outputs : this._Buffer.values())
			for (Output _Output : _Outputs)
				_Output.setFault(_Fault);
		this._Buffer.clear();
	}

	private class Output
	{
		private boolean _ValueSet;
		private B _Result;
		private Throwable _Fault;

		public Output()
		{
			this._ValueSet = false;
			this._Result = null;
			this._Fault = null;
		}

		public synchronized B getResult() throws TaskExecutionException
		{
			if (!this._ValueSet)
				try
				{
					wait();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			if (this._Fault != null)
				throw new TaskExecutionException(this._Fault);
			return this._Result;
		}

		public synchronized void setResult(B _Result)
		{
			if (this._ValueSet)
				return;
			this._Result = _Result;
			this._ValueSet = true;
			notify();
		}

		public synchronized void setFault(Throwable _Fault)
		{
			if (this._ValueSet)
				return;
			this._Fault = _Fault;
			this._ValueSet = true;
			notify();
		}
	}

	private class RunnableImpl implements Runnable
	{
		@Override
		public void run()
		{
			AbstractBufferedExecutor.this.execute();
		}
	}
}