import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AetherChunkGenerator extends ChunkGenerator {

    private static final Material[] ISLAND_MATERIALS = {Material.STONE, Material.GRASS_BLOCK, Material.DIRT};
    private static final int MAX_ISLAND_HEIGHT = 60;
    private static final int ISLAND_FREQUENCY = 10; // 1 in X chance of generating an island

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList(new AetherBlockPopulator());
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true; // Allow players to spawn in the Aether world
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        byte[][] result = new byte[world.getMaxHeight() / 16][];

        // Generate the blocks for each chunk section
        for (int sectionY = 0; sectionY < result.length; sectionY++) {
            result[sectionY] = new byte[16 * 16 * 16];
        }

        // Generate islands
        if (random.nextInt(ISLAND_FREQUENCY) == 0) {
            int islandX = random.nextInt(16) + (chunkX * 16);
            int islandZ = random.nextInt(16) + (chunkZ * 16);
            generateIsland(world, islandX, islandZ, random, result);
        }

        return result;
    }

    private void generateIsland(World world, int x, int z, Random random, byte[][] result) {
        int islandY = random.nextInt(MAX_ISLAND_HEIGHT);
        int islandSize = random.nextInt(5) + 5;

        for (int xOffset = -islandSize; xOffset <= islandSize; xOffset++) {
            for (int zOffset = -islandSize; zOffset <= islandSize; zOffset++) {
                for (int yOffset = 0; yOffset <= islandSize * 2; yOffset++) {
                    int blockX = x + xOffset;
                    int blockY = islandY + yOffset;
                    int blockZ = z + zOffset;

                    if (isValidBlockPosition(blockY)) {
                        Material material = getIslandBlockType(yOffset, islandSize);
                        setBlock(result, blockX, blockY, blockZ, material);
                    }
                }
            }
        }
    }

    private boolean isValidBlockPosition(int blockY) {
        return blockY >= 0 && blockY < 256;
    }

    private Material getIslandBlockType(int yOffset, int islandSize) {
        if (yOffset < islandSize) {
            return ISLAND_MATERIALS[0]; // Stone
        } else if (yOffset == islandSize) {
            return ISLAND_MATERIALS[1]; // Grass Block
        } else if (yOffset == islandSize + 1) {
            return ISLAND_MATERIALS[2]; // Dirt
        } else {
            return Material.AIR;
        }
    }

    private void setBlock(byte[][] result, int x, int y, int z, Material material) {
        if (y >= 0 && y < 256) {
            int section = y >> 4;
            if (result[section] == null) {
                result[section] = new byte[16 * 16 * 16];
            }
            result[section][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
        }
    }
}
