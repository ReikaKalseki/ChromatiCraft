/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderChroma extends WorldProvider {

	public WorldProviderChroma() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean getWorldHasVoidParticles()
	{
		return false;
	}

	@Override
	protected void generateLightBrightnessTable()
	{
		super.generateLightBrightnessTable();
		for (int i = 0; i < lightBrightnessTable.length; i++) {
			lightBrightnessTable[i] *= 1.5F;
		}
	}

	@Override
	public String getWelcomeMessage()
	{
		return "";
	}

	@Override
	public String getDepartMessage()
	{
		return "";
	}

	@Override
	public double getMovementFactor()
	{
		return 1;//ReikaRandomHelper.getRandomPlusMinus(4, 3.5);
	}

	@Override
	public int getHeight()
	{
		return 256; //1024?
	}

	@Override
	public IChunkProvider createChunkGenerator()
	{
		return new ChunkProviderChroma(worldObj);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		return super.getSkyRenderer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getCloudRenderer()
	{
		return super.getCloudRenderer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getWeatherRenderer()
	{
		return super.getWeatherRenderer();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight()
	{
		return 512;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float celang, float ptick)
	{
		return super.getFogColor(celang, ptick);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float celang, float ptick)
	{
		float[] ret = super.calcSunriseSunsetColors(celang, ptick);
		return ret;
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return true;//false; //return false makes sun and the like not render
	}

	@Override
	public boolean canRespawnHere()
	{
		return false;
	}

	@Override
	public String getDimensionName() {
		return "Chroma";
	}

}
