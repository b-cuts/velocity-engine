package org.apache.velocity.runtime.resource;

/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Velocity", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Iterator;

import org.apache.commons.collections.LRUMap;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;

/**
 * Default implementation of the resource cache for the default
 * ResourceManager.  The cache uses a <i>least recently used</i> (LRU)
 * algorithm, with a maximum size specified via the
 * <code>resource.manager.cache.size</code> property (idenfied by the
 * {@link
 * org.apache.velocity.runtime.RuntimeConstants#RESOURCE_MANAGER_CACHE_SIZE}
 * constant).  This property get be set to <code>0</code> or less for
 * a greedy, unbounded cache (the behavior from pre-v1.5).
 *
 * @author <a href="mailto:geirm@apache.org">Geir Magnusson Jr.</a>
 * @author <a href="mailto:dlr@finemaltcoding.com">Daniel Rall</a>
 * @version $Id: ResourceCacheImpl.java,v 1.3 2003/10/22 03:00:46 dlr Exp $
 */
public class ResourceCacheImpl implements ResourceCache
{
    /**
     * Cache storage, assumed to be thread-safe.
     */
    protected Map cache = new Hashtable();

    /**
     * Runtime services, generally initialized by the
     * <code>initialize()</code> method.
     */
    protected RuntimeServices rsvc = null;
    
    public void initialize( RuntimeServices rs )
    {
        rsvc = rs;

        int maxSize =
            rsvc.getInt(RuntimeConstants.RESOURCE_MANAGER_CACHE_SIZE, 89);
        if (maxSize > 0)
        {
            // Create a whole new Map here to avoid hanging on to a
            // handle to the unsynch'd LRUMap for our lifetime.
            Map lruCache = Collections.synchronizedMap(new LRUMap(maxSize));
            lruCache.putAll(cache);
            cache = lruCache;
        }
        rsvc.info("ResourceCache : initialized. (" + this.getClass() + ')');
    }
    
    public Resource get( Object key )
    {
        return (Resource) cache.get( key );
    }
    
    public Resource put( Object key, Resource value )
    {
        return (Resource) cache.put( key, value );
    }
    
    public Resource remove( Object key )
    {
        return (Resource) cache.remove( key );
    }
    
    public Iterator enumerateKeys()
    {
        return cache.keySet().iterator();
    }
}
