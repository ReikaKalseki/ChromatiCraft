/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import Reika.ChromatiCraft.World.Dimension.Rendering.ChromaCloudRenderer;
import Reika.ChromatiCraft.World.Dimension.Rendering.ChromaSkyRenderer;
import Reika.ChromatiCraft.World.Dimension.Rendering.ChromaWeatherRenderer;
import Reika.DragonAPI.Interfaces.CustomBiomeDistributionWorld;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderChroma extends WorldProvider implements CustomBiomeDistributionWorld {

	private ChunkProviderChroma chunkGen;

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
		//for (int i = 0; i < lightBrightnessTable.length; i++) {
		//lightBrightnessTable[i] = Math.max(0, lightBrightnessTable[i]*4F-3);
		//}
	}

	@Override
	public float calculateCelestialAngle(long time, float ptick)
	{
		return 0.5F;//super.calculateCelestialAngle(time, ptick);
	}

	@Override
	public double getVoidFogYFactor()
	{
		return 0.0001;//1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z)
	{
		return false;
	}

	@Override
	public boolean shouldMapSpin(String entity, double x, double y, double z)
	{
		return true;
	}

	@Override
	public boolean isDaytime()
	{
		return false;
	}

	@Override
	public float getSunBrightnessFactor(float par1)
	{
		return worldObj.getSunBrightnessFactor(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getSkyColor(Entity cameraEntity, float partialTicks)
	{
		return worldObj.getSkyColorBody(cameraEntity, partialTicks);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getSunBrightness(float par1)
	{
		return worldObj.getSunBrightnessBody(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getStarBrightness(float par1)
	{
		return worldObj.getStarBrightnessBody(par1);
	}

	@Override
	public void calculateInitialWeather() {
		/*
		worldObj.getWorldInfo().setRaining(true);
		if (worldObj.isRemote)
			worldObj.setRainStrength(0.25F);
		 */
	}

	@Override
	public void updateWeather() {
		/*
		worldObj.getWorldInfo().setRaining(true); //always raining, but most biomes do not render it
		if (worldObj.isRemote)
			worldObj.setRainStrength(0.25F);
		 */
	}

	@Override
	public boolean canBlockFreeze(int x, int y, int z, boolean byWater) {
		return false;
	}

	@Override
	public boolean canSnowAt(int x, int y, int z, boolean checkLight) {
		return false;
	}

	@Override
	public long getSeed() {
		return worldObj.getWorldInfo().getSeed();
	}

	@Override
	public long getWorldTime() {
		return worldObj.getWorldInfo().getWorldTime();
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
		return this.getChunkGenerator();
	}

	public ChunkProviderChroma getChunkGenerator() {
		if (chunkGen == null)
			chunkGen = new ChunkProviderChroma(worldObj);
		return chunkGen;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer()
	{
		return ChromaSkyRenderer.instance;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getCloudRenderer()
	{
		return ChromaCloudRenderer.instance;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getWeatherRenderer()
	{
		return ChromaWeatherRenderer.instance;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight()
	{
		double d = Math.sin(System.currentTimeMillis()/250000D);
		return 128+(float)(64*d);//512;
	}
	/*
	@Override
	public Vec3 getFogColor(float celang, float ptick)
	{
		return Vec3.createVectorHelper(0, 0, 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float celang, float ptick)
	{
		float[] ret = super.calcSunriseSunsetColors(celang, ptick);
		return ret;
	}
	 */
	@Override
	public boolean isSurfaceWorld()
	{
		return true;//false;//false; //return false makes sun and the like not render
	}

	@Override
	public boolean canRespawnHere()
	{
		return false;
	}

	@Override
	public String getDimensionName() {
		return "Proxima";
	}

	@Override
	public int getAverageGroundLevel() {
		return super.getAverageGroundLevel()+ChunkProviderChroma.VERTICAL_OFFSET;
	}

	@Override
	public boolean canMineBlock(EntityPlayer player, int x, int y, int z) {
		if (player.capabilities.isCreativeMode)
			return true;
		Block b = worldObj.getBlock(x, y, z);
		if (b instanceof SemiUnbreakable) {
			return !((SemiUnbreakable)b).isUnbreakable(worldObj, x, y, z, worldObj.getBlockMetadata(x, y, z));
		}
		return super.canMineBlock(player, x, y, z);
	}

	@Override
	public ChunkCoordinates getSpawnPoint() {
		return new ChunkCoordinates(0, 1024, 0);
	}

	@Override
	public int getBiomeID(World world, int x, int z) {
		return BiomeDistributor.getBiome(x, z).biomeID;
	}

}
