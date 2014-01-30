package jffsss.util.d;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jffsss.util.Utils;

public class DList extends DAbstractObject
{
	private List<DObject> _Value;

	public DList()
	{
		this(Collections.<DObject>emptyList());
	}

	public DList(List<DObject> _Value)
	{
		this._Value = new ArrayList<DObject>(_Value);
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