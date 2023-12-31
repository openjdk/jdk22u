/*
 * Copyright (c) 2015, 2023, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BooleanSupplier;

import jdk.internal.logger.BootstrapLogger;

public final class BootstrapLoggerUtils {

    private static final Field IS_BOOTED;
    private static final Method AWAIT_PENDING;

    static {
        try {
            IS_BOOTED = BootstrapLogger.class.getDeclaredField("isBooted");
            IS_BOOTED.setAccessible(true);
            // private reflection hook that allows us to test wait until all
            // the tasks pending in the BootstrapExecutor are finished.
            AWAIT_PENDING = BootstrapLogger.class.getDeclaredMethod("awaitPendingTasks");
            AWAIT_PENDING.setAccessible(true);
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static void setBootedHook(BooleanSupplier supplier) throws IllegalAccessException {
        IS_BOOTED.set(null, supplier);
    }

    public static void awaitPending() {
        try {
            AWAIT_PENDING.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
            ex.printStackTrace();
        }
    }
}
