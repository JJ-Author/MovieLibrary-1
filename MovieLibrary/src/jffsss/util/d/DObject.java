package jffsss.util.d;

import java.util.List;
import java.util.Map;

import jffsss.ParseException;

/**
 * Diese Klasse kann eingesetzt werden, wenn vorher nicht klar ist, wie die Daten strukturiert sind und sie dynamisch
 * erzeugt werden müssen.
 */
public interface DObject
{
	/**
	 * Überprüft, um dieses Objekt ein String ist.
	 * 
	 * @return <CODE>wahr</CODE>, falls dieses Objekt ein String ist
	 */
	public boolean isString();

	/**
	 * Gibt dieses Objekt als einen String zurück, falls dieses Objekt tatsachlich ein String ist. Sonst wird ein Fehler
	 * zurückgeworfen.
	 * 
	 * @return die String-Repräsentation dieses Objekts
	 */
	public String asString();

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Wahrheitswert daraus zu parsen.
	 * 
	 * @return der geparste Wert
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public Boolean parseAsBoolean() throws ParseException;

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Wahrheitswert daraus zu parsen.
	 * 
	 * @param _DefaultValue
	 *            falls das Parsen gescheitert ist, wird der Default-Wert zurückgegeben
	 * @return der geparste Wert
	 */
	public Boolean parseAsBoolean(Boolean _DefaultValue);

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Integer-Wert daraus zu parsen.
	 * 
	 * @return der geparste Wert
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public Integer parseAsInteger() throws ParseException;

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Integer-Wert daraus zu parsen.
	 * 
	 * @param _DefaultValue
	 *            falls das Parsen gescheitert ist, wird der Default-Wert zurückgegeben
	 * @return der geparste Wert
	 */
	public Integer parseAsInteger(Integer _DefaultValue);

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Long-Wert daraus zu parsen.
	 * 
	 * @return der geparste Wert
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public Long parseAsLong() throws ParseException;

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Long-Wert daraus zu parsen.
	 * 
	 * @param _DefaultValue
	 *            falls das Parsen gescheitert ist, wird der Default-Wert zurückgegeben
	 * @return der geparste Wert
	 */
	public Long parseAsLong(Long _DefaultValue);

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Double-Wert daraus zu parsen.
	 * 
	 * @return der geparste Wert
	 * @throws ParseException
	 *             falls das Parsen gescheitert ist
	 */
	public Double parseAsDouble() throws ParseException;

	/**
	 * Falls dieses Objekt ein String ist, wird versucht, der Double-Wert daraus zu parsen.
	 * 
	 * @param _DefaultValue
	 * @return der geparste Wert
	 */
	public Double parseAsDouble(Double _DefaultValue);

	/**
	 * Überprüft, ob dieses Objekt eine Liste ist.
	 * 
	 * @return <CODE>wahr</CODE>, falls dieses Objekt eine Liste ist
	 */
	public boolean isList();

	/**
	 * Gibt dieses Objekt als eine Liste zurück, falls dieses Objekt tatsachlich eine Liste ist. Sonst wird ein Fehler
	 * zurückgeworfen.
	 * 
	 * @return die Liste-Repräsentation dieses Objekts
	 */
	public List<DObject> asList();

	/**
	 * Überprüft, ob dieses Objekt eine Map ist.
	 * 
	 * @return <CODE>wahr</CODE>, falls dieses Objekt eine Map ist
	 */
	public boolean isMap();

	/**
	 * Gibt dieses Objekt als eine Map zurück, falls dieses Objekt tatsachlich eine Map ist. Sonst wird ein Fehler
	 * zurückgeworfen.
	 * 
	 * @return die Map-Repräsentation dieses Objekts
	 */
	public Map<String, DObject> asMap();
}