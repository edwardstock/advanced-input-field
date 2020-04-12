package com.edwardstock.inputfield.mocks;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;

import com.edwardstock.inputfield.InputField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    }

    @Nullable
    @Override
    public Editable getText() {
        return null;
    }
}
