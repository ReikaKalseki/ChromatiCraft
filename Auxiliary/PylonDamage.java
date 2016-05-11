/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import Reika.ChromatiCraft.Registry.CrystalElement;

public final class PylonDamage extends DamageSource {

	public final CrystalElement color;

	public PylonDamage(CrystalElement e) {
		super("pylon");
		color = e;
	}

	@Override
	public final IChatComponent func_151519_b(EntityLivingBase e)
	{
		String sg = e.getCommandSenderName()+" got too close to a Crystal Pylon";
		switch(color) {
			case BLACK:
				sg = e.getCommandSenderName()+" died to black magic";
				break;
			case RED:
				sg = e.getCommandSenderName()+" protected themselves from living";
				break;
			case GREEN:
				sg = e.getCommandSenderName()+" has returned to nature";
				break;
			case BROWN:
				sg = e.getCommandSenderName()+" turned to mineral";
				break;
			case BLUE:
				sg = e.getCommandSenderName()+" was blinded by the light";
				break;
			case PURPLE:
				sg = "Apparently "+e.getCommandSenderName()+" felt death was an improvement";
				break;
			case CYAN:
				sg = e.getCommandSenderName()+" drowned in energy";
				break;
			case LIGHTGRAY:
				sg = "A Crystal Pylon tricked "+e.getCommandSenderName()+" into dying";
				break;
			case GRAY:
				sg = e.getCommandSenderName()+" experienced change...in the form of death";
				break;
			case PINK:
				sg = e.getCommandSenderName()+" got in a fight with a Crystal Pylon and lost";
				break;
			case LIME:
				sg = e.getCommandSenderName()+" became lethally agile";
				break;
			case YELLOW:
				sg = e.getCommandSenderName()+" experienced an energy overload";
				break;
			case LIGHTBLUE:
				sg = e.getCommandSenderName()+" accelerated everything, including dying";
				break;
			case MAGENTA:
				sg = e.getCommandSenderName()+" healed so strongly it killed them";
				break;
			case ORANGE:
				sg = e.getCommandSenderName()+" overheated";
				break;
			case WHITE:
				sg = e.getCommandSenderName()+" was purified into nothing";
				break;
		}
		return new ChatComponentTranslation(sg);
	}

	@Override
	public boolean isUnblockable()
	{
		return true;
	}

	@Override
	public boolean isDamageAbsolute()
	{
		return true;
	}

	@Override
	public boolean isExplosion()
	{
		return false;
	}

	@Override
	public boolean isProjectile()
	{
		return false;
	}

	@Override
	public boolean canHarmInCreative()
	{
		return false;
	}

	@Override
	public Entity getSourceOfDamage()
	{
		return this.getEntity();
	}

	@Override
	public Entity getEntity()
	{
		return null;
	}

	@Override
	public boolean isFireDamage()
	{
		return false;
	}

	@Override
	public boolean isDifficultyScaled()
	{
		return false;
	}

	@Override
	public boolean isMagicDamage()
	{
		return true;
	}

}
