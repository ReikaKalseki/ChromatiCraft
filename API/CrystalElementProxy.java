/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.API;

/** A proxy for the internal CrystalElement class; each enum object here corresponds to the Crystal Element of the same color.
 * Some basic info about each element is provided, including its ingame non-English name, its render color, and what "things" it represents. */
public enum CrystalElementProxy {

	BLACK("Kuro", 0x191919), //Magic
	RED("Karmir", 0xFF0000), //Endurance/Durability
	GREEN("Kijani", 0x007F0E), //Nature
	BROWN("Ruskea", 0x724528), //Mineral
	BLUE("Nila", 0x0026FF), //Light
	PURPLE("Zambarau", 0x8C00EA), //Enhancement
	CYAN("Vadali", 0x009FBF), //Water
	LIGHTGRAY("Argia", 0x979797), //Deception
	GRAY("Ykri", 0x404040), //Change
	PINK("Ruzova", 0xFFBAD9), //Aggression
	LIME("Asveste", 0x00FF00), //Motion
	YELLOW("Kitrino", 0xFFFF00), //Energy
	LIGHTBLUE("Galazio", 0x7FD4FF), //Time
	MAGENTA("Kurauri", 0xFF00DC), //Life
	ORANGE("Portokali", 0xFF6A00), //Fire
	WHITE("Tahara", 0xFFFFFF); //Purity/Harmony

	public final String name;
	public final int color;

	public static final CrystalElementProxy[] list = values();

	private CrystalElementProxy(String n, int c) {
		name = n;
		color = c;
	}

}
