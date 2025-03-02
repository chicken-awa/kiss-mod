package org.kissmod.kissMod.extension;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ByteArrayDataExtension {

    private ByteArrayDataExtension() {
        throw new IllegalStateException("Extension class");
    }

    public static UUID readUuid(@NotNull ByteArrayDataInput in) {
        return new UUID(in.readLong(), in.readLong());
    }

    public static <T extends ByteArrayDataOutput> void writeUuid(T out, UUID uuid) {
        out.writeLong(uuid.getMostSignificantBits());
        out.writeLong(uuid.getLeastSignificantBits());
    }
}