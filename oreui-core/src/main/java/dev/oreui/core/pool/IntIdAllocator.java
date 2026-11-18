package dev.oreui.core.pool;

import java.util.Arrays;

public final class IntIdAllocator {
    private int[] free = new int[16];
    private boolean[] used = new boolean[16];
    private int freeSize;
    private int next;

    public int allocate() {
        if (freeSize > 0) {
            int id = free[--freeSize];
            used[id] = true;
            return id;
        }
        int id = next++;
        if (id >= used.length) {
            used = Arrays.copyOf(used, used.length * 2);
        }
        used[id] = true;
        return id;
    }

    public void release(int id) {
        if (id < 0 || id >= next || !used[id]) {
            throw new IllegalArgumentException("id is not allocated: " + id);
        }
        used[id] = false;
        if (freeSize >= free.length) {
            free = Arrays.copyOf(free, free.length * 2);
        }
        free[freeSize++] = id;
    }

    public boolean isAllocated(int id) {
        return id >= 0 && id < next && used[id];
    }

    public int highWaterMark() {
        return next;
    }
}