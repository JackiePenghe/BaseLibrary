package com.sscl.baselibrary.utils;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 内存缓存工具类
 *
 * @author ALM
 */
public class MemoryCache {

    /*--------------------------------成员变量--------------------------------*/

    /**
     * 放入缓存时是个同步操作
     * LinkedHashMap构造方法的最后一个参数true代表这个map里的元素将按照最近使用次数由少到多排列，即LRU
     * 这样的好处是如果要将缓存中的元素替换，则先遍历出最近最少使用的元素来替换以提高效率
     */
    private Map<String, Bitmap> cache = Collections
            .synchronizedMap(new LinkedHashMap<String, Bitmap>(20, 1.5f, true));

    /**
     * 缓存中图片所占用的字节，初始0，将通过此变量严格控制缓存所占用的堆内存
     * current allocated size
     */
    private long size = 0;

    /**
     * 缓存只能占用的最大堆内存
     * max memory in bytes
     */
    private long limit = 1000000;

    /*--------------------------------构造方法--------------------------------*/

    /**
     * 构造器
     */
    public MemoryCache() {
        // use 25% of available heap size
        setLimit(Runtime.getRuntime().maxMemory() / 4);
    }

    /*--------------------------------私有方法--------------------------------*/

    /**
     * 设置内存可用大小
     *
     * @param newLimit 内存可用大小
     */
    private void setLimit(long newLimit) {
        limit = newLimit;
    }

    /**
     * 严格控制堆内存，如果超过将首先替换最近最少使用的那个图片缓存
     */
    private void checkSize() {
        if (size > limit) {
            // 先遍历最近最少使用的元素
            Iterator<Map.Entry<String, Bitmap>> iter = cache.entrySet()
                    .iterator();
            while (iter.hasNext()) {
                Map.Entry<String, Bitmap> entry = iter.next();
                size -= getSizeInBytes(entry.getValue());
                iter.remove();
                if (size <= limit) {
                    break;
                }
            }
        }
    }

    /**
     * 图片占用的内存
     *
     * @param bitmap 位图图片
     * @return 图片占用的内存大小
     */
    private long getSizeInBytes(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /*--------------------------------库内方法--------------------------------*/

    /**
     * 从链表里获取图片
     *
     * @param key 链表对应的key
     * @return 位图图片
     */
    @Nullable
    public Bitmap get(@NonNull String key) {
        try {
            if (!cache.containsKey(key)) {
                return null;
            }
            return cache.get(key);
        } catch (NullPointerException ex) {
            return null;
        }
    }

    /**
     * 往链表里添加一张图片
     *
     * @param key    链表对应的key
     * @param bitmap 位图图片
     */
    public void put(@NonNull String key,@NonNull Bitmap bitmap) {
        try {
            if (cache.containsKey(key)) {
                size -= getSizeInBytes(cache.get(key));
            }
            cache.put(key, bitmap);
            size += getSizeInBytes(bitmap);
            checkSize();
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    /**
     * 清除缓存
     */
    public void clear() {
        cache.clear();
    }
}