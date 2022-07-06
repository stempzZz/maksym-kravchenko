package com.epam.spring.time_tracking.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "Password must be at least 8 characters, contain numbers and Latin letters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
