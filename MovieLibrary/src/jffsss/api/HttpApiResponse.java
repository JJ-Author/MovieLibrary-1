package jffsss.api;

import jffsss.util.d.DMap;
import jffsss.util.d.DObject;
import jffsss.util.d.DString;

/**
 * HttpApiResponse ist die Standard-Response der HTTP-Schnittstellen.
 */
public class HttpApiResponse extends DMap
{
	/**
	 * Konstruiert ein HttpApiResponse-Objekt mit einem Statuscode und einem Inhalt.
	 * 
	 * @param _StatusCode
	 *            der Statuscode
	 * @param _Content
	 *            der Inhalt
	 */
	public HttpApiResponse(int _StatusCode, DObject _Content)
	{
		this.asMap().put("StatusCode", new DString(_StatusCode));
		this.asMap().put("Content", _Content);
	}

	/**
	 * Konstruiert ein HttpApiResponse-Objekt nur mit einem Statuscode.
	 * 
	 * @param _StatusCode
	 *            der Statuscode
	 */
	public HttpApiResponse(int _StatusCode)
	{
		this.asMap().put("StatusCode", new DString(_StatusCode));
	}
}