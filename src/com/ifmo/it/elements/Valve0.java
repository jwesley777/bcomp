/**
* $Id$
*/

package com.ifmo.it.elements;

public class Valve0 extends Valve
{
	public Valve0(DataSource ... ctrls)
	{
		super(1, ctrls);
	}

	public void setValue(int ctrl)
	{
		setValue(ctrl, 0);
	}
}
