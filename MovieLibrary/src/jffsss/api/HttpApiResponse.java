package jffsss.api;

import jffsss.util.d.DMap;
import jffsss.util.d.DObject;
import jffsss.util.d.DString;

public class HttpApiResponse extends DMap
{
	public HttpApiResponse(int _StatusCode, DObject _Content)
	{
		this.asMap().put("StatusCode", new DString(_StatusCode));
		this.asMap().put("Content", _Content);
	}
	
	public HttpApiResponse(int _StatusCode)
	{
		this.asMap().put("StatusCode", new DString(_StatusCode));
	}
}