package org.apache.velocity.runtime.parser.node;
/*
 * Copyright 2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

import java.lang.reflect.InvocationTargetException;

import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.util.introspection.Introspector;


/**
 * Executor that simply tries to execute a put(key, value)
 * operation. This will try to find a put(key) method
 * for any type of object, not just objects that
 * implement the Map interface as was previously
 * the case.
 *
 * @author <a href="mailto:jvanzyl@apache.org">Jason van Zyl</a>
 * @author <a href="mailto:henning@apache.org">Henning P. Schmiedehausen</a>
 * @version $Id$
 */
public class PutExecutor extends SetExecutor
{
    private final Introspector introspector;
    private final String property;

    public PutExecutor(final Log log, final Introspector introspector,
            final Class clazz, final Object arg, final String property)
    {
        this.log = log;
        this.introspector = introspector;
        this.property = property;

        discover(clazz, arg);
    }

    protected void discover(final Class clazz, final Object arg)
    {
        Object [] params;

        // If you passed in null as property, we don't use the value
        // for parameter lookup. Instead we just look for put(Object) without
        // any parameters.
        //
        // In any other case, the following condition will set up an array
        // for looking up put(String, Object) on the class.

        if (property == null)
        {
            // The passed in arg object is used by the Cache to look up the method.
            params = new Object[] { arg };
        }
        else
        {
            params = new Object[] { property, arg };
        }

        try
        {
            setMethod(introspector.getMethod(clazz, "put", params));
        }
        /**
         * pass through application level runtime exceptions
         */
        catch( RuntimeException e )
        {
            throw e;
        }
        catch(Exception e)
        {
            log.error("While looking for put('" + params[0] + "') method:", e);
        }
    }

    /**
     * Execute method against context.
     */
    public Object execute(final Object o, final Object value)
        throws IllegalAccessException,  InvocationTargetException
    {
        Object [] params;

        if (isAlive())
        {
            // If property != null, pass in the name for put(key, value). Else just put(value).
            if (property == null)
            {
                params = new Object [] { value };
            }
            else
            {
                params = new Object [] { property, value };
            }

            return getMethod().invoke(o, params);
        }

        return null;
    }
}