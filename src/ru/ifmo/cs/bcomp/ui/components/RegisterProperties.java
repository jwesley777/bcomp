/*
 * $Id$
 */

package ru.ifmo.cs.bcomp.ui.components;

import ru.ifmo.cs.bcomp.CPU;

/**
 *
 * @author Dmitry Afanasiev <KOT@MATPOCKuH.Ru>
 */
public class RegisterProperties {
	public final CPU.Reg reg;
	public final String title;
	public final int x;
	public final int y;
	public final boolean hex;

	public RegisterProperties(CPU.Reg reg, String title, int x, int y, boolean hex) {
		this.reg = reg;
		this.title = title;
		this.x = x;
		this.y = y;
		this.hex = hex;
	}
}
