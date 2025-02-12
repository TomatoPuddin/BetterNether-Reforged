package someoneelse.betternetherreforged.world;

import java.util.HashMap;

import someoneelse.betternetherreforged.biomes.ActualNetherBiome;
import someoneelse.betternetherreforged.biomes.NetherBiome;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import someoneelse.betternetherreforged.noise.OpenSimplexNoise;

public class BiomeMap {
	private static final SharedSeedRandom RANDOM = new SharedSeedRandom();

	private final HashMap<ChunkPos, BiomeChunk> cache = new HashMap<ChunkPos, BiomeChunk>();
	private final int sizeXZ;
	private final int sizeY;
	protected final int maxHeight;
	private final int depth;
	private final int size;
	private final OpenSimplexNoise noiseX;
	private final OpenSimplexNoise noiseY;
	private final OpenSimplexNoise noiseZ;
	private final boolean volumetric;
	private final BiomeResolver resolver;

	public BiomeMap(long seed, int sizeXZ, int sizeY, boolean volumetric, BiomeResolver resolver) {
		RANDOM.setSeed(seed);
		noiseX = new OpenSimplexNoise(RANDOM.nextLong());
		noiseY = new OpenSimplexNoise(RANDOM.nextLong());
		noiseZ = new OpenSimplexNoise(RANDOM.nextLong());
		this.sizeXZ = sizeXZ;
		this.sizeY = sizeY;
		this.volumetric = volumetric;
		maxHeight = volumetric ? (int) Math.ceil(128F / sizeY) : 1;

		depth = (int) Math.ceil(Math.log(Math.max(sizeXZ, sizeY)) / Math.log(2)) - 2;
		size = 1 << depth;

		this.resolver = resolver;
	}

	public void clearCache() {
		if (cache.size() > 16)
			cache.clear();
	}

	private ActualNetherBiome getRawBiome(int bx, int by, int bz) {
		double x = bx * size / sizeXZ;
		double y = volumetric ? by * size / sizeY : 0;
		double z = bz * size / sizeXZ;
		double nx = x;
		double ny = y;
		double nz = z;

		double px = bx * 0.2;
		double py = by * 0.2;
		double pz = bz * 0.2;

		for (int i = 0; i < depth; i++) {
			nx = (x + noiseX.eval(px, pz)) / 2F;
			nz = (z + noiseZ.eval(px, pz)) / 2F;

			if (volumetric) {
				nz = (z + noiseY.eval(px, pz)) / 2F;

				y = ny;
				py = py / 2 + i;
			}

			x = nx;
			z = nz;

			px = px / 2 + i;
			pz = pz / 2 + i;
		}

		ChunkPos cpos = new ChunkPos(
				(int) Math.floor((double) x / BiomeChunk.WIDTH),
				(int) Math.floor((double) z / BiomeChunk.WIDTH));
		BiomeChunk chunk = cache.get(cpos);
		if (chunk == null) {
			RANDOM.setBaseChunkSeed(cpos.x, cpos.z);
			chunk = new BiomeChunk(this, RANDOM);
			cache.put(cpos, chunk);
		}

		return chunk.getBiome((int) x, MathHelper.clamp((int) y, 0, maxHeight - 1), (int) z);
	}

	public ActualNetherBiome getBiome(int x, int y, int z) {
		ActualNetherBiome actualNetherbiome = getRawBiome(x, y > 30 ? y : 30, z);
		NetherBiome biome = actualNetherbiome.getNetherBiome();

		if (biome.hasEdge() || (biome.hasParentBiome() && biome.getParentBiome().hasEdge())) {
			NetherBiome search = biome;
			if (biome.hasParentBiome()) {
				search = biome.getParentBiome();
			}
			int d = (int) Math.ceil(search.getEdgeSize() / 4F) << 2;

			boolean edge = !search.isSame(getRawBiome(x + d, y, z).getNetherBiome());
			edge = edge || !search.isSame(getRawBiome(x - d, y, z).getNetherBiome());
			edge = edge || !search.isSame(getRawBiome(x, y, z + d).getNetherBiome());
			edge = edge || !search.isSame(getRawBiome(x, y, z - d).getNetherBiome());
			edge = edge || !search.isSame(getRawBiome(x - 1, y, z - 1).getNetherBiome());
			edge = edge || !search.isSame(getRawBiome(x - 1, y, z + 1).getNetherBiome());
			edge = edge || !search.isSame(getRawBiome(x + 1, y, z - 1).getNetherBiome());
			edge = edge || !search.isSame(getRawBiome(x + 1, y, z + 1).getNetherBiome());

			if (edge) {
				biome = search.getEdge();
			}
		}

		return actualNetherbiome;
	}

	public BiomeResolver getResolver() {
		return resolver;
	}
}