package com.giligency.zkweb.util.validator;

import org.hibernate.validator.HibernateValidator;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;


public class ValidatorUtil {
    public static final ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            //快速失败返回模式(只要有一个验证失败，则返回)
            .failFast(true)
            .buildValidatorFactory();
    public static final Validator validator = validatorFactory.getValidator();

    private ValidatorUtil() {

    }


}
