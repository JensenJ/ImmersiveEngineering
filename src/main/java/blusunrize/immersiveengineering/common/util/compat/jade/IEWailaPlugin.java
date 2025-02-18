/*
 * BluSunrize
 * Copyright (c) 2017
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.common.util.compat.jade;

import blusunrize.immersiveengineering.common.blocks.IEMultiblockBlock;
import blusunrize.immersiveengineering.common.blocks.plant.HempBlock;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;

@WailaPlugin
public class IEWailaPlugin implements IWailaPlugin
{
	@Override
	public void register(IRegistrar registrar)
	{
		registrar.registerComponentProvider(new HempDataProvider(), TooltipPosition.BODY, HempBlock.class);
		registrar.registerIconProvider(new MultiblockIconProvider(), IEMultiblockBlock.class);
	}
}