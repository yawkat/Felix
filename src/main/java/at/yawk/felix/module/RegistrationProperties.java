/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package at.yawk.felix.module;

import lombok.*;
import lombok.experimental.Wither;

/**
 * Settings used for registering new modules.
 *
 * @author Jonas Konrad (yawkat)
 */
@Getter(AccessLevel.PACKAGE)
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RegistrationProperties {
    static RegistrationProperties defaults = new RegistrationProperties();
    static RegistrationProperties anonymous =
            defaults.withDuplicateFinder(DuplicateFinder.IGNORE_DUPLICATE_CLASSES_ALLOW_DIRECT);

    private DependencyResolutionStrategy dependencyResolutionStrategy = DependencyResolutionStrategy.REGISTER;
    @Wither(AccessLevel.PACKAGE) private DuplicateFinder duplicateFinder = DuplicateFinder.IGNORE_DUPLICATE_CLASSES;
}
