package jffsss.api;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import jffsss.util.d.DObject;

public class HideMyAssProxyProvider implements ProxyProvider
{
	private LinkedBlockingDeque<Proxy> _Proxies = new LinkedBlockingDeque<Proxy>();
	private int _PagesBuffer;

	public HideMyAssProxyProvider(int _PagesBuffer)
	{
		this._PagesBuffer = _PagesBuffer;
	}

	public synchronized Proxy provideProxy()
	{
		if (this._Proxies.isEmpty())
		{
			System.out.println("this._Proxies.isEmpty()");
			(new Thread(new RunnableImpl())).start();
		}
		try
		{
			return this._Proxies.pollFirst(10, TimeUnit.SECONDS);
		}
		catch (InterruptedException e)
		{
			return null;
		}
	}

	private class RunnableImpl implements Runnable
	{
		@Override
		public void run()
		{
			for (int i = 1; i <= HideMyAssProxyProvider.this._PagesBuffer; i++)
			{
				try
				{
					HideMyAssApi _Api = new HideMyAssApi();
					List<DObject> _ResponseMapList = _Api.requestProxies(i).asMap().get("Content").asList();
					System.out.println(_ResponseMapList);
					for (DObject _ResponseMapListElement : _ResponseMapList)
					{
						Map<String, DObject> _ResponseMapListMap = _ResponseMapListElement.asMap();
						String _Host = _ResponseMapListMap.get("Host").asString();
						int _Port = _ResponseMapListMap.get("Port").parseAsInteger();
						Proxy _Proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(_Host, _Port));
						HideMyAssProxyProvider.this._Proxies.add(_Proxy);
					}
				}
				catch (Exception e)
				{
					break;
				}
			}
		}
	}
}