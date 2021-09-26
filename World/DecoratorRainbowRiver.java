package Reika.ChromatiCraft.World;


public class DecoratorRainbowRiver extends RainbowForestDecorator {

	public DecoratorRainbowRiver() {
		super();
		flowersPerChunk *= 6;
		reedsPerChunk *= 15;
		grassPerChunk *= 0.4;

		sandPerChunk2 /= 2;
		clayPerChunk *= 9;
		sandPerChunk *= 8;
	}

	@Override
	protected boolean generateLavaLakes() {
		return false;
	}

	@Override
	protected boolean genRoses() {
		return true;
	}

}
