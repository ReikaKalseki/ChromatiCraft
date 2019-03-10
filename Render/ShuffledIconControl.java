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


public class ShuffledIconControl extends AnimationMetadataSection {

	private final Random rand = new Random();

	public static final int ANIMATION_SECTIONS_GLOWLEAF = 5;
	public static final int ANIMATION_SECTION_LENGTH_GLOWLEAF = 16; //in frames

	//public static final int ANIMATION_SECTIONS_GLOWCAVE = 1;
	//public static final int ANIMATION_SECTION_LENGTH_GLOWCAVE = 40;

	private long nextCallTime = -1;
	private int currentSection;

	private final int animationSections;
	private final int animationSectionLength;

	public ShuffledIconControl(int index, AnimationMetadataSection ams, int s, int l) {
		super(ams.animationFrames, ams.getFrameWidth(), ams.getFrameHeight(), ams.getFrameTime());

		rand.setSeed(System.currentTimeMillis() ^ System.identityHashCode(ams) + index * 24578941);

		animationSectionLength = l;
		animationSections = s;
	}

	@Override
	public int getFrameIndex(int counter) {
		long time = System.currentTimeMillis();
		if (time >= nextCallTime) {
			currentSection = rand.nextInt(animationSections);
			nextCallTime = time+2000+rand.nextInt(3001);
		}
		int n = (counter%animationSectionLength)%this.getFrameCount()+currentSection*animationSectionLength;
		return super.getFrameIndex(n);
	}

}
