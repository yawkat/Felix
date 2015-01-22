/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module;

/**
 * @author yawkat
 */
public class InvalidModuleException extends Error {
    public InvalidModuleException() {}

    public InvalidModuleException(String message) {
        super(message);
    }

    public InvalidModuleException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidModuleException(Throwable cause) {
        super(cause);
    }
}
