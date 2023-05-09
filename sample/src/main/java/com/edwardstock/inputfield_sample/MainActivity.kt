/*
 * Copyright (C) by Eduard Maximovich. 2022
 * @link <a href="https://github.com/edwardstock">Profile</a>
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.edwardstock.inputfield_sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edwardstock.inputfield.form.DecimalInputFilter
import com.edwardstock.inputfield.menu.InputMenuItem
import com.edwardstock.inputfield_sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            inputDecimal.input.keyListener = DecimalInputFilter(decimals = 4)

            valueMultilinePassword.setSuffixMenu(listOf(
                InputMenuItem(
                    itemId = 100,
                    title = "Toggle password visibility",
                    isEnabled = true,
                    iconRes = R.drawable.ic_visible,
                    onMenuItemClickListener = { menuRoot, item ->
                        valueMultilinePassword.togglePasswordVisibility()
                        menuRoot.notifyItemChanged(
                            item.copy(
                                iconRes = if (valueMultilinePassword.isPasswordVisible) R.drawable.ic_visible else R.drawable.ic_visible_off
                            )
                        )
                    }
                )
            ))

            inputCreditCard.setSuffixMenu(listOf(
                InputMenuItem(
                    itemId = 100,
                    title = "Toggle card visibility",
                    isEnabled = true,
                    iconRes = R.drawable.ic_visible,
                    onMenuItemClickListener = { menuRoot, item ->
                        inputCreditCard.togglePasswordVisibility()
                        menuRoot.notifyItemChanged(
                            item.copy(
                                iconRes = if (inputCreditCard.isPasswordVisible) R.drawable.ic_visible else R.drawable.ic_visible_off
                            )
                        )
                    }
                ),
                InputMenuItem(
                    itemId = R.id.menu_message,
                    showAsAction = MenuItem.SHOW_AS_ACTION_IF_ROOM, // ifroom hardcoded to 3 items
                    title = "Action with black tint ",
                    iconRes = R.drawable.ic_message_grey,
                    iconTintColorRes = android.R.color.black,
                    onMenuItemClickListener = { _, _ ->
                        Toast.makeText(this@MainActivity, "Action 1", Toast.LENGTH_SHORT).show()
                    }
                ),
                InputMenuItem(
                    itemId = 101,
                    title = "Disabled item",
                    isEnabled = false,
                    iconRes = R.drawable.ic_cancel_grey,
                    iconTintColorRes = R.color.aif_grey,
                    onMenuItemClickListener = { _, _ ->
                        Toast.makeText(this@MainActivity, "Disabled item", Toast.LENGTH_SHORT).show()
                    }
                ),
                InputMenuItem(
                    itemId = R.id.menu_clear,
                    showAsAction = MenuItem.SHOW_AS_ACTION_NEVER,
                    iconRes = R.drawable.ic_cancel_grey,
                    title = "Action 2 (never shown)",
                    onMenuItemClickListener = { _, _ ->
                        Toast.makeText(this@MainActivity, "Action 2", Toast.LENGTH_SHORT).show()
                    }
                )
            ))
        }

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}