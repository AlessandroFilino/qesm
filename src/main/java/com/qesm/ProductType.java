package com.qesm;

import java.util.UUID;

public interface ProductType {
    public String getNameType();
    public UUID getUuid();
    public int getQuantityProduced();
    public int setQuantityProduced(int quantityProduced);
}
