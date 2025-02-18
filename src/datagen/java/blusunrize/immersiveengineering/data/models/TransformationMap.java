/*
 * BluSunrize
 * Copyright (c) 2019
 *
 * This code is licensed under "Blu's License of Common Sense"
 * Details can be found in the license file in the root folder of this project
 */

package blusunrize.immersiveengineering.data.models;

import blusunrize.immersiveengineering.common.util.chickenbones.Matrix4;
import com.google.gson.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraftforge.client.model.generators.ModelBuilder.Perspective;
import net.minecraftforge.common.model.TransformationHelper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

public class TransformationMap
{
	private static Field nameFromPerspective;

	static
	{
		try
		{
			//Who decided to make the one useful field in that enum private????
			nameFromPerspective = Perspective.class.getDeclaredField("name");
			nameFromPerspective.setAccessible(true);
		} catch(NoSuchFieldException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	private final Map<Perspective, Transformation> transforms = new EnumMap<>(Perspective.class);

	public TransformationMap setTransformations(Perspective t, Matrix4 mat)
	{
		transforms.put(t, new Transformation(mat.toMatrix4f()));
		return this;
	}

	private static String getName(Perspective p)
	{
		try
		{
			return (String)nameFromPerspective.get(p);
		} catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void addFromJson(String json)
	{
		Gson GSON = new GsonBuilder()
				.registerTypeAdapter(Transformation.class, new TransformationHelper.Deserializer())
				.registerTypeAdapter(ItemTransform.class, new ItemTransform.Deserializer())
				.create();
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		Map<Perspective, Transformation> transforms = new HashMap<>();
		Optional<String> type = Optional.ofNullable(obj.remove("type")).map(JsonElement::getAsString);
		boolean vanilla = type.map("vanilla"::equals).orElse(false);
		for(Perspective perspective : Perspective.values())
		{
			String key = getName(perspective);
			JsonObject forType = obj.getAsJsonObject(key);
			obj.remove(key);
			if(forType==null)
			{
				key = alternateName(perspective);
				forType = obj.getAsJsonObject(key);
				obj.remove(key);
			}
			Transformation transform;
			if(forType!=null)
			{
				if(vanilla)
				{
					ItemTransform vanillaTransform = GSON.fromJson(forType, ItemTransform.class);
					transform = TransformationHelper.toTransformation(vanillaTransform);
				}
				else
				{
					transform = readMatrix(forType, GSON);
					if(type.map("no_corner_offset"::equals).orElse(false))
						transform = transform.blockCornerToCenter();
				}
			}
			else
				transform = Transformation.identity();
			transforms.put(perspective, transform);
		}
		Transformation baseTransform;
		if(obj.size() > 0)
			baseTransform = readMatrix(obj, GSON);
		else
			baseTransform = Transformation.identity();
		for(Entry<Perspective, Transformation> e : transforms.entrySet())
		{
			Transformation transform = composeForgeLike(e.getValue(), baseTransform);
			this.transforms.put(e.getKey(), transform);
		}
	}

	/**
	 * Composes two matrices, with special cases for one being the identity. There's a method for that in Forge in
	 * principle, but calling it is rather inconsistent due to a naming conflict with official names and Forge making
	 * an absolute mess of mappings and patches.
	 */
	private static Transformation composeForgeLike(Transformation a, Transformation b)
	{
		if(a.isIdentity()) return b;
		if(b.isIdentity()) return a;
		Matrix4f m = a.getMatrix();
		m.multiply(b.getMatrix());
		return new Transformation(m);
	}

	private Transformation readMatrix(JsonObject json, Gson GSON)
	{
		if(!json.has("origin"))
			json.addProperty("origin", "center");
		return GSON.fromJson(json, Transformation.class);
	}

	private String alternateName(Perspective type)
	{
		return type.vanillaType.name()
				.toLowerCase(Locale.US);
	}

	public JsonObject toJson()
	{
		JsonObject ret = new JsonObject();
		for(Entry<Perspective, Transformation> entry : transforms.entrySet())
			add(ret, entry.getKey(), entry.getValue());
		return ret;
	}

	private void add(JsonObject main, Perspective type, Transformation trsr)
	{
		JsonObject result = new JsonObject();
		result.add("translation", toJson(trsr.getTranslation()));
		result.add("rotation", toJson(trsr.getLeftRotation()));
		result.add("scale", toJson(trsr.getScale()));
		result.add("post-rotation", toJson(trsr.getRightRotation()));
		result.addProperty("origin", "corner");
		main.add(getName(type), result);
	}

	private static JsonArray toJson(Quaternion v)
	{
		JsonArray ret = new JsonArray();
		ret.add(v.i());
		ret.add(v.j());
		ret.add(v.k());
		ret.add(v.r());
		return ret;
	}

	private static JsonArray toJson(Vector3f v)
	{
		JsonArray ret = new JsonArray();
		ret.add(v.x());
		ret.add(v.y());
		ret.add(v.z());
		return ret;
	}

	private static Vector3f fromJson(JsonElement ele)
	{
		JsonArray arr = ele.getAsJsonArray();
		return new Vector3f(arr.get(0).getAsFloat(), arr.get(1).getAsFloat(), arr.get(2).getAsFloat());
	}
}
