package com.klutzybubbles.assignment1.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.klutzybubbles.assignment1.interfaces.OnNavigationClickListener;

import java.util.ArrayList;

import petrov.kristiyan.colorpicker.ColorPicker;

public class GameSettingsHolder extends android.support.v4.app.Fragment {

    private View view;

    private ColorPicker border, colorA, colorB, highlight, blank;

    @Override
    public View onCreateView(LayoutInflater i, ViewGroup g, Bundle b) {
        if (this.view == null)
            this.view = i.inflate(R.layout.activity_settings_screen, g, false);
        return this.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getView() == null || this.getActivity() == null)
            return;
        OnNavigationClickListener l;
        try {
            l = (OnNavigationClickListener) this.getActivity();
        } catch (ClassCastException e) {
            return;
        }
        final ArrayList<String> colors = new ArrayList<>();
        colors.add("#e6194b");
        colors.add("#3cb44b");
        colors.add("#ffe119");
        colors.add("#0082c8");
        colors.add("#f58231");
        colors.add("#911eb4");
        colors.add("#46f0f0");
        colors.add("#f032e6");
        colors.add("#d2f53c");
        colors.add("#fabebe");
        colors.add("#008080");
        colors.add("#e6beff");
        colors.add("#aa6e28");
        colors.add("#fffac8");
        colors.add("#800000");
        colors.add("#aaffc3");
        colors.add("#808000");
        colors.add("#ffd8b1");
        colors.add("#000080");
        colors.add("#808080");
        colors.add("#FFFFFF");
        colors.add("#000000");

        final SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        s.edit().clear().commit();

        /*
        final ColorPicker c = new ColorPicker(this.getActivity());
        c.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_blank), "#FFFFFF")))
                .setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        s.edit().putString("blank", String.valueOf(color)).apply();
                        if (c.getmDialog() != null)
                            c.getmDialog().dismiss();
                        c.dismissDialog();
                    }

                    @Override
                    public void onCancel() {
                        if (c.getmDialog() != null)
                            c.getmDialog().dismiss();
                        c.dismissDialog();
                    }
                });
        this.blank = c;
        final ColorPicker co = new ColorPicker(this.getActivity());
        co.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_color_a), "#0082c8")))
                .setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        s.edit().putString("color_a", String.valueOf(color)).apply();
                        if (co.getmDialog() != null)
                            co.getmDialog().dismiss();
                        co.dismissDialog();
                    }

                    @Override
                    public void onCancel() {
                        if (co.getmDialog() != null)
                            co.getmDialog().dismiss();
                        co.dismissDialog();
                    }
                });
        this.colorA = co;
        final ColorPicker col = new ColorPicker(this.getActivity());
        col.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_color_b), "#3cb44b")))
                .setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        s.edit().putString("color_b", String.valueOf(color)).apply();
                        if (col.getmDialog() != null)
                            col.getmDialog().dismiss();
                        col.dismissDialog();
                    }

                    @Override
                    public void onCancel() {
                        if (col.getmDialog() != null)
                            col.getmDialog().dismiss();
                        col.dismissDialog();
                    }
                });
        this.colorB = col;
        final ColorPicker colo = new ColorPicker(this.getActivity());
        colo.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_highlight), "#e6194b")))
                .setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int color) {
                        s.edit().putString("highlight", String.valueOf(color)).apply();
                        if (colo.getmDialog() != null)
                            colo.getmDialog().dismiss();
                        colo.dismissDialog();
                    }

                    @Override
                    public void onCancel() {
                        if (colo.getmDialog() != null)
                            colo.getmDialog().dismiss();
                        colo.dismissDialog();
                    }
                });
        this.highlight = colo;
        /*
        final ColorPicker color = new ColorPicker(this.getActivity());
        color.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_border), "#808080")))
                .setColors(colors)
                .setColumns(5)
                .setRoundColorButton(true)
                .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                    @Override
                    public void onChooseColor(int position, int colo) {
                        s.edit().putString("border", String.valueOf(colo)).apply();
                        if (color.getmDialog() != null)
                            color.getmDialog().dismiss();
                        color.dismissDialog();
                    }

                    @Override
                    public void onCancel() {
                        if (color.getmDialog() != null)
                            color.getmDialog().dismiss();
                        color.dismissDialog();
                    }
                });
        this.border = color;
*/
        if (this.view != null) {
            Button b = view.findViewById(R.id.button_home);
            b.setOnClickListener(v -> l.onClick(SplashScreen.MAIN_MENU));
            b = view.findViewById(R.id.button_blank);
            b.setOnClickListener(v -> {
                final ColorPicker c = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_blank), getString(R.string.pref_default_blank)));
                c.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_blank), getString(R.string.pref_default_blank))))
                        .setColors(colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_blank), colors.get(position)).apply();
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
            });
            b = view.findViewById(R.id.button_color_a);
            b.setOnClickListener(v -> {
                final ColorPicker co = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_color_a), getString(R.string.pref_default_color_a)));
                co.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_color_a), getString(R.string.pref_default_color_a))))
                        .setColors(colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_color_a), colors.get(position)).apply();
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
            });
            b = view.findViewById(R.id.button_color_b);
            b.setOnClickListener(v -> {
                final ColorPicker col = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_color_b), getString(R.string.pref_default_color_b)));
                col.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_color_b), getString(R.string.pref_default_color_b))))
                        .setColors(colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_color_b), colors.get(position)).apply();
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
            });
            b = view.findViewById(R.id.button_highlight);
            b.setOnClickListener(v -> {
                final ColorPicker colo = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_highlight), getString(R.string.pref_default_highlight)));
                colo.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_highlight), getString(R.string.pref_default_highlight))))
                        .setColors(colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int color) {
                                s.edit().putString(getString(R.string.pref_key_highlight), colors.get(position)).apply();
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
            });
            b = view.findViewById(R.id.button_border);
            b.setOnClickListener(v -> {
                final ColorPicker color = new ColorPicker(this.getActivity());
                Log.i("GSH:onActivityC", s.getString(getString(R.string.pref_key_border), getString(R.string.pref_default_border)));
                color.setDefaultColorButton(Color.parseColor(s.getString(getString(R.string.pref_key_border), getString(R.string.pref_default_border))))
                        .setColors(colors)
                        .setColumns(5)
                        .setRoundColorButton(true)
                        .setOnChooseColorListener(new ColorPicker.OnChooseColorListener() {
                            @Override
                            public void onChooseColor(int position, int colo) {
                                s.edit().putString(getString(R.string.pref_key_border), colors.get(position)).apply();
                            }

                            @Override
                            public void onCancel() {}
                        }).show();
            });
        }
    }

}
