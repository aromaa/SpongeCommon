/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedDataStorage;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.command.args.ChildCommandElementExecutor;
import org.spongepowered.api.data.type.Profession;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.storage.WorldProperties;
import org.spongepowered.common.command.SpongeCommandFactory;
import org.spongepowered.common.entity.SpongeProfession;
import org.spongepowered.common.event.tracking.PhaseContext;
import org.spongepowered.common.event.tracking.PhaseTracker;
import org.spongepowered.common.event.tracking.context.ItemDropData;
import org.spongepowered.common.event.tracking.phase.block.BlockPhase;
import org.spongepowered.common.event.tracking.phase.plugin.BasicPluginContext;
import org.spongepowered.common.event.tracking.phase.plugin.PluginPhase;
import org.spongepowered.common.interfaces.IMixinMinecraftServer;
import org.spongepowered.common.interfaces.block.tile.IMixinTileEntity;
import org.spongepowered.common.interfaces.entity.IMixinEntityLivingBase;
import org.spongepowered.common.interfaces.entity.player.IMixinEntityPlayer;
import org.spongepowered.common.interfaces.world.IMixinITeleporter;
import org.spongepowered.common.interfaces.world.IMixinWorldServer;
import org.spongepowered.common.item.inventory.util.InventoryUtil;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;
import org.spongepowered.common.registry.type.entity.EntityTypeRegistryModule;
import org.spongepowered.common.registry.type.entity.ProfessionRegistryModule;
import org.spongepowered.common.util.SpawnerSpawnType;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.FutureTask;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * Contains default Vanilla implementations for features that are only
 * available in Forge. SpongeForge overwrites the methods in this class
 * with calls to the Forge methods.
 */
public final class SpongeImplHooks {

    public static boolean isVanilla() {
        return true;
    }

    public static boolean isClientAvailable() {
        return false;
    }

    public static boolean isDeobfuscatedEnvironment() {
        return true;
    }

    public static String getModIdFromClass(Class<?> clazz) {
        final String className = clazz.getName();
        if (className.startsWith("net.minecraft.")) {
            return "minecraft";
        }
        if (className.startsWith("org.spongepowered.")) {
            return "sponge";
        }
        return "unknown";
    }

    // Entity

    public static boolean isCreatureOfType(Entity entity, EnumCreatureType type) {
        return type.getBaseClass().isAssignableFrom(entity.getClass());
    }

    public static boolean isFakePlayer(Entity entity) {
        return false;
    }

    public static void fireServerConnectionEvent(NetworkManager netManager) {
        // Implemented in SF
    }

    public static void firePlayerJoinSpawnEvent(EntityPlayerMP playerMP) {
        // Overwritten in SpongeForge
    }

    public static void handlePostChangeDimensionEvent(EntityPlayerMP playerIn, WorldServer fromWorld, WorldServer toWorld) {
        // Overwritten in SpongeForge
    }

    public static boolean checkAttackEntity(EntityPlayer entityPlayer, Entity targetEntity) {
        return true;
    }

    public static double getBlockReachDistance(EntityPlayerMP player) {
        return 5.0d;
    }

    // Entity registry

    @Nullable
    public static Class<? extends Entity> getEntityClass(ResourceLocation name) {
        return (Class) EntityTypeRegistryModule.getInstance().get((CatalogKey) (Object) name).get().getEntityClass();
    }

    @Nullable
    public static String getEntityTranslation(ResourceLocation name) {
        return IRegistry.ENTITY_TYPE.get(name).getTranslationKey();
    }

    public static int getEntityId(Class<? extends Entity> entityClass) {
        return EntityList.REGISTRY.getIDForObject(entityClass);
    }

    // Block

    public static boolean isBlockFlammable(Block block, IBlockReader world, BlockPos pos, EnumFacing face) {
        return ((BlockFire) Blocks.FIRE).getFlammability(block) > 0;
    }

    public static int getBlockLightOpacity(IBlockState state, IBlockReader world, BlockPos pos) {
        return state.getOpacity(world, pos);
    }

	public static int getChunkPosLight(IBlockState blockState, World world, BlockPos pos) {
		return blockState.getLightValue();
	}
    // Tile entity

    @Nullable
    public static TileEntity createTileEntity(Block block, net.minecraft.world.World world, IBlockState state) {
        if (block instanceof ITileEntityProvider) {
            return ((ITileEntityProvider) block).createNewTileEntity(world);
        }
        return null;
    }

    public static boolean hasBlockTileEntity(Block block, IBlockState state) {
        return block instanceof ITileEntityProvider;
    }

    public static boolean shouldRefresh(TileEntity tile, net.minecraft.world.World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public static void onTileChunkUnload(TileEntity te) {
        // Overwritten in SpongeForge
    }

    // World

    public static Iterator<Chunk> getChunkIterator(WorldServer world) {
        return world.getPlayerChunkMap().getChunkIterator();
    }

    public static void registerPortalAgentType(@Nullable IMixinITeleporter teleporter) {
        // Overwritten in SpongeForge
    }

    // World provider

    public static boolean canDoLightning(Dimension provider, net.minecraft.world.chunk.Chunk chunk) {
        return true;
    }

    public static boolean canDoRainSnowIce(Dimension provider, net.minecraft.world.chunk.Chunk chunk) {
        return true;
    }

    public static DimensionType getRespawnDimension(Dimension targetDimension, EntityPlayerMP player) {
        return DimensionType.OVERWORLD;
    }

    public static BlockPos getRandomizedSpawnPoint(WorldServer world) {
        BlockPos ret = world.getSpawnPoint();

        boolean isAdventure = world.getWorldInfo().getGameType() == GameType.ADVENTURE;
        int spawnFuzz = Math.max(0, world.getServer().getSpawnRadius(world));
        int border = MathHelper.floor(world.getWorldBorder().getClosestDistance(ret.getX(), ret.getZ()));
        if (border < spawnFuzz) {
            spawnFuzz = border;
        }

        if (!world.dimension.isNether() && !isAdventure && spawnFuzz != 0)
        {
            if (spawnFuzz < 2) {
                spawnFuzz = 2;
            }
            int spawnFuzzHalf = spawnFuzz / 2;
            ret = world.getTopSolidOrLiquidBlock(ret.add(world.rand.nextInt(spawnFuzzHalf) - spawnFuzz, 0, world.rand.nextInt(spawnFuzzHalf) - spawnFuzz));
        }

        return ret;
    }

    // Item stack merging

    public static void addItemStackToListForSpawning(Collection<ItemDropData> itemStacks, @Nullable ItemDropData itemStack) {
        // This is the hook that can be overwritten to handle merging the item stack into an already existing item stack
        if (itemStack != null) {
            itemStacks.add(itemStack);
        }
    }

    public static WorldSavedDataStorage getWorldSavedDataStorage(World world) {
        return world.getSavedDataStorage();
    }

    public static int countEntities(WorldServer worldServer, net.minecraft.entity.EnumCreatureType type, boolean forSpawnCount) {
        return worldServer.countEntities(type.getBaseClass());
    }

    public static int getMaxSpawnPackSize(EntityLiving entityLiving) {
        return entityLiving.getMaxSpawnedInChunk();
    }

    public static SpawnerSpawnType canEntitySpawnHere(EntityLiving entityLiving, boolean entityNotColliding) {
        if (entityLiving.getCanSpawnHere() && entityNotColliding) {
            return SpawnerSpawnType.NORMAL;
        }
        return SpawnerSpawnType.NONE;
    }

    @Nullable
    public static Object onUtilRunTask(FutureTask<?> task, Logger logger) {
        final PhaseTracker phaseTracker = PhaseTracker.getInstance();
        try (final BasicPluginContext context = PluginPhase.State.SCHEDULED_TASK.createPhaseContext()
                .source(task))  {
            context.buildAndSwitch();
            final Object o = Util.runTask(task, logger);
            return o;
        } catch (Exception e) {
            phaseTracker.printExceptionFromPhase(e);
            return null;
        }
    }

    public static void onEntityError(Entity entity, CrashReport crashReport) {
        throw new ReportedException(crashReport);
    }

    public static void onTileEntityError(TileEntity tileEntity, CrashReport crashReport) {
        throw new ReportedException(crashReport);
    }

    public static void blockExploded(Block block, World world, BlockPos blockpos, Explosion explosion) {
        world.setBlockState(blockpos, Blocks.AIR.getDefaultState());
        block.onExplosionDestroy(world, blockpos, explosion);
    }

    public static boolean isRestoringBlocks(World world) {
        if (PhaseTracker.getInstance().getCurrentState() == BlockPhase.State.RESTORING_BLOCKS) {
            return true;
        }

        return false;
    }

    public static void onTileEntityChunkUnload(net.minecraft.tileentity.TileEntity tileEntity) {
        // forge only method
    }

    public static boolean canConnectRedstone(Block block, IBlockState state, IBlockReader world, BlockPos pos, @Nullable EnumFacing side) {
        return state.canProvidePower() && side != null;
    }
    // Crafting

    public static Optional<ItemStack> getContainerItem(ItemStack itemStack) {
        checkNotNull(itemStack, "The itemStack must not be null");

        net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(itemStack);

        if (nmsStack.isEmpty()) {
            return Optional.empty();
        }

        Item nmsItem = nmsStack.getItem();

        if (nmsItem.hasContainerItem()) {
            Item nmsContainerItem = nmsItem.getContainerItem();
            net.minecraft.item.ItemStack nmsContainerStack = new net.minecraft.item.ItemStack(nmsContainerItem);
            ItemStack containerStack = ItemStackUtil.fromNative(nmsContainerStack);

            return Optional.of(containerStack);
        } else {
            return Optional.empty();
        }
    }

    public static Optional<CraftingRecipe> findMatchingRecipe(CraftingGridInventory inventory, org.spongepowered.api.world.World world) {
        IRecipe recipe = ((World) world).getRecipeManager().getRecipe(InventoryUtil.toNativeInventory(inventory), ((net.minecraft.world.World) world));
        return Optional.ofNullable(((CraftingRecipe) recipe));
    }

    public static Collection<CraftingRecipe> getCraftingRecipes() {
        return Streams.stream(SpongeImpl.getServer().getRecipeManager().getRecipes().iterator()).map(CraftingRecipe.class::cast).collect(ImmutableList.toImmutableList());
    }

    public static Optional<CraftingRecipe> getRecipeById(String id) {
        IRecipe recipe = SpongeImpl.getServer().getRecipeManager().getRecipe(new ResourceLocation(id));
        if (recipe == null) {
            return Optional.empty();
        }
        return Optional.of(((CraftingRecipe) recipe));
    }

    public static Optional<CraftingRecipe> getRecipeById(CatalogKey id) {
        IRecipe recipe = SpongeImpl.getServer().getRecipeManager().getRecipe((ResourceLocation) (Object) id);
        if (recipe == null) {
            return Optional.empty();
        }
        return Optional.of(((CraftingRecipe) recipe));
    }

    @Nullable
    public static PluginContainer getActiveModContainer() {
        return null;
    }

    public static Text getAdditionalCommandDescriptions() {
        return Text.empty();
    }

    public static void registerAdditionalCommands(ChildCommandElementExecutor flagChildren, ChildCommandElementExecutor nonFlagChildren) {
        // Overwritten in SpongeForge
    }

    public static Predicate<? super PluginContainer> getPluginFilterPredicate() {
        return plugin -> !SpongeCommandFactory.CONTAINER_LIST_STATICS.contains(plugin.getId());
    }

    // Borrowed from Forge, with adjustments by us

    @Nullable
    public static RayTraceResult rayTraceEyes(EntityLivingBase entity, double length) {
        final Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        final Vec3d endPos = startPos.add(entity.getLookVec().scale(length));
        return entity.world.rayTraceBlocks(startPos, endPos);
    }

    public static boolean shouldKeepSpawnLoaded(DimensionType dimensionType) {
        final WorldServer worldServer =
            ((IMixinMinecraftServer) Sponge.getServer()).getWorldLoader().getWorld(dimensionType).orElse(null);
        return worldServer != null && ((WorldProperties) worldServer.getWorldInfo()).doesKeepSpawnLoaded();

    }

    public static void setShouldLoadSpawn(DimensionType dimensionType, boolean keepSpawnLoaded) {
        // This is only used in SpongeForge
    }

    @Nullable
    public static BlockPos getBedLocation(EntityPlayer playerIn, DimensionType dimensionType) {
        return ((IMixinEntityPlayer) playerIn).getBedLocation(dimensionType);
    }

    public static boolean isSpawnForced(EntityPlayer playerIn, DimensionType dimensionType) {
        return ((IMixinEntityPlayer) playerIn).isSpawnForced(dimensionType);
    }

    @Nullable
    public static Inventory toInventory(Object inventory, @Nullable Object fallback) {
        SpongeImpl.getLogger().error("Unknown inventory " + inventory.getClass().getName() + " report this to Sponge");
        return null;
    }

    public static void onTileEntityInvalidate(TileEntity te) {
        te.remove();
    }

    public static void capturePerEntityItemDrop(PhaseContext<?> phaseContext, Entity owner,
        EntityItem entityitem) {
        phaseContext.getPerEntityItemEntityDropSupplier().get().put(owner.getUniqueID(), entityitem);
    }

    /**
     * @author gabizou - April 21st, 2018
     * Gets the enchantment modifier for looting on the entity living base from the damage source, but in forge cases, we need to use their hooks.
     *
     * @param mixinEntityLivingBase
     * @param entity
     * @param cause
     * @return
     */
    public static int getLootingEnchantmentModifier(IMixinEntityLivingBase mixinEntityLivingBase, EntityLivingBase entity, DamageSource cause) {
        return EnchantmentHelper.getLootingModifier(entity);
    }

    public static double getWorldMaxEntityRadius(IMixinWorldServer mixinWorldServer) {
        return 2.0D;
    }

    /**
     * Provides the {@link Profession} to set onto the villager. Since forge has it's own
     * villager profession system, sponge has to bridge the compatibility and
     * the profession may not be "properly" registered.
     * @param professionId
     * @return
     */
    public static Profession validateProfession(int professionId) {
        List<Profession> professions = (List<Profession>) ProfessionRegistryModule.getInstance().getAll();
        for (Profession profession : professions) {
            if (profession instanceof SpongeProfession) {
                if (professionId == ((SpongeProfession) profession).type) {
                    return profession;
                }
            }
        }
        throw new IllegalStateException("Invalid Villager profession id is present! Found: " + professionId
                                        + " when the expected contain: " + professions);

    }

    public static void onTETickStart(TileEntity te) {

    }

    public static void onTETickEnd(TileEntity te) {

    }

    public static void onEntityTickStart(Entity entity) {

    }

    public static void onEntityTickEnd(Entity entity) {

    }

    public static boolean isMainThread() {
        // Return true when the server isn't yet initialized, this means on a client
        // that the game is still being loaded. This is needed to support initialization
        // events with cause tracking.
        return !Sponge.isServerAvailable() || Sponge.getServer().onMainThread();
    }

    // Overridden by MixinSpongeImplHooks_ItemNameOverflowPrevention for exploit check
    public static boolean creativeExploitCheck(Packet<?> packetIn, EntityPlayerMP playerMP) {
        return false;
    }

    public static TileEntityType getTileEntityType(Class<? extends IMixinTileEntity> aClass) {
        return SpongeImpl.getRegistry().getTranslated(aClass, TileEntityType.class);
    }

    public static float getExplosionResistance(IBlockState state, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return state.getBlock().getExplosionResistance();
    }

    public static float getExplosionResistance(IFluidState fluidState, IWorldReader world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return fluidState.getExplosionResistance();
    }
}
