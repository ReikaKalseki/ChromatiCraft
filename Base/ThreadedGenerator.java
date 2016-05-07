/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import java.util.Random;


public abstract class ThreadedGenerator {

	protected final long seed;
	protected final Random rand;

	protected ThreadedGenerator(long seed) {
		this.seed = seed;
		rand = new Random(seed);
	}

	public abstract void run() throws Throwable;

	public abstract String getStateMessage();

}
