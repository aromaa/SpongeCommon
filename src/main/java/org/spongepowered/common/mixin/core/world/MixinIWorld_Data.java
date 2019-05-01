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
package org.spongepowered.common.mixin.core.world;

import static com.google.common.base.Preconditions.checkState;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IWorld;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.property.LocationBasePropertyHolder;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.data.property.store.PropertyStore;
import org.spongepowered.api.data.value.Value;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.ProtoWorld;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.volume.LocationCompositeValueStore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.registry.provider.DirectionFacingProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;

/**
 * Specifically all of the Data API related implementation methods are
 * gathered here, instead of {@link MixinIWorld_API}, because a majority
 * of all of this implementation will still function the same way on
 * any ProtoWorld (IWorld) type, including client worlds, chunk caches,
 * etc. because they are based on
 * @param <P>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
@Mixin(IWorld.class)
public interface MixinIWorld_Data<P extends ProtoWorld<P>> extends ProtoWorld<P>, LocationBasePropertyHolder, LocationCompositeValueStore {

    @Shadow net.minecraft.world.World shadow$getWorld();

    @Override
    default Map<Property<?>, ?> getProperties(Vector3i coords) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getPropertiesFor(new Location(getWorld(), coords));
    }

    @Override
    default Map<Property<?>, ?> getProperties(int x, int y, int z) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getPropertiesFor(new Location(getWorld(), x, y, z));
    }

    @Override
    default <V> Optional<V> getProperty(Vector3i coords, Property<V> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getStore(property).getFor(new Location(getWorld(), coords));
    }

    @Override
    default <V> Optional<V> getProperty(int x, int y, int z, Property<V> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getStore(property).getFor(new Location(getWorld(), x, y, z));
    }

    @Override
    default <V> Optional<V> getProperty(Vector3i coords, Direction direction, Property<V> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getStore(property).getFor(new Location(getWorld(), coords), direction);
    }

    @Override
    default <V> Optional<V> getProperty(int x, int y, int z, Direction direction, Property<V> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getStore(property).getFor(new Location(getWorld(), x, y, z), direction);
    }

    @Override
    default OptionalDouble getDoubleProperty(Vector3i coords, Property<Double> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getDoubleStore(property).getDoubleFor(new Location(getWorld(), coords));
    }

    @Override
    default OptionalDouble getDoubleProperty(int x, int y, int z, Property<Double> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getDoubleStore(property).getDoubleFor(new Location(getWorld(), x, y, z));
    }

    @Override
    default OptionalInt getIntProperty(Vector3i coords, Property<Integer> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getIntStore(property).getIntFor(new Location(getWorld(), coords));
    }

    @Override
    default OptionalInt getIntProperty(int x, int y, int z, Property<Integer> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        return SpongeImpl.getPropertyRegistry().getIntStore(property).getIntFor(new Location(getWorld(), x, y, z));
    }

    @Override
    default Collection<Direction> getFacesWithProperty(Vector3i coords, Property<?> property) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        final PropertyStore<?> store = Sponge.getPropertyRegistry().getStore(property);
        final Location loc = new Location(getWorld(), coords);
        final ImmutableList.Builder<Direction> faces = ImmutableList.builder();
        for (EnumFacing facing : EnumFacing.values()) {
            final Direction direction = DirectionFacingProvider.getInstance().getKey(facing).get();
            if (store.getFor(loc, direction).isPresent()) {
                faces.add(direction);
            }
        }
        return faces.build();
    }

    @Override
    default Collection<Direction> getFacesWithProperty(int x, int y, int z, Property<?> property) {
        return getFacesWithProperty(new Vector3i(x, y, z), property);
    }

    @Override
    default <E> Optional<E> get(int x, int y, int z, Key<? extends Value<E>> key) {
        checkState(shadow$getWorld() instanceof World, "Not a valid type of world!");
        final Optional<E> optional = getBlock(x, y, z).get(key);
        if (optional.isPresent()) {
            return optional;
        }
        final Optional<TileEntity> tileEntityOptional = getTileEntity(x, y, z);
        return tileEntityOptional.flatMap(tileEntity -> tileEntity.get(key));
    }

    @SuppressWarnings("unchecked")
    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> get(int x, int y, int z, Class<T> manipulatorClass) {
        final Collection<DataManipulator<?, ?>> manipulators = getManipulators(x, y, z);
        for (DataManipulator<?, ?> manipulator : manipulators) {
            if (manipulatorClass.isInstance(manipulator)) {
                return Optional.of((T) manipulator);
            }
        }
        return Optional.empty();
    }

    @Override
    default <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(int x, int y, int z, Class<T> manipulatorClass) {
        final Optional<T> optional = get(x, y, z, manipulatorClass);
        if (optional.isPresent()) {
            return optional;
        }
        final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
        return tileEntity.flatMap(tileEntity1 -> tileEntity1.getOrCreate(manipulatorClass));
    }

    @Override
    default <E, V extends Value<E>> Optional<V> getValue(int x, int y, int z, Key<V> key) {
        final BlockState blockState = getBlock(x, y, z);
        if (blockState.supports(key)) {
            return blockState.getValue(key);
        }
        final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
        if (tileEntity.isPresent() && tileEntity.get().supports(key)) {
            return tileEntity.get().getValue(key);
        }
        return Optional.empty();
    }

    @Override
    default boolean supports(int x, int y, int z, Key<?> key) {
        final BlockState blockState = getBlock(x, y, z);
        final boolean blockSupports = blockState.supports(key);
        final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
        final boolean tileEntitySupports = tileEntity.isPresent() && tileEntity.get().supports(key);
        return blockSupports || tileEntitySupports;
    }

    @Override
    default boolean supports(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        final BlockState blockState = getBlock(x, y, z);
        final List<ImmutableDataManipulator<?, ?>> immutableDataManipulators = blockState.getManipulators();
        boolean blockSupports = false;
        for (ImmutableDataManipulator<?, ?> manipulator : immutableDataManipulators) {
            if (manipulator.asMutable().getClass().isAssignableFrom(manipulatorClass)) {
                blockSupports = true;
                break;
            }
        }
        if (!blockSupports) {
            final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
            final boolean tileEntitySupports;
            tileEntitySupports = tileEntity.isPresent() && tileEntity.get().supports(manipulatorClass);
            return tileEntitySupports;
        }
        return true;
    }

    @Override
    default Set<Key<?>> getKeys(int x, int y, int z) {
        final ImmutableSet.Builder<Key<?>> builder = ImmutableSet.builder();
        final BlockState blockState = getBlock(x, y, z);
        builder.addAll(blockState.getKeys());
        final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
        tileEntity.ifPresent(tileEntity1 -> builder.addAll(tileEntity1.getKeys()));
        return builder.build();
    }

    @Override
    default Set<Value.Immutable<?>> getValues(int x, int y, int z) {
        final ImmutableSet.Builder<Value.Immutable<?>> builder = ImmutableSet.builder();
        final BlockState blockState = getBlock(x, y, z);
        builder.addAll(blockState.getValues());
        final Optional<TileEntity> tileEntity = getTileEntity(x, y, z);
        tileEntity.ifPresent(tileEntity1 -> builder.addAll(tileEntity1.getValues()));
        return builder.build();
    }

    @Override
    default <E> DataTransactionResult offer(int x, int y, int z, Key<? extends Value<E>> key, E value) {
        final BlockState blockState = getBlock(x, y, z);
        if (blockState.supports(key)) {
            Value.Immutable<E> old = ((Value.Mutable<E>) getValue(x, y, z, (Key) key).get()).asImmutable();
            setBlock(x, y, z, blockState.with(key, value).get());
            Value.Immutable<E> newVal = ((Value.Mutable<E>) getValue(x, y, z, (Key) key).get()).asImmutable();
            return DataTransactionResult.successReplaceResult(newVal, old);
        }
        return getTileEntity(x, y, z)
                .map(tileEntity ->  tileEntity.offer(key, value))
                .orElseGet(DataTransactionResult::failNoData);
    }

    @Override
    default DataTransactionResult offer(int x, int y, int z, DataManipulator<?, ?> manipulator, MergeFunction function) {
        final BlockState blockState = getBlock(x, y, z);
        final ImmutableDataManipulator<?, ?> immutableDataManipulator = manipulator.asImmutable();
        if (blockState.supports((Class) immutableDataManipulator.getClass())) {
            final List<Value.Immutable<?>> old = new ArrayList<>(blockState.getValues());
            final BlockState newState = blockState.with(immutableDataManipulator).get();
            old.removeAll(newState.getValues());
            setBlock(x, y, z, newState);
            return DataTransactionResult.successReplaceResult(old, manipulator.getValues());
        }
        return getTileEntity(x, y, z)
                .map(tileEntity -> tileEntity.offer(manipulator, function))
                .orElseGet(() -> DataTransactionResult.failResult(manipulator.getValues()));
    }

    @Override
    default DataTransactionResult remove(int x, int y, int z, Class<? extends DataManipulator<?, ?>> manipulatorClass) {
        final Optional<TileEntity> tileEntityOptional = getTileEntity(x, y, z);
        return tileEntityOptional
            .map(tileEntity -> tileEntity.remove(manipulatorClass))
            .orElseGet(DataTransactionResult::failNoData);
    }

    @Override
    default DataTransactionResult remove(int x, int y, int z, Key<?> key) {
        final Optional<TileEntity> tileEntityOptional = getTileEntity(x, y, z);
        return tileEntityOptional
            .map(tileEntity -> tileEntity.remove(key))
            .orElseGet(DataTransactionResult::failNoData);
    }

    @Override
    default DataTransactionResult undo(int x, int y, int z, DataTransactionResult result) {
        return DataTransactionResult.failNoData(); // todo
    }

    @Override
    default DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from) {
        return copyFrom(xTo, yTo, zTo, from, MergeFunction.IGNORE_ALL);
    }

    @Override
    default DataTransactionResult copyFrom(int xTo, int yTo, int zTo, DataHolder from, MergeFunction function) {
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        final Collection<DataManipulator<?, ?>> manipulators = from.getContainers();
        for (DataManipulator<?, ?> manipulator : manipulators) {
            builder.absorbResult(offer(xTo, yTo, zTo, manipulator, function));
        }
        return builder.build();
    }

    @Override
    default DataTransactionResult copyFrom(int xTo, int yTo, int zTo, int xFrom, int yFrom, int zFrom, MergeFunction function) {
        return copyFrom(xTo, yTo, zTo, new Location(getWorld(), xFrom, yFrom, zFrom), function);
    }

    @Override
    default Collection<DataManipulator<?, ?>> getManipulators(int x, int y, int z) {
        final List<DataManipulator<?, ?>> list = new ArrayList<>();
        final Collection<ImmutableDataManipulator<?, ?>> manipulators = getBlock(x, y, z)
            .getManipulators();
        for (ImmutableDataManipulator<?, ?> immutableDataManipulator : manipulators) {
            list.add(immutableDataManipulator.asMutable());
        }
        final Optional<TileEntity> optional = getTileEntity(x, y, z);
        optional
            .ifPresent(tileEntity -> list.addAll(tileEntity.getContainers()));
        return list;
    }

    @Override
    default boolean validateRawData(int x, int y, int z, DataView container) {
        return false; // todo
    }

    @Override
    default void setRawData(int x, int y, int z, DataView container) throws InvalidDataException {
        // todo
    }


}
