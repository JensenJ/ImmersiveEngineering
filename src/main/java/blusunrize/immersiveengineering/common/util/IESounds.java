package blusunrize.immersiveengineering.common.util;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.HashSet;
import java.util.Set;

/**
 * @author BluSunrize - 03.07.2016
 */
public class IESounds
{
	static Set<SoundEvent> registeredEvents = new HashSet();
	public static SoundEvent metalpress_piston = registerSound("metalPressPiston");
	public static SoundEvent metalpress_smash = registerSound("metalPressSmash");
	public static SoundEvent birthdayParty = ImmersiveEngineering.register(new SoundEvent(new ResourceLocation(ImmersiveEngineering.MODID, "birthdayParty")),"birthdayParty");
	public static SoundEvent revolverFire = registerSound("revolverFire");
	public static SoundEvent spray= registerSound("spray");
	public static SoundEvent sprayFire = registerSound("spray_fire");
	public static SoundEvent chargeFast = registerSound("chargeFast");
	public static SoundEvent chargeSlow = registerSound("chargeSlow");
	public static SoundEvent spark = registerSound("spark");
	public static SoundEvent railgunFire = registerSound("railgunFire");
	public static SoundEvent tesla = registerSound("tesla");
	public static SoundEvent crusher = registerSound("crusher");
	public static SoundEvent dieselGenerator = registerSound("dieselGenerator");

	private static SoundEvent registerSound(String name)
	{
		ResourceLocation location = new ResourceLocation(ImmersiveEngineering.MODID, name);
		SoundEvent event = new SoundEvent(location);
		registeredEvents.add(event.setRegistryName(location));
		return event;
	}
	public static void init()
	{
		for(SoundEvent event : registeredEvents)
			GameRegistry.register(event);
	}

	public static void PlaySoundForPlayer(Entity player, SoundEvent sound, float volume, float pitch)
	{
		if(player instanceof EntityPlayerMP)
			((EntityPlayerMP) player).connection.sendPacket(new SPacketSoundEffect(sound, player.getSoundCategory(), player.posX, player.posY, player.posZ, volume, pitch));
	}
}
