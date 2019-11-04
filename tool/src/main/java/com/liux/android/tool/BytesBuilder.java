package com.liux.android.tool;

import java.util.Arrays;

/**
 * 仿造 {@link StringBuilder} 编写的 Byte 处理类
 */
public class BytesBuilder {

    private byte[] bytes;

    private int count;

    public BytesBuilder() {
        this(16);
    }

    public BytesBuilder(int capacity) {
        this.bytes = new byte[capacity];
        this.count = 0;
    }

    public BytesBuilder(byte... bytes) {
        this(bytes.length + 16);
        append(bytes);
    }

    /**
     * 添加字节数组
     * @param targetBytes
     */
    public BytesBuilder append(byte... targetBytes) {
        return append(0, targetBytes.length, targetBytes);
    }

    /**
     * 添加字节数组
     * @param targetOffset
     * @param targetCount
     * @param targetBytes
     * @return
     */
    public BytesBuilder append(int targetOffset, int targetCount, byte... targetBytes) {
        if (targetCount <= 0) return this;
        if (targetOffset < 0 || targetBytes.length < targetOffset + targetCount) throw new IndexOutOfBoundsException(
                "targetOffset " + targetOffset + ", targetCount " + targetCount + ", targetBytes.length " + targetBytes.length);

        ensureCapacityInternal(count + targetCount);

        System.arraycopy(targetBytes, targetOffset, bytes, count, targetCount);

        count += targetCount;

        return this;
    }

    /**
     * 插入字节数组
     * @param offset
     * @param targetBytes
     * @return
     */
    public BytesBuilder insert(int offset, byte... targetBytes) {
        return insert(offset, 0, targetBytes.length, targetBytes);
    }

    /**
     * 插入字节数组
     * @param offset
     * @param targetOffset
     * @param targetCount
     * @param targetBytes
     * @return
     */
    public BytesBuilder insert(int offset, int targetOffset, int targetCount, byte... targetBytes) {
        if (offset < 0) throw new IndexOutOfBoundsException("offset " + offset);
        if (targetOffset < 0 || targetBytes.length < targetOffset + targetCount) throw new IndexOutOfBoundsException(
                "targetOffset " + targetOffset + ", targetCount " + targetCount + ", targetBytes.length " + targetBytes.length);

        ensureCapacityInternal(count + targetCount);

        System.arraycopy(bytes, offset, bytes, offset + targetCount, count - offset);
        System.arraycopy(targetBytes, targetOffset, bytes, offset, targetCount);

        count += targetCount;

        return this;
    }

    /**
     * 删除字节
     * @param index
     * @return
     */
    public BytesBuilder deleteAt(int index) {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException("index " + index);

        System.arraycopy(bytes, index + 1, bytes, index, count - index - 1);

        count --;

        return this;
    }

    /**
     * 删除字节数组
     * @param start
     * @return
     */
    public BytesBuilder delete(int start) {
        return delete(start, count - start);
    }

    /**
     * 删除字节数组
     * @param start
     * @param count
     * @return
     */
    public BytesBuilder delete(int start, int count) {
        if (start < 0) throw new IndexOutOfBoundsException("start " + start);

        int end = start + count;
        if (end > this.count) {
            end = this.count;
            count = end - start;
        }
        if (start > end) throw new IndexOutOfBoundsException("start > length() or count < 0 ");

        if (count > 0) {
            System.arraycopy(bytes, start + count, bytes, start, this.count - end);
            this.count -= count;
        }
        return this;
    }

    /**
     * 替换字节
     * @param index
     * @param b
     * @return
     */
    public BytesBuilder replaceAt(int index, byte b) {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException("index " + index);

        bytes[index] = b;

        return this;
    }

    /**
     * 替换字节数组
     * @param start
     * @param count
     * @param targetBytes
     * @return
     */
    public BytesBuilder replace(int start, int count, byte... targetBytes) {
        return replace(start, count, 0, targetBytes);
    }

    /**
     * 替换字节数组
     * @param start
     * @param count
     * @param targetOffset
     * @param targetBytes
     * @return
     */
    public BytesBuilder replace(int start, int count, int targetOffset, byte... targetBytes) {
        if (start < 0) throw new IndexOutOfBoundsException("start " + start);

        int end = start + count;
        if (end > this.count) {
            end = this.count;
            count = end - start;
        }
        if (start > end) throw new IndexOutOfBoundsException("start > length or count < 0");

        if (count > 0) {
            if (targetOffset < 0) throw new IndexOutOfBoundsException("targetOffset " + targetOffset);
            if (targetBytes.length - targetOffset < count) throw new IndexOutOfBoundsException("targetBytes.length - targetOffset < count");

            System.arraycopy(targetBytes, targetOffset, bytes, start, count);
        }
        return this;
    }

    /**
     * 字节数组反转
     * @return
     */
    public BytesBuilder reverse() {
        if (count > 1) {
            byte[] newBytes = new byte[count];
            for (int i = 0; i < count; i++) {
                newBytes[count - i - 1] = bytes[i];
            }
            System.arraycopy(newBytes, 0, bytes, 0, count);
        }
        return this;
    }

    /**
     * 查找字节
     * @param b
     * @return
     */
    public int indexOf(byte b) {
        return indexOf(0, b);
    }

    /**
     * 查找字节
     * @param fromIndex
     * @param b
     * @return
     */
    public int indexOf(int fromIndex, byte b) {
        if (fromIndex < 0 || fromIndex >= count) throw new IndexOutOfBoundsException("fromIndex < 0 or fromIndex > max index");
        int result = -1;
        for (int i = fromIndex; i < count; i++) {
            if (bytes[i] == b) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 查找字节数组
     * @param targetBytes
     * @return
     */
    public int indexArrayOf(byte... targetBytes) {
        return indexArrayOf(0, targetBytes);
    }

    /**
     * 查找字节数组
     * @param fromIndex
     * @param targetBytes
     * @return
     */
    public int indexArrayOf(int fromIndex, byte... targetBytes) {
        if (fromIndex < 0 || fromIndex >= count) throw new IndexOutOfBoundsException("fromIndex < 0 or fromIndex > max index");
        int result = -1;
        if (targetBytes == null || targetBytes.length < 1) return result;
        int i, j, max = count - targetBytes.length;
        for (i = fromIndex; i <= max; i++) {
            if (bytes[i] == targetBytes[0]) {
                for (j = 1; j < targetBytes.length; j++) {
                    if (bytes[i + j] != targetBytes[j]) break;
                }
                if (j == targetBytes.length) {
                    result = i;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 倒序查找字节
     * @param b
     * @return
     */
    public int lastIndexOf(byte b) {
        return lastIndexOf(0, b);
    }

    /**
     * 倒序查找字节
     * @param fromIndex
     * @param b
     * @return
     */
    public int lastIndexOf(int fromIndex, byte b) {
        if (fromIndex < 0 || fromIndex >= count) throw new IndexOutOfBoundsException("fromIndex < 0 or fromIndex > max index");
        int result = -1;
        for (int i = count - fromIndex - 1; i >= 0; i--) {
            if (bytes[i] == b) {
                result = i;
                break;
            }
        }
        return result;
    }

    /**
     * 倒序查找字节数组
     * @param targetBytes
     * @return
     */
    public int lastIndexArrayOf(byte... targetBytes) {
        return lastIndexArrayOf(0, targetBytes);
    }

    /**
     * 倒序查找字节数组
     * @param fromIndex
     * @param targetBytes
     * @return
     */
    public int lastIndexArrayOf(int fromIndex, byte... targetBytes) {
        if (fromIndex < 0 || fromIndex >= count) throw new IndexOutOfBoundsException("fromIndex < 0 or fromIndex > max index");
        int result = -1;
        if (targetBytes == null || targetBytes.length < 1) return result;
        int i, j = 0, min = targetBytes.length;
        for (i = count - fromIndex - 1; i >= min; i--) {
            if (bytes[i] == targetBytes[targetBytes.length - 1]) {
                if (targetBytes.length > 1) {
                    for (j = targetBytes.length - 2; j >= 0; j--) {
                        if (bytes[i - j] != targetBytes[j]) break;
                    }
                }
                if (j == 0) {
                    result = i - targetBytes.length + 1;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 获取字节
     * @param index
     * @return
     */
    public byte byteAt(int index) {
        if (index < 0 || index >= count) throw new IndexOutOfBoundsException("index " + index);
        return bytes[index];
    }

    /**
     * 截取字节数组
     * @param start
     * @return
     */
    public byte[] subBytes(int start) {
        return subBytes(start, count - start);
    }

    /**
     * 截取字节数组
     * @param start
     * @param count
     * @return
     */
    public byte[] subBytes(int start, int count) {
        if (start < 0) throw new IndexOutOfBoundsException("start " + start);
        int end = start + count;
        if (end > this.count) {
            end = this.count;
            count = end - start;
        }
        if (start > end) throw new IndexOutOfBoundsException("start > length or count < 0");
        return Arrays.copyOfRange(bytes, start, start + count);
    }

    /**
     * 获取字节数组
     * @return
     */
    public byte[] toBytes() {
        if (count == 0) return new byte[0];
        return Arrays.copyOf(bytes, count);
    }

    /**
     * 获取字节数组的包装类数组
     * @return
     */
    public Byte[] toBytesWraps() {
        byte[] bytes = toBytes();
        Byte[] byteBoxs = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            byteBoxs[i] = bytes[i];
        }
        return byteBoxs;
    }

    /**
     * Attempts to reduce storage used for the byte sequence.
     * If the buffer is larger than necessary to hold its current sequence of
     * bytes, then it may be resized to become more space efficient.
     * Calling this method may, but is not required to, affect the value
     * returned by a subsequent call to the {@link #capacity()} method.
     */
    public void trimToSize() {
        if (count < bytes.length) {
            bytes = Arrays.copyOf(bytes, count);
        }
    }

    /**
     * Sets the length of the byte sequence.
     * The sequence is changed to a new byte sequence
     * whose length is specified by the argument. For every nonnegative
     * index <i>k</i> less than {@code newLength}, the byte at
     * index <i>k</i> in the new byte sequence is the same as the
     * byte at index <i>k</i> in the old sequence if <i>k</i> is less
     * than the length of the old byte sequence; otherwise, it is the
     * null byte {@code '0x00'}.
     *
     * In other words, if the {@code newLength} argument is less than
     * the current length, the length is changed to the specified length.
     * <p>
     * If the {@code newLength} argument is greater than or equal
     * to the current length, sufficient null bytes
     * ({@code '0x00'}) are appended so that
     * length becomes the {@code newLength} argument.
     * <p>
     * The {@code newLength} argument must be greater than or equal
     * to {@code 0}.
     *
     * @param      newLength   the new length
     * @throws     IndexOutOfBoundsException  if the
     *               {@code newLength} argument is negative.
     */
    public void setLength(int newLength) {
        if (newLength < 0)
            throw new StringIndexOutOfBoundsException(newLength);
        ensureCapacityInternal(newLength);

        if (count < newLength) {
            Arrays.fill(bytes, count, newLength, (byte) 0x00);
        }

        count = newLength;
    }

    /**
     * Returns the length (byte count).
     *
     * @return  the length of the sequence of bytes currently
     *          represented by this object
     */
    public int length() {
        return count;
    }

    /**
     * Returns the current capacity. The capacity is the amount of storage
     * available for newly inserted bytes, beyond which an allocation
     * will occur.
     *
     * @return  the current capacity
     */
    public int capacity() {
        return bytes.length;
    }

    /**
     * Ensures that the capacity is at least equal to the specified minimum.
     * If the current capacity is less than the argument, then a new internal
     * array is allocated with greater capacity. The new capacity is the
     * larger of:
     * <ul>
     * <li>The {@code minimumCapacity} argument.
     * <li>Twice the old capacity, plus {@code 2}.
     * </ul>
     * If the {@code minimumCapacity} argument is nonpositive, this
     * method takes no action and simply returns.
     * Note that subsequent operations on this object can reduce the
     * actual capacity below that requested here.
     *
     * @param   minimumCapacity   the minimum desired capacity.
     */
    public void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity > 0)
            ensureCapacityInternal(minimumCapacity);
    }

    /**
     * For positive values of {@code minimumCapacity}, this method
     * behaves like {@code ensureCapacity}, however it is never
     * synchronized.
     * If {@code minimumCapacity} is non positive due to numeric
     * overflow, this method throws {@code OutOfMemoryError}.
     */
    private void ensureCapacityInternal(int minimumCapacity) {
        // overflow-conscious code
        if (minimumCapacity - bytes.length > 0) {
            bytes = Arrays.copyOf(bytes,
                    newCapacity(minimumCapacity));
        }
    }

    /**
     * The maximum size of array to allocate (unless necessary).
     * Some VMs reserve some header words in an array.
     * Attempts to allocate larger arrays may result in
     * OutOfMemoryError: Requested array size exceeds VM limit
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    /**
     * Returns a capacity at least as large as the given minimum capacity.
     * Returns the current capacity increased by the same amount + 2 if
     * that suffices.
     * Will not return a capacity greater than {@code MAX_ARRAY_SIZE}
     * unless the given minimum capacity is greater than that.
     *
     * @param  minCapacity the desired minimum capacity
     * @throws OutOfMemoryError if minCapacity is less than zero or
     *         greater than Integer.MAX_VALUE
     */
    private int newCapacity(int minCapacity) {
        // overflow-conscious code
        int newCapacity = (bytes.length << 1) + 2;
        if (newCapacity - minCapacity < 0) {
            newCapacity = minCapacity;
        }
        return (newCapacity <= 0 || MAX_ARRAY_SIZE - newCapacity < 0)
                ? hugeCapacity(minCapacity)
                : newCapacity;
    }

    private int hugeCapacity(int minCapacity) {
        if (Integer.MAX_VALUE - minCapacity < 0) { // overflow
            throw new OutOfMemoryError();
        }
        return (minCapacity > MAX_ARRAY_SIZE)
                ? minCapacity : MAX_ARRAY_SIZE;
    }
}
