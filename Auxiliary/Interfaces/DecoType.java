/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import java.util.List;

import net.minecraft.util.IIcon;

public interface DecoType {

	List<IIcon> getIcons(int pass);

	boolean hasBlockRender();

}
