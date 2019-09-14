package net.amoebaman.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.Validate;

public final class ArrayWrapper<E>
{
    private E[] _array;
    
    public ArrayWrapper(final E... elements) {
        this.setArray(elements);
    }
    
    public E[] getArray() {
        return this._array;
    }
    
    public void setArray(final E[] array) {
        Validate.notNull((Object)array, "The array must not be null.");
        this._array = array;
    }
    
    @Override
    public boolean equals(final Object other) {
        return other instanceof ArrayWrapper && Arrays.equals(this._array, ((ArrayWrapper)other)._array);
    }
    
    @Override
    public int hashCode() {
        return Arrays.hashCode(this._array);
    }
    
    public static <T> T[] toArray(final Iterable<? extends T> list, final Class<T> c) {
        int size = -1;
        if (list instanceof Collection) {
            final Collection coll = (Collection)list;
            size = coll.size();
        }
        if (size < 0) {
            size = 0;
            for (final T element : list) {
                ++size;
            }
        }
        final Object[] result = (Object[])Array.newInstance(c, size);
        int i = 0;
        for (final T element2 : list) {
            result[i++] = element2;
        }
        return (T[])result;
    }
}
