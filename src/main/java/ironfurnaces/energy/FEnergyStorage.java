/*
 * Copyright 2025 pizzaatime and XenoMustache
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ironfurnaces.energy;

import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;

public class FEnergyStorage extends SimpleEnergyHandler {

    public FEnergyStorage(int capacity) {
        super(capacity);
    }

    public FEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public FEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public FEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    protected void onEnergyChanged(int previousAmount) {
    }

    public int getEnergy() {
        return this.getAmountAsInt();
    }

    public int getCapacity() {
        return this.getCapacityAsInt();
    }

    public FEnergyStorage setCapacity(int capacity) {
        int previous = this.energy;
        this.capacity = capacity;
        if (energy > capacity) {
            energy = capacity;
        }
        onEnergyChanged(previous);
        return this;
    }

    public FEnergyStorage setMaxTransfer(int maxTransfer) {
        setMaxReceive(maxTransfer);
        setMaxExtract(maxTransfer);
        return this;
    }

    public FEnergyStorage setMaxReceive(int maxReceive) {
        this.maxInsert = maxReceive;
        return this;
    }

    public FEnergyStorage setMaxExtract(int maxExtract) {
        this.maxExtract = maxExtract;
        return this;
    }

    public int getMaxReceive() {
        return maxInsert;
    }

    public int getMaxExtract() {
        return maxExtract;
    }

    public void setEnergy(int energy) {
        if (energy > capacity) {
            energy = capacity;
        } else if (energy < 0) {
            energy = 0;
        }
        set(energy);
    }

    public void setCapacityDirectly(int capacity) {
        this.capacity = capacity;
    }

    public void setEnergyDirectly(int energy) {
        this.energy = energy;
    }

    public boolean canReceiveEnergy() {
        return maxInsert > 0;
    }

    public boolean canExtractEnergy() {
        return maxExtract > 0;
    }
}