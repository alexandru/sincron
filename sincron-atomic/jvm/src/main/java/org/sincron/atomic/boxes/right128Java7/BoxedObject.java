/*
 * Copyright (c) 2016 by its authors. Some rights reserved.
 * See the project homepage at: https://sincron.org
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

package org.sincron.atomic.boxes.right128Java7;

import org.sincron.misc.UnsafeAccess;
import java.lang.reflect.Field;

abstract class BoxedObjectImpl implements org.sincron.atomic.boxes.BoxedObject {
    public volatile Object value;
    public static final long OFFSET;
    static {
        try {
            Field field = BoxedObjectImpl.class.getDeclaredField("value");
            OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(field);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public BoxedObjectImpl(Object initialValue) {
        this.value = initialValue;
    }

    public Object volatileGet() {
        return value;
    }

    public void volatileSet(Object update) {
        value = update;
    }

    public void lazySet(Object update) {
        UnsafeAccess.UNSAFE.putOrderedObject(this, OFFSET, update);
    }

    public boolean compareAndSet(Object current, Object update) {
        return UnsafeAccess.UNSAFE.compareAndSwapObject(this, OFFSET, current, update);
    }

    public Object getAndSet(Object update) {
        Object current = value;
        while (!UnsafeAccess.UNSAFE.compareAndSwapObject(this, OFFSET, current, update))
            current = value;
        return current;
    }
}

public final class BoxedObject extends BoxedObjectImpl {
    public volatile long p1, p2, p3, p4, p5, p6, p7, p8 = 7;
    public volatile long p9, p10, p11, p12, p13, p14 = 7;
    public long sum() {
        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 +
                p9 + p10 + p11 + p12 + p13 + p14;
    }

    public BoxedObject(Object initialValue) {
        super(initialValue);
    }
}
