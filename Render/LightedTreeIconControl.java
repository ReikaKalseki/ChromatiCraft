/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render;

import java.util.Random;

import net.minecraft.client.resources.data.AnimationMetadataSection;


public class LightedTreeIconControl extends AnimationMetadataSection {

	private final Random rand = new Random();

	private static final int ANIMATION_SECTIONS = 5;
	private static final int ANIMATION_SECTION_LENGTH = 16; //in frames

	private long nextCallTime = -1;
	private int currentSection;

	public LightedTreeIconControl(int index, AnimationMetadataSection ams) {
		super(ams.animationFrames, ams.getFrameWidth(), ams.getFrameHeight(), ams.getFrameTime());

		rand.setSeed(System.currentTimeMillis() ^ System.identityHashCode(ams) + index * 24578941);
	}

	@Override
	public int getFrameIndex(int counter) {
		long time = System.currentTimeMillis();
		if (time >= nextCallTime) {
			currentSection = rand.nextInt(ANIMATION_SECTIONS);
			nextCallTime = time+2000+rand.nextInt(3001);
		}
		int n = (counter%ANIMATION_SECTION_LENGTH)%this.getFrameCount()+currentSection*ANIMATION_SECTION_LENGTH;
		return super.getFrameIndex(n);
	}

}
