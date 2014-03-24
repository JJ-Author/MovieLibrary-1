package jffsss.util;

/**
 * Interface f�r den Listener aus dem Observer-Designpattern.
 */
public interface Listener
{
	/**
	 * Wird aufgerufen, wenn im beobachteten Objekt eine �nderung auftrat.
	 * 
	 * @param _Source
	 *            das beobachtete Objekt, in dem eine �nderung auftrat
	 * @param _Command
	 *            die benannte �nderung, die aufgetreten ist
	 * @param _Arg
	 *            ein optionales Argument
	 */
	public void on(Object _Source, String _Command, Object _Arg);
}