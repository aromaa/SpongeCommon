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
package org.spongepowered.common.registry.type.block;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.registry.AlternateCatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.state.IntegerStateProperties;
import org.spongepowered.api.state.IntegerStateProperty;
import org.spongepowered.common.registry.AbstractCatalogRegistryModule;
import org.spongepowered.common.registry.SpongeAdditionalCatalogRegistryModule;

import java.util.Locale;

@RegisterCatalog(IntegerStateProperties.class)
public final class IntegerStatePropertyRegistryModule
    extends AbstractCatalogRegistryModule<IntegerStateProperty>
        implements SpongeAdditionalCatalogRegistryModule<IntegerStateProperty>, AlternateCatalogRegistryModule<IntegerStateProperty> {

    public static IntegerStatePropertyRegistryModule getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public boolean allowsApiRegistration() {
        return false;
    }

    @Override
    public void registerAdditionalCatalog(IntegerStateProperty extraCatalog) {
        this.register(extraCatalog);
    }

    public void registerBlock(CatalogKey id, BlockType block, IntegerStateProperty property) {
        checkNotNull(id, "Id was null!");
        this.map.put(id, property);
        final String propertyId = block.getKey().toString().toLowerCase(Locale.ENGLISH) + "_" + property.getName().toLowerCase(Locale.ENGLISH);
        this.map.put(CatalogKey.resolve(propertyId), property);
    }

    private static final class Holder {
        final static IntegerStatePropertyRegistryModule INSTANCE = new IntegerStatePropertyRegistryModule();
    }
}