/**
 * Copyright (C) 2000-2010 Atomikos <info@atomikos.com>
 *
 * This code ("Atomikos TransactionsEssentials"), by itself,
 * is being distributed under the
 * Apache License, Version 2.0 ("License"), a copy of which may be found at
 * http://www.atomikos.com/licenses/apache-license-2.0.txt .
 * You may not use this file except in compliance with the License.
 *
 * While the License grants certain patent license rights,
 * those patent license rights only extend to the use of
 * Atomikos TransactionsEssentials by itself.
 *
 * This code (Atomikos TransactionsEssentials) contains certain interfaces
 * in package (namespace) com.atomikos.icatch
 * (including com.atomikos.icatch.Participant) which, if implemented, may
 * infringe one or more patents held by Atomikos.
 * It should be appreciated that you may NOT implement such interfaces;
 * licensing to implement these interfaces must be obtained separately from Atomikos.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */

package com.atomikos.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 *
 *
 *A filter for restricting which members (fields, constructors, methods)
 *are returned by the ClassInspector.
 */

public interface MemberFilter
{
    /**
     * Test if the filter accepts a field.
     * @param field  The field
     * @return  boolean True iff acceptable.
     */

    public boolean acceptsField ( Field field );

    /**
     * Test if the filter accepts a method.
     * @param method  The method.
     * @return  boolean True iff acceptable.
     */

    public boolean acceptsMethod ( Method method );

    /**
     * Test if the filter accepts this constructor.
     *
     * @param constructor  The constructor.
     * @return  boolean True iff acceptable.
     */

    public boolean acceptsConstructor ( Constructor constructor );
}
