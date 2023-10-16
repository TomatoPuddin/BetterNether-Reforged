package someoneelse.betternetherreforged.world;

import java.util.Random;

import someoneelse.betternetherreforged.biomes.ActualNetherBiome;

public class BiomeChunk {
	protected static final int WIDTH = 16;
	private static final int SM_WIDTH = WIDTH >> 1;
	private static final int MASK_A = SM_WIDTH - 1;
	private static final int MASK_C = WIDTH - 1;

	private final int maxY;
	private final int maskB;
	private final ActualNetherBiome[][][] biomes;

	public BiomeChunk(BiomeMap map, Random random) {
		int sm_height = clampOne(map.maxHeight >> 1);
		maskB = sm_height - 1;
		maxY = map.maxHeight - 1;
		ActualNetherBiome[][][] PreBio = new ActualNetherBiome[sm_height][SM_WIDTH][SM_WIDTH];
		biomes = new ActualNetherBiome[map.maxHeight][WIDTH][WIDTH];

		for (int y = 0; y < sm_height; y++)
			for (int x = 0; x < SM_WIDTH; x++)
				for (int z = 0; z < SM_WIDTH; z++)
					PreBio[y][x][z] = map.getResolver().getBiome(random);

		for (int y = 0; y < map.maxHeight; y++)
			for (int x = 0; x < WIDTH; x++)
				for (int z = 0; z < WIDTH; z++)
					biomes[y][x][z] = map.getResolver().getBiome(PreBio[offsetY(y, random)][offsetXZ(x, random)][offsetXZ(z, random)].getNetherBiome().getSubBiome(random));
	}

	public ActualNetherBiome getBiome(int x, int y, int z)
	{
		return biomes[clamp(y)][x & MASK_C][z & MASK_C];
	}

	private int offsetXZ(int x, Random random)
	{
		return ((x + random.nextInt(2)) >> 1) & MASK_A;
	}

	private int offsetY(int y, Random random)
	{
		return ((y + random.nextInt(2)) >> 1) & maskB;
	}

	private int clamp(int y)
	{
		return y < 0 ? 0 : y > maxY ? maxY : y;
	}

	private int clampOne(int x)
	{
		return x < 1 ? 1 : x;
	}
}
