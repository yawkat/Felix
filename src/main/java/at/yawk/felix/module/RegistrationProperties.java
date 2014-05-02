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
