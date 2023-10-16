package someoneelse.betternetherreforged.biomes;

import net.minecraft.world.biome.Biome;

public class ActualNetherBiome {
    private final NetherBiome netherBiome;
    private final Biome actualBiome;

    public ActualNetherBiome(NetherBiome netherBiome, Biome actualBiome) {
        this.netherBiome = netherBiome;
        this.actualBiome = actualBiome;
    }

    public NetherBiome getNetherBiome() {
        return netherBiome;
    }

    public Biome getActualBiome() {
        return actualBiome;
    }
}
