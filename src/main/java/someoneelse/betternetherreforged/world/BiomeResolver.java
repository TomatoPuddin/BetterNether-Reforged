package someoneelse.betternetherreforged.world;

import net.minecraft.world.biome.Biome;
import someoneelse.betternetherreforged.biomes.ActualNetherBiome;
import someoneelse.betternetherreforged.biomes.NetherBiome;
import someoneelse.betternetherreforged.registry.NetherBiomesRegistry;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class BiomeResolver {
    final Map<Biome, NetherBiome> mutable;
    final ArrayList<ActualNetherBiome> generator;
    final Map<NetherBiome, ActualNetherBiome> actualBiomeMap;
    final float maxChance;
    final ActualNetherBiome fallbackBiome;

    public BiomeResolver(Map<Biome, NetherBiome> mutable, ArrayList<ActualNetherBiome> generator, float maxChance) {
        this.mutable = mutable;
        this.generator = generator;
        this.maxChance = maxChance;
        fallbackBiome = generator.stream().filter(b -> b.getNetherBiome() == NetherBiomesRegistry.BIOME_EMPTY_NETHER).findFirst().get();
        actualBiomeMap = generator.stream().collect(Collectors.toMap(ActualNetherBiome::getNetherBiome, b -> b));
    }

    public ActualNetherBiome getBiome(Random random) {
        float chance = random.nextFloat() * maxChance;
        for (ActualNetherBiome biome : generator)
            if (biome.getNetherBiome().canGenerate(chance))
                return biome;
        return fallbackBiome;
    }

    public ActualNetherBiome getBiome(NetherBiome biome) {
        return actualBiomeMap.get(biome);
    }

    public NetherBiome getFromBiome(Biome biome) {
        return mutable.getOrDefault(biome, NetherBiomesRegistry.BIOME_EMPTY_NETHER);
    }
}
