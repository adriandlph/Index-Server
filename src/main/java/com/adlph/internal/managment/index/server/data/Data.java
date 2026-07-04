package com.adlph.internal.managment.index.server.data;

import com.adlph.internal.managment.index.server.exception.InvalidDataException;

public interface Data {
    public void validateData() throws InvalidDataException;
}
