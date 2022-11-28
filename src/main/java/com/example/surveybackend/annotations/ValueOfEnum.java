package com.example.surveybackend.annotations;

import com.example.surveybackend.validators.UniqueEmailValidator;
import com.example.surveybackend.validators.ValueOfEnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.annotation.*;

@Constraint(validatedBy = ValueOfEnumValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValueOfEnum {
    Class<? extends Enum<?>> enumClass();
    String message() default "{surveybackend.constraints.enum.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

