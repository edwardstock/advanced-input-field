package com.edwardstock.inputfield.mocks;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import com.edwardstock.inputfield.InputField;
import com.edwardstock.inputfield.form.validators.BaseValidator;
import com.edwardstock.inputfield.form.validators.CustomValidator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import kotlin.jvm.functions.Function1;

/**
 * Advanced InputField. 2020
 *
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
public class MockInputField extends InputField {
    public MockInputField(@NotNull Context context) {
        super(context);
    }

    public MockInputField(@NotNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MockInputField(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MockInputField(@NotNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        CustomValidator v = new CustomValidator("a", new Function1<CharSequence, Boolean>() {
            @Override
            public Boolean invoke(CharSequence charSequence) {
                return null;
            }
        });
    }

    @Nullable
    @Override
    public Editable getText() {
        return null;
    }

    public static class RValidator {

        public void doSome() {
            BaseValidator.Inherit in = new BaseValidator.Inherit();
            in.setErrorMessage("aaa");
            in.setRequired(true);
        }

    }
}
