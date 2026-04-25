package net.alex9849.cocktailpi.utils;

import com.pi4j.context.Context;
import com.pi4j.io.i2c.I2C;
import com.pi4j.io.i2c.I2CConfig;
import com.pi4j.io.i2c.I2CRegister;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

/**
 * A wrapper for I2C that synchronizes all operations on a global lock.
 * This is used to prevent "java.lang.RuntimeException: Could not obtain an access-lock!"
 * which can occur in Pi4J v2 when multiple threads access the same I2C bus concurrently.
 */
public class SynchronizedI2C implements I2C {
    private final I2C delegate;
    private final Object busLock;

    public SynchronizedI2C(I2C delegate, Object busLock) {
        this.delegate = delegate;
        this.busLock = busLock;
    }

    @Override
    public int write(byte b) {
        synchronized (busLock) {
            return delegate.write(b);
        }
    }

    @Override
    public int write(byte[] data, int offset, int length) {
        synchronized (busLock) {
            return delegate.write(data, offset, length);
        }
    }

    @Override
    public int read() {
        synchronized (busLock) {
            return delegate.read();
        }
    }

    @Override
    public int read(byte[] buffer, int offset, int length) {
        synchronized (busLock) {
            return delegate.read(buffer, offset, length);
        }
    }

    @Override
    public int writeRead(byte[] writeBuffer, int writeOffset, int writeLength, byte[] readBuffer, int readOffset, int readLength) {
        synchronized (busLock) {
            return delegate.writeRead(writeBuffer, writeOffset, writeLength, readBuffer, readOffset, readLength);
        }
    }

    @Override
    public I2CRegister getRegister(int address) {
        synchronized (busLock) {
            return delegate.getRegister(address);
        }
    }

    @Override
    public <T> T execute(Callable<T> action) {
        synchronized (busLock) {
            return delegate.execute(action);
        }
    }

    @Override
    public I2CConfig config() {
        return delegate.config();
    }

    @Override
    public String id() {
        return delegate.id();
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public I2C name(String name) {
        synchronized (busLock) {
            return (I2C) delegate.name(name);
        }
    }

    @Override
    public String description() {
        return delegate.description();
    }

    @Override
    public I2C description(String description) {
        synchronized (busLock) {
            return (I2C) delegate.description(description);
        }
    }

    @Override
    public com.pi4j.io.i2c.I2CProvider provider() {
        return delegate.provider();
    }

    @Override
    public I2C initialize(Context context) {
        synchronized (busLock) {
            return (I2C) delegate.initialize(context);
        }
    }

    @Override
    public I2C shutdown(Context context) {
        synchronized (busLock) {
            return (I2C) delegate.shutdown(context);
        }
    }

    @Override
    public boolean isOpen() {
        synchronized (busLock) {
            return delegate.isOpen();
        }
    }

    @Override
    public com.pi4j.common.Metadata metadata() {
        return delegate.metadata();
    }

    @Override
    public void close() {
        synchronized (busLock) {
            try {
                delegate.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int readRegister(int register) {
        synchronized (busLock) {
            return delegate.readRegister(register);
        }
    }

    @Override
    public int readRegister(byte[] register, byte[] buffer, int offset, int length) {
        synchronized (busLock) {
            return delegate.readRegister(register, buffer, offset, length);
        }
    }

    @Override
    public int readRegister(int register, byte[] buffer, int offset, int length) {
        synchronized (busLock) {
            return delegate.readRegister(register, buffer, offset, length);
        }
    }

    @Override
    public int writeRegister(int register, byte b) {
        synchronized (busLock) {
            return delegate.writeRegister(register, b);
        }
    }

    @Override
    public int writeRegister(int register, byte[] data, int offset, int length) {
        synchronized (busLock) {
            return delegate.writeRegister(register, data, offset, length);
        }
    }

    @Override
    public int writeRegister(byte[] register, byte[] data, int offset, int length) {
        synchronized (busLock) {
            return delegate.writeRegister(register, data, offset, length);
        }
    }
}
