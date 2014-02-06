package jffsss.net;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class HideMyAssProxyProvider implements ProxyProvider
{
	private List<Proxy> proxies = new ArrayList<Proxy>();
	private int buf;

	public HideMyAssProxyProvider(int buf)
	{
		this.buf = buf;
	}

	public synchronized Proxy provideProxy()
	{
		return null;
	}
}