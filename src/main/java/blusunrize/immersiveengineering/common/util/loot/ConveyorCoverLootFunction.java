/*
 * BluSunrize
 * Copyright (c) 2021
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 *
 */

package blusunrize.immersiveengineering.common.util.loot;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler;
import blusunrize.immersiveengineering.api.tool.conveyor.ConveyorHandler.IConveyorBlockEntity;
import blusunrize.immersiveengineering.common.blocks.metal.ConveyorBlock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;

public class ConveyorCoverLootFunction extends LootItemConditionalFunction
{
	public static final ResourceLocation ID = new ResourceLocation(ImmersiveEngineering.MODID, "conveyor_cover");

	protected ConveyorCoverLootFunction(LootItemCondition[] conditionsIn)
	{
		super(conditionsIn);
	}

	@Nonnull
	@Override
	protected ItemStack run(@Nonnull ItemStack stack, @Nonnull LootContext context)
	{
		Block asBlock = Block.byItem(stack.getItem());
		if(ConveyorHandler.isConveyorBlock(asBlock)&&context.hasParam(LootContextParams.BLOCK_ENTITY))
		{
			BlockEntity te = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
			if(te instanceof IConveyorBlockEntity<?> conveyorBE)
			{
				Block cover = conveyorBE.getConveyorInstance().getCover();
				if(cover!=Blocks.AIR)
					return ConveyorBlock.makeCovered(stack.getItem(), cover);
			}
		}
		return stack;
	}

	@Override
	public LootItemFunctionType getType()
	{
		return IELootFunctions.conveyorCover;
	}

	public static LootItemFunction.Builder builder()
	{
		return simpleBuilder(ConveyorCoverLootFunction::new);
	}
}
