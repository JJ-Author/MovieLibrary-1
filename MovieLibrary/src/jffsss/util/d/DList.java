package jffsss.util.d;

import java.util.ArrayList;
import java.util.List;

import jffsss.util.Utils;

/**
 * Die Liste-Implementation der dynamischen Struktur.
 */
public class DList extends DAbstractObject
{
	private List<DObject> _Value;

	/**
	 * Konstruiert eine leere Liste.
	 */
	public DList()
	{
		this._Value = new ArrayList<DObject>();
	}

	/**
	 * Konstruiert eine Liste mit dem Vorgabe-Wert.
	 * 
	 * @param _Value
	 *            ein Vorgabe-Wert
	 */
	public DList(List<DObject> _Value)
	{
		this();
		this._Value.addAll(_Value);
	}

	@Override
	public boolean isList()
	{
		return true;
	}

	@Override
	public List<DObject> asList()
	{
		return this._Value;
	}

	@Override
	public String toString()
	{
		return "[" + Utils.join(this._Value, ", ") + "]";
	}
}