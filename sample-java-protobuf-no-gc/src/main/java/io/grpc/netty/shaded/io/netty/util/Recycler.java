package io.grpc.netty.shaded.io.netty.util;


import io.grpc.netty.shaded.io.netty.util.concurrent.FastThreadLocal;
import io.grpc.netty.shaded.io.netty.util.internal.MathUtil;
import io.grpc.netty.shaded.io.netty.util.internal.SystemPropertyUtil;
import io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLogger;
import io.grpc.netty.shaded.io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Recycler<T> {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(Recycler.class);
    private static final Recycler.Handle NOOP_HANDLE = new Recycler.Handle() {
        public void recycle(Object object) {
        }
    };
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(-2147483648);
    private static final int OWN_THREAD_ID;
    private static final int DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD = 4096;
    private static final int DEFAULT_MAX_CAPACITY_PER_THREAD;
    private static final int INITIAL_CAPACITY;
    private static final int MAX_SHARED_CAPACITY_FACTOR;
    private static final int MAX_DELAYED_QUEUES_PER_THREAD;
    private static final int LINK_CAPACITY;
    private static final int RATIO;
    private final int maxCapacityPerThread;
    private final int maxSharedCapacityFactor;
    private final int interval;
    private final int maxDelayedQueuesPerThread;
    private final FastThreadLocal<Recycler.Stack<T>> threadLocal;
    private static final FastThreadLocal<Map<Recycler.Stack<?>, Recycler.WeakOrderQueue>> DELAYED_RECYCLED;

    protected Recycler() {
        this(DEFAULT_MAX_CAPACITY_PER_THREAD);
    }

    protected Recycler(int maxCapacityPerThread) {
        this(maxCapacityPerThread, MAX_SHARED_CAPACITY_FACTOR);
    }

    protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor) {
        this(maxCapacityPerThread, maxSharedCapacityFactor, RATIO, MAX_DELAYED_QUEUES_PER_THREAD);
    }

    protected Recycler(int maxCapacityPerThread, int maxSharedCapacityFactor, int ratio, int maxDelayedQueuesPerThread) {
        this.threadLocal = new FastThreadLocal<Recycler.Stack<T>>() {
            protected Recycler.Stack<T> initialValue() {
                return new Recycler.Stack(Recycler.this, Thread.currentThread(), Recycler.this.maxCapacityPerThread, Recycler.this.maxSharedCapacityFactor, Recycler.this.interval, Recycler.this.maxDelayedQueuesPerThread);
            }

            protected void onRemoval(Recycler.Stack<T> value) {
                if (value.threadRef.get() == Thread.currentThread() && Recycler.DELAYED_RECYCLED.isSet()) {
                    ((Map)Recycler.DELAYED_RECYCLED.get()).remove(value);
                }

            }
        };
        this.interval = MathUtil.safeFindNextPositivePowerOfTwo(ratio);
        if (maxCapacityPerThread <= 0) {
            this.maxCapacityPerThread = 0;
            this.maxSharedCapacityFactor = 1;
            this.maxDelayedQueuesPerThread = 0;
        } else {
            this.maxCapacityPerThread = maxCapacityPerThread;
            this.maxSharedCapacityFactor = Math.max(1, maxSharedCapacityFactor);
            this.maxDelayedQueuesPerThread = Math.max(0, maxDelayedQueuesPerThread);
        }

    }

    public final T get() {
        if (this.maxCapacityPerThread == 0) {
            return (T)this.newObject(NOOP_HANDLE);
        } else {
            Recycler.Stack<T> stack = (Recycler.Stack)this.threadLocal.get();
            Recycler.DefaultHandle<T> handle = stack.pop();
            if (handle == null) {
                handle = stack.newHandle();
                handle.value = this.newObject(handle);
                Object o = handle.value;
                log(stack,"New Value",o);
            } else {
                Object o = handle.value;
                log(stack,"Return cached",o);

            }

            return (T)handle.value;
        }
    }

    static void log( Recycler.Stack s, String what, Object o){
        System.out.println((s==null?-1:System.identityHashCode(s))+"|"+new Date()+": "+what+" "+System.identityHashCode(o)+":"+o.getClass());

    }

    /** @deprecated */
    @Deprecated
    public final boolean recycle(T o, Recycler.Handle<T> handle) {
        if (handle == NOOP_HANDLE) {
            return false;
        } else {
            Recycler.DefaultHandle<T> h = (Recycler.DefaultHandle)handle;
            if (h.stack.parent != this) {
                log(h.stack,"Cannot recycle", o);
                return false;
            } else {
                log(h.stack,"Recycle", o);

                h.recycle(o);
                return true;
            }
        }
    }

    final int threadLocalCapacity() {
        return ((Recycler.Stack)this.threadLocal.get()).elements.length;
    }

    final int threadLocalSize() {
        return ((Recycler.Stack)this.threadLocal.get()).size;
    }

    protected abstract T newObject(Recycler.Handle<T> var1);

    static {
        OWN_THREAD_ID = ID_GENERATOR.getAndIncrement();
        int maxCapacityPerThread = SystemPropertyUtil.getInt("io.grpc.netty.shaded.io.netty.recycler.maxCapacityPerThread", SystemPropertyUtil.getInt("io.grpc.netty.shaded.io.netty.recycler.maxCapacity", 4096));
        if (maxCapacityPerThread < 0) {
            maxCapacityPerThread = 4096;
        }

        DEFAULT_MAX_CAPACITY_PER_THREAD = maxCapacityPerThread;
        MAX_SHARED_CAPACITY_FACTOR = Math.max(2, SystemPropertyUtil.getInt("io.grpc.netty.shaded.io.netty.recycler.maxSharedCapacityFactor", 2));
        MAX_DELAYED_QUEUES_PER_THREAD = Math.max(0, SystemPropertyUtil.getInt("io.grpc.netty.shaded.io.netty.recycler.maxDelayedQueuesPerThread", NettyRuntime.availableProcessors() * 2));
        LINK_CAPACITY = MathUtil.safeFindNextPositivePowerOfTwo(Math.max(SystemPropertyUtil.getInt("io.grpc.netty.shaded.io.netty.recycler.linkCapacity", 16), 16));
        RATIO = MathUtil.safeFindNextPositivePowerOfTwo(SystemPropertyUtil.getInt("io.grpc.netty.shaded.io.netty.recycler.ratio", 8));
        if (logger.isDebugEnabled()) {
            if (DEFAULT_MAX_CAPACITY_PER_THREAD == 0) {
                logger.debug("-Dio.netty.recycler.maxCapacityPerThread: disabled");
                logger.debug("-Dio.netty.recycler.maxSharedCapacityFactor: disabled");
                logger.debug("-Dio.netty.recycler.linkCapacity: disabled");
                logger.debug("-Dio.netty.recycler.ratio: disabled");
            } else {
                logger.debug("-Dio.netty.recycler.maxCapacityPerThread: {}", DEFAULT_MAX_CAPACITY_PER_THREAD);
                logger.debug("-Dio.netty.recycler.maxSharedCapacityFactor: {}", MAX_SHARED_CAPACITY_FACTOR);
                logger.debug("-Dio.netty.recycler.linkCapacity: {}", LINK_CAPACITY);
                logger.debug("-Dio.netty.recycler.ratio: {}", RATIO);
            }
        }

        INITIAL_CAPACITY = Math.min(DEFAULT_MAX_CAPACITY_PER_THREAD, 256);
        DELAYED_RECYCLED = new FastThreadLocal<Map<Recycler.Stack<?>, Recycler.WeakOrderQueue>>() {
            protected Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> initialValue() {
                return new WeakHashMap();
            }
        };
    }

    private static final class Stack<T> {
        final Recycler<T> parent;
        final WeakReference<Thread> threadRef;
        final AtomicInteger availableSharedCapacity;
        private final int maxDelayedQueues;
        private final int maxCapacity;
        private final int interval;
        Recycler.DefaultHandle<?>[] elements;
        int size;
        private int handleRecycleCount;
        private Recycler.WeakOrderQueue cursor;
        private Recycler.WeakOrderQueue prev;
        private volatile Recycler.WeakOrderQueue head;

        Stack(Recycler<T> parent, Thread thread, int maxCapacity, int maxSharedCapacityFactor, int interval, int maxDelayedQueues) {
            this.parent = parent;
            this.threadRef = new WeakReference(thread);
            this.maxCapacity = maxCapacity;
            this.availableSharedCapacity = new AtomicInteger(Math.max(maxCapacity / maxSharedCapacityFactor, Recycler.LINK_CAPACITY));
            this.elements = new Recycler.DefaultHandle[Math.min(Recycler.INITIAL_CAPACITY, maxCapacity)];
            this.interval = interval;
            this.handleRecycleCount = interval;
            this.maxDelayedQueues = maxDelayedQueues;
        }

        synchronized void setHead(Recycler.WeakOrderQueue queue) {
            queue.setNext(this.head);
            this.head = queue;
        }

        int increaseCapacity(int expectedCapacity) {
            int newCapacity = this.elements.length;
            int maxCapacity = this.maxCapacity;

            do {
                newCapacity <<= 1;
            } while(newCapacity < expectedCapacity && newCapacity < maxCapacity);

            newCapacity = Math.min(newCapacity, maxCapacity);
            if (newCapacity != this.elements.length) {
                this.elements = (Recycler.DefaultHandle[])Arrays.copyOf(this.elements, newCapacity);
            }

            return newCapacity;
        }

        Recycler.DefaultHandle<T> pop() {
            int size = this.size;
            if (size == 0) {
                if (!this.scavenge()) {
                    return null;
                }

                size = this.size;
                if (size <= 0) {
                    return null;
                }
            }

            --size;
            Recycler.DefaultHandle ret = this.elements[size];
            this.elements[size] = null;
            this.size = size;
            if (ret.lastRecycledId != ret.recycleId) {
                throw new IllegalStateException("recycled multiple times");
            } else {
                ret.recycleId = 0;
                ret.lastRecycledId = 0;
                return ret;
            }
        }

        private boolean scavenge() {
            if (this.scavengeSome()) {
                return true;
            } else {
                this.prev = null;
                this.cursor = this.head;
                return false;
            }
        }

        private boolean scavengeSome() {
            Recycler.WeakOrderQueue cursor = this.cursor;
            Recycler.WeakOrderQueue prev;
            if (cursor == null) {
                prev = null;
                cursor = this.head;
                if (cursor == null) {
                    return false;
                }
            } else {
                prev = this.prev;
            }

            boolean success = false;

            Recycler.WeakOrderQueue next;
            do {
                if (cursor.transfer(this)) {
                    success = true;
                    break;
                }

                next = cursor.getNext();
                if (cursor.get() == null) {
                    if (cursor.hasFinalData()) {
                        while(cursor.transfer(this)) {
                            success = true;
                        }
                    }

                    if (prev != null) {
                        cursor.reclaimAllSpaceAndUnlink();
                        prev.setNext(next);
                    }
                } else {
                    prev = cursor;
                }

                cursor = next;
            } while(next != null && !success);

            this.prev = prev;
            this.cursor = cursor;
            return success;
        }

        void push(Recycler.DefaultHandle<?> item) {
            Thread currentThread = Thread.currentThread();
            if (this.threadRef.get() == currentThread) {
                this.pushNow(item);
            } else {
                this.pushLater(item, currentThread);
            }

        }

        private void pushNow(Recycler.DefaultHandle<?> item) {
            if ((item.recycleId | item.lastRecycledId) != 0) {
                throw new IllegalStateException("recycled already");
            } else {
                item.recycleId = item.lastRecycledId = Recycler.OWN_THREAD_ID;
                int size = this.size;
                if (size < this.maxCapacity && !this.dropHandle(item)) {
                    if (size == this.elements.length) {
                        this.elements = (Recycler.DefaultHandle[])Arrays.copyOf(this.elements, Math.min(size << 1, this.maxCapacity));
                    }

                    this.elements[size] = item;
                    this.size = size + 1;
                }
            }
        }

        private void pushLater(Recycler.DefaultHandle<?> item, Thread thread) {
            if (this.maxDelayedQueues != 0) {
                Map<Recycler.Stack<?>, Recycler.WeakOrderQueue> delayedRecycled = (Map)Recycler.DELAYED_RECYCLED.get();
                Recycler.WeakOrderQueue queue = (Recycler.WeakOrderQueue)delayedRecycled.get(this);
                if (queue == null) {
                    if (delayedRecycled.size() >= this.maxDelayedQueues) {
                        delayedRecycled.put(this, Recycler.WeakOrderQueue.DUMMY);
                        return;
                    }

                    if ((queue = this.newWeakOrderQueue(thread)) == null) {
                        return;
                    }

                    delayedRecycled.put(this, queue);
                } else if (queue == Recycler.WeakOrderQueue.DUMMY) {
                    return;
                }

                queue.add(item);
            }
        }

        private Recycler.WeakOrderQueue newWeakOrderQueue(Thread thread) {
            return Recycler.WeakOrderQueue.newQueue(this, thread);
        }

        boolean dropHandle(Recycler.DefaultHandle<?> handle) {
            if (!handle.hasBeenRecycled) {
                if (this.handleRecycleCount < this.interval) {
                    ++this.handleRecycleCount;
                    return true;
                }

                this.handleRecycleCount = 0;
                handle.hasBeenRecycled = true;
            }

            return false;
        }

        Recycler.DefaultHandle<T> newHandle() {
            return new Recycler.DefaultHandle(this);
        }
    }

    private static final class WeakOrderQueue extends WeakReference<Thread> {
        static final Recycler.WeakOrderQueue DUMMY = new Recycler.WeakOrderQueue();
        private final Recycler.WeakOrderQueue.Head head;
        private Recycler.WeakOrderQueue.Link tail;
        private Recycler.WeakOrderQueue next;
        private final int id;
        private final int interval;
        private int handleRecycleCount;

        private WeakOrderQueue() {
            super(null);
            this.id = Recycler.ID_GENERATOR.getAndIncrement();
            this.head = new Recycler.WeakOrderQueue.Head((AtomicInteger)null);
            this.interval = 0;
        }

        private WeakOrderQueue(Recycler.Stack<?> stack, Thread thread) {
            super(thread);
            this.id = Recycler.ID_GENERATOR.getAndIncrement();
            this.tail = new Recycler.WeakOrderQueue.Link();
            this.head = new Recycler.WeakOrderQueue.Head(stack.availableSharedCapacity);
            this.head.link = this.tail;
            this.interval = stack.interval;
            this.handleRecycleCount = this.interval;
        }

        static Recycler.WeakOrderQueue newQueue(Recycler.Stack<?> stack, Thread thread) {
            if (!Recycler.WeakOrderQueue.Head.reserveSpaceForLink(stack.availableSharedCapacity)) {
                return null;
            } else {
                Recycler.WeakOrderQueue queue = new Recycler.WeakOrderQueue(stack, thread);
                stack.setHead(queue);
                return queue;
            }
        }

        Recycler.WeakOrderQueue getNext() {
            return this.next;
        }

        void setNext(Recycler.WeakOrderQueue next) {
            assert next != this;

            this.next = next;
        }

        void reclaimAllSpaceAndUnlink() {
            this.head.reclaimAllSpaceAndUnlink();
            this.next = null;
        }

        void add(Recycler.DefaultHandle<?> handle) {
            handle.lastRecycledId = this.id;
            if (this.handleRecycleCount < this.interval) {
                ++this.handleRecycleCount;
            } else {
                this.handleRecycleCount = 0;
                Recycler.WeakOrderQueue.Link tail = this.tail;
                int writeIndex;
                if ((writeIndex = tail.get()) == Recycler.LINK_CAPACITY) {
                    Recycler.WeakOrderQueue.Link link = this.head.newLink();
                    if (link == null) {
                        return;
                    }

                    this.tail = tail = tail.next = link;
                    writeIndex = tail.get();
                }

                tail.elements[writeIndex] = handle;
                handle.stack = null;
                tail.lazySet(writeIndex + 1);
            }
        }

        boolean hasFinalData() {
            return this.tail.readIndex != this.tail.get();
        }

        boolean transfer(Recycler.Stack<?> dst) {
            Recycler.WeakOrderQueue.Link head = this.head.link;
            if (head == null) {
                return false;
            } else {
                if (head.readIndex == Recycler.LINK_CAPACITY) {
                    if (head.next == null) {
                        return false;
                    }

                    head = head.next;
                    this.head.relink(head);
                }

                int srcStart = head.readIndex;
                int srcEnd = head.get();
                int srcSize = srcEnd - srcStart;
                if (srcSize == 0) {
                    return false;
                } else {
                    int dstSize = dst.size;
                    int expectedCapacity = dstSize + srcSize;
                    if (expectedCapacity > dst.elements.length) {
                        int actualCapacity = dst.increaseCapacity(expectedCapacity);
                        srcEnd = Math.min(srcStart + actualCapacity - dstSize, srcEnd);
                    }

                    if (srcStart != srcEnd) {
                        Recycler.DefaultHandle[] srcElems = head.elements;
                        Recycler.DefaultHandle[] dstElems = dst.elements;
                        int newDstSize = dstSize;

                        for(int i = srcStart; i < srcEnd; ++i) {
                            Recycler.DefaultHandle<?> element = srcElems[i];
                            if (element.recycleId == 0) {
                                element.recycleId = element.lastRecycledId;
                            } else if (element.recycleId != element.lastRecycledId) {
                                throw new IllegalStateException("recycled already");
                            }

                            srcElems[i] = null;
                            if (!dst.dropHandle(element)) {
                                element.stack = dst;
                                dstElems[newDstSize++] = element;
                            }
                        }

                        if (srcEnd == Recycler.LINK_CAPACITY && head.next != null) {
                            this.head.relink(head.next);
                        }

                        head.readIndex = srcEnd;
                        if (dst.size == newDstSize) {
                            return false;
                        } else {
                            dst.size = newDstSize;
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
            }
        }

        private static final class Head {
            private final AtomicInteger availableSharedCapacity;
            Recycler.WeakOrderQueue.Link link;

            Head(AtomicInteger availableSharedCapacity) {
                this.availableSharedCapacity = availableSharedCapacity;
            }

            void reclaimAllSpaceAndUnlink() {
                Recycler.WeakOrderQueue.Link head = this.link;
                this.link = null;

                int reclaimSpace;
                Recycler.WeakOrderQueue.Link next;
                for(reclaimSpace = 0; head != null; head = next) {
                    reclaimSpace += Recycler.LINK_CAPACITY;
                    next = head.next;
                    head.next = null;
                }

                if (reclaimSpace > 0) {
                    this.reclaimSpace(reclaimSpace);
                }

            }

            private void reclaimSpace(int space) {
                this.availableSharedCapacity.addAndGet(space);
            }

            void relink(Recycler.WeakOrderQueue.Link link) {
                this.reclaimSpace(Recycler.LINK_CAPACITY);
                this.link = link;
            }

            Recycler.WeakOrderQueue.Link newLink() {
                return reserveSpaceForLink(this.availableSharedCapacity) ? new Recycler.WeakOrderQueue.Link() : null;
            }

            static boolean reserveSpaceForLink(AtomicInteger availableSharedCapacity) {
                int available;
                do {
                    available = availableSharedCapacity.get();
                    if (available < Recycler.LINK_CAPACITY) {
                        return false;
                    }
                } while(!availableSharedCapacity.compareAndSet(available, available - Recycler.LINK_CAPACITY));

                return true;
            }
        }

        static final class Link extends AtomicInteger {
            final Recycler.DefaultHandle<?>[] elements;
            int readIndex;
            Recycler.WeakOrderQueue.Link next;

            Link() {
                this.elements = new Recycler.DefaultHandle[Recycler.LINK_CAPACITY];
            }
        }
    }

    private static final class DefaultHandle<T> implements Recycler.Handle<T> {
        int lastRecycledId;
        int recycleId;
        boolean hasBeenRecycled;
        Recycler.Stack<?> stack;
        Object value;

        DefaultHandle(Recycler.Stack<?> stack) {
            this.stack = stack;
        }

        public void recycle(Object object) {
            if (object != this.value) {
                throw new IllegalArgumentException("object does not belong to handle");
            } else {
                log(this.stack, "Recycle", object);

                Recycler.Stack<?> stack = this.stack;
                if (this.lastRecycledId == this.recycleId && stack != null) {
                    stack.push(this);
                } else {
                    throw new IllegalStateException("recycled already");
                }
            }
        }
    }

    public interface Handle<T> extends io.grpc.netty.shaded.io.netty.util.internal.ObjectPool.Handle<T> {
    }
}
